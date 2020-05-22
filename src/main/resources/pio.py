#!/usr/bin/python
#coding:utf-8

# pio的配套python脚本
# 该脚本是对python的交互模式下无法方便地使用标准输入输出的一种妥协
# 通过命令行方式来进行标准输入输出
# @author: fomjar

import sys
import traceback

# 只能用这种方式来读，for in不行
line = sys.stdin.readline()

while line:
    try:
        exec(line)
    except:
        '''防止异常退出'''
        traceback.print_exc()

    line = sys.stdin.readline()
