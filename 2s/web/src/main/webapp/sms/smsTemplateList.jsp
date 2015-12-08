<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 13-12-23
  Time: 下午4:21
  To change this template use File | Settings | File Templates.
--%>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>短信模板</title>

<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
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
<script type="text/javascript" src="js/smsWrite<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/smsWriteUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript">
defaultStorage.setItem(storageKey.MenuUid, "WEB.CUSTOMER_MANAGER.SMS_MANAGER");
defaultStorage.setItem(storageKey.MenuCurrentItem,"<a href='sms.do?method=toSmsTemplateList' style='color: #007CDA;cursor:pointer;font-size:14px;height: 30px;line-height: 30px;'>短信模板</a>");

function initSmsTemplateList(result){
    $("#smsTemplateTable tr:gt(0)").remove();
    if(G.isEmpty(result)){
        return;
    }
    var smsTemplates=result.results;
    $("#templateNumSpan").text("共"+result.pager.totalRows+"条");
    var trStr="";
    if(G.isEmpty(smsTemplates)){
        trStr+="<tr><td colspan='8' style='text-align: center;'>没有模板数据。</td></tr>";
        $("#smsTemplateTable").append(trStr);
        return;
    }
    for(var i=0;i<smsTemplates.length;i++){
        var smsTemplate=smsTemplates[i];
        var id=smsTemplate.idStr;
        var name=G.normalize(smsTemplate.name);
        var content=G.normalize(smsTemplate.content);
        var contentDisplay = content.length>90?content.substr(0, 90)+"...":content;
        trStr+='<tr class="template_item">'+
                '<td class="J-template-check"><div class="news-01">'+
                '<input type="hidden" class="template_id" value="'+id+'"/>'+
                '<input class="itemChk" type="checkbox" />'+
                '</div></td>'+
                '<td class="template_name">'+name+'</td>'+
                '<td class="template_content" title="' + content + '">'+contentDisplay+'</td>'+
                '</tr>';
    }
    $("#smsTemplateTable").append(trStr);
}
$(function(){

    $("#toSmsWriteBtn").click(function(){
        if($(".itemChk:checked").length!=1){
            nsDialog.jAlert("请选择一条模板数据。");
            return;
        }
        var templateId=$(".itemChk:checked").closest("tr").find(".template_id").val();
        if(!G.isEmpty(templateId)){
            window.location.href="sms.do?method=smswrite&templateId="+templateId;
        }
    });

    $("#clearSmsBtn").click(function(){
        $('#smsContent').val('');
        $('#contentLength').text(0);
        $('#smsLength').text(0);
    });

    $("#smsContent").live("keyup",function(){
        _calculateSmsLen();
    });

    function _calculateSmsLen(){
        var smsLimit=67;
        var len=$("#smsContent").val().length;
        $("#contentLength").text(len);
        var totalLen=len+$("#shopPrefix").text().length+1;
        var smsLen=parseInt(totalLen/smsLimit);
        if(len!=0) smsLen++;
        $("#smsLength").text(smsLen);
    }


    $("#delSmsTemplateBtn").click(function(){
        var $itemChks=$(".itemChk:checked");
        if($itemChks.length<=0){
            nsDialog.jAlert("请选择要删除的模板。");
            return;
        }
        var idArray=new Array();
        for(var i=0;i<$itemChks.length;i++){
            var templateId=$($itemChks[i]).closest("tr").find(".template_id").val();
            if(!G.isEmpty(templateId)){
                idArray.push(templateId);
            }
        }
        if(G.isEmpty(templateId)) return;
        nsDialog.jConfirm("友情提示：您是否确认删除该短信模板？",null,function(flag){
            if(flag){
                APP_BCGOGO.Net.asyncAjax({
                    url: "sms.do?method=deleteSmsTemplate",
                    type: "POST",
                    cache: false,
                    data:{
                        templateIds:idArray.toString()
                    },
                    dataType: "json",
                    success: function (result) {
                        if(!result.success){
                            nsDialog.jAlert(result.msg);
                            return;
                        }
//                        for(var i=0;i<$itemChks.length;i++){
//                            $($itemChks[i]).closest("tr").remove();
//                        }
                        window.location.reload();
                    },
                    error:function(){
                        nsDialog.jAlert("网络异常！");
                    }
                });
            }
        });

    });

    $("#addSmsTemplateBtn").click(function(){
        $("#addSmsTemplateAlert").dialog({
            resizable: false,
            draggable:false,
            title: "<span style='font-size: 14px'>新建模板</span>",
            height: 400,
            width: 620,
            modal: true,
            closeOnEscape: false,
//            beforeClose:function(){
//                $("#smsName").val("");
//                $("#smsContent").val("");
//                $("#templateId").val("");
//                 $('#contentLength').text(0);
//        $('#smsLength').text(0);
//            },
            open:function(){
                $("#smsName").val("");
                $("#smsContent").val("");
                $("#templateId").val("");
                $('#contentLength').text(0);
                $('#smsLength').text(0);
            }
        });
    });

    $("#addSmsTemplateAlert #okBtn").click(function(){
        var smsName=$("#smsName").val();
        if(G.isEmpty(smsName)){
            nsDialog.jAlert("模板名不能为空。");
            return;
        }
        var content=$("#smsContent").val();
        if(G.isEmpty(content)){
            nsDialog.jAlert("短信内容不能为空。");
            return;
        }

        APP_BCGOGO.Net.asyncAjax({
            url: "sms.do?method=saveSmsTemplate",
            type: "POST",
            cache: false,
            data:{
                templateId:$("#templateId").val(),
                name:smsName,
                content:content
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
                nsDialog.jAlert("网络异常！");
            }
        });


    });

    $(".template_item td").live("click",function(e){
        if($(e.target).hasClass("itemChk") || $(e.target).hasClass("J-template-check") || $(e.target).parents(".J-template-check")[0]) return;
        var $tr=$(this).closest("tr");
        var template_id=$tr.find(".template_id").val();
        if(!G.isEmpty(template_id)){
            $("#addSmsTemplateAlert").dialog({
                resizable: false,
                draggable:false,
                title: "<span style='font-size: 14px'>编辑模板</span>",
                height: 400,
                width: 620,
                modal: true,
                closeOnEscape: false,
                open:function(){
                    $("#smsName").val($tr.find(".template_name").text());
                    $("#smsContent").val($tr.find(".template_content").attr("title"));
                    $("#templateId").val(template_id);
                     _calculateSmsLen();
                },
                beforeClose:function(){
                    $("#smsName").val("");
                    $("#smsContent").val("");
                    $("#templateId").val();
                }
            });
        }
    });

    $("#addSmsTemplateAlert #cancelBtn").click(function(){
        $("#addSmsTemplateAlert").dialog("close");
    });

    function _queryTemplate(keyWord){
        var url="sms.do?method=getSmsTemplateList";
        var data={
            startPageNo:1,
            keyWord:keyWord
        };
        APP_BCGOGO.Net.asyncAjax({
            url:url,
            type: "POST",
            cache: false,
            data:data,
            dataType: "json",
            success: function (result) {
                initSmsTemplateList(result);
                initPage(result,"_initSmsTemplateList",url, null, "initSmsTemplateList", '', '',data,null);
            },
            error:function(){
                nsDialog.jAlert("网络异常。");
            }
        });

    }

    $("#searchTemplateBtn").click(function(){
        _queryTemplate($.trim($("#template_keyword_input").val()));
    });
    _queryTemplate("");




});
</script>
</head>
<body class="bodyMain">
<input id="pageType" type="hidden" value="${type}"/>
<%@include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
    <div class="mainTitles">
        <div class="titleWords">短信管理</div>
    </div>
    <div class="messageContent">
        <jsp:include page="smsNavi.jsp">
            <jsp:param name="currPage" value="smsTemplate" />
        </jsp:include>

        <div class="messageRight">
            <div class="messageRight_radius">
                <div class="addressList">
                    <a id="addSmsTemplateBtn" class="main-btn">新建模板</a>
                    <a id="delSmsTemplateBtn" class="assis-btn">删 除</a>
                    <a id="toSmsWriteBtn" class="assis-btn">发送短信</a>
                </div>
                <div class="txt_border">
                    <input id="template_keyword_input" type="text" style="width:150px;" placeholder="请输入关键字" autocomplete="off">
                    <div id="searchTemplateBtn" class="i_search"></div>
                </div>
                <div class="clear i_height"></div>
                <div><strong style="font-size:14px;padding-left:10px">短信模板(<span id="templateNumSpan">共0条</span>)</strong></div>
                <table id="smsTemplateTable" width="796" border="0" cellspacing="0" class="news-table">
                    <colgroup valign="top">
                        <col width="30" />
                        <col width="120" />
                        <col/>
                    </colgroup>
                    <tr class="news-thbody">
                        <td align="center"><input class="select_all" type="checkbox"/></td>
                        <td align="center"> 模板名称 </td>
                        <td align="center">短信内容</td>
                    </tr>


                </table>
                <!-- 分页代码 -->
                <div class="i_pageBtn" style="float:right">
                    <bcgogo:ajaxPaging
                            url="sms.do?method=getSmsTemplateList"
                            postFn="initSmsTemplateList"
                            display="none"
                            dynamical="_initSmsTemplateList"/>
                </div>
                <div class="clear"></div>
            </div>
            <div class="clear i_height"></div>
        </div>
    </div>
