部署流程
=======


1. 修改Jenkinsfile中docker_host为公司内部docker仓库地址
2. 修改Jenkinsfile中push stage中docker认证的key
3. 创建jenkins  pipeline job
4. 构建发布到docker仓库
5. 在需要部署的机器上pull docker image，启动。
6. 可以使用k8s

