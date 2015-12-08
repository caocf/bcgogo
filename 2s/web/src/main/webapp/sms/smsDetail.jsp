<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 14-1-2
  Time: 下午5:03
  To change this template use File | Settings | File Templates.
--%>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%
    //会员个性化
    boolean isMemberSwitchOn = WebUtil.checkMemberStatus(request);
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>短信详情</title>
<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/message<%=ConfigController.getBuildVersion()%>.css"/>

<%@include file="/WEB-INF/views/header_script.jsp" %>
<script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/smsWrite<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript">
defaultStorage.setItem(storageKey.MenuUid, "WEB.CUSTOMER_MANAGER.SMS_MANAGER");
defaultStorage.setItem(storageKey.MenuCurrentItem,"<a href='sms.do?method=toSmsList&smsType=SMS_SENT' style='color: #007CDA;cursor:pointer;font-size:14px;height: 30px;line-height: 30px;'>已发送</a> > 短信详情");

$(function(){
    function _toNextSms(){
        getTheAssignSmsBtn(Number($("#rowStart").val())+1);
    }
    $("#backBtn").click(function(){
        window.location.href="sms.do?method=toSmsList&smsType="+$("#smsType").val();
    });
    $("#sendTimeInput").datetimepicker({
        "numberOfMonths": 1,
        "showButtonPanel": false,
        "changeYear": true,
        "changeMonth": true,
        "yearSuffix": "",
        "onClose": function(dateText, inst) {
            if(!$(this).val()) {
                return;
            }
            var _self = inst.input || inst.$input;
            if(G.isEmpty(_self.val())){
                nsDialog.jAlert("发送时间应不能为空，请重新选择！");
                return;
            }
            $("#s_sendTime").text(_self.val());
        },
        "onSelect": function(dateText, inst) {
            if(inst.lastVal == dateText) {
                return;
            }
            $(this).val(dateText);
        }
    });
    $("#modifySendTimeBtn").click(function() {


        var sendTimeStr=$("#sendTimeSpan").text();

        var year=sendTimeStr.match(new RegExp("\\d{4}", "g"))[0];
        var month=sendTimeStr.match(new RegExp("\\d{2}", "g"))[2];
        var day=sendTimeStr.match(new RegExp("\\d{2}", "g"))[3];
        var min=sendTimeStr.match(new RegExp("\\d{2}", "g"))[4];
        var s=sendTimeStr.match(new RegExp("\\d{2}", "g"))[5];
        var dateStr=year+"-"+month+"-"+day+" "+min+":"+s;

        $("#timingSelectorAlert").dialog({
            resizable: false,
            draggable:false,
            title: "定时发送",
            height: 200,
            width: 300,
            modal: true,
            closeOnEscape: false,
            beforeClose:function(){
                $("#sendTimeInput").val("");
            },
            open:function(){
                $("#sendTimeInput").val(dateStr);
            },
            buttons:{
                "确定":function(){
                    var sendTimeStr=$("#sendTimeInput").val();
                    if(G.isEmpty(sendTimeStr)){
                        nsDialog.jAlert("发送时间应不能为空，请重新选择！");
                        return;
                    }
                    var newValue = GLOBAL.Util.getExactDate(sendTimeStr).getTime();
                    if(newValue<new Date().getTime()) {
                        nsDialog.jAlert("发送时间应大于当前时间，请重新选择！");
                        return;
                    }
                    $(this).dialog("close");

                    APP_BCGOGO.Net.asyncAjax({
                        url: "sms.do?method=modifySendTime",
                        type: "POST",
                        cache: false,
                        data:{
                            smsId:$("#sms_id").val(),
                            sendTimeStr:sendTimeStr
                        },
                        dataType: "json",
                        success: function (json) {
                            $("#sendTimeSpan").text(sendTimeStr);
                            nsDialog.jAlert("修改成功。");
                        },
                        error:function(){
                            nsDialog.jAlert("网络异常！");
                        }
                    });



                },
                "取消":function(){
                    $(this).dialog("close");
                }
            }
        });
    });

    $("#delSmsBtn").live("click",function(){
        var smsId=$("#sms_id").val();
        if(G.isEmpty(smsId)) return;
        var tLabel=$("#smsType").val()=="SMS_SEND"?"定时":"";
        nsDialog.jConfirm("是否确认要删除信息？","提示",function(flag){
            if(flag){
                APP_BCGOGO.Net.asyncAjax({
                    url:"sms.do?method=deleteSms",
                    type: "POST",
                    cache: false,
                    data:{
                        smsIds:smsId
                    },
                    dataType: "json",
                    success: function (result) {
                        if(!result.success){
                            nsDialog.jAlert(result.msg);
                            return;
                        }
                        _toNextSms();
                    },
                    error:function(){
                        nsDialog.jAlert("网络异常。");
                    }
                });
                if($.isFunction(refreshSmsStat)){
                    refreshSmsStat();
                }
            }
        },false);
    });

    $("#cancelSendBtn").live("click",function(){
        var smsId=$("#sms_id").val();
        if(G.isEmpty(smsId)) return;
        nsDialog.jConfirm("友情提示：您是否确认取消发送定时短信，取消后该短信将无法发送，并且保存在草稿箱中！","提示",function(flag){
            if(flag){
                APP_BCGOGO.Net.asyncAjax({
                    url:"sms.do?method=cancelSendSms",
                    type: "POST",
                    cache: false,
                    data:{
                        smsIds:smsId
                    },
                    dataType: "json",
                    success: function (result) {
                        if(!result.success){
                            nsDialog.jAlert(result.msg);
                            return;
                        }
                        _toNextSms();
                    },
                    error:function(){
                        nsDialog.jAlert("网络异常。");
                    }
                });
            }
        },false);
    });

    //转发按钮
    $("#sendSmsBtn").live("click",function(){
        var smsId=$("#sms_id").val();
        if(!G.isEmpty(smsId)){
            window.location.href="sms.do?method=smswrite&smsId="+smsId + "&excludeContact=true";
        }
    });

    $("#reSendBtn").live("click",function(){
//         var smsId=$("#sms_id").val();
//          APP_BCGOGO.Net.asyncAjax({
//            url:"sms.do?method=reSendSms",
//            type: "POST",
//            cache: false,
//            data:{
//                smsId:smsId
//            },
//            dataType: "json",
//            success: function (result) {
//                if(!result.success){
//                    nsDialog.jAlert(result.msg);
//                    return;
//                }
//               nsDialog.jAlert("发送成功。");
//                refreshSmsStat();
//            },
//            error:function(){
//                nsDialog.jAlert("网络异常。");
//            }
//        });
        var smsId=$("#sms_id").val();
        if(!G.isEmpty(smsId)){
            window.location.href="sms.do?method=smswrite&smsId="+smsId;
        }
    });

    $(".lastSmsBtn").click(function(){
        if($(this).hasClass("assign-invalid")) return;
        getTheAssignSmsBtn(Number($("#rowStart").val())-1);
    });
    $(".nextSmsBtn").click(function(){
        if($(this).hasClass("assign-invalid")) return;
        getTheAssignSmsBtn(Number($("#rowStart").val())+1);
    });
    getTheAssignSmsBtn(${rowStart});
});

