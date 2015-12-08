
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
<title>写短信</title>

<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/message<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/zTreeStyle<%=ConfigController.getBuildVersion()%>.css"/>
<%--<link rel="stylesheet" type="text/css" href="styles/messGuide<%=ConfigController.getBuildVersion()%>.css"/>--%>
<%--<link rel="stylesheet" type="text/css" href="styles/userGuid<%=ConfigController.getBuildVersion()%>.css"/>--%>
<%--<link rel="stylesheet" type="text/css" href="styles/SMSwrite<%=ConfigController.getBuildVersion()%>.css"/>--%>
<%--<link rel="stylesheet" href="js/components/themes/bcgogo-customerSms-input<%=ConfigController.getBuildVersion()%>.css"/>--%>

<%@include file="/WEB-INF/views/header_script.jsp" %>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery.ztree.core-3.5.min.js"></script>
<script type="text/javascript" src="js/customer<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/txnbase<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-customerSms-input<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-iframe-post<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/smsWrite<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/smsWriteUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript">
defaultStorage.setItem(storageKey.MenuUid, "WEB.CUSTOMER_MANAGER.SMS_MANAGER");
defaultStorage.setItem(storageKey.MenuCurrentItem,"<a href='sms.do?method=smswrite' style='color: #007CDA;cursor:pointer;font-size:14px;height: 30px;line-height: 30px;'>写短信</a>");



function doTemplateSelect(){
    $("#templateSelectorAlert").dialog({
        resizable: true,
        draggable:true,
        title: "短信模板（双击可选择模板）",
        height: 300,
        width: 690,
        modal: true,
        closeOnEscape: false,
//            beforeClose:function(){
//                $("#sendTime").val("");
//            },
        open:function(){
            var url="sms.do?method=getSmsTemplateList";
            var data={
                startPageNo:1,
                pageSize:5
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
//               ,
//            buttons:{
//                "确定":function(){
//                    if ($("#sendTime").val() == "") {
//                        alert("请选择延时发送时间");
//                        return;
//                    }
//                    $(this).dialog("close");
//                    doSmsSend($("#sendTime").val());
//                },
//                "取消":function(){
//                    $(this).dialog("close");
//                }
//
//            }
    });
}



function initSmsTemplateList(result){
    $("#templateTable tr:gt(0)").remove();
    if(G.isEmpty(result)){
        return;
    }
    var smsTemplates=result.results;
    var trStr="";
    if(G.isEmpty(smsTemplates)){
        trStr+="<tr><td colspan='8' style='text-align: center;'>没有模板数据。</td></tr>";
        $("#templateTable").append(trStr);
        return;
    }
    for(var i=0;i<smsTemplates.length;i++){
        var smsTemplate=smsTemplates[i];
        var id=smsTemplate.idStr;
        var name=G.normalize(smsTemplate.name);
        var content=G.normalize(smsTemplate.content);
        trStr+='<tr>'+
                '<td style="padding-left:10px;">'+name+'</td>'+
                '<td class="template_content" title="' + content + '">'+content+'</td>'+
                '</tr>';
    }
    $("#templateTable").append(trStr);

}
$().ready(function () {

    $("#templateTable tr:not(.tabTitle)").live("dblclick","",function(){
        $("#templateSelectorAlert").dialog("close");
         var smsWriteSelection= document.getElementById("iframe_smsWriteSelection").contentWindow;
        var $smsContent=$(smsWriteSelection.document).find("#smsContent");
        $smsContent.val($(this).find(".template_content").text());
        smsWriteSelection._calculateSmsLen();
    });

    $("#searchContactBtn").bind("click",function(e){
        if(G.isEmpty($("#contactSerachWord").val())){
            $("#searchContactInfoResultDiv").hide();
            $("#searchContactInfoResultDiv").find("li").hide();
            $("#allContactInfoDiv").show();
        }else{
            var count = 0;
            $("#searchContactInfoResultDiv").find("li").each(function(){
                if($(this).attr("search-content").indexOf($("#contactSerachWord").val().trim()) > -1){
                    $(this).show();
                    count++;
                }else{
                    $(this).hide();
                }
            });
            $("#searchContactCount").text(count);
            $("#searchContactInfoResultDiv").show();
            $("#allContactInfoDiv").hide();
        }
    });
    $("#deleteRecentlyUsedContactBtn").bind("click",function(e){
        e.preventDefault();
        nsDialog.jConfirm("确定是否删除所有最近联系人?", null, function (returnVal) {
            if (returnVal) {
                APP_BCGOGO.Net.syncPost({
                    url: "contact.do?method=deleteAllRecentlyUsedContact",
                    dataType: "json",
                    success: function (result) {
                        $("#recentlyUsedContactUl").empty();
                        $("#moreRecentlyUsedContactBtn").remove();
                        $("#recentlyUsedContactUl").text('暂无');

                    },
                    error: function () {
                        nsDialog.jAlert("数据异常，请刷新页面！");
                    }
                });
            }
        });

    });

    $("#contactSerachWord")
            .bind('click', function () {
                getContactSingleFieldSuggestion($(this));
            })
            .bind('keyup', function (event) {
                if(G.isEmpty($(this).val())){
                    $("#searchContactInfoResultDiv").hide();
                    $("#searchContactInfoResultDiv").find("li").hide();
                    $("#allContactInfoDiv").show();
                }
                var eventKeyCode = event.which || event.keyCode;
                if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                    getContactSingleFieldSuggestion($(this),eventKeyCode);
                }
            });

    function getContactSingleFieldSuggestion($domObject, keycode) {
        var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
        var dropList = App.Module.droplist;
        if(G.isEmpty(searchWord)){
            dropList.hide();
            dropList.clear();
            return;
        }
        dropList.setUUID(G.generateUUID());
        var ajaxData = {
            searchWord: searchWord,
            searchField: "multiFieldToSingle",
            uuid: dropList.getUUID()
        };
        var ajaxUrl = "contact.do?method=querySmsContactSuggestion";
        App.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
            if(!G.isEmpty(result.data[0])){
                G.completer(
                        {
                            'domObject':$domObject[0],
                            'keycode':keycode,
                            'title':result.data[0].label
                        }
                );
            }
            dropList.show({
                "selector": $domObject,
                "data": result,
                "onSelect": function (event, index, data) {
                    $domObject.val(data.label);
                    $domObject.css({"color": "#000000"});
                    dropList.hide();
                    $("#searchContactBtn").click();
                }
            });
        });
    }


    $("#moreRecentlyUsedContactBtn").bind("click",function(e){
        e.preventDefault();
        $(this).hide();
        $("#recentlyUsedContactUl").find(".item_group_contact").show();
    });

    $("#moreSmsContactBtn").bind("click",function(e){
        e.preventDefault();
        $(this).hide();
        $("#smsContactUI").find(".item_group_contact").show();
    });

});

