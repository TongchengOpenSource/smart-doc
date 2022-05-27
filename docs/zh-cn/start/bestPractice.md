# 最佳实践

`smart-doc`是一款根据接口的泛型定义来在编译器期加载分析项源代码的返回类型和请求参数类型来实现的工具。
如果你在代码的接口定义中返回如下几种类型我们都将无法做处理。


# 不规范的返回定义
## 1.1 接口中使用`Map`
因为无法分析代码中`Map`的`key`值，所以`smart-doc`无法生成好的文档。
```java
@GetMapping(value = "/object")
public Map<String, User> testMapUser() {
    return null;
}
```
这种生成文档中`key`没法明确。
## 1.2 返回`JSONObject`

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

## 1.3 返回`ModelMap`
 
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

## 1.4 不规范说明
上面只列举了一些常见的，可能还有很多，如果项目中使用了类似例子，也不愿意调整代码那就回归`Swagger`老老实实写注解。

# 正确的示范

## 2.1 设计通用返回
大多数成熟团队的统一返回类似如下(因文档篇幅，省略很多注释，使用`smart-doc`在真实代码中请规范些注释)，当然可以根据自己项目来定制。
```java
public abstract class BaseResult<T> implements Serializable {

    private boolean success = false;

    private String message;

    private T data;

    private String code;

    private String timestamp;
}
```
编写一个静态的返回工具类

```java
public class CommonResult<T> extends BaseResult implements Serializable {

    private static final long serialVersionUID = -7268040542410707954L;

    public CommonResult() {

    }

    public CommonResult(boolean success, String message) {
        this.setSuccess(success);
        this.setMessage(message);
    }

    public CommonResult(boolean success) {
        this.setSuccess(success);
    }

    public CommonResult(String code, String message) {
        this.setCode(code);
        this.setMessage(message);
    }

    public CommonResult(boolean success, String message, T data) {
        this.setSuccess(success);
        this.setMessage(message);
        this.setData(data);
    }

    public static CommonResult ok() {
        return ok(BaseErrorCode.Common.SUCCESS);
    }

    public static <T> CommonResult<T> ok(IMessage msg) {
        return baseCreate(msg.getCode(), msg.getMessage(), true);
    }

    public static CommonResult fail() {
        return fail(BaseErrorCode.Common.UNKNOWN_ERROR);
    }

    public static CommonResult fail(IMessage message) {
        return fail(message.getCode(), message.getMessage());
    }

    public static CommonResult fail(String code, String msg) {
        return baseCreate(code, msg, false);
    }

    private static <T> CommonResult<T> baseCreate(String code, String msg, boolean success) {
        CommonResult result = new CommonResult();
        result.setCode(code);
        result.setSuccess(success);
        result.setMessage(msg);
        result.setTimestamp(DateTimeUtil.nowStrTime());
        return result;
    }

    public CommonResult<T> setResult(T data) {
        this.setData(data);
        return this;
    }

    public T getData() {
        return (T) super.getData();
    }
}
```
## 2.2 接口例子

**正确示范：** 
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
```
这种定义统一返回结构和明确的返回对象定义，`smart-doc`能够推导根据接口定义帮你推导出类的字段定义，包括嵌套的对象定义。

**错误示范：** 

```java
@PostMapping("/add")
public CommonResult addUser(@RequestBody User user){
    return CommonResult.ok().setResult(user);
}
```
上面这种情况`smart-doc`无法知道你定义的返回类型
# 泛型命名规范
`smart-doc`的一些用户在使用过程中，在定义一些实体中涉及到泛型命名是使用多字母组合要做为泛型名，`smart-doc`是不支持这样的命名解析的。
包括在`JDK`的源代码中也没有这样定义过，这一是`smart-doc`不想支持奇奇怪怪的泛型命名方式的原因，约定规范一直是`smart-doc`的理念。

虽然没有强制的命名规范，但是为了便于代码阅读，也形成了一些约定俗成的命名规范，如下：
- `T`: `Type`（`JAVA` 类）通用泛型类型，通常作为第一个泛型类型
- `S`: 通用泛型类型，如果需要使用多个泛型类型，可以将S作为第二个泛型类型
- `U`: 通用泛型类型，如果需要使用多个泛型类型，可以将U作为第三个泛型类型
- `V`: 通用泛型类型，如果需要使用多个泛型类型，可以将V作为第四个泛型类型
- `E`: 集合元素泛型类型，主要用于定义集合泛型类型
- `K`: 映射-键泛型类型，主要用于定义映射泛型类型
- `V`: 映射-值泛型类型，主要用于定义映射泛型类型
- `N`: `Number`数值泛型类型，主要用于定义数值类型的泛型类型
- `?`: 表示不确定的`JAVA`类型



