package com.waps.union_jd_api.utils;

import com.alibaba.fastjson.JSONArray;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageUtils {


    public static ArrayList<BufferedImage> downloadImages(String[] files) {

        Map<Integer, BufferedImage> imagesMap = new HashMap<>();
        try {
            // 开始的倒数锁
            final CountDownLatch begin = new CountDownLatch(1);

            // 结束的倒数锁
            final CountDownLatch end = new CountDownLatch(files.length);

            // 十名选手
            final ExecutorService exec = Executors.newFixedThreadPool(files.length);

            for (int index = 0; index < files.length; index++) {
                final int NO = index;
                int num = index;

                Runnable run = new Runnable() {
                    public void run() {
                        try {
                            // 如果当前计数为零，则此方法立即返回。
                            // 等待
                            begin.await();
                            String filePath = files[NO];
                            if (filePath.startsWith("http")) {
                                URL url = new URL(filePath);
                                BufferedImage _image = ImageIO.read(url);
//                                images.add(_image);
                                imagesMap.put(num, _image);
                                System.out.println(filePath + "  " + num + " 完成");
                            }

                        } catch (Exception e) {
                            System.out.println("Runnable download ERROR:"+e.getLocalizedMessage());
                        } finally {
                            // 每个选手到达终点时，end就减一
                            end.countDown();
                        }
                    }
                };
                exec.submit(run);
            }
            System.out.println("DownLoad Images Start");
            // begin减一，开始游戏
            begin.countDown();
            // 等待end变为0，即所有选手到达终点
            end.await();
            System.out.println("DownLoad Images Over");
            exec.shutdown();
        } catch (Exception e) {
            System.out.println("downloadImages Error:" + e.getLocalizedMessage());
        }
        ArrayList<BufferedImage> images = new ArrayList<>();
        for(int i=0;i<files.length;i++){
            if(imagesMap.get(i)!=null) {
                images.add(imagesMap.get(i));
            }
        }

        System.out.println("downloadMap.size():" + imagesMap.size()+" images.size():" + images.size());
        return images;
    }

    public static String mergeImage(String[] files, int type, String targetFile) {
        int len = files.length;
        if (len < 1) {
            throw new RuntimeException("图片数量小于1");
        }

        ArrayList<BufferedImage> images = new ArrayList<>();

        images = downloadImages(files);

        int maxWidth = 600;
        int maxHeight = 600;

        ArrayList<Integer[]> xyList = new ArrayList();

        if (images.size() == 1) {
            maxHeight = maxWidth;
            Integer[] xy1 = {0, 0, maxWidth, maxHeight};
            xyList.add(xy1);
        }
        if (images.size() == 2) {
            maxHeight = maxWidth / 2;
            Integer[] xy1 = {0, 0, maxWidth / 2, maxWidth / 2};  //0,0
            Integer[] xy2 = {maxWidth / 2, 0, maxWidth / 2, maxWidth / 2}; //300,0
            xyList.add(xy1);
            xyList.add(xy2);

        }
        if (images.size() == 3) {
            maxHeight = maxWidth + (maxWidth / 2);
            Integer[] xy1 = {0, 0, maxWidth, maxWidth};  //0,0
            Integer[] xy2 = {0, maxWidth, maxWidth / 2, maxWidth / 2};  //0,300
            Integer[] xy3 = {maxWidth / 2, maxWidth, maxWidth / 2, maxWidth / 2}; //300,300
            xyList.add(xy1);
            xyList.add(xy2);
            xyList.add(xy3);
        }
        if (images.size() == 4) {
            maxHeight = maxWidth;
            Integer[] xy1 = {0, 0, maxWidth / 2, maxWidth / 2};  //0,0
            Integer[] xy2 = {0, maxWidth / 2, maxWidth / 2, maxWidth / 2};  //0,300
            Integer[] xy3 = {maxWidth / 2, 0, maxWidth / 2, maxWidth / 2};   //300,0
            Integer[] xy4 = {maxWidth / 2, maxWidth / 2, maxWidth / 2, maxWidth / 2};//300,300

            xyList.add(xy1);
            xyList.add(xy2);
            xyList.add(xy3);
            xyList.add(xy4);

        }
        if (images.size() == 5) {
            maxHeight = maxWidth + (maxWidth / 2) * 2;
            Integer[] xy1 = {0, 0, maxWidth, maxWidth};   //0.0
            Integer[] xy2 = {0, maxWidth, maxWidth / 2, maxWidth / 2};  //0,600
            Integer[] xy3 = {maxWidth / 2, maxWidth, maxWidth / 2, maxWidth / 2};  //300,600
            Integer[] xy4 = {0, maxWidth + maxWidth / 2, maxWidth / 2, maxWidth / 2};  //0,900
            Integer[] xy5 = {maxWidth / 2, maxWidth + maxWidth / 2, maxWidth / 2, maxWidth / 2};//300,900

            xyList.add(xy1);
            xyList.add(xy2);
            xyList.add(xy3);
            xyList.add(xy4);
            xyList.add(xy5);
        }
        if (images.size() == 6) {
            maxHeight = (maxWidth / 2) * 3;

            Integer[] xy1 = {0, 0, maxWidth / 2, maxWidth / 2};  //0,0
            Integer[] xy2 = {maxWidth / 2, 0, maxWidth / 2, maxWidth / 2};  //300,0

            Integer[] xy3 = {0, maxWidth / 2, maxWidth / 2, maxWidth / 2};   //0,300
            Integer[] xy4 = {maxWidth / 2, maxWidth / 2, maxWidth / 2, maxWidth / 2};  //300,300

            Integer[] xy5 = {0, maxWidth, maxWidth / 2, maxWidth / 2};   //0,600
            Integer[] xy6 = {maxWidth / 2, maxWidth, maxWidth / 2, maxWidth / 2}; //300,600

            xyList.add(xy1);
            xyList.add(xy2);
            xyList.add(xy3);
            xyList.add(xy4);
            xyList.add(xy5);
            xyList.add(xy6);
        }

        int newHeight = maxHeight;
        int newWidth = maxWidth;
        if (type == 1 && newWidth < 1) {
            return null;
        }
        if (type == 2 && newHeight < 1) {
            return null;
        }

        // 生成新图片
        try {
            BufferedImage ImageNew = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics graphics = ImageNew.getGraphics();

            for (int i = 0; i < xyList.size(); i++) {
                Integer[] xy = xyList.get(i);
                int x = xy[0];
                int y = xy[1];
                int w = xy[2];
                int h = xy[3];

                graphics.drawImage(images.get(i), x, y, w, h, null);
            }
            String watermark="No."+System.currentTimeMillis();
            Color color=new Color(0,0,0,128);
            graphics.setColor(color);
            graphics.setFont(new Font("Serif", Font.PLAIN, 10));
            graphics.drawString(watermark, 10, 12);


            //输出想要的图片
            ImageIO.write(ImageNew, targetFile.split("\\.")[1], new File(targetFile));

            return targetFile;

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }
}
