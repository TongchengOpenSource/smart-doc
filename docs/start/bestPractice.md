# Best Practices

smart-doc is a tool that loads the return type and request parameter type of the analysis item source code at compile time based on the generic definition of the interface. If you return the following types in the interface definition of your code, we will not be able to process them.

### Response class (defined using generics)
The unified return of most mature teams is similar to the following (due to the length of the document, many comments are omitted. Please standardize some comments in real code when using smart-doc). Of course, it can be customized according to your own project.
```java
public class CommonResult<T> implements Serializable {

     /**
          * whether succeed
          */
     private boolean success = false;
     private String message;
     private T data;
     private String code;
     private String timestamp;
}

```


### Interface request
This definition unifies the return structure and clear return object definition. Smart-doc can help you deduce the field definition of the class based on the interface definition, including nested object definitions.
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

/**
      * Query user information by page
      * @param user
      * @return
      */
@PostMapping("/page")
public CommonResult<Page<User>> addUser(@RequestBody UserQuery query){
     return CommonResult.ok().setResult(user);
}
```

### Error example
#### 1. Use `Map` in the interface
Because it cannot analyze the key value of the Map in the code, smart-doc cannot generate good documentation.

```java
@GetMapping(value = "/object")
public Map<String, User> testMapUser() {
     return null;
}
```

#### 2. Return `JSONObject`

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
The team must criticize if there is such a definition of return data, who knows what is returned. Programmers can't understand it, let alone `smart-doc`.

#### 3. Return `ModelMap`
 
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
This is the same as `Map`, and `smart-doc` directly blocks `ModelMap`.