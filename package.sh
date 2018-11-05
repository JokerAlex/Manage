#!/bin/bash

# 本地打包成 jar 文件

echo "===========================================mvn package======================================="

mvn clean package  -Dmaven.test.skip=true

./deploy.sh
