<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 13-12-24
  Time: 上午10:54
  To change this template use File | Settings | File Templates.
--%>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>通讯录</title>

<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
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
<script type="text/javascript"
        src="js/components/ui/bcgogo-customerSms-input<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript"
        src="js/components/ui/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/smsWrite<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/smsWriteUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript">
defaultStorage.setItem(storageKey.MenuUid, "WEB.CUSTOMER_MANAGER.SMS_MANAGER");
defaultStorage.setItem(storageKey.MenuCurrentItem,"<a href='sms.do?method=toAddressList' style='color: #007CDA;cursor:pointer;font-size:14px;height: 30px;line-height: 30px;'>通讯录</a>");

function initAddressList(result) {
    $("#addressListTable tr:gt(0)").remove();
    $(".c_select_all").attr("checked", false);
    if (G.isEmpty(result)) {
        return;
    }
    var contactDTOList = result.results;
    var trStr = "";
    if (G.isEmpty(contactDTOList)) {
        trStr += "<tr><td colspan='8' style='text-align: center;'>没有联系人数据。</td></tr>";
        $("#addressListTable").append(trStr);
        return;
    }
    for (var i = 0; i < contactDTOList.length; i++) {
        var contactDTO = contactDTOList[i];
        var specialIdStr = contactDTO.specialIdStr;
        var customerOrSupplierName = G.normalize(contactDTO.customerOrSupplierName);
        var nameShort=customerOrSupplierName.length>10?customerOrSupplierName.substr(0,6)+"...":customerOrSupplierName;
//        var nameShort=customerOrSupplierName;
        var name = G.isEmpty(contactDTO.name) ? "未命名" : contactDTO.name;
        var con_name_short=name.length>10?name.substr(0,6)+"...":name;
        var mobile = G.normalize(contactDTO.mobile);
        var contactGroupTypeList = $(G.normalize(contactDTO.contactGroupTypeList));
        trStr += '<tr>' +
                '<td><div class="news-01">' +
                '<input class="special_Id" type="hidden" value="' + specialIdStr + '"/>' +
                '<input class="cItemChk" type="checkbox" />' +
                '</div></td>' +
                '<td title="'+customerOrSupplierName+'">' + nameShort ;
        if(contactGroupTypeList.length==1&&contactGroupTypeList[0]=="OTHERS"){
            trStr+= '<a class="contact_edit_btn"></a>';
        }
        trStr += '</td>' +
                '<td class="contact_name" title="'+name+'">' + con_name_short + '</td>' +
                '<td class="contact_mobile">' + mobile + '</td>' +
                '<td>';
        for (var j = 0; j < contactGroupTypeList.length; j++) {
            var groupType = contactGroupTypeList[j];
            var groupTypeLabel = "";
            if (groupType == "CUSTOMER") {
                groupTypeLabel = "客户组";
            } else if (groupType == "SUPPLIER") {
                groupTypeLabel = "供应商组";
            } else if (groupType == "MEMBER") {
                groupTypeLabel = "会员组";
            } else if (groupType == "APP_CUSTOMER") {
                groupTypeLabel = "客户端组";
            } else if (groupType == "OTHERS") {
                groupTypeLabel = "未分组";
            }
            trStr += '<span style="margin-left:5px" contactGroupType="' + groupType + '" class="item_group item-group-span gray_wid67">' + groupTypeLabel + '</span>';
        }
        trStr += '</td></tr>';
    }
    $("#addressListTable").append(trStr);
//    $("#addressListTable tr:last td").css("border-bottom","none");
//    $("#addressListTable tr").each(function() {
//        if ($(this).find(".item_group").attr("contactGroupType") != "OTHERS") {
//            $(this).find(".cItemChk").attr("disabled", "disable");
//            $(this).find(".cItemChk").attr("title", "不能编辑");
//        }
//    });
}

