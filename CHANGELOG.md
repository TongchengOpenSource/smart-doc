## smart-doc版本
版本小于1.0都属于试用，正式1.0起始发布将会等到文中提到的问题解决后才发布。
#### 版本号：2.0.8
- 更新日期: 2020-02-26
- 更新内容：
    1. 修复文件上传的参数丢失的注释。
    2. 修复2.0.7新增忽略接口方法后解析父类字段缺失注释bug。
    3. 修改byte类型的转换，将过去的string转为int8。
#### 版本号：2.0.7
- 更新日期: 2020-01-30
- 更新内容：
    1. 修复postman的url中不附加的context-path的问题。
    2. 修复带正则的path路径参数解析出现截取越界的问题。
    3. 添加对默认接口实现中get方法重写忽略的能力解析。
    4. 修改数组、map等字段类型的自定义mock值显示错误问题。
    5. 修复对mapping中headers的处理。
#### 版本号：2.0.6
- 更新日期: 2020-01-15
- 更新内容：
    1. 修复带正则的path路径参数在postman中用例问题。
    2. 增强对祖传不良代码的分析兼容。
#### 版本号：2.0.5
- 更新日期: 2020-01-09
- 更新内容：
    1. 修复集合类无泛型参数作为入参出参时的数组越界。
    2. 修复新开tab访问的url拼接问题。
#### 版本号：2.0.3-2.0.4
- 更新日期: 2020-01-01
- 更新内容：
    1. 修改页面的错误列表标题显示。
    2. 修改debug页面curl header语法错误。
    3. 修改debug页面json参数输入框，允许粘贴小段文本。
    4. 解决使用dubbo 2.7+，在provider中生成文档出错问题 github #77.
#### 版本号：2.0.2
- 更新日期: 2020-02-27
- 更新内容：
    1. 修改创建openapi时的空指针异常。
    2. 修改debug页面时未使用mock值的问题。
    3. debug页面可以根据请求动态更新curl命令。
    4. 优化debug页面中的文件下载测试。
    5. 优化enum入参mock错误的bug。
    6. mock页面支持使用新窗口打开后端渲染的页面。
    7. 修改生成一些字段值生成错误的bug。
    8. 修改类中使用集合字段未指定泛型可能出错的bug。
    9. 优化set等集合类在文档中的类型显示。
    10. 添加对集合字段中枚举的处理。
    11. 枚举序列化支持优化。
    12. 调试页面新增Highlight支持。
#### 版本号：2.0.1
- 更新日期: 2020-12-20
- 更新内容：
    1. debug调试页面支持文件上传。
    2. 修改简单请求参数mock值和类型不匹配问题。
    3. debug页面完全支持文件下载测试。
    4. 所有html的文档支持接口目录搜索。
    5. 剔除flexmark依赖，旧的非allInOne模板删除，统一h5文档样式。
#### 版本号：2.0.0
- 更新日期: 2020-12-13
- 更新内容：
    1. 优化了文档的显示，将query和path单独提出来做了展示
    2. 优化openapi 3.0文档规范的支持，可集成swagger ui等ui使用。
    3. 优化postman collection 2.0的支持。
    4. 添加分组支持group。
    5. 修改mock的一些bug和增强使用
    6. 支出创建debug页面
#### 版本号：1.9.9.1
- 更新日期: 2020-11-23
- 更新内容：
    1. 这是一个紧急修改版本。
    2. 解决1.9.9版本controller中存在非路径映射方法时的错误。
#### 版本号：1.9.9
- 更新日期: 2020-11-23
- 更新内容：
    1. 修改1.9.8启用严格检查注释模式下的bug。
    2. 修改使用泛型数组参数时解析错误。
    3. 修复ResponseEntity中的数组解析错误。
    4. 修复controller方法标注ignore后文档序号错误。
    5. 增加对@RequestMapping注解的path属性的解析支持
    6. 修复postman中formdata表单不显示描述信息的问题
    7. html5 allInOne模板支持代码高亮。
