sudo docker build . -t rweiw/jmcli:0.2-java8 --no-cache
sudo docker tag rweiw/jmcli:0.2-java8 rweiw/jmcli:latest-java8
sudo docker login
sudo docker push rweiw/jmcli:0.2-java8
sudo docker push rweiw/jmcli:latest-java8