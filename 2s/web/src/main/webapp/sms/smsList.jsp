<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 13-12-19
  Time: 下午1:34
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
<title>
    <c:choose>
        <c:when test="${smsType=='SMS_SENT'}">
            已发送
        </c:when>
        <c:when test="${smsType=='SMS_SEND'}">
            待发送
        </c:when>
        <c:otherwise>
            草稿箱
        </c:otherwise>
    </c:choose>
</title>

<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/message<%=ConfigController.getBuildVersion()%>.css"/>
<style type="text/css">
        /*.text-overflow{*/
        /*overflow: hidden;*/
        /*text-overflow: ellipsis;*/
        /*white-space: nowrap;*/
        /*word-break: keep-all;*/
        /*}*/
</style>

<%@include file="/WEB-INF/views/header_script.jsp" %>
<script type="text/javascript" src="js/customer<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/txnbase<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-customerSms-input<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/smsWrite<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/smsWriteUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript">
<c:choose>
<c:when test="${smsType=='SMS_SENT'}">
defaultStorage.setItem(storageKey.MenuUid, "WEB.CUSTOMER_MANAGER.SMS_MANAGER");
defaultStorage.setItem(storageKey.MenuCurrentItem,"<a href='sms.do?method=toSmsList&smsType=SMS_SENT' style='color: #007CDA;cursor:pointer;font-size:14px;height: 30px;line-height: 30px;'>已发送</a>");
</c:when>
<c:when test="${smsType=='SMS_SEND'}">
defaultStorage.setItem(storageKey.MenuUid, "WEB.CUSTOMER_MANAGER.SMS_MANAGER");
defaultStorage.setItem(storageKey.MenuCurrentItem,"<a href='sms.do?method=toSmsList&smsType=SMS_SEND' style='color: #007CDA;cursor:pointer;font-size:14px;height: 30px;line-height: 30px;'>待发送</a>");
</c:when>
<c:otherwise>
defaultStorage.setItem(storageKey.MenuUid, "WEB.CUSTOMER_MANAGER.SMS_MANAGER");
defaultStorage.setItem(storageKey.MenuCurrentItem,"<a href='sms.do?method=toSmsList&smsType=SMS_DRAFT' style='color: #007CDA;cursor:pointer;font-size:14px;height: 30px;line-height: 30px;'>草稿箱</a>");

</c:otherwise>
</c:choose>

function toSmsSendFinish(smsId){
    window.location.href="sms.do?method=toSmsSendFinish&smsId="+smsId;
}

