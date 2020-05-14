# fomjar-core

常用中间件与工具库合集，每类工具库都包含了统一的抽象封装。

## 目录

- *algo*: 算法包。md2、md5、sha1、sha256、sha384、sha512、crc32
- *anno*: 注解包。注解扫描，对Spring环境下地自定义注解地初始化处理很有帮助
- *async*: 异步包。异步执行、队列化执行等。在Spring环境下会进行Bean的自动配置
- *data*: 数据结构包。对数据结构的特殊处理。对字段和方法的方便的反射操作，修改final常量值，类的自定义扫描，集合的方便地链式调用等
- *dist*: 分布式包。各类分布式中间件的统一封装。在Spring环境下会进行Bean的自动配置。分布式锁、分布式调度
- *el*: 表达式包。各类模板引擎和表达式引擎的统一封装。也可以用来做Web端推送。在Spring环境下会进行Bean的自动配置。支持注册自定义变量、自定义方法
- *lio*: 长连接包。各类网络协议的长连接的统一封装。在Spring环境下提供了基于注解的LIO容器自动配置。Websocket、Socket-IO、TCP、Redis
- *mq*: 消息队列包。各类消息中间件的统一封装。在Spring环境下会进行Bean的自动配置。AliyunMQ、RedisMQ。
- *oss*: 对象存储包。各类对象存储中间件的统一封装。在Spring环境下会进行Bean的自动配置。AliyunOSS、MinioOSS
- *pio*: 进程IO包。操作系统进程的IO异步读写工具
- *spring*: Spring支持包。框架的Spring启动器自动配置以及Spring相关工具集。动态获取Bean、动态获取配置项等

## DEMO

### 算法-algo
```java
System.out.println(Digest.md5("password"));
```

### 注解-anno
```java
Anno.scan("com.fomjar.core", new AnnoScanAdapter() {
    @Override
    public void read(Annotation[] annotations, Class<?> clazz) {
        if (0 < annotations.length)
            System.out.println(clazz);
        for (Annotation annotation : annotations) {
            System.out.println("[CLASS  ANNO] " + annotation.annotationType());
        }
    }
}
```
这是一个扫描Controller接口清单的例子：
```java
Anno.scan(new URLClassLoader(new URL[]{
                new File("/Users/fomjar/Documents/code/fomjar-core/target/classes").toURI().toURL(),
        }),
        "com.fomjar.core",
        null,
        AnnoScanFilter.any(Controller.class, RestController.class),
        new AnnoScanAdapter() {
            @Override
            public void read(Annotation[] annotations, Class<?> type, Method method) {
                Annotation anno0 = Anno.any(type.getAnnotations(), RequestMapping.class, GetMapping.class, PostMapping.class);
                Annotation anno1 = Anno.any(method.getAnnotations(), RequestMapping.class, GetMapping.class, PostMapping.class);
                if (null == anno1) return;

                String[] path0 = new String[0];
                String[] path1 = new String[0];
                try {
                    path0 = null == anno0 ? new String[] {} : Struct.call(anno0, String[].class, "value");
                    path1 = Struct.call(anno1, String[].class, "value");
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }

                if (0 == path1.length) return;

                for (String p0 : path0) {
                    for (String p1 : path1) {
                        String path = "/" + p0 + "/" + p1;
                        while (path.contains("//"))
                            path = path.replace("//", "/");
                        while (path.endsWith("/"))
                            path = path.substring(0, path.length() - 1);

                        System.out.println(path);
                    }
                }
            }
        });
```

### 异步-async
```java
@Autowired
private ExecutorService queue;

for (int i = 0; i < 8; i++) {
    final int n = i;
    this.queue.submit(() -> {
        try {Thread.sleep(100L);}
        catch (InterruptedException e) {e.printStackTrace();}
        System.out.println(Thread.currentThread().getName() + ": " + n);
    });
}
this.queue.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
```

### 数据结构-data
集合操作：
```java
Struct.wrapList().add(3).add(4).add(5).get().size(); // 3
```
数据结构访问：
```java
System.out.println(Struct.get(new LinkedList<>(), "size")); // 0
System.out.println(Struct.call("123456", "substring", 1, 4)); // "234"
```
常量修改：
```java
Struct.setFinalDouble(Double.class, "MAX_VALUE", 1.1D);
System.out.println(Double.MAX_VALUE); // 1.1

String s = "12345";
Struct.setFinalObject(s, "value", new char[] {'a', 'b', 'c', 'd', 'e'}); // "abcde"
```

