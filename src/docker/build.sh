sudo docker build . -t rweiw/jmcli:0.2 --no-cache
sudo docker tag rweiw/jmcli:0.2 rweiw/jmcli:latest
sudo docker login
sudo docker push rweiw/jmcli:0.2
sudo docker push rweiw/jmcli:latest