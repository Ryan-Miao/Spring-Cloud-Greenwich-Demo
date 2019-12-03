service-gateway
===============


服务网关。本demo提供了多个service，但只能通过eureka实现互相调用。当别的项目组需要使用
我们的服务的时候，我们如何提供服务。

- 不能直接让其他项目组直接连接我们的eureka，这样互相影响太大
- 提供restful服务
- 简单限定一些权限，比如允许谁访问，允许访问哪些服务

## 快速体验

启动eureka server.
```
gradlew clean build
cd eureka-server/.deploy
docker-compose up
```

然后启动provider
```
cd provider-user-service/.deploy
docker-compose up
```

最后启动service gateway
```
cd service-gateway/.deploy
docker-compose up
```

这时候，三个服务都是运行在同一个docker 子网spring_cloud_net中。其中eureka
映射到本地9001,9002,9003.可以直接访问localhost:9001.

gateway映射本地8088， 访问

```
GET http://localhost:8088/USER-SERVICE/users
header:
client-secret:123456
client-id:test
```


## 开发

启动eureka, 本地启动provider, 本地启动gateway即可。

快速访问的方式，服务都是部署在子网中，只有gateway暴露端口。本地启动则都在本地网络，
可以直接访问。


## 部署

参见.deploy下的Jenkinsfile