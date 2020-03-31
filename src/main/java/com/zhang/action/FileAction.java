package com.zhang.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.zhang.domain.UploadRecord;
import com.zhang.util.FileUtils;
import com.zhang.util.StringUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

//文件上传、下载
public class FileAction extends ActionSupport {
    //日志
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    //文件名
    private File file;
    //类型
    private String resourceContentType;
    //名称
    private String resourceFileName;
    //保存路径
    private String savePath;

    //下载
    //文件名
    private String fileName;
    //路径
    private String filePath;

    //首页
    public String home() {
        logger.info("访问首页");
        List<UploadRecord> records = FileUtils.findAllRecords();
        ActionContext.getContext().put("uploadRecords", records);
        logger.info("加载文件列表，文件数为：{}", records == null ? 0 : records.size());
        return "home";
    }

    //文件上传
    public String upload() {
        logger.info("上传文件");
        if (file == null) {
            logger.info("上传资源为空，返回主页");
            return "toHome";
        }

        logger.info("保存文件");
        String path = FileUtils.saveUploadFile(savePath);
        File dest = new File(path);
        //把文件移动到dest处
        file.renameTo(dest);

        logger.info("保存记录");
        UploadRecord record = new UploadRecord();
        record.setName(this.resourceFileName);
        record.setUuidName(path);
        record.setUploadDate(StringUtils.simpleDate(new Date()));
        record.setSize(String.valueOf(FileUtils.fileSize(path)));
        logger.info("详细信息: {}" , JSON.toJSONString(record));
        FileUtils.saveRecord(record);
        return "toIndex";
    }

