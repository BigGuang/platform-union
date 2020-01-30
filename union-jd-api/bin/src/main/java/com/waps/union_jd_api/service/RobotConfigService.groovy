package com.waps.union_jd_api.service

import com.waps.utils.StringUtils

class RobotConfigService {
    final static String configPath = "/home/white_list.txt"
    final static String blackWordsPath = "/home/black_words_list.txt"
    final static String blackChannelList = "/home/black_channel_list.txt"
    final static String badWordsDict="/home/dictionary.txt"

    static Boolean hasBlackWords(String content) {
        return isContentHasKey(content, getBlackWordsFile())
    }

    static addBlackWord(String w) {
        addKeyToFile(w, getBlackWordsFile())
    }

    static String getBlackWordList() {
        return getKeyList(getBlackWordsFile())
    }

    static removeBlackWord(String w) {
        removeKeyFromFile(w, getBlackWordsFile())
    }

    static Boolean isBlackChannel(String channelName) {
        return isKeyInFile(channelName, getBlackChannelListFile())
    }

    static addBlackChannel(String channelName) {
        addKeyToFile(channelName, getBlackChannelListFile())
    }

    static String getBlackChannelList() {
        return getKeyList(getBlackChannelListFile())
    }

    static removeBlackChannel(String channelName) {
        removeKeyFromFile(channelName, getBlackChannelListFile())
    }


    static Boolean isContentHasKey(String content, File configFile) {
        boolean flg = false
        if (!StringUtils.isNull(content)) {

            if (configFile.exists()) {
                configFile.eachLine { w ->
                    if (content.toLowerCase().indexOf(w.toLowerCase()) > -1) {
                        flg = true
                        return true
                    }
                }
            }
        }
        return flg
    }

    static Boolean isKeyInFile(String key, File configFile) {
        boolean flg = false
        if (configFile.exists()) {
            configFile.eachLine { line ->
                line = line.replaceAll("\n", "")
                if (line.toLowerCase().equals(key.toLowerCase())) {
                    flg = true
                    return true
                }
            }
        }
        return flg
    }

    static addKeyToFile(String key, File configFile) {
        if (!isKeyInFile(key, configFile)) {
            configFile.append(key.toLowerCase() + "\n")
        }
    }

    static String getKeyList(File configFile) {
        StringBuffer buffer = new StringBuffer()
        if (configFile.exists()) {
            configFile.eachLine { line ->
                line = line.replaceAll("\n", "")
                buffer.append(line + ',')
            }
        }
        return buffer.toString()
    }

    static removeKeyFromFile(String key, File configFile) {
        if (configFile.exists()) {
            StringBuffer buffer = new StringBuffer()
            configFile.eachLine { line ->
                line = line.replaceAll("\n", "")
                if (!line.equalsIgnoreCase(key)) {
                    buffer.append(line + "\n")
                }
            }
            configFile.write(buffer.toString())
        }
    }

    static File getBlackWordsFile() {
        File configFile = new File(blackWordsPath)
        return configFile
    }


    static File getBlackChannelListFile() {
        File configFile = new File(blackChannelList)
        return configFile
    }

    static File getConfigFile() {
        File configFile = new File(configPath)
        return configFile
    }
}
