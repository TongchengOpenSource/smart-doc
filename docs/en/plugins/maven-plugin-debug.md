When using the `smart-doc-maven-plugin` to build and generate `API` documentation, you may encounter some issues.

If complex issues arise, only roughly summarizing error information in an `issue` report may not be sufficient for the official team to resolve the problem, as we cannot simulate the user's environment and their written code.

Therefore, we hope that users of `smart-doc-maven-plugin` can obtain more detailed information by using `debug` when encountering errors. When submitting an `issue`, providing a detailed problem description can also help us resolve issues more quickly.

The following will introduce how to debug the `smart-doc-maven-plugin` .

# Add the smart-doc dependency
Because the `smart-doc-maven-plugin` ultimately utilizes `smart-doc` to perform the project's source code analysis and document generation, in most cases, the actual code debugging occurs in `smart-doc`. However, this process is primarily tracked through the `smart-doc-maven-plugin`.

```xml
<dependency>
     <groupId>com.github.shalousun</groupId>
     <artifactId>smart-doc</artifactId>
     <version>[Latest version]</version>
     <scope>test</scope>
</dependency>
```
**Note:** It's advisable to ensure that the version of `smart-doc` you use matches the version of `smart-doc` that the plugin depends on.

# Add breakpoints
Add breakpoints as shown in the figure.

![输入图片说明](../../_images/232807_f88b94b2_144669.png "maven-debug1.png")

# Run the build target in Debug mode
Running the `maven` plugin in `debug` mode in `IDEA` is quite simple, as shown in the following figure.

![启动debug](../../_images/233101_c48191e6_144669.png "maven-debug2.png")

This way you can directly enter the breakpoints.

**Tips:** The above method is for debugging the source code of `smart-doc` through the plugin as an entry point. If you want to debug the execution process of the plugin itself, add the plugin's dependency to the project's dependencies as follows:

```xml
<dependency>
    <groupId>com.github.shalousun</groupId>
    <artifactId>smart-doc-maven-plugin</artifactId>
    <version>【Latest version】</version>
</dependency>
```
Then, follow similar steps to debug the source code of `smart-doc-maven-plugin`.