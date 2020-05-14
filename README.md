# fomjar-core

常用中间件与工具库合集，每类工具库都包含了统一的抽象封装。

## 目录

- algo: 算法包。md2、md5、sha1、sha256、sha384、sha512、crc32
- anno: 注解包。注解扫描，对Spring环境下地自定义注解地初始化处理很有帮助
- async: 异步包。异步执行、队列化执行等。在Spring环境下会进行Bean的自动配置
- data: 数据结构包。对数据结构的特殊处理。对字段和方法的方便的反射操作，修改final常量值，类的自定义扫描，集合的方便地链式调用等
- dist: 分布式包。各类分布式中间件的统一封装。在Spring环境下会进行Bean的自动配置。分布式锁、分布式调度
- el: 表达式包。各类模板引擎和表达式引擎的统一封装。在Spring环境下会进行Bean的自动配置。支持注册自定义变量、自定义方法
- lio: 长连接包。各类网络协议的长连接的统一封装。在Spring环境下提供了基于注解的LIO容器自动配置。Websocket、Socket-IO、TCP、Redis
- MQ: 消息队列包。各类消息中间件的统一封装。在Spring环境下会进行Bean的自动配置。AliyunMQ、RedisMQ。
- OSS: 对象存储包。各类对象存储中间件的统一封装。在Spring环境下会进行Bean的自动配置。AliyunOSS、MinioOSS
- pio: 进程IO包。操作系统进程的IO异步读写工具
- spring: Spring支持包。框架的Spring启动器自动配置以及Spring相关工具集。动态获取Bean、动态获取配置项等