#### 版本号：1.9.8
- 更新日期: 2020-11-10
- 更新内容：
    1. 忽略Class对象的解析。
    2. 增加对抽象Controller方法的解析。
    3. 修改阿里版本dubbo注解名称解析错误 。
    4. 修改模拟值生成错误。
    5. 支持ResponseBodyAdvice通用接口响应包装设置。
    6. 修复类同时继承和基类和实现接口中可能出现字段重复的bug。
#### 版本号：1.9.7
- 更新日期: 2020-10-24
- 更新内容：
    1. 修复restful接口泛型中使用?时的解析错误。
    2. 优化rpc html非all in one的问题。
    3. 对rest query参数自动添加描述，增加可读性。
    4. support ali dubbo,#I22CF7 .
    5. support @RequestMapping headers.
#### 版本号：1.9.6
- 更新日期: 2020-10-09
- 更新内容：
    1. 修复RequestParam 解析错误。
    2. 修复泛型中使用?时的解析错误。
    3. 修改服务url的地址为空字符串，不再提供默认http前缀
    4. 增加泛型实际类型的显示开关控制。
    5. 修复类继承一个泛型类时的解析错误。
    6. 优化smart-doc maven插件，提升用户在多模块下的使用体验。
#### 版本号：1.9.5
- 更新日期: 2020-09-19
- 更新内容：
    1. 接口参数无注解时将required设置为false。
    2. 修改html自适应。
#### 版本号：1.9.4
- 更新日期: 2020-09-06
- 更新内容：
    1. 添加order tag支持对api做排序。
    2. 优化一些重复的代码。
    3. 修改基础url中使用常量出现空格的问题。
    4. 添加生成yapi文件的功能。
#### 版本号：1.9.3
- 更新日期: 2020-08-30
- 更新内容：
    1. 修复Get请求用例参数值被去空格问题。
    2. 修改复杂参数表树型数据转化的错误。
    3. 修复非allInOne模板使用渲染错误。
    4. 修复一些泛型例子解析错误bug。
    5. 优化MultipartFile文件上传参数处理，不对该参数进行展开分析。
#### 版本号：1.9.2
- 更新日期: 2020-08-23
- 更新内容：
    1. 修改前面版本修改引发的普通jsr 303验证解析错误问题。
    2. 新增忽略请求参数对象的配置gitee #I1RBJO。
    3. 修改smart-doc的beetl配置避免和用户的业务中beetl配置冲突。
    4. 新增ApiDataBuilder中获取树形格式参数数据的接口#40。
    5. 新增对Open Api 3.0的支持。
    6. 修改字典表空时内部发生空指针的问题。
    7. 优化curl用例，增加请求头。
#### 版本号：1.9.1
- 更新日期: 2020-08-02
- 更新内容：
    1. 修改进去版本更新导致的泛型解析问题。
    2. 修改1.8.9版本修改后带来的dubbo接口文档显示问题
    2. 修改smart-doc-maven-plugin生成dubbo文档时缺乏配置文件错误问题。
    3. 修改gradle插件的对多模块的支持。
#### 版本号：1.9.0
- 更新日期: 2020-07-19
- 更新内容：
    1. 修改dubbo html依赖部分错乱问题。
    2. 新增自定义输出文件名称的配置。
    3. 添加请求和响应示例的开关配置项。
    4. 修改使用JSR303参数校验时，默认分组验证被忽略问题。
    5. 修改jackson JsonIgnore注解在参数对象中不生效的问题。
#### 版本号：1.8.9
- 更新日期: 2020-07-05
- 更新内容：
    1. 修改git #38。
    2. 修改gitee #I1LBKO。
    3. 修改fix #39多泛型解析顺序问题。
    4. 优化支持gitee #I1IQKY常量解析需求
#### 版本号：1.8.8
- 更新日期: 2020-06-21
- 更新内容：
    1. 修改忽略对LinkedHashMap的解析，gitee #I1JI5W。
    2. 修改接口或和实现类合并分析是字段重复问题，gitee #I1JHMW。
    3. 优化接口方法字段不能获取docletTag的问题。
    4. 优化枚举参数展示，支持自定义控制显示。
    5. 添加Feign的支持。
    6. 优化递归执行，对外提供递归次数限制。
