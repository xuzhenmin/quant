#!/bin/bash
# 查找所有Java进程并强制终止
pids=$(ps -ef | grep '[j]ava' | awk '{print $2}')
if [ -z "$pids" ]; then
    echo "No Java processes found."
else
    echo "Killing Java processes: $pids"
    echo $pids | xargs kill -9
fi