function toSmsSendFinish(smsId){
    window.location.href="sms.do?method=toSmsSendFinish&smsId="+smsId;
}
</script>

<script type="text/javascript">

function postSaveSmsContact(contact){
    var idStr=contact.idStr;
    var name=contact.name;
    var mobile=contact.mobile;
    var smsWriteSelection= document.getElementById("iframe_smsWriteSelection").contentWindow;
    var $currentToken=smsWriteSelection.$currentToken;
    if($currentToken){
        name=G.isEmpty(name)?"未命名":name;
        var tLabel=name+"<"+mobile+">";
        $currentToken.attr("data-value",tLabel);
        $currentToken.find(".token-label").text(tLabel);
        $currentToken.find(".close").remove();
        $currentToken.attr("disabled","disabled");
        var paramStr='<input type="hidden" value="contact" name="type" class="token_param">';
        paramStr+='<input type="hidden" value="CONTACT_'+idStr+'" name="contactId" class="token_param">';
        paramStr+='<input type="hidden" value="'+name+'" name="name" class="token_param">';
        paramStr+='<input type="hidden" value="'+mobile+'" name="mobile" class="token_param">';
        $currentToken.append(paramStr);
        $currentToken.addClass("token-un-editable");
    }
    addNewContactToList(contact);
}


