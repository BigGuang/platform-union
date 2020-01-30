# CGI处理模块
import cgi, cgitb

form = cgi.FieldStorage()

site_name = form.getvalue('name')
site_url = form.getvalue('url')

print("Content-type:text/html\n")
print("<html>")
print("<head>")
print("<meta charset=\"utf-8\">")
print("<title>CGI 测试实例</title>")
print("</head>")
print("<body>")
print("<h2>%s官网：%s</h2>" % (site_name, site_url))
print("</body>")
print("</html>")