### 分布式-dist
分布式调度：
```java
@Autowired
private Dist dist;

String timerName = "loop-a-123";
this.dist.revoke("loop-a-"); // 旧任务清理，确保服务在多实例部署的情况下只有一个任务在运行
this.dist.loop((Runnable & Serializable) () ->
                System.out.println(Thread.currentThread().getName() + "-loop-a-" + System.currentTimeMillis()),
        timerName, 100, 1000, TimeUnit.MILLISECONDS); // 发布定时任务
```
分布式锁：
```java
@Autowired
private Dist dist;

String name = "123";
this.dist.lock(() -> {
    System.out.println(Thread.currentThread().getName() + ": " + System.currentTimeMillis());
    try { Thread.sleep(100L); }
    catch (InterruptedException e) { e.printStackTrace(); }
}, name);
```

### 表达式-el
```java
@Autowired
private EL el;
    
System.out.println(this.el.eval("now('yyyy/MM/dd HH/mm/ss.SSS')"));
System.out.println(this.el.eval("randomBoolean()"));
System.out.println(this.el.eval("randomInt()"));
System.out.println(this.el.eval("randomLong()"));
System.out.println(this.el.eval("randomFloat()"));
System.out.println(this.el.eval("randomDouble()"));
System.out.println(this.el.eval("length('abcde')")); // "5"
System.out.println(this.el.eval("indexOf('abcde', 'cd')")); // "2"
System.out.println(this.el.eval("lastIndexOf('abcde', 'cd')")); // "2"
System.out.println(this.el.eval("trim('  abcde')")); // "abcde"
System.out.println(this.el.eval("reverse('abcde')")); // "edcba"
System.out.println(this.el.eval("substring('abcde', 1, 3)")); // "bc"
System.out.println(this.el.eval("substring('abcde', 3)")); // "de"
System.out.println(this.el.eval("replace('abcde', 'c', 'm')")); // "abmde"
System.out.println(this.el.eval("split('abcde', 'c')[0]")); // "ab"
System.out.println(this.el.eval("if(1 > 0, 'g', 'l')")); // "g"
System.out.println(this.el.eval("ifblank('', 'abcd')")); // "abcd"
System.out.println(this.el.eval("ifblank('1234', 'abcd')")); // "1234"
System.out.println(this.el.eval("Math.PI")); // "3.141592653589793"
System.out.println(this.el.eval("Math.E")); // "2.718281828459045"
System.out.println(this.el.eval("Math.abs(-15)")); // "15"
System.out.println(this.el.eval("Math.sqrt(2)")); // "1.4142135623730951"
System.out.println(this.el.eval("'123' + 'abc'")); // "123abc"
```

### 长连接-lio
```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestFomjarCoreApplication.class})
// 控制器定义
@LIOController
public class TestLIOSpring {
    // 请求数据
    @LIORequest
    public String accept(JSONObject json) {
        System.out.println("server received: " + json.toString());
        return json.toString();
    }
    // 连接事件
    @LIOConnect
    public void connect(LIO lio) {
        System.out.println(String.format("Client %s:%d connect", lio.remoteHost(), lio.remotePort()));
    }
    // 断连事件
    @LIODisconnect
    public void disconnect(LIO lio) {
        System.out.println(String.format("Client %s:%d disconnect", lio.remoteHost(), lio.remotePort()));
    }
    // 创建客户端发起请求
    @Test
    public void test() throws IOException, InterruptedException, URISyntaxException {
//        LIO lio = new TCPLIO(new Socket("127.0.0.1", 9001));
        LIO lio = new WebSocketLIO(new URI("ws://127.0.0.1:" + 9001 + "/hello?a=1&b=2"));
        while (!lio.isOpen()) {
            Thread.sleep(100L);
        }
        for (int i = 0; i < 3; i++) {
            lio.write("{\"hello\" : \"world!\"}");
            Thread.sleep(200L);
        }
        lio.close();
    }
}
```

### 消息队列-mq
```java
@Autowired
private MQ mq;

// 消费
this.mq.consume("test", msg -> {
    System.out.println(msg.toString());
});
// 生产
for (int i = 0; i < 3; i++) {
    this.mq.produce(new MQMsg().tag("test").data("Hello world!"));
    Thread.sleep(200L);
}
```

### 对象存储-oss
```java
@Autowired
private OSS oss;

long time = System.currentTimeMillis();
System.out.println(oss.upload("test-" + time + ".txt", ("hello world! " + time).getBytes())); // http://url
```

### 进程IO-pio
```java
new PIO()
    .readInput(new PIOLineReader() {
        @Override
        public void readLine(String line) throws Exception {
            logger.info("[OUT] {}", line);
        }
    })
    .readError(new PIOLineReader() {
        @Override
        public void readLine(String line) throws Exception {
            logger.info("[ERR] {}", line);
        }
    })
    .startup("java -h")
    .await();
```
