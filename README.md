###服务器部署
服务器:10.1.0.19
api服务目录:/home/api_server/weapps/jd_union/ 
订单信息定时抓取:/home/jd_order_server/weapps/jd_union/

api 接口由 /home/tomcat8_1 和 /home/tomcat8_2 负载运行，/root/api_restart.sh 无缝启动
catalina.out 输出在/home/logs/

order 接口由 /home/tomcat8_order 运行, 访问端口在api地址上加:9000
catalina.out 输出在 /home/tomcat8_order/logs/


###代码
com.robot 存放未启用的netty服务
com.waps.* 业务代码


删除ES索引
curl -XDELETE "http://10.1.0.8:9200/jd_category/"




京东关键词搜索接口

brandCode 可作为品牌搜索，指向到官方旗舰店，但需要字典表