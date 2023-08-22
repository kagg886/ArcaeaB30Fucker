package com.kagg886.fuck_arc_b30.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.kagg886.fuck_arc_b30.server.model.Result;
import com.kagg886.fuck_arc_b30.server.model.SingleSongData;
import com.kagg886.fuck_arc_b30.server.servlet.AbstractServlet;
import com.kagg886.fuck_arc_b30.server.servlet.impl.AssetsGet;
import com.kagg886.fuck_arc_b30.server.servlet.impl.Image;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 文件操作类
 *
 * @author kagg886
 * @date 2023/7/12 22:34
 **/
@SuppressWarnings("all")
public class IOUtil {

    public static final String base = "http://localhost:61616";
    private static final FileFilter emptyFilter = pathname -> true;

    public static <T> T fetch(AbstractServlet url, Class<T> resultClass) throws IOException {
        Connection.Method method = switch (url.getMethod()) {
            case GET -> Connection.Method.GET;
            case POST -> Connection.Method.POST;
        };
        String body = Jsoup.connect(base + url.getPath())
                .ignoreContentType(true)
                .method(method)
                .timeout(5000)
                .execute()
                .body();
        Log.d(IOUtil.class.getName(), "fetch:" + url.getPath() + "'s result is:" + body);
        try {
            JSONObject json = JSON.parseObject(body);
            if (json.getIntValue("code") != Result.OK().getCode()) {
                throw new IOException(json.getString("msg"));
            }
            return JSON.parseObject(json.getJSONObject("data").toString(), resultClass);
        } catch (Exception e) {
            if (resultClass == String.class) {
                return (T) body;
            }
        }
        return null;
    }

    public static Bitmap loadArcaeaResource(String path) throws IOException {
        Connection.Response response = Jsoup.connect(base + AssetsGet.INSTANCE.getPath())
                .ignoreContentType(true)
                .method(AssetsGet.INSTANCE.getMethod() == AbstractServlet.Method.POST ? Connection.Method.POST : Connection.Method.GET)
                .timeout(5000)
                .data("path", path)
                .execute();
        return BitmapFactory.decodeStream(response.bodyStream());
    }

    public static Bitmap loadArcaeaSongImage(SingleSongData data) throws IOException {
        if (data.getId() == null) {
            Log.d(IOUtil.class.getName(), data + "is illegal");
        }
        Connection conn = Jsoup.connect(base + Image.INSTANCE.getPath())
                .ignoreContentType(true)
                .method(Image.INSTANCE.getMethod() == AbstractServlet.Method.POST ? Connection.Method.POST : Connection.Method.GET)
                .timeout(5000)
                .data("id", data.getId())
                .data("difficulty", data.getDifficulty().name());
        Bitmap res = BitmapFactory.decodeStream(conn.execute().bodyStream());
        if (res == null) {
            conn = Jsoup.connect(base + Image.INSTANCE.getPath())
                    .ignoreContentType(true)
                    .method(Image.INSTANCE.getMethod() == AbstractServlet.Method.POST ? Connection.Method.POST : Connection.Method.GET)
                    .timeout(5000)
                    .data("id", data.getId());
            //防止prs,pst,byd曲绘获取失败问题
            res = BitmapFactory.decodeStream(conn.execute().bodyStream());
        }
        //| *id        | 一个字符串，代表要查询的id                   |
        //| ---------- | -------------------------------------------- |
        //| difficulty | 难度，可以填写难度和难度代号，默认为future。 |
        //| size       | 大小，可填写256和512，默认为256              |
        return res;
    }

    public static void zipFile(File src, File dst) throws IOException {
        if (!dst.exists()) {
            dst.createNewFile();
        }
        ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(dst));
        if (src.isDirectory()) {
            zipFile(outputStream, src, src);
        } else {

        }
        outputStream.flush();
        outputStream.close();
    }

    private static void zipFile(ZipOutputStream outputStream, File base, File file) throws IOException {
        if (!file.isDirectory()) {
            String filePath = file.getAbsolutePath().replace(base.getAbsolutePath() + "/", "");
            outputStream.putNextEntry(new ZipEntry(filePath));
            outputStream.write(IOUtil.loadByteFromFile(file));
        } else {
            for (File k : file.listFiles()) {
                zipFile(outputStream, base, k);
            }
        }
    }

    public static String getException(Throwable e) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        return writer.toString();
    }

    /*
     * 删除文件
     * */
    public static void delFile(File f) {
        delFile(f, emptyFilter);
    }

    /*
     * @param f:
     * @return void
     * @author kagg886
     * @description 删除文件，但是有过滤器
     * @date 2023/03/13 17:12
     */
    public static void delFile(File f, FileFilter filter) {
        if (!f.isDirectory()) {
            f.delete();
            return;
        }
        for (File t : f.listFiles(filter)) {
            delFile(t);
        }
        f.delete();

    }

    /*
     * 从流中读取所有字节 花费11922ms左右 3.6mb 3万行
     * */

//    public static byte[] loadByteFromStream(InputStream stream) throws IOException {
//        long start = System.currentTimeMillis();
//        ByteArrayOutputStream output = new ByteArrayOutputStream();
//        int by;
//        while ((by = stream.read()) != -1) {
//            output.write(by);
//        }
//
//        stream.close();
//        output.close();
//        System.out.println("第一种方式读取完成");
//        System.out.println(System.currentTimeMillis() - start);
//        return output.toByteArray();
//    }

    public static String loadStringFromStream(InputStream stream) throws IOException {
        return new String(loadByteFromStream(stream));
    }

    /*
     * 尝试高效读取 花费46ms左右 3.6mb 3万行
     */

    //Hamusuta0320 NB
    public static byte[] loadByteFromStream(InputStream is) throws IOException {
        ReadableByteChannel readableByteChannel = Channels.newChannel(is);
        ByteBuffer bf = ByteBuffer.allocate(8192);
        ByteArrayOutputStream sb = new ByteArrayOutputStream();
        int read;
        while ((read = readableByteChannel.read(bf)) != -1) {
            bf.flip();
            sb.write(bf.array(), 0, read);
            bf.clear();
        }
        readableByteChannel.close();
        byte[] res = sb.toByteArray();
        sb.close();
        return res;
    }


    /*
     *读取文件
     * */

    public static byte[] loadByteFromFile(File file) throws IOException {
        FileInputStream stream;
        try {
            stream = new FileInputStream(file);
        } catch (Exception e) {
            return new byte[0];
        }
        return loadByteFromStream(stream);
    }

    public static String loadStringFromFile(File file) throws IOException {
        return new String(loadByteFromFile(file), StandardCharsets.UTF_8);
    }


    public static void writeByteToFile(File file, byte[] byt) throws IOException {
        FileOutputStream stream;
        try {
            stream = new FileOutputStream(file);
        } catch (Exception e) {
            file.getParentFile().mkdirs();
            if (!file.createNewFile()) {
                throw e;
            }
            writeByteToFile(file, byt);
            return;
        }
        stream.write(byt);
        stream.close();
    }

    public static void writeStringToFile(File file, String content) throws IOException {
        writeByteToFile(file, content.getBytes(StandardCharsets.UTF_8));
    }
}