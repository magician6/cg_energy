# 添加 Java 8 镜像来源
FROM openjdk:8u212-jre-alpine

# 添加元数据标签
LABEL maintainer="fengzixin@cn.wilmar-intl.com"
LABEL version="1.0"
LABEL author="FZX"
LABEL description="重庆能源看板"

# 设置时区
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

# 添加参数
ARG JAR_FILE
# 添加 Spring Boot 包
ADD ${JAR_FILE} app.jar

# expose port 只是申明，要expost 多个端口时用空格分隔
# 注意该指令只是起到声明作用， 并不会自动完成端口映射  -p HOST_PORT:CONTAINER_PORT
EXPOSE 8080/TCP

# 执行启动命令
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
