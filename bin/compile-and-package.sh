#!/bin/bash

# 定义源代码目录和输出目录
SOURCE_DIR="./../src"
OUTPUT_DIR="classes"

# 创建输出目录，如果不存在的话
mkdir -p ${OUTPUT_DIR}

# 找到所有Java源文件并编译
find ${SOURCE_DIR} -name '*.java' -print -exec javac -d ${OUTPUT_DIR} {} +




