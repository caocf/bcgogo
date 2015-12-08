<%--
    @author:zhangjuntao
    @notice:addClientInfo.jsp 同步
--%>
<%@ page import="com.bcgogo.config.ConfigController" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/prompt_box<%=ConfigController.getBuildVersion()%>.css"/>
</head>

<body>
<form name="thisform" id="thisform" action="txn.do?method=updateCustomer" method="post">
<input type="hidden" autocomplete="off"  name="customerId" id="dialogCustomerId"/>
<input type="hidden" autocomplete="off"  name="businessScope" id="businessScope"/>
<input type="hidden" autocomplete="off" id="simplifierFlag" value="false"/>
<div class="prompt_box" id="customerDetailPromptBox" style="width:800px; display:block;margin: 0 auto;">
    <div class="title" style="width: 782px;padding-left: 17px;" id="customerDetailPromptBoxTitle">
        <div class="turn_off" style="padding-right: 10px;" id="closeCustomerDetail"></div>
        新增客户
    </div>
    <div class="content" style="float: left; line-height: 25px">
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <colgroup>
                <col width="50" custoemr-detail/>
                <col width="80"/>
                <col width="50" custoemr-detail/>
                <col width="80" />
                <col width="50" custoemr-detail/>
                <col width="80" custoemr-detail/>
                <col width="80"/>
            </colgroup>
            <tr>
                <td align="right"><span class="red_color">*</span>客户名：</td>
                <td>
                    <input name="name" id="dialogDetailCustomerName" class="txt" maxlength="20" autocomplete="off"/>
                </td>
                <td align="right">客户手机：</td>
                <td>
                        <span >
                            <input name="mobile" id="dialogDetailCustomerMobile" class="txt" maxlength="11" autocomplete="off"/>
                        </span>
                </td>
                <td align="right" custoemr-detail>座机：</td>
                <td custoemr-detail>
                        <span >
                            <input name="phone" id="dialogDetailCustomerPhone" class="txt" maxlength="20" autocomplete="off"/>
                        </span>
                </td>
                <td custoemr-detail>&nbsp;</td>
                <td custoemr-simplifier style="display: none;"><a class="blue_color" id="moreCustomerInfo">更多信息</a></td>
            </tr>
            <tr custoemr-detail>
                <td align="right">联系人：</td>
                <td>
                        <span >
                            <input name="contact" id="dialogDetailCustomerContact" class="txt" maxlength="20" autocomplete="off"/>
                        </span>
                </td>
                <td align="right">地址：</td>
                <td colspan="4">
                    <select autocomplete="off"  id="select_province" name="province" class="txt" style="width:75px">
                        <option value="">省份</option>
                    </select>
                    <select autocomplete="off"  id="select_city" name="city" class="txt" style="width:75px">
                        <option value="">城市</option>
                    </select>
                    <select autocomplete="off"  id="select_township" name="region" class="txt" style="width:90px">
                        <option value="">区</option>
                    </select>
                    <input autocomplete="off"  type="text" id="input_address" class="txt" style="float:none; width:170px;" name="address" value="" placeholder="详细地址"/>
                </td>
            </tr>
            <tr custoemr-detail>
                <td align="right">传真：</td>
                <td>
                        <span >
                            <input name="fax" id="dialogDetailCustomerFax" class="txt" maxlength="20" autocomplete="off"/>
                        </span>
                </td>
                <td align="right">QQ：</td>
                <td>
                        <span >
                            <input name="qq" id="dialogDetailCustomerQQ" class="txt" maxlength="20" autocomplete="off"/>
                        </span>
                </td>
                <td align="right"><span class="test1">Email：</span></td>
                <td>
                        <span >
                            <input name="email" id="dialogDetailCustomerEmail" class="txt" maxlength="20" autocomplete="off"/>
                        </span>
                </td>
                <td>&nbsp;</td>
            </tr>
            <tr custoemr-detail>
                <td align="right">简称：</td>
                <td>
                        <span >
                            <input name="shortName" id="dialogDetailCustomerShortName" class="txt" maxlength="20" autocomplete="off"/>
                        </span>
                </td>
                <td align="right">客户类别：</td>
                <td>
                    <select autocomplete="off"  name="customerKind" id="dialogDetailCustomerKind" style="width:130px;"
                            class="txt select">
                        <c:forEach items="${customerTypeMap}" var="type" varStatus="status">
                            <option value="${type.key}">${type.value}</option>
                        </c:forEach>
                    </select>
                </td>
                <td align="right">&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </tr>
        </table>
        <br/>

        <div class="cuSearch">
            <div class="gray-radius" style="margin:0;">
                <table class="tab_cuSearch" id="customerVehicleTable" cellpadding="0" cellspacing="0" style="width:750px;">
                    <colgroup>
                        <col width="100" custoemr-detail/>
                        <col width="70" custoemr-detail/>
                        <col width="140" />
                        <col width="100"/>
                        <col width="100"/>
                        <col width="70" custoemr-detail/>
                        <col width="70" custoemr-detail/>
                        <col width="70" custoemr-detail/>
                        <col width="100" custoemr-detail/>
                        <col width="120" custoemr-detail/>
                        <col width="160" custoemr-detail/>
                        <col width="100" custoemr-detail/>
                    </colgroup>
                    <tr class="titleBg">
                        <td style="padding-left:10px;">车牌号</td>
                        <td custoemr-detail>车主</td>
                        <td custoemr-detail>车主手机</td>
                        <td>车辆品牌</td>
                        <td>车型</td>
                        <td custoemr-detail>年代</td>
                        <td custoemr-detail>排量</td>
                        <td custoemr-detail>颜色</td>
                        <td custoemr-detail>购车日期</td>
                        <td custoemr-detail>发动机号</td>
                        <td custoemr-detail>车架号</td>
                        <td>操作</td>
                    </tr>
                    <tr class="space">
                        <td colspan="12"></td>
                    </tr>
                    <tr class="titBottom_Bg">
                        <td colspan="12"></td>
                    </tr>
                </table>
                <div class="clear"></div>
            </div>
            <div class="clear i_height"></div>
        </div>
        <div class="clear"></div>
        <div class="wid275">
            <div class="addressList"><a id="confirmBtn">确 定</a> <a id="cancelCustomerDetailBtn">取 消</a></div>
        </div>
        <div class="clear"></div>
    </div>
</div>
</form>
<script type="text/javascript" src="js/module/invoiceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/clientVehicle<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/customerOrSupplier/addCustomerDialog<%=ConfigController.getBuildVersion()%>.js"></script>
</body>
</html>
