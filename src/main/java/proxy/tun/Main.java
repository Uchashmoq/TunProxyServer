package proxy.tun;

import proxy.tun.server.TunServerBootstrap;

public class Main {
    public static void main(String[] args) {
        if (args.length!=1){
            System.out.println("args[0]=configPath");
            System.exit(1);
        }
        TunServerBootstrap tunServerBootstrap = new TunServerBootstrap(args[0]);
        tunServerBootstrap.launch();
    }
}
