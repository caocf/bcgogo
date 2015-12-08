<%--
  Created by IntelliJ IDEA.
  User: cfl
  Date: 13-1-15
  Time: 下午3:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>合格证</title>

    <%--<link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>--%>
    <link rel="stylesheet" type="text/css" href="styles/addPlan<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/statistics<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/qualifide<%=ConfigController.getBuildVersion()%>.css"/>
    <style type="text/css">
        body {
            color: #FFFFFF;
            /*font-family: "宋体", Arial;*/
            font-size: 12px;
        }
    </style>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/inventorySearchIndex<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/qualifiedCredentials<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/invoicesolr<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        $().ready(function(){
            $("#endDateStr,#startDateStr").datetimepicker({
                "numberOfMonths": 1,
                "showButtonPanel": true,
                "changeYear": true,
                "changeMonth": true,
                "yearRange": "c-100:c+100",
                "yearSuffix": "",
                "onClose": function (dateText, inst) {
                    if (!$(this).val()) {
                        return;
                    }
                    if ($("#endDateStr").val() && $("#startDateStr").val() > $("#endDateStr").val()) {
                        alert("预约出厂时间不能早于进厂时间，请修改!");
                        $("#endDateStr").val($("#startDateStr").val());
                        return;
                    }
                    if (G.getDate($("#startDateStr").val()).getTime() - G.getDate(G.getCurrentFormatDate()).getTime() > 0) {
                        alert("请选择今天之前的时间。");
                        $("#startDateStr").val(G.getCurrentFormatDate());
                        return;
                    }
                },
                "onSelect": function (dateText, inst) {
                    if (inst.lastVal == dateText) {
                        return;
                    }
                    $(this).val(dateText);
                    var This = inst.input;
                    if (inst.id == "startDateStr") {
                        //如果选在非当前的时间 提醒逻辑
                        if (!This.val()) return;
                        if ((G.getDate(G.getCurrentFormatDate()).getTime() - new Date(Date.parse(This.val().replace(/-/g, "/"))).getTime() > 0)) {
                            $("#dialog-confirm-invoicing").dialog('open');
                        }
                    }
                }
            });
        });
    </script>

