package com.zhang.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zhang.domain.UploadRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class FileUtils {
    //日志
    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

    //保存上传文件，并返回存放的路径
    public static String saveUploadFile(String basePath){
        //把日期格式化成字符串的一个帮助类
        SimpleDateFormat sdf = new SimpleDateFormat("/yyyy/MM/dd/");
        //把日期类型格式化为"/yyyy/MM/dd/"这种形式的字符串
        String subPath = sdf.format(new Date());
        //如果文件夹不存在，就创建文件夹
        File dir = new File(basePath+subPath);
        if(!dir.exists()){
            dir.mkdirs();
        }
        //String path = basePath+"/"+this.uploadFileName;
        //UUID.randomUUID().toString()能够保证名字的唯一性
        String path = basePath+subPath+ UUID.randomUUID().toString();
        //String path = basePath+subPath + fileName;
//        File dest = new File(path);
//        //把文件移动到dest处
//        upload.renameTo(dest);
        return  path;  //路径不包括文件的名字
    }

    public static boolean mergeFiles(File[] files, String toFile) {
        if (files == null) return false;
        Arrays.sort(files);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(toFile);
            FileInputStream input  = null;
            for (File file : files) {
                input = new FileInputStream(file);
                byte[] b = new byte[1024];
                int n;
                while((n=input.read(b))!=-1){
                    out.write(b, 0, n);
                }
                input.close();
                input = null;
                file.delete();
            }
            out.close();
            out=null;
        } catch (Exception e) {
            throw new RuntimeException("合并文件出错");
        }
        return true;
    }

    //上传记录保存位置
    private static String uploadRecords = "/uploads/records.json";

    //保存记录
    public static void saveRecord(UploadRecord record) {
        String jsons = readToString(uploadRecords);
        if (jsons == null) jsons = "{}";
        JSONObject obj = JSON.parseObject(jsons);
        JSONArray arr;
        if (obj.containsKey("records")) {
            arr = obj.getJSONArray("records");
        } else {
            arr = new JSONArray();
            obj.put("records", arr);
        }
        arr.add(0,record);
        writeToFile(obj.toString(SerializerFeature.PrettyFormat));
    }

    //从文件查询所有记录
    public static List<UploadRecord> findAllRecords() {
        String jsons = readToString(uploadRecords);
        if (jsons == null) jsons = "{}";
        JSONObject obj = JSON.parseObject(jsons);
        if (!obj.containsKey("records")) return null;

        JSONArray arr = obj.getJSONArray("records");
        List<UploadRecord> list = arr.toJavaList(UploadRecord.class);
        return list;
    }

    //删除一条记录
    public static int deleteRecords(String uuidName) {
        if (uuidName == null) return 0;
        String jsons = readToString(uploadRecords);
        if (jsons == null) return 0;
        JSONObject obj = JSON.parseObject(jsons);
        if (!obj.containsKey("records")) return 0;

        JSONArray arr = obj.getJSONArray("records");
        for (int i = 0; i < arr.size(); i++) {
            JSONObject object = arr.getJSONObject(i);
            if (uuidName.equals(object.getString("uuidName"))) {
                arr.remove(i);
                writeToFile(obj.toString(SerializerFeature.PrettyFormat));
                return 1;
            }
        }
        return 0;
    }

    //读出文件中的字符串
    private static String readToString(String file) {
        File exitFile = new File(uploadRecords);
        if (!exitFile.exists()) { // 如果文件不存在
            return null;
        }

        char cbuf[] = new char[10000];
        InputStreamReader input = null;
        try {
            input = new InputStreamReader(new FileInputStream(new File(file)),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int len = 0;
        try {
            len = input.read(cbuf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String text =new String(cbuf,0,len);
        return text;
    }

    //保存字符串到文件
    private static boolean writeToFile(String json) {
        // 生成json格式文件
        try {
            // 保证创建一个新文件
            File file = new File(uploadRecords);
            if (!file.getParentFile().exists()) { // 如果父目录不存在，创建父目录
                file.getParentFile().mkdirs();
            }
            if (file.exists()) { // 如果已存在,删除旧文件
                file.delete();
            }
            file.createNewFile();
            // 将格式化后的字符串写入文件
            Writer write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            write.write(json);
            write.flush();
            write.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //获取文件大小
    public static Long fileSize(String filePath) {
        File file = new File(filePath);
        return file.length();
    }
}
