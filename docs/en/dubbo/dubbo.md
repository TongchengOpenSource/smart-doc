## Dubbo document generation

Smart-doc supports the generation of Apache Dubbo service interfaces documentation from version 1.8.7. The following describes how to use the smart-doc tool to generate dubbo's rpc internal interface documentation.
### Introduce
Smart-doc developed the maven plug-in and gradle based on the principle of simple use, through plug-ins to reduce the integration difficulty of smart-doc and remove the dependency intrusiveness. You can select the relevant plug-in according to the dependency build management tool you use. The following uses the smart-doc-maven-plugin plug-in to integrate smart-doc to generate dubbo as an example. Of course, you have two options for integrating smart-doc to generate dubbo rpc interface documentation:

- Use smart-doc to scan Dubbo service api module
- Use smart-doc to scan Dubbo provider module

Let's look at the integration method.
#### Add plugin
Add smart-doc-maven-plugin to your dubbo service api or dubbo provider module. Of course you only need to select one method
```xml
<plugin>
    <groupId>com.github.shalousun</groupId>
    <artifactId>smart-doc-maven-plugin</artifactId>
    <version>[Latest version]</version>
    <configuration>
        <!--Specify the configuration file used to generate the document, and the configuration file is placed in your own project -->
        <configFile>./src/main/resources/smart-doc.json</configFile>
        <!--Specify the project name-->
        <projectName>Test</projectName>
        <!--smart-doc realizes automatic analysis of the dependency tree to load the source code of third-party dependencies. If some framework dependency libraries cannot be loaded and cause an error, please use excludes to exclude -->
        <excludes>
            <!--The format is: groupId:artifactId; reference is as follows-->
            <!- ​​Starting from version 1.0.7, you can also use regular matching to exclude, such as: poi.* -->
            <exclude>com.alibaba:fastjson</exclude>
        </excludes>
        <!--Since version 1.0.8, the plugin provides includes support-->
        <!--smart-doc can automatically analyze the dependency tree to load all dependent source code, in principle, it will affect the efficiency of document construction, so you can use includes to let the plug-in load the components you configure -->
        <includes>
            <!--The format is: groupId:artifactId; reference is as follows-->
            <include>com.alibaba:fastjson</include>
        </includes>
    </configuration>
    <executions>
        <execution>
            <!--If you don't need to start smart-doc when compiling, please comment out the phase -->
            <phase>compile</phase>
            <goals>
                <goal>html</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

#### Add configuration files required by smart-doc
Add the smart-doc.json configuration file to your dubbo service api or dubbo provider module resources.

```json
{
   "isStrict": false, //Whether to enable strict mode
   "allInOne": true, //Whether to merge the documents into one file, generally recommended as true
   "outPath": "D://md2", //Specify the output path of the document
   "projectName": "smart-doc",//Configure your own project name
   "rpcApiDependencies":[{ // The project's open dubbo api interface module is dependent, after configuration, it is output to the document to facilitate user integration
       "artifactId":"SpringBoot2-Dubbo-Api",
       "groupId":"com.demo",
       "version":"1.0.0"
   }],
   "rpcConsumerConfig":"src/main/resources/consumer-example.conf"//Add dubbo consumer integration configuration to the document to facilitate the integration party to quickly integrate
}
```
About smart-doc, if you need more detailed configuration for generating documents, please refer to other documents on the official project wiki.

**rpcConsumerConfig：**

If you want to make dubbo consumer integration faster, you can put the integration configuration example in `consumer-example.conf`, and Smart-doc will output the example directly to the document.

```
dubbo:
  registry:
    protocol: zookeeper
    address:  ${zookeeper.adrress}
    id: my-registry
  scan:
    base-packages: com.iflytek.demo.dubbo
  application:
    name: dubbo-consumer
```

### Apache Dubbo provider interface scan
As mentioned above, smart-doc supports scanning Apache Dubbo service api or dubbo provider separately. The scanning principle is mainly through the recognition of @dubbo annotation tags (idea can support adding custom annotation tags to remind you can refer to the smart-doc wiki document introduction) or dubbo's @service annotations.

#### Scan Apache Dubbo Service API
The dubbo service api is usually a very concise dubbo interface definition. If you need smart-doc to scan the dubbo interface, you need to add the @dubbo annotation tag. Examples are as follows:

```java
/**
 * User action
 *
 * @author yu 2019/4/22.
 * @author zhangsan 2019/4/22.
 * @version 1.0.0
 * @dubbo
 */
public interface UserService {

    /**
     * Query all users
     *
     * @return
     */
    List<User> listOfUser();

    /**
     * Query based on user id
     *
     * @param userId
     * @return
     */
    User getById(String userId);
}
```

#### Scan Dubbo provider
If you want to generate rpc interface documentation through dubbo provider, you don't need to add any other annotation tags, smart-doc automatically scans @service annotations to complete.

```java
/**
 * @author yu 2019/4/22.
 */
@Service
public class UserServiceImpl implements UserService {

    private static Map<String,User> userMap = new HashMap<>();

    static {
        userMap.put("1",new User()
                .setUid(UUIDUtil.getUuid32())
                .setName("zhangsan")
                .setAddress("chengdu")
        );
    }
    
    /**
     * Get users
     * @param userId
     * @return
     */
    @Override
    public User getById(String userId) {
        return userMap.get(userId);
    }

    /**
     * Get users
     * @return
     */
    @Override
    public List<User> listOfUser() {
        return userMap.values().stream().collect(Collectors.toList());
    }
}
```

#### Generate operation
Run the plug-in's document generation command directly through the maven command or click the plug-in's visualization command directly in idea.
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200705230512435.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3NoYWxvdXN1bg==,size_16,color_FFFFFF,t_70)

Run rpc-html etc. to generate dubbo rpc document
