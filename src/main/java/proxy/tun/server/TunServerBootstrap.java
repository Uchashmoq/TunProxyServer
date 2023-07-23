package proxy.tun.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class TunServerBootstrap extends ServerBootstrap{
    public static  String configPath="C:\\Users\\13637\\Desktop\\tunserver\\config.properties";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static String serverAddr;
    public static int port;
    public static byte[] iv;

    public TunServerBootstrap (String configPath){
        TunServerBootstrap.configPath=configPath;
        loadConfig();
    }
    private static void loadConfig(){
        Properties p=new Properties();
        try (FileInputStream fis = new FileInputStream(configPath)){
            p.load(fis);
            serverAddr=p.getProperty("serverAddr");
            if (serverAddr==null) throw new Exception("\"serverAddr\" not found");
            String port0 = p.getProperty("port");
            if (port0==null) throw new Exception("\"port\" not found");
            port=Integer.parseInt(port0);
            String ivs=p.getProperty("staticKey");
            if (ivs==null) throw new Exception("\"iv\" not found");
            iv=ivs.getBytes(StandardCharsets.UTF_8);
        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void launch(){
        group(new NioEventLoopGroup(), new NioEventLoopGroup());
        channel(NioServerSocketChannel.class);
        childHandler(new TunServerChannelInitializer());
        bind(new InetSocketAddress(serverAddr,port));
        logger.info(String.format("listening %s:%d",serverAddr,port));
    }
}
