package com.waps.union_jd_api.utils

import com.waps.tools.security.MD5;
import com.waps.utils.StringUtils;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.stream.FileImageInputStream;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

class HttpUtils {
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
        BufferedInputStream bufferedInputStream = null;
        BufferedOutputStream out = null;
        bufferedInputStream = new BufferedInputStream(is);
        out = new BufferedOutputStream(new FileOutputStream(fileName));
        int len = -1;
        byte[] b = new byte[1024];
        while ((len = bufferedInputStream.read(b)) != -1) {
            out.write(b, 0, len);
        }
        bufferedInputStream.close();
        out.close();
    }

    public static String getUrl(String urlString) {
        return getUrl(urlString, "UTF-8");
    }

    public static String getUrl(String urlString, String charSet) {
        BufferedReader bufferedReader = null;
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
            bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), charSet));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
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
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result.toString();
    }

    public static String postJsonString(String urlString, String jsonString) {
        println "POST_URL:"+urlString
        println "POST_JSON:"+jsonString
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

    public static String postFormParams(String urlString, Map<String, Object> params, Map<String, String> header) {
        String BOUNDARY = UUID.randomUUID().toString();  //边界标识   随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";   //内容类型

        StringBuilder returnContent = new StringBuilder();
        HttpURLConnection connection = null;
        try {
            // 创建连接
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            if (header != null) {
                Iterator header_it = header.keySet().iterator();
                while (header_it.hasNext()) {
                    String k = (String) header_it.next();
                    String v = header.get(k);
                    if (!StringUtils.isNull(v)) {
                        connection.setRequestProperty(k, v);
                    }
                }
            }
            String content_type = connection.getRequestProperty("Content-Type");
            if (!StringUtils.isNull(content_type) && content_type.startsWith("multipart/form-data")) {
                connection.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            }
            content_type = connection.getRequestProperty("Content-Type");

            OutputStream out = new DataOutputStream(connection.getOutputStream());

            Iterator it = params.keySet().iterator();
            StringBuilder content = new StringBuilder();
            if (!StringUtils.isNull(content_type) && content_type.startsWith("multipart/form-data")) {
                while (it.hasNext()) {
                    String key = (String) it.next();
                    Object value = params.get(key);
                    if (value instanceof byte[]) {
                        StringBuffer strBuf = new StringBuffer();
                        String filename = new MD5().getMD5(UUID.randomUUID().toString()) + ".jpg";
                        String contentType = "image/jpeg";
                        strBuf.append(PREFIX).append(BOUNDARY).append(LINE_END);
                        strBuf.append("Content-Disposition: form-data; name=\"").append(key).append("\"; filename=\"").append(filename).append("\"").append(LINE_END);
                        strBuf.append("Content-Type: ").append(contentType).append(LINE_END).append(LINE_END);
                        out.write(strBuf.toString().getBytes());

                        System.out.println("==byte[] 上传==");
                        byte[] imgByte = (byte[]) value;
                        out.write(imgByte);
                        out.write(LINE_END.getBytes());

                    } else if (value instanceof File) {

                        StringBuffer strBuf = new StringBuffer();
                        File _file = (File) value;
                        System.out.println("==File 上传==");
                        System.out.println(_file.getPath());
                        String filename = _file.getName();
                        String contentType = new MimetypesFileTypeMap().getContentType(_file);
                        if (filename.endsWith(".png")) {
                            contentType = "image/png";
                        } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
                            contentType = "image/jpeg";
                        } else {
                            contentType = "application/octet-stream";
                        }

                        strBuf.append(PREFIX).append(BOUNDARY).append(LINE_END);
                        strBuf.append("Content-Disposition: form-data; name=\"").append(key).append("\"; filename=\"").append(filename).append("\"").append(LINE_END);
                        strBuf.append("Content-Type: ").append(contentType).append(LINE_END).append(LINE_END);
                        out.write(strBuf.toString().getBytes());

                        FileImageInputStream input = new FileImageInputStream(_file);
                        byte[] buf = new byte[1024];
                        int numBytesRead = 0;
                        while ((numBytesRead = input.read(buf)) != -1) {
                            out.write(buf, 0, numBytesRead);
                        }
                        out.write(LINE_END.getBytes());
                        input.close();
                    } else {
                        StringBuffer buffer = new StringBuffer();
                        String _value = (String) value;
                        buffer.append(PREFIX).append(BOUNDARY).append(LINE_END);
                        buffer.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(LINE_END).append(LINE_END);
                        buffer.append(_value).append(LINE_END);
                        out.write(buffer.toString().getBytes());
                    }
                }
                String endLine = PREFIX + BOUNDARY + PREFIX + LINE_END;
                out.write(endLine.getBytes())
            } else {
                while (it.hasNext()) {
                    String key = (String) it.next()
                    Object value = params.get(key)
                    content.append(key).append("=").append(URLEncoder.encode(value as String, "UTF-8")).append("&")
                }
                out.write(content.toString().getBytes())
            }
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
            System.out.println("postFormParams ERROR:" + e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
                connection = null;
            }
        }
        return returnContent.toString();
    }


    /**
     * 根据地址获得数据的字节流
     *
     * @param strUrl 网络连接地址
     * @return
     */
    public static byte[] getImageFromNetByUrl(String strUrl) {
        try {
            URL url = new URL(strUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            InputStream inStream = conn.getInputStream();// 通过输入流获取图片数据
            byte[] btImg = readInputStream(inStream);// 得到图片的二进制数据
            return btImg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从输入流中获取数据
     *
     * @param inStream 输入流
     * @return
     * @throws Exception
     */
    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[10240];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }
}