$(function(){
    //post提交数据给iframe
    IframePost.doPost(
        {
            Url: "/web/sms.do?method=toSmsWriteSelection&smsId=${smsId}&templateId=${templateId}&templateFlag=${templateFlag}&remindEventId=${remindEventId}&mobile=${mobile}&shopPlanId=${shopPlanId}&excludeContact=${excludeContact}",
            Target: document.getElementById("iframe_smsWriteSelection").contentWindow,
            PostParams: {contactName:"${contactName}",smsContent:"${smsContent}", contactIds: "${contactIds}"}
        });

    $(".contact_tab").click(function(){
        $(".contact_tab_div").hide();
        if($(this).closest(".contact_tab_div").hasClass("contact_tab_container")){
            $(".contact_group_tab_container").show();
        }else{
            $(".contact_tab_container").show();
        }
    });

    $("#addContactAlert .ok_btn").live("click",function() {
        var name = $("#addContactAlert .contact_name").val();
        if (G.isEmpty(name)) {
            nsDialog.jAlert("联系人名不能为空。")
            return;
        }
        var mobile = $("#addContactAlert .contact_mobile").val();
        if (!APP_BCGOGO.Validator.stringIsMobile(mobile)) {
            nsDialog.jAlert("手机号有误，请重新输入。");
            return;
        }
        var specialIdStr=$("#addContactAlert .contact_id").val();
        var contactId=G.isEmpty(specialIdStr)?"":specialIdStr.split("_")[1];
        var data = {
            contactId:contactId,
            name:name,
            mobile:mobile
        };
        var flag=false;
        if ($("#customerSupplierFlag").attr("checked")) {
            data['customerSupplierFlag'] = $("#customerSupplierSelector").val();  //传递新增客户的标志
            flag=true;
        }
        var url="sms.do?method=saveSmsContact";
        if(!G.isEmpty(specialIdStr)){
            url="sms.do?method=updateSmsContact";
        }
        $("#addContactAlert").dialog("close");
        APP_BCGOGO.Net.syncAjax({
            url: url,
            type: "POST",
            cache: false,
            data:data,
            dataType: "json",
            success: function (result) {
                if (!result.success) {
                    nsDialog.jAlert(result.msg);
                    return;
                }
                var contact=result.data;
                if(G.isEmpty(specialIdStr)){   //联系人输入框处编辑
                    postSaveSmsContact(contact);
                }else{ //图标处编辑
                    refreshPageContactInfo(contact,flag);
                }
                nsDialog.jAlert("保存成功！");
            },
            error:function() {
                nsDialog.jAlert("网络异常！");
                $currentToken="";
            }
        });

    });

    function refreshPageContactInfo(contact,flag){
        if(G.isEmpty(contact)||G.isEmpty(contact.idStr)){
            return;
        }
        $(".contact_edit_btn").each(function(){
            var $contactItem=$(this).closest(".item_group_contact");
            var contactId=$contactItem.attr("contactId");
            if(contactId.split("_")[1]==contact.idStr){
                var name=contact.name;
                var mobile=contact.mobile;
                $contactItem.attr("contactId",contact.specialIdStr);
                $contactItem.attr("name",name);
                $contactItem.attr("mobile",mobile);
                var tLabel=(G.isEmpty(name)?"未命名":name)+"<"+mobile+">";
                $contactItem.find(".t_label").text(tLabel);
                if(flag){
                    $(this).remove()
                }
            }
        });
    }

    $("#addContactAlert .cancel_btn").live("click",function() {
        $("#addContactAlert").dialog("close");
    });

    $(".item_group").click(function(){
        var name=$(this).text();
        var groupId=$(this).attr("groupId");
        var data={
            label: name,
            value:name,
            params:{
                type:"group",
                groupId:groupId
            },
            editable:false
        };
        var smsWriteSelection= document.getElementById("iframe_smsWriteSelection").contentWindow;

        smsWriteSelection.createToken(data);
    });

    $(".contact_edit_btn").live("click",function(){
        var $contactItem=$(this).closest(".item_group_contact");
        var contactId=$contactItem.attr("contactId");
        if(G.isEmpty(contactId)) return;
        var name=$contactItem.attr("name");
        var mobile=$contactItem.attr("mobile");
        $("#addContactAlert").dialog({
            resizable: false,
            draggable:true,
            title: "编辑联系人",
            height: 220,
            width: 400,
            closeText: "hide",
            modal: true,
            closeOnEscape: false,
            open:function(){
                name=name=="未命名"?"":name;
                $("#addContactAlert .contact_name").val(name);
                $("#addContactAlert .contact_id").val(contactId);
                $("#addContactAlert .contact_mobile").val(mobile);
                var source=contactId.split("_")[0];
                if(source=="VEHICLE"||source=="CUSTOMER"){
                    $("#customerSupplierFlag").attr("checked", true);
                    $("#customerSupplierFlag").attr("disabled", "disabled");
                    $("#customerSupplierSelector").val("CUSTOMER");
                    $("#customerSupplierSelector").attr("disabled", "disabled");
                }else if(source=="SUPPLIER"){
                    $("#customerSupplierFlag").attr("checked", true);
                    $("#customerSupplierFlag").attr("disabled", "disabled");
                    $("#customerSupplierSelector").val("SUPPLIER");
                    $("#customerSupplierSelector").attr("disabled", "disabled");
                }
            },
            beforeClose:function() {
                $("#addContactAlert .contact_name").val("");
                $("#addContactAlert .contact_id").val("");
                $("#addContactAlert .contact_mobile").val("");
                $("#customerSupplierFlag").attr("checked", false);
                $("#customerSupplierFlag").removeAttr("disabled");
                $("#customerSupplierSelector").val("CUSTOMER");
            }
        });
    });

    $(".item_group_contact").live("click",function(e){
        if($(e.target).hasClass("contact_edit_btn")) return;
        var name= $(this).attr("name");
        name=G.isEmpty(name)?"未命名":name;
        var mobile=$(this).attr("mobile");
        var contactId=$(this).attr("contactId");
        var  label=name+'<'+mobile+'>';
        var data={
            label: label,
            value:label,
            params:{
                type:"contact",
                contactId:contactId,
                name:name,
                mobile:mobile
            },
            editable:false
        };
        var smsWriteSelection= document.getElementById("iframe_smsWriteSelection").contentWindow;

        smsWriteSelection.createToken(data);
        adjustHeight();
    });

    initContactGroupZTree();

    $("#customerSupplierSelector").bind("change",function(e){
        $("#saveContactPromptSpan").text($(this).find("option:selected").text());
    });
});