function getTheAssignSmsBtn(rowStart){

    APP_BCGOGO.Net.asyncAjax({
        url: "sms.do?method=getTheAssignSms",
        type: "POST",
        cache: false,
        data:{
            smsType:$("#smsType").val(),
            rowStart:rowStart
        },
        dataType: "json",
        success: function (smsDTO) {
            $("#sms_id").val(smsDTO.idStr);
            $("#userName").text(G.normalize(smsDTO.userName));
            $("#sendTime").text(G.normalize(smsDTO.sendTimeStr));
            $("#content").text(G.normalize(smsDTO.content));
            $(".nextSmsBtn").removeClass("assign-invalid");
            $(".lastSmsBtn").removeClass("assign-invalid");
            var rowStart=smsDTO.rowStart;
            var smsTotalNum=smsDTO.smsTotalNum;
            $("#rowStart").val(rowStart);
            if(rowStart<=1){
                $(".lastSmsBtn").addClass("assign-invalid");
            }
            if(rowStart>=smsTotalNum){
                $(".nextSmsBtn").addClass("assign-invalid");
            }
            var contactStr="";
            var contactGroupDTOs=smsDTO.contactGroupDTOs;
            if(!G.isEmpty(contactGroupDTOs)){
                for(var i=0;i<contactGroupDTOs.length;i++){
                    var contactGroupDTO=contactGroupDTOs[i];
                    contactStr+=G.normalize(contactGroupDTO.name);
                    contactStr+=";";
                }
            }
            var contactDTOs=smsDTO.contactDTOs;

            var customerDTOs =  smsDTO.customerDTOs;
            var customerNameStr = "";

            if (!G.isEmpty(customerDTOs)) {
              for (var i = 0; i < customerDTOs.length; i++) {
                var customerDTO = customerDTOs[i];
                if (G.isEmpty(customerNameStr)) {
                  customerNameStr = G.isEmpty(customerDTO.name) ? "未命名" : G.normalize(customerDTO.name);
                }
              }
            }

            if(!G.isEmpty(contactDTOs)){
                for(var i=0;i<contactDTOs.length;i++){
                    var contactDTO=contactDTOs[i];
                    var name=G.isEmpty(contactDTO.name)?customerNameStr:G.normalize(contactDTO.name);
                    var mobile=G.normalize(contactDTO.mobile);
                    contactStr+=name+"<"+mobile+">";
                    contactStr+=";";
                }
            }
            $("#contactContainer").text(contactStr);
            if(Number($("#viewFlag").val())!=1){  //查看短信详情不显示
                $(".turn_page").show();
            }
        },
        error:function(){
            nsDialog.jAlert("网络异常！");
        }
    });
};
</script>


