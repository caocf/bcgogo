<%@ page import="com.bcgogo.config.ConfigController" %>
<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 13-12-18
  Time: 上午11:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
<link href="js/extension/jquery/plugin/sliptree-tokenfield/tokenfield-typeahead.css" type="text/css" rel="stylesheet">
<link href="js/extension/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="js/extension/jquery/plugin/sliptree-tokenfield/bootstrap-tokenfield.css"/>
<link rel="stylesheet" type="text/css" href="styles/message<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
<link href="js/extension/jquery/1.10.3/jquery-ui-1.10.3.custom.min.css" type="text/css" rel="stylesheet">
<style type="text/css">
    .smsContentFocus {
        border-color: #66afe9;
        outline: 0;
        -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075), 0 0 8px rgba(102, 175, 233, 0.6);
        box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075), 0 0 8px rgba(102, 175, 233, 0.6);
    }
</style>
<script type="text/javascript" src="js/extension/jquery/jquery-1.9.1.js"></script>
<script type="text/javascript" src="js/extension/jquery/1.10.3/jquery-ui.js"></script>
<script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/bcgogo-paging<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/extension/jquery/1.10.3/jquery-ui-timepicker-addon.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.min.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/sliptree-tokenfield/bootstrap-tokenfield.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/sliptree-tokenfield/typeahead.min.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/sliptree-tokenfield/scrollspy.js"></script>

<script type="text/javascript">

function initSmsWrite(smsDTO){
    if(!smsDTO) return;
    $("#smsContent").val(smsDTO.content);
    _calculateSmsLen();
    $("#smsDraftId").val(G.normalize(smsDTO.smsDraftIdStr));
    var contactGroupDTOs=smsDTO.contactGroupDTOs;
    if(contactGroupDTOs){
        for(var i=0;i<contactGroupDTOs.length;i++){
            var contactGroupDTO=contactGroupDTOs[i];
            var name=G.normalize(contactGroupDTO.name);
            var groupId=contactGroupDTO.idStr;
            var data={
                label: name,
                value:name,
                params:{
                    type:"group",
                    groupId:groupId
                },
                editable:false
            };
            $("#mobileContainer").tokenfield('createToken',data);
        }
    }
    var contactDTOs=smsDTO.contactDTOs;
    if(contactDTOs){
        for(var i=0;i<contactDTOs.length;i++){
            var contactDTO=contactDTOs[i];
            var name=G.normalize(contactDTO.name);
            name=G.isEmpty(name)?"未命名":name;
            var mobile=contactDTO.mobile;
            var contactId=contactDTO.idStr;
            var label=name+'<'+mobile+'>';
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
            $("#mobileContainer").tokenfield('createToken',data);
        }
    }
    if($.isFunction(window.parent.adjustHeight)){
        window.parent.adjustHeight();
    }

//    if(contactDTOs&&!contactGroupDTOs&&contactGroupDTOs.length==1&&contactGroupDTOs[0].contactGroupType=="APP_CUSTOMER"){
//
//    }

}


function validateSmsSend(){
    var smsContent= $("#smsContent").val();
    if (!smsContent) {
        nsDialog.jAlert("请输入内容！");
        return false;
    }
    if (smsContent.length > 500) {
        nsDialog.jAlert("短信内容最多为500个汉字，请修改后发送!");
        return false;
    }
    var smsType = $('#smsType').val();    //短信类型
    //余额判断
    if($("#smsChk")[0].checked){
        var shopMoney=0;
        //ajax请求 smsBalance
        APP_BCGOGO.Net.syncGet({
            url: "sms.do?method=checkSmsBalance",
            data: {
                time:new Date()
            },
            success:function(data){
                shopMoney=Number(data);
            }
        });
        if(shopMoney<=0){
            nsDialog.jAlert("您的短信余额不足，请充值后再发送!");
            return false;
        }
    }
    var validateSuccess = true;
    //validate Sensitive Word
    APP_BCGOGO.Net.syncAjax({
        url: "sensitiveWord.do?method=validateSensitiveWord",
        type: "POST",
        cache: false,
        data:{
            content: smsContent
        },
        dataType: "json",
        success: function (result) {
            if (!result.success) {
                nsDialog.jAlert('存在敏感词：【' + result.msg + '】请修改后重新发送');
                validateSuccess = false;
            }
        },
        error:function(){
            validateSuccess = false;
            nsDialog.jAlert("网络异常！");
        }
    });
    return validateSuccess;
}

function generateSmsDTO(){
    var smsContent= $("#smsContent").val();
    var data={};
    if(smsContent){
        data['content']=smsContent;
    }
    //组装联系人信息
    var token_datas=$("#mobileContainer").tokenfield('getTokens');
    var contacts=new Array();
    var contactGroups=new Array();
    for(var i=0;i<token_datas.length;i++){
        var token_data=token_datas[i];
        if(!G.isEmpty(token_data.params)){
            var params=token_data.params;
            var type=params.type;
            if(type=="contact"||type=="new_contact"){
                var contact={};
                contact['id']=params.contactId;
                contact['name']=params.name;
                contact['mobile']=params.mobile;
                contact['sGroupType']=params.sGroupType;
                contacts.push(contact);
            }else if(type=="group"){
                var contactGroup={};
                contactGroup['id']=params.groupId;
                contactGroup['name']=params.name;
                contactGroups.push(contactGroup);
            }
        }else{  //手输的
            var tValue=token_data.value;
            tValue= tValue.replace(/[<>]/g,"");
            var mobile=tValue.match(new RegExp("(?:1[3|5|8]\\d)-?\\d{5}(\\d{3}|\\*{3})$","g"));
            var name=tValue.replace(mobile,"");
            var contact={};
            contact['name']=name;
            contact['mobile']=mobile[0];
            contacts.push(contact);
        }
    }
    for(var i=0;i<contacts.length;i++){
        var contact=contacts[i];
        data['contactDTOs['+i+'].specialIdStr']=G.normalize(contact.id);
        data['contactDTOs['+i+'].name']=G.normalize(contact.name);
        data['contactDTOs['+i+'].mobile']=G.normalize(contact.mobile);
    }
    for(var i=0;i<contactGroups.length;i++){
        var contactGroup=contactGroups[i];
        data['contactGroupDTOs['+i+'].id']=G.normalize(contactGroup.id);
        data['contactGroupDTOs['+i+'].name']=G.normalize(contactGroup.name);
    }
    return data;
}

function doSmsSend(sendTimeStr){
    var $tokenList= $(".token");
    if($tokenList.length<=0){
        nsDialog.jAlert("请填写联系人后再发送。");
        return;
    }
    for(var i=0;i<$tokenList.length;i++){
        if($($tokenList[i]).hasClass("token-invalid")){
            nsDialog.jAlert("联系人填写有误，请修改后再发送。");
            return;
        }
    }
    if (!validateSmsSend()){
        return;
    }
    var data=generateSmsDTO();
    if(G.isEmpty(data)){
        return;
    }
    data['sendTimeStr']=sendTimeStr;
    if(!G.isEmpty($("#appChk")[0])){
        data['appFlag']=$("#appChk")[0].checked;
    }
    data['smsFlag']=$("#smsChk")[0].checked;
    data['smsDraftId']=$("#smsDraftId").val();
    data['templateFlag']=$("#templateFlag").val();
    data['remindEventId']=$(window.parent.document).find("#remindEventId").val();
    data['smsSendScene']=$(window.parent.document).find("#smsSendScene").val();
    APP_BCGOGO.Net.asyncAjax({
        url: "sms.do?method=sendSms",
        data:data,
        type: "POST",
        cache: false,
        dataType: "json",
        success: function (result) {
            if(!result.success){
                nsDialog.jAlert(result.msg);
                return;
            }
            if(result.data=="SEND_APP_SUCCESS"){
                nsDialog.jAlert("发送APP消息成功。");
                return;
            }
            if(!G.isEmpty(result.dataStr)){
                window.parent.toSmsSendFinish(result.dataStr);
            }
        },
        error:function(){
            alert("网络异常。");
        }
    });

}

function _calculateSmsLen(){
    var smsLimit=67;
    var len=$("#smsContent").val().length;
    $("#contentLength").text(len);
    var totalLen=len+$("#shopPrefix").text().length;
    var smsLen=parseInt(totalLen/smsLimit);
    if((totalLen/smsLimit-smsLen)>0){
        smsLen++;
    }
    if(len==0){
        smsLen=0;
    }
    $("#smsLength").text(smsLen);
}


var $currentToken="";

$(function(){
    $("#smsContent").focus(function(){
//            $(this).css("border","1px solid #CCCCCC");
    }).focusout(function(){

            });


    $("#smsContent").on("keyup blur",function(){
         _calculateSmsLen();
    });

    $("#sendTime").datetimepicker({
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

    $('#sendBtn').click(function(){
        var _$self=$(this);
        if(_$self.attr("lock")){
            return;
        }
        _$self.attr("lock","lock");
        doSmsSend("");
    });
    $("#sendTime").click(function(){
        $("#sendTime").datepicker("show");
    });

    $("#saveSmsDraftBtn").click(function(){
        var _$self=$(this);
        if(_$self.attr("lock")){
            return;
        }
        _$self.attr("lock","lock");
        var $tokenList= $(".token");
        var smsContent= $("#smsContent").val();
        if($tokenList.length == 0 && (smsContent==null || $.trim(smsContent).length==0)){
            nsDialog.jAlert("请至少填写收信人或短信内容中的一项信息！");
            _$self.removeAttr("lock");
            return;
        }
        for(var i=0;i<$tokenList.length;i++){
            if($($tokenList[i]).hasClass("token-invalid")){
                nsDialog.jAlert("联系人填写有误，请修改后再保存。");
                _$self.removeAttr("lock");
                return;
            }
        }

//        if (!smsContent) {
//            nsDialog.jAlert("请输入内容！");
//            _$self.removeAttr("lock");
//            return false;
//        }
        if (smsContent.length > 500) {
            nsDialog.jAlert("短信内容最多为500个汉字，请修改后发送!");
            _$self.removeAttr("lock");
            return false;
        }
        var data=generateSmsDTO();
//        if(G.isEmpty(data)){
//            return;
//        }
        data['smsDraftId']=$("#smsDraftId").val();
//          data['smsSendScene']=$(window.parent.document).find("#smsSendScene").val();
        APP_BCGOGO.Net.asyncAjax({
            url: "sms.do?method=saveSmsDraft",
            type: "POST",
            cache: false,
            data:data,
            dataType: "json",
            success: function(result){
                if(!result.success){
                    nsDialog.jAlert(result.msg);
                    return;
                }
                $("#smsDraftId").val(result.dataStr);
                 _$self.removeAttr("lock");
                showMessage.fadeMessage("45%", "34%", "slow", 3000, "短信草稿保存成功！");
            },
            error:function(){
                nsDialog.jAlert("网络异常。");
                _$self.removeAttr("lock");
            }
        });
    });

    $("#templateSelector").click(function(){
        window.parent.doTemplateSelect();

    });

    $("#timingSendBtn").click(function() {
        $("#timingSelectorAlert").dialog({
            resizable: false,
            draggable:false,
            title: "定时发送",
            height: 250,
            width: 350,
            modal: true,
            closeOnEscape: false,
            beforeClose:function(){
                $("#sendTime").val("");
                $("#s_sendTime").text("");
            },
            open:function(){
//                $("#sendTime").datepicker("hide");
            },
            buttons:{
                "确定":function(){
                    var sendTimeStr=$("#sendTime").val();
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
                    doSmsSend(sendTimeStr);
                },
                "取消":function(){
                    $(this).dialog("close");
                }
            }
        });
    });

    $(".send_type_chk").click(function(){
         if(G.isEmpty($("#appChk")[0])){
             return;
         }
        if($("#smsChk")[0].checked && !$("#appChk")[0].checked){
            $("#smsChk").attr("disabled","disabled");
        }else  if(!$("#smsChk")[0].checked && $("#appChk")[0].checked){
            $("#appChk").attr("disabled","disabled");
        }else if($("#smsChk")[0].checked&&$("#appChk")[0].checked){
            $("#appChk").removeAttr("disabled");
            $("#smsChk").removeAttr("disabled");
        }
    });

    $("#clearSmsBtn").click(function(){
          $('#smsContent').val('');
          $('#contentLength').text(0);
          $('#smsLength').text(0);
    });

//    $("#smsChk").click();

    $('#mobileContainer').tokenfield({
        allowDuplicates:true,
        autocomplete: {
            source: "contact.do?method=querySmsContactSuggestion&searchType=tokenSuggestion",
            minLength: 2,
            select: function( event, ui ) {
//                console.log("-----------click-----------");
            }
        },
        showAutocompleteOnFocus: false,
        createTokensOnBlur: true,
        delimiter: [',', ';','，','；'],
        checkIfExist:function(value, label){
            return true;
        },
        dealNotExist: function(value, label){
            window.parent.nsDialog.jAlert("add:" + value);
        },
        dealExist: function(value, label,ui){
            if(G.isEmpty(value)) return;
            $currentToken=ui
            value=value.replace(">","").split("<");
            var name=value[0];
            var mobile=value[1];
            if($.isFunction(window.parent.doAddContactAlert)){
                 window.parent.doAddContactAlert(name,mobile);
            }

        }
    })
            .on('afterCreateToken', function (e){
                var allGroupArray=["有手机客户组","有手机供应商组","有手机会员组","手机客户端组","未分组"];
                var tValue = e.token.value ;
                var params=e.token.params;
                if(params.sGroupType=="APP_CUSTOMER"){
                    return;
                }
                var $currentToken=$(e.relatedTarget);
                if($.inArray(tValue, allGroupArray)!=-1){
                     $currentToken.addClass('token-un-editable');
                    return;
                }
                var foo = APP_BCGOGO.Validator;
                //手机号
                var mobile_pattern="^1[3|5|8][0-9]\\d{8}$";
                //例如：ndong<13063897965>
                var token_reg1=new RegExp("<(1[3|5|8]\\d)-?\\d{5}(\\d{3}|\\*{3})>$","g");
//                 var token_reg1_pattern="<(1[3|5|8]\\d)-?\\d{5}(\\d{3}|\\*{3})>$";
                //以手机号开头，或以手机号结尾  例如： 13063897965或ndong13063897965或13063897965ndong
                var token_reg2=new RegExp("^(?:1[3|5|8]\\d)-?\\d{5}(\\d{3}|\\*{3})|(?:1[3|5|8]\\d)-?\\d{5}(\\d{3}|\\*{3})$","g");
                //中文数字或字母
                var stantard_word_pattern="[a-zA-Z\\d\\u4e00-\\u9fa5]+";

                var tValueArr=tValue.split(" ");

                if(foo._equalsTo(tValue, "\\d{12,}")||tValueArr.length>2){   //不能出现连续12个以上的数字||只允许输入一个空格
                    $currentToken.addClass('token-invalid');
                    $currentToken.attr("title","联系手机格式错误，双击后可修改");
                    return;
                }
                var tokenType;
                if($currentToken.attr("disabled")=="disabled"){
                    tokenType="sys";
                }
                if(tValueArr.length==1){
                    if(token_reg1.test(tValue)){
                        tValue=tValue.replace(/[<>]/g,"");
                    }
                    var mobileArr=tValue.match(token_reg2);
                    if(G.isEmpty(mobileArr)){
                        $currentToken.addClass('token-invalid');
                        $currentToken.attr("title","联系手机格式错误。");
                        return;
                    }
                    mobile=mobileArr[mobileArr.length-1];
                    var tLabel=tValue.replace(mobile,"");
                    var defaultContactDTO;
                    if((tLabel.length==0||tLabel=="未命名")&&tokenType!="sys"){
                        APP_BCGOGO.Net.syncAjax({
                            url: "sms.do?method=getDefaultContactDTO",
                            type: "POST",
                            cache: false,
                            data:{
                                mobile:mobile
                            },
                            dataType: "json",
                            success: function (contactDTO) {
                                defaultContactDTO=contactDTO ;
                            },
                            error:function(){
                                nsDialog.jAlert("网络异常！");
                            }
                        });
                    }
                    if(defaultContactDTO&&defaultContactDTO.idStr){
                        tLabel=G.normalize(defaultContactDTO.name);
                        $currentToken.find(".close").remove();
                        $currentToken.attr("disabled","disabled");
                        var paramStr='<input type="hidden" value="contact" name="type" class="token_param">';
                        paramStr+='<input type="hidden" value="CONTACT_'+defaultContactDTO.idStr+'" name="contactId" class="token_param">';
                        paramStr+='<input type="hidden" value="'+tLabel+'" name="name" class="token_param">';
                        paramStr+='<input type="hidden" value="'+mobile+'" name="mobile" class="token_param">';
                        $currentToken.append(paramStr);
                        tokenType="sys";
                    }
                    tLabel=tLabel.length==0?"未命名":tLabel;
//                    if (!mobile||!foo._equalsTo(tLabel,stantard_word_pattern)){
                    if (!mobile){
                        $currentToken.addClass('token-invalid');
                        $currentToken.attr("title","联系手机格式错误。");
                        return;
                    }else{
                        tLabel+="<"+mobile+">";
                        $currentToken.find(".token-label").text(tLabel);
                        $currentToken.attr("data-value",tLabel);
                    }
                }else if(tValueArr.length==2){
                    if(!foo._equalsTo(tValueArr[0],mobile_pattern)&&!foo._equalsTo(tValueArr[1],mobile_pattern)){
                        $currentToken.addClass('token-invalid');
                        $currentToken.attr("title","联系手机格式错误。");
                        return;
                    }else{
                        var tLabel=tValueArr[0];
                        var mobile=tValueArr[1];
                        if(!foo._equalsTo(mobile,mobile_pattern)){
                            var temp=mobile;
                            mobile=tLabel;
                            tLabel=temp;
                        }
                        tLabel=tLabel.length==0?"未命名":tLabel;
                        tLabel=tLabel+"<"+mobile+">";
                        $currentToken.find(".token-label").text(tLabel);
                        $currentToken.attr("data-value",tLabel);
                    }

                }
                //增加不可编辑样式
                if(tokenType=="sys"){
                     $currentToken.addClass('token-un-editable');
                }

            })
            .on('beforeEditToken', function (e) {
                $(e.relatedTarget).attr("title","");
            });
    var smsDTO=JSON.parse('${smsDTOJson}');
    initSmsWrite(smsDTO);
    $("#smsContent").focus(function(){
        $(this).closest(".mes_textarea").addClass("smsContentFocus");
    })
    .focusout(function(){
        $(this).closest(".mes_textarea").removeClass("smsContentFocus");
    });
});

function createToken(data){
    $("#mobileContainer").tokenfield('createToken',data);
}

function getTokens(){
    return $("#mobileContainer").tokenfield('getTokens');
}

function onTreeClick(event, treeId, treeNode, clickFlag) {
    var contactId=treeNode.specialIdStr;
    var data={
        label: treeNode.name,
        value:treeNode.name,
        params:{
            type:"contact",
            contactId:contactId,
            name:treeNode.contactName,
            mobile:treeNode.mobile,
            sGroupType:treeNode.sGroupType
        },
        editable:false
    };
    createToken(data);
    if(treeNode.appCustomerFlag){
        $("#appChk").removeAttr("disabled");
        $("#smsChk").removeAttr("disabled");
        $("#appChk").attr("checked",true);
    }
}

</script>
<%@ include file="/common/messagePrompt.jsp" %>
<input id="smsDraftId" type="hidden" value="${smsDTO.smsDraftId}"/>
<span class="g-label">收信人</span>
<div class="tokenfield-area balance_left8" style="">
    <input type="text" class="form-control" id="mobileContainer" style="width: 452px;"/>
</div>
<div class="clear height"></div>
<div class="g-label">
    <div>短信内容</div>
    <c:if test="${!templateFlag}">
        <a id="templateSelector" class=" blue_color">选择模板</a>
    </c:if>
</div>
<div class="mes_textarea balance_left8">
    <textarea id="smsContent" name="content" maxlength="500" <c:if test="${templateFlag}"> readonly="true"  </c:if>></textarea>
    <div id="shopPrefix" style="padding:5px 2px; text-align:right; color:#999;">【${shopName}】</div>
    <div class="bottom">已输入<strong id="contentLength" class="red_color">0</strong>个字/将分为<span id="smsLength" class="orange_color">0</span>条短信发出
        <a id="clearSmsBtn"  class="blue_color" style="float:right">清空内容</a>
    </div>
</div>
<div class="clear height"></div>
<div class="mes_send" style="padding-left: 48px">
    <input id="templateFlag" type="hidden" value="${templateFlag}"/>
    <a id="sendBtn" class="blueBtn_64 balance_left8">发 送</a>

    <c:if test="${!templateFlag}">
        <a id="timingSendBtn" class="blueBtn_64 balance_left8">定时发送</a>
        <a id="saveSmsDraftBtn" class="blueBtn_64 balance_left8">存草稿</a>
    </c:if>

    <div class="clear height"></div>
    <div class="balance_left8 checkbox_mes"><input id="smsChk" class="send_type_chk" type="checkbox" checked="true" disabled="disabled"/>发送手机短信</div>
    <c:if test="${!isWholesalerVersion}">
        <div class="balance_left8 checkbox_mes"><input id="appChk" class="send_type_chk" type="checkbox" />发送手机端信息<span class="gray_color">（仅对已装手机客户端的联系人有效）</span></div>
    </c:if>
</div>
<div class="clear height"></div>
<div class="clear"></div>

<div id="timingSelectorAlert"  style="display: none">
    <div class="clear i_height"></div>
    <div>请选择定时发送的时间</div>
    <div>
        <input type="hidden" id="hiddenTxt" autofocus="true"/>
        <input type="text" id="sendTime" autocomplete="off" readonly="true"/>
    </div>
    <div>本短信将于<span id="s_sendTime"></span>发送</div>
</div>



