<%@ page language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>后台管理系统——微信管理</title>
    <link rel="stylesheet" type="text/css" href="styles/dataMaintenance.css"/>
    <link rel="stylesheet" type="text/css" href="styles/backstage.css"/>
    <link rel="stylesheet" type="text/css" href="styles/backAgent.css"/>
    <link rel="stylesheet" type="text/css" href="styles/storesMange.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style.css"/>
    <link rel="stylesheet" type="text/css" href="styles/shopIndividuation.css"/>
    <%@include file="/WEB-INF/views/style-thirdpartLibs.jsp"%>

    <%@include file="/WEB-INF/views/script-thirdpartLibs.jsp"%>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.validate.js"></script>
    <%@include file="/WEB-INF/views/script-common.jsp"%>
    <script type="text/javascript" src="js/bcgogo.js"></script>
    <script type="text/javascript" src="js/wxArticle.js"></script>
    <script type="text/javascript" src="js/mask.js"></script>
    <script type="text/javascript">
        function confirmThis(){
            var titleT = $("#title").val();
            var description = $("#description").val();
            if(titleT==null||titleT==""){
                alert("请输入标题！");
                return false;
            }
            if(description==null||description==""){
                alert("请输入正文！");
                return false;
            }
            document.getElementById('template_form').submit();
        }

        function chancel(){
            window.location.href="weChat.do?method=toAdultList";
        }

        function sendMassMsg(){
            if(1==1){
//                alert("功能暂未开放");
//                return;
            }
            var msgId=$("#weChatId").val();
            var description=$("#description").val();
            if(description&&description.length>500){
                  alert("正文过长，应在500字以内。");
                return;
            }
            APP_BCGOGO.Net.asyncGet({
                url: 'weChat.do?method=sendShopMassNewsMsg',
                data: {
                    id: msgId,
                    description:description,
                    "now": new Date()
                },
                dataType: "json",
                success: function (result) {
                    if(!result.success){
                        alert(result.msg)
                        return false;
                    }
                    alert("审核通过，信息发送成功。")
                    window.location.href="weChat.do?method=toAdultList";
                }
            })
        }

    </script>
</head>
<body>
<div class="main">
    <!--头部-->
    <%@include file="/WEB-INF/views/header.jsp" %>
    <!--头部结束-->
    <div class="body">
        <!--左侧列表-->
        <%@include file="/WEB-INF/views/left.jsp" %>
        <!--左侧列表结束-->
        <!--右侧内容-->
        <div class="bodyRight">
            <!--搜索-->
            <div class="rightText">
                <div class="textLeft"></div>
                <div class="textBody"><input type="text" value="店铺名" id="txt_shopName"/></div>
                <div class="textRight"></div>
            </div>
            <div class="rightText">
                <div class="textLeft"></div>
                <div class="textBody"><input type="text" value="店主" id="txt_shopOwner"/></div>
                <div class="textRight"></div>
            </div>
            <div class="rightText">
                <div class="textLeft"></div>
                <div class="textAddressbody"><input type="text" value="地址" id="txt_address"/></div>
                <div class="textRight"></div>
            </div>
            <div class="rightText">
                <div class="textLeft"></div>
                <div class="textBody"><input type="text" value="手机/电话" id="txt_phone"/></div>
                <div class="textRight"></div>
            </div>
            <input type="button" class="rightSearch" value="搜 索"/>
            <!--搜索结束-->
            <!--内容-->
            <div class="rightMain clear">
                <%@include file="/weChat/wxNav.jsp"%>
                <div  id="div_show">
                    <div class="i_arrow"></div>
                    <div class="i_upLeft"></div>
                    <div class="i_upCenter">
                        <div class="" id="div_drag">编辑公共模板</div>

                    </div>
                    <form:form id="template_form" action="weChat.do?method=modifyAudit"
                               method="post" enctype="multipart/form-data" >

                    <div class="i_upRight"></div>
                    <div class="bodyRight">
                        <%--<div style="color:red">每个店铺每月至多发送一条推送消息，请仔细审核。</div>--%>
                        <div class="height"></div>
                        <div class="wx-shop-info">
                            <div class="wx-title-label">店铺信息</div>
                            <div class="wx-shop-info-description">店铺名：${shopDTO.name}</div>
                            <div class="wx-shop-info-description">本月已群发条数：${mass_count}</div>

                        </div>
                        <table cellpadding="0" id="configTable" cellspacing="0" class="configTable audit-article-detail">
                            <col width="100">
                            <col  width="100"/>
                            <input type="hidden" id="weChatId" name="id" value="${wxMsg.idStr}"/>
                            <br/>

                            <tr>
                                <td class="label">标题</td>

                                <td style="height:20px;width: 224px;"><input type="text" id="title" name="title" value="${wxMsg.title}" style="height:20px;width: 224px"/>
                                </td>
                            </tr>
                            <tr>
                                <td class="label">图片</td>
                                <td><img src="${wxMsg.picUrl}" class="wx-pic-url" style="height: 200px"> </td>
                            </tr>
                            <tr>
                                <td class="label" id="type">正文</td>
                                <td style="height:20px;width: 224px;"><textarea id="description" name="description" style="height: 86px; width: 224px;" >${wxMsg.description}</textarea>  </td>
                                    <%--<td><textarea id="description" name="description"  style="height: 86px; width: 224px;">${wxMsg.description}</textarea>  </td>--%>
                            </tr>
                            <tr>
                                <td class="label">重新上传</td>
                                <td style="height:20px;width: 224px;"><input type="file" id="pic" name="pic" style="height:20px;"/></td>
                            </tr>
                        </table>
                        <div class="height"></div>
                        <div class="more_his">
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;
                            <%--<input type="button" value="通过" onclick="sendMassMsg()" class="rightSearch" id="confirmBtn"/>--%>
                            <input type="button" value="确定" onclick="confirmThis()" class="rightSearch" id="confirmBtn"/>
                            <input type="button" value="返回" onclick="chancel()" class="rightSearch" id="cancleBtn"/>
                        </div>
                    </div>
                </div>
                </form:form>
                <!--内容结束-->
            </div>
            <!--内容结束-->
            <!--圆角-->
            <div class="bottom_crile clear">
                <div class="crile"></div>
                <div class="bottom_x"></div>
                <div style="clear:both;"></div>
            </div>
            <!--圆角结束-->
        </div>
        <!--右侧内容结束-->
    </div>
</div>
<ul class="suggestionMain" style="list-style: none;color:#000000;">
</ul>
<div id="mask" style="display:block;position: absolute;">
</div>
<%--<div id="setLocation"   style="position: fixed; left: 37%; top: 37%;  z-index: 8; display: none;">--%>
<%--<jsp:include page="sms/editSms.jsp"></jsp:include>--%>
<%--</div>--%>
</body>
</html>