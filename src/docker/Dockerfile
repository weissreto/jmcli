FROM openjdk:11-jdk
RUN wget https://github.com/weissreto/jmx-cli/releases/download/v0.2.0/jmx-cli-0.2.0-linux.zip \
  && unzip jmx-cli-0.2.0-linux.zip -d /app 
ENV PATH="/app/jmx-cli-0.2.0/bin:$PATH"  
CMD /bin/bash 