function adjustHeight(){
    var $frame = $(document.getElementById("iframe_smsWriteSelection"));
    $frame.attr("height", $frame.contents().height() + "px");
}

function setFontCss(treeId, treeNode) {
    return treeNode.level == 0 ? {"font-weight":"bold", "width":"161px"} : {};
}

function initContactGroupZTree() {
    var setting = {
        view: {
            showIcon: false,
            showLine: false,
            addDiyDom: addDiyDom,
            fontCss: setFontCss
        },
        data: {
            simpleData: {
                enable: true
            }
        },
        callback: {
            onClick: onTreeClick
        }
    };

    APP_BCGOGO.Net.syncPost({
        url: "contact.do?method=getContactGroupTreeNode",
        dataType: "json",
        success:function(result){
            if(!result.success){
                nsDialog.jAlert(result.msg);
                return;
            }
            var zNodes;
            if (!G.isEmpty(result) && !G.isEmpty(result.data)) {
                zNodes = result.data;
            }
            $.fn.zTree.init($("#contactGroupZTree"), setting, zNodes);
        },
        error:function(){
            nsDialog.jAlert("网络异常。");
        }
    });


}

function addDiyDom(treeId, treeNode) {
    var IDMark_A = "_a";
    var aObj = $("#" + treeNode.tId + IDMark_A);
    if (Number(treeNode.pId) == 0) {
        if(G.rounding(treeNode.nodeNum) <= 0) {
            aObj.after("<span style='color: #BBBBBB;' >全选</span>");
        } else {
            aObj.after("<a id='diyBtn_" +treeNode.id+ "' style='color: #007CDA;'>全选</a>");
        }

        var btn = $("#diyBtn_"+treeNode.id);
        if (btn) btn.bind("click", function(){
            var name=treeNode.groupName;
            var nodeNum=G.rounding(treeNode.nodeNum);
            if(nodeNum<=0){
                nsDialog.jAlert(name+"成员为空！");
                return;
            }
            var groupId=treeNode.contactGroupIdStr;
            var data={
                label: name,
                value:name,
                params:{
                    type:"group",
                    groupId:groupId,
                    name:name
                },
                editable:false
            };
            var smsWriteSelection= document.getElementById("iframe_smsWriteSelection").contentWindow;
            smsWriteSelection.createToken(data);
            adjustHeight();
           if(treeNode.appCustomerFlag){
              var selection=$(smsWriteSelection.document);
              selection.find("#appChk").removeAttr("disabled");
               selection.find("#smsChk").removeAttr("disabled");
               selection.find("#appChk").attr("checked",true);
           }
        });
    }
}