</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main clear">
    <bcgogo:permission>
        <bcgogo:if resourceType="logic" permissions="WEB.VERSION.DISABLE_VEHICLE_CONSTRUCTION">
          <%--  <jsp:include page="txnNavi.jsp">
                <jsp:param name="currPage" value="repair"/>
            </jsp:include>--%>
            <jsp:include page="vehicleConstructionNavi.jsp">
                <jsp:param name="currPage" value=""/>
            </jsp:include>
        </bcgogo:if>
        <bcgogo:else>
            <jsp:include page="vehicleNavi.jsp">
                <jsp:param name="currPage" value=""/>
            </jsp:include>
        </bcgogo:else>
    </bcgogo:permission>

    <div class="titBody">
        <form:form action="txn.do?method=saveOrUpdateQualifiedCredentials" id="form" commandName="qualifiedCredentialsDTO"
                   method="post">
            <form:hidden path="orderId" value="${qualifiedCredentialsDTO.orderId}"/>
            <div style="text-align:center; line-height:30px;">
                <span style="color:#000000; text-align:center; font-size:24px; font-weight:bold;">维修出厂合格证</span>
                <span class="red_color" style="padding-left:20px; font-weight:bold; font-size:20px;">
                    NO</span><span style="color:red">*</span>:<input type="text"  id="no" name="no" value="${qualifiedCredentialsDTO.no}" maxlength="20" class="txt" style="width:120px;" hiddenValue="请输入合格证编号" />
            </div>
            <div class="height"></div>
            <table cellpadding="0" cellspacing="0" class="tabRepair">
                <col width="120">
                <col width="200">
                <tr>
                    <td>托修方<span style="color:red">*</span>：</td>
                    <td><input type="text" class="txt" id="customer" maxlength="50" name="customer" value="${qualifiedCredentialsDTO.customer}"/>
                        <input type="hidden"  id="customerId" name="customerId" value="${qualifiedCredentialsDTO.customerId}"/>
                    </td>
                </tr>
                <tr>
                    <td>车牌号码：</td>
                    <td><span style="float:left;padding-left: 12px">${qualifiedCredentialsDTO.licenseNo}</span><input type="hidden" name = "licenseNo" id="licenceNo" value="${qualifiedCredentialsDTO.licenseNo}"  /></td>
                </tr>
                <tr>
                    <td>车型：</td>
                    <td>
                        <input type="text" style="width:77px" maxlength="30" class="txt" name = "brand" id="brand" value="${qualifiedCredentialsDTO.brand}" style="width:83px;" hiddenValue="请输品牌"/>&nbsp;
                        <input type="text" class="txt" name = "model" maxlength="30" id="model" value="${qualifiedCredentialsDTO.model}" style="width:83px;" hiddenValue="请输车型"/>
                    </td>
                </tr>
                <tr>
                    <td>发动机型号/编号：</td>
                    <td><input type="text" class="txt" name = "engineNo" maxlength="20" id="engineNo" value="${qualifiedCredentialsDTO.engineNo}"/></td>
                </tr>
                <tr>
                    <td>底盘(车身)号：</td>
                    <td><input type="text" class="txt" name = "chassisNumber" maxlength="20" id="chassisNumber" value="${qualifiedCredentialsDTO.chassisNumber}"/></td>
                </tr>
                <tr>
                    <td>维修类别<span style="color:red">*</span>：<input type="hidden" id="defalutType" value="${qualifiedCredentialsDTO.repairType}"></td>
                    <td>
                        <form:select path="repairType" class="txt" style="width:188px;" >
                            <form:option value="----请选择----" style="color:#000"></form:option>
                            <form:options items="${repairOrderType}"/>
                        </form:select>
                    </td>

                    <%--<td><select class="txt"  style=" height:auto; width:94%;"><option>--请选择--</option></select></td>--%>
                </tr>
                <tr>
                    <td>维修合同编号<span style="color:red">*</span>：</td>
                    <td><input type="text" class="txt" name = "repairContractNo" maxlength="20" id="repairContractNo" value="${qualifiedCredentialsDTO.repairContractNo}"/></td>
                </tr>
                <tr>
                    <td>出厂里程表示值：</td>
                    <td><input type="text" class="txt" name = "producedMileage" maxlength="20" id="producedMileage" value="${qualifiedCredentialsDTO.producedMileage}"/></td>
                </tr>
                <tr>
                    <td colspan="2" style="height:50px;">该车按维修合同维修，经检验合格，准予出厂。</td>
                </tr>
                <tr>
                    <td>质量检验员<span style="color:red">*</span>：</td>
                    <td><input type="text" class="txt" name = "qualityInspectors" maxlength="30" id="qualityInspectors" value="${qualifiedCredentialsDTO.qualityInspectors}"/></td>
                </tr>
                <tr>
                    <td style="margin-bottom:30px;">承修单位<span style="color:red">*</span>：</td>
                    <td><input type="text" class="txt" name = "shopName" id="shopName" maxlength="30" value="${qualifiedCredentialsDTO.shopName}"/></td>
                </tr>
                <tr>
                    <td colspan="2" style="height:20px;"></td>
                </tr>
                <tr>
                    <td>进厂日期<span style="color:red">*</span>：</td>
                    <td><input type="text" readonly="true" initstartdatestrvalue="${qualifiedCredentialsDTO.startDateStr}" class="txt" name = "startDateStr" id="startDateStr" value="${qualifiedCredentialsDTO.startDateStr}"/></td>
                </tr>
                <tr>
                    <td>出厂日期<span style="color:red">*</span>：</td>
                    <td><input type="text" readonly="true" initstartdatestrvalue="${qualifiedCredentialsDTO.endDateStr}" class="txt" name = "endDateStr" id="endDateStr" value="${qualifiedCredentialsDTO.endDateStr}"/></td>
                </tr>
                <tr>
                    <td>托修方接车人：</td>
                    <td><span>(签字)</span></td>
                </tr>
                <tr>
                    <td>接车日期：</td>
                    <td><span>(签字)</span></td>
                </tr>
            </table>
            <div class="words">
                <h3>【质量保障卡】</h3>
                <span>
                    &nbsp;&nbsp;&nbsp;&nbsp;该车按维修合同进行修改，本厂对维修竣工的车辆实行质量保证，质量保证期为车辆行驶
                    <input type="text"  hiddenValue = "0.5" maxlength="10" name = "travelLength" id="travelLength" value="${qualifiedCredentialsDTO.travelLength}" style="width:50px; height:19px;" class="txt"/>
                    万公里或者
                    <input type="text" hiddenValue="30" name = "travelDate" maxlength="10" id="travelDate" value="${qualifiedCredentialsDTO.travelDate}" class="txt" style="width:50px; height:19px;"/>
                    日。在托修单位严格执行走合期规定、合理使用、正常维护的情况下，出现的维修质量问题，凭此卡随竣工出厂合格证，由本厂负责包修，免返修工料费和工时费，在原维修类别期限内修竣交托修方。
                </span>
                <div class="height"></div>
                <label>返修情况记录：</label>
                <table cellpadding="0" cellspacing="0" class="tabMerge">
                    <col width="80">
                    <col width="200">
                    <col width="100">
                    <col width="100">
                    <col width="60">
                    <col width="60">
                    <tr>
                        <td class="tab_title">次数</td>
                        <td class="tab_title">返修项目</td>
                        <td class="tab_title">返修日期</td>
                        <td class="tab_title">修竣日期</td>
                        <td class="tab_title">送修人</td>
                        <td class="tab_title">质检员</td>
                    </tr>
                    <tr>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                </table>
                <div class="height"></div>
                <div>维修发票号：<input type="text" class="txt" maxlength="15" name = "repairInvoiceNumber" id="repairInvoiceNumber" value="${qualifiedCredentialsDTO.repairInvoiceNumber}"/></div>
            </div>
            <div class="height"></div>
            <div class="btnSave">
                <input type="button" value="保&nbsp;存" id="save" class="jieCount"/>
                <input type="button" value="打&nbsp;印" btn= "print" id="print" class="jieCount"/>
            </div>
        </form:form>
    </div>
