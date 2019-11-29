部署流程
=======


1. 修改Jenkinsfile中docker_host为公司内部docker仓库地址
2. 修改Jenkinsfile中push stage中docker认证的key
3. 创建jenkins  pipeline job
4. 构建发布到docker仓库
5. 在需要部署的机器上pull docker image，启动。

e.g.

三台机器
- 10.102.16.2
- 10.102.16.3
- 10.102.16.4

创建配置文件application-prod2.properties
```
eureka.instance.hostname=10.102.16.2

eureka.client.service-url.defaultZone=http://10.102.16.3:${server.port}/eureka/,http://10.102.16.4:${server.port}/eureka/
eureka.instance.instance-id=${eureka.instance.hostname}
```

application-prod3.properties
```
eureka.instance.hostname=10.102.16.3

eureka.client.service-url.defaultZone=http://10.102.16.2:${server.port}/eureka/,http://10.102.16.4:${server.port}/eureka/
eureka.instance.instance-id=${eureka.instance.hostname}
```

application-prod4.properties
```
eureka.instance.hostname=10.102.16.4

eureka.client.service-url.defaultZone=http://10.102.16.3:${server.port}/eureka/,http://10.102.16.2:${server.port}/eureka/
eureka.instance.instance-id=${eureka.instance.hostname}

```


复制./deploy/prod到三台机器，对应修改command的profile即可。

