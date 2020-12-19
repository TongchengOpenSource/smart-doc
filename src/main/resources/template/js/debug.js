$(function () {
    const Accordion = function (el, multiple) {
        this.el = el || {};
        this.multiple = multiple || false;
        const links = this.el.find('.dd');
        links.on('click', {el: this.el, multiple: this.multiple}, this.dropdown);
    };
    Accordion.prototype.dropdown = function (e) {
        const $el = e.data.el;
        const $this = $(this), $next = $this.next();
        $next.slideToggle();
        $this.parent().toggleClass('open');
        if (!e.data.multiple) {
            $el.find('.submenu').not($next).slideUp("20").parent().removeClass('open');
        }
    };
    new Accordion($('#accordion'), false);
});
$('textarea').each(function () {
    this.setAttribute('style', 'height:' + (this.scrollHeight) + 'px;overflow-y:hidden;');
}).on('input', function () {
    this.style.height = '80px';
    this.style.height = (this.scrollHeight) + 'px';
});
$("button").on("click", function () {
    const $this = $(this);
    const id = $this.data("id");
    console.log("method-id=>" + id);

    let body = $("#" + id + "-body").val();

    // header
    const $headerElement = $("#" + id + "-header");
    const headersData = getInputData($headerElement);

    // body param
    const $paramElement = $("#" + id + "-param");
    let bodyParamData = getInputData($paramElement)

    // path param
    const $pathElement = $("#" + id + "-path-params")
    const pathParamData = getInputData($pathElement)

    // query param
    const $queryElement = $("#" + id + "-query-params")
    const url = $("#" + id + "-url").data("url");
    const isDownload = $("#" + id + "-url").data("download");
    const method = $("#" + id + "-method").data("method");
    const contentType = $("#" + id + "-content-type").data("content-type");
    console.log("request-headers=>" + JSON.stringify(headersData))
    console.log("path-params=>" + JSON.stringify(pathParamData))

    console.log("body-params=>" + JSON.stringify(bodyParamData))
    console.log("json-body=>" + body);

    let queryParamData = "";
    if (isDownload) {
        queryParamData = getInputData($queryElement);
        download(url, headersData, pathParamData, queryParamData, bodyParamData, method, contentType);
        return;
    }
    const ajaxOptions = {};
    let finalUrl = "";
    if ("multipart/form-data" == contentType) {
        finalUrl = castToGetUri(url, pathParamData);
        queryParamData = getInputData($queryElement, true)
        body = queryParamData;
        ajaxOptions.processData = false;
        ajaxOptions.contentType = false;
    } else {
        queryParamData = getInputData($queryElement)
        finalUrl = castToGetUri(url, pathParamData, queryParamData)
        ajaxOptions.contentType = contentType;
    }
    console.log("query-params=>" + JSON.stringify(queryParamData));
    console.log("url=>" + finalUrl)
    ajaxOptions.headers = headersData
    ajaxOptions.url = finalUrl
    ajaxOptions.type = method
    ajaxOptions.data = body;

    const ajaxTime = new Date().getTime();
    $.ajax(ajaxOptions).done(function (result, textStatus, jqXHR) {
        $this.css("background", "#5cb85c");
        $("#" + id + "-response").find("pre").text(JSON.stringify(result, null, 4));
        const totalTime = new Date().getTime() - ajaxTime;
        $("#" + id + "-resp-status").html("&nbsp;Status:&nbsp;" + jqXHR.status + "&nbsp;&nbsp;" + jqXHR.statusText + "&nbsp;&nbsp;&nbsp;&nbsp;Time:&nbsp;" + totalTime + "&nbsp;ms")
    }).fail(function (jqXHR) {
        $this.css("background", "#D44B47");
        $("#" + id + "-response").find("pre").text(JSON.stringify(jqXHR.responseJSON, null, 4));
        const totalTime = new Date().getTime() - ajaxTime;
        $("#" + id + "-resp-status").html("&nbsp;Status:&nbsp;" + jqXHR.status + "&nbsp;&nbsp;" + jqXHR.statusText + "&nbsp;&nbsp;&nbsp;&nbsp;Time:&nbsp;" + totalTime + "&nbsp;ms")
    }).always(function () {

    });
})
$(".check-all").on("click", function () {
    const checkboxName = $(this).prop("name");
    const checked = $(this).is(':checked');
    if (!checked) {
        $(this).removeAttr("checked");
    } else {
        $(this).prop("checked", true);
    }
    $('input[name="' + checkboxName + '"]').each(function () {
        if (!checked) {
            $(this).removeAttr("checked");
        } else {
            $(this).prop("checked", true);
        }
    })
})

