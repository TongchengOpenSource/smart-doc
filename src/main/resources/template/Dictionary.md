<%if(isNotEmpty(dictList)){%>
# ${dictListTitle!"Data Dictionaries"}
<%
for(dict in dictList){
%>
## ${dict.title}

| Name | Code | Type | Description |
|------|------|------|-------------|
<%
for(dataDict in dict.dataDictList){
%>
|${dataDict.name}|${dataDict.value}|${dataDict.type}|${htmlEscape(dataDict.desc)}|
<%}%>
<%}%>
<%}%>