#### 版本号：1.8.7
- 更新日期: 2020-06-01
- 更新内容：
    1. 增加对java接口的分析，例如Jpa的分页Page类。
    2. 增强对使用@RequestBody绑定参数方法的解析。
    3. 增加dubbo rpc文档生成支持。
    4. 增加将驼峰字段格式转化为下划线格式。
    5. maven插件和gradle插件提供includes支持，方便自行配置加载第三方库。
    6. fix #32.
    7. 增加文档接口根据接口标题排序功能。
#### 版本号：1.8.6
- 更新日期: 2020-05-09
- 更新内容：
	1. 增加localTime支持[gitee #I1F7CW](https://gitee.com/sunyurepository/smart-doc/issues/I1F7CW)。
	2. 优化smart-doc导入Postman collection时的header问题[gitee #I1EX42](https://gitee.com/sunyurepository/smart-doc/issues/I1EX42)
    3. 优化smart-doc-maven-plugin加载source的过滤，支持使用通配符来过滤。
    4. 首次发布gradle插件，发布smart-doc-gradle-plugin插件，
    5. 修复通用泛型解析出错[git #28](https://github.com/smart-doc-group/smart-doc/issues/28)。
#### 版本号：1.8.5
- 更新日期: 2020-04-19
- 更新内容：
	1. maven插件错误码列表导出bug[git #I1EHXA](https://gitee.com/sunyurepository/smart-doc/issues/I1EHXA)。
	2. 增加@PatchMapping支持[gitee #I1EDRF](https://gitee.com/sunyurepository/smart-doc/issues/I1EDRF)
	3. 解决javadoc包含重复tag生成文档报错[gitee #I1ENNM](https://gitee.com/sunyurepository/smart-doc/issues/I1ENNM)。
    4. 修改当请求参数为泛型时数据解析错误问题。
    5. 修复分组验证空指针问题，不对返回对象做分组验证处理。
    6. 优化smart-doc-maven-plugin对多级maven项目的加载。
    7. 支持请求参数对象替换成另外的对象来渲染文档
#### 版本号：1.8.4
- 更新日期: 2020-03-30
- 更新内容：
	1. Controller新增时候@ignore tag,可适应该tag忽略不需要生成文档的controller[git #24](https://github.com/smart-doc-group/smart-doc/issues/24)。
	2. 参数中包含 HttpSession时smart-doc卡主，[gitee #I1CA9M](https://gitee.com/sunyurepository/smart-doc/issues/I1CA9M)
	3. 解决一些复杂分组场景smart-doc报错的问题[gitee #I1CPSM](https://gitee.com/sunyurepository/smart-doc/issues/I1CPSM)。
    4. 解决smart-doc-maven-plugin插件读取配置乱码问题。
#### 版本号：1.8.3
- 更新日期: 2020-03-21
- 更新内容：
	1. 增加从接口方法getter或者setter方法中读取注释。
	2. 修改smart-doc默认编码为utf-8，解决生成文档乱码问题。
	3. 增加对代码中@author tag的支持，支持多作者。
#### 版本号：1.8.2
- 更新日期: 2020-03-13
- 更新内容：
	1. 修改gitee #I19IYW 。
	2. 修改文档模板中的title设置错误。
	3. 修改gitee #I191EO
	4. 支持@Validated 分组
#### 版本号：1.8.1
- 更新日期: 2020-01-22
- 更新内容：
	1. 增加对接口get方法的分析。
	2. 增加对第三方jar中list泛型数据的解析。
	3. 删除原来冗长的SourceBuilder代码。
	4. 修改AdocDocBuilder、HtmlApiDocBuilder、ApiDocBuilder的方法名规范化，单元测试的升级需要做小部分变更。
	5. 修改1.8.0重构后的请求示例将header放入普通参数的bug。
	6. 修改参数加上@Validated注解后，文档里没有该参数信息的bug。
	7. 新增@Deprecated标注接口的支持(使用line through完成样式标记)
#### 版本号：1.8.0
- 更新日期: 2020-01-01
- 更新内容：
	1. 修改参数上多个验证注解不支持的问题。
	2. 修改支持上传文件参数不列举到文档的问题。
	3. 新增ApiDataBuilder用于获取smart-doc生成的文档数据，包含header、字典、错误码等。
	4. 合并fork分支的github book html5模板，新增搜索和锚点。
	5. 新增自定义@mock tag用于指定生成文档的字段值，@param 的参数注释增加mock值的功能(@param name 姓名|张三)
	6. 重点：smart-doc的maven插件smart-doc-maven-plugin增强对maven标准项目的支持。
	7. 全面支持spring的表单参数绑定解析。
	8. postman json生成支持所有参数自动回填。再也不用自己建参数了。
	9. 优化对实体类中枚举字段的支持。
	10. 增加对实体中静态常量常量字段的过滤。
#### 版本号：1.7.9
- 更新日期: 2019-12-16
- 更新内容：
	1. 修改request请求参数中嵌套对象不能解析的bug，参考gitee #I16AN2.
	2. controller参数是数组时添加@PathVariable注解会报空指针,参考gitee #I16F6G
	3. 新增ApiDataBuilder用于获取smart-doc生成的文档数据，包含header、字典、错误码等。
	4. 修改github #9 文档错误bug.
	5. 新增接口的@author展示，方法从文档中查到找到接口负责人，生成文档可以选择关闭显示。
	6. 重点：smart-doc的maven插件smart-doc-maven-plugin 1.0.0版本发布。
#### 版本号：1.7.8
- 更新日期: 2019-12-02
- 更新内容：
	1. 修改Spring Controller使用非Spring Web注解时生成的响应示例出错的bug。
	2. 修改使用mybatis-plus实体继承Model对象时将log字段输出到文档的问题。
	3. 添加对transient修饰字段文档输出开关，默认不输出。
	4. html文档添加项目名称显示
	5. 修改github #4 泛型中Void类型解析死循环
	6. 修改github #5 简单枚举参数解析空指针异常
	7. 添加导出PostMan json数据
#### 版本号：1.7.7
- 更新日期：2019-11-18
- 更新内容：
	1. 修改timestamp类型字段创建json示例错误bug。
	2. fix #I1545A 单接口多路径bug。
	3. 修改部分url生成部署空格问题。
	4. 优化对java.util.concurrent.ConcurrentMap的解析。
#### 版本号：1.7.6
- 更新日期：2019-11-13
- 更新内容：
	1. fix #I14PT5 header重复渲染到文档
	2. fix #I14MV7 不设置dataDictionaries出现空指针错误
	3. 增加请求参数枚举字段解析(试用功能)
#### 版本号：1.7.5
- 更新日期：2019-11-06
- 更新内容：
	1. 优化文档中错误列表的标题，可根据语言环境变化显示中文或因为。
	2. 解决项目外jar中内部类生成文档错误的bug。
	3. 支持环形依赖分析。只要你敢写！
	4. 修改使用SpringMvc或者SpringBoot上传时接口的Content-Type显示错误。
	5. 支持设置项目作为markdown的一级标题。
	6. 修改方法注释相同引起的html链接跳转错误。
	7. 添加生成AllInOne的覆盖配置项，默认自动加版本号不覆盖。
	8. 新增枚举字典码导出到文档的功能。
#### 版本号：1.7.4
- 更新日期：2019-10-29
- 更新内容：
	1. 修改gitee上bug #I1426C。
	2. 修改gitee上bug #I13ZAL,1.7.0~1.7.3 结构优化后产生的bug，建议用户升级。
	3. 修改gitee上bug #I13U4C。
	4. 修改设置中文语言环境(默认中文)下错误码列表title显示英文的问题。
	5. 优化AllInOne的markdown展示，生成时带上自动产生的序号。
#### 版本号：1.7.3
- 更新日期：2019-10-24
- 更新内容：
	1. 优化html5模板左侧文档目录展示，能够展开和收缩。
	2. 修改gitee上bug #I13R3K。
	3. 修改gitee上bug #I13NR1。
	4. 开放的文档数据获取接口添加返回方法的唯一id和方法名称，方便一些企业自己做对接。
#### 版本号：1.7.2
- 更新日期：2019-10-19
- 更新内容：
	1. 优化注释换行\n\r问题，依赖common-util 1.8.7。
	2. 修改gitee上bug #I135PG、#I13NR1。
	3. 添加@requestHeader注解的支持，文档自定将参数绑定到请求头列表中。
	4. 增加javadoc apiNote tag的支持。
	5. 解决扫描分析controller中private方法的问题。
	6. 添加支持@RequestParam注解重写参数名和设置默认值的文档解析。
	7. 支持使用@PostMapping和@PutMapping请求自定义注解接收单个json参数场景下生成json请求实例。
	8. 新增对Spring ResponseEntity的解析。
	9. 增加内部类返回结构解析。
	10. 修改文档中显示的字段类型，float、double等由原来的number直接变成具体类型。
#### 版本号：1.7.1
- 更新日期：已废弃
- 更新内容：
	1. 优化注释换行\n\r问题。
	2. 修改bug #I135PG
	3. 添加requestHeader功能
#### 版本号：1.7.0
- 更新日期：2019-09-30
- 更新内容：
	1. 优化代码。
	2. 添加生成HTML5和Asciidoctor文档的功能。
	3. 增加开放API数据接口功能。
	4. 支持Callable,Future,CompletableFuture等异步接口返回的推导。
	5. 支持Spring Boot Web Flux(Controller方式书写)。
#### 版本号：1.6.4
- 更新日期：2019-09-23
- 更新内容：
	1. 优化代码
	2. 增加对普通的get请求参数拼装示例的生成
	3. 增加spring mvc占位符restful url请求示例生成
#### 版本号：1.6.2
- 更新日期：2019-09-13
- 更新内容：
	1. 修改字段注释多行显示错误bug
	2. 字段描述文档增加@Since tag的支持
	3. 解析代码忽略WebRequest类防止生产过多信息
	4. 升级基础库依赖版本
#### 版本号：1.3
- 更新日期：2018-09-15
- 更新内容：
	1. 增加PutMapping和DeleteMapping支持
	2. 添加字符串date和Date类型时间的模拟值生成
#### 版本号：1.2
- 更新日期：2018-09-04
- 更新内容：
	1. 根据用户反馈增加controller报名过滤功能，该功能为可选项
#### 版本号：1.1
- 更新日期：2018-08-30
- 更新内容：
	1. 修改PostMapping和GetMapping value为空报错的bug
	2. 增强时间字段的mock数据创建
	3. 修改smart-doc解析自引用对象出错的bug
#### 版本号：1.0
- 更新日期：2018-08-25
- 更新内容：
	1. smart-doc增加将所有文档导出归档到一个markdown中件的功能
	2. 参考阿里开发手册将直接提升到1.0，之前的版本主要是个人内部测试
	
#### 版本号：0.5
- 更新日期：2018-08-23
- 更新内容：
	1. 将api-doc重命名为smart-doc并发布到中央仓库	
#### 版本号：0.4
- 更新日期：2018-07-11
- 更新内容：
	1. 修改api-doc对类继承属性的支持。	
	
#### 版本号：0.3
- 更新日期：2018-07-10
- 更新内容：
	1. api-doc增加对jackson和fastjson注解的支持，可根据注解定义来生成返回信息。
### 版本号：0.2  
- 更新日期：2018-07-07
- 更新内容：
	1. 修改api-doc泛型推导的bug.

### 版本号：0.1 
- 更新日期：2018-06-25  
- 更新内容：
	1. 手册将api-doc发布到中央仓库