function castToGetUri(url, pathParams, params) {
    if (pathParams instanceof Object && !(pathParams instanceof Array)) {
        url = url.format(pathParams)
    }
    if (params instanceof Object && !(params instanceof Array)) {
        const pm = params || {};
        const arr = [];
        arr.push(url);
        let j = 0;
        for (const i in pm) {
            if (j === 0) {
                arr.push("?");
                arr.push(i + "=" + pm[i]);
            } else {
                arr.push("&" + i + "=" + pm[i]);
            }
            j++;
        }
        return arr.join("");
    } else {
        return url;
    }
}

function getInputData(element, returnFormDate) {
    const formData = new FormData();
    $(element).find("tr").each(function (i) {
        const checked = $(this).find('td:eq(0)').children(".checkbox").children("input").is(':checked');
        if (checked) {
            const input = $(this).find('td:eq(2) input');
            console.log("input type:" + $(input).attr("type"))
            const name = $(input).attr("name");
            if ($(input).attr("type") == "file") {
                formData.append(name, $(input)[0].files[0]);
            } else {
                const val = $(input).val();
                formData.append(name, val);
            }
        }
    });
    if (returnFormDate) {
        return formData;
    }
    const headersData = {};
    formData.forEach((value, key) => headersData[key] = value);
    return headersData;
}

String.prototype.format = function (args) {
    let reg;
    if (arguments.length > 0) {
        let result = this;
        if (arguments.length == 1 && typeof (args) == "object") {
            for (const key in args) {
                reg = new RegExp("({" + key + "})", "g");
                result = result.replace(reg, args[key]);
            }
        } else {
            for (let i = 0; i < arguments.length; i++) {
                if (arguments[i] == undefined) {
                    return "";
                } else {
                    reg = new RegExp("({[" + i + "]})", "g");
                    result = result.replace(reg, arguments[i]);
                }
            }
        }
        return result;
    } else {
        return this;
    }
}

function download(url, headersData, pathParamData, queryParamData, bodyParamData, method, contentType) {
    url = castToGetUri(url, pathParamData, queryParamData)
    const xmlRequest = new XMLHttpRequest();
    xmlRequest.open(method, url, true);
    xmlRequest.setRequestHeader("Content-type", contentType);
    for (let key in headersData) {
        xmlRequest.setRequestHeader(key, headersData[key])
    }
    xmlRequest.responseType = "blob";
    xmlRequest.onload = function () {
        if (this.status === 200) {
            let fileName = decodeURI(xmlRequest.getResponseHeader('filename'));
            console.log(fileName);
            const blob = this.response;
            if (navigator.msSaveBlob) {
                // IE10 can't do a[download], only Blobs:
                window.navigator.msSaveBlob(blob, fileName);
                return;
            }
            if (window.URL) { // simple fast and modern way using Blob and URL:
                const a = document.createElement("a");
                document.body.appendChild(a);
                const url = window.URL.createObjectURL(blob);
                a.href = url;
                a.download = fileName;
                a.click();
                window.URL.revokeObjectURL(url);
            }
            console.log(fileName);
        } else {
            console.log("download failed");
        }
    };
    try {
        xmlRequest.send(bodyParamData);
    } catch (e) {
        console.error("Failed to send data", e);
    }
}