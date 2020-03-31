package com.zhang.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zhang.domain.UploadRecord;
import org.junit.Test;

import java.util.Date;
import java.util.List;

public class FileUtilTest {

    @Test
    public void testRecordsJson() {
        UploadRecord record = new UploadRecord("三体","123三体",
                StringUtils.simpleDate(new Date()), "12kb");
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        obj.put("records", arr);
        arr.add(record);
        System.out.println(obj.toString(SerializerFeature.PrettyFormat));
    }

    @Test
    public void testReadJson() {
        String jsons = "{\n" +
                "\t\"records\":[\n" +
                "\t\t{\n" +
                "\t\t\t\"name\":\"三体\",\n" +
                "\t\t\t\"size\":\"12kb\",\n" +
                "\t\t\t\"uploadDate\":\"/2020/03/22/\",\n" +
                "\t\t\t\"uuidName\":\"123三体\"\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}";
        if (jsons == null) jsons = "{}";
        JSONObject obj = JSON.parseObject(jsons);
        if (!obj.containsKey("records")) System.out.println("null");

        JSONArray arr = obj.getJSONArray("records");
        List<UploadRecord> list = arr.toJavaList(UploadRecord.class);
    }

    @Test
    public void testSaveRecord() {
        UploadRecord record = new UploadRecord("算法","算法333",
                StringUtils.simpleDate(new Date()), "12kb");
        FileUtils.saveRecord(record);
    }

    @Test
    public void testfindAllRecord() {
        List<UploadRecord> list = FileUtils.findAllRecords();
        System.out.println(JSON.toJSONString(list));
    }

    @Test
    public void fixFileNameTest() {
        String index = "0";
        String fixName = StringUtils.fiexLengthFileName(index);
        System.out.println(fixName);
    }
}
