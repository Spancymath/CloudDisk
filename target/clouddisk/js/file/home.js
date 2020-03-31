﻿//格式化字符
function byteFormater(size) {
    if (size == 0) return size;

    var formatSize = size;
    var unit = "byte";
    if (formatSize > 1024) {
        formatSize /= 1024;
        unit = "kb";
    }
    if (formatSize > 1024) {
        formatSize /= 1024;
        unit = "M";
    }
    if (formatSize > 1024) {
        formatSize /= 1024;
        unit = "G";
    }

    return Math.round(formatSize * 100) / 100 + unit;
}

//删除文件
function deleteFile(aObj, ahref) {
    if (!confirm("确定删除该文件？")) {
        aObj.href = "#";
        return false;
    }
    aObj.href = ahref;
    return true;
}

//页面初始化
$(document).ready(function () {
    //文件大小显示样式
    $(".byteFormater").each(function (index, element) {
        var size = element.innerHTML;
        $(element).html(byteFormater(size));
        $(element).css("visibility", "visible");
    });
    //a标签中文参数问题
    $(".oprTd a").each(function (index, element) {
        element.href = encodeURI(element.href);
    });

    //初始化文件上传
    //alert("uploader");
    $list = $('#thelist');
    $ctlBtn = $('#ctlBtn');
    $resetBtn = $('#resetBtn');
    state = 'pending';
    var GUID = WebUploader.Base.guid();//一个GUID
    //初始化WebUploader插件
    uploader = WebUploader.create({
        // swf文件路径， 需要修改为你自己存放的路径
        swf: '../thirdParty/Uploader.swf',
        // 文件接收服务端。  // 需要修改为你的后端地址
        server: 'fileAction_webUpload',
        // dnd 指定Drag And Drop拖拽的容器，如果不指定，则不启动
        // 禁用全局拖拽，否则在没有启动拖拽容器的情况下，视频拖进来后会直接在浏览器内播放。
        disableGlobalDnd: true,

        // 选择文件的按钮。可选。内部根据当前运行是创建，可能是input元素，也可能是flash.
        pick: {
            id: '#picker',                     // 对应 html 中的 picker
            innerHTML: '选择文件',   // 按钮上显示的文字
            multiple: false                  // 多文件选择
        },

        // 允许视频和图片类型的文件上传。
        // accept: {
        //     title: 'Video',
        //     extensions: 'mp4,gif,jpg,jpeg,bmp,png',      // 可以多个后缀，以逗号分隔， 不要有空格
        //     mimeTypes: 'video/*,image/*'
        // },

        // 只允许选择图片文件。
        //accept: {
        // title: 'Images',
        //  extensions: '',
        //  mimeTypes: ''
        //}

        // thumb配置生成缩略图的选项， 此项交由后台完成， 所以前台未配置

        // 自动上传暂时关闭，使用多文件队列上传， 如果值为true，那么在选择完文件后，将直接开始上传文件，因为我还要做一些其他处理，故选择false。
        auto: false,

        //是否允许在文件传输时提前把下一个文件准备好。 对于一个文件的准备工作比较耗时，比如图片压缩，md5序列化。 如果能提前在当前文件传输期处理，可以节省总体耗时。
        prepareNextFile: true,

        // 可选，是否要分片处理大文件上传
        chunked: true,
        // 如果要分片，分多大一片？这里我设置为2M, 如需更大值，可能需要需修改php.ini等配置
        chunkSize: 2*1024*1024,
        // 如果某个分片由于网络问题出错，允许自动重传多少次
        chunkRetry: 3,
        // 上传并发数，允许同时上传最大进程数，默认3
        threads: 5,

        // formData {Object} [可选] [默认值：{}] 文件上传请求的参数表，每次发送都会发送此对象中的参数。 其实就是post中的表单数据，可自定义字段。
        formData: {
            guid: GUID
        },
        //[可选] 验证文件总数量, 超出9个文件则不允许加入队列。
        fileNumLimit: 1,
        // 验证文件总大小是否超出限制（2G）, 超出则不允许加入队列。根据需要进行设置。除了前面几个，其它都是可选项
        fileSizeLimit: 1024*1024*1024*2,
        // 验证单个文件大小是否超出限制（2G）, 超出则不允许加入队列。
        fileSingleSizeLimit: 1024*1024*1024*2,
        // [可选] 去重， 根据文件名字、文件大小和最后修改时间来生成hash Key.
        duplicate: true,
        // 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
        // resize: false,
        // 压缩选项
        compress: {
            // 如果压缩后比源文件大，则不压缩，图片有可能压缩后比原文件还大，需设置此项
            noCompressIfLarger: true,
        },
    });

    //进度条
    $list.append( '<div style="position: relative; font-size: 12px;" class="item">' +
        '<p class="info"></p>' +
        '<p class="state"></p>' +
        '</div>' );

    // 以下都是监听事件， 方法中的file 和 response 参数，需了解，并

    // 当有文件被添加进队列的时候触发，用于显示加载进度条
    uploader.on( 'fileQueued', function( file ) {
        $(".uploader-list").css("visibility", "visible");
        // $list.append( '<div style="position: relative; font-size: 12px;" id="' + file.id + '" class="item">' +
        //     '<p class="info">' + file.name + '</p>' +
        //     '<p class="state">正在加载...</p>' +
        //     '</div>' );
        var $li = $('.uploader-list>div');
        // var $li = $( '#'+file.id );
        // // 生成文件的MD5值， 可以用来实现秒传， 如不需要，可以忽略（数据库中保存md5值，如果存在相同md5，直接在文件服务器复制一份，不需再次分片上传以及合并，极快）
        // uploader.md5File( file )
        // // 及时显示进度
        //     .progress(function(percentage) {
        //         $percent = $li.find('.state');
        //         $li.find('p.state').text('加载中 '+  Math.round(percentage * 100)  + '%');
        //         console.log('Percentage:', percentage);
        //     })
        //     // 完成
        //     .then(function(md5) {
        //         // 将md5值加入到post的表单数据formData中， 与上文中的 context 和 from字段相同
        //         uploader.option("formData",{
        //             "md5": md5
        //     });
        //     console.log('md5:', md5);
        // });
        $li.find('p.info').text(file.name);
        $li.find('p.state').text("加载完成，请上传").css( 'background-size', '0');
    });

    // 文件上传过程中创建进度条实时显示。
    // 显示进度条
    uploader.on( 'uploadProgress', function( file, percentage ) {
        var $li = $('.uploader-list>div'),
            $percent = $li.find('p.state');

        // 避免重复创建
        // if ( !$percent.length ) {
        //     $percent = $('<div class="progress progress-striped active">' +
        //         '<div class="progress-bar" role="progressbar" style="width: 0%">' +
        //         '</div>' +
        //         '</div>').appendTo( $li ).find('.progress-bar');
        // }

        $li.find('p.state').text('上传中 '+ Math.round(percentage * 100)  + '%' );
        if(Math.round(percentage * 100) == 100)
        {
            $li.find('p.state').text('即将完成...');
            //100的时候背景还没被填满
            percentage = 101;
        }

        $percent.css( 'background-size', percentage * 100 + '%' );
    });

    // 监听上传成功
    uploader.on( 'uploadSuccess', function( file ) {
        $.post('fileAction_merge', { guid: GUID, name: file.name }, function (data) {
            $('.uploader-list>div').find('p.state').text('已上传');
        });
    });
    // 监听上传失败
    uploader.on( 'uploadError', function( file ) {
        alert("上传出错")
        $( '#'+file.id ).find('p.state').text('上传出错');
    });
    // 监听上传完成，不论失败还是成功
    uploader.on( 'uploadComplete', function( file ) {
        $( '#'+file.id ).find('.progress').fadeOut();
        console.log(uploader.getStats());
    });
    $('#ctlBtn').click(function(){
        uploader.upload(); // 手动上传
    });
    $('#resetBtn').click(function(){
        alert(666)
        uploader.retry(); // 重新上传
    });

});

