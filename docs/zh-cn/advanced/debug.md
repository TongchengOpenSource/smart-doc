# 接口UI集成

## `smart-doc`调试页面

从`smart-doc 2.0.0`版本开始，在`html`的`allInOne`的模式下。可以添加`"createDebugPage": true`的配置。`smart-doc`会
创建一个`debug.html`的页面。 在让生成`smart-doc`生成文档时直接放入到`static/doc/`下，
这样可以直接启动程序访问页面`localhost:8080/doc/debug.html`进行开发调试。
从`smart-doc 2.0.1`开始，对`html`文档，无论是`allInOne`还是非`allInOne`模式都能够生成`debug`页面。`smart-doc`目前的`debug`页面支持文件上传和文件下载测试。

### 配置

```json
{
  "serverUrl": "http://localhost:8080",
  "isStrict": false,
  "allInOne": true,
  "outPath": "src/main/resources/static/doc",
  "coverOld": true,
  "style":"xt256",//喜欢json高亮的可以设置
  "createDebugPage": true, //启用生成debug
  "md5EncryptedHtmlName": false,
  "projectName": "SpringBoot2-Open-Api"
}
```
### 跨域配置
有的开发人员直接在`idea`中使用【Open In Browser】打开`smart-doc`生成的`debug`页面，
如果非要这做，前端`js`请求后台接口时就出现了跨域。因此你需要在后端配置跨域。
这里以`SpringBoot`为例：

```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    /**
     * 跨域配置会覆盖默认的配置，
     * 因此需要实现addResourceHandlers方法，增加默认配置静态路径
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/resources/")
                .addResourceLocations("classpath:/static/");
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowCredentials(true);
    }
}
```
> 如果采用服务器方式来访问页面，则无需配置。

### 界面效果
![mock](https://raw.githubusercontent.com/chenqi146/smart-doc.github.io/book/_images/mock.png "1.png")

### debug页面调试
在使用`smart-doc`生成的`html`调试页面做接口调试时，你可能会碰到一下问题，
通常当点击`Send Request`按钮后，按钮变成了红色就说明接口出错或者是`debug`页面出现错误。
这时请打开浏览器的调试控制台查看问题或者是调试。`smart-doc`创建的页面中只是用了`jquery`和原生`js`来开发的，
`debug.js`是用于处理接口测试请求的，`search.js`是用于处理文档目录标题搜索的。源码都未做压缩，可以直接调试页面的`js`源码。
调试操作参考如下图：

![mock](https://raw.githubusercontent.com/chenqi146/smart-doc.github.io/book/_images/debug-console.png "1.png")


> Swagger是通过侵入一个web接口模块到项目中实现了调试接口调试功能，由于smart-doc不侵入代码中，打包的代码中看不到任何smart-doc的
依赖，因此如果你想调试接口只能生成html文档到src/resources/static目录中，这样SpringBoot就能自动渲染范围这个html文档页面。
当然smart-doc的调试页面对于文件上传你只能传一个文件，这点相比Swagger UI要弱。但是smart-doc也有比Swagger UI强的地方，
例如：文档展示更清晰明了；支持测试文件下载

## Postman导入调试
从`smart-doc 1.7.8`版本开始，`smart-doc`支持生成`Postman`的`json`文件，
你可以使用`smart-doc`生成整个项目的或者某个微服务所有接口的`Postman`的`json`文件，
然后通过将这个`json`文件导入`Postman`的`Collections`做测试。导出`json`.


导入`json`到`Postma`n效果如下图：
![输入图片说明](https://raw.githubusercontent.com/chenqi146/smart-doc.github.io/book/_images/095300_24a7f126_144669.png "postman.png")

### postman中设置环境变量

![输入图片说明](https://raw.githubusercontent.com/chenqi146/smart-doc.github.io/book/_images/141540_aed7de0b_144669.png "postman_set_env.png")
**注意：** 在`Add Environment`中不要忘记给环境设置名称(例如：本地开发测试)，否则按上图不能保存成功。

> smart-doc自动生成的Json文件会贴心的给在Postman中给填充上注释，如果你自己写了mock值也会携带进入，
远比自己手动填省心多了

## `swagger UI`集成

`smart-doc`支持生成`openapi 3.0+`规范的接口文档，因此你可以使用支持`openapi 3.0+`规范的文档管理系统或者`ui`界面来
展示`smart-doc`生成的文档。 本文来说下如何快速集成`swagger ui`来在开发中测试你的接口。

### 添加依赖

```
<!--swagger ui -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-ui</artifactId>
    <version>1.5.0</version>
</dependency>
```
`smart-doc`支持的`openapi`规范版本比较高，因此需要集成`1.5.0`或者是更高的版本。
### 配置swagger ui
在`application`配置文件中添加如下配置
```
# custom path for swagger-ui
springdoc:
  swagger-ui:
    path: /swagger-ui-custom.html
    operations-sorter: method
   #custom path for api docs
    url: /doc/openapi.json
```
- `url`是配置的关键，代表指向`smart-doc`生成的`openapi.json`文件。并且你需要将`openapi`生成到`src/main/resources/static/doc`下。


生成好`api`文件后启动你的应用访问`localhost:8080/swagger-ui-custom.html`即可看到文档。
接下来你就可以在开发的时候使用这个ui界面来自测了。

**提醒：** 关于`swagger ui`的其他配置就自行研究吧，我们也不会。
