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
import java.awt.GraphicsEnvironment
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import java.awt.RenderingHints

@Component
class ImageService {
    static int width = 720
    static int height = 570
    private Font font
    private int wordLine = 0
    private int wordMaxLine = 3
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


        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = e.getAvailableFontFamilyNames();
        for (String fontName : fontNames) {
            System.out.println(fontName);
        }
    }

    public String makeImage(List<ElementBean> elementBeanList, String type, HttpServletResponse response) {
        this.init(true)
        for (ElementBean el : elementBeanList) {
            String _type = el.getType()
            switch (_type) {
                case "text":
                    this.font = new Font(el.getFont(), Font.PLAIN, el.getSize())
                    drawWord(el.getText(), el.getColor(), this.font,el.getInterval(), el.getX(), el.getY())
                    break

                case "text-blod":
                    this.font = new Font(el.getFont(), Font.BOLD, el.getSize())
                    drawWord(el.getText(),el.getInterval(), el.getX(), el.getY())
                    break

                case "img":
                    drawImage(el.getUrl(), el.getX(), el.getY(), el.getW(), el.getH())
                    break

                case "area":
                    drawArea(el.getX(), el.getY(), el.getW(), el.getH(), el.getColor())
                    break

                case "area-round":
                    drawAreaRound(el.getX(), el.getY(), el.getW(), el.getH(), el.getArc(), el.getColor())
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

    /**
     * 矩形
     * @param x
     * @param y
     * @param w
     * @param h
     * @param colorStr
     */
    public void drawArea(int x, int y, int w, int h, String colorStr) {
        this.graphics.drawRect(x, y, w, h)//画线框
        this.graphics.setColor(toColorFromString(colorStr))
        this.graphics.fillRect(x, y, w, h)//画着色块
    }

    /**
     * 圆角矩形
     * @param x
     * @param y
     * @param w
     * @param h
     */
    public void drawAreaRound(int x, int y, int w, int h, int arc, String colorStr) {
        this.graphics.setColor(toColorFromString(colorStr))
        RoundRectangle2D rect = new RoundRectangle2D.Double(x, y, w, h, arc, arc)
        this.graphics.draw(rect);
    }

    /**
     * 将一个在线图片绘制下来
     * @param imgUrl
     * @param x
     * @param y
     * @param w
     * @param h
     * @throws FileNotFoundException* @throws IOException
     */
    public void drawImage(String imgUrl, int x, int y, int w, int h) throws FileNotFoundException, IOException {
        ArrayList<BufferedImage> imgList = ImageUtils.downloadImages(imgUrl)
        BufferedImage _bufferImg = imgList.get(0)
        this.graphics.drawImage(_bufferImg, x, y, w, h, null);    // 写入图片
    }

    /**
     * 绘制文本，自动换行
     * @param content
     * @param colorStr
     * @param font
     * @param x
     * @param y
     */
    public void drawWord(String content, String colorStr, Font font,int interval, int x, int y) {
        drawWord(content, interval, x, y, font, wordLine, toColorFromString(colorStr))
    }

    public void drawWord(String content,int interval, int x, int y) {
        drawWord(content, interval, x, y, this.font, wordLine, Color.BLACK);
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
            List<Integer> oneLineCharNumber = getOneLineCharNumber(content, oneLineWidth)
            //控制文字显示在几行内
            int num = wordMaxLine
            if (oneLineCharNumber.size() < num) {
                num = oneLineCharNumber.size()
            }
            for (int i = 0; i < num; i++) {
                String string = "";
                if (i == 0) {
                    result = y + fontSize;
                    string = content.substring(0, oneLineCharNumber.get(i));
                } else {
                    result = y + (fontSize + (fontSize * i));
                    string = content.substring(oneLineCharNumber.get(i - 1), oneLineCharNumber.get(i));
                }
                //最后一行
                if(i==(num-1)){
                    string=string.substring(0,string.length()-1)+"..."
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
        list.add(charArray.length)

        return list;
    }

    /**
     * 字符串转换成Color对象
     * @param colorStr 16进制颜色字符串
     * @return Color对象*           */
    public static Color toColorFromString(String colorStr) {
        try {
            colorStr = colorStr.replaceAll("#", "")
            int i = Integer.parseInt(colorStr, 16)
            return new Color(i);
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
    String font="宋体"
    int size
    int x = 0
    int y = 0
    int w
    int h
    int arc = 0
    int interval=20   //文字左右间隔
}