//分段下载文件

/**
 * 获取文件信息，进行分段下载
 * name：包含文件路径的文件名
 */
function downloadRequest(aElement, filePath) {
    if (downloading) {
        alert("正在下载，请稍侯");
        return false;
    }
    saveFileName = $(aElement).closest("tr").find("td").eq(0).text();

    //进度条
    $(".uploader-list").css("visibility", "visible");
    var $li = $('.uploader-list>div');
    $li.find('p.info').text(saveFileName);
    $li.find('p.state').text("正在加载...").css( 'background-size', '0');

    downloading = true;
    path = filePath;
    //请求文件
    var data = {
        path: path
    };
    //Ajax提交
    $.ajax({
        url: "fileAction_requestDownloadFile",
        type: "POST",
        data: data,
        dataType: "json",
        timeout: 36000,
        success: function (res) {
            if (res == '') {
                $('.uploader-list>div').find('p.state')
                    .text("出错了").css( 'background-size', '0');
                console.log(res);
                alert("出错了，尝试刷新页面，或下载ie/chrome的高版本浏览器重试");
                clearDownloadVar();
                return;
            } else {
                console.log(res);
                //拿到文件开始分片下载
                maxCount = res.count - 1 ;
                console.log(maxCount);
                blobArray = new Array(res.count);
                download();
                $li.find('p.state').text("下载中 0%");
            }
        }
    });
}

