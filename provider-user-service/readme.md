user-service
=============


## How to run dev

 1.run eureka server for dev

```
gradlew eureka-server:clean eureka-server:build
cd eureka-server/.deploy

docker-compose up
```
thus, it will start 3 eureka server in localhost on ports 9001,9002,9003


 2.run com.demo.service.user.UserServiceApplication in profile dev
 
 ```
gradlew provider-user-service:bootRun
```

 3.view localhost:8081 on browser
 
 
## How to deploy as service

1.Run eureka cluster
```
gradlew eureka-server:clean eureka-server:build
cd eureka-server/.deploy

docker-compose up
```

2.Deploy user service in docker network
```
gradlew provider-user-service:clean provider-user-service:build
cd provider-user-service/.deploy
docker-compose up -d
docker-compose scale user-service=2
```

thus, it will run 2 user-service instance and register them on eureka.

consumer can consume the service now. 