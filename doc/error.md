api-doc对Spring mvc或者SpringBoot应用的Controller接口返回做了一些强制规约，一旦在代码中使用 这些被api-doc不推荐的接口返回类型，api-doc将会直接报错。

# 违反规约的实例

## 直接返回Object

```
/**
 * 返回object
 * @return
 */
@GetMapping("/test/Object")
public Object getMe(){
    return null;
}
```

报错提示：Please do not return java.lang.Object directly in api interface.

## 将非String对象作为Map的key，然后将map作为接口中返回

```
/**
 * 测试object的作为map的key
 * @return
 */
@GetMapping("/test/map")
public Map<Object,Object> objectMap(){
    return null;
}
```