<%--
  Created by IntelliJ IDEA.
  User: Mather
  Date: 2020/3/21
  Time: 21:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" pageEncoding="UTF-8" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>简单云盘</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/file/webuploader.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/file/home.css"/>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/thirdParty/jquery-3.4.1.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/thirdParty/webuploader.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/file/home.js"></script>
</head>
<body>
    <div id="fileUploadDiv">
        <!-- webupload文件分片上传 -->
        <div id="uploader" class="wu-display">
            <!--用来存放文件信息-->
            <div id="thelist" class="uploader-list"></div>
            <div class="btns">
                <div id="picker">选择文件</div>
                <button id="ctlBtn" class="btn btn-default" >开始上传</button>
                <button id="resetBtn" class="btn btn-default" style="display: none;">重试</button>
            </div>
        </div>
    </div>
    <div id="errorMessage">
        <s:property value="#uploadErrorMessage" default=""/>
    </div>
    <div id="uploadListDiv">
        <table id="fileListTable">
            <thead>
                <tr>
                    <th>文件名</th>
                    <th>上传时间</th>
                    <th>文件大小</th>
                    <th>操作</th>
                </tr>
            </thead>
            <tbody>
                <s:iterator value="uploadRecords">
                    <tr>
                        <td class="nameTd"><s:property value="name"></s:property></td>
                        <td class="normalTd"><s:property value="uploadDate"></s:property></td>
                        <td class="byteFormater sizeTd" style="visibility: hidden"><s:property value="size"></s:property></td>
                        <td class="oprTd">
                            <a href="#" onclick="downloadRequest(this, '<s:property value="uuidName"></s:property>')">下载</a>
                            <a href="#" onclick="deleteFile(this, 'fileAction_delete?filePath=<s:property value="uuidName"></s:property>')">删除</a>
                        </td>
                    </tr>
                </s:iterator>
            </tbody>
        </table>
    </div>
</body>
</html>
