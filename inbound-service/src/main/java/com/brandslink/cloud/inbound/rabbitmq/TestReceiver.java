package com.brandslink.cloud.inbound.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

//@Component
public class TestReceiver {

    //监听队列queue-a
//    @RabbitListener(queues = "inboundStatisticsQueue")
    public void process(String message) {
        System.out.println("接收端Receiver  : " + message);
    }

}
