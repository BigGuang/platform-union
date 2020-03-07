package com.waps.union_jd_api.service

import com.alibaba.fastjson.JSONArray
import com.waps.union_jd_api.utils.ImageUtils
import org.springframework.stereotype.Component

import javax.imageio.ImageIO
import javax.servlet.http.HttpServletResponse
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.geom.Ellipse2D
import java.awt.image.BufferedImage
import java.awt.RenderingHints

@Component
class ImageService {
    static int width = 720
    static int height = 570
    private Font font
    private BufferedImage bufferedImage;
    private Graphics2D graphics

    ImageService() {

    }

    private void init(boolean bool) {
        this.bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR)
        this.graphics = bufferedImage.createGraphics()
        if (bool) {
            this.graphics.setBackground(Color.WHITE) // 设置背景为白色
            this.graphics.clearRect(0, 0, width, height) // 擦除默认背景(默认背景擦除后会显示自己设置的背景)
        }
        this.graphics.setPaint(Color.BLACK); // 设置字体为黑色
        this.graphics.setFont(this.font) // 设置 字体 斜率 字体大小
        this.graphics.setStroke(new BasicStroke(1f))    // 画笔粗细
        this.graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // 消除文字锯齿
        this.graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 消除图片锯齿
    }

    public String makeImage(List<ElementBean> elementBeanList, String type, HttpServletResponse response) {
        this.init(true)
        for (ElementBean element : elementBeanList) {
            String _type = element.getType()
            switch (_type) {
                case "text":
                    this.font = new Font("黑体", Font.PLAIN, element.getSize())
                    drawWord(element.getText(), element.getColor(), this.font, element.getX(), element.getY())
                    break

                case "text-blod":
                    this.font = new Font("黑体", Font.BOLD, element.getSize())
                    drawWord(element.getText(), element.getX(), element.getY())
                    break

                case "img":
                    drawImage(element.getUrl(), element.getX(), element.getY(), element.getW(), element.getH())
                    break

                case "ar":
                    drawArea(element.getX(), element.getY(), element.getW(), element.getH(), element.getColor())
                    break

                case "area-round":
                    break

            }
        }
        //绘制圆形
//        graphics.setColor(Color.BLACK);
//        Ellipse2D.Double ellipse = new Ellipse2D.Double(20, 20, 100, 100);
//        graphics.draw(ellipse);

        // 绘图逻辑 END
        //处理绘图
        graphics.dispose();
        //将绘制好的图片写入到图片
        OutputStream outputStream = response.getOutputStream()
        String _type = "jpg"
        if (type.toLowerCase() == "jpg" || type.toLowerCase() == "jpeg") {
            response.setHeader("Content-type", "image/jpg")
            _type = "jpg"
        }
        if (type.toLowerCase() == "png") {
            response.setHeader("Content-type", "image/png")
            _type = "png"
        }
        ImageIO.write(bufferedImage, _type, outputStream)
    }

    public void drawArea(int x, int y, int w, int h, String colorStr) {
        this.graphics.drawRect(x, y, w, h)//画线框
        this.graphics.setColor(toColorFromString(colorStr))
        this.graphics.fillRect(x, y, w, h)//画着色块
    }

    public void drawImage(String imgUrl, int x, int y, int w, int h) throws FileNotFoundException, IOException {
        ArrayList<BufferedImage> imgList = ImageUtils.downloadImages(imgUrl)
        BufferedImage _bufferImg = imgList.get(0)
        this.graphics.drawImage(_bufferImg, x, y, w, h, null);    // 写入图片
    }


    public void drawWord(String content, String colorStr, Font font, int x, int y) {
        drawWord(content, 0, x, y, font, 5, toColorFromString(colorStr))
    }

    public void drawWord(String content, int x, int y) {
        drawWord(content, 0, x, y, this.font, 5, Color.BLACK);
    }

    /**
     * content 需要绘制的文字
     * interval 文字两端空余像素
     * oneLineWidth 绘制文字一行像素值
     * x 绘制起始点x
     * y 绘制起始点y
     * font 绘制使用字体(文字大小 文字加粗)
     * line 行间距
     * color 文字颜色
     */
    public void drawWord(String content, int interval, int oneLineWidth, int x, int y, Font font, int line, Color color) {
        this.graphics.setPaint(color);
        this.graphics.setFont(font);

        x += interval;

        int fontSize = font.getSize() + line;
        int result = 0;
        if (isMoreThanOneLine(content, oneLineWidth)) { // 文字长度没有超出一行的长度
            result = y + fontSize;
            this.graphics.drawString(content, x, y + fontSize); // 文字画入画布中
        } else { // 文字超出一行处理
            List<Integer> oneLineCharNumber = getOneLineCharNumber(content, oneLineWidth);
            for (int i = 0; i < oneLineCharNumber.size(); i++) {
                String string = "";
                if (i == 0) {
                    result = y + fontSize;
                    string = content.substring(0, oneLineCharNumber.get(i));
                } else {
                    result = y + (fontSize + (fontSize * i));
                    string = content.substring(oneLineCharNumber.get(i - 1), oneLineCharNumber.get(i));
                }
                this.graphics.drawString(string, x, result); // 文字画入画布中
            }
        }
    }

    public void drawWord(String content, int interval, int x, int y, Font font, int line, Color color) {
        this.drawWord(content, interval, width - x - (interval * 2), x, y, font, line, color);
    }


    /**
     * 获取文字需要画几行
     * @param cont
     * @return 行数
     */
    public int getMessageRowsNumber(String message, int oneLineWidth, Font cont) {
        this.graphics.setFont(cont);
        char[] charArray = message.toCharArray();
        int messagePX = getMessagePX(message, 0, charArray.length); // 获取文字所需总像素值
        int count = 0;
        if (messagePX / oneLineWidth > 1 && messagePX % oneLineWidth != 0) {
            count = 1;
        }
        return messagePX / oneLineWidth + count; // 总像素值/每行像素值
    }

    public void flush(File file) throws IOException {
        this.graphics.dispose()
        ImageIO.write(this.bufferedImage, "png", file);
    }

    /**
     * 写入字符 字符长度是否超过一行
     * @param graphics2D
     * @param message
     */
    private boolean isMoreThanOneLine(String message, int oneLineWidth) {
        return isMoreThanOneLine(message, 0, message.length(), oneLineWidth);
    }

    /**
     * @param graphics2D 画布
     * @param message 画入的字符串
     * @param off 字符串的偏移量
     * @param charArrayLength 字符串的长度
     */
    private boolean isMoreThanOneLine(String message, int off, int charArrayLength, int oneLineWidth) {
        int charsWidth = getMessagePX(message, off, charArrayLength)
        println "文字像素值长度:" + charsWidth
        return charsWidth <= oneLineWidth; // 如果文字长度大于每行长度 返回false
    }

    /**
     * 获取文字像素长度
     * @param graphics2D 画布
     * @param message 画入的字符串
     * @param off 字符串的偏移量
     * @param charArrayLength 从偏移量开始 取多少个字符
     * @return 文字像素长度
     */
    private int getMessagePX(String message, int off, int charArrayLength) {
        return this.graphics.getFontMetrics().charsWidth(message.toCharArray(), off, charArrayLength);
    }

    /**
     * 获取一行的文字数
     * @return 文字数
     */
    private List<Integer> getOneLineCharNumber(String message, int oneLineWidth) {
        List<Integer> list = new ArrayList<>();
        int start = 0;
        char[] charArray = message.toCharArray();
        for (int i = 0; i < charArray.length - 1; i++) {
            int rowsLength = getMessagePX(message, start, i - start);
            if (rowsLength > oneLineWidth) {
                list.add(i);
                start = i;
            } else if (rowsLength == oneLineWidth) {
                list.add(i + 1);
                start = i + 1;
            }
        }
        list.add(charArray.length);

        return list;
    }

    /**
     * 字符串转换成Color对象
     * @param colorStr 16进制颜色字符串
     * @return Color对象*        */
    public static Color toColorFromString(String colorStr) {
        try {
            colorStr = colorStr.substring(4)
            Color color = new Color(Integer.parseInt(colorStr, 16))
            //java.awt.Color[r=0,g=0,b=255]
            return color;
        } catch (Exception e) {
            return Color.black
        }
    }
}

class ElementBean {
    String type    //img,text,text-blod,area,area-round
    String url
    String text
    String color
    int size
    int x = 0
    int y = 0
    int w
    int h
}
