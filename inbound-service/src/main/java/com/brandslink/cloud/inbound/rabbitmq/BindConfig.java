package com.brandslink.cloud.inbound.rabbitmq;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列路由交换机绑定配置
 * */
@Configuration
public class BindConfig {

    @Bean("InboundStatisticsBinding")
    Binding InboundStatisticsBinding(@Qualifier(RabbitConfig.InboundStatistics_QUEUE) Queue queueA, @Qualifier(RabbitConfig.InboundStatistics_EXCHANGE) TopicExchange exchange1) {
        return BindingBuilder.bind(queueA).to(exchange1).with(RabbitConfig.InboundStatistics_ROUTINGKEY);
    }

}
