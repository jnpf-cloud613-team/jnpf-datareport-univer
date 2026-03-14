> 特别说明：源码、JDK、数据库、Redis等安装或存放路径禁止包含中文、空格、特殊字符等

## 一 环境要求

### 1.1 开发环境

| 类目 | 版本说明或建议           |
| --- |------------------|
| 硬件 | 开发电脑建议使用I3及以上CPU，16G及以上内存  |
| 操作系统 | Windows 10/11，MacOS |
| JDK | 默认使用JDK 21，如需要切换JDK 8/11/17版本请参考文档调整代码，推荐使用 `OpenJDK`，如 `Liberica JDK`、`Eclipse Temurin`、`Alibaba Dragonwell`、`BiSheng` 等发行版； |
| Maven | 依赖管理工具，推荐使用 `3.6.3` 及以上版本  |
| Redis | 数据缓存，推荐使用 `5.0` 及以上版本 |
| 数据库 | 兼容 `MySQL 5.7.x/8.x`、`SQLServer 2012+`、`Oracle 11g`、`PostgreSQL 12+`、`达梦数据库(DM8)`、`人大金仓数据库(KingbaseES_V8R6)` |
| IDE   | 代码集成开发环境，推荐使用 `IDEA2024` 及以上版本，兼容 `Eclipse`、 `Spring Tool Suite` 等IDE工具 |
| 文件存储 | 默认使用本地存储，兼容 `MinIO` 及多个云对象存储，如 `阿里云 OSS`、`华为云 OBS`、`七牛云 Kodo`、`腾讯云 COS` 等； |

### 1.2 运行环境

> 适用于测试或生产环境

| 类目 | 版本说明或建议                               |
| --- |-----------------------------------------------|
| 服务器配置 | 建议至少在 `4C/16G/50G` 的机器配置下运行；|
| 操作系统 | 建议使用 `Windows Server 2019` 及以上版本或主流 `Linux` 发行版本，推荐使用 `Linux` 环境；兼容 `统信UOS`，`OpenEuler`，`麒麟服务器版` 等信创环境；    |
| JRE | 默认使用JRE 21，如需要切换JRE 8/11/17版本请参考文档调整代码；推荐使用 `OpenJDK`，如 `Liberica JDK`、`Eclipse Temurin`、`Alibaba Dragonwell`、`BiSheng` 等发行版；   |
| Redis | 数据缓存，推荐使用 `5.0` 及以上版本 |
| 数据库 | 兼容 `MySQL 5.7.x/8.x`、`SQLServer 2012+`、`Oracle 11g`、`PostgreSQL 12+`、`达梦数据库(DM8)`、`人大金仓数据库(KingbaseES_V8R6)` |
| 文件存储 | 默认使用本地存储，兼容 `MinIO` 及多个云对象存储，如 `阿里云 OSS`、`华为云 OBS`、`七牛云 Kodo`、`腾讯云 COS` 等； |

## 二 关联项目

| 项目                 | 分支            | 说明         |
|--------------------|---------------|------------|
| jnpf-common | v6.1.x-stable  | 项目基础依赖源码 |
| jnpf-java-datareport-univer-core | v6.1.x-stable  | Univer报表核心依赖源码 |

## 三 Maven私服配置

> 建议使用 Apache Maven 3.6.3 及以上版本<br>以解决依赖无法从公共Maven仓库下载的问题<br>通过官方私服下载依赖完成后，由于IDEA的缓存可能会出现部分报红，重启IDEA即可

打开Maven安装目录中的 `conf/settings.xml` 文件，<br/>
在 `<servers></servers>` 中添加如下内容

```xml
<server>
  <id>maven-releases</id>
  <username>您的账号</username>
  <password>您的密码</password>
</server>
```

在 `<mirrors></mirrors>` 中添加

```xml
<mirror>
  <id>maven-releases</id>
  <mirrorOf>*</mirrorOf>
  <name>maven-releases</name>
  <url>https://repository.jnpfsoft.com/repository/maven-public/</url>
</mirror>
```

## 四 开发环境

### 4.1 导入数据库脚本

> 以 MySQL数据库为例<br>字符集：`utf8mb4` <br/>排序规则：`utf8mb4_general_ci`

在MySQL创建 `jnpf_init` 数据库，并将 `jnpf-database/MySQL/jnpf_db_init.sql` 导入；

### 4.2 导入依赖

详见 `jnpf-java-datareport-univer-core` 项目中的 `README.md` 文档说明

### 4.3 项目配置

打开编辑 `jnpf-datareport-univer-admin/src/main/resources/application.yml`

#### 4.3.1 指定环境配置

环境说明：

- `application-dev.yml` 开发环境(默认)
- `application-preview.yml` 预生产环境
- `application-test.yml` 测试环境
- `application-prod.yml` 生产环境

> 以开发环境为例，根据实际需求修改

```yaml
# application.yml第 6 行,可选值：dev(开发环境-默认)、test(测试环境)、preview(预生产环境)、prod(生产环境)
active: dev
```

#### 4.3.2 配置域名

打开编辑 `jnpf-datareport-univer-admin/src/main/resources/application.yml` ，修改以下配置

```yaml
  ApiDomain: http://127.0.0.1:30000 #主项目后端域名(文档预览中使用)
  FrontDomain: http://127.0.0.1:3100 #前端域名(文档预览中使用)
  AppDomain: http://127.0.0.1:8080 #app/h5端域名配置(文档预览中使用)
```
#### 4.3.3 调整运行端口
> 根据实际需求调整

打开编辑 `jnpf-datareport-univer-admin/src/main/resources/application-dev.yml`，第 5 行

```yaml
port: 32000 # 默认运行端口
```

#### 4.3.4 数据源配置

配置参数说明：

- `db-type`：数据库类型（可选值：`MySQL`、`SQLServer`、`Oracle`、`PostgreSQL`、`DM`、`KingbaseES`）
- `host`：数据库主机地址
- `port`：数据库端口
- `dbname`：平台初始库
- `username`：数据库用户名
- `password`：数据库密码
- `db-schema`：数据库模式
- `prepare-url`：自定义JDBC连接配置

打开编辑 `jnpf-datareport-univer-admin/src/main/resources/application-dev.yml`，修改以下配置

##### 4.3.4.1 MySQL数据库

```yaml
  datasource:
    db-type: MySQL
    host: 127.0.0.1
    port: 3306
    db-name: jnpf_init
    username: dbuser
    password: dbpasswd
    db-schema:
    prepare-url:
```

##### 4.3.4.2 SQLServer数据库

```yaml
  datasource:
    db-type: SQLServer
    host: 127.0.0.1
    port: 1433
    db-name: jnpf_init
    username: dbuser
    password: dbpasswd
    db-schema:
    prepare-url:
```

##### 4.3.4.3 Oracle数据库

```yaml
  datasource:
    db-type: Oracle
    host: 127.0.0.1
    port: 1521
    db-name:
    username: JNPF_INIT
    password: dbpasswd
    db-schema:
    prepare-url: jdbc:oracle:thin:@127.0.0.1:1521:ORCL
```

##### 4.3.4.4 PostgreSQL数据库配置

```yaml
  datasource:
    db-type: PostgreSQL
    host: 127.0.0.1
    port: 5432
    db-name: jnpf_init
    username: postgres
    password: dbpasswd
    db-schema: public
    prepare-url:
```

##### 4.3.4.5 达梦（DM8）数据库

```yaml
  datasource:
    db-type: DM
    host: 127.0.0.1
    port: 5236
    db-name: JNPF_INIT
    username: DBUSER
    password: dbpasswd
    db-schema:
    prepare-url:
    tablespace: MAIN
```

##### 4.3.4.6 人大金仓（KingbaseES_V8R6）数据库

```yaml
  datasource:
    db-type: KingbaseES
    host: 127.0.0.1
    port: 54321
    db-name: jnpf_init
    username: system
    password: dbpasswd
    db-schema: public
    prepare-url:
```

#### 4.3.5 Redis配置

打开编辑 `jnpf-datareport-univer-admin/src/main/resources/application-dev.yml`，修改以下配置
> 支持单机模式和集群模式，配置默认为单机模式

**若使用Redis单机模式**
> 第 71-82 行

```yaml
  redis:
    database: 1 #缓存库编号
    host: 127.0.0.1
    port: 6379
    password: 123456  # 密码为空时，请将本行注释
    timeout: 3000 #超时时间(单位：秒)
    lettuce: #Lettuce为Redis的Java驱动包
      pool:
        max-active: 8 # 连接池最大连接数
        max-wait: -1ms  # 连接池最大阻塞等待时间（使用负值表示没有限制）
        min-idle: 0 # 连接池中的最小空闲连接
        max-idle: 8 # 连接池中的最大空闲连接
```

**若使用Redis集群模式**
> 第 85-101 行

```yaml
 redis:
   cluster:
     nodes:
       - 192.168.0.225:6380
       - 192.168.0.225:6381
       - 192.168.0.225:6382
       - 192.168.0.225:6383
       - 192.168.0.225:6384
       - 192.168.0.225:6385
   password: 123456 # 密码为空时，请将本行注释
   timeout: 3000 # 超时时间(单位：秒)
   lettuce: #Lettuce为Redis的Java驱动包
     pool:
       max-active: 8 # 连接池最大连接数
       max-wait: -1ms  # 连接池最大阻塞等待时间（使用负值表示没有限制）
       min-idle: 0 # 连接池中的最小空闲连接
       max-idle: 8 # 连接池中的最大空闲连接
```

#### 4.3.6 静态资源配置

打开编辑 `jnpf-datareport-univer-admin/src/main/resources/application-dev.yml` ，修改以下配置(第 121-166 行)
> 默认使用本地存储，兼容 `MinIO` 及多个云对象存储，如阿里云 OSS、华为云 OBS、七牛云 Kodo、腾讯云 COS等

```yaml
  # ===================== 文件存储配置 =====================
  file-storage: #文件存储配置，不使用的情况下可以不写
    default-platform: local-plus-1 #默认使用的存储平台
    thumbnail-suffix: ".min.jpg" #缩略图后缀，例如【.min.jpg】【.png】
    local-plus: # 本地存储升级版
      - platform: local-plus-1 # 存储平台标识
        enable-storage: true  #启用存储
        enable-access: true #启用访问（线上请使用 Nginx 配置，效率更高）
        domain: "" # 访问域名，例如：“http://127.0.0.1:8030/”，注意后面要和 path-patterns 保持一致，“/”结尾，本地存储建议使用相对路径，方便后期更换域名
        base-path: D:/project/jnpf-resources/ # 基础路径
        path-patterns: /** # 访问路径
        storage-path:  # 存储路径
    aliyun-oss: # 阿里云 OSS ，不使用的情况下可以不写
      - platform: aliyun-oss-1 # 存储平台标识
        enable-storage: false  # 启用存储
        access-key: ??
        secret-key: ??
        end-point: ??
        bucket-name: ??
        domain: ?? # 访问域名，注意“/”结尾，例如：https://abc.oss-cn-shanghai.aliyuncs.com/
        base-path: hy/ # 基础路径
    qiniu-kodo: # 七牛云 kodo ，不使用的情况下可以不写
      - platform: qiniu-kodo-1 # 存储平台标识
        enable-storage: false  # 启用存储
        access-key: ??
        secret-key: ??
        bucket-name: ??
        domain: ?? # 访问域名，注意“/”结尾，例如：http://abc.hn-bkt.clouddn.com/
        base-path: base/ # 基础路径
    tencent-cos: # 腾讯云 COS
      - platform: tencent-cos-1 # 存储平台标识
        enable-storage: false  # 启用存储
        secret-id: ??
        secret-key: ??
        region: ?? #存仓库所在地域
        bucket-name: ??
        domain: ?? # 访问域名，注意“/”结尾，例如：https://abc.cos.ap-nanjing.myqcloud.com/
        base-path: hy/ # 基础路径
    minio: # MinIO，由于 MinIO SDK 支持 AWS S3，其它兼容 AWS S3 协议的存储平台也都可配置在这里
      - platform: minio-1 # 存储平台标识
        enable-storage: true  # 启用存储
        access-key: Q9jJs2b6Tv
        secret-key: Thj2WkpLu9DhmJyJ
        end-point: http://192.168.0.207:9000/
        bucket-name: jnpfsoftoss
        domain:  # 访问域名，注意“/”结尾，例如：http://minio.abc.com/abc/
        base-path:  # 基础路径
```

### 4.4 执行调试或运行

#### 4.4.1 `jnpf-java-datareport-univer-core` 项目未使用加密

- 在IDEA中, 展开右侧 `Maven` 中 `Profiles` 去除勾选 `encrypted` 选项, 再点击 Maven `刷新` 图标刷新Maven
- 找到 `jnpf-datareport-univer-admin/src/main/java/jnpf/ReportUniverApplication.java`，右击运行即可。

若使用JDK9及以上版本，在IDEA中，打开 `Edit Configurations` VM启动参数添加如下参数：

 ```bash
 --add-opens java.base/java.lang=ALL-UNNAMED
 ```

#### 4.4.2 `jnpf-java-datareport-univer-core` 项目使用加密

- 在IDEA中，展开右侧 `Maven` 中 `Profiles` 勾选 `encrypted` 选项, 再点击Maven `刷新` 图标刷新Maven
- 在IDEA中，双击右侧 `Maven` 中 `jnpf-datareport-univer` > `clean` 将会自动安装加密打包插件, 并创建创建 `jnpf-datareport-univer-entity/target/copylib` 复制依赖包用于下一步运行

**参数说明**：

```bash
# 打开项目中`jnpf-datareport-univer-entity/target/copylib` 目录, 复制Jar包 `jnpf-datareport-univer-model-版本号.jar` 的文件名
-javaagent:项目存放路径/jnpf-datareport-univer-entity/target/copylib/jnpf-datareport-univer-model-当前版本号.jar(上面复制的文件名)="decryptProjectPathPrefix=jnpf-datareport-univer-common___jnpf-datareport-univer-model"
```

- 在IDEA中，打开 `Edit Configurations` VM启动参数添加如下参数

若使用 JDK8 运行，需要替换下方命令示例中的Jar包路径

 ``` bash
-XX:+DisableAttachMechanism
-javaagent:D:/Projects/IdeaProjects/jnpf-java-datareport-univer/jnpf-datareport-univer-entity/target/copylib/jnpf-datareport-univer-model-6.1.0-RELEASE.jar="decryptProjectPathPrefix=jnpf-datareport-univer-common___jnpf-datareport-univer-model"
 ```

若使用JDK 9及以上版本，需要替换下方命令示例中的Jar包路径

 ``` bash
--add-opens java.base/java.lang=ALL-UNNAMED
-XX:+DisableAttachMechanism
-javaagent:D:/Projects/IdeaProjects/jnpf-java-datareport-univer/jnpf-datareport-univer-entity/target/copylib/jnpf-datareport-univer-model-6.1.0-RELEASE.jar="decryptProjectPathPrefix=jnpf-datareport-univer-common___jnpf-datareport-univer-model"
 ```
## 五 项目发布

### 5.1 `jnpf-java-datareport-univer-core` 项目未使用加密

- 在IDEA中, 展开右侧 `Maven` 中 `Profiles` 去除勾选 `encrypted` 选项, 再点击Maven `刷新` 图标刷新Maven
- 在IDEA中，双击右侧Maven中 `jnpf-datareport-univer` > `Lifecycle` > `clean` 清理项目
- 在IDEA中，双击右侧Maven中 `jnpf-datareport-univer` > `Lifecycle` > `package` 打包项目
- 打开 `jnpf-datareport-univer/jnpf-datareport-univer-entity/target`，将 `jnpf-datareport-univer-admin-6.1.0-RELEASE.jar` 上传至服务器

### 5.2 `jnpf-java-datareport-univer-core` 项目使用加密

- 在IDEA中, 展开右侧 `Maven` 中 `Profiles` 勾选 `encrypted` 选项, 再点击Maven `刷新` 图标刷新Maven
- 在IDEA中，双击右侧 `Maven` > `jnpf-java-datareport-univer` > `clean` 将会自动安装加密打包插件
- 在IDEA中，双击右侧 `Maven` > `jnpf-java-datareport-univer` > `Lifecycle` > `package` 打包项目
- 打开 `jnpf-datareport-univer/jnpf-datareport-univer-admin/target`，将 `jnpf-datareport-univer-admin-6.1.0-RELEASE.jar` 上传至服务器
- 启动命令

**若使用 JDK/JRE 8 运行**

```bash
java -javaagent:./jnpf-datareport-univer-admin-6.1.0-RELEASE.jar -XX:+DisableAttachMechanism -jar jnpf-datareport-univer-admin-6.1.0-RELEASE.jar
 ```

**若使用 JDK/JRE 9及以上版本运行**

```bash
java -javaagent:./jnpf-datareport-univer-admin-6.1.0-RELEASE.jar --add-opens java.base/java.lang=ALL-UNNAMED -XX:+DisableAttachMechanism -jar jnpf-datareport-univer-admin-6.1.0-RELEASE.jar
 ```
