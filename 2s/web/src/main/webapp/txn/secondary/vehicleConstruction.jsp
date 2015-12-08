<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title id="title">车辆施工</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="js/components/themes/bcgogo-droplist.css">
    <link rel="stylesheet" type="text/css" href="styles/secondary/vehicleConstruction<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/invoiceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/invoicesolr<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-droplist.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-droplist-lite.js"></script>
    <script type="text/javascript" src="js/secondary/vehicleConstruction<%=ConfigController.getBuildVersion()%>.js"></script>
</head>

<body>
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>

<div class="i_main">
    <div class="body">
        <div>
            <h1 class="inlineBlock">施工结算附表</h1>
                <div class="inlineBlock">
                    <span style="font-family: '宋体'; color: #666666;font-size: 14px; font-weight: bold;">单据号：</span>
                    <span style="color: #CB0000;font-weight: bold;">${repairOrderSecondaryDTO.receipt}</span>&#160;&#160;
                    <a style="color: #007CDA;font-weight: bold;" href="txn.do?method=getRepairOrder&repairOrderId=${repairOrderSecondaryDTO.repairOrderId}" target="_blank">查看原单</a>
                </div>
        </div>
        <form id="repairOrderSecondary" name="repairOrderSecondary" method="post">
            <input id="id" name="id" value="${repairOrderSecondaryDTO.id}" type="hidden" autocomplete="off">
            <input id="repairOrderId" name="repairOrderId" value="${repairOrderSecondaryDTO.repairOrderId}" type="hidden" autocomplete="off">
            <input id="customerId" name="customerId" value="${repairOrderSecondaryDTO.customerId}" type="hidden" autocomplete="off">
            <input id="vehicleId" name="vehicleId" value="${repairOrderSecondaryDTO.vehicleId}" type="hidden" autocomplete="off">
            <input id="receipt" name="receipt" value="${repairOrderSecondaryDTO.receipt}" type="hidden" autocomplete="off">
            <input id="serviceTotal" name="serviceTotal" value="${repairOrderSecondaryDTO.serviceTotal}" type="hidden" autocomplete="off">
            <input id="salesTotal" name="salesTotal" value="${repairOrderSecondaryDTO.salesTotal}" type="hidden" autocomplete="off">
            <input id="otherIncomeTotal" name="otherIncomeTotal" value="${repairOrderSecondaryDTO.otherIncomeTotal}" type="hidden" autocomplete="off">
            <input id="settledAmount" name="settledAmount" value="${repairOrderSecondaryDTO.settledAmount}" type="hidden" autocomplete="off">
            <input id="accountDebtAmount" name="accountDebtAmount" value="${repairOrderSecondaryDTO.accountDebtAmount}" type="hidden" autocomplete="off">
            <input id="accountDiscount" name="accountDiscount" value="${repairOrderSecondaryDTO.accountDiscount}" type="hidden" autocomplete="off">
            <input id="total" name="total" value="${repairOrderSecondaryDTO.total}" type="hidden" autocomplete="off">
            <input id="again" name="again" value="${repairOrderSecondaryDTO.again}" type="hidden" autocomplete="off">
            <input id="isPrint" name="isPrint" value="false" type="hidden" autocomplete="off">
            <div class="float_clear">

                <div class="content float_left" style="width:590px;">
                    <div class="title">客户信息<c:if test="${memberDTO != null}"><div class="inlineBlock member">VIP</div></c:if></div>
                    <div class="detail">
                        <div class="inlineBlock cell"><label for="vehicleLicense">车牌号&#160;<span class="red_color">*</span></label><input type="text" id="vehicleLicense" name="vehicleLicense" value="${repairOrderSecondaryDTO.vehicleLicense}" data-original-value="${vehicleDTO.licenceNo}" autocomplete="off" maxlength="15"></div>
                        <div class="inlineBlock cell" id="td_brand">
                            <label for="brand">车辆品牌</label><input type="text" id="brand" name="brand" value="${repairOrderSecondaryDTO.vehicleBrand}" data-original-value="${vehicleDTO.brand}" autocomplete="off" maxlength="50">
                            <input type="hidden" value="" id="input_brandname" autocomplete="off">
                        </div>
                        <div class="inlineBlock cell" id="td_model">
                            <label for="model">车&#160;&#160;&#160;&#160;型</label><input type="text" id="model" name="model" value="${repairOrderSecondaryDTO.vehicleModel}"  data-original-value="${vehicleDTO.model}" autocomplete="off" maxlength="50">
                            <input type="hidden" value="" id="input_modelname" autocomplete="off">
                        </div>
                        <div class="inlineBlock cell"><label for="vehicleContact">车&#160;&#160;&#160;&#160;主</label><input type="text" id="vehicleContact" name="vehicleContact" value="${repairOrderSecondaryDTO.vehicleContact}" data-original-value="${vehicleDTO.contact}" autocomplete="off" maxlength="50"></div>
                        <div class="inlineBlock cell"><label for="vehicleMobile">车主手机</label><input type="text" id="vehicleMobile" name="vehicleMobile" value="${repairOrderSecondaryDTO.vehicleMobile}" data-original-value="${vehicleDTO.mobile}" autocomplete="off" maxlength="11" data-node-type="number"></div>
                        <div class="inlineBlock cell"><label for="customerLandline">座&#160;&#160;&#160;&#160;机</label><input type="text" id="customerLandline" name="customerLandline" value="${repairOrderSecondaryDTO.customerLandline}" data-original-value="${customerDTO.landLine}" autocomplete="off" maxlength="13"></div>
                        <div class="inlineBlock cell"><label for="customerName">客户名&#160;<span class="red_color">*</span></label><input type="text" id="customerName" name="customerName" value="${repairOrderSecondaryDTO.customerName}" data-original-value="${customerDTO.name}" autocomplete="off" maxlength="50"></div>
                        <div class="inlineBlock cell"><label for="customerContact">联&#160;系&#160;人</label><input type="text" id="customerContact" name="customerContact" value="${repairOrderSecondaryDTO.customerContact}" data-original-value="${customerDTO.contact}" autocomplete="off" maxlength="50"></div>
                        <div class="inlineBlock cell"><label for="customerMobile">手&#160;&#160;&#160;&#160;机</label><input type="text" id="customerMobile" name="customerMobile" value="${repairOrderSecondaryDTO.customerMobile}" data-original-value="${customerDTO.mobile}" autocomplete="off" maxlength="11" data-node-type="number"></div>
                    </div>
                </div>

                <div class="content float_right" style="width:400px;">
                    <div class="title">车辆进厂交接</div>
                    <div class="detail">
                        <div class="inlineBlock cell"><label for="startDateStr">进厂时间</label><input type="text" id="startDateStr" name="startDateStr" readonly="readonly" value="${repairOrderSecondaryDTO.startDateStr}" autocomplete="off"></div>
                        <div class="inlineBlock cell"><label for="vehicleHandover">接&#160;车&#160;人</label><input type="text" id="vehicleHandover" name="vehicleHandover" value="${repairOrderSecondaryDTO.vehicleHandover}" readonly="readonly" autocomplete="off" maxlength="30"></div>
                        <div class="inlineBlock cell"><label for="startMileage">进厂里程</label><input type="text" id="startMileage" name="startMileage" value="${repairOrderSecondaryDTO.startMileage}" autocomplete="off" maxlength="30" data-node-type="decimal" style="width: 100px;">&#160;km</div>
                        <div class="inlineBlock cell"><label for="fuelNumber">油&#160;&#160;&#160;&#160;量</label>
                            <form:select path="repairOrderSecondaryDTO.fuelNumber" autocomplete="off" cssStyle="width: 120px;height: 20px;">
                                <form:option value="" label="--请选择--"/>
                                <form:options items="${fuelNumberList}"/>
                            </form:select>
                        </div>
                        <div class="inlineBlock cell"><label for="endDateStr">出厂时间</label><input type="text" id="endDateStr" name="endDateStr" readonly="readonly" value="${repairOrderSecondaryDTO.endDateStr}" autocomplete="off"></div>
                    </div>
                </div>
            </div>

            <div class="content" id="fault">
                <div class="title"> 故障说明<a href="javascript:void(0)">(点击展开)</a></div>
                <div class="hide" style="background: #ffffff;padding: 10px;text-align: center;">
                    <textarea name="description" id="description" autocomplete="off" style="width:600px; height: 161px;padding: 10px;">${repairOrderSecondaryDTO.description}</textarea>
                    <img src="images/discription.png" alt="图片">
                </div>
            </div>

            <div class="content">
                <div class="title">单据信息</div>
                <div class="detail">

                    <div class="second-content" id="constructionOrder">
                        <div class="second-title">施工单</div>
                        <table cellpadding="0" cellspacing="0" width="100%">
                            <colgroup>
                                <col width="15%">
                                <col width="10%">
                                <col width="10%">
                                <col width="10%">
                                <col width="10%">
                                <col width="10%">
                                <col>
                                <col width="8%">
                            </colgroup>
                            <tr class="tableTitle">
                                <td>施工内容</td>
                                <td>标准工时</td>
                                <td>工时单价</td>
                                <td>实际工时</td>
                                <td>金额</td>
                                <td>施工人</td>
                                <td>备注</td>
                                <td>操作</td>
                            </tr>
                            <c:forEach items="${repairOrderSecondaryDTO.serviceDTOs}" var="serviceDTO" varStatus="status">
                                <tr class="tableLine">
                                    <input name="serviceDTOs[${status.index}].id" type="hidden" value="${serviceDTO.id}" autocomplete="off">
                                    <td><input name="serviceDTOs[${status.index}].service" value="${serviceDTO.service}" type="text" autocomplete="off"></td>
                                    <td><input name="serviceDTOs[${status.index}].standardHours" value="${serviceDTO.standardHours}" type="text" autocomplete="off" data-node-type="decimal" data-filter-zero="true"></td>
                                    <td><input name="serviceDTOs[${status.index}].standardUnitPrice" value="${serviceDTO.standardUnitPrice}" type="text" autocomplete="off" data-node-type="decimal" data-filter-zero="true"></td>
                                    <td><input name="serviceDTOs[${status.index}].actualHours" value="${serviceDTO.actualHours}" type="text" autocomplete="off" data-node-type="decimal" data-filter-zero="true"></td>
                                    <td><input name="serviceDTOs[${status.index}].total" value="${serviceDTO.total}" type="text" autocomplete="off" data-node-type="decimal" data-filter-zero="true"></td>
                                    <td><input name="serviceDTOs[${status.index}].workers" value="${serviceDTO.workers}" type="text" autocomplete="off"></td>
                                    <td><input name="serviceDTOs[${status.index}].memo" value="${serviceDTO.memo}" type="text" autocomplete="off"></td>
                                    <td><a href="javascript:void(0)">删除</a><a href="javascript:void(0)" class="hide">添加</a></td>
                                </tr>
                            </c:forEach>
                        </table>
                        <div class="total bold">
                            施工合计：<span class="number" data-node-type="serviceTotal" data-filter-zero="true">${repairOrderSecondaryDTO.serviceTotal}</span>元
                        </div>
                    </div>

                    <div class="second-content" id="materialBill">
                        <div class="second-title">
                            <span>材料单</span>&#160;&#160;
                            <label for="productSaler">销售人</label><input id="productSaler" name="productSaler" value="${repairOrderSecondaryDTO.productSaler}" type="text" autocomplete="off" style="width:200px;">
                        </div>
                        <table cellpadding="0" cellspacing="0" width="100%">
                            <colgroup>
                                <col width="13%">
                                <col width="20%">
                                <col width="15%">
                                <col width="10%">
                                <col width="10%">
                                <col width="6%">
                                <col width="6%">
                                <col width="6%">
                                <col>
                                <col width="8%">
                            </colgroup>
                            <tr class="tableTitle">
                                <td>商品编号</td>
                                <td>品名</td>
                                <td>品牌/产地</td>
                                <td>规格</td>
                                <td>型号</td>
                                <td>单价</td>
                                <td>数量</td>
                                <td>单位</td>
                                <td>小计</td>
                                <td>操作</td>
                            </tr>
                            <c:forEach items="${repairOrderSecondaryDTO.itemDTOs}" var="itemDTO" varStatus="status">
                                <tr class="tableLine item">
                                    <input name="itemDTOs[${status.index}].id" type="hidden" value="${itemDTO.id}" autocomplete="off">
                                    <input name="itemDTOs[${status.index}].productId" id="itemDTOs${status.index}.productId" type="hidden" value="${itemDTO.productId}" autocomplete="off">
                                    <td><input name="itemDTOs[${status.index}].commodityCode" id="itemDTOs${status.index}.commodityCode" value="${itemDTO.commodityCode}" type="text" autocomplete="off"></td>
                                    <td><input name="itemDTOs[${status.index}].productName" id="itemDTOs${status.index}.productName" value="${itemDTO.productName}" type="text" autocomplete="off"></td>
                                    <td><input name="itemDTOs[${status.index}].brand" id="itemDTOs${status.index}.brand" value="${itemDTO.brand}" type="text" autocomplete="off"></td>
                                    <td><input name="itemDTOs[${status.index}].spec" id="itemDTOs${status.index}.spec" value="${itemDTO.spec}" type="text" autocomplete="off"></td>
                                    <td><input name="itemDTOs[${status.index}].model" id="itemDTOs${status.index}.model"  value="${itemDTO.model}" type="text" autocomplete="off"></td>
                                    <td><input name="itemDTOs[${status.index}].price" id="itemDTOs${status.index}.price" value="${itemDTO.price}" type="text" autocomplete="off" data-node-type="decimal" data-filter-zero="true" class="itemPrice"></td>
                                    <td><input name="itemDTOs[${status.index}].amount" id="itemDTOs${status.index}.amount" value="${itemDTO.amount}" type="text" autocomplete="off" class="itemAmount" data-node-type="decimal" data-filter-zero="true"></td>
                                    <td>
                                        <input name="itemDTOs[${status.index}].unit" id="itemDTOs${status.index}.unit" value="${itemDTO.unit}" type="text" autocomplete="off" class="itemUnit table_input">
                                        <input name="itemDTOs[${status.index}].storageUnit" id="itemDTOs${status.index}.storageUnit" value="${itemDTO.storageUnit}" type="hidden" autocomplete="off" class="itemStorageUnit table_input">
                                        <input name="itemDTOs[${status.index}].sellUnit" id="itemDTOs${status.index}.sellUnit" value="${itemDTO.sellUnit}" type="hidden" autocomplete="off" class="itemSellUnit table_input">
                                        <input name="itemDTOs[${status.index}].rate" id="itemDTOs${status.index}.rate" value="${itemDTO.rate}" type="hidden" autocomplete="off" class="itemRate table_input">
                                    </td>
                                    <td><span id="itemDTOs${status.index}.totalSpan" data-node-type="materialTotal" data-filter-zero="true">${itemDTO.total}</span><input name="itemDTOs[${status.index}].total" id="itemDTOs${status.index}.total" value="${itemDTO.total}" type="hidden" autocomplete="off"></td>
                                    <td><a href="javascript:void(0)">删除</a><a href="javascript:void(0)" class="hide">添加</a></td>
                                </tr>
                            </c:forEach>
                        </table>
                        <div class="total bold">
                            材料合计：<span class="number" data-node-type="salesTotal" data-filter-zero="true">${repairOrderSecondaryDTO.salesTotal}</span>元
                        </div>
                    </div>

                    <div class="second-content" id="otherExpenses">
                        <div class="second-title">其他费用信息</div>
                        <table cellpadding="0" cellspacing="0" width="100%">
                            <col width="20%">
                            <col width="30%">
                            <col>
                            <col width="10%">
                            <tr class="tableTitle">
                                <td>费用名称</td>
                                <td>金额</td>
                                <td>备注</td>
                                <td>操作</td>
                            </tr>
                            <c:forEach items="${repairOrderSecondaryDTO.otherIncomeItemDTOs}" var="itemDTO" varStatus="status">
                                <tr class="tableLine">
                                    <input name="otherIncomeItemDTOs[${status.index}].id" type="hidden" value="${itemDTO.id}" autocomplete="off">
                                    <td><input name="otherIncomeItemDTOs[${status.index}].name" value="${itemDTO.name}" type="text" autocomplete="off"></td>
                                    <td>
                                        <div class="inlineBlock <c:if test="${itemDTO.name == '材料管理费'}">hide</c:if>"><input name="otherIncomeItemDTOs[${status.index}].price" value="${itemDTO.price}" type="text" autocomplete="off" data-filter-zero="true"></div>
                                        <div class="inlineBlock <c:if test="${itemDTO.name != '材料管理费'}">hide</c:if>">
                                            <input name="otherIncomeItemDTOs[${status.index}].otherIncome" type="hidden" autocomplete="off" value="${itemDTO.otherIncome}" data-node-type="decimal" data-filter-zero="true">
                                            <div>
                                                <input type="radio" name="otherIncomeItemDTOs[${status.index}].otherIncomeCalculateWay" value="RATIO" <c:if test="${itemDTO.otherIncomeCalculateWay == 'RATIO'}">checked="checked"</c:if> autocomplete="off" class="inlineBlock float_left" style="width: 20px;margin-top: 2px;">
                                                <label class="inlineBlock">按材料费比率计算&#160;</label>
                                                <input name="otherIncomeItemDTOs[${status.index}].otherIncomeRate" type="text" <c:if test="${itemDTO.otherIncomeCalculateWay == 'RATIO'}">value="${itemDTO.otherIncome}"</c:if> placeholder="请输入比率" autocomplete="off" data-node-type="decimal" data-filter-zero="true" style="width: 60px;">&#160;%&#160;
                                                <span data-node-type="price"><c:if test="${itemDTO.otherIncomeCalculateWay == 'RATIO'}">${itemDTO.price}</c:if><c:if test="${itemDTO.otherIncomeCalculateWay == 'AMOUNT'}">0</c:if>元</span>
                                            </div>
                                            <div>
                                                <input type="radio" name="otherIncomeItemDTOs[${status.index}].otherIncomeCalculateWay" value="AMOUNT" <c:if test="${itemDTO.otherIncomeCalculateWay == 'AMOUNT'}">checked="checked"</c:if> autocomplete="off" class="inlineBlock float_left" style="width: 20px;margin-top: 2px;">
                                                <label class="inlineBlock">按固定金额计算&#160;&#160;&#160;</label>
                                                <input name="otherIncomeItemDTOs[${status.index}].otherIncomePrice" type="text" <c:if test="${itemDTO.otherIncomeCalculateWay == 'AMOUNT'}">value="${itemDTO.otherIncome}"</c:if> placeholder="请输入金额" autocomplete="off" data-node-type="decimal" data-filter-zero="true" style="width: 100px;">
                                            </div>
                                        </div>
                                    </td>
                                    <td><input name="otherIncomeItemDTOs[${status.index}].memo" value="${itemDTO.memo}" type="text" autocomplete="off"></td>
                                    <td><a href="javascript:void(0)">删除</a><a href="javascript:void(0)" class="hide">添加</a></td>
                                </tr>
                            </c:forEach>
                        </table>
                        <div class="total bold">
                            其他合计：<span class="number" data-node-type="otherIncomeTotal" data-filter-zero="true">${repairOrderSecondaryDTO.otherIncomeTotal}</span>元
                        </div>
                    </div>

                    <div class="float_clear" style="margin: 10px 0px 10px 0px;">
                        <div class="inlineBlock float_right bold">单据总额：<span class="number" data-node-type="total" data-filter-zero="true">${repairOrderSecondaryDTO.total}</span>元</div>
                    </div>

                    <div style="margin: 10px 0px 10px 0px;">
                        <label for="memo" class="bold">备注：</label><input name="memo" id="memo" type="text" value="${repairOrderSecondaryDTO.memo}" autocomplete="off" style="width: 930px;">
                    </div>

                </div>
            </div>

            <div class="float_clear">
                <div class="operate float_left" id="reset">
                    <img src="images/cancelInput.png" alt="重置">
                    <div>重置</div>
                </div>
                <div class="operate float_right" id="settlement">
                    <img src="images/settlement.jpg" alt="结算">
                    <div>结算</div>
                </div>
            </div>
        </form>
    </div>
</div>
<div>
    <iframe id="iframe_PopupBox_account" style="position:absolute;z-index:5; left:800px; top:500px; display:none;" allowtransparency="true" width="450" height="450px" frameborder="0" src="" scrolling="no"></iframe>
    <div id="id-searchcomplete" style="position:absolute;display: none;left: -1000px;"></div>
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
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>