function initSmsList(result){
    $("#smsTable tr:gt(0)").remove();
    $(".select_all").attr("checked",false);
    if(G.isEmpty(result)){
        return;
    }
    var smsType=$("#smsType").val();
    if(smsType=="SMS_SENT"){

    }else if(smsType=="SMS_SEND"){

    }
    var resultMap=result.results;
    var rowStart=result.pager.rowStart;
    var trStr="";
    if(G.isEmpty(resultMap)){
        trStr+="<tr><td colspan='8' style='text-align: center;'>没有短信数据。</td></tr>";
        $("#smsTable").append(trStr);
        return;
    }
    var data_today=resultMap.data_today;
    var data_yesterday=resultMap.data_yesterday;
    var data_last_week=resultMap.data_last_week;
    var data_others=resultMap.data_others;
    if(!G.isEmpty(data_today)){

        trStr+='<tr class="news-thtitle">'+
                '<td colspan="5"><a style="color: #007CDA">今天（'+G.rounding(result.today_total_num)+'条）</a></td>'+
                '</tr>';
        for(var i=0;i<data_today.length;i++){
            var data_sms=data_today[i];
            var smsId=data_sms.idStr;
            var sendTimeStr=G.normalize(data_sms.sendTimeStr);
            var content=G.normalize(data_sms.content);
            if(G.isEmpty(content)){
                content = "（未填写短信内容）";
            }
            var contentStr=content.length>25?content.substr(0,25)+'...':content;
            var operator=G.normalize(data_sms.userName);
            var name="";
            var contactGroupDTOs=G.normalize(data_sms.contactGroupDTOs);
            for(var j=0;j<contactGroupDTOs.length;j++){
                var contactGroupDTO=contactGroupDTOs[j];
                var contactName=contactGroupDTO.name;
                name+=G.isEmpty(contactName)?"未命名":contactName;
//                name=G.normalize(contactGroupDTO.name);
                name+=";";
            }
            var contactDTOs=G.normalize(data_sms.contactDTOs);
            for(var j=0;j<contactDTOs.length;j++){
                var contactDTO=contactDTOs[j];
                var contactName = G.normalize(contactDTO.name);
                name+=G.isEmpty(contactName)?"未命名":contactName;
                name+="<"+contactDTO.mobile+">";
                name+=";";
            }
            if((contactDTOs== null || contactDTOs.length == 0) && (contactGroupDTOs == null || contactGroupDTOs.length == 0)){
                name += "（收件人未填写）";
            }
            var nameStr=name.length>15?name.substr(0,15)+'...':name;
            trStr+='<tr class="sms_item">'+
                    '<td class="item_chk_td"><div class="news-01">'+
                    '<input class="sms_id" type="hidden" value="'+smsId+'"/>'+
                    '<input class="itemChk" type="checkbox" rowStart="'+(++rowStart)+'"/>'+
                    '</div></td>'+
                    '<td title="'+name+'">'+nameStr+'</td>'+
                    '<td title="'+content+'">'+contentStr+'</td>'+
                    '<td align="center"> '+operator+'</td>'+
                    '<td align="center" valign="top">'+sendTimeStr+'</td>'+
                    '</tr>';

        }
    }
    if(!G.isEmpty(data_yesterday)){
        trStr+='<tr class="news-thtitle">'+
                '<td colspan="5"><a style="color: #007CDA">昨天（'+G.rounding(result.yesterday_total_num)+'条）</a></td>'+
                '</tr>';
        for(var i=0;i<data_yesterday.length;i++){
            var data_sms=data_yesterday[i];
            var smsId=data_sms.idStr;
            var sendTimeStr=G.normalize(data_sms.sendTimeStr);
            var content=G.normalize(data_sms.content);
            if(G.isEmpty(content)){
                content = "（未填写短信内容）";
            }
            var contentStr=content.length>25?content.substr(0,25)+'...':content;
            var operator=G.normalize(data_sms.userName);
            var name="";
            var contactGroupDTOs=G.normalize(data_sms.contactGroupDTOs);
            for(var j=0;j<contactGroupDTOs.length;j++){
                var contactGroupDTO=contactGroupDTOs[j];
                name+=contactGroupDTO.name;
                name+=";";
            }
            var contactDTOs=G.normalize(data_sms.contactDTOs);
            for(var j=0;j<contactDTOs.length;j++){
                var contactDTO=contactDTOs[j];
                var contactName = G.normalize(contactDTO.name);
                name+=G.isEmpty(contactName)?"未命名":contactName;
                name+="<"+contactDTO.mobile+">";
                name+=";";
            }
            if((contactDTOs== null || contactDTOs.length == 0) && (contactGroupDTOs == null || contactGroupDTOs.length == 0)){
                name += "（收件人未填写）";
            }
            var nameStr=name.length>15?name.substr(0,15)+'...':name;
            trStr+='<tr class="sms_item">'+
                    '<td class="item_chk_td"><div class="news-01">'+
                    '<input class="sms_id" type="hidden" value="'+smsId+'"/>'+
                    '<input class="itemChk" type="checkbox" rowStart="'+(++rowStart)+'"/>'+
                    '</div></td>'+
                    '<td title="'+name+'">'+nameStr+'</td>'+
                    '<td title="'+content+'">'+contentStr+'</td>'+
                    '<td align="center"> '+operator+'</td>'+
                    '<td align="center" valign="top">'+sendTimeStr+'</td>'+
                    '</tr>';
        }
    }
    if(!G.isEmpty(data_last_week)){
        trStr+='<tr class="news-thtitle">'+
                '<td colspan="5"><a style="color: #007CDA">上周（'+G.rounding(result.last_week_total_num)+'条）</a></td>'+
                '</tr>';
        for(var i=0;i<data_last_week.length;i++){
            var data_sms=data_last_week[i];
            var smsId=data_sms.idStr;
            var sendTimeStr=G.normalize(data_sms.sendTimeStr);
            var content=G.normalize(data_sms.content);
            if(G.isEmpty(content)){
                content = "（未填写短信内容）";
            }
            var contentStr=content.length>25?content.substr(0,25)+'...':content;
            var operator=G.normalize(data_sms.userName);
            var name="";
            var contactGroupDTOs=G.normalize(data_sms.contactGroupDTOs);
            for(var j=0;j<contactGroupDTOs.length;j++){
                var contactGroupDTO=contactGroupDTOs[j];
                name+=contactGroupDTO.name;
                name+=";";
            }
            var contactDTOs=G.normalize(data_sms.contactDTOs);
            for(var j=0;j<contactDTOs.length;j++){
                var contactDTO=contactDTOs[j];
                var contactName = G.normalize(contactDTO.name);
                name+=G.isEmpty(contactName)?"未命名":contactName;
                name+="<"+contactDTO.mobile+">";
                name+=";";
            }
            if((contactDTOs== null || contactDTOs.length == 0) && (contactGroupDTOs == null || contactGroupDTOs.length == 0)){
                name += "（收件人未填写）";
            }
            var nameStr=name.length>15?name.substr(0,15)+'...':name;
            trStr+='<tr class="sms_item">'+
                    '<td class="item_chk_td"><div class="news-01">'+
                    '<input class="sms_id" type="hidden" value="'+smsId+'"/>'+
                    '<input class="itemChk" type="checkbox" rowStart="'+(++rowStart)+'"/>'+
                    '</div></td>'+
                    '<td title="'+name+'">'+nameStr+'</td>'+
                    '<td title="'+content+'">'+contentStr+'</td>'+
                    '<td align="center"> '+operator+'</td>'+
                    '<td align="center" valign="top">'+sendTimeStr+'</td>'+
                    '</tr>';
        }
    }
    if(!G.isEmpty(data_others)){
        trStr+='<tr class="news-thtitle">'+
                '<td colspan="5"><a style="color: #007CDA">更早（'+G.rounding(result.other_total_num)+'条）</a></td>'+
                '</tr>';
        for(var i=0;i<data_others.length;i++){
            var data_sms=data_others[i];
            var smsId=data_sms.idStr;
            var sendTimeStr=G.normalize(data_sms.sendTimeStr);
            var content=G.normalize(data_sms.content);
            if(G.isEmpty(content)){
                content = "（未填写短信内容）";
            }
            var contentStr=content.length>25?content.substr(0,25)+'...':content;
            var operator=G.normalize(data_sms.userName);
            var name="";
            var contactGroupDTOs=G.normalize(data_sms.contactGroupDTOs);
            for(var j=0;j<contactGroupDTOs.length;j++){
                var contactGroupDTO=contactGroupDTOs[j];
                name+=contactGroupDTO.name;
                name+=";";
            }
            var contactDTOs=G.normalize(data_sms.contactDTOs);
            for(var j=0;j<contactDTOs.length;j++){
                var contactDTO=contactDTOs[j];
                var contactName = G.normalize(contactDTO.name);
                name+=G.isEmpty(contactName)?"未命名":contactName;
                name+="<"+contactDTO.mobile+">";
                name+=";";
            }
            if((contactDTOs== null || contactDTOs.length == 0) && (contactGroupDTOs == null || contactGroupDTOs.length == 0)){
                name += "（收件人未填写）";
            }
            var nameStr=name.length>15?name.substr(0,15)+'...':name;
            trStr+='<tr class="sms_item">'+
                    '<td class="item_chk_td"><div class="news-01">'+
                    '<input class="sms_id" type="hidden" value="'+smsId+'"/>'+
                    '<input class="itemChk" type="checkbox" rowStart="'+(++rowStart)+'"/>'+
                    '</div></td>'+
                    '<td title="'+name+'">'+nameStr+'</td>'+
                    '<td title="'+content+'">'+contentStr+'</td>'+
                    '<td align="center"> '+operator+'</td>'+
                    '<td align="center" valign="top">'+sendTimeStr+'</td>'+
                    '</tr>';
        }
    }
    $("#smsTable").append(trStr);
}

