package com.config.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class NettyServerListener  implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerListener.class);

    /**
     * 当一个applicationContext被初始化或被刷新时触发
     * @param event
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(event.getApplicationContext().getParent() == null){
            logger.info("NettyServer Start Success");
            //自己的NettyServer
            NettyServerBootstrap serverBootstrap = new NettyServerBootstrap(10020);
            new Thread(serverBootstrap).start();
        }
    }
}