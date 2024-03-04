<%if(isNotEmpty(projectName)){%>
# ${projectName}
<%}%>
<%
for(doc in webSocketDocList){
%>
<%if(doc.deprecated){%>
### ~~${htmlEscape(doc.desc)}~~
<%}else{%>
### ${htmlEscape(doc.desc)}
<%}%>
**URL:** ${doc.url}

**Author:** ${doc.author}

**Description:** ${doc.desc}

**SubProtocols:** ${doc.subProtocols}

<%if(isNotEmpty(doc.pathParams)){%>

**Path-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
<%
for(param in doc.pathParams){
%>
|${param.field}|${param.type}|${param.required}|${lineBreaksToBr(param.desc)}|${param.version}|
<%}%>
<%}%>
<%}%>