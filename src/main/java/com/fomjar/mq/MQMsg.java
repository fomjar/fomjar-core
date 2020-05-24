package com.fomjar.mq;


import com.alibaba.fastjson.JSONObject;

import java.util.UUID;

/**
 * @author fomjar
 */
public class MQMsg {

    public static MQMsg fromString(String string) {
        JSONObject json = JSONObject.parseObject(string);
        MQMsg msg = new MQMsg();
        msg
                .id(json.getString("id"))
                .time(json.getLong("time"))
                .tag(json.getString("tag"))
                .transaction(json.getString("transaction"))
                .data(json.getString("data").getBytes());
        return msg;
    }

    private String  id          = UUID.randomUUID().toString().replace("-", "");
    private long    time        = System.currentTimeMillis();
    private String  tag;
    private String  transaction;
    private byte[]  data;

    public String       id()            {return this.id;}
    public long         time()          {return this.time;}
    public String       tag()           {return this.tag;}
    public String       transaction()   {return this.transaction;}
    public byte[]       data()          {return this.data;}
    public JSONObject   data2json()     {return null == this.data() ? null : JSONObject.parseObject(this.data2string());}
    public String       data2string()   {return null == this.data() ? null : new String(this.data());}

    private MQMsg id(String id) {
        this.id = id;
        return this;
    }
    private MQMsg time(long time) {
        this.time = time;
        return this;
    }
    public MQMsg tag(String tag) {
        this.tag = tag;
        return this;
    }
    MQMsg transaction(String transaction) {
        this.transaction = transaction;
        return this;
    }
    public MQMsg data(byte[] data) {
        this.data = data;
        return this;
    }
    public MQMsg data(String data) {
        this.data = null == data ? null : data.getBytes();
        return this;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        json.put("id",          this.id);
        json.put("time",        this.time);
        json.put("tag",         this.tag);
        json.put("transaction", this.transaction);
        json.put("data",        this.data2string());
        return json.toString();
    }

}