var maxDownloadThreadNum = 5;//最大下载线程数
var freeDownloadThread = maxDownloadThreadNum;//空闲线程数
var downloading = false;
var path;//存储路径
var saveFileName;//文件名
var maxCount = 0;
var index = 0;
var completedNum = 0;
var blobArray = [];//blob数组

//下载分区文件
function download() {
    console.log("chunk download-->index:" + index + ", completedNum: "
        + completedNum + ", freeTread:" + freeDownloadThread);
    var xhr = new XMLHttpRequest();
    var indexT = index;
    var str = "path=" + path + "&index=" + indexT;
    xhr.open('POST', "fileAction_downloadTrunkFile", true);    //也可以使用POST方式，根据接口
    xhr.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
    xhr.responseType = "blob";   //返回类型blob
    xhr.onload = function () {
        //定义请求完成的处理函数
        if (this.status === 200) {
            var downloadPersent = Math.round(completedNum / maxCount * 100);
            $('.uploader-list>div').find('p.state')
                .text("下载中 " + downloadPersent + "%").css( 'background-size', downloadPersent + '%');
            console.log(indexT + ":over");
            blobArray[indexT] = this.response;
            completedNum++;
            freeDownloadThread++;
            if (index <= maxCount) {
                download();
            } else if (completedNum == maxCount + 1) {
                $('.uploader-list>div').find('p.state')
                    .text("即将完成").css( 'background-size', 101 + '%');
                console.log("所有分片下载完成");
                var type = xhr.getResponseHeader('Content-Type');
                mergeToFile(type);
            }
        } else {
            $('.uploader-list>div').find('p.state')
                .text("出错了").css( 'background-size', '0');
            console.log("xhr error", this.status, "index: ", index);
            alert("出错了，尝试刷新页面，或下载ie/chrome的高版本浏览器重试");
            clearDownloadVar();
        }
    };
    xhr.send(str);
    index++;
    freeDownloadThread--;
    while (freeDownloadThread > 0 && index <= maxCount) {
        download();
    }
}

function mergeToFile(type) {
    console.log("合并分片文件");
    var blob = new Blob(blobArray, {type: type})
    if (typeof window.navigator.msSaveBlob !== 'undefined') {
        console.log("msSaveBlob 存储");
        window.navigator.msSaveBlob(blob, saveFileName);
    } else {
        var link = document.createElement('a');
        link.download = saveFileName;
        link.href = URL.createObjectURL(blob);
        document.body.appendChild(link);
        link.click();

        setTimeout(function () {
            window.URL.revokeObjectURL(link.href);
            document.body.removeChild(link);
            console.log("清除缓存link");
        }, 100);
    }

    $('.uploader-list>div').find('p.state')
        .text("下载完成");

    setTimeout(function () {
        clearDownloadVar();
        console.log("清除缓存");
    }, 100);
}

function clearDownloadVar() {
    downloading = false;
    path = "";//存储路径
    saveFileName = "";//文件名
    maxCount = 0;
    index = 0;
    blobArray = [];
    freeDownloadThread = maxDownloadThreadNum;
    completedNum = 0;
}