# CGI处理模块
# coding=utf-8
import cgi, cgitb
form = cgi.FieldStorage()

print("Content-type:text/html\n")
try:
    print("hello")
#     form = cgi.FieldStorage()

#     print(str(json_str))
except Exception as err:
    print('异常')
    print(err)
else:
    print("无异常")