function onTreeClick(event, treeId, treeNode, clickFlag) {
    var smsWriteSelection= document.getElementById("iframe_smsWriteSelection").contentWindow;
    if(!treeNode.groupNodeFlag){
        smsWriteSelection.onTreeClick(event,treeId,treeNode,clickFlag);
    }else if(treeNode.isParent){
        var zTreeObj = $.fn.zTree.getZTreeObj(treeId);
        zTreeObj.expandNode(treeNode, !treeNode.open);
    }
    adjustHeight();
}



</script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>

<c:if test="${from_sendPromotionsMsg}">
    <c:forEach items="${customerDTOList}" var="customer" varStatus="status">
        <input type="hidden" class="pCustomer" mobile="${customer.mobile}" customerName="${customer.name}" userId="${customer.userId}" />
    </c:forEach>
</c:if>

<input id="remindEventId" type="hidden" value="${remindEventId}"/>
<input id="smsSendScene" type="hidden" value="${smsSendScene}"/>
<div class="i_main clear">
    <div class="mainTitles">
        <div class="titleWords">短信管理</div>
    </div>
    <div class="messageContent">

        <jsp:include page="smsNavi.jsp">
            <jsp:param name="currPage" value="smsWrite" />
        </jsp:include>

        <div class="messageRight">
            <div class="messageRight_radius">
                <div class="content_01">
                    <div class="clear height"></div>
                    <form id="smsSendForm" action="sms.do?method=sendSms" method="post">
                        <input type="hidden" name="phoneNumbers" id="phoneNumbers">
                        <div class="mes_content">
                            <div>
                                <%--<iframe id="iframe_smsWriteSelection" style="" allowtransparency="true" width="540px" height="410px" frameborder="0"--%>
                                        <%--src="/web/sms.do?method=toSmsWriteSelection&smsId=${smsId}&contactIds=${contactIds}&templateId=${templateId}&templateFlag=${templateFlag}--%>
                                        <%--&smsContent=${smsContent}&remindEventId=${remindEventId}&contactName=${contactName}&mobile=${mobile}&shopPlanId=${shopPlanId}&excludeContact=${excludeContact}">--%>

                                <%--</iframe>--%>
                                <iframe id="iframe_smsWriteSelection" style="" allowtransparency="true" width="540px" height="410px" frameborder="0" src="about:blank"></iframe>
                            </div>
                        </div>
                    </form>
                    <div class="clear"></div>
                </div>
                <div class="content_02 contact_tab_div contact_tab_container" style="height: 90%">
                    <div class="contact_title">
                        <ul>
                            <li class="tab-click contact_tab">联系人</li>
                            <li class="tab-normal contact_tab">联系人组</li>
                        </ul>
                    </div>
                    <div class="clear"></div>
                    <div class="contact_body">
                        <input id="contactSerachWord" type="text" class="search-scope" /><input type="button" id="searchContactBtn" style="cursor: pointer;float: right" value="查找"  class="search-btn"/>
                        <div class="clear"></div>
                    </div>
                    <div id="allContactInfoDiv">
                        <div class="contact_body">
                            <c:if test="${empty recentlyUsedContactDTOs}">
                                <div class="contact_title2">最近联系</div>
                                <ul id="recentlyUsedContactUl">
                                    暂无
                                </ul>
                            </c:if>
                            <c:if test="${not empty recentlyUsedContactDTOs}">
                                <div class="contact_title2"><a class=" blue_color" id="deleteRecentlyUsedContactBtn">清空</a>最近联系</div>
                                <ul id="recentlyUsedContactUl" style="height: 154px;overflow-y:auto;">
                                    <c:forEach items="${recentlyUsedContactDTOs}" var="recentlyUsedContactDTO" varStatus="status">
                                        <c:if test="${not empty recentlyUsedContactDTO.mobile}">
                                            <li class="item_group_contact" style="cursor:pointer;display: ${status.index<7?'':'none'}" name="${recentlyUsedContactDTO.name}" mobile="${recentlyUsedContactDTO.mobile}" contactId="${recentlyUsedContactDTO.specialIdStr}">
                                                <span class="t_label">  ${empty recentlyUsedContactDTO.name?"未命名":recentlyUsedContactDTO.name}<${recentlyUsedContactDTO.mobile}></span>
                                                <c:if test="${recentlyUsedContactDTO.dataSourceFrom=='OTHER'}">
                                                    <a class="contact_edit_btn"></a>
                                                </c:if>
                                            </li>
                                        </c:if>
                                    </c:forEach>
                                </ul>
                            </c:if>
                            <c:if test="${fn:length(recentlyUsedContactDTOs)>7}">
                                <div id="moreRecentlyUsedContactBtn" style="cursor: pointer"><a class="blue_color">显示更多</a><img src="images/rightArrow.png" /></div>
                            </c:if>
                            <div class="clear"></div>
                        </div>
                        <div class="line_separated"></div>
                        <div class="contact_body">
                            <div class="contact_title2">所有联系人（${fn:length(allContactDTOs)}）</div>
                            <ul id="smsContactUI" style="height: 154px;overflow-y:auto;">
                                <c:if test="${not empty allContactDTOs}">
                                    <c:forEach items="${allContactDTOs}" var="contactDTO" varStatus="status">
                                        <li class="item_group_contact" style="cursor:pointer;display: ${status.index<7?'':'none'}" name="${contactDTO.name}" mobile="${contactDTO.mobile}" contactid="${contactDTO.specialIdStr}">
                                            <span class="t_label">${empty contactDTO.name?"未命名":contactDTO.name}<c:if test="${not empty contactDTO.mobile}"><${contactDTO.mobile}></c:if></span>
                                            <c:if test="${contactDTO.dataSourceFrom=='OTHER'}">
                                                <a class="contact_edit_btn"></a>
                                            </c:if>
                                        </li>
                                    </c:forEach>
                                </c:if>
                                <c:if test="${empty allContactDTOs}">
                                    暂无
                                </c:if>
                            </ul>
                            <div class="clear i_height"></div>
                            <c:if test="${fn:length(allContactDTOs)>7}">
                                <div id="moreSmsContactBtn"><a class="blue_color">显示更多</a> <img src="images/rightArrow.png" /></div>
                            </c:if>
                            <div class="clear"></div>
                            <div class="i_height"></div>
                            <%--<c:forEach items="${contactGroupDTOs}" var="contactGroupDTO" varStatus="status">--%>
                            <%--<div class="group-item">--%>
                            <%--<input class="group_item_btn" type="button" style="width:20px" groupType="${contactGroupDTO.contactGroupType}"/>--%>
                            <%--<a groupId="${contactGroupDTO.idStr}" class="blue_color item_group">${contactGroupDTO.name}</a>--%>
                            <%--</div>--%>
                            <%--</c:forEach>--%>
                        </div>
                    </div>
                    <div id="searchContactInfoResultDiv" style="display: none">
                        <div class="line_separated"></div>
                        <div class="contact_body">
                            <div class="contact_title2">找到的联系人（<span id="searchContactCount">${fn:length(allContactDTOs)}</span>）</div>
                            <ul style="height: 399px;overflow-y:auto;">
                                <c:if test="${not empty allContactDTOs}">
                                    <c:forEach items="${allContactDTOs}" var="contactDTO" varStatus="status">
                                        <li class="item_group_contact" style="cursor:pointer;display: none" name="${contactDTO.name}" mobile="${contactDTO.mobile}" contactid="${contactDTO.specialIdStr}"
                                                search-content="${contactDTO.name} ${contactDTO.customerOrSupplierName} ${contactDTO.mobile}">
                                            ${empty contactDTO.name?"未命名":contactDTO.name}<c:if test="${not empty contactDTO.mobile}"><${contactDTO.mobile}></c:if>
                                        </li>
                                    </c:forEach>
                                </c:if>
                            </ul>
                        </div>
                    </div>
                </div>

                <div class="content_02 contact_tab_div contact_group_tab_container" style="height: 90%;display: none">
                    <div class="contact_title">
                        <ul>
                            <li class="tab-normal contact_tab">联系人</li>
                            <li class="tab-click contact_tab">联系人组</li>
                        </ul>
                    </div>
                    <div class="clear"></div>
                    <div class="shop store_list store_kindList" style="height: 410px;overflow-y:auto;">
                        <ul id="contactGroupZTree" class="ztree"></ul>
                    </div>
                </div>

                <div class="clear"></div>
            </div>
        </div>
    </div>
