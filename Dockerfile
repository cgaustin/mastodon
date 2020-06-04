FROM centos:centos7

RUN yum update -y

## Install Java
RUN yum install -y java-1.8.0-openjdk.x86_64 java-1.8.0-openjdk-devel.x86_64  java-1.8.0-openjdk-headless.x86_64

## Install NGINX
ENV nginxversion="1.16.1-1" \
    os="centos" \
    osversion="7" \
    elversion="7"

RUN yum install -y curl wget openssl sed &&\
    yum -y autoremove &&\
    yum clean all &&\
    wget http://nginx.org/packages/$os/$osversion/x86_64/RPMS/nginx-$nginxversion.el$elversion.ngx.x86_64.rpm &&\
    rpm -iv nginx-$nginxversion.el$elversion.ngx.x86_64.rpm

ENV LEIN_ROOT ok
ENV LEIN /usr/local/bin/lein
RUN curl -L -o $LEIN \
    https://raw.githubusercontent.com/technomancy/leiningen/2.8.3/bin/lein
RUN chmod 755 $LEIN
RUN $LEIN

RUN mkdir /app
COPY . /app/
WORKDIR /app
RUN lein uberjar

## Install Mastodon
RUN cp /app/resources/public/index.html /usr/share/nginx/html/index.html && \
    mkdir -p /usr/share/nginx/html/js/compiled && \
    cp /app/resources/public/js/compiled/mastodon_min.js /usr/share/nginx/html/js/compiled/mastodon_min.js && \
    cp /app/resources/public/js/jquery.min.js /usr/share/nginx/html/js/jquery.min.js && \
    cp -r /app/resources/public/css /usr/share/nginx/html/css/ && \
    cp -r /app/resources/public/images /usr/share/nginx/html/images/ && \
    cp /app/resources/log4j.properties /log4j.properties && \
    cp /app/default.conf /etc/nginx/conf.d/default.conf
    
RUN mkdir /data

# Run!
#CMD /startup.sh
CMD /app/bin/startup.sh

