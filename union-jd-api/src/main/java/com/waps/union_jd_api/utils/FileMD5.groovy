package com.waps.union_jd_api.utils

import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class FileMD5 {

    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

    public static String getFileMD5(File file) {
        try {
            InputStream stream = Files.newInputStream(file.toPath(), StandardOpenOption.READ)
            return getInputStreamMD5(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    // 输入流取MD5
    public static String getInputStreamMD5(InputStream stream) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buf = new byte[8192];
            int len;
            while ((len = stream.read(buf)) > 0) {
                digest.update(buf, 0, len);
            }
            return toHexString(digest.digest());
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String toHexString(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }

}
