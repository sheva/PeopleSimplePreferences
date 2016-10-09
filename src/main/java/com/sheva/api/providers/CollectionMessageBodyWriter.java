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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base class for message body writers operated with collection of entities.
 * Provides customization for collections using {@link com.sheva.api.providers.xml.XmlRootElementCollection} annotation.
 * Annotations {@link javax.ws.rs.ext.Provider} should be present on descendant classes.
 *
 * Created by Sheva on 10/3/2016.
 */
abstract class CollectionMessageBodyWriter<E> {

    void writeTo(ArrayList<E> list, MediaType mediaType, OutputStream outputStream) throws WebApplicationException {
        try {
            if (MediaType.APPLICATION_JSON_TYPE.isCompatible(mediaType)) {
                writeJson(list, outputStream);
            }
            else if (MediaType.APPLICATION_XML_TYPE.isCompatible(mediaType)) {
                writeXml(list, outputStream);
            }
            else {
                getLogger().log(Level.WARNING, "Request with not supported media type was called: " + mediaType);
                throw new WebApplicationException("Unsupported media type " + mediaType.toString());
            }
        } catch (IOException e) {

            getLogger().log(Level.SEVERE, String.format("Unexpected exception occurred during writing to response of object %s.", list), e);
            throw new WebApplicationException("Unexpected exception occurred during writing to response.", e);
        }
    }

    void writeJson(ArrayList<E> list, OutputStream outputStream) throws IOException {
        Gson gson = new GsonBuilder().create();

        JsonArray root = new JsonArray();
        list.forEach(item -> {
            JsonElement jsonItem = gson.toJsonTree(item);
            appendCustomToJson(gson, item, jsonItem.getAsJsonObject());
            root.add(jsonItem);
        });

        String json = gson.toJson(root);
        getLogger().log(Level.FINEST, "Constructing collection of objects : " + json);

        outputStream.write(json.getBytes());
    }

    void writeXml(ArrayList<E> list, OutputStream outputStream) throws IOException {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(buildRootElement());

        list.forEach(item -> {
            try (StringWriter stringWriter = new StringWriter()) {
                XMLStreamWriter xmlStream = XMLOutputFactory.newInstance().createXMLStreamWriter(stringWriter);

                JaxbMarshallerProvider.getInstance().getMarshaller().marshal(item, xmlStream);

                Document personDoc = DocumentHelper.parseText(stringWriter.toString());
                Element rootElement = personDoc.getRootElement();
                appendCustomToXml(item, rootElement);

                root.add(rootElement.detach());

            } catch (JAXBException | IOException | XMLStreamException | DocumentException exception) {

                getLogger().log(Level.SEVERE, String.format("Error serializing object %s to the output stream.", item), exception);
                throw new WebApplicationException("Error serializing to the output stream.", exception);
            }
        });

        outputStream.write(document.asXML().getBytes());
    }

    protected abstract Class getEntityClass();

    protected abstract Logger getLogger();

    protected QName buildRootElement() {
        ApplicationHelper helper = new ApplicationHelper();

        XmlRootElementCollection xmlAnnotation = helper.getAnnotation(getEntityClass(), XmlRootElementCollection.class);
        String name = xmlAnnotation != null ? StringUtils.trim(xmlAnnotation.name()) : getEntityClass().getSimpleName() + "s";

        getLogger().log(Level.FINE, String.format("XML node name set to '%s' for collection of %s.", name, getEntityClass().getSimpleName()));
        return new QName(name);
    }

    protected void appendCustomToXml(E entity, Element element) {
    }

    protected void appendCustomToJson(Gson gson, E entity, JsonElement element) {
    }
}
