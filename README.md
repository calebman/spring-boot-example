### 本文简介
- 为什么使用SpringBoot
- 搭建怎样一个环境
- 开发环境
- 导入快速启动项目
- 集成前准备
- 集成Mybatis
- 集成Swagger2
- 多环境配置
- 多环境下的日志配置
- jwt配置
- 常用配置
### 为什么使用SpringBoot
SpringBoot相对于传统的SSM框架的优点是提供了默认的样板化配置，简化了Spring应用的初始搭建过程，如果你不想被众多的xml配置文件困扰，可以考虑使用SpringBoot替代
### 搭建怎样一个环境
本文将基于Spring官方提供的快速启动项目模板集成Mybatis、Swagger2框架，并讲解mybatis generator一键生成代码插件、logback、一键生成文档以及多环境的配置方法，最后再介绍一下自定义配置的注解获取、全局异常处理等经常用到的东西。
### 开发环境
本人使用IDEA作为开发工具，IDEA下载时默认集成了SpringBoot的快速启动项目可以直接创建，如果使用Eclipse的同学可以考虑安装SpringBoot插件或者直接从[这里](https://start.spring.io/)配置并下载SpringBoot快速启动项目，需要注意的是本次环境搭建选择的是SpringBoot2.0的快速启动框架，SpringBoot2.0要求jdk版本必须要在1.8及以上。
### 导入快速启动项目
不管是由IDEA导入还是现实下载模板工程都需要初始化快速启动工程的配置，如果使用IDEA，在新建项目时选择Spring Initializr，主要配置如下图
![IDEA新建SpringBoot项目-填写项目/包名](http://upload-images.jianshu.io/upload_images/10936059-865b16fcec74d227.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![IDEA新建SpringBoot项目-选择依赖包](http://upload-images.jianshu.io/upload_images/10936059-4fc8bad8fe8d75be.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
点击next之后finish之后IDEA显示正在下载模板工程，下载完成后会根据pom.xml下载包依赖，依赖下载完毕后模板项目就算创建成功了，如果是直接从官方网站配置下载快速启动项目可参考下图配置
![直接下载SpringBoot快速启动项目-项目配置](http://upload-images.jianshu.io/upload_images/10936059-9059e75b2f08fb1a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
从Search for dependencies 框中输入并选择Web、Mysql、Mybatis加入依赖，点击Generate Project下载快速启动项目，然后在IDE中选择导入Maven项目，项目导入完成后可见其目录结构如下图
![快速启动项目-项目结构](http://upload-images.jianshu.io/upload_images/10936059-b401a783d6cc8337.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
需要关注红色方框圈起来的部分，由上往下第一个java类是用来启动项目的入口函数，第二个properties后缀的文件是项目的配置文件，第三个是项目的依赖包以及执行插件的配置
### 集成前准备
##### 修改.properties为.yml
yml相对于properties更加精简而且很多官方给出的Demo都是yml的配置形式，在这里我们采用yml的形式代替properties，相对于properties形式主要有以下两点不同
>1. 对于键的描述由原有的 "." 分割变成了树的形状
>2. 对于所有的键的后面一个要跟一个空格，不然启动项目会报配置解析错误
```
# properties式语法描述
spring.datasource.name = mysql
spring.datasource.url = jdbc:mysql://localhost:3306/db?characterEncoding=utf-8
spring.datasource.username = root
spring.datasource.password = 123
# yml式语法描述
spring:
    datasource:
        name: mysql
        url: jdbc:mysql://localhost:3306/db?characterEncoding=utf-8
        username: root
        password: 123
```

##### 配置所需依赖
快速启动项目创建成功后我们观察其pom.xml文件中的依赖如下图，包含了我们选择的Web、Mybatis以及Mysql
```
        <!-- spring web mvc -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- mybatis -->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>1.3.1</version>
        </dependency>
        <!-- mysql -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        <!-- test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
```
但是我们使用ORM框架一般还会配合数据库连接池以及分页插件来使用，在这里我选择了阿里的druid以及pagehelper这个分页插件，再加上我们还需要整合swagger2文档自动化构建框架，所以增加了以下四个依赖项
```
		<!-- 分页插件 -->
		<dependency>
			<groupId>com.github.pagehelper</groupId>
			<artifactId>pagehelper-spring-boot-starter</artifactId>
			<version>1.2.3</version>
		</dependency>
		<!-- alibaba的druid数据库连接池 -->
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>druid-spring-boot-starter</artifactId>
			<version>1.1.1</version>
		</dependency>
		<!-- alibaba的json格式化对象 -->
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>fastjson</artifactId>
			<version>1.2.31</version>
		</dependency>
		<!-- 自动生成API文档 -->
		<dependency>
			<groupId>io.springfox</groupId>
			<artifactId>springfox-swagger2</artifactId>
			<version>2.5.0</version>
		</dependency>
```
### 集成Mybatis
Mybatis的配置主要包括了druid数据库连接池、pagehelper分页插件、mybatis-generator代码逆向生成插件以及mapper、pojo扫描配置
##### 配置druid数据库连接池
添加以下配置至application.yml文件中
```
spring:
    datasource:
        # 如果存在多个数据源，监控的时候可以通过名字来区分开来
        name: mysql
        # 连接数据库的url
        url: jdbc:mysql://localhost:3306/db?characterEncoding=utf-8
        # 连接数据库的账号
        username: root
        #  连接数据库的密码
        password: 123
        # 使用druid数据源
        type: com.alibaba.druid.pool.DruidDataSource
        # 扩展插件
        # 监控统计用的filter:stat 日志用的filter:log4j 防御sql注入的filter:wall
        filters: stat
        # 最大连接池数量
        maxActive: 20
        # 初始化时建立物理连接的个数。初始化发生在显示调用init方法，或者第一次getConnection时
        initialSize: 1
        # 获取连接时最大等待时间，单位毫秒
        maxWait: 60000
        # 最小连接池数量
        minIdle: 1
        timeBetweenEvictionRunsMillis: 60000
        # 连接保持空闲而不被驱逐的最长时间
        minEvictableIdleTimeMillis: 300000
        # 用来检测连接是否有效的sql，要求是一个查询语句
        # 如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会其作用
        validationQuery: select count(1) from 'table'
        # 申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效
        testWhileIdle: true
        # 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
        testOnBorrow: false
        # 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
        testOnReturn: false
        # 是否缓存preparedStatement，即PSCache
        poolPreparedStatements: false
        # 要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true
        maxOpenPreparedStatements: -1
```
##### 配置pagehelper分页插件
```
# pagehelper分页插件
pagehelper:
    # 数据库的方言
    helperDialect: mysql
    # 启用合理化，如果pageNum < 1会查询第一页，如果pageNum > pages会查询最后一页
    reasonable: true
```
##### 代码逆向生成插件mybatis-generator的配置及运行
mybatis-generator插件的使用主要分为以下三步
>1. pom.xml中添加mybatis-generator插件
```
    <build>
        <plugins>
            <!-- 将Spring Boot应用打包为可执行的jar或war文件 -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <!-- mybatis generator 自动生成代码插件 -->
            <plugin>
                <groupId>org.mybatis.generator</groupId>
                <artifactId>mybatis-generator-maven-plugin</artifactId>
                <version>1.3.2</version>
                <configuration>
                    <!-- 扫描resources/generator目录下的generatorConfig.xml配置 -->
                    <configurationFile>
                        ${basedir}/src/main/resources/generator/generatorConfig.xml
                    </configurationFile>
                    <overwrite>true</overwrite>
                    <verbose>true</verbose>
                </configuration>
            </plugin>
        </plugins>
    </build>
```
>2.创建逆向代码生成配置文件generatorConfig.xml

参照pom.xml插件配置中的扫描位置，在resources目录下创建generator文件夹，在新建的文件夹中创建generatorConfig.xml配置文件，文件的详细配置信息如下
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
        PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
<generatorConfiguration>
    <!-- 运行方式:mvaen运行命令 mybatis-generator:generate -e -->
    <!-- 数据库驱动:选择你的本地硬盘上面的数据库驱动包-->
    <properties resource="generator/generator.properties"/>
    <classPathEntry location="${classPathEntry}"/>
    <context id="DB2Tables" targetRuntime="MyBatis3">
        <!--数据库链接URL，用户名、密码 -->
        <jdbcConnection
                driverClass="com.mysql.jdbc.Driver"
                connectionURL="jdbc:mysql://localhost:3306/${db}?characterEncoding=utf-8"
                userId="${userId}"
                password="${password}">
        </jdbcConnection>
        <javaTypeResolver>
            <property name="forceBigDecimals" value="false"/>
        </javaTypeResolver>
        <javaModelGenerator targetPackage="${pojoTargetPackage}" targetProject="src/main/java">
            <property name="enableSubPackages" value="true"/>
            <property name="trimStrings" value="true"/>
        </javaModelGenerator>
        <!-- 生成映射文件的包名和位置-->
        <sqlMapGenerator targetPackage="${mapperTargetPackage}" targetProject="src/main/resources">
            <property name="enableSubPackages" value="true"/>
        </sqlMapGenerator>
        <!-- 生成DAO的包名和位置-->
        <javaClientGenerator type="XMLMAPPER" targetPackage="${daoTargetPackage}" targetProject="src/main/java">
            <property name="enableSubPackages" value="true"/>
        </javaClientGenerator>
        <!-- 要生成的表 tableName是数据库中的表名或视图名 schema是数据库名称-->
        <table tableName="%" schema="${db}"/>
    </context>
</generatorConfiguration>
```
为了将generatorConfig.xml配置模板化，在这里将变动性较大的配置项单独提取出来作为一个generatorConfig.xml的配置文件，然后通过properties标签读取此文件的配置，这样做的好处是当需要多处复用此xml时只需要关注少量的配置项。
在generatorConfig.xml同级创建generator.properties文件，现只需要配置generator.properties文件即可，配置内容如下
```
# 请手动配置以下选项
# 数据库驱动:选择你的本地硬盘上面的数据库驱动包
classPathEntry = D:/CJH/maven-repository/mysql/mysql-connector-java/5.1.30/mysql-connector-java-5.1.30.jar
# 数据库名称、用户名、密码
db = db
userId = root
password = 123
# 生成pojo的包名位置 在src/main/java目录下
pojoTargetPackage = com.spring.demo.springbootexample.mybatis.po
# 生成DAO的包名位置 在src/main/java目录下
daoTargetPackage = com.spring.demo.springbootexample.mybatis.mapper
# 生成Mapper的包名位置 位于src/main/resources目录下
mapperTargetPackage = mapper
```
>3. 运行mybatis-generator插件生成Dao、Model、Mapping
```
# 打开命令行cd到项目pom.xml同级目录运行以下命令
mvn mybatis-generator:generate -e
```
##### mybatis扫描包配置
至此已经生成了指定数据库对应的实体、映射类，但是还不能直接使用，需要配置mybatis扫描地址后才能正常调用
>1. 在application.yml配置mapper.xml以及pojo的包地址
```
mybatis:
    # mapper.xml包地址
    mapper-locations: classpath:mapper/*.xml
    # pojo生成包地址
    type-aliases-package: com.spring.demo.springbootexample.mybatis.po
```
>2. 在SpringBootExampleApplication.java中开启Mapper扫描注解
```
@SpringBootApplication
@MapperScan("com.spring.demo.springbootexample.mybatis.mapper")
public class SpringBootExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootExampleApplication.class, args);
	}
}
```
##### 测试mapper的有效性
```
@Controller
public class TestController {
    //替换成自己生成的mapper
    @Autowired
    UserMapper userMapper;

    @RequestMapping("/test")
    @ResponseBody
    public Object test(){
        //查询该表的所有数据
        return userMapper.selectByExample(null);
    }
}
```
启动SpringBootExampleApplication.java的main函数，如果没有在application.yml特意配置server.port那么springboot会采用默认的8080端口运行，运行成功将打印如下日志
```
Tomcat started on port(s): 8080 (http) with context path ''
```
在浏览器输入地址如果返回表格的中的所有数据代表mybatis集成成功
```
http://localhost:8080/test
```
### 集成Swagger2
Swagger2是一个文档快速构建工具，能够通过注解自动生成一个Restful风格json形式的接口文档，并可以通过如swagger-ui等工具生成html网页形式的接口文档，swagger2的集成比较简单，使用需要稍微熟悉一下，集成、注解与使用分如下四步
>1. 建立SwaggerConfig文件
```
@Configuration
public class SwaggerConfig {
	// 接口版本号
	private final String version = "1.0";
	// 接口大标题
	private final String title = "SpringBoot示例工程";
	// 具体的描述
	private final String description = "API文档自动生成示例";
	// 服务说明url
	private final String termsOfServiceUrl = "http://www.kingeid.com";
	// licence
	private final String license = "MIT";
	// licnce url
	private final String licenseUrl = "https://mit-license.org/";
	// 接口作者联系方式
	private final Contact contact = new Contact("calebman", "https://github.com/calebman", "chenjianhui0428@gmail.com");

	@Bean
	public Docket buildDocket() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(buildApiInf())
				.select().build();
	}

	private ApiInfo buildApiInf() {
		return new ApiInfoBuilder().title(title).termsOfServiceUrl(termsOfServiceUrl).description(description)
				.version(version).license(license).licenseUrl(licenseUrl).contact(contact).build();

	}

}
```
>2. 在SpringBootExampleApplication.java中启用Swagger2注解

在@SpringBootApplication注解下面加上@EnableSwagger2注解
>3. 常用注解示例
```
//Contorller中的注解示例
@Controller
@RequestMapping("/v1/product")
// 表示标识这个类是swagger的资源 
@Api(value = "DocController", tags = {"restful api示例"})
public class DocController extends BaseController {

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    //表示一个http请求的操作
    @ApiOperation(value = "修改指定产品", httpMethod = "PUT", produces = "application/json")
    //@ApiImplicitParams用于方法，包含多个@ApiImplicitParam表示单独的请求参数 
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "产品ID", required = true, paramType = "path")})
    public WebResult update(@PathVariable("id") Integer id, @ModelAttribute Product product) {
        logger.debug("修改指定产品接收产品id与产品信息=>%d,{}", id, product);
        if (id == null || "".equals(id)) {
            logger.debug("产品id不能为空");
            return WebResult.error(ERRORDetail.RC_0101001);
        }
        return WebResult.success();
    }
}
//Model中的注解示例
//表示对类进行说明，用于参数用实体类接收 
@ApiModel(value = "产品信息")
public class Product {
    //表示对model属性的说明或者数据操作更改 
	@ApiModelProperty(required = true, name = "name", value = "产品名称", dataType = "query")
	private String name;
	@ApiModelProperty(name = "type", value = "产品类型", dataType = "query")
	private String type;
}
```
>4. 生成json形式的文档

集成成功后启动项目控制台会打印级别为INFO的日志，截取部分如下，表明可通过访问应用的v2/api-docs接口得到文档api的json格式数据，可在浏览器输入指定地址验证集成是否成功
```
 Mapped "{[/v2/api-docs],methods=[GET],produces=[application/json || application/hal+json]}" 
 http://localhost:8080/v2/api-docs
```
### 多环境配置
应用研发过程中多环境是不可避免的，假设我们现在有开发、演示、生产三个不同的环境其配置也不同，如果每次都在打包环节来进行配置难免出错，SpringBoot支持通过命令启动不同的环境，但是配置文件需要满足application-{profile}.properties的格式，profile代表对应环境的标识，加载时可通过不同命令加载不同环境。
```
application-dev.properties：开发环境
application-test.properties：演示环境
application-prod.properties：生产环境
# 运行演示环境命令
java -jar spring-boot-example-0.0.1-SNAPSHOT --spring.profiles.active=test
```
基于现在的项目实现多环境我们需要在application.yml同级目录新建application-dev.yml、application-test.yml、application-prod.yml三个不同环境的配置文件，将不变的公有配置如druid的大部分、pagehelper分页插件以及mybatis包扫描配置放置于application.yml中，并在application.yml中配置默认采用开发环境，那么如果不带--spring.profiles.active启动应用就默认为开发环境启动，变动较大的配置如数据库的账号密码分别写入不同环境的配置文件中
```
spring:
    profiles:
      # 默认使用开发环境
      active: dev
```
配置到这里我们的项目目录结构如下图所示
![src/main/java目录结构](http://upload-images.jianshu.io/upload_images/10936059-2acd0bf47c5070c0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![src/main/resources目录结构](http://upload-images.jianshu.io/upload_images/10936059-bce0976473692a93.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

至此我们分别完成了Mybatis、Swagger2以及多环境的集成，接下来我们配置多环境下的logger。对于logger我们总是希望在项目研发过程中越多越好，能够给予足够的信息定位bug，项目处于演示或者上线状态时为了不让日志打印影响程序性能我们只需要警告或者错误的日志，并且需要写入文件，那么接下来就基于logback实现多环境下的日志配置
### 多环境下的日志配置
创建logback-spring.xml在application.yml的同级目录，springboot推荐使用logback-spring.xml而不是logback.xml文件，logback-spring.xml的配置内容如下所示
```
<?xml version="1.0" encoding="UTF-8"?>
<configuration scan="true" scanPeriod="60 seconds" debug="false">
    <!--
        简要描述
        日志格式 => %d{HH:mm:ss.SSS}(时间) [%-5level](日志级别) %logger{36}(logger名字最长36个字符，否则按照句点分割) - %msg%n(具体日志信息并且换行)

        开发环境 => ${basepackage}包下控制台打印DEBUG级别及以上、其他包控制台打印INFO级别及以上
        演示（测试）环境 => ${basepackage}包下控制台打印INFO级别及以上、其他包控制台以及文件打印WARN级别及以上
        生产环境 => 控制台以及文件打印ERROR级别及以上

        日志文件生成规则如下：
        文件生成目录 => ${logdir}
        当日的log文件名称 => ${appname}.log
        其他时候的log文件名称 => ${appname}.%d{yyyy-MM-dd}.log
        日志文件最大 => ${maxsize}
        最多保留 => ${maxdays}天
    -->
    <!--自定义参数 -->
    <!--用来指定日志文件的上限大小，那么到了这个值，就会删除旧的日志-->
    <property name="maxsize" value="30MB" />
    <!--只保留最近90天的日志-->
    <property name="maxdays" value="90" />
    <!--application.yml 传递参数 -->
    <!--log文件生成目录-->
    <springProperty scope="context" name="logdir" source="resources.logdir"/>
    <!--应用名称-->
    <springProperty scope="context" name="appname" source="resources.appname"/>
    <!--项目基础包-->
    <springProperty scope="context" name="basepackage" source="resources.basepackage"/>

    <!--输出到控制台 ConsoleAppender-->
    <appender name="consoleLog" class="ch.qos.logback.core.ConsoleAppender">
        <!--展示格式 layout-->
        <layout class="ch.qos.logback.classic.PatternLayout">
            <pattern>
                <pattern>%d{HH:mm:ss.SSS} [%-5level] %logger{36} - %msg%n</pattern>
            </pattern>
        </layout>
    </appender>
    <!--输出到文件 FileAppender-->
    <appender name="fileLog" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!--
            日志名称，如果没有File 属性，那么只会使用FileNamePattern的文件路径规则
            如果同时有<File>和<FileNamePattern>，那么当天日志是<File>，明天会自动把今天
            的日志改名为今天的日期。即，<File> 的日志都是当天的。
        -->
        <File>${logdir}/${appname}.log</File>
        <!--滚动策略，按照时间滚动 TimeBasedRollingPolicy-->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!--文件路径,定义了日志的切分方式——把每一天的日志归档到一个文件中,以防止日志填满整个磁盘空间-->
            <FileNamePattern>${logdir}/${appname}.%d{yyyy-MM-dd}.log</FileNamePattern>
            <maxHistory>${maxdays}</maxHistory>
            <totalSizeCap>${maxsize}</totalSizeCap>
        </rollingPolicy>
        <!--日志输出编码格式化-->
        <encoder>
            <charset>UTF-8</charset>
            <pattern>%d{HH:mm:ss.SSS} [%-5level] %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 开发环境-->
    <springProfile name="dev">
        <root level="INFO">
            <appender-ref ref="consoleLog"/>
        </root>
        <!--
            additivity是子Logger 是否继承 父Logger 的 输出源（appender） 的标志位
            在这里additivity配置为false代表如果${basepackage}中有INFO级别日志则子looger打印 root不打印
        -->
        <logger name="${basepackage}" level="DEBUG" additivity="false">
            <appender-ref ref="consoleLog"/>
        </logger>
    </springProfile>

    <!-- 演示（测试）环境-->
    <springProfile name="test">
        <root level="WARN">
            <appender-ref ref="consoleLog"/>
            <appender-ref ref="fileLog"/>
        </root>
        <logger name="${basepackage}" level="INFO" additivity="false">
            <appender-ref ref="consoleLog"/>
            <appender-ref ref="fileLog"/>
        </logger>
    </springProfile>

    <!-- 生产环境 -->
    <springProfile name="prod">
        <root level="ERROR">
            <appender-ref ref="consoleLog"/>
            <appender-ref ref="fileLog"/>
        </root>
    </springProfile>
</configuration>
```
日志配置中引用了application.yml的配置信息，主要有logdir、appname、basepackage三项，logdir是日志文件的写入地址，可以传入相对路径，appname是应用名称，引入这项是为了通过日志文件名称区分是哪个应该输出的，basepackage是包过滤配置，比如开发环境中需要打印debug级别以上的日志，但是又想使除我写的logger之外的DEBUG不打印，可过滤到本项目的包名才用DEBUG打印，此外包名使用INFO级别打印，在application.yml中新建这三项配置，也可在不同环境配置不同属性
```
#应用配置
resources:
    # log文件写入地址
    logdir: logs/
    # 应用名称
    appname: spring-boot-example
    # 日志打印的基础扫描包
    basepackage: com.spring.demo.springbootexample
```
使用不同环境启动测试logger配置是否生效，在开发环境下将打印DEBUG级别以上的四条logger记录，在演示环境下降打印INFO级别以上的三条记录并写入文件，在生产环境下只打印ERROR级别以上的一条记录并写入文件
```
    @RequestMapping("/logger")
    @ResponseBody
    public WebResult logger() {
        logger.trace("日志输出 {}", "trace");
        logger.debug("日志输出 {}", "debug");
        logger.info("日志输出 {}", "info");
        logger.warn("日志输出 {}", "warn");
        logger.error("日志输出 {}", "error");
        return "00";
    }
```  
### jwt配置  

pom文件添加jwt与spring security依赖

```
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt</artifactId>
			<version>0.7.0</version>
		</dependency>		 
```  

自定义配置文件中添加jwt超时时间，token生成私钥

在/jwt/JsonWebTokenUtility中定义生成token置于response头的方法以及解析token校验的方法  
```  


#jwt配置
jwt:
    EXPIRATIONTIME: 1000
    SECRET: OHAHAHAHA

```  
自定义配置统一在config/Config中配置get方法  
  
  spring security配置某些url不被控制，例如 /testJwt  /testJwtdecrypt
  ```
      @Override
      protected void configure(HttpSecurity http) throws Exception {
          http
                  .authorizeRequests()
                  .antMatchers("/testJwt","/testJwtdecrypt").permitAll()
                  .anyRequest().authenticated()
                  .and()
                  .formLogin()
                  .loginPage("/login")
                  .permitAll()
                  .and()
                  .logout()
                  .permitAll();
      }
  
      @Autowired
      public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
          auth
                  .inMemoryAuthentication()
                  .withUser("user").password("password").roles("USER");
      }
  ```

### 常用配置
##### 加载自定义配置
```
@Component
@PropertySource(value = {"classpath:application.yml"}, encoding = "utf-8")
public class Config {

    @Value("${resources.midpHost}")
    private String midpHost;

    public String getMidpHost() {
        return midpHost;
    }
}
```
##### 全局异常处理器
```
@ControllerAdvice
public class GlobalExceptionResolver {

    Logger logger = LoggerFactory.getLogger(GlobalExceptionResolver.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public WebResult exceptionHandle(HttpServletRequest req, Exception ex) {
        ex.printStackTrace();
        logger.error("未知异常", ex);
        return WebResult.error(ERRORDetail.RC_0401001);
    }
}
```
### 示例工程开源地址
[github](https://github.com/calebman/spring-boot-example)