</div>
<div id="div_brand" class="i_scroll" style="display:none;">
    <div class="Container">
        <div id="Scroller-1">
            <div class="Scroller-Container" id="Scroller-Container_id">
            </div>
        </div>
    </div>
</div>
<iframe id="iframe_PopupBox"
        style="position:absolute;z-index:6;top:210px;left:87px;display:none;overflow-x:hidden;overflow-y:auto; "
        allowtransparency="true" width="1000px" height="100%" frameborder="0" src=""></iframe>
<iframe id="iframe_PopupBox_1" style="position:absolute;z-index:6;top:210px;left:87px;display:none; "
        allowtransparency="true" width="1000px" height="500px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_PopupBox_2"
        style="position:absolute;z-index:6;top:210px;left:87px;display:none;  background: none repeat scroll;"
        allowtransparency="true" width="1000px" height="650px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_PopupBoxMakeTime" style="position:absolute;z-index:10;display:none; " allowtransparency="true"
        width="350px" height="500px" frameborder="0" src="" scrolling="no"></iframe>
<input type="button" id="idAddRow" value="确定" style="display:none;"
       onclick="addOneRow()"/> <%--------------------欠款结算  2011-12-14-------------------------------%>
<iframe id="iframe_qiankuan" style="position:absolute; left:0px; top:300px; display:none;z-index:8;"
        allowtransparency="true" width="1000px" height="900px" frameborder="0" src="" scrolling="no">
</iframe>

<iframe id="iframe_PopupBox_account" style="position:absolute;z-index:5; left:500px; top:300px; display:none;"
        allowtransparency="true" width="900px" height="450px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_CardList" style="position:absolute;z-index:9; left:300px; top:200px; display:none;"
        allowtransparency="true" width="850px" height="300px" frameborder="0" src=""></iframe>
<iframe id="iframe_buyCard" style="position:absolute;z-index:7; left:300px; top:10px; display:none;"
        allowtransparency="true" width="800px" height="740px" scrolling="no" frameborder="0" src=""></iframe>

<iframe id="iframe_moreUserInfo"
        style="position:absolute;z-index:7; left:200px; top:200px; display:none;overflow:hidden;"
        allowtransparency="true" width="840px" height="600px" frameborder="0" scrolling="no" src=""></iframe>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>