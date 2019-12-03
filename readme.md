Spring Cloud Greenwich Demo
==========================



基于Spring Cloud Greenwich的学习demo.


## 快速测试

```
gradlew clean build
cd eureka-server/.deploy
docker-compose up -d

cd provider-user-service/.deploy
docker-compose up 

cd service-gateway/.deploy
docker-compose up

访问http://localhost:8088/USER-SERVICE/users

```

使用docker-compose

把eureka, provider，gateway都放到同一个网络spring-cloud-net(172.18.xxx), 然后，gateway做端口
映射，即本地可以访问，如此实现只可以通过gateway访问其他服务。


## 开发调试

1.启动eureka server
```
gradlew clean build
cd eureka-server/.deploy
docker-compose up -d
```

也可以手动java -jar启动eureka，docker-compose启动了3个eureka，
绑定本地端口为9001,9002,9003. 其他服务可以通过这个注册。

2.其他服务

直接启动即可，默认读取dev配置文件。


## 测试和生产部署

微服务化会造成服务数量极其庞大，必须走持续集成才有可能维护的起来。

每个项目.deploy文件夹下，有参考Jenkinsfile，用来创建jenkins pipeline.
有k8s.yml，用来创建k8s service.




