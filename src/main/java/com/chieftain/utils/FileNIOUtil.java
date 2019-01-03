package com.chieftain.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class FileNIOUtil {

    /**
     * @param file
     * @return
     * @throws Exception
     */
    public static String read(String file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        // 文件通道
        FileChannel fc = fis.getChannel();
        // 分配和文件同等的缓存区
        ByteBuffer bf = ByteBuffer.allocate((int) fc.size());
        // 文件内容读入缓冲区
        fc.read(bf);
        // 缓冲中位置回复零
        bf.rewind();
        StringBuilder sb = new StringBuilder();
        while (bf.hasRemaining()) {
            sb.append((char) bf.get());
        }
        // 关闭文件通道
        fc.close();
        // 关闭文件输入流
        fis.close();
        return sb.toString();
    }

    /**
     * @param file
     * @return
     * @throws Exception
     */
    public static String read2(String file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        // 文件通道
        FileChannel fc = fis.getChannel();
        // 分配和文件同等的缓存区
        ByteBuffer bf = ByteBuffer.allocate((int) fc.size());
        // 文件内容读入缓冲区
        fc.read(bf);
        // 缓冲中位置回复零
        bf.rewind();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bf.capacity(); i++) {
            sb.append((char) bf.get());
        }
        // 关闭文件通道
        fc.close();
        // 关闭文件输入流
        fis.close();
        return sb.toString();
    }

    /**
     * @param file
     * @return
     * @throws Exception
     */
    public static String read3(String file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        // 文件通道
        FileChannel fc = fis.getChannel();
        // 分配缓存区
        ByteBuffer bf = ByteBuffer.allocate(512);
        StringBuilder sb = new StringBuilder();
        while (fc.read(bf) != -1) {
            //当缓冲区的 limit 设置为之前 position 值时，把缓冲中当前位置回复为零，
            bf.flip();
            while (bf.hasRemaining()) {
                sb.append((char) bf.get());
            }
            // 清理缓冲区，准备再次读取数据
            bf.clear();
        }
        // 缓冲中位置回复零
        bf.rewind();
        // 关闭文件通道
        fc.close();
        // 关闭文件输入流
        fis.close();
        return sb.toString();
    }

    /**
     * @param file
     * @return
     * @throws Exception
     */
    public static String read4(String file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        // 文件通道
        FileChannel fc = fis.getChannel();
        // 映射文件到内存
        MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
        byte[] bt = mbb.array();
        mbb.rewind();
        // 关闭文件通道
        fc.close();
        // 关闭文件输入流
        fis.close();
        return new String(bt);
    }

    /**
     * @param file
     * @param context
     * @throws Exception
     */
    public static void write(String file, String context) throws Exception {
        Charset charset = Charset.forName("UTF-8");//Java.nio.charset.Charset处理了字符转换问题。它通过构造CharsetEncoder和CharsetDecoder将字符序列转换成字节和逆转换。
        CharsetDecoder decoder = charset.newDecoder();

        FileOutputStream fos = new FileOutputStream(file);
        // 文件通道
        FileChannel fc = fos.getChannel();
        int len = context.getBytes().length;
        // 缓冲区
        ByteBuffer bf = ByteBuffer.allocate(len * 2);
        // 放入缓冲区
        for (int j = 0; j < context.length(); j++) {
            bf.putChar(context.charAt(j));
        }
        bf.flip();
        // 缓冲区数据写入到文件中
        fc.write(bf);
        // 关闭文件通道
        fc.close();
        //关闭文件输出流
        fos.close();
    }

    /**
     * @param file
     * @param context
     * @throws Exception
     */
    public static void write2(String file, String context) throws Exception {
        FileOutputStream fos = new FileOutputStream(file);
        // 文件通道
        FileChannel fc = fos.getChannel();
        // 缓冲区
        ByteBuffer bf = ByteBuffer.allocate(512);

        while (bf.limit() < bf.position()) {
            // 放入缓冲区
            for (int j = 0; j < context.length(); j++) {
                bf.putChar(context.charAt(j));
            }
            // 缓冲区数据写入到文件中
            fc.write(bf);
            bf.flip();
        }
        // 关闭文件通道
        fc.close();
        //关闭文件输出流
        fos.close();
    }

    public static String readFile(String filePath) throws Exception {
        Charset charset = Charset.forName("UTF-8");//Java.nio.charset.Charset 处理了字符转换问题。它通过构造 CharsetEncoder 和 CharsetDecoder 将字符序列转换成字节和逆转换。
        CharsetDecoder decoder = charset.newDecoder();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
            FileChannel fileChannel = fis.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            CharBuffer charBuffer = CharBuffer.allocate(1024);
            StringBuffer stringBuffer = new StringBuffer();
            int bytes = fileChannel.read(byteBuffer);
            while (bytes != -1) {
                byteBuffer.flip();
                decoder.decode(byteBuffer, charBuffer, false);
                charBuffer.flip();
                stringBuffer.append(charBuffer);
                charBuffer.clear();
                byteBuffer.clear();
                bytes = fileChannel.read(byteBuffer);
            }
            if (fis != null) {
                fis.close();
            }
            return stringBuffer.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * writeFile
     * @param content
     * @param fileName
     * @throws IOException
     */
    private static void writeFile(String content, String folder, String fileName) throws IOException {
        File f = new File(folder);
        f.setWritable(true);
        if (!f.exists()) {  // 如果该路径不存在，就创建该路径
            f.mkdir();
        }
        String filePath = folder + "/" + fileName;  // 得到完整文件路径
        FileOutputStream fos = null;
        FileChannel fc_out = null;
        try {
            fos = new FileOutputStream(filePath, true);
            fc_out = fos.getChannel();
            ByteBuffer buf = ByteBuffer.wrap(content.getBytes("UTF-8"));
            buf.put(content.getBytes());
            buf.flip();
            fc_out.write(buf);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fc_out) {
                fc_out.close();
            }
            if (null != fos) {
                fos.close();
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
//        String text = read("C:\\DEMO.java");
//        String text2 = read2("C:\\DEMO.java");
//        String text3 = read3("C:\\DEMO.java");
//        System.out.println(text3);
//        write("C:\\DEMO2.java",text);
//        write("C:\\DEMO3.java",text2);
//        String text4 = read4("C:\\DEMO.java");
//        System.out.println(text4);

        System.out.println(readFile("F:\\桌面文件\\note.txt"));
        writeFile(readFile("F:\\桌面文件\\note.txt"),"F:\\桌面文件","note2.txt");
    }

}