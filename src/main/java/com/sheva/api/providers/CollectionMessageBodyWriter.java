package com.sheva.api.providers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.sheva.api.providers.xml.JaxbMarshallerProvider;
import com.sheva.api.providers.xml.XmlRootElementCollection;
import com.sheva.utils.ApplicationHelper;
import org.apache.commons.lang.StringUtils;
import org.dom4j.*;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.logging.Logger;

import static java.util.logging.Level.*;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_XML_TYPE;
import static com.sheva.api.providers.xml.JaxbMarshallerProvider.INSTANCE;

/**
 * Base class for message body writers operated with collection of entities.
 * Provides customization for collections using {@link com.sheva.api.providers.xml.XmlRootElementCollection} annotation.
 * Annotations {@link javax.ws.rs.ext.Provider} should be present on descendant classes.
 *
 * Created by Sheva on 10/3/2016.
 */
abstract class CollectionMessageBodyWriter<E> {

    protected final Logger logger = Logger.getLogger(getClass().getName());

    void writeTo(ArrayList<E> list, MediaType mediaType, OutputStream outputStream) throws WebApplicationException {
        try {
            if (APPLICATION_JSON_TYPE.isCompatible(mediaType)) {
                writeJson(list, outputStream);
            } else if (APPLICATION_XML_TYPE.isCompatible(mediaType)) {
                writeXml(list, outputStream);
            } else {
                logger.log(WARNING, String.format("Media type '%s' is not supported.", mediaType));
                throw new WebApplicationException(String.format("Unsupported media type '%s'.", mediaType));
            }
        } catch (IOException e) {
            logger.log(SEVERE, "Unexpected exception occurred during writing to response.", e);
            throw new WebApplicationException("Unexpected exception occurred during writing to response.", e);
        }
    }

    private void writeJson(ArrayList<E> list, OutputStream outputStream) throws IOException {
        final Gson gson = new GsonBuilder().create();
        final JsonArray root = new JsonArray();

        list.forEach(item -> {
            JsonElement jsonItem = gson.toJsonTree(item);
            appendCustomToJson(gson, item, jsonItem.getAsJsonObject());
            root.add(jsonItem);
        });

        String json = gson.toJson(root);
        logger.log(FINEST, "Constructing collection of objects : " + json);

        outputStream.write(json.getBytes());
    }

    private void writeXml(ArrayList<E> list, OutputStream outputStream) throws IOException {
        final Document document = DocumentHelper.createDocument();
        final Element root = document.addElement(buildRootElement());

        list.forEach(item -> {
            try (StringWriter stringWriter = new StringWriter()) {
                XMLStreamWriter xmlStream = XMLOutputFactory.newInstance().createXMLStreamWriter(stringWriter);

                INSTANCE.createMarshaller().marshal(item, xmlStream);

                Document personDoc = DocumentHelper.parseText(stringWriter.toString());
                Element rootElement = personDoc.getRootElement();
                appendCustomToXml(item, rootElement);

                root.add(rootElement.detach());

            } catch (JAXBException | IOException | XMLStreamException | DocumentException exception) {
                logger.log(SEVERE, String.format("Error serializing object '%s' to the output stream.", item), exception);
                throw new WebApplicationException("Error serializing to the output stream.", exception);
            }
        });

        outputStream.write(document.asXML().getBytes());
    }

    protected abstract Class getEntityClass();

    private QName buildRootElement() {
        XmlRootElementCollection xmlAnnotation = ApplicationHelper.getAnnotation(getEntityClass(), XmlRootElementCollection.class);
        String name = xmlAnnotation != null ? StringUtils.trim(xmlAnnotation.name()) : getEntityClass().getSimpleName() + "s";

        logger.log(FINE, String.format("XML node name set to '%s' for collection of %s.", name, getEntityClass().getSimpleName()));
        return new QName(name);
    }

    protected void appendCustomToXml(E entity, Element element) {}

    protected void appendCustomToJson(Gson gson, E entity, JsonElement element) {}
}
