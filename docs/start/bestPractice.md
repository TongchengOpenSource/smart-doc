# Best Practice

smart-doc is a tool that loads source code during compilation and analyzes and generates API documentation according to the generic definition of the interface. 
If you use several interface return types similar to the following in your code, we will not be able to handle them well.

# Irregular return definition
## 1.1 Use Map in the interface
Because the key value of the map in the code cannot be analyzed, smart-doc cannot generate a good document.
```java
@GetMapping(value = "/object")
public Map<String, User> testMapUser() {
    return null;
}

// or
@GetMapping(value = "/object")
public Map<String, Object> testMap() {
    return null;
}

```
Smart-doc cannot generate high-quality documents when using Map as the interface return value.
## 1.2 Return JSONObject

```java
/**
 * Return user information
 * @return
 */
@GetMapping(value = "/user")
public JSONObject object() {
    return null;
}
```
JSONObject can return any json data while the program is running, so smart-doc cannot clearly know the return data type to automatically generate documents.
## 1.3 Return ModelMap
 
```java
/**
 * Return user information
 * @return
 */
@GetMapping(value = "/user")
public ModelMap object() {
    return null;
}

```
This is the same as using Map. Smart-doc has ignored `ModelMap` internally.





# Recommended example

## 2.1 Design a general data return structure
A similar general return data structure can be designed 
```java
public abstract class BaseResult<T> implements Serializable {

    private boolean success = false;

    private String message;

    private T data;

    private String code;

    private String timestamp;
}
```
Write a static return tool class

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
## 2.2 Interface example

**Correct demonstration:** 
```java
/**
 * Add user information
 * @param user
 * @return
 */
@PostMapping("/add")
public CommonResult<User> addUser(@RequestBody User user){
    return CommonResult.ok().setResult(user);
}
```
This definition has a unified return structure and a clear return object definition. Smart-doc can derive the field definition of the class according to the interface definition, including nested object definitions.

**Error demonstration:** 

```java
@PostMapping("/add")
public CommonResult addUser(@RequestBody User user){
    return CommonResult.ok().setResult(user);
}
```
In the above case, smart-doc cannot know the return type you defined.

# Generic naming convention

When using smart-doc, we recommend using the following specifications in the code to define JAVA generics,
Irregular definitions may cause smart-doc to not parse your source code correctly

Although there is no mandatory naming convention, in order to facilitate code reading, some conventional naming conventions have been formed, as follows:
- T: Type (Java class) general generic type, usually as the first generic type
- S: Universal generic type, if you need to use multiple generic types, you can use S as the second generic type
- U: Universal generic type, if you need to use multiple generic types, you can use U as the third generic type
- V: General generic type, if you need to use multiple generic types, you can use V as the fourth generic type
- E: Collection element generic type, mainly used to define collection generic type
- K: mapping-key generic type, mainly used to define mapping generic type
- V: mapping-value generic type, mainly used to define mapping generic type
- N: Number numeric generic type, mainly used to define generic types of numeric types
- ?: indicates an uncertain java type

