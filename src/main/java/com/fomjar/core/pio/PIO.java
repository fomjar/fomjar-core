package com.fomjar.core.pio;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Process I/O 进程IO的通用实现。所有IO数据均通过进程标准IO来传递。
 *
 * @author fomjar
 */
public class PIO {

    private static final ExecutorService pool = Executors.newCachedThreadPool();

    private List<PIOReader> inputReaders;
    private List<PIOReader> errorReaders;

    private Process         process;
    private String[]        cmds;
    private Integer         pid;
    private PrintWriter     printer;

    private Map<String, Object> attach;

    public PIO() {
        super();
        this.inputReaders   = new LinkedList<>();
        this.errorReaders   = new LinkedList<>();
    }

    /**
     * 根据给定命令启动此PIO。相关进程启动，相关IO线程挂载。
     *
     * @param cmds
     * @return
     * @throws IOException
     */
    public PIO startup(String... cmds) throws IOException {
        this.shutdown();

        this.process    = cmds.length == 1
                ? Runtime.getRuntime().exec(cmds[0])
                : Runtime.getRuntime().exec(cmds);
        this.printer    = new PrintWriter(this.process.getOutputStream(), true);
        this.cmds       = cmds;

        PIO.pool.submit(new Worker(this.process,
                this.process.getInputStream(),
                this.inputReaders));
        PIO.pool.submit(new Worker(this.process,
                this.process.getErrorStream(),
                this.errorReaders));

        return this;
    }

    /**
     * 关闭此PIO。相关进程和IO线程资源会同步关闭释放。
     *
     * @return
     */
    public PIO shutdown() {
        if (null != this.process) {
            this.process.destroyForcibly();
            this.process    = null;
            this.cmds       = null;
            this.pid        = null;
            this.printer    = null;
        }
        return this;
    }

    /**
     * 获取此PIO的开启状态。
     *
     * @return
     */
    public boolean isOpen() {return null != this.process && this.process.isAlive();}

    /**
     * 等待进程结束。
     *
     * @return 返回码
     * @throws InterruptedException
     */
    public int await() throws InterruptedException {
        return this.process.waitFor();
    }

    /**
     * 等待进程结束。
     *
     * @param time
     * @param unit
     * @return true - 进程成功退出；false - 等待时间超时
     * @throws InterruptedException
     */
    public boolean await(long time, TimeUnit unit) throws InterruptedException {
        return this.process.waitFor(time, unit);
    }

    /**
     * 添加标准输出Reader。
     *
     * @param reader
     * @return
     */
    public PIO readInput(PIOReader reader) {
        if (null != reader) this.inputReaders.add(reader);
        return this;
    }

    /**
     * 添加错误输出Reader。
     *
     * @param reader
     * @return
     */
    public PIO readError(PIOReader reader) {
        if (null != reader) this.errorReaders.add(reader);
        return this;
    }

    /**
     *
     * @return
     */
    public PrintWriter printer() {
        return this.printer;
    }

    /**
     * 获取当前正在运行的进程命令。
     *
     * @return
     */
    public String[] cmds() {return this.cmds;}

    /**
     * 获取进程ID。
     *
     * @return
     */
    public Integer pid() {
        if (null == this.pid && this.isOpen()) {
            try {
                Field pid = Class.forName("java.lang.UNIXProcess").getDeclaredField("pid");
                pid.setAccessible(true);
                this.pid = pid.getInt(this.process);
            } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
        return this.pid;
    }

    /**
     * 获取进程附加的业务内容。
     *
     * @param key
     * @return
     */
    public Object attach(String key) {
        if (null == this.attach) return null;
        return this.attach.get(key);
    }

    /**
     * 向附加业务内容。
     *
     * @param key
     * @param val
     * @return
     */
    public PIO attach(String key, Object val) {
        if (null == this.attach)
            this.attach = new HashMap<>();
        this.attach.put(key, val);
        return this;
    }

    private static class Worker implements Runnable {

        private Process         process;
        private InputStream     reader;
        private List<PIOReader> readers;

        private Worker(Process process, InputStream reader, List<PIOReader> readers) {
            this.process    = process;
            this.reader     = reader;
            this.readers    = readers;
        }

        @Override
        public void run() {
            int     len = 0;
            byte[]  buf = new byte[1024 * 4];
            while (null != this.process && this.process.isAlive()) {
                try {
                    if (0 < (len = this.reader.read(buf))) {
                        for (PIOReader reader : this.readers) {
                            try {reader.read(buf, 0, len);}
                            catch (Exception e) {e.printStackTrace();}
                        }
                    }
                } catch (IOException e) {
                    // maybe process is killed, no need to print stack.
                }
            }
        }

    }
}
