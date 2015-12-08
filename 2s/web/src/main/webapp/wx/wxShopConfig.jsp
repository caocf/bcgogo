<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>发送微信</title>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/message<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="/web/js/components/ui/bcgogo-wait-mask<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="/web/js/wx/wx_shop_config<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        ;
        var cursorControl={
            getType:function(dom){
                return Object.prototype.toString.call(dom).match(/^\[object\s(.*)\]$/)[1];
            },
            getStart:function(dom){
                var start;
                if (dom.selectionStart || dom.selectionStart == '0'){
                    start = dom.selectionStart;
                }else if (window.getSelection){
                    var rng = window.getSelection().getRangeAt(0).cloneRange();
                    rng.setStart(dom, 0);
                    start = rng.toString().length;
                };
                return start;
            },
            insertText:function(dom,text){
                dom.focus();
                if (document.all){
                    var c = document.selection.createRange();
                    document.selection.empty();
                    c.text = text;
                    c.collapse();
                    c.select();
                }else{
                    var start=this.getStart(dom);
                    if(this.getType(dom)=='HTMLDivElement'){
                        dom.innerHTML=dom.innerHTML.substr(0,start)+text+dom.innerHTML.substr(start);

                    }else{
                        dom.value=dom.value.substr(0,start)+text+dom.value.substr(start);
                    }
                }
            }
        };



        $(function(){

            $(".j_config_sample").click(function(){
                $("#edit_area").text("");
                $("#edit_area").append($.trim($(this).html()));
            });

            $(".js_switch").hover(function(){
                $(".js_emotionArea").show();
            });

            $(document).click(function(event){
                var $target=$(event.target);
                var selectorArray = [
                    ".emotion_wrp",
                    ".js_switch"
                ];
                if($target.closest(selectorArray).length==0){
                    $(".js_emotionArea").hide();
                }
            });




            $(".js_emotion_i").hover(function(){
                var url="./images/emotion/"+$(this).attr("data-title")+".gif";
                var _img='<img src="'+url+'">';
                $(".js_emotionPreviewArea").text("");
                $(".js_emotionPreviewArea").append(_img);
            }).click(function(){
                        var url="./images/emotion/"+$(this).attr("data-title")+".gif";
                        var code="/"+$(this).attr("data-title");
                        var _img="<img src='"+url+"' code='"+code+"'>";
//                        cursorControl.insertText($("#edit_area")[0],_img);
            $(".js_editorArea").append(_img);
                        $(".js_emotionArea").hide();
                    });

            $("#clearBtn").click(function(){
                $("#edit_area").text("");
            });

            $("#saveBtn").click(function(){
                var mask=APP_BCGOGO.Module.waitMask;
                mask.login();
                var content=$.trim($(".js_editorArea").html());
                APP_BCGOGO.Net.asyncAjax({
                    url:"weChat.do?method=saveWelcomeWord",
                    type: "POST",
                    cache: false,
                    dataType: "json",
                    data:{content:content},
                    success: function (result) {
                        mask.open();
                        if(!result.success){
                            nsDialog.jAlert(result.msg);
                            return;
                        }
                        nsDialog.jAlert("保存成功。");
//                        window.location.reload();
                    },
                    error:function(){
                        nsDialog.jAlert("网络异常。");
                    }
                });

            });

            $(".js_editorArea").keyup(function(){
                var content=$.trim($(".js_editorArea").text());
                if(content.length>200){
//                   nsDialog.jAlert("欢迎词内容应在200字以内。");
                    return;
                }
                var len=$("#j_content_length").text();
                $("#j_content_length").text(len--);
            });


        });
    </script>

</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main clear">
    <div class="mainTitles">
        <div class="titleWords">微信管理</div>
    </div>
    <div class="messageContent">

        <jsp:include page="wxNavi.jsp">
            <jsp:param name="currPage" value="wxShopConfig" />
        </jsp:include>

        <div class="messageRight">
            <div class="messageRight_radius">
                <div class="news-table shop-config">

                    <div class="d-shop-config">
                        <div class="">
                            <h2>欢迎词配置</h2>
                            <div id="js_msgSender" class="msg_sender">
                                <div class="msg_tab">
                                    <div class="tab_panel">
                                        <div class="tab_content">
                                            <div class="js_textArea inner no_extra">
                                                <div class="emotion_editor">
                                                    <div id="edit_area" contenteditable="true" class="edit_area js_editorArea" style="overflow-y: auto; overflow-x: hidden;">
                                                        ${welcomeWord}
                                                    </div>
                                                    <div class="editor_toolbar">
                                                        <a class="icon_emotion emotion_switch js_switch" href="javascript:void(0);">表情</a>

                                                        <p class="editor_tip js_editorTip">内容请控制在200字以内</p>
                                                        <%@include file="../common/emotion_wrp.jsp" %>
                                                    </div>
                                                </div>

                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="tool_bar">
                                <span class="btn btn_primary btn_input" id="js_save"><button id="saveBtn">保存</button></span>
                                <span class="btn btn_default btn_input btn_disabled" id="js_del"><button id="clearBtn" >清除内容</button></span>
                            </div>
                        </div>
                        <div style="margin-top: 30px;">
                            <span>说明：欢迎词为扫描二维码后回复的内容</span><br/>
                            <span style="margin-left: 35px;">建议将本店无线密码填到欢迎词中，轻松扩大您的店铺微信粉丝群 </span>
                        </div>
                    </div>

                    <div class="d-config-pre">
                        <div class="pre-mobile">
                            <h3>模版一</h3>
                            <div class="j_config_sample shop-config-sample">
                                欢迎关注【${shopName}】 <img code="/玫瑰" src="./images/emotion/玫瑰.gif"><img code="/玫瑰" src="./images/emotion/玫瑰.gif"><br/>
                                本店无线名：my-wifi<br/>无线密码：1234567890<br/>
                                绑定您的车牌号，并输入车架号，发动机号（方便您随时查违章）。<br/>
                                后续可以查询在我店消费明细，也可以点评我店的服务。<br/>
                                欢迎转发，转发有礼，欢迎光临。<br/>
                                </a>
                            </div>
                        </div>
                        <div class="pre-mobile">
                            <h3>模版二</h3>
                            <div class="j_config_sample shop-config-sample">
                                欢迎关注【${shopName}】，您将收到本店的实时账单。<br/>
                                本店无线名：my-wifi<br/>无线密码：1234567890</a>
                            </div>
                        </div>

                    </div>
                </div>

                <div>
                </div>
                <div class="clear"></div>
            </div>
        </div>

    </div>
</div>

<%@include file="/WEB-INF/views/footer_html.jsp" %>

</body>

</html>