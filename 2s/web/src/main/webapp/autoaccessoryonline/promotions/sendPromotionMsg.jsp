<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>发送促销消息</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" href="js/components/themes/bcgogo-customerSms-input<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/promotions<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "PROMOTIONS_MANAGER_MENU");
        $().ready(function(){
            //发送时间修改
            $("#sendTime").datetimepicker({
                "numberOfMonths": 1,
                "showButtonPanel": true,
                "changeYear": true,
                "changeMonth": true,
                "yearRange": "c-100:c+100",
                "yearSuffix": "",
                "onSelect": function (dateText, inst) {
                    var This = inst.input || inst.$input;
                    //如果选在非当前的时间 提醒逻辑
                    var newValue = This.val();
                    if (!newValue) return;
                    if (GLOBAL.Util.getExactDate(newValue).getTime() - (new Date().getTime()-60*1000) < 0) {
                        $(This).datetimepicker("hide");

                        nsDialog.jAlert("发送时间请选择现在之后的时间!", "信息提示", function() {
                            setTimeout(function() {
                                var date = new Date();
                                $(This).val(GLOBAL.Date.getCurrentFormatDate() + " " + date.getHours() + ":" + (date.getMinutes()<10?("0"+date.getMinutes()):date.getMinutes()));
                            }, 200);
                        });
                        return;
                    }
                    $(This).attr("lastvalue", newValue);
                }
            });


            $("#customerSelectBtn").click(function(){
                $("#allChk").attr("checked",false);
                var paramJson={startPageNo:1,maxRows:10};
                APP_BCGOGO.Net.syncPost({
                    url:"message.do?method=selectCustomer",
                    data:paramJson,
                    dataType:"json",
                    success:function (data) {
                        drawCustomerTable(data);
                        initPages(data, "_customerList", "message.do?method=selectCustomer", '', "drawCustomerTable", '', '', paramJson, '');
                    },
                    error:function () {
                        nsDialog.jAlert("数据异常!");
                    }
                });

                $("#customerList").dialog({
                    resizable: true,
                    title: "选择客户",
//                    height: 500,
                    width: 800,
                    modal: true,
                    closeOnEscape: false

                });
            });
        });

        function sendMsgBtn(){

            if (GLOBAL.Lang.isEmpty($("#content").val())) {
                nsDialog.jAlert("请填写消息内容！");
                return;
            }

            var newValue = $("#sendTime").val();
            if (!newValue || G.Lang.isEmpty(newValue)) {
                nsDialog.jAlert("请选择发送时间!", "信息提示", function() {
                    setTimeout(function() {
                    }, 200);
                });
                return;
            }
            if (GLOBAL.Util.getExactDate(newValue).getTime() - (new Date().getTime()-60*1000) < 0) {
                nsDialog.jAlert("发送时间请选择现在之后的时间!", "信息提示", function() {
                    setTimeout(function() {
                    }, 200);
                });
                return;
            }

            if (G.isEmpty($("#messageReceivers").val())) {
                nsDialog.jAlert("请添加客户！");
                return;
            }
            if($.trim($("#content").text()).length>200){
                nsDialog.jAlert("消息内容过长，请重新输入！");
                return;
            }
            var url="message.do?method=savePromotionMsgJob"
            $("#messageForm").ajaxSubmit({
                url:url,
                dataType: "json",
                type: "POST",
                success: function(json){
                    if(!json.success){
                        nsDialog.jAlert(json.msg);
                        return;
                    }
                    nsDialog.jConfirm("站内促销消息已发送成功！是否要发送短信给您的客户？", "友情提示", function (returnVal) {
                        if (returnVal) {
                            var pContent=$("#content").text();
                            var customerIds=new Array();
                            $("#receiverBox li").each(function(){
                                customerIds.push($(this).attr("customerId"));
                            });
                            if(customerIds.length<1){
                                nsDialog.jAlert("所选客户为空！");
                                return;
                            }
                            window.open("sms.do?method=smswrite&from=sendPromotionsMsg&pContent="+pContent+"&customerIds="+customerIds.toString(),"_self");
                        }else{
                            toPromotionsList();
                        }
                    });

                },
                error:function(){
                    nsDialog.jAlert("网络异常！");
                }
            });


        }


    </script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
    <div class="clear i_height"></div>
    <div class="titBody">
        <jsp:include page="../supplyCenterLeftNavi.jsp">
            <jsp:param name="currPage" value="promotions"/>
            <jsp:param name="biMenu" value="promotionManager"/>
        </jsp:include>
        <div class="bodyLeft">
            <h3 class="title">创建促销</h3>
            <div class="cuSearch">
                <div class="clear chartStep blue_color">
                    <span>1、促销设置</span>
                    <a class="stepImg"></a>
                    <span>2、添加上架商品</span>
                    <a class="stepImg"></a>
                    <span>3、促销中的商品</span>
                    <a class="stepImg"></a>
                    <span class="yellow_color">4、推广您的促销</span>
                </div>
                <form:form commandName="messageDTO" id="messageForm" action="message.do?method=savePromotionMsgJob" method="post" name="messageForm">
                    <input type="hidden" name="typeName" value="促销"/>
                    <input id="promotionsId" type="hidden" name="promotionsId" value="${promotionsId}"/>
                    <input id="messageReceivers" type="hidden" name="messageReceivers" />
                    <table cellpadding="0" cellspacing="0" class="table_promotion table_message">
                        <col width="100" >
                        <col width="600" >
                        <col >
                        <tr>
                            <td class="name_right" >收件人：<a id="customerSelectBtn" class="btn_promotion">选择客户</a></td>
                            <td>
                                <div id="receiverBox" class="txt" style="padding:10px; width:94.7%;height: 50px;overflow-x: auto;">

                                </div>
                            </td>

                            <td></td>
                        </tr>
                        <tr>
                            <td class="name_right" >消息内容：</td>
                                <%--<ul class="bcgogo-customerSmsInput-optionContainer" style="visibility: visible;">--%>
                            <td>
                                <textarea id="content" style="height: 100px" name="content" class="txt">${pContent}</textarea>
                            </td>
                                <%--</ul>--%>
                            <td class="gray_color" >（最多200个字）</td>
                        </tr>

                        <tr>
                            <td class="name_right">发送时间</td>
                            <td><input id="sendTime" readonly="true" name="sendTimeStr" type="text" value="${sendTime}" class="txt" style="width:120px;" /></td>
                            <td></td>
                        </tr>
                    </table>
                </form:form>
                <div class="clear i_height"></div>
                <div class="divTit" style=" float:right; margin-right:100px;">
                    <a onclick="sendMsgBtn()" class="button">确定发送</a>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="mask" style="display:block;position: absolute;"></div>
<%@ include file="promotionCustomerList.jsp" %>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>