    //删除文件
    public String delete() {
        logger.info("删除文件：{}", filePath);
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            logger.error("文件不存在");
            FileUtils.deleteRecords(filePath);
            logger.info("删除记录");
            ActionContext.getContext().put("uploadErrorMessage", "该文件不存在");
            return "toIndex";
        }
        //文件所在的目录
        File parentFile = file.getParentFile();
        //删除文件
        boolean deleteFileFlag = file.delete();
        //删除文件导致文件夹为空则删除
        while (!savePath.replace('/', '\\').equals(parentFile.getPath()) && parentFile.listFiles().length <= 0) {
            File temp = parentFile;
            parentFile = parentFile.getParentFile();
            logger.info("删除文件夹：" + temp.getPath());
            temp.delete();
        }
        int deleteRecordNum = FileUtils.deleteRecords(filePath);
        logger.info("删除文件数为：{}", deleteRecordNum);
        if (deleteFileFlag || deleteRecordNum != 0) {
            ActionContext.getContext().put("uploadErrorMessage", "已删除");
        } else {
            ActionContext.getContext().put("uploadErrorMessage", "该文件不存在");
        }
        return "toHome";
    }

    //下载
    public String download() {
        logger.info("下载文件：{}", filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            logger.error("该文件不存在");
            ActionContext.getContext().put("uploadErrorMessage", "该文件不存在");
            return "toHome";
        }
        return "download";
    }

    //为下载文件写的
    public InputStream getDownloadFile() {
        File file = new File(filePath);
        FileInputStream inputStream = null;
        if (file.exists()) {
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                //解决中文文件名不对的问题
                this.fileName = URLEncoder.encode(fileName, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return inputStream;
        }
        return null;
    }

    //利用webupload实现文件分片上传，前端js设置分片大小
    public String webUpload() {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        //当前分块序号
        String index = request.getParameter("chunk");
        index = StringUtils.fiexLengthFileName(index);

        //前端传来的GUID号
        String guid = request.getParameter("guid");
        //临时保存分块的目录
        String tempDir = FilenameUtils.concat(savePath + "/temp", guid);
        logger.info("分块临时文件路径：{}, 分块数：{}", tempDir, index);
        File tempFilePath = new File(tempDir);
        if (!tempFilePath.exists()) {
            tempFilePath.mkdirs();
        }
        //分块文件名为索引名，更严谨一些可以加上是否存在的判断，防止多线程时并发冲突
        String tempFile = FilenameUtils.concat(tempDir, index);
        File fileT = new File(tempFile);
        file.renameTo(fileT);

        PrintWriter writer = null;
        try {
            writer = response.getWriter();
        } catch (IOException e) {
            throw new RuntimeException("内部错误");
        }
        //返回给ajax数据
        writer.println("0");
        logger.info("上传文件块{}成功", index);
        writer.flush();
        writer.close();
        return null;
    }

    //合并分片上传的文件
    public void merge()
    {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        //文件名
        String name = request.getParameter("name");
        //前端传来的GUID号
        String guid = request.getParameter("guid");
        String tempDir = FilenameUtils.concat(savePath + "/temp", guid);
        File fileDir = new File(tempDir);
        logger.info("合并分片文件：{}", tempDir);

        //合并
        File[] files = fileDir.listFiles();
        String realPath = FileUtils.saveUploadFile(savePath);
        FileUtils.mergeFiles(files, realPath);

        logger.info("保存记录");
        UploadRecord record = new UploadRecord();
        record.setName(name);
        record.setUuidName(realPath);
        record.setUploadDate(StringUtils.simpleDate(new Date()));
        record.setSize(String.valueOf(FileUtils.fileSize(realPath)));
        logger.info("详细信息: {}" , JSON.toJSONString(record));
        FileUtils.saveRecord(record);

        //删除文件夹
        if (fileDir.listFiles().length <= 0) fileDir.delete();
        //删除父文件夹
        File temp = fileDir.getParentFile();
        if (temp.listFiles().length <= 0) temp.delete();

        PrintWriter writer = null;
        try {
            writer = response.getWriter();
        } catch (IOException e) {
            throw new RuntimeException("内部错误");
        }
        //返回给ajax数据
        writer.println("0");
        logger.info("合并分片文件成功");
        writer.flush();
        writer.close();
    }

    //分片文件大小，下载
    private static int trunkSize = 1 * 1024 * 1024;
    //请求下载文件信息
    public String requestDownloadFile() {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        //文件名--存储的文件名，包含路径
        String path = request.getParameter("path");

        logger.info("分片下载，获取文件信息，文件路径：{}", path);
        File realFile = new File(path);
        if (!realFile.exists()) {
            logger.error("该文件不存在");
        }

        long fileSize = realFile.length();
        long count = fileSize / trunkSize;
        if (fileSize % trunkSize != 0) count += 1;

        JSONObject object = new JSONObject();
        object.put("count", count);
        object.put("trunksize", trunkSize);
        object.put("filesize", fileSize);

        PrintWriter writer = null;
        try {
            writer = response.getWriter();
        } catch (IOException e) {
            throw new RuntimeException("内部错误");
        }
        //返回给ajax数据
        writer.println(object);
        logger.info("获取文件信息成功：{}", object);
        writer.flush();
        writer.close();

        return null;
    }

    //按序号下载文件
    public String downloadTrunkFile() {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        response.setContentType("application/octet-stream");
        //文件名--存储的文件名，包含路径
        String path = request.getParameter("path");
        //分片序号
        String index = request.getParameter("index");

        if (path == null || index == null) {
            logger.error("下载分片文件参数错误");
            return null;
        }

        int indexInt = Integer.parseInt(index);
        File realFile = new File(path);
        if (!realFile.exists()) {
            logger.error("该文件不存在");
            return null;
        }
        long fileSize = realFile.length();
        long count = fileSize / trunkSize;
        if (fileSize % trunkSize != 0) count += 1;

        if (indexInt >= count) {
            logger.error("文件分片号{}错误，总文件分片数{}", index, count);
            return null;
        }

        FileInputStream inputStream = null;
        ServletOutputStream os = null;
        try {
            os = response.getOutputStream();
            inputStream = new FileInputStream(path);
            inputStream.skip(trunkSize * indexInt);
            long len = trunkSize;
            if (indexInt == count - 1) {
                len = fileSize - trunkSize * (count - 1);
            }
            //从文件流中读指定长度的片段输出
            byte[] buf = new byte[1024];
            while (len > 0) {
                inputStream.read(buf);
                long l = len > 1024 ? 1024 : len;
                os.write(buf, 0, (int) l);
                os.flush();
                len -= l;
            }
        } catch (Exception e) {
            throw new RuntimeException("分片下载文件操作出错");
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getResourceContentType() {
        return resourceContentType;
    }

    public void setResourceContentType(String resourceContentType) {
        this.resourceContentType = resourceContentType;
    }

    public String getResourceFileName() {
        return resourceFileName;
    }

    public void setResourceFileName(String resourceFileName) {
        this.resourceFileName = resourceFileName;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
