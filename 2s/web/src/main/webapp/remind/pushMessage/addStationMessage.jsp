<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>消息中心——消息中心</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/newTodo<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/returnStorage<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/messageManage<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/message<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/cuSearch<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreUserinfo<%=ConfigController.getBuildVersion()%>.css"/>

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/page/remind/addMessage<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/xheditor-1.1.14/xheditor-1.1.14-zh-cn<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/remind/message/stationMessage<%=ConfigController.getBuildVersion()%>.js"></script>

    <style type="text/css">
        .tabTitle {
            background: auto;
            height: auto;
            margin: 0;
            width: auto;
        }
    </style>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
    <div class="i_search">
        <div class="i_searchTitle">消息中心</div>
    </div>
    <div class="i_mainRight" id="i_mainRight">
        <jsp:include page="pushMessageNavi.jsp">
            <jsp:param name="currPage" value="AddStationMessage"/>
        </jsp:include>
        <div class="right">
            <div class="request">
                <div class="top"></div>
                <form:form commandName="messageDTO" id="messageForm" action="message.do?method=saveMessage" method="post" name="messageForm">
                    <form:hidden path="id" autocomplete="off" />
                    <form:hidden path="messageReceivers" autocomplete="off" value="${messageDTO.messageReceivers==null?'':messageDTO.messageReceivers}"/>
                    <form:hidden path="productIds" autocomplete="off" value="${messageDTO.productIds==null?'':messageDTO.productIds}"/>
                    <form:hidden path="shopId" autocomplete="off" value="${messageDTO.shopId==null?'':messageDTO.shopId}"/>
                    <form:hidden path="type" autocomplete="off"/>
                    <div class="body line lineBody">
                        <div class="divLine">
                            <label class="title">有效日期：</label>
                            <div class="select">
                                <form:select autocomplete="off" path="validTimePeriod" cssStyle="width: 100%">
                                    <form:options items="${messageValidTimePeriods}" itemLabel="name"/>
                                </form:select>
                            </div>
                        </div>
                        <div class="clear height"></div>
                        <div class="divLine">
                            <label class="title">信息内容：<input type="button" id="selectProduct" class="btnSelect" value="选择商品" onfocus="this.blur();"/></label>
                            <form:textarea class="textarea" path="content" autocomplete="off" ></form:textarea>
                        </div>
                        <div style="float: right;margin:5px 60px 10px;">
                        (最多200字)
                        </div>
                        <div class="divLine">
                            <label class="title">添加客户：<input type="button" id="selectCustomer" class="btnSelect" value="选择客户" onfocus="this.blur();"/></label>
                            <div class="textarea">
                                <ul style="float:left;width: 710px" id="detailCustomers">
                                    <c:forEach var="messageReceiverDTO" items="${messageDTO.messageReceiverDTOList}">
                                        <li>
                                            <div class='divNumber'>${messageReceiverDTO.receiverName}:${messageReceiverDTO.receiveMobile==null?"无手机号":messageReceiverDTO.receiveMobile}<a data-customer-id="${messageReceiverDTO.receiverId}" class="close j_customer_delete"></a>
                                            </div>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </div>
                            <div class="clear height"></div>
                            <div class="btnSure">
                                <input type="button" value="确&nbsp;定" class="buttonSmall" id="confirmBtn" />
                                <label><form:checkbox autocomplete="off" path="smsFlag"/>发送短信</label>
                            </div>
                        </div>
                    </div>
                    <div class="bottom"></div>
                </form:form>
            </div>
        </div>
    </div>
</div>

<div id="mask" style="display:block;position: absolute;"></div>

