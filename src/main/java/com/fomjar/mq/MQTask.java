package com.fomjar.mq;

/**
 * @author fomjar
 */
public interface MQTask {

    /**
     * 消费处理MQ消息。
     *
     * @param msg 待消费的消息
     */
    void consume(MQMsg msg);

}
