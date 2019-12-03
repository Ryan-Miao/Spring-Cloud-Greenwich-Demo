eureka-server
============

注冊中心eureka.

## How to run

配置了dev1, dev2两个配置文件。分别是不同的端口，实现了双注册中心互相备份。

```
java -jar eureka-server-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev1
java -jar eureka-server-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev2
```

访问localhost:9001即可。

也可以使用docker

```
cd .deploy
docker-compose up
```
绑定了三个eureka注册中心到本地的9001,9002,9003, 实现三台互相备份。


## 可能遇到到问题

都在unavailable-replicas。

1. 保证spring.application.name相同
2. 保证eureka.instance.appname相同
3. 保证eureka.instance.hostname不同
4. 保证cloud和spring boot版本一致。比如 cloud:Greenwich.SR4  -- boot:2.1.10.RELEASE
5. 保证加载了org.springframework.boot:spring-boot-starter-actuator

通常默认加载health check, 通过比较check结果来计算副本, 实在不行就忽略即可。

```java
//com.netflix.eureka.util.StatusUtil.isReplicaAvailable

    private boolean isReplicaAvailable(String url) {

        try {
            Application app = registry.getApplication(myAppName, false);
            if (app == null) {
                return false;
            }
            for (InstanceInfo info : app.getInstances()) {
                if (peerEurekaNodes.isInstanceURL(url, info)) {
                    return true;
                }
            }
        } catch (Throwable e) {
            logger.error("Could not determine if the replica is available ", e);
        }
        return false;
    }

```