</head>
<body class="bodyMain">
<input id="smsType" type="hidden" value="${smsType}"/>
<input id="sms_id" type="hidden"/>
<input id="rowStart" type="hidden" value="${rowStart}"/>
<input id="viewFlag" type="hidden" value="${viewFlag}"/>
<%@include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
    <div class="mainTitles">
        <div class="titleWords">短信管理</div>
    </div>
    <div class="messageContent">
        <c:choose>
            <c:when test="${smsType=='SMS_SENT'}">
                <jsp:include page="smsNavi.jsp">
                    <jsp:param name="currPage" value="smsSent" />
                </jsp:include>
            </c:when>
            <c:otherwise>
                <jsp:include page="smsNavi.jsp">
                    <jsp:param name="currPage" value="smsSend" />
                </jsp:include>
            </c:otherwise>
        </c:choose>

        <div class="messageRight">
            <div class="messageRight_radius">
                <strong style="font-size:14px;">短信详情</strong>
                <div class="turn_page" style="display: none">
                    <a class="lastSmsBtn blue_color">上一条</a>
                    |
                    <a class="nextSmsBtn blue_color">下一条</a>
                </div>
                <div class="clear"></div>
                <div class="content_03">
                    <div class="detailTop_bg">
                        <div class="content_txt">
                            <div class="left">发送操作人：</div>
                            <div id="userName" class="right"></div>
                            <div class="clear"></div>
                        </div>
                        <div class="content_txt">
                            <div class="left">发送时间 ：</div>
                            <%--<div class="right">2013年10月20日（星期天）13：45<br />--%>
                            <div class="right" id="sendTime"><br />
                            </div>
                            <div class="clear"></div>
                        </div>
                        <div class="content_txt">
                            <div class="left">收信人 ：</div>
                            <div id="contactContainer" class="right"></div>
                            <div class="clear"></div>
                        </div>
                    </div>
                    <div class="clear"></div>
                    <c:if test="${smsType=='SMS_SEND'}">
                        <div class="timing">短信是定时短信，将在<span id="sendTimeSpan">${smsDTO.sendTimeStr}</span>发出。<a id="modifySendTimeBtn" class="blue_color">[修改时间]</a></div>
                    </c:if>
                    <div class="attached" id="content"></div>
                    <div class="clear height"></div>
                </div>
                <div class="clear"></div>
                <div class="addressList">
                    <a id="backBtn" class="main-btn" href="#">返 回</a>
                    <a id="sendSmsBtn" class="assis-btn" href="#">转 发</a>
                    <c:choose>
                        <c:when test="${smsType=='SMS_SENT'}">
                            <a id="reSendBtn" class="assis-btn">再次发送</a>
                        </c:when>
                        <c:otherwise>
                            <a id="cancelSendBtn" class="assis-btn">取消发送</a>
                        </c:otherwise>
                    </c:choose>
                    <a id="delSmsBtn" class="assis-btn" href="#">删 除</a>
                </div>
                <div class="turn_page">
                    <a class="lastSmsBtn blue_color">上一条</a>
                    |
                    <a class="nextSmsBtn blue_color">下一条</a>
                </div>
                <div class="clear"></div>
            </div>
        </div>
    </div>
</div>
</div>

<div id="timingSelectorAlert"  style="display: none">
    <div class="clear i_height"></div>
    <div>请选择定时发送的时间</div>
    <div>
        <input type="hidden" id="hiddenTxt" autofocus="true"/>
        <input id="sendTimeInput" type="text" autocomplete="off" readonly="true"/>
    </div>
    <div>本短信将于 <span id="s_sendTime"></span>  发送</div>
</div>



<div id="mask" style="display:block;position: absolute;"></div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>