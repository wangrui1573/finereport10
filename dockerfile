#
# FineReport 10.0 Dockerfile
#
# Version 2.0.1 20210809

# https://github.com/ysslang/FineDocker
# https://www.finereport.com
#

# Pull base image.
FROM docker.io/realwang/fr10:3.5

# Pass some arguments
COPY /0 /0
RUN rm -rf /0/tomcat/webapps/webroot/WEB-INF/embed/finedb/*


# Expose HTTP port and WebSocket port
EXPOSE 8080
EXPOSE 38888

# Define default command and running mode
ENTRYPOINT ["sh", "finedocker.sh"]
CMD ["run"]
