#!/bin/bash

#服务器脚本
#杀掉服务进程

appName=`ls *.jar`

# kill
ps -efww|grep ${appName}|grep -v grep|cut -c 9-15|xargs kill -9

# rm *.jar
mv ${appName} previousversion/
rm -f nohup.out
ls
# start

mv ./temp/${appName} .

nohup java -jar ${appName} --spring.profiles.active=prod &

sleep 2

a=5;
while [ ! ${a} -lt 1 ]
do
    echo "======================================================${a}-${a}-${a}-${a}-${a}=================================================="
    sleep 1
    a=`expr ${a} - 1`
done

tail -f nohup.out