package com.fomjar.core.mq;

import java.util.*;

/**
 * MQ的抽象定义。
 *
 * @author fomjar
 */
public abstract class MQ {

    /** 主题 */
    private String                      topic;
    /** 集群组 */
    private String                      group;
    /** 非事务任务 <tag, tasks> */
    private Map<String, List<MQTask>>   tasks;
    /** 事务任务（指定消息响应）<transaction, task> */
    private Map<String, MQTask>         transactions;

    public MQ(String topic) {
        this.topic          = topic;
        this.group          = "GROUP-" + UUID.randomUUID().toString().replace("-", "");
        this.tasks          = new LinkedHashMap<>();
        this.transactions   = new LinkedHashMap<>();
    }

    public String topic() {return this.topic;}
    public String group() {return this.group;}

    /**
     * 设置所属集群组。消息面向集群组进行广播，一个消息只能被相同集群组内的一个实例所消费。
     *
     * @param group 集群分组，同一个模块请配置为同一个组
     * @return 此MQ实例
     */
    public MQ group(String group) {
        this.group = group;
        return this;
    }

    /**
     * 分布式锁。用于争取集群组内的消息取消费权。
     *
     * @param msg 待加锁的消息
     * @return true为成功，false为失败
     */
    protected abstract boolean lock(String msg);

    /**
     * 消费消息的具体逻辑，请子类在各自实现的消息消费回调接口中调用此方法来确保执行正确的消费逻辑。
     *
     * @param msg 待消费的消息
     */
    protected void doConsume(MQMsg msg) {
        // process transaction message first
        if (null != msg.transaction()) {
            if (this.transactions.containsKey(msg.transaction())) {
                try {this.transactions.remove(msg.transaction()).consume(msg);}
                catch (Exception e) {e.printStackTrace();}
            }
            return;
        }
        // process general message second
        try {
            if (this.lock(String.format("MESSAGE[%s]-consumed-by-GROUP[%s]", msg.id(), this.group()))) {
                String tag = msg.tag();
                if (this.tasks.containsKey(tag)) {
                    for (MQTask task : this.tasks.get(tag)) {
                        try {task.consume(msg);}
                        catch (Exception e) {e.printStackTrace();}
                    }
                }
            }
        } catch (Exception e) {e.printStackTrace();}
    }

    /**
     * 添加消费任务。同一个MQ消费者可以添加多个不同的tag和对应的消费任务，
     * 在消费消息时指定tag下的所有消费任务均会被执行。
     * <br>
     * 此处添加的消费任务不会处理事务<b>响应</b>消息。
     *
     * @param tag 消费任务对应的消息tag
     * @param task 消费任务
     * @return 此MQ对象
     */
    public MQ consume(String tag, MQTask task) {
        if (!this.tasks.containsKey(tag)) this.tasks.put(tag, new LinkedList<>());

        this.tasks.get(tag).add(task);
        return this;
    }

    /**
     * 生产一个消息。该消息是非事务性的，将会广播到所有集群组。
     *
     * @param msg 待生产的消息
     * @return 此MQ对象
     */
    public MQ produce(MQMsg msg) {
        return this.produce(msg, (MQTask) null);
    }

    /**
     * 生产一个消息。该消息是对指定请求消息的事务性回复，只有事务发起方才会收到此回复。
     *
     * @param req 原事务请求消息
     * @param rsp 需要回复的响应消息内容
     * @return 此MQ对象
     */
    public MQ produce(MQMsg req, MQMsg rsp) {
        rsp.transaction(req.transaction());
        return this.produce(rsp, (MQTask) null);
    }

    /**
     * 生产一个消息。传入task参数可以接受对方的事务性响应消息。
     *
     * @param msg 消息的内容
     * @param task 如果需要对应事务的响应，则需要传入响应消息回调处理方法，否则可以传空
     * @return 此MQ对象
     */
    public MQ produce(MQMsg msg, MQTask task) {
        if (null == msg.tag()) throw new IllegalArgumentException("No tag specified!");

        // mark transaction
        if (null != task) {
            if (null == msg.transaction())
                msg.transaction("transaction-" + msg.id());
            this.transactions.put(msg.transaction(), task);
        }

        return this.doProduce(msg);
    }

    /**
     * 实际的生产消息的方法由子类来实现。
     *
     * @param msg 待生产的消息
     * @return 此MQ对象
     */
    protected abstract MQ doProduce(MQMsg msg);

    /**
     * 关闭MQ客户端。
     */
    public abstract void shutdown();

}
