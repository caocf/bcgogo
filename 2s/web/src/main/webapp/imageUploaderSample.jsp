<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>image uploader sample</title>
    <meta http-equiv="content-type" content="text/html;charset=utf-8"/>
    <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
    <link rel="stylesheet" href="js/extension/uploadify/uploadify.css"/>
    <link rel="stylesheet" href="js/components/themes/bcgogo-imageUploader.css"/>

    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="js/base.js"></script>
    <script type="text/javascript" src="js/application.js"></script>

    <%@include file="/WEB-INF/views/image_script.jsp" %>
    <script type="text/javascript" src="js/components/ui/bcgogo-imageUploader.js"></script>

    <script type="text/javascript">
        // 如果你需要 动态改变 flash 的地图 请调用如下 代码
        // imageUploader.getFlashObject().resetAddButton(isTransparent, normalUrl, overUrl, width, height);


        // 获取得到的 上传图片数据
        var uploadedData = [];
        $(window).bind("load", function() {

            var imageUploader = new App.Module.ImageUploader();

            imageUploader.init({
                "selector":".group-button-imageuploader",
                "flashvars":{
                    "debug":"off",
                    "transparent":"false",
                    "maxFileNum":"5",
                    "currentItemNum": 0, // 看这个看这个
                    "width":61,
                    "height":24,
                    "buttonBgUrl":"images/imageUploader.png",
                    "buttonOverBgUrl":"images/imageUploader.png"
                },
                "selectFileCallback":function(message) {
                    G.warning("文件已选择!\n");
                    G.warning(message)
                },
                "exceedFileCallback":function() {
                    G.warning("文件数量超出!");
                },
                "deleteFileCallback":function(message) {
                    G.warning("文件被删除!\n" + message);
                },
                "startUploadCallback":function(message) {
                    G.warning("开始上传文件!\n" + message);

                    // 设置 视图组件 uploading 状态
                    imageUploaderView.setState("uploading");
                },
                "uploadCompleteCallback":function(message, data) {
                    uploadedData.push(data);
                    G.warning("上传文件成功（单个）!\n" + message);
                    G.warning(data);
                },
                "uploadErrorCallback":function(message, data) {
                    G.warning("上传错误!\n" + message);
                    G.warning(data);
                },
                "uploadAllCompleteCallback":function() {
                    G.warning("完整上传成功!\n");
                    G.warning(uploadedData);

                    // 设置 视图组件  idle 状态
                    imageUploaderView.setState("idle");


                    /*
                    *
                    *      *         "isPlaceholder": true | false,  // not necessary
                     *
                     *         "isEmphasis":    true | false,  // not necessary
                     *         "emphasisColor": "#xxxxx", // not necessary, but make sure "isEmphasis = true"
                     *
                     *         "isAssist":      true | false,  // not necessary
                     *         "assistButtonLabel":""
                    * */

                    var getTotelUrlToData = function(inData) {
                        var outData = [];
                        for (var i = 0; i < inData.length; i++) {
                            var dataItem        = inData[i],
                                infoJson        = JSON.parse(dataItem.info),
                                url             = "http://bcgogo-dev.b0.upaiyun.com" + infoJson.url,
                                name            = "开发者自定义";

                            var outDataItem = {
                                "url":      url
                                ,
                                "name":     name
                            };
                            outData.push(outDataItem);
                        }


                        // 测试 辅助按钮功能
                        // 假设：
                        // 1）需要从 index = 2 开始的按钮 支持按钮
                        // 2）让 index = 0 为“主图” ， 即 isEmphasis = true

                        // @ 1) 的实现
                        for (var j = 2; j < outData.length; j++) {
                            var obj = outData[j];
                            obj["isAssist"] = true;
                            obj["assistButtonLabel"] = "设为主图";
                        }

                        // @ 2) 的实现
                        if(outData[0]) {
                            outData[0]["isEmphasis"] = true;
                            outData[0]["emphasisColor"] = "#ff7800";
                        }

                        return outData;
                    };
                    imageUploaderView.update( getTotelUrlToData(uploadedData) );
                }
            });


            /**
             * 视图组建的 样例代码
             * */
            var imageUploaderView = new App.Module.ImageUploaderView();
            imageUploaderView.init({
                // 你所需要注入的 dom 节点
                "selector":".group-imageuploader-view"
                ,
//                "width":100,
//                "height":103,
                "width":500,
                "height":200,
                "iWidth":100,
                "iHeight":100,
                "labelHeight":    20,
                "horizontalGap":  10,
                "verticalGap":    10,
                "paddingTop":     20,
                "paddingBottom":  20,
                "paddingLeft":    20,
                "paddingRight":   20,
                "isDeletable":true,
                "waitingInfo":"加载中",
                "showWaitingImage": true,
                "placeholderUrl":"js/components/themes/res/imageupload/bg_none_uploaded_pic.png",
                "maxFileNum":5,
                // 当删除某张图片时会触发此回调
                "onDelete": function (event, data, index) {
                    G.warning("event : " + event);
                    G.warning( data );
                    G.warning("index : " + index);
                    imageUploader.getFlashObject().deleteFile(index);

                    // 从已获得的图片数据池中删除 图片数据
                    uploadedData.splice(index, 1);
                },
                onAssistButtonClick:function(event, data, index) {
                    G.warning("event : " + event);
                    G.warning( data );
                    G.warning("index : " + index);
                }
            });


            // make alias
            window.imageUploader     = imageUploader;
            window.imageUploaderView = imageUploaderView;
        });

    </script>
</head>

<body>

<%--test button group--%>
<div class="group-button-imageuploader" style="position: relative;"></div>

<%--test view group--%>
<div class="group-imageuploader-view" style="position: relative;"></div>
</body>
</html>