</div>

<div id="addSmsTemplateAlert"  style="display: none">
    <div  class="messageRight_radius s-alert" style="border:none; background:none; padding:0;">
        <input id="templateId" type="hidden" />
        <div class="content_01" style="border:none;">
            <div class="select_per">
                <span style="float:left; color:#333;"><span class="red_color">*</span>模板名称</span>
                <input id="smsName" name="" type="text"  class="txt balance_left8" placeholder="请输入短信模板名称" style="width:180px;" maxlength="20"/>

            </div>
            <div class="clear height"></div>
            <div class="mes_content">
                <span style="float:left; line-height:20px;"><span class="red_color">*</span>短信内容</span>
                <div class="mes_textarea balance_left8">
                    <textarea id="smsContent" name="" maxlength="500"></textarea>
                    <div id="shopPrefix" style="padding:5px 2px; text-align:right; color:#999;">[${shopName}]</div>
                    <div class="bottom">已输入<strong id="contentLength" class="red_color">0</strong>个字/将分为<span id="smsLength" class="orange_color">0</span>条短信发出
                        <a id="clearSmsBtn" class="blue_color" style="float:right;">清空内容</a>
                    </div>
                </div>
                <div class="clear"></div>
            </div>
            <div class="clear"></div>
            <div class="clear height"></div>
            <div class="button">
                <a id="okBtn" class="btnSure">确&nbsp;定</a>
                <a id="cancelBtn" class="btnSure">取&nbsp;消</a>
            </div>
        </div>
        <div class="clear"></div>
    </div>
</div>


<div id="mask" style="display:block;position: absolute;"></div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>