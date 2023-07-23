# TunProxyServer

一个简单的采用aes加密的socks5代理
![客户端仓库](https://github.com/Uchashmoq/TunProxyClient)
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

下载好依赖后，**将config.properties的路径作为第一个运行实参**，运行主类。或前往release使用启动脚本运行（使用前确保配置了正确的服务器IP）

#### 运行原理

1. 客户端监听本机某个端口，等待浏览器（或代理软件）连接

2. 浏览器发出代理请求并连接到客户端后，客户端会开启一个协程，连接到服务端，初次连接时服务端会生成一个32字节的密钥用于aes加密并发回客户端，以后客户端和服务器通信都用它加密。

3. ``` 
   协议：
   标识位两字节{0x89,0x64} 数据长度4字节 数据
   数据部分采用aes加密
   ```

4. 与服务端协商后，服务端开始连接到目的服务器转发数据
