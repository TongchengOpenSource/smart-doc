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

<%if(isNotEmpty(doc.messageParams)){%>

**Message-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
<%
for(param in doc.messageParams){
%>
|${param.field}|${param.type}|${param.required}|${lineBreaksToBr(param.desc)}|${param.version}|
<%}%>
<%}%>


<%if(isNotEmpty(responseParams)){%>

**Response-parameters:**
<%
for(param in responseParams){
%>

| Parameter | Type | Description | Since |
|-----------|------|-------------|-------|
<%
for(paramItem in param){
%>
|${paramItem.field}|${paramItem.type}|${lineBreaksToBr(paramItem.desc)}|${paramItem.version}|
<%}%>
<%}%>
<%}%>