<div class="i_supplierInfo more_supplier select" id="selectCustomerDialog" title="选择客户" style="display:none">
    <div class="i_upBody">
        <a class="person">所有客户(<span class="j_clear_span" id="allCustomerNum">0</span>)</a>
        <a class="person">手机客户(<span class="j_clear_span" id="hasMobileCustomerNum">0</span>)</a>
        <div class="height"></div>
        <div class="condition">
            <input type="text" id="customerInfoText" pagetype="relatedcustomerdata" initialValue="客户/联系人/手机" value="客户/联系人/手机" style="color:#ADADAD;width:280px;" class="textbox" tabindex="-1" />
            <input type="button" id="searchCustomerBtn" value="查&nbsp;询" onfocus="this.blur();" class="buttonSmall"  />
        </div>
        <div class="height"></div>
        <div class="tabTitle">
            <div class="titleLeft"></div>
            <div class="titleBody">
                <div style="width:120px;"><input type="checkbox" id="checkAllCustomerCheckBox" />NO</div>
                <div style="width:328px;">客户</div>
                <div style="width:180px;">联系人</div>
                <div style="width:120px;">联系方式</div>
            </div>
            <div class="titleRight"></div>
        </div>
        <table cellpadding="0" cellspacing="0" class="table2 tabProduct" id="customerTable">
            <col width="120">
            <col>
            <col width="180">
            <col width="180">
        </table>
        <!--分页-->
        <div class="clear height"></div>
        <jsp:include page="/common/pageAJAX.jsp">
            <jsp:param name="url" value="message.do?method=selectCustomer"></jsp:param>
            <jsp:param name="dynamical" value="dynamical1"></jsp:param>
            <jsp:param name="data" value="{startPageNo:1,maxRows:10}"></jsp:param>
            <jsp:param name="jsHandleJson" value="drawCustomerTable"></jsp:param>
            <jsp:param name="display" value="none"></jsp:param>
        </jsp:include>
        <!--分页结束-->
        <div class="clear height"></div>
        <div class="btnClick">
            <input type="button" id="selectCustomerConfirmBtn" value="确&nbsp;定" onfocus="this.blur();" class="buttonBig" />
            <input type="button" id="selectCustomerCloseBtn" value="关&nbsp;闭" onfocus="this.blur();" class="buttonBig" />
        </div>
    </div>
</div>

<div class="i_supplierInfo more_supplier select" id="selectProductDialog" title="选择商品" style="display:none">
    <div class="i_upBody">
        <div class="condition">
            <input id="product_name2_id" type="text" class="txt j_clear_input" autocomplete="off" style="width:90px;" value="品名" initialValue="品名" inputtype="inquiryCenter"/>
            <input id="product_brand_id" type="text" class="txt j_clear_input" autocomplete="off" style="width:90px;" value="品牌/产地" initialValue="品牌/产地" inputtype="inquiryCenter"/>
            <input id="product_spec_id" type="text" class="txt j_clear_input" autocomplete="off" style="width:90px;" value="规格" initialValue="规格" inputtype="inquiryCenter"/>
            <input id="product_model_id" type="text" class="txt j_clear_input" autocomplete="off" style="width:90px;" value="型号" initialValue="型号" inputtype="inquiryCenter"/>
            <input id="pv_brand_id" type="text" class="txt j_clear_input" autocomplete="off" style="width:90px;" value="车辆品牌" initialValue="车辆品牌" inputtype="inquiryCenter"/>
            <input id="pv_model_id" type="text" class="txt j_clear_input" autocomplete="off" style="width:90px;" value="车型" initialValue="车型" inputtype="inquiryCenter"/>
            <input id="product_commodity_code" class="txt j_clear_input" type="text" style="text-transform:uppercase;width:80px;" autocomplete="off" initialValue="商品编号" value="商品编号" inputtype="inquiryCenter"/>

            <input type="button" id="searchProductBtn" value="查&nbsp;询" onfocus="this.blur();" class="btnCheck"/>
        </div>
        <div class="height"></div>
        <div class="tabTitle">
            <div class="titleLeft"></div>
            <div class="titleBody">
                <div style="width:50px;"><input type="checkbox" id="checkAllProductCheckBox"/>NO</div>
                <div style="width:100px;">商品编号</div>
                <div style="width:110px;">品名</div>
                <div style="width:110px;">品牌/产地</div>
                <div style="width:110px;">规格</div>
                <div style="width:110px;">型号</div>
                <div style="width:100px;">车辆品牌</div>
                <div style="width:100px;">车型</div>
            </div>
            <div class="titleRight"></div>

        </div>
        <table cellpadding="0" cellspacing="0" class="table2 tabProduct" id="productTable">
            <col width="50"/>
            <col width="100"/>
            <col width="110"/>
            <col width="110"/>
            <col width="110"/>
            <col width="110"/>
            <col width="100"/>
            <col/>
        </table>
        <div class="clear height"></div>
        <!--分页-->
        <jsp:include page="/common/pageAJAX.jsp">
            <jsp:param name="url" value="message.do?method=selectProduct"></jsp:param>
            <jsp:param name="dynamical" value="dynamical2"></jsp:param>
            <jsp:param name="data" value="{startPageNo:1,maxRows:10}"></jsp:param>
            <jsp:param name="jsHandleJson" value="drawProductTable"></jsp:param>
            <jsp:param name="display" value="none"></jsp:param>
        </jsp:include>
        <!--分页结束-->
        <div class="clear height"></div>
        <div class="btnClick">
            <input type="button" class="buttonBig" id="selectProductConfirmBtn" value="确&nbsp;定" onfocus="this.blur();"/>
            <input type="button" class="buttonBig" id="selectProductCloseBtn" value="关&nbsp;闭" onfocus="this.blur();"/>
        </div>
    </div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
