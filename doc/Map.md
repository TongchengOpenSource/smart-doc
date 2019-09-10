Api-doc对于api中map结构数据的json化处理多组测试用例,对于map返回的json结构，
目前基本仅仅支持，String类型的key。

**基础数据类型：** json支持的基本java数据类型(不包含byte,包含String)

# map使用基础数据类型

```
/**
 * 测试map使用基础数据类型
 * @return
 */
@GetMapping(value = "/map/Primitive")
public Map<String,Integer> testMap() {
    return null;
}
```
api-doc 生成的json:
```
{
	"mapKey1": 721,
	"mapKey2": 280
}
```
# map使用Object

因为api-doc使用的是无侵入静态分析生成api文档，因此对于直接使用Object做map value的接口，api-doc无法准确的生成json。
所以api-doc返回是会在默认json中加一段警告，使用者需要自己去修改返回数据，或者是使用显示的类型数据结构。
```
/**
 * 测试map使用基础数据类型
 * @return
 */
@GetMapping(value = "/map/Primitive")
public Map<String,Object> testMap() {
    return null;
}
```
api-doc 生成的json:
```
{
	"mapKey": {
		"waring": "You may use java.util.Object for Map value;Api-doc can't be handle."
	}
}
```
# map中属于自己定义的简单数据结构
User对象的属性仅仅是基本数据类型
```
/**
 * 测试map使用自定义数据结构
 * @return
 */
@GetMapping(value = "/map/Primitive")
public Map<String,User> testMap() {
    return null;
}
```
api-doc 生成的json:
```
{
	"mapKey": {
		"userName": "7t2ccy",
		"userAddress": "3ipy7g",
		"userAge": 280
	}
}
```
# map中属于自己定义的复杂数据结构
Student对象的属性有基本类型又有User类型和Map类型的属性。
```
/**
 * 测试map使用自定义数据结构
 * @return
 */
@GetMapping(value = "/map/Primitive")
public Map<String,Student> testMap() {
    return null;
}
```
api-doc 生成的json:
```
{
	"mapKey": {
		"stuName": "9cwzml",
		"stuAge": 792,
		"stuAddress": "rdfmtx",
		"user": {
			"userName": "fjglql",
			"userAddress": "yy6vkf",
			"userAge": 398
		},
		"userMap": {
			"mapKey": {
				"userName": "paw90w",
				"userAddress": "mnmz42",
				"userAge": 937
			}
		},
		"user1": {
			"userName": "rr3v6g",
			"userAddress": "rbeorq",
			"userAge": 399
		}
	}
}
```

# Map<M,N<P,k>>复杂结构

```
{
	"mapKey":{
		"data":{
			"userName":"tumrit",
			"userAddress":"v8fvdi",
			"userAge":465
		},
		"data1":{
			"userName":"f7wbwk",
			"userAddress":"brdh8j",
			"userAge":345
		},
		"age":194
	}
}
```
# Map<String,T<List<M>,N>超复杂结构

```
/**
 * Map<String,T<List<M>,N>超复杂结构
 * @return
 */
@GetMapping(value = "/map/Primitive")
public Map<String,Teacher<List<User>,User>> testMap() {
    return null;
}
```
# Map其他复杂结构

对于map的key采用多泛型的情况，目前api-doc也是支持的。
```
/**
 * Map<String,T<List<M>,N>超复杂结构
 * @return
 */
public Map<String,Teacher<Map<String,User>,Map<String,User>,Map<String,User>>> testMap() {
    return null;
}
```
**注意：** api-doc为了传入的复杂泛型结构数据，做了许多情况的测试，目前基本能兼容系统开发中95%以上的Map返回接口，
也提供了一些能够处理的很复杂的泛型结构，但是这种复杂的泛型结构在开发中是不被推荐的。