$(function(){
    var delMessages = {
        SMS_SENT:{
            DEL_BTN_UNCHECK:"请选择要删除的短信。",
            DEL_BTN_CONFIRM:"是否确认要删除信息？"
        },
        SMS_SEND:{
            DEL_BTN_UNCHECK:"请选择要删除的定时短信。",
            DEL_BTN_CONFIRM:"友情提示：您是否确认删除该定时短信，删除后则无法发送！"
        },
        SMS_DRAFT:{
            DEL_BTN_UNCHECK:"请选择要删除的短信草稿。",
            DEL_BTN_CONFIRM:"友情提示：您是否确认删除该短信草稿?"
        }
    };
    $("#sendSmsBtn").live("click",function(){
        if($(".itemChk:checked").length==0){
            nsDialog.jAlert("请选择要转发的短信。");
            return;
        }
        if($(".itemChk:checked").length>1){
            nsDialog.jAlert("对不起，只可选择一条短信转发，请重新选择。");
            return;
        }
        var smsId=$(".itemChk:checked").closest("tr").find(".sms_id").val();
        if(!G.isEmpty(smsId)){
            window.location.href="sms.do?method=smswrite&smsId="+smsId;
        }
    });

    var smsPageType = $("#smsType").val();
    $("#delSmsBtn").live("click",function(){
        var _$self=$(this);
        if(_$self.attr("lock")){
            return;
        }
        _$self.attr("lock","lock");
        if($(".itemChk:checked").length==0){
            nsDialog.jAlert(delMessages[smsPageType]["DEL_BTN_UNCHECK"]);
            _$self.removeAttr("lock");
            return;
        }
        var idArray=new Array();
        $(".itemChk:checked").each(function(){
            var smsId=$(this).closest("tr").find(".sms_id").val();
            if(!G.isEmpty(smsId)){
                idArray.push(smsId);
            }
        });
        if(idArray.length<=0) {
            _$self.removeAttr("lock");
            return;
        }
        nsDialog.jConfirm(delMessages[smsPageType]["DEL_BTN_CONFIRM"],"友情提示",function(flag){
            if(flag){
                APP_BCGOGO.Net.asyncAjax({
                    url:"sms.do?method=deleteSms",
                    type: "POST",
                    cache: false,
                    data:{
                        smsIds:idArray.toString()
                    },
                    dataType: "json",
                    success: function (result) {
                        if(!result.success){
                            nsDialog.jAlert(result.msg);
                            return;
                        }
                        window.location.reload();
                    },
                    error:function(){
                        nsDialog.jAlert("网络异常。");
                    }
                });
            }else{
                _$self.removeAttr("lock");
            }
        },false);
    });

    $("#cancelSendBtn").live("click",function(){
        var _$self=$(this);
        if(_$self.attr("lock")){
            return;
        }
        _$self.attr("lock","lock");
        if($(".itemChk:checked").length==0){
            nsDialog.jAlert("请选择要取消发送的短信。");
            _$self.removeAttr("lock");
            return;
        }
        var idArray=new Array();
        $(".itemChk:checked").each(function(){
            var smsId=$(this).closest("tr").find(".sms_id").val();
            if(!G.isEmpty(smsId)){
                idArray.push(smsId);
            }
        });
        if(idArray.length<=0) return;
        nsDialog.jConfirm("友情提示：您是否确认取消发送定时短信，取消后该短信将无法发送，并且保存在草稿箱中！","提示",function(flag){
            if(flag){
                APP_BCGOGO.Net.asyncAjax({
                    url:"sms.do?method=cancelSendSms",
                    type: "POST",
                    cache: false,
                    data:{
                        smsIds:idArray.toString()
                    },
                    dataType: "json",
                    success: function (result) {
                        if(!result.success){
                            nsDialog.jAlert(result.msg);
                            return;
                        }
                        window.location.reload();
                    },
                    error:function(){
                        nsDialog.jAlert("网络异常。");
                        _$self.removeAttr("lock");
                    }
                });
            }else{
                _$self.removeAttr("lock");
            }
        },false);
    });

    $(".sms_item td").live("click",function(e){
        if($(e.target).hasClass("itemChk") || $(e.target).hasClass("item_chk_td") || $(e.target).parents(".item_chk_td")[0]) return;
        var smsId=$(this).closest("tr").find(".sms_id").val();
        if(!G.isEmpty(smsId)){
            var smsType=$("#smsType").val();
            if(smsType=="SMS_DRAFT"){
                window.location.href="sms.do?method=smswrite&smsId="+smsId+"&smsType="+smsType;
            }else{
                var rowStart=$(this).closest("tr").find(".itemChk").attr("rowStart")
                window.location.href="sms.do?method=toSmsDetail&smsId="+smsId+"&rowStart="+rowStart;
            }
        }
    });

    $("#doSendBtn").click(function(){
        var _$self=$(this);
        if(_$self.attr("lock")){
            return;
        }
        _$self.attr("lock","lock");
        if($(".itemChk:checked").length==0){
            nsDialog.jAlert("请选择要马上发送的短信。");
            _$self.removeAttr("lock");
            return;
        }
        var idArray=new Array();
        $(".itemChk:checked").each(function(){
            var smsId=$(this).closest("tr").find(".sms_id").val();
            if(!G.isEmpty(smsId)){
                idArray.push(smsId);
            }
        });
        if(idArray.length<=0) return;
        nsDialog.jConfirm("是否确认要马上发送此短信？", null, function(flag){
            if(flag){
                APP_BCGOGO.Net.asyncAjax({
                    url:"sms.do?method=doSendSms",
                    type: "POST",
                    cache: false,
                    data:{
                        smsIds:idArray.toString()
                    },
                    dataType: "json",
                    success: function (result){
                        if(!result.success){
                            nsDialog.jAlert(result.msg);
                            return;
                        }
                        toSmsSendFinish(result.dataStr);
                    },
                    error:function(){
                        nsDialog.jAlert("网络异常。");
                        _$self.removeAttr("lock");
                    }
                });
            }else{
                 _$self.removeAttr("lock");
            }
        });
    });

    $("#searchSmsBtn").click(function(){
        querySms();
    });
    //init
    querySms();

});

