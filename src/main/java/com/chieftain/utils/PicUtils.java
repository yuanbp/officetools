package com.chieftain.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class PicUtils {
    static BASE64Encoder encoder = new BASE64Encoder();
    static BASE64Decoder decoder = new BASE64Decoder();

    // 将图片转换成字符串
    public static String getImg2Binary(String fpath) throws Exception {
        FileInputStream fis = null;
        try {
            File f = new File(fpath);
            fis = new FileInputStream(f);
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);

            // 生成字符串
            String imgStr = byte2hex(bytes);
            return imgStr;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != fis) {
                fis.close();
            }
        }
        return null;
    }

    // 将字符串转换成二进制，用于显示图片
    public static void change2BianryToImg(String fpath, String imgstr) throws Exception {
        OutputStream o = null;
        InputStream in = null;
        try {
            // 将上面生成的图片格式字符串 imgStr，还原成图片显示
            o = new FileOutputStream(fpath);
            byte[] imgByte = hex2byte(imgstr);
            in = new ByteArrayInputStream(imgByte);
            byte[] b = new byte[1024];
            int nRead = 0;
            while ((nRead = in.read(b)) != -1) {
                o.write(b, 0, nRead);
            }
            o.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != o) {
                o.close();
            }
            if (null != in) {
                in.close();
            }
        }

    }

    /**
     * 二进制转字符串
     *
     * @param b byte数组
     * @return 二进制字符串
     */
    public static String byte2hex(byte[] b) {
        StringBuffer sb = new StringBuffer();
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) {
                sb.append("0" + stmp);
            } else {
                sb.append(stmp);
            }
        }
        return sb.toString();
    }

    /**
     * 字符串转二进制
     *
     * @param str 字符串
     * @return byte数组
     */
    public static byte[] hex2byte(String str) {
        if (str == null) {
            return null;
        }
        str = str.trim();
        int len = str.length();
        if (len == 0 || len % 2 == 1) {
            return null;
        }
        byte[] b = new byte[len / 2];
        try {
            for (int i = 0; i < str.length(); i += 2) {
                b[i / 2] = (byte) Integer.decode("0X" + str.substring(i, i + 2)).intValue();
            }
            return b;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * basebase64方式
     * 将图片转换为2进制字符串
     *
     * @return
     */
    public static String getImageBinary(String fpath) {
        File f = new File(fpath);
        BufferedImage bi;
        try {
            bi = ImageIO.read(f);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, "jpg", baos);
            byte[] bytes = baos.toByteArray();

            return encoder.encodeBuffer(bytes).trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将二进制字符串转换为图片
     *
     * @param base64String
     */
    public static void base64StringToImage(String base64String, String path) {
        try {
            byte[] bytes1 = decoder.decodeBuffer(base64String);

            ByteArrayInputStream bais = new ByteArrayInputStream(bytes1);
            BufferedImage bi1 = ImageIO.read(bais);
            File w2 = new File(path);//可以是jpg,png,gif格式    
            ImageIO.write(bi1, "jpg", w2);//不管输出什么格式图片，此处不需改动    
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        String imgPath = "H:\\input.jpg";
        String outImgPath = "H:\\output.jpg";
        String imgStr = getImg2Binary(imgPath);
        System.out.println(imgStr);
        change2BianryToImg(outImgPath, imgStr);
    }
}
