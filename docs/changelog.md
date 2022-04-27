# Changelog
## Version: 2.4.2
- Update date: 2022-04-04
- update content:
 1. Modify the problem of incomplete automatic resolution of static constants in 2.4.1. 
 2. Add header automatic constant resolution. 
 3. Modify the https://github.com/smart-doc-group/smart-doc/issues/215 of generic inheritance resolution in some special cases. 
 4. Optimizes the handling of special greater-than and less-than symbols in documents.

## Version: 2.4.1
- Update date: 2022-03-26
- update content:
  1. Custom ResponseFields New replaceName configuration supports replacement of field names. 
  2. Fix the error bug of getting non-@ param annotation tag value. 
  3. Supports automatic parsing of static constants without using apiConstants for configuration. 
  4. Fix dubbo RPC

## Version: 2.4.0
- Update date: 2022-03-06
- update content:
 1. Support the resolution of field multi-generic nested private B < K, V > body

## Version: 2.3.9
- Update date: 2022-02-26
- update content:
 1. Supports pushing deprecated interface tags to torna. 
 2. The RPC mock is perfect. 
 3. The dubbo interface documentation supports exporting dictionaries. 4. Support for Solon, https://gitee.com/noear/solon.

## Version: 2.3.8
- Update date: 2022-02-19
- update content:
 1. Multi-level inheritance resolution is supported. 
 2. Optimize the generation of postman post request documents.

## Version: 2.3.7
- Update date: 2022-01-17
- update content:
 1. JAX-RS is supported.
 2. Optimize the loading of project files. 
 3. Solve the bug that the file data is not obtained is a data error.

## Version: 2.3.6
- Update date: 2022-01-02
- update content:
 1. The serverUrl no longer forces the protocol to be set when exporting postman. 
 2. Error in pushing torna data after modifying responseBody Advice.   
 3. Fixes an issue where the gradle plugin cannot add include and exclude to KTs scripts.

## Version: 2.3.5
- Update date: 2021-12-18
- update content:
 1. Modify the extra double quotes for the author name. 
 2. Fixed an issue where interface constants were treated as fields.  
 3. Enhance the plug-in's filtering of the old version's dependent jar to prevent the new version's qdox parsing error.

## Version: 2.3.4
- Update date: 2021-12-05
- update content:
 1. Fix the bug that @NotEmpty is placed at the front of the field, and other annotations behind it will not be analyzed. 
 2. The plugin enhances the projectName setting. 
 3. Enhance the filtering of the plug-in to the old version dependency jar to prevent the new version qdox from resolving to some old dependencies with errors.

## Version: 2.3.3
- Update date: 2021-11-29
- update content:
 1. Offset DateTime is supported. 
 2. Fix the bug that the directory is not displayed completely after the HTML document search is cleared. 
 3. Fix the problem of HTML document directory sequence disorder when non-grouping is used. 
 4. Fix English request header escaping problem.

## Version: 2.3.2
- Update date: 2021-11-21
- update content:
  1. Fix the directory anchor error after searching on the debug page.
  2. Fix the type conversion error when inputting basic type arrays such as List<String> when generating openapi.
  3. Optimize the display of generic types. When the actual parameter type of the generic type is the basic type, it is directly displayed as the basic type.
  4. Added ignoring of @RequestAttribute parameter.
  5. Fix the request parameter example error when @RequstBody uses basic types

## Version: 2.3.1
- Update date: 2021-11-13
- update content:
  1. Fix the bug that the Chinese value of the request header is not supported for debugging.
  2. Fix the problem that the response custom tag setting returns unformatted line break.
  3. Fix the problem that the designated mock of the enumeration type field does not take effect.
  4. Added ignoring the @SessionAttribute parameter.
  5. Support the controller to implement the interface and use the default method.

## Version: 2.3.0
- Update date: 2021-11-07
- update content:
  1. Fix the problem of missing search.js files when creating html documents.
  2. Fix the problem that only the first one takes effect when multiple packages are configured in packageFilters.
  3. Fix the curl command error on the debug page.
  4. Fix the bug that the data does not take effect after setting the download file interface and the download mark.
  5. Optimize the random value generation of some common fields.

