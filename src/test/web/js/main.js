var rootURL = "../../preferences/";
 $.ajaxSetup({
    success: renderResponse,
    beforeSend: retrieveRequest,
    error: renderError
});

function getType() {
    return $('input:radio[name=mediaType]:checked').val();
}

function findAll() {
    $.ajax({
        type: 'GET',
        url: rootURL + "people",
        dataType: getType()
    });
}

function deletePerson() {
    $.ajax({
        type: 'DELETE',
        url: rootURL + 'people/' + $('#id').val(),
        dataType: "json"
    });
}

function updatePerson() {
    $.ajax({
        type: 'PUT',
        url: rootURL + 'people/' + $('#id').val(),
        contentType: "application/" + getType(),
        dataType: getType(),
        data: prepareData()
    });
}

function createPerson() {
    $.ajax({
        type: 'POST',
        url: rootURL + 'people',
        contentType: "application/" + getType(),
        dataType: getType(),
        data: prepareData()
    });
}

function retrieveRequest(xhr, settings) {
    $('#requestURLText').text(xhr.url);
    $('#requestBodyText').text(settings.data || '');
    $('#responseBodyText').text('');
}

function renderResponse(response, status, xhr) {
    $('#responseStatusText').text(status);
    $('#requestContentTypeHeader').text(this.contentType);
    $('#contentTypeHeader').text(xhr.getResponseHeader('Content-Type'));
    $('#responseBodyText').text("");
    if (xhr.status === 201) {
        $('#responseBodyText').text(xhr.getResponseHeader('Location'));
    } else {
        var responseMsg;
        if (xhr.responseJSON) {
            responseMsg = JSON.stringify(response);
        } else {
            responseMsg = xhr.responseText;
        }
        $('#responseBodyText').text(responseMsg || '');
    }
}

function renderError(xhr, ajaxOptions, thrownError) {
    $('#responseStatusText').text(xhr.status);

    if (xhr.status === 201) {
        // jQuery bug: 201 response has empty body but jQuery is trying to parse it
        return;
    }

    var responseMsg;

    if (xhr.responseJSON) {
        responseMsg = JSON.stringify(xhr.responseText);
    } else {
        responseMsg = xhr.responseText ? xhr.responseText : thrownError;
    }
    $('#responseBodyText').text(responseMsg);
}

function findByName(firstName, lastName) {
    var query = "firstName=" + firstName + "&lastName=" + lastName;
    var url = rootURL + 'people/search?' + query;
    $.ajax({
        type: 'GET',
        url: url,
        dataType: getType()
    });
}

function findById(id) {
    $.ajax({
        type: 'GET',
        url: rootURL + 'people/' + id,
        success: renderResponse,
        dataType: getType()
    });
}

function decideGetBehavior() {
    var id = $('#id').val();
    var firstName = $('#firstName').val();
    var lastName = $('#lastName').val();
    var mediaType = getType();
    if (id) {
        return findById(id, mediaType);
    } else if (firstName || lastName) {
        return findByName($('#firstName').val(), $('#lastName').val(), mediaType);
    }  else {
        return findAll(mediaType);
    }
}

function httpButtonsEvent() {
    $('#get').click(function () {
        configureForGet();
    });
     $('#post').click(function () {
        setFieldsStatus(true, false, false, false, false, false, false);
        $('#send').off().on("click", createPerson);
        $('#fields input').each(function() { $(this).off() });
    });
     $('#put').click(function () {
        setFieldsStatus(false, false, false, false, false, false, false);
        $('#send').off().on("click", updatePerson);
        $('#fields input').each(function() { $(this).off() });
    });
     $('#delete').click(function () {
        setFieldsStatus(false, true, true, true, true, true, true);
        $('#send').off().on("click", deletePerson);
        $('#fields input').each(function() { $(this).off() });
    });
}

function configureForGet() {
    setFieldsStatus(false, false, false, true, true, true, true);
    $('#send').off().on("click", decideGetBehavior);
     $('#id').off().on('input',function() {
        if ($(this).val()) {
            $('#firstName').prop('disabled', true);
            $('#lastName').prop('disabled', true);
        } else {
            $('#firstName').prop('disabled', false);
            $('#lastName').prop('disabled', false);
        }
    });
     $('#firstName').off().on('input',function() {
        if ($(this).val()) {
            $('#id').prop('disabled', true);
         }
    });
     $('#lastName').off().on('input',function() {
        if ($(this).val()) {
            $('#id').prop('disabled', true);
         }
    });
}

function setFieldsStatus(idDis, firstNameDis, lastNameDis, dateOfBirthDis, favoriteColorDis, favoriteFoodDis) {
    $('#id').prop('disabled', idDis);
    $('#firstName').prop('disabled', firstNameDis);
    $('#lastName').prop('disabled', lastNameDis);
    $('#dateOfBirth').prop('disabled', dateOfBirthDis);
    $('#favoriteColor').prop('disabled', favoriteColorDis);
    $('#favoriteFood').prop('disabled', favoriteFoodDis);
}

function fieldsToJSON() {
    var id = $('#id').val();

    if ($('#id').prop('disabled') || !id) {
        id = null;
    }

    var colors = $('#favoriteColor').val();
    var parsedColors = [];

    if (colors) {
        colors.split(',').forEach(function(x) {
            parsedColors.push(x.trim());
        });
    }

    var food = $('#favoriteFood').val();
    var parsedFood = [];

    if (food) {
        food.split(',').forEach(function(x) {
            parsedFood.push({
                "name": x.trim()
            });
        });
    }

    return JSON.stringify({
        "id": id,
        "firstName": $('#firstName').val(),
        "lastName": $('#lastName').val(),
        "dateOfBirth": $('#dateOfBirth').val() || null,
        "favoriteColor": parsedColors,
        "favoriteFood": parsedFood
    });
}

function fieldsToXML() {
    var id = $('#id').val();

    if ($('#id').prop('disabled') || !id) {
        id = '';
    }

    var colors = $('#favoriteColor').val();
    var parsedColors = '';

    if (colors) {
        colors.split(',').forEach(function(x) {
            parsedColors += '<color>' + x.trim() + '</color>';
        });
    }

    var food = $('#favoriteFood').val();
    var parsedFood = '';

    if (food) {
        food.split(',').forEach(function(x) {
            parsedFood += '<food><name>' + x.trim() + '</name></food>';
        });
    }

    var result = '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>' +
        '<person>' +
            '<id>' + id + '</id>' +
            '<firstName>' + $('#firstName').val() + '</firstName>' +
            '<lastName>' + $('#lastName').val() + '</lastName>' +
            '<dateOfBirth>' + $('#dateOfBirth').val() + '</dateOfBirth>' +
            '<favoriteColor>' + parsedColors + '</favoriteColor>' +
            '<favoriteFood>' + parsedFood + '</favoriteFood>' +
        '</person>';
    return result;
}

function prepareData() {
    var mediaType = getType();
    if (mediaType === "json") {
        return fieldsToJSON();
    } else if (mediaType === "xml") {
        return fieldsToXML();
    }
}

$(function(){
    findAll();
    configureForGet();
    httpButtonsEvent();
});
