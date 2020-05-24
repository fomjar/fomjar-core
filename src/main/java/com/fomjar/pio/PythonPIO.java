package com.fomjar.pio;

import com.fomjar.io.BufferedStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Python的PIO实现。封装了一些方便python使用的方法。
 *
 * @author fomjar
 */
public class PythonPIO extends PIO {

    private static final String SCRIPT = "pio.py";

    /**
     * 启动默认python解释器。基于当前系统的环境变量。
     * 为了方便地使用Python的标准输入输出，此处依赖pio.py脚本在命令模式下来传递和执行数据。
     * 对python交互模式的标准输入输出太复杂太难用的一种妥协。
     *
     * @return Python进程关联的PIO对象
     * @throws IOException 启动进程失败
     */
    public PythonPIO startup() throws IOException {
        File file = new File(PythonPIO.SCRIPT);
        if (!file.isFile()) {
            InputStream is = PythonPIO.class.getResourceAsStream("/" + PythonPIO.SCRIPT);
            try { new BufferedStream().write(is).writeTo(file); }
            finally { is.close(); }
        }
        super.startup("python", "-u", PythonPIO.SCRIPT);  // parameter "-u" to force script write it's output to stdout.
        return this;
    }

    /**
     * 方便地导入依赖模块。导入后会自动reload以使自定义模块的代码修改实时生效。
     *
     * @param mod 模块名
     */
    public void imp(String mod) {
        this.printer().printf("import %s", mod);
        this.printer().println();
        this.printer().println();
        this.printer().println("from imp import reload");  // 3.x support
        this.printer().printf("reload(%s)", mod);          // make changes effective
        this.printer().println();
    }

}
