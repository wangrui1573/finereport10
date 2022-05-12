# finereport10
帆软10.0 测试版，无授权


端口 8080，参考工作目录

docker run --restart=always -d --name FR10-2 -p 80:8080 -p 38889:38888 realwang/fr10:3.0

http://IP地址/webroot/decision

删除DB重建账户密码： rm -rf /0/tomcat/webapps/webroot/WEB-INF/embed/finedb/

可能因为DB锁住导致服务无法启动，进入容器，运行 rm -rf /0/tomcat/webapps/webroot/WEB-INF/embed/finedb/db.lck
