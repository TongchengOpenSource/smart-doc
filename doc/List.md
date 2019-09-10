关于list结构的返回json数据测试

# List<String>结构

api-doc对于List中返回基础数据类型都是支持的
```
/**
 * List<String>
 *
 * @return
 */
@GetMapping(value = "listString")
public List<String> testList() {
    return null;
}
```
api-doc生成的响应数据
```
[ "ivvqah","isrz5x"]
```
# List<Map<String,String>>结构

```
/**
 * 
 * @return
 */
@GetMapping(value = "/map/Primitive")
public List<Map<String,String>> testMap() {
    return null;
}
```
api-doc生成的响应数据
```
[{
	"mapKey1": "o9mibj",
	"mapKey2": "3dnnrn"
}]
```

# List<Map<String,T>>结构

```
@GetMapping(value = "/map/Primitive")
public List<Map<String,Student>> testMap() {
  return null;
}

```
相应数据省略

# 测试List<T>结构

```
/**
 * 测试List<T>结构
 * @return
 */
@GetMapping(value = "/map/Primitive")
public List<Teacher> testMap() {
    return null;
}
```

# List<T<M,N>>结构
```
/**
 * 测试List<T<M,N>>结构
 * @return
 */
@GetMapping(value = "/map/Primitive")
public List<Teacher<User,User>> testMap() {
    return null;
}
```
# List<Map<M,N<P,k>>>超复杂结构
```
/**
 * 测试List<Map<M,N<P,k>>>超复杂结构
 * @return
 */
@GetMapping(value = "/map/Primitive")
public List<Map<String,Teacher<User,User>>> testMap() {
    return null;
}
```
api-doc自动返回的数据
```
[{
	"mapKey": {
		"data": {
			"userName": "lxh2yi",
			"userAddress": "6jfp3h",
			"userAge": 741
		},
		"data1": {
			"userName": "1wp54g",
			"userAddress": "8ul6m4",
			"userAge": 550
		},
		"age": 10
	}
}]
```
# List<T<List<M>,List<M>,List<M>>>超复杂结构

```
/**
 * List<T<List<M>,List<M>,List<M>>>
 * @return
 */
@GetMapping(value = "listString")
public List<Teacher<List<User>,List<User>,List<User>>> testListString(){
    return null;
}
```
# 其他复杂结构
```
/**
 * List<T<List<M>,List<M>,List<M>>>
 *
 * @return
 */
@GetMapping(value = "listString")
public List<Teacher<Teacher<User,User,User>,User,User>> testListString() {
    return null;
}

@GetMapping(value = "listString")
public List<Teacher<Teacher<User,User,User>,Teacher<User,User,User>,Teacher<User,User,User>>> testListString() {
    return null;
}
```

**注意：** api-doc为了传入的复杂泛型结构数据，做了许多情况的测试，目前基本能兼容系统开发中95%以上的List返回接口，
也提供了一些能够处理的很复杂的泛型结构，但是这种复杂的泛型结构在开发中是不被推荐的。