# FeedApiClientDemo

#### 介绍
守望者威胁情报Feed系统，合作伙伴下载API接口，java和python的客户端代码示例。

#### 说明
客户端共需要做两次数据交互
- 第一次请求：获取请求当天的数据类型列表，其中包含dataName,cursor和文件的md5值；
- 第二次请求：从第一次请求的响应结果中，获取到需要的文件类型的dataName，将其作为第二次请求的type；cursor作为第二次请求的cursor；服务器将返回dataName.json.gz的gzip压缩包；

1. token：用户必须注册，登录后在用户中心获取token值，注册地址：https://feed.watcherlab.com/#/user/register 
2. type：首次请求使用all，第二次请求使用对应的dataName作为type类型；
3. cursor：第一次请求使用0，第二次请求使用对应数据类型的cursor值；
4. date：获取数据的日期，应至少是昨天的日期，日期格式：yyyyMMdd,例：19001001

#### 联系我们
https://feed.watcherlab.com

将开源情报做到极致


