sudo docker build . -t rweiw/jmcli:0.1 --no-cache
sudo docker tag rweiw/jmcli:0.1 rweiw/jmcli:latest
sudo docker login
sudo docker push rweiw/jmcli:0.1
sudo docker push rweiw/jmcli:latest