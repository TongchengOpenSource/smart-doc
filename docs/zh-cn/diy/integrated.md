# 接口UI集成

## `swagger UI`集成

smart-doc支持生成openapi 3.0+规范的接口文档，因此你可以使用支持openapi 3.0+规范的文档管理系统或者ui界面来展示smart-doc生成的文档。
本文来说下如何快速集成swagger ui来在开发中测试你的接口。

### 添加依赖

```
<!--swagger ui -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-ui</artifactId>
    <version>1.5.0</version>
</dependency>
```
smart-doc支持的openapi规范版本比较高，因此需要集成1.5.0或者是更高的版本。
### 配置swagger ui
在application配置文件中添加如下配置
```
# custom path for swagger-ui
springdoc:
  swagger-ui:
    path: /swagger-ui-custom.html
    operations-sorter: method
   #custom path for api docs
    url: /doc/openapi.json
```
- url是配置的关键，代表指向smart-doc生成的openapi.json文件。并且你需要将openapi生成到`src/main/resources/static/doc`下。

生成好api文件后启动你的应用访问`http://localhost:8080/swagger-ui-custom.html`即可看到文档。
接下来你就可以在开发的时候使用这个ui界面来自测了。

 **提醒：** 关于swagger ui的其他配置就自行研究吧，我们也不会。


## `smart-doc`调试页面

从smart-doc 2.0.0版本开始，在html的allInOne的模式下。可以添加`"createDebugPage": true`的配置。smart-doc会创建一个debug.html的页面。
在让生成smart-doc生成文档时直接放入到`static/doc/`下，这样可以直接启动程序访问页面`localhost:8080/doc/debug.html`进行开发调试。
从smart-doc 2.0.1开始，对html文档，无论是allInOne还是非allInOne模式都能够生成debug页面。smart-doc目前的debug页面支持文件上传和文件下载测试。

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
有的开发人员直接在idea中使用【Open In Browser】打开smart-doc生成的debug页面，如果非要这做，前端js请求后台接口时就出现了跨域。因此你需要在后端配置跨域。
这里以SpringBoot为例：

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
>如果采用服务器方式来访问页面，则无需配置。

### 界面效果

![](https://gitee.com/smart-doc-team/smart-doc/raw/master/screen/mock.png "1.png")
### debug页面调试
在使用smart-doc生成的html调试页面做接口调试时，你可能会碰到一下问题，通常当点击`Send Request`按钮后，按钮变成了红色就说明接口出错或者是debug页面出现错误。这时请打开浏览器的调试控制台查看问题或者是调试。smart-doc创建的页面中只是用了jquery和原生js来开发的，`debug.js`是用于处理接口测试请求的，`search.js`是用于处理文档目录标题搜索的。源码都未做压缩，可以直接调试页面的js源码。调试操作参考如下图：

![](https://gitee.com/smart-doc-team/smart-doc/raw/master/screen/debug-console.png "2.png")





