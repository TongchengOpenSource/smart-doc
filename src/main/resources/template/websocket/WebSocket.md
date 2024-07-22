<%if(deprecated){%>
# ~~${htmlEscape(desc)}~~
<%}else{%>
# ${htmlEscape(desc)}
<%}%>
**URL:** ${uri}

**Author:** ${author}

**Description:** ${desc}

**SubProtocols:** ${subProtocols}

<%if(isNotEmpty(pathParams)){%>

**Path-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
<%
for(param in pathParams){
%>
|${param.field}|${param.type}|${param.required}|${lineBreaksToBr(param.desc)}|${param.version}|
<%}%>
<%}%>