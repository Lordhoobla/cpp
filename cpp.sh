#!/bin/bash
apt-get install default-jre -y
apt-get install default-jdk -y
apt install net-tools -y
touch ./Downloads/cpp/run.txt && ./Downloads/cpp/stats.txt && ./Downloads/cpp/users.txt
chmod a+w ./Downloads/cpp/stats.txt && a+w ./Downloads/cpp/run.txt && a+w ./Downloads/cpp/Cpp.java && a+w ./Downloads/cpp/users.txt
printf "Look here for listening ports\\/\n\n" >> ./Downloads/cpp/stats.txt
netstat -tulpn|grep LISTEN >> ./Downloads/cpp/stats.txt
printf "\n\nLook here for users in the system\\/\n\n" >> ./Downloads/cpp/stats.txt
sudo getent passwd {1000..6000} >> ./Downloads/cpp/stats.txt
echo -e "\e[1;42mRefer to the \"stats.txt\" file for information for the questions (type \"ok\")\e[0m"
read conf
cd ./Downloads/cpp
javac ./Cpp.java
java -cp . Cpp
cd ..&&cd ..
rm ./Downloads/cpp/stats.txt
rm ./Downloads/cpp/users.txt
mv ./Downloads/cpp/run.txt ./Downloads/cpp/run.sh
chmod +x ./Downloads/cpp/run.sh
./Downloads/cpp/run.sh
rm ./Downloads/cpp/run.sh
