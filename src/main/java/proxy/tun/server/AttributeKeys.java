package proxy.tun.server;

import io.netty.util.AttributeKey;

public class AttributeKeys {
    //When a client connects,tool.RandomStringGenerator will generate a 32 bytes key for AES CBC encrypting
    public static final AttributeKey<byte[]> KEY = AttributeKey.valueOf("key");
}
