服务器部署
---
服务器:10.1.0.19  
api服务目录:/home/api_server/weapps/jd_union/   
订单信息定时抓取:/home/jd_order_server/weapps/jd_union/  

api 接口由 /home/tomcat8_1 , /home/tomcat8_2, /home/tomcat8_3 负载运行，  
./root/api_1_restart.sh  
./root/api_2_restart.sh  
./root/api_3_restart.sh  
分别控制重启  
输出统一在/home/logs/catalina.out  
  
order 接口由 /home/tomcat8_order 运行, 访问端口在api地址上加:9000  
catalina.out 输出在 /home/tomcat8_order/logs/  
  
ElasticSearch服务器:10.1.0.8  


代码模块说明
---
####commons-esclient 模块  
ElasticSearch基础操作类  
####service-jd 模块  
目前包含京东联盟的API接口，以及京东联盟用到的ES具体结构和操作方法  
####union-jd-api 模块  
此模块作为web服务部署，包含给前端公开出的api接口   


编译和运行
---
Maven运行platform-union 中的clean、validate、compile、package  




新增与涂色微信机器人接口  
---
代码目录com.waps.robot_api;  
已实现与涂色服务器授权链接，操作回调接收。  
TODO:  
统一处理回调记录，便于分类查询  

