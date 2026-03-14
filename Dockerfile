# 基础镜像
FROM bellsoft/liberica-openjre-rocky:21
# FROM bellsoft/liberica-openjre-rocky:17
# FROM bellsoft/liberica-openjre-rocky:11
# FROM bellsoft/liberica-openjre-rocky:8
LABEL maintainer=jnpf-team

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 指定运行时的工作目录
WORKDIR /data/jnpfsoft/univerApi

# 将构建产物jar包拷贝到运行时目录中
COPY jnpf-datareport-univer-admin/target/*.jar ./jnpf-univer-admin.jar

# 指定容器内运行端口
EXPOSE 32000

# 指定容器启动时要运行的命令
ENTRYPOINT ["/bin/sh","-c","java -javaagent:./jnpf-univer-admin.jar -Dfile.encoding=utf8 -Djava.security.egd=file:/dev/./urandom --add-opens java.base/java.lang=ALL-UNNAMED -XX:+DisableAttachMechanism -jar jnpf-univer-admin.jar"]