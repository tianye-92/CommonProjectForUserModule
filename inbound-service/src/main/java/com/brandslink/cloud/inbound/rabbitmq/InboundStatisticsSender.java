package com.brandslink.cloud.inbound.rabbitmq;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.brandslink.cloud.common.entity.InboundWorkloadInfo;


/**
 * MQ消息发送
 * */
@Component
public class InboundStatisticsSender {

    private final static Logger log = LoggerFactory.getLogger(InboundStatisticsSender.class);

//    @Qualifier("rabbitTemplateCommodity")
    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * 入库统计消息
     * @param info
     */
    public void InboundStatisticsSend(InboundWorkloadInfo info) {
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        log.debug("MQ消息开始发送==>{}{}", correlationId, info);
        rabbitTemplate.convertAndSend(RabbitConfig.InboundStatistics_EXCHANGE, RabbitConfig.InboundStatistics_ROUTINGKEY, JSONObject.toJSONString(info), correlationId);
    }

}
