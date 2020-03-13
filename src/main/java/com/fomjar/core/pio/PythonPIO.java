package com.fomjar.core.pio;

import java.io.IOException;

/**
 * Python的PIO实现。封装了一些方便python使用的方法。
 *
 * @author fomjar
 */
public class PythonPIO extends PIO {

    /**
     * 启动默认python解释器。基于当前系统的环境变量。
     * 为了方便地使用Python的标准输入输出，此处依赖pio.py脚本在命令模式下来传递和执行数据。
     * 对python交互模式的标准输入输出太复杂太难用的一种妥协。
     *
     * @return
     * @throws IOException
     */
    public PythonPIO startup() throws IOException {
        super.startup("python", "-u", "pio.py");  // parameter "-u" to force script write it's output to stdout.
        return this;
    }

    /**
     * 方便地导入依赖模块。导入后会自动reload以使自定义模块的代码修改实时生效。
     *
     * @param mod
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
