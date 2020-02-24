package com.waps.union_jd_api.utils

import com.waps.utils.ConfigUtils

import java.util.regex.Matcher
import java.util.regex.Pattern

class FFMpegUtils {

    final static String FFMPEG_CMD = "ffmpeg"

    /**
     * 命令行处理基本方法
     * @param commands
     * @return
     */
    public static String RealTimeProcess(List<String> commands) {
        try {
            String testStr = "";
            for (int i = 0; i < commands.size(); i++) {
                String ss = commands.get(i)
                testStr = testStr + ss + " "
            }
            println testStr
            ProcessBuilder builder = new ProcessBuilder()
            builder.command(commands)
            final Process p = builder.start();
            //从输入流中读取视频信息
            BufferedReader brError = new BufferedReader(new InputStreamReader(p.getErrorStream()))
            StringBuffer sb = new StringBuffer()
            String line = ""
            while ((line = brError.readLine()) != null) {
                sb.append(line + "\n")
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n")
            }
            String retStr = sb.toString().replaceAll("  ", "")
            br.close()
            return retStr
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    public static String videoAutoZip(String video_filePath, String out_filePath) {
        File file = new File(video_filePath)
        if (!file.exists()) {
            System.err.println("路径[" + video_filePath + "]对应的视频文件不存在!")
        }
        // -i 0001.MOV -vcodec h264 -s 550*960 -r 25 -b 800000 test04.mp4
        VideoInfo videoInfo = getVideoINFO(video_filePath)
        int w = Integer.parseInt(videoInfo.getWidth())
        int h = Integer.parseInt(videoInfo.getHeight())
        String dpi = w + "*" + h
        if (h > w) {
            println "竖屏"
            if (w > 550) {
                dpi = "550*960"
            }
        } else {
            if (videoInfo.getRotate() != null && videoInfo.getRotate() != "") {
                println "竖屏"
                dpi = "550*960"
            } else {
                println "横屏"
                if (w > 960) {
                    dpi = "960*550"
                }
            }
        }

        List<String> commands = new java.util.ArrayList<String>()
        commands.add(FFMPEG_CMD)
        commands.add("-i")
        commands.add(video_filePath)
        commands.add("-vcodec")
        commands.add("h264")
        commands.add("-s")
        commands.add(dpi)
        commands.add("-r")
        commands.add("25")
        commands.add("-b")
        commands.add("800000")
        commands.add(out_filePath)
        return RealTimeProcess(commands)
    }

    /**
     * 自动截屏，最多8张
     * @param video_path
     * @return
     */
    public static String getAutoScreenShot(String video_path, String outPath) {
        File file = new File(video_path)
        if (!file.exists()) {
            System.err.println("路径[" + video_path + "]对应的视频文件不存在!")
        }
        int allMS = getVideoINFO(video_path).getTimeMS()
        int num = 8
        int stepNum = 1 //步长
        if (allMS >= num) {
            stepNum = allMS / num
        }
        String ss = "1/" + (stepNum + 1)
        println ss
        println "stepNum:" + stepNum
        println "allMS:" + allMS
        String fileName = file.getName().substring(0, file.getName().indexOf("."))
        for (int i = 0; i < num; i++) {
            int ms = i * stepNum + 1
            if (ms > allMS) {
                ms = allMS - 1
            }
            println ms
            //-y -ss 1 -t 0.001 -i  1001.mp4  -f  image2  1001_01.jpg
            List<String> commands = new java.util.ArrayList<String>()
            commands.add(FFMPEG_CMD)
            commands.add("-y")
            commands.add("-ss")
            commands.add(ms + "")
            commands.add("-t")
            commands.add("0.001")
            commands.add("-i")
            commands.add(video_path)
            commands.add("-f")
            commands.add("image2")
            commands.add(outPath + i + ".jpg")
            RealTimeProcess(commands)
        }


//        println ss
//
//        List<String> commands = new java.util.ArrayList<String>()
//        commands.add(ffmpeg_path)
//        commands.add("-i")
//        commands.add(video_path)
//        commands.add("-y")
//        commands.add("-f")
//        commands.add("image2")
//        commands.add("-vf")
//        commands.add("fps=fps=" + ss + "")
//        commands.add("/Users/xguang/test/video/" + file.getName().substring(0, file.getName().indexOf(".")) + "_%03d.jpeg")
//        return RealTimeProcess(commands)
    }

    /**
     * 获取视频信息，通过正则转换到VideoInfo类中
     * @param video_path
     * @return
     */
    public static VideoInfo getVideoINFO(String video_path) {
        VideoInfo videoInfo = new VideoInfo()
        List<String> commands = new java.util.ArrayList<String>()
        commands.add(FFMPEG_CMD)
        commands.add("-i")
        commands.add(video_path)
        try {
            String infoStr = RealTimeProcess(commands)


            String regexDuration = "Duration: (.*?), start: (.*?), bitrate: (\\d*) kb\\/s"
            String regexVideo = "Video: (.*?), (.*?), (\\d*)x(\\d*)"
            String regexFPS = "(\\d*|\\d*.\\d*) fps"
            String regexRotate = "rotate: (\\d*)"
            String regexAudio = "Audio: (.*?), (\\d*) Hz"

            Pattern patternDuration = Pattern.compile(regexDuration)
            Matcher m1 = patternDuration.matcher(infoStr)
            if (m1.find()) {
                int time = getTimelen(m1.group(1))
                videoInfo.setTimeMS(time)
                videoInfo.setTimeOrg(m1.group(1))
                videoInfo.setStartTime(m1.group(2))
                videoInfo.setVideoBitrate(m1.group(3))
            }

            Pattern patternVideo = Pattern.compile(regexVideo)
            Matcher m2 = patternVideo.matcher(infoStr)
            if (m2.find()) {
                videoInfo.setVideoCode(m2.group(1))
                videoInfo.setVideoFormat(m2.group(2))
                videoInfo.setVideoResolution(m2.group(3) + "x" + m2.group(4))

                videoInfo.setWidth(m2.group(3))
                videoInfo.setHeight(m2.group(4))
            }

            Pattern patternAudio = Pattern.compile(regexAudio)
            Matcher m3 = patternAudio.matcher(infoStr)
            if (m3.find()) {
                videoInfo.setAudioCode(m3.group(1))
                videoInfo.setAudioSampleRate(m3.group(2))
            }

            Pattern patternWidth = Pattern.compile(regexFPS)
            Matcher m4 = patternWidth.matcher(infoStr)
            if (m4.find()) {
                videoInfo.setVideoFPS(m4.group(1))
            }

            Pattern patternRotate = Pattern.compile(regexRotate)
            Matcher m5 = patternRotate.matcher(infoStr)
            if (m5.find()) {
                videoInfo.setRotate(m5.group(1))
            }
            return videoInfo
        } catch (Exception e) {
            e.printStackTrace()
        }
        return videoInfo
    }

    //格式:"00:00:10.68"
    private static int getTimelen(String timelen) {
        int min = 0;
        String[] strs = timelen.split(":");
        if (strs[0] > "0") {
            min += Integer.valueOf(strs[0]) * 60 * 60;//秒
        }
        if (strs[1] > "0") {
            min += Integer.valueOf(strs[1]) * 60;
        }
        if (strs[2] > "0") {
            min += Math.round(Float.valueOf(strs[2]));
        }
        return min;
    }

    //格式 1024x768
    private static String getWidth(String str) {
        if (str != null && str.indexOf("x") > -1) {
            return str.split("x")[0]
        }
    }

    private static String getHeight(String str) {
        if (str != null && str.indexOf("x") > -1) {
            return str.split("x")[1]
        }
    }
}

class VideoInfo {
    int timeMS
    String timeOrg  //提取出播放时间
    String startTime  //开始时间
    String videoBitrate   //码率 单位 kb
    String videoCode   //编码格式
    String videoFormat  //视频格式
    String videoResolution   //分辨率
    String videoFPS   //帧率
    String audioCode   //音频编码
    String audioSampleRate    //音频采样率 kHz
    String width
    String height
    String rotate
}

