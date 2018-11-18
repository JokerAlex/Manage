#!/bin/bash
#
# 该部署脚本需要在项目跟目录下运行
#

# 用户名
# Username=
# ip 地址
# IP=
# 密码
# Pass=
# 项目目录
# ProjectDir=
# 应用存储目录
# RemoteDir=
# 应用临时存储目录
# RemoteDirTemp=


source ./parameter.sh

cd ${ProjectDir}/target

pwd

FileName=`ls *.jar`


echo "=============================================upload=========================================="

/usr/bin/expect << EOF

# 永不超时
set timeout -1

###############upload################

spawn scp ${FileName} ${Username}@${IP}:${RemoteDirTemp}

expect {
    "*password:" { send "${Pass}\r" }
    "yes/no" { send "yes\r";exp_continue }
    }

expect "*ETA" {
        exp_continue;
    }

###############kill-delete#########

spawn ssh ${Username}@${IP}

expect {
    "*password:" { send "${Pass}\r" }
    "yes/no" { send "yes\r";exp_continue }
}

expect -re {[#\$]+}

send "cd ${RemoteDir}\r"

expect -re {[#\$]+}

send "./kill.sh\r"

expect -re {[#\$]+}

send "ls\r"

###############start################

expect -re {[#\$]+}

send "mv ${RemoteDirTemp}/${FileName} ${RemoteDir}\r"

expect -re {[#\$]+}

send "cd ${RemoteDir}\r"

expect -re {[#\$]+}

send "./start.sh\r"

expect "*Started *Application in * seconds (JVM running for *)"

#  发送Ctrl-C
send "\003"

expect -re {[#\$]+}

send "logout\r"

expect eof

EOF

