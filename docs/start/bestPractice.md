# Best Practice

Smart-doc is a tool that assumes that the generic definition of the interface can be determined to load the return type and request parameter type of the analysis item source code during the compiler period. Unable to process.


# Irregular return definition
## 1.1 Use Map in the interface
Because the key value of the map in the code cannot be analyzed, smart-doc cannot generate a good document.
```java
@GetMapping(value = "/object")
public Map<String, User> testMapUser() {
    return null;
}
```
The key in this generated document is not clear.
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
The team must criticize the return data for such a definition, the ghost knows what it is returning. Programmers can't understand it, let alone smart-doc.

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
This is the same as map, and smart-doc naturally shields `ModelMap`.

## 1.4 Irregular description
Only some common ones are listed above, and there may be many more. If similar examples are used in the project and you are unwilling to adjust the code, then return to swagger to write comments honestly.




# Correct demonstration

## 2.1 Design universal return
The unified return of most mature teams is similar to the following (due to the length of the document, many comments are omitted, use smart-doc to standardize some comments in the real code), of course, you can customize it according to your own project.
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
Some users of smart-doc in the process of defining some entities involve generic names using multi-letter combinations to make generic names. Smart-doc does not support such name resolution.
There is no such definition in the source code of jdk. This is why smart-doc does not want to support weird generic naming methods. Conventions have always been the concept of smart-doc.

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

