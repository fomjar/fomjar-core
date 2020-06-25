package com.fomjar.dist;

/**
 * 选举的回调接口。
 */
public interface Election {

    /**
     * 当选
     *
     * @param topic 事项
     */
    void elected(String topic);

    /**
     * 落选
     *
     * @param topic 事项
     */
    void lost(String topic);

}
