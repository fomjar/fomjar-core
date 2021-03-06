package com.fomjar.mq;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.PropertyValueConst;
import com.aliyun.openservices.ons.api.order.OrderAction;
import com.aliyun.openservices.ons.api.order.OrderConsumer;
import com.aliyun.openservices.ons.api.order.OrderProducer;

import java.util.Properties;

/**
 * MQ的AliyunMQ封装。
 *
 * @author fomjar
 */
public class AliyunMQ extends MQ {

    /** 生产者id */
    private String          producerId;
    /** 消费者id */
    private String          consumerId;
    /** accessKey */
    private String          accessKey;
    /** secretKey */
    private String          secretKey;
    /** MQ TCP 协议接入点 */
    private String          onsaddr;
    /** 消费者对象 */
    private OrderConsumer   consumer;
    /** 生产者对象 */
    private OrderProducer   producer;
    /** MQ属性实体类 */
    private Properties      properties;

    public AliyunMQ(String topic) {super(topic);}

    /**
     * 初始化AliyunMQ配置。
     *
     * @param producerId 生产者ID
     * @param consumerId 消费者ID
     * @param accessKey AK
     * @param secretKey SK
     * @param onsAddr 服务地址
     * @return 此MQ对象
     */
    public AliyunMQ setup(
            String producerId,
            String consumerId,
            String accessKey,
            String secretKey,
            String onsAddr) {

        this.shutdown();

        this.producerId     = producerId;
        this.consumerId     = consumerId;
        this.accessKey      = accessKey;
        this.secretKey      = secretKey;
        this.onsaddr        = onsAddr;

        this.properties = new Properties();
        this.properties.setProperty(PropertyKeyConst.ConsumerId,    this.consumerId);
        this.properties.setProperty(PropertyKeyConst.ProducerId,    this.producerId);
        this.properties.setProperty(PropertyKeyConst.AccessKey,     this.accessKey);
        this.properties.setProperty(PropertyKeyConst.SecretKey,     this.secretKey);
        this.properties.setProperty(PropertyKeyConst.ONSAddr,       this.onsaddr);
        this.properties.setProperty(PropertyKeyConst.MessageModel,  PropertyValueConst.CLUSTERING);

        this.initProducer();
        this.initConsumer();

        return this;
    }

    private void initProducer() {
        this.producer = ONSFactory.createOrderProducer(this.properties);
        this.producer.start();
    }

    private void initConsumer() {
        this.consumer = ONSFactory.createOrderedConsumer(this.properties);
        this.consumer.start();

        // native task do not support multi-tasks with different tags
        // here we subscribe only one native task with tag "*"
        this.consumer.subscribe(this.topic(), "*", (message, context) -> {
            super.doConsume(MQMsg.fromString(new String(message.getBody())));
            return OrderAction.Success;
        });
    }

    @Override
    protected boolean lock(String msg) {
        return true;
    }

    @Override
    protected void doProduce(MQMsg msg) {
        this.producer.send(new Message(this.topic(), msg.tag(), msg.transaction(), msg.toString().getBytes()), getClass().getName());
    }

    @Override
    public void shutdown() {
        if (null != this.consumer) this.consumer.shutdown();
        if (null != this.producer) this.producer.shutdown();
        this.consumer = null;
        this.producer = null;
    }
}
