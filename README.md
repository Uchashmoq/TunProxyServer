# TunProxyServer

一个简单的采用aes加密的socks5代理

#### 启动

程序入口`src\main\java\proxy\tun\Main.java` , 创建 `config.properties`,内容示例如下（注意大小写）

```properties
#服务器ip
serverAddr=127.0.0.1
#服务器端口
port=14445
#用于aes cbc模式加密的初始向量，16个字节，服务器和客户端上的staticKey应保持一致
staticKey=HrGZo2uaSgccL4Ke
```

下载好依赖后，**将config.properties的路径作为第一个运行实参**，运行主类

