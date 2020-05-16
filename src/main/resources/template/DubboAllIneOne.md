# 1、数据同步接口

**接口名称：** com.xxx.service

**接口地址：** dubbo://xxx:20880/com.xxx.service

**接口协议：** dubbo

**接口版本：** 1.0.0

## 1.1 sycData方法

**方法定义：** User getUser(String name)

**方法描述：** com.xxx.service

**请求参数：**

Param | Type|Description|Since
---|---|---|---
name|String|姓名|-
age|Integer|年龄|-

**响应参数：**

Field | Type|Description|Since
---|---|---|---
name|String|姓名|-
age|Integer|年龄|-