function deleteContactByIdArray(idArray){
    APP_BCGOGO.Net.asyncAjax({
        url:"contact.do?method=deleteContact",
        type: "POST",
        cache: false,
        data:{
            specialIds:idArray.toString()
        },
        dataType: "json",
        success: function (result) {
            if (!result.success) {
                nsDialog.jAlert(result.msg);
                return;
            }
            window.location.reload();
        },
        error:function() {
            nsDialog.jAlert("网络异常。");
        }
    });
}


$(function() {
    function _queryContact(searchWord, contactGroupType) {
        var url = "contact.do?method=queryContact";
        var data = {
            startPageNo:1,
            maxRows:15,
            contactGroupType:contactGroupType,
            searchWord:searchWord
        };
        APP_BCGOGO.Net.asyncAjax({
            url:url,
            type: "POST",
            cache: false,
            data:data,
            dataType: "json",
            success: function (result) {
                initAddressList(result);
                initPage(result, "_initAddressList", url, null, "initAddressList", '', '', data, null);
                var contactGroup;
                switch(contactGroupType){
                    case "CUSTOMER":
                        contactGroup="有手机客户组";
                        break;
                    case "SUPPLIER":
                        contactGroup="有手机供应商组";
                        break;
                    case "MEMBER":
                        contactGroup="有手机会员组";
                        break;
                    case "APP_CUSTOMER":
                        contactGroup="手机客户端组";
                        break;
                    case "OTHERS":
                        contactGroup="未分组联系人";
                        break;
                    default:
                        contactGroup="全部联系人";
                }
                $("#contactGroupTitle").text(contactGroup);
                $("#contactNumSpan").text("(共"+result.pager.totalRows+"条)");

            },
            error:function() {
                nsDialog.jAlert("网络异常。");
            }
        });
    }

    $(".item_group").live("click", function() {
        _queryContact("", G.normalize($(this).attr("contactGroupType")));
    });

    $("#toSmsWriteBtn").click(function() {
        if($(".cItemChk:checked").length<=0){
            nsDialog.jAlert("请选择联系人。");
            return;
        }
        var idArray = new Array();
        $(".cItemChk:checked").each(function() {
            var contactId = $(this).closest("tr").find(".special_Id").val();
            if (!G.isEmpty(contactId)) {
                idArray.push(contactId);
            }
        });
        toSmsWrite("", idArray.toString());
    });

    $("#delContactBtn").click(function() {
        var idArray = new Array();
        var groupArray = [];
        var containSpecialGroup = false;
        $(".cItemChk:checked").each(function() {
            var special_Id = $(this).closest("tr").find(".special_Id").val();
            var groupType = $(this).closest("tr").find(".item_group").attr("contactgrouptype");
            if (!G.isEmpty(groupType)) {
                groupArray.push(groupType);
                if(groupType != "OTHERS"){
                    containSpecialGroup = true;
                }else if (!G.isEmpty(special_Id)) {
                    idArray.push(special_Id);
                }
            }
        });
        if (idArray.length <= 0 && containSpecialGroup) {
            nsDialog.jAlert("只能删除未分组联系人，请重新选择！");
            return;
        }else if(idArray.length<=0){
            nsDialog.jAlert("请选择要删除的联系人。");
            return;
        }else if(containSpecialGroup){
            nsDialog.jConfirm("友情提示：已分组联系人不能删除！如果继续删除，只能删除未分组联系人！是否继续删除操作？", "友情提示", function(flag){
                if(flag){
                    deleteContactByIdArray(idArray);
                }
            });
            return;
        }

        nsDialog.jConfirm("是否确认删除选择的联系人？", "提示", function(flag) {
            if (flag) {
                deleteContactByIdArray(idArray);
            }
        }, false);
    });

    $("#addContactBtn").click(function() {
        $("#addContactAlert").dialog({
            resizable: false,
            draggable:false,
            title: "<span style='font-size: 14px'>新建联系人</span>",
            height: 200,
            width: 360,
            modal: true,
            closeOnEscape: false,
             open:function(){
                $("#addContactAlert .contact_name").val("");
                $("#addContactAlert .contact_id").val("");
                $("#addContactAlert .contact_mobile").val("");
            },
            beforeClose:function() {
                $("#addContactAlert .contact_name").val("");
                $("#addContactAlert .contact_mobile").val("");
                $("#customerSupplierFlag").attr("checked", false)
            }
        });
    });

    $("#addContactAlert .ok_btn").click(function() {
        var name = $("#addContactAlert .contact_name").val();
        if (G.isEmpty(name)) {
            nsDialog.jAlert("联系人名不能为空。")
            return;
        }
        var mobile = $("#addContactAlert .contact_mobile").val();
        if(G.isEmpty(mobile)){
            nsDialog.jAlert("手机号不能为空，请重新输入。");
            return;
        }
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
        if ($("#customerSupplierFlag").attr("checked")) {
            data['customerSupplierFlag'] = $("#customerSupplierSelector").val();
        }
        var url="sms.do?method=saveSmsContact";
        if(!G.isEmpty(specialIdStr)){
            url="sms.do?method=updateSmsContact";
        }
        $("#addContactAlert").dialog("close");
        APP_BCGOGO.Net.asyncAjax({
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
                window.location.reload();
            },
            error:function() {
                nsDialog.jAlert("网络异常！");
            }
        });

    });

    $("#addContactAlert .cancel_btn").click(function() {
        $("#addContactAlert").dialog("close");
    });

    $("#contactSearchInput").live("keyup", function(event) {
        droplistLite.show({
            event: event,
            id: "id",
            keyword: "searchWord",
            data: "contact.do?method=queryContactSuggestion",
            name: "keyWord"
        });
    });

    $(".cItemChk").live("click", function() {
        $(".c_select_all").attr("checked", $(".cItemChk").length == $(".cItemChk:checked").length)
    });

    $(".c_select_all").click(function() {
        var checkFlag = $(this).attr("checked");
        $(".cItemChk").each(function() {
            if (!$(this).attr("disabled")) {
                $(this).attr("checked", checkFlag);
            }
        });
        $(".c_select_all").attr("checked", checkFlag);
    });

    $(".contact_edit_btn").live("click",function(){
        var $target=$(this);
        var $tr=$(this).closest("tr");
        var contactId=$tr.find(".special_Id").val();
        if(G.isEmpty(contactId)) return;
        var name=$tr.find(".contact_name").text();
        var mobile=$tr.find(".contact_mobile").text();
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

    $("#searchContactBtn").click(function() {
        _queryContact($("#contactSearchInput").val(), "");
    });

    _queryContact();

    $("#customerSupplierSelector").bind("change",function(e){
        $("#saveContactPromptSpan").text($(this).find("option:selected").text());
    });
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
            <jsp:param name="currPage" value="addressList"/>
        </jsp:include>

        <div class="messageRight">
            <div class="messageRight_radius" style="padding-bottom: 50px">
                <div class="content_01" style="width: 592px;min-height: 575px">
                    <div class="select_per">
                        <a id="addContactBtn" class="blueBtn_77">新建联系人</a>

                        <div class="addressList balance_left10">
                            <a id="toSmsWriteBtn" class="assis-btn">写短信</a>
                            <a id="delContactBtn" class="assis-btn">删 除</a>
                        </div>
                        <div class="txt_border" style="margin-right:8px;width: 195px">
                            <input id="contactSearchInput" type="text" style="width:170px;" autocomplete="off"
                                   placeholder="客户/供应商/联系人/手机号">

                            <div id="searchContactBtn" class="i_search"></div>
                        </div>
                        <%--<input id="contactSearchInput" name="" type="text" class="search-scope" placeholder="客户/供应商/联系人/手机号"  style="margin-left:35px; width:170px;"/><a class="blueBtn_52 balance_left8">查 找</a>--%>
                        <div class="clear"></div>
                    </div>

                    <div class="clear height"></div>
                    <div>
                        <strong style="font-size:14px;padding-left:10px"><span id="contactGroupTitle">全部联系人</span><span id="contactNumSpan">（共0条）</span></strong>
                    </div>
                    <div class="clear height"></div>
                    <table id="addressListTable" width="580" border="0" cellspacing="0" class="news-table"
                           style="float:left; margin:7px;">
                        <colgroup valign="top">
                            <col width="30"/>
                            <col width="135"/>
                            <col/>
                            <col width="120"/>
                            <col width="160"/>
                        </colgroup>
                        <tr class="news-thbody">
                            <td>
                                <div class="news-01">
                                    <input class="c_select_all" type="checkbox">
                                </div>
                            </td>
                            <td align="center"> 客户/供应商</td>
                            <td align="center">联系人</td>
                            <td align="center">手机号码</td>
                            <td align="center">所在分组</td>
                        </tr>
                    </table>
                    <div class="clear"></div>
                </div>
                <div class="content_02" style="height: 575px;width: 200px">
                    <div class="contact_body">
                        <div class="contact_title2">联系人</div>
                        <ul>
                            <li><a class="item_group blue_color">全部联系人（${allContactNum}）</a></li>
                            <%--<li><a class="item_group orange_color">全部联系人（100）</a></li>--%>
                            <li><a class="item_group blue_color"
                                   contactGroupType="OTHERS">未分组联系人（${otherContactNum}）</a></li>
                        </ul>
                        <div class="clear"></div>
                    </div>
                    <div class="line_separated"></div>
                    <div class="contact_body">
                        <div class="contact_title2">联系人组</div>
                        <ul>
                            <c:forEach items="${contactGroupDTOs}" var="contactGroupDTO" varStatus="status">
                                <c:if test="${contactGroupDTO.contactGroupType!='OTHERS'}">
                                    <li><a class="item_group blue_color"
                                           contactGroupType="${contactGroupDTO.contactGroupType}">${contactGroupDTO.name}（${contactGroupDTO.totalNum}）</a>
                                    </li>
                                </c:if>
                            </c:forEach>
                        </ul>
                        <div class="clear"></div>
                    </div>
                </div>


                <div class="clear"></div>
            </div>
            <div class="i_pageBtn" style="float:right;margin:-27px 0 0 10px">
                <bcgogo:ajaxPaging
                        url="contact.do?method=queryContact"
                        postFn="initAddressList"
                        display="none"
                        dynamical="_initAddressList"/>
            </div>
        </div>
    </div>
</div>
</div>

<div id="addContactAlert" style="display: none">
    <div class="s-alert" style="padding-left: 10px">
        <table width="100%" border="0" cellspacing="0" cellpadding="0" class="addMessage_table">
            <tr>
                <td width="21%">
                    <span class="divTit alert_divTit"><span class="red_color">*</span>联系人</span>
                    <input type="hidden" class="contact_id"/>
                </td>
                <td width="79%">
                    <span class="divTit alert_divTit">
                   <input type="text" class="contact_name txt" maxlength="20"/>
                    </span>
                </td>
            </tr>
            <tr>
                <td><span class="divTit alert_divTit"><span class="red_color">*</span>手机号</span></td>
                <td><span class="divTit alert_divTit">
            <input type="text" class="contact_mobile txt"/>
        </span></td>
            </tr>
            <tr>
                <td colspan="2">
                    <span style="float:left; margin-left:10px;">
            <input id="customerSupplierFlag" type="checkbox" style="float:left; margin-left:0"/>
        </span>加为 <select id="customerSupplierSelector" name="">
                    <option value="CUSTOMER">客户</option>
                    <option value="SUPPLIER">供应商</option>
                </select>
                    <span class="gray_color">（勾选后系统自动保存为<span id="saveContactPromptSpan">客户</span>信息）</span>
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

<div id="mask" style="display:block;position: absolute;"></div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>