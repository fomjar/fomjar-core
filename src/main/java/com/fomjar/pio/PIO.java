package com.fomjar.pio;

import com.fomjar.lang.Task;
import com.fomjar.lang.Struct;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Process I/O 进程IO的通用实现。所有IO数据均通过进程标准IO来传递。
 *
 * @author fomjar
 */
public class PIO {

    private List<PIOReader> inputReaders;
    private List<PIOReader> errorReaders;

    private Process     process;
    private String[]    cmd;
    private int         pid;
    private PrintWriter printer;

    private Map<String, Object> attach;

    public PIO() {
        this.pid = -1;
        this.inputReaders   = new LinkedList<>();
        this.errorReaders   = new LinkedList<>();
    }

    /**
     * 根据给定命令启动此PIO。相关进程启动，相关IO线程挂载。
     *
     * @param cmd 待执行的命令
     * @return 此命令启动的PIO对象
     * @throws IOException 启动失败
     */
    public PIO startup(String... cmd) throws IOException {
        this.shutdown();

        this.process    = cmd.length == 1
                ? Runtime.getRuntime().exec(cmd[0])
                : Runtime.getRuntime().exec(cmd);
        this.printer    = new PrintWriter(this.process.getOutputStream(), true);
        this.cmd = cmd;

        Task.async(new Worker(this.process,
                this.process.getInputStream(),
                this.inputReaders));
        Task.async(new Worker(this.process,
                this.process.getErrorStream(),
                this.errorReaders));

        return this;
    }

    /**
     * 关闭此PIO。相关进程和IO线程资源会同步关闭释放。
     *
     * @return 此PIO对象
     */
    public PIO shutdown() {
        if (null != this.process) {
            this.process.destroyForcibly();
            this.process    = null;
            this.cmd        = null;
            this.pid        = -1;
            this.printer    = null;
        }
        return this;
    }

    /**
     * 获取此PIO的开启状态。
     *
     * @return true为开启，false为关闭
     */
    public boolean isOpen() {return null != this.process && this.process.isAlive();}

    /**
     * 等待进程结束。
     *
     * @return 返回码
     * @throws InterruptedException 等待结束过程中被中断
     */
    public int await() throws InterruptedException {
        return this.process.waitFor();
    }

    /**
     * 等待进程结束。
     *
     * @param time 时间
     * @param unit 单位
     * @return true - 进程成功退出；false - 等待时间超时
     * @throws InterruptedException 等待被中断
     */
    public boolean await(long time, TimeUnit unit) throws InterruptedException {
        return this.process.waitFor(time, unit);
    }

    /**
     * 添加标准输出Reader。
     *
     * @param reader 异步读取回调接口
     * @return 此PIO对象
     */
    public PIO readInput(PIOReader reader) {
        if (null != reader) this.inputReaders.add(reader);
        return this;
    }

    /**
     * 添加错误输出Reader。
     *
     * @param reader 异步读取回调对象
     * @return 此PIO对象
     */
    public PIO readError(PIOReader reader) {
        if (null != reader) this.errorReaders.add(reader);
        return this;
    }

    /**
     * 打印器，用于写入数据。
     *
     * @return 此PIO对象关联进程的写入打印器
     */
    public PrintWriter printer() {
        return this.printer;
    }

    /**
     * 获取当前正在运行的进程命令。
     *
     * @return 命令数组
     */
    public String[] cmds() {return this.cmd;}

    /**
     * 获取进程ID。
     *
     * @return 进程ID
     */
    public int pid() {
        if (-1 == this.pid) {
            try { this.pid = Struct.get(this.process, int.class, "pid"); }
            catch (NoSuchFieldException | IllegalAccessException e) { throw new IllegalStateException(e); }
        }
        return this.pid;
    }

    /**
     * 获取进程附加的业务内容。
     *
     * @param key 键
     * @return 值
     */
    public Object attach(String key) {
        if (null == this.attach) return null;
        return this.attach.get(key);
    }

    /**
     * 向附加业务内容。
     *
     * @param key 键
     * @param val 值
     * @return 此PIO对象
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
                        int finalLen = len;
                        for (PIOReader reader : this.readers)
                            Task.catchdo(() -> reader.read(buf, 0, finalLen));
                    }
                } catch (IOException e) {
                    // maybe process is killed, no need to print stack.
                }
            }
        }

    }
}
