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
        <%-- struts2文件上传
        <s:form action="fileAction_upload" method="POST" enctype="multipart/form-data">
            <!--<input class="file-upload" name="resource" type="file"/>
            <input value="上传" type="submit"/>-->
            <div style="float: left; width: 8%; margin-left: 21%;">
                <input type="button" value="选择文件"/>
            </div>
            <div style="float: left; width: 50%;">
                <input id="fileUrl" name="type" type="text" class="td_input" value="未选择任何文件"
                       style="width: 100%; background: #E3E3E3; border: none;"/>
            </div>
            <div style="position:absolute;filter:alpha(opacity=1); -moz-opacity:.0; opacity:0.0; width: 39.4%; margin-left: -39%; display: inline;" id="realFileDiv">
                <input type="file" name="resource" onmouseover="this.style.cursor='pointer'"
                       onchange="document.getElementById('fileUrl').value= this.value=='' ? '未选择任何文件' : this.value.substring(this.value.lastIndexOf('\\')+1)" style="width: 100%;" />
            </div>
            <input value="上传" type="submit" style="margin-left: 1%;"/>
        </s:form> --%>
        <!-- webupload文件分片上传 -->
        <div id="uploader" class="wu-display">
            <!--用来存放文件信息-->
            <div id="thelist" class="uploader-list"></div>
            <div class="btns">
                <!--<input class="style_file_content" accept="video/mp4" type="file" id="upload_file_id"/>-->
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