## Version: 2.2.9
- Update date: 2021-10-31
- update content:
  1. Fix the problem of uploading errors when generating openapi files.
  2. Fix the problem that the torna parameter pushed by the file upload interface is placed in the query parameter list.
  3. Modify the problem that the List<T> type parameter is pushed to Torna incorrectly
  4. Optimize the outPath configuration. When only using torna push, it is no longer required to configure the outPath item.

## Version: 2.2.8
- Update date: 2021-10-07
- update content:
  1. Fix the problem that the anchor point jumps incorrectly when there is no interface comment in the html document.
  2. Fix the problem that the service port is configured as a variable when exporting postman.

## Version: 2.2.7
- Update date: 2021-09-12
- update content:
  1. Fix the css connection error of dubbo document.
  2. Fix the grouping error after grouping.
  3. Fix the bug that the path constant similarity is replaced incorrectly.
  4. JSR303 group optimization, the group field marked with Null will no longer be displayed in the document.

## Version: 2.2.6
- Update date: 2021-09-05
- update content:
  1. Fix the link error of html document static resource.
  2. The group is not displayed when the group is not configured.
  3. Fix the search error of the catalog item after grouping.
  4. Optimize the maven plugin prompt.

## Version: 2.2.5
- Update date: 2021-08-28
- Update content:
  1. Support not displaying parameter list in html document.
  2. All resources used in html documents are changed to local references.
  3. Fix the bug that the is prefix is ​​removed when the Boolean type field is named as the is prefix.
  4. After the dictionary code list is configured, the enumeration field comment display error [git #139](https://github.com/smart-doc-group/smart-doc/issues/139) is fixed.
  5. Fix the array out-of-bounds exception during the analysis process.
  6. Added interface grouping support.


## Version: 2.2.4
- Update date: 2021-08-08
- Update content:
  1. Fix dictionary code push torna error [gitee #I43JQR](https://gitee.com/smart-doc-team/smart-doc/issues/I43JQR).
  2. Added support for jsr303 @size and @length.
  3. Modify the html template style error.
  4. Fix postman error #I41G2E.
  5. Added isReplace configuration.
  6. Fix the issue that some annotations become invalid when there are multiple jsr annotations.


## Version: 2.2.3
- Update date: 2021-07-18
- Update content:
  1. The pathPrefix configuration item is added to configure the context. After the configuration item is introduced, the serverUrl is only used to configure the server address.
  2. Support request header constant setting analysis.
  3. Support the use of JsonIgnoreProperties and JSONType annotations to ignore multiple fields.
  4. Modify the issue of invalid setting allInOneDocFileName in some documents, [git #131](https://github.com/smart-doc-group/smart-doc/issues/131).
  5. Fix dubbo rpc document template format error [gitee #I40ZGE](https://gitee.com/smart-doc-team/smart-doc/issues/I40ZGE).
  6. Support setting global request parameters in the configuration add interceptor, [git #132](https://github.com/smart-doc-group/smart-doc/issues/132).
  7. Fix the problem that some types of mocks are not pushed to Torna.
  

## Version: 2.2.2

- Update date: 2021-07-04
- Update content:
  1. Fix the problem of regular expression parsing errors encountered in the URL.
  2. Fix the bug that the generated json sample part is formatted incorrectly, gitee #I3XSE5.
  3. Enhance the processing of html special characters in the document to prevent display errors after html rendering, gitee #I3XO31.
  4. The request header setting is enhanced to support the configuration of urlPatterns and excludePathPatterns to match the corresponding interface.
  5. Optimized the prompts of the maven plug-in. After optimization, the code paths of the modules loaded with those directories can be printed.
  6. Provides the ability of other frameworks to extend document parsing.
  7. Fix doc template error, gitee #I3Y640.
  8. Fix dictionary template error, #119.
  9. Add ignore HttpServlet object.
  10. Support built-in replacement of Jpa Pageable paging object, remove unnecessary request parameter replacement configuration.
  11. PackageFilters are enhanced, and regular matching can be used, gitee #I3YKZ4.
  12. Fix the data error of dubbo rpc file pushed to Torna.
  13. Fix the bug that fields with the same name of different classes are overwritten when customResponseFields and customRequestFields are set, gitee #I3Y6AL.
  14. Fixed the bug that the gradle plugin could not load the dependency and returned an empty json when using implements to add dependencies in a higher version of gradle.

## Version: 2.2.1
- Update date: 2021-06-20
- Update content:
   1. Remove System.out.print from the code.
  

## Version: 2.2.0
- Update date: 2021-06-20
- Update content:
   1. Fix the comment extraction error when the parameter is multi-line comment, gitee #I3TYYP.
   2. Fix the null pointer problem that may occur in some codes.
   3. Add @response tag. Support setting response example by yourself
   4. Fix the example display error when pushing to Torna request or returning as an array
   5. Character type analysis support.
   6. Fix the type parsing error of JobDataMap in Quartz.
   7. Remove YapiBuilder. smart-doc no longer supports other third-party interface systems, please use torna.

## Version: 2.1.9

- Update date: 2021-05-29
- Update content:
   1. Fix the problem that enumeration is displayed in the parameter when inlineEnum is false.
   2. Return to the Spring file download object to support automatic identification as a file download, reducing the manual tag @download tag.
   3. Replace the css cdn used by smart-doc, use domestic cdn by default, improve the domestic loading speed, and switch the English environment to use google's cdn.
   4. Add multi-level generic nested parsing support. gitee #I3T6UV.
   5. Fix the json sample error in the LocalDateTime type field in the parent class when the parent class is generic.
   6. Add to push the interface sort order to Torna.
   7. Fix the bug that the @ignore tag on the class does not take effect.
   8. Optimized dictionary code push, empty dictionary code will not initiate push request like Torna.


## Version: 2.1.8

- Update date: 2021-05-22
- Update content:
    1. Fix the problem of missing some mock values ​​from the push interface to Torna.
    2. Fix the bug of parsing non-class names into class names when configuring class substitution in parameter comments.
    3. Support subclasses annotated with @RestController on the parent class to be recognized and scanned
    4. Added to push the business error code and definition dictionary to Torna.
    5. Fix the problem that the two tasks of the maven plugin torna-rest and torna-rpc do not add the compilation prefix.
    6. Fix the problem that the array type json is wrong in the use case of generating json.
    7. Fix the bug that setting field value in customRequestFields does not take effect in the use case.
    8. Add @JsonProperty support, support JsonProperty.Access control field.

## Version: 2.1.7

- Update date: 2021-05-12
- Update content:
    1. Add the bug that push interface author information to Torna data error.
    2. Fix the empty parameter curl command is redundant? Issue number, github.

## Version: 2.1.6

- Update date: 2021-05-10
- Update content:
    1. Fix the bug that does not allow the wrong file upload object to be placed in the List.
    2. Add the author information of the push interface to torna, and set the push person by configuring the author. If not, the computer user name will be the default.
    3. Add push queryParams parameter to torna (requires torna 1.6.0+)

 ## Version: 2.1.5

- Update date: 2021-05-05
- Update content:
    1. Fix the missing requestBodyAdvice request sample.
    2. Add dubbo documents to torna's push.

## Version: 2.1.4

- Update date: 2021-04-24
- Update content:
    1. Fix the problem that the Mapping of the parent class is not inherited when the Controller is inherited.
    2. After the responseBodyAdvice is configured, the void method in the controller returns a display error.
    3. Fix the problem of missing pathParams when pushing to Torna.
    4. Fix the problem of mandatory checking error of binding enumeration in non-json request collection.
    5. Added requestBodyAdvice support, which can realize request parameter packaging.
    6. Fix the problem that the type is object when the generic type is List data.
    7. Fix the problem of invalid configuration when customFiled is an inherited parameter.

## Version: 2.1.3

- Update date: 2021-04-11
- Update content:
    1. Enhance the support for file upload.
    2. Add customRequestFields configuration item, #97.
    3. Fix the problem of missing pathParams when pushing to Torna.
    4. Modify debug test page to support post form request
    5. Modify the bug that the default value of the enumeration field in the form request object is wrong

## Version: 2.1.2

- Update date: 2021-03-29
- Update content:
    1. Fix the stack overflow problem of Map nesting in some structures, gitee #I3CCLY.
    2. Fix Torna data push issue.

## Version: 2.1.1

- Update date: 2021-03-24
- Update content:
    1. Fix the stack overflow problem of Map nesting in some structures, gitee #I3CCLY.
    2. Fix Torna data push issue.

## Version: 2.1.0

- Update date: 2021-03-21
- Update content:
    1. Add the missing protocol under the url resource of the exported postman.
    2. Add @ignoreParams custom tag to filter out parameters that you don't want to display in the document.
    3. Added the function of automatically generating version records.
    4. Modify the bug pushed by torna.
    5. Support the url suffix of the old SpringMVC project, and it is not recommended to add any suffixes to the new project.

    ## Version: 2.0.9

- Update date: 2021-03-12
- Update content:
    1. Support UUID and ZonedDateTime field types, #89.
    2. Add a switch to the map parameter to be compatible with the old project. It is still not recommended to use the map parameter.
    3. Complete the docking with Torna.

## Version: 2.0.8

- Update date: 2021-02-26
- Update content:
    1. Fix the comment that the parameters of the file upload are missing.
    2. Fix the missing comment bug when parsing the parent class field after ignoring the interface method in 2.0.7.
    3. Modify the conversion of byte type to convert the past string to int8.

## Version: 2.0.7

- Update date: 2021-01-30
- Update content:
    1. Fix the problem of the context-path not attached to the postman URL.
    2. Fix the problem of intercepting out of bounds in path parameter parsing with regularization.
    3. Add capability analysis that ignores the overriding of the get method in the default interface implementation.
    4. Modify the problem that the custom mock values ​​of field types such as arrays, maps, etc. display errors.
    5. Fix the processing of headers in mapping.

## Version: 2.0.6

- Update date: 2021-01-15
- Update content:
    1. Fix the use case problem of path parameter with regular path in postman.
    2. Enhance the analysis and compatibility of ancestral bad code.

## Version: 2.0.5

- Update date: 2021-01-09
- Update content:
    1. Fix the array out-of-bounds when the collection class has no generic parameters as input and output parameters.
    2. Fix the url splicing problem of newly opened tab access.

## Version: 2.0.3-2.0.4

- Update date: 2021-01-01
- Update content:
    1. Modify the title display of the error list on the page.
    2. Modify the curl header syntax error on the debug page.
    3. Modify the json parameter input box of the debug page to allow pasting small pieces of text.
    4. Solve the problem of using dubbo 2.7+ to generate documentation errors in the provider github #77.

    ## Version: 2.0.2

- Update date: 2020-12-27
- Update content:
    1. Modify the null pointer exception when creating openapi.
    2. The problem that the mock value is not used when modifying the debug page.
    3. The debug page can dynamically update the curl command according to the request.
    4. Optimize the file download test in the debug page.
    5. Optimize the bug that the enum enters the parameter mock error.
    6. The mock page supports using a new window to open the back-end rendered page.
    7. Modify the bug that generated some field values ​​incorrectly.
    8. Modify the bug where the generic type is not specified when using the collection field in the class.
    9. Optimize the type display of collections such as set in the document.
    10. Add processing of enumeration in the collection field.
    11. Enumeration serialization supports optimization.
    12. Added Highlight support to the debug page.

## Version: 2.0.1

- Update date: 2020-12-20
- Update content:
    1. The debug page supports file upload.
    2. Modify the mismatch between the mock value and type of the simple request parameter.
    3. The debug page fully supports file download testing.
    4. All html documents support interface directory search.
    5. Remove the flexmark dependency, delete the old non-allInOne template, and unify the h5 document style.

## Version: 2.0.0

- Update date: 2020-12-13
- Update content:
    1. Optimized the display of documents, and presented query and path separately for display
    2. Optimize the support of openapi 3.0 document specification, which can be integrated with ui such as swagger ui.
    3. Optimize the support of postman collection 2.0.
    4. Add group support group.
    5. Modify some bugs and enhance the use of mock
    6. Expenditure to create a debug page

## Version: 1.9.9.1

- Update date: 2020-11-23
- Update content:
    1. This is an urgently modified version.
    2. Solve the error when there is a non-path mapping method in the controller of version 1.9.9.

## Version: 1.9.9

- Update date: 2020-11-23
- Update content:
    1. Modify the bug in 1.9.8 to enable strict check comment mode.
    2. Modify the parsing error when using generic array parameters.
    3. Fix the array parsing error in ResponseEntity.
    4. Fix the document serial number error after the controller method is marked ignore.
    5. Added support for parsing the path attribute of the @RequestMapping annotation
    6. Fix the problem that the description information is not displayed in the formdata form in postman
    7. The html5 allInOne template supports code highlighting.

## Version: 1.9.8

- Update date: 2020-11-10
- Update content:
    1. Ignore the parsing of the Class object.
    2. Increase the analysis of the abstract Controller method.
    3. Modify the name resolution error in the dubbo annotation of Ali version.
    4. Modify the analog value to generate an error.
    5. Support ResponseBodyAdvice general interface response packaging settings.
    6. Fix the bug that the fields may be duplicated in the class inheritance and the base class and the implementation interface at the same time.

    ## Version: 1.9.7

- Update date: 2020-10-24
- Update content:
    1. Fix the parsing error when using? In restful interface generics.
    2. Optimize rpc html non-all in one problem.
    3. Automatically add descriptions to rest query parameters to increase readability.
    4. support ali dubbo,#I22CF7.
    5. support @RequestMapping headers.

## Version: 1.9.6

- Update date: 2020-10-09
- Update content:
    1. Fix RequestParam parsing error.
    2. Fix the parsing error when using? In generics.
    3. Modify the address of the service url to an empty string, and no longer provide the default http prefix
    4. Add the display switch control of the generic actual type.
    5. Fix the parsing error when the class inherits a generic class.
    6. Optimize the smart-doc maven plug-in to improve the user experience under multiple modules.

## Version: 1.9.5

- Update date: 2020-09-19
- Update content:
    1. Set required to false when the interface parameter has no annotation.
    2. Modify html adaptive.

## Version: 1.9.4

- Update date: 2020-09-06
- Update content:
    1. Add order tag to support api sorting.
    2. Optimize some duplicate codes.
    3. Modify the problem of spaces when using constants in the basic URL.
    4. Add the function of generating yapi files.

## Version: 1.9.3

- Update date: 2020-08-30
- Update content:
    1. Fix the problem that the parameter value of the Get request use case is removed from the space.
    2. Modify the error of the tree data conversion of the complex parameter table.
    3. Fix the rendering error when using non-allInOne templates.
    4. Fix the parsing error bug of some generic examples.
    5. Optimize the processing of MultipartFile file upload parameters and do not analyze the parameters.

    ## Version: 1.9.2

- Update date: 2020-08-23
- Update content:
    1. Modified the common jsr 303 verification and parsing error problem caused by the modification of the previous version.
    2. Added the configuration gitee #I1RBJO that ignores the request parameter object.
    3. Modify the beetl configuration of smart-doc to avoid conflicts with the beetl configuration of the user's business.
    4. Added interface #40 for obtaining tree format parameter data in ApiDataBuilder.
    5. Added support for Open Api 3.0.
    6. The problem of internal null pointers occurs when the dictionary table is empty.
    7. Optimize curl use cases and increase request headers.

## Version: 1.9.1

- Update date: 2020-08-02
- Update content:
    1. Modify the generic resolution problem caused by the version update.
    2. Modified the dubbo interface document display problem caused by the 1.8.9 version modification
    2. Modify the problem of lack of configuration file error when smart-doc-maven-plugin generates dubbo documentation.
    3. Modify the support for multiple modules of the gradle plugin.

## Version: 1.9.0

- Update date: 2020-07-19
- Update content:
    1. Modified the confusion problem of dubbo html dependency.
    2. Add the configuration of custom output file name.
    3. Add switch configuration items for request and response examples.
    4. When modifying the parameter verification using JSR303, the default group verification is ignored.
    5. Modify the problem that jackson JsonIgnore annotation does not take effect in the parameter object.

## Version: 1.8.9

- Update date: 2020-07-05
- Update content:
    1. Modify git #38.
    2. Modify gitee #I1LBKO.
    3. Modify fix #39 multi-generic parsing order issue.
    4. Optimize support for gitee #I1IQKY constant analysis requirements

## Version: 1.8.8

- Update date: 2020-06-21
- Update content:
    1. Modify to ignore the analysis of LinkedHashMap, gitee #I1JI5W.
    2. Modifying the interface or merging analysis with the implementation class is a field duplication problem, gitee #I1JHMW.
    3. Optimize the problem that the interface method field cannot get docletTag.
    4. Optimize the display of enumeration parameters and support custom control display.
    5. Add Feign support.
    6. Optimize the recursive execution, and provide a limit on the number of recursions externally.

    ## Version: 1.8.7

- Update date: 2020-06-01
- Update content:
    1. Add the analysis of java interface, such as the Page class of Jpa.
    2. Enhance the analysis of using @RequestBody to bind parameters.
    3. Add dubbo rpc document generation support.
    4. Added the conversion of camel case field format to underscore format.
    5. The maven plug-in and gradle plug-in provide includes support for easy configuration and loading of third-party libraries.
    6. fix #32.
    7. Add the function of sorting the document interface according to the interface title.

## Version: 1.8.6

- Update date: 2020-05-09
- Update content:
    1. Add localTime support [gitee #I1F7CW](https://gitee.com/sunyurepository/smart-doc/issues/I1F7CW).
    2. Optimize smart-doc to import Postman
       Header issue during collection [gitee #I1EX42](https://gitee.com/sunyurepository/smart-doc/issues/I1EX42)
    3. Optimize the filtering of the source loaded by smart-doc-maven-plugin, and support the use of wildcards to filter.
    4. Release the gradle plugin for the first time, release the smart-doc-gradle-plugin plugin,
    5. Fix general generic parsing error [git #28](https://github.com/smart-doc-group/smart-doc/issues/28).

## Version: 1.8.5

- Update date: 2020-04-19
- Update content:
    1. Maven plugin error code list export bug[git #I1EHXA](https://gitee.com/sunyurepository/smart-doc/issues/I1EHXA).
    2. Add @PatchMapping support [gitee #I1EDRF](https://gitee.com/sunyurepository/smart-doc/issues/I1EDRF)
    3. Solve the problem that javadoc contains duplicate tags to generate document error [gitee #I1ENNM](https://gitee.com/sunyurepository/smart-doc/issues/I1ENNM).
    4. Modify the problem of data parsing errors when the request parameters are generic.
    5. Fix the null pointer problem of group verification and do not perform group verification processing on returned objects.
    6. Optimize the loading of multi-level maven projects by smart-doc-maven-plugin.
    7. Support the request parameter object to be replaced with another object to render the document

## Version: 1.8.4

- Update date: 2020-03-30
- Update content:
    1. @ignore when Controller is added
       tag, which can be adapted to ignore the controller[git #24](https://github.com/smart-doc-group/smart-doc/issues/24) that does not need to generate documentation.
    2. Smart-doc card owner when HttpSession is included in the parameter, [gitee #I1CA9M](https://gitee.com/sunyurepository/smart-doc/issues/I1CA9M)
    3. Solve the problem of smart-doc reporting errors in some complex grouping scenarios [gitee #I1CPSM](https://gitee.com/sunyurepository/smart-doc/issues/I1CPSM).
    4. Solve the problem that the smart-doc-maven-plugin plug-in reads configuration garbled characters.

## Version: 1.8.3

- Update date: 2020-03-21
- Update content:
    1. Add comments read from the interface method getter or setter method.
    2. Modify the default encoding of smart-doc to utf-8 to solve the problem of garbled generated documents.
    3. Add support for @author tag in the code and support multiple authors.

## Version: 1.8.2

- Update date: 2020-03-13
- Update content:
    1. Modify gitee #I19IYW.
    2. Modify the title setting error in the document template.
    3. Modify gitee #I191EO
    4. Support @Validated grouping

    ## Version: 1.8.1

- Update date: 2020-01-22
- Update content:
    1. Add the analysis of the interface get method.
    2. Add the analysis of the list generic data in the third-party jar.
    3. Delete the original lengthy SourceBuilder code.
    4. Modify the standardization of the method names of AdocDocBuilder, HtmlApiDocBuilder, and ApiDocBuilder. The upgrade of unit tests requires minor changes.
    5. Modified the bug in the request example after 1.8.0 refactoring to put the header into the common parameter.
    6. After modifying the parameter and adding @Validated annotation, there is no bug of the parameter information in the document.
    7. Added support for @Deprecated annotation interface (use line through to complete the style mark)

## Version: 1.8.0

- Update date: 2020-01-01
- Update content:
    1. Modify the issues that are not supported by multiple verification annotations on the parameters.
    2. Modified the problem that the parameters of supporting uploaded files are not listed in the document.
    3. Added ApiDataBuilder for obtaining document data generated by smart-doc, including headers, dictionaries, error codes, etc.
    4. Merge the github book html5 template of the fork branch, and add search and anchor points.
    5. Newly added custom @mock tag is used to specify the field value of the generated document, and the parameter annotation of @param adds the function of mock value (@param name name|Zhang San)
    6. Key point: smart-doc-maven-plugin, smart-doc's maven plug-in, enhances the support for maven standard projects.
    7. Fully supports spring form parameter binding analysis.
    8. Postman json generation supports automatic backfilling of all parameters. No longer need to build parameters yourself.
    9. Optimize the support for enumerated fields in entity classes.
    10. Add filtering of static constant fields in entities.

## Version: 1.7.9

- Update date: 2019-12-16
- Update content:
    1. Modify the bug that the nested object cannot be parsed in the request parameter, refer to gitee #I16AN2.
    2. When the controller parameter is an array, adding @PathVariable annotation will report a null pointer, refer to gitee #I16F6G
    3. Added ApiDataBuilder for obtaining document data generated by smart-doc, including headers, dictionaries, error codes, etc.
    4. Modify the github #9 document error bug.
    5. The @author display of the new interface is added. The method finds the person in charge of the interface from the document, and can choose to close the display when generating the document.
    6. Focus: Smart-doc-maven-plugin 1.0.0 version of smart-doc's maven plug-in is released.

## Version: 1.7.8

- Update date: 2019-12-02
- Update content:
    1. Modify the bug that the response example generated when Spring Controller uses non-Spring Web annotations is wrong.
    2. Modify the problem of outputting the log field to the document when using the mybatis-plus entity to inherit the Model object.
    3. Add a switch for document output of transient modified fields, which is not output by default.
    4. Add project name display to html document
    5. Modify github #4 Void type analysis infinite loop in generics
    6. Modify github #5 Simple enumeration parameter parsing null pointer exception
    7. Add export PostMan json data

    ## Version: 1.7.7

- Update date: 2019-11-18
- Update content:
    1. Modify the timestamp type field to create a json example error bug.
    2. Fix #I1545A single interface multipath bug.
    3. Modify the space problem of some URL generation and deployment.
    4. Optimize the analysis of java.util.concurrent.ConcurrentMap.

## Version: 1.7.6

- Update date: 2019-11-13
- Update content:
    1. fix #I14PT5 header is rendered to the document repeatedly
    2. fix #I14MV7 Null pointer error occurs without setting dataDictionaries
    3. Add request parameter enumeration field analysis (trial function)

## Version: 1.7.5

- Update date: 2019-11-06
- Update content:
    1. Optimize the title of the error list in the document, which can display Chinese or because of changes in the language environment.
    2. Solve the bug that the internal class generated documentation error in the jar outside the project.
    3. Support ring dependency analysis. As long as you dare to write!
    4. Modify the content-type display error of the interface when uploading using SpringMvc or SpringBoot.
    5. Support setting items as the first-level title of markdown.
    6. The html link jump error caused by the same modification method comment.
    7. Add the coverage configuration item that generates AllInOne, and the default Version is automatically added without coverage.
    8. Added the function of exporting enumerated dictionary codes to documents.

## Version: 1.7.4

- Update date: 2019-10-29
- Update content:
    1. Modify bug #I1426C on gitee.
    2. Modify bug #I13ZAL on gitee, the bug generated after structure optimization from 1.7.0~1.7.3, users are recommended to upgrade.
    3. Modify bug #I13U4C on gitee.
    4. Modify the problem that the title of the error code list under the Chinese language environment (default Chinese) is displayed in English.
    5. Optimize the markdown display of AllInOne, with the automatically generated serial number when generating.

## Version: 1.7.3

- Update date: 2019-10-24
- Update content:
    1. Optimize the document catalog display on the left side of the html5 template, which can be expanded and contracted.
    2. Modify bug #I13R3K on gitee.
    3. Modify bug #I13NR1 on gitee.
    4. The open document data acquisition interface adds the unique id and method name of the return method, which is convenient for some companies to do their own docking.

    ## Version: 1.7.2

- Update date: 2019-10-19
- Update content:
    1. Optimize the problem of comment wrapping\n\r, relying on common-util 1.8.7.
    2. Modify bug #I135PG, #I13NR1 on gitee.
    3. Add support for the @requestHeader annotation, and the document will bind the parameters to the request header list by itself.
    4. Add support for javadoc apiNote tag.
    5. Solve the problem of scanning and analyzing the private method in the controller.
    6. Add document parsing that supports @RequestParam annotation to rewrite parameter names and set default values.
    7. Support the use of @PostMapping and @PutMapping to request custom annotations to generate json request instances in the scenario of receiving a single json parameter.
    8. Added analysis of Spring ResponseEntity.
    9. Add internal class to return structure analysis.
    10. Modify the field type displayed in the document, float, double, etc. directly from the original number to the specific type.

## Version: 1.7.1

- Update date: Obsolete
- Update content:
    1. Optimize the problem of comment wrapping\n\r.
    2. Modify bug #I135PG
    3. Add requestHeader function

## Version: 1.7.0

- Update date: 2019-09-30
- Update content:
    1. Optimize the code.
    2. Add the function of generating HTML5 and Asciidoctor documents.
    3. Increase the open API data interface function.
    4. Support the derivation of asynchronous interface return such as Callable, Future, CompletableFuture.
    5. Support Spring Boot Web Flux (written in Controller mode).

## Version: 1.6.4

- Update date: 2019-09-23
- Update content:
    1. Optimize the code
    2. Add the generation of common get request parameter assembly examples
    3. Add spring mvc placeholder restful url request sample generation

## Version: 1.6.2

- Update date: 2019-09-13
- Update content:
    1. Modify the field comment multi-line display error bug
    2. Added @Since tag support for field description documents
    3. The parsing code ignores the WebRequest class to prevent the production of too much information
    4. Upgrade the dependent version of the base library

## Version: 1.3

- Update date: 2018-09-15
- Update content:
    1. Add PutMapping and DeleteMapping support
    2. Add analog value generation of string date and Date type time

## Version: 1.2

- Update date: 2018-09-04
- Update content:
    1. Add controller registration filtering function based on user feedback, this function is optional

## Version: 1.1

- Update date: 2018-08-30
- Update content:
    1. Modify the bug that the PostMapping and GetMapping value are empty and report an error
    2. Enhance the creation of mock data for time fields
    3. Modify the bug that smart-doc parses self-referenced objects incorrectly

## Version: 1.0

- Update date: 2018-08-25
- Update content:
    1. smart-doc adds the function of exporting and archiving all documents to a markdown middleware
    2. Reference to the Ali development manual will be directly upgraded to 1.0, the previous version is mainly personal internal testing

    ## Version: 0.5

- Update date: 2018-08-23
- Update content:
     1. Rename api-doc to smart-doc and publish to the central warehouse

## Version: 0.4

- Update date: 2018-07-11
- Update content:
     1. Modify api-doc's support for class inheritance attributes.

## Version: 0.3

- Update date: 2018-07-10
- Update content:
     1. api-doc adds support for jackson and fastjson annotations, and can generate return information based on annotation definitions.

## Version: 0.2

- Update date: 2018-07-07
- Update content:
     1. Modify the bug of api-doc generic deduction.

## Version: 0.1

- Update date: 2018-06-25
- Update content:
     1. The manual publishes api-doc to the central warehouse