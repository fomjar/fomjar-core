package com.fomjar.dist;

import com.fomjar.lang.Struct;

import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

/**
 * 分布式工具集。
 *
 * @author fomjar
 */
public interface Dist {

    /**
     * 当前JVM进程的PID。
     *
     * @return 当前进程ID
     */
    static long pid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.substring(0, name.indexOf("@"));
        return Long.parseLong(pid);
    }

    /**
     * 统一进程ID。结合了 MAC地址、进程号 的唯一编号，用于在分布式场景下区分主机、进程。
     *
     * @return 统一进程编号
     */
    static String upid() {
        StringBuilder sb = new StringBuilder();
        byte[] mac = new byte[] {0, 0, 0, 0, 0, 0};
        try { mac = Struct.call(NetworkInterface.class, NetworkInterface.class, "getDefault").getHardwareAddress(); }
        catch (SocketException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
        for (byte b : mac) sb.append(Integer.toHexString(b));

        return sb.toString()
                + "-"
                + String.format("%08x", Dist.pid());
    }

    /**
     * 统一线程ID。结合了 MAC地址、进程号、线程号 的唯一编号，用于在分布式场景下区分主机、进程、线程。
     *
     * @return 统一线程编号
     */
    static String utid() {
        return Dist.upid()
                + "-"
                + String.format("%08x", Thread.currentThread().getId());
    }

    /**
     * 选举。多实例同时竞选唯一的主实例，一般的运用场景包括：多实例部署但只需启动单个定时器、单个数据同步器、或者单个数据流处理进程等等。<br>
     *     随着分布式系统的环境变化，在任意时间当选或落选时均会回调选举接口<br>
     *     注意：落选的回调必须在第一次当选成功之后，即，如果自选举开始就从未当选成功过则不会回调落选
     *
     * @param topic 选举事项
     * @param election 选举回调
     */
    void elect(String topic, Election election);

    /**
     * 是否当选。
     *
     * @param topic 选举事项
     * @return true为当选，false为落选
     */
    boolean isElected(String topic);

    /**
     * 弃权。退出选举。
     * @param topic 弃权事项
     */
    void abstain(String topic);


    /**
     * 解锁。注意：此方法会解其他线程/进程加的锁。
     *
     * @param name 锁名
     */
    void unlock(String name);

            boolean lock(String name, long wait, long hold, TimeUnit unit);
    default void    lock(String name,            long hold, TimeUnit unit) {        this.lock(name, Integer.MAX_VALUE, hold, unit); }
    default boolean lock(String name, long wait, long hold               ) { return this.lock(name, wait,              hold, TimeUnit.MILLISECONDS); }
    default void    lock(String name,            long hold               ) {        this.lock(name, Integer.MAX_VALUE, hold, TimeUnit.MILLISECONDS); }

    default boolean lock(Runnable task, String name, long wait, long hold, TimeUnit unit) {
        boolean locked = false;
        try {
            if (locked = this.lock(name, wait, hold, unit))
                if (null != task)
                    task.run();
        } finally {
            if (locked)
                this.unlock(name);
        }
        return locked;
    }
    default void    lock(Runnable task, String name,            long hold, TimeUnit unit) {        this.lock(task, name, Integer.MAX_VALUE, hold, unit); }
    default boolean lock(Runnable task, String name, long wait, long hold               ) { return this.lock(task, name, wait,              hold, TimeUnit.MILLISECONDS); }
    default void    lock(Runnable task, String name,            long hold               ) {        this.lock(task, name, Integer.MAX_VALUE, hold, TimeUnit.MILLISECONDS); }

}
