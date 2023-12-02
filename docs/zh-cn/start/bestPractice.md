# 最佳实践

smart-doc是一款根据接口的泛型定义来在编译器期加载分析项源代码的返回类型和请求参数类型来实现的工具。 如果你在代码的接口定义中返回如下几种类型我们都将无法做处理。

### 响应类(使用泛型定义)
大多数成熟团队的统一返回类似如下(因文档篇幅，省略很多注释，使用smart-doc在真实代码中请规范些注释)，当然可以根据自己项目来定制。
```java
public class CommonResult<T> implements Serializable {

    /**
         * 是否成功
         */
    private boolean success = false;
    private String message;
    private T data;
    private String code;
    private String timestamp;
}

```


### 接口请求
这种定义统一返回结构和明确的返回对象定义，smart-doc能够推导根据接口定义帮你推导出类的字段定义，包括嵌套的对象定义。
```java
/**
     * 添加用户信息
     * @param user
     * @return
     */
@PostMapping("/add")
public CommonResult<User> addUser(@RequestBody User user){
    return CommonResult.ok().setResult(user);
}

/**
     * 分页查询用户信息
     * @param user
     * @return
     */
@PostMapping("/page")
public CommonResult<Page<User>> addUser(@RequestBody UserQuery query){
    return CommonResult.ok().setResult(user);
}
```

### 错误示例
#### 1. 接口中使用`Map`
因为无法分析代码中Map的key值，所以smart-doc无法生成好的文档。

```java
@GetMapping(value = "/object")
public Map<String, User> testMapUser() {
    return null;
}
```

#### 2. 返回`JSONObject`

```java
/**
     * 返回用户信息
     * @return
     */
@GetMapping(value = "/user")
public JSONObject object() {
    return null;
}
```
团队中有这样定义返回数据一定要批评，鬼知道返回的是啥。程序员都看不懂，更别说`smart-doc`了。

#### 3. 返回`ModelMap`
 
```java
/**
     * 返回用户信息
     * @return
     */
@GetMapping(value = "/user")
public ModelMap object() {
    return null;
}

```
这个和`Map`是一个道理，并且`smart-doc`直接天生屏蔽`ModelMap`。