function querySms(){
    var smsType=G.normalize($("#smsType").val());
    var keyWord=$.trim($("#sms_keyword_input").val());
    var url="sms.do?method=querySms";
    var data={
        smsType:smsType,
        keyWord:keyWord,
        startPageNo:1,
        pageSize:10
    };
    APP_BCGOGO.Net.asyncAjax({
        url:url,
        type: "POST",
        cache: false,
        data:data,
        dataType: "json",
        success: function (result) {
            initSmsList(result);
            initPage(result,"_initSmsList",url, null, "initSmsList", '', '',data,null);
        },
        error:function(){
            nsDialog.jAlert("网络异常。");
        }
    });
}

</script>
</head>
<body class="bodyMain">
<input id="smsType" type="hidden" value="${smsType}"/>
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
            <c:when test="${smsType=='SMS_SEND'}">
                <jsp:include page="smsNavi.jsp">
                    <jsp:param name="currPage" value="smsSend" />
                </jsp:include>
            </c:when>
            <c:otherwise>
                <jsp:include page="smsNavi.jsp">
                    <jsp:param name="currPage" value="smsDraft" />
                </jsp:include>
            </c:otherwise>
        </c:choose>

        <div class="messageRight">
            <div class="messageRight_radius">

                <div class="addressList">
                    <c:if test="${smsType=='SMS_SENT'}">
                        <a id="sendSmsBtn" class="main-btn">转发短信</a>
                    </c:if>
                    <c:if test="${smsType=='SMS_SEND'}">
                        <a id="doSendBtn" class="main-btn">马上发送</a>
                        <a id="cancelSendBtn" class="assis-btn">取消发送</a>
                    </c:if>
                    <a id="delSmsBtn" class="assis-btn">删 除</a>
                </div>
                <div class="txt_border">
                    <input id="sms_keyword_input" type="text" style="width:150px;" placeholder="请输入关键字" autocomplete="off">
                    <div id="searchSmsBtn" class="i_search"></div>
                </div>


                <div class="clear i_height"></div>
                <table id="smsTable" width="796" border="0" cellspacing="0" class="news-table">
                    <colgroup valign="top">
                        <col width="30" />
                        <col width="180" />
                        <col/>
                        <col width="80" />
                        <col width="135" />
                    </colgroup>
                    <tr class="news-thbody">
                        <td align="center"><input class="select_all" type="checkbox" /></td>
                        <td align="center"> 收信人</td>
                        <td align="center">短信内容</td>
                        <td align="center">发送操作人</td>
                        <c:choose>
                            <c:when test="${smsType=='SMS_SENT'}">
                                <td align="center">发送时间</td>
                            </c:when>
                            <c:when test="${smsType=='SMS_SEND'}">
                                <td align="center">指定发送时间</td>
                            </c:when>
                            <c:otherwise>
                                <td align="center">保存时间</td>
                            </c:otherwise>
                        </c:choose>
                    </tr>
                </table>
                <div class="i_pageBtn" style="float:right;">
                    <bcgogo:ajaxPaging
                            url="sms.do?method=querySms"
                            data="{
                               startPageNo:1,
                               maxRows:10,
                             }"
                            postFn="initSmsList"
                            display="none"
                            dynamical="_initSmsList"/>
                </div>
                <div class="clear"></div>
            </div>
            <div class="clear i_height"></div>
        </div>
    </div>
</div>

<div id="mask" style="display:block;position: absolute;"></div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>