</div>
<div id="div_operate" class="selectTime" style="display:none;">
    <a>半个月</a>
    <a>1个月</a>
    <a>2个月</a>
    <a>3个月</a>
</div>

<div id="div_money" class="selectMoney" style="display:none;">
    <a>10</a>
    <a>20</a>
    <a>50</a>
    <a>100</a>
</div>

<div id="templateSelectorAlert" class="alertMain" style="display: none">
    <div class="height"></div>
    <table id="templateTable" cellpadding="0" cellspacing="0" class="tabRecord template-table">
        <col width="120">
        <col>
        <tr class="tabTitle">
            <td style="padding-left:10px;">模板名称</td>
            <td>短信内容</td>
        </tr>


    </table>
    <div class="height"></div>
    <div class="i_pageBtn" style="float:right">
        <bcgogo:ajaxPaging
                url="sms.do?method=getSmsTemplateList"
                postFn="initSmsTemplateList"
                display="none"
                dynamical="_initSmsTemplateList"/>
    </div>
</div>

<div id="addContactAlert" style="display: none;">
    <div class="s-alert" style="padding-left: 10px">
        <table width="100%" border="0" cellspacing="0" cellpadding="0" class="addMessage_table">
            <tr>
                <td width="21%">
                    <span class="divTit alert_divTit"><span class="red_color">*</span>联系人</span>
                    <input type="hidden" class="contact_id"/>
                </td>
                <td width="79%">
                    <span class="divTit alert_divTit">
                   <input type="text" class="contact_name txt" maxlength="20" style="width: 148px"/>
                    </span>
                </td>
            </tr>
            <tr>
                <td><span class="divTit alert_divTit"><span class="red_color">*</span>手机号</span></td>
                <td><span class="divTit alert_divTit">
            <input type="text" class="contact_mobile txt" style="width: 148px"/>
        </span></td>
            </tr>
            <tr>
                <td colspan="2">
                    <span style="float:left; margin-left:24px;">加为</span><input id="customerSupplierFlag" type="checkbox" style="float:left; margin-left:3"/>
                    <select id="customerSupplierSelector" style="width: 154px;margin-left: 10px">
                        <option value="CUSTOMER">客户</option>
                        <option value="SUPPLIER">供应商</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td  height="25" colspan="2">
                    <div>
                        <span style="margin-left: 50px" class="gray_color">（勾选后系统自动保存为<span id="saveContactPromptSpan">客户</span>信息）</span>
                    </div>
                </td>
            </tr>
        </table>
        <div class="clear height"></div>
        <div class="button">
            <a class="ok_btn btnSure">确&nbsp;定</a>
            <a class="cancel_btn btnSure">取&nbsp;消</a>
        </div>
    </div>
</div>



<%--<div id="writer_mask" style="display: none; position: absolute; top: 0px; left: 0px; width: 100%; height: 100%; z-index: 3; background-color: rgb(0, 0, 0); opacity: 0.4;">--%>
</div>
<%--<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:200px; top:200px; display:none;"--%>
<%--allowtransparency="true" width="890px" height="350px" frameborder="0" src="" scrolling="no"></iframe>--%>
<iframe id="iframe_PopupBox" scrolling="no" style="position:absolute;z-index:5; left:400px; top:350px; display:none;" allowtransparency="true" width="850px" height="500px" frameborder="0" src=""></iframe>

<script type="text/javascript">
    <%
     String smsBalanceTip = (String)request.getAttribute("smsBalanceTip");
     if(smsBalanceTip!=null){
         if(smsBalanceTip.equals("debt")){
               Double debt = (Double)request.getAttribute("smsDebt");
               if(debt>=0.3){

    %>
    nsDialog.jAlert("您的短信欠款已达<%=(request.getAttribute("smsDebt"))%>元,无法正常发送短信,请及时充值!");
    <%
               }else{
    %>
    nsDialog.jAlert("您的短信已欠款<%=(request.getAttribute("smsDebt"))%>元,请及时充值!");
    <%
               }
        }else if(smsBalanceTip.equals("notEnough")){
    %>
    nsDialog.jAlert("您的短信余额不足,部分短信可能无法正常发送,请及时充值!");
    <%
        }else if(smsBalanceTip.equals("lessThan5")){
    %>
    nsDialog.jAlert("您的短信余额不足5元,请及时充值!");
    <%
        }
    }
    %>
</script>
<%@include file="/WEB-INF/views/footer_html.jsp" %>



</body>
</html>