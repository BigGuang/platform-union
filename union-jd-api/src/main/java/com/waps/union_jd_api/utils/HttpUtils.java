package com.waps.union_jd_api.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

public class HttpUtils {
    public static String postJsonInputStream(String urlString, String jsonString, String savePath) {

        StringBuffer sb = new StringBuffer();
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            // 创建连接
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
//**注意点1**，需要此格式，后边这个字符集可以不设置
            connection.connect();
            DataOutputStream out = new DataOutputStream(
                    connection.getOutputStream());
            out.write(jsonString.getBytes("UTF-8"));//**注意点2**，需要此格式
            inputStream = connection.getInputStream();

            saveFile(inputStream, savePath);

            out.flush();
            out.close();
            // 断开连接
            connection.disconnect();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("postJsonString ERROR:" + e.getLocalizedMessage());
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return savePath;
    }

    public static void inputStreamToFile(InputStream is, String fileName) throws IOException {
        OutputStream outputStream = null;
        File file = new File(fileName);
        outputStream = new FileOutputStream(file);
        int bytesWritten = 0;
        int byteCount = 0;
        byte[] bytes = new byte[1024];
        while ((byteCount = is.read(bytes)) != -1) {
            outputStream.write(bytes, bytesWritten, byteCount);
            bytesWritten += byteCount;
        }
        is.close();
        outputStream.close();
    }

    public static void saveFile(InputStream is, String fileName) throws IOException {
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        in = new BufferedInputStream(is);
        out = new BufferedOutputStream(new FileOutputStream(fileName));
        int len = -1;
        byte[] b = new byte[1024];
        while ((len = in.read(b)) != -1) {
            out.write(b, 0, len);
        }
        in.close();
        out.close();
    }

    public static String getUrl(String urlString) {
        return getUrl(urlString, "UTF-8");
    }

    public static String getUrl(String urlString, String charSet) {
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            //Get请求不需要DoOutPut
            conn.setDoOutput(false);
            conn.setDoInput(true);
            //设置连接超时时间和读取超时时间
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //连接服务器
            conn.connect();
            // 取得输入流，并使用Reader读取
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), charSet));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            System.out.println("getUrl ERROR:" + e.getLocalizedMessage());
            System.out.println("getUrl ERROR:" + urlString);
//            e.printStackTrace();
        }
        //关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result.toString();
    }

    public static String postJsonString(String urlString, String jsonString) {
        StringBuffer sb = new StringBuffer();
        HttpURLConnection connection = null;
        try {
            // 创建连接
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
//**注意点1**，需要此格式，后边这个字符集可以不设置
            connection.connect();
            DataOutputStream out = new DataOutputStream(
                    connection.getOutputStream());
            out.write(jsonString.getBytes("UTF-8"));//**注意点2**，需要此格式
            out.flush();
            out.close();
            // 读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "utf-8"));//**注意点3**，需要此格式
            String lines;

            while ((lines = reader.readLine()) != null) {
                sb.append(lines);
            }
            reader.close();
            // 断开连接
            connection.disconnect();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("postJsonString ERROR:" + e.getLocalizedMessage());
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return sb.toString();
    }

    public static String postFormParams(String urlString, Map<String, String> params) {
        StringBuilder returnContent = new StringBuilder();
        HttpURLConnection connection = null;
        try {
            // 创建连接
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.connect();
            DataOutputStream out = new DataOutputStream(
                    connection.getOutputStream());
            Iterator it = params.keySet().iterator();
            StringBuilder content = new StringBuilder();
            while (it.hasNext()) {
                String key = (String) it.next();
                String value = params.get(key);
                content.append(key).append("=").append(URLEncoder.encode(value, "UTF-8")).append("&");
            }
            out.writeBytes(content.toString());
            out.flush();
            out.close();
            // 读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), StandardCharsets.UTF_8));//**注意点3**，需要此格式
            String lines;

            while ((lines = reader.readLine()) != null) {
                returnContent.append(lines);
            }
            reader.close();
            // 断开连接
            connection.disconnect();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("postJsonString ERROR:" + e.getLocalizedMessage());
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return returnContent.toString();
    }
}
