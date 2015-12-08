<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>供应商管理</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/customData<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/supplierData<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/yinshouyinfu<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" href="js/components/themes/bcgogo-ratting<%=ConfigController.getBuildVersion()%>.css">
    <link rel="stylesheet" href="js/components/themes/bcgogo-scorePanel<%=ConfigController.getBuildVersion()%>.css">

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript">
        <bcgogo:permissionParam permissions="WEB.SUPPLIER_MANAGER.SUPPLIER_DATA.PAY_EARNEST_MONEY,WEB.SUPPLIER_MANAGER.SUPPLIER_DATA.DUE_SETTLEMENT">
        APP_BCGOGO.Permission.SupplierManager.PayEarnestMoney = ${WEB_SUPPLIER_MANAGER_SUPPLIER_DATA_PAY_EARNEST_MONEY};
        APP_BCGOGO.Permission.SupplierManager.DueSettlement = ${WEB_SUPPLIER_MANAGER_SUPPLIER_DATA_DUE_SETTLEMENT};
        </bcgogo:permissionParam>
        <bcgogo:permissionParam resourceType="logic" permissions="WEB.VERSION.RELATION_SUPPLIER">
        APP_BCGOGO.Permission.Version.RelationSupplier=${WEB_VERSION_RELATION_SUPPLIER};
        </bcgogo:permissionParam>
    </script>
    <script type="text/javascript" src="js/invoicing<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/txnbase<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/customer/supplierData<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/jsScroller<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/jsScrollbar<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/detailsArrears<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/stockSearch<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/customerOrSupplier/mergeSupplier<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/customerOrSupplier/supplierCommentUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-scorePanel<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-ratting<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        $().ready(function () {
            //新增供应商
            $("#addSupplierBtn").click(function () {
                bcgogo.checksession({'parentWindow':window.parent, 'iframe_PopupBox':$("#iframe_PopupBox")[0], 'src':"RFSupplier.do?method=showSupplier"});
            });
        });
        userGuide.currentPageIncludeGuideStep = "CONTRACT_SUPPLIER_GUIDE_SUPPLIER_DATA,CONTRACT_SUPPLIER_GUIDE_RECOMMEND_SUPPLIER,CONTRACT_SUPPLIER_GUIDE_SINGLE_APPLY,";
        userGuide.currentPage = "supplierData";
        var fromUserGuideStep = '${param.fromUserGuideStep}';
    </script>

</head>
<body class="bodyMain">
<input type="hidden" id="pageType" value="supplierData" />
<%--存放选择客户的id--%>
<div id="selectedIdArray">
</div>
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
    <div class="i_search">
      <%--  <jsp:include page="supplierNavi.jsp" >
            <jsp:param name="currPage" value="searchSupplier" />
        </jsp:include>--%>
        <div class="i_main clear">
            <jsp:include page="supplierDataNavi.jsp">
                <jsp:param name="currPage" value="supplierData"/>
            </jsp:include>
            <div class="i_mainRight">
                <div class="cus_condition">
                    <div class="cus_left"></div>
                    <div class="cus_Bg">
                        <div class="cus_searchLeft"></div>
                        <div class="cus_searchBg"  style=" width:420px;">
                            <img src="images/cus_searchIcon.png"/>
                            <input type="text" class="txtSearch"  id="supplierInfoText" initialValue="供应商/联系人/手机" pagetype="supplierdata" value="供应商/联系人/手机" style="margin-right:5px;color: #666666;width: 200px;border:1px solid #7F9DB9; margin-top:3px; height:21px;"/>
                            <input type="text" class="txtSearch cus_searchBg" id="product_name2_id" searchfield="product_name" inputtype="supplierdata" initialvalue="品名" value="品名" style="margin-left:5px;color: #666666;width: 107px;border:1px solid #7F9DB9; margin-top:3px; height:21px;"/>
                            <input type="hidden" value="${supplierIds}" id="supplierIds">
                            <input type="button" value="搜索" id="supplierSearchBtn" class="cus_btnSearch" onfocus="this.blur();"/>
                            <div class="cus_searchRight"></div>
                        </div>
                        <div class="cus_right"></div>
                        <div class="cus_member" id="conditions" style="display: none">
                        </div>
                        <div class="cus_search">
                            <div class="cus_line" id="totalTradeAmount">
                                <label>按交易金额选择：</label>
                                <a class="hoverText_deepyellow">一千以下</a>
                                <a class="hoverText_deepyellow">一千~一万</a>
                                <a class="hoverText_deepyellow">一万以上</a>
                                <input type="text" class="txt textbox" id="totalTradeAmountStart" style="width:80px;"/>
                                ~
                                <input type="text" class="txt textbox" id="totalTradeAmountEnd" style="width:80px;"/>
                                <a class="cusSure">确认</a>
                            </div>
                            <div class="cus_line" id="lastInventoryTime">
                                <label>按入库时间选择：</label>
                                <a class="hoverText_deepyellow">昨天</a>
                                <a class="hoverText_deepyellow">今天</a>
                                <a class="hoverText_deepyellow">最近一周</a>
                                <a class="hoverText_deepyellow">最近一月</a>
                                <input id="lastInventoryTimeStart"  class="txt textbox" type="text" style="width:80px;" readonly="readonly"/>
                                ~
                                <input id="lastInventoryTimeEnd"  class="txt textbox" type="text" style="width:80px;" readonly="readonly"/>
                                <a class="cusSure">确认</a>
                            </div>
                            <div class="cus_line" id="debtAmount">
                                <label>按应付金额选择：</label>
                                <a class="hoverText_deepyellow">无应付</a>
                                <a class="hoverText_deepyellow">有应付</a>
                                <a class="hoverText_deepyellow">一千以下</a>
                                <a class="hoverText_deepyellow">一千~一万</a>
                                <a class="hoverText_deepyellow">一万以上</a>
                                <input type="text" class="txt textbox" id="debtAmountStart" style="width:80px;"/>
                                ~
                                <input type="text" class="txt textbox" id="debtAmountEnd" style="width:80px;"/>
                                <a class="cusSure">确认</a>
                            </div>
                        </div>
                        <div class="cus_bottomLeft"></div>
                        <div class="cus_bottomRight"></div>
                    </div>
                    <div class="cus_current">

                        <div class="cus_add add_user">
                            <bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.SUPPLIER_DATA.ADD">
                                <input id="addSupplierBtn" type="button" value="新增供应商" onfocus="this.blur();"/>
                            </bcgogo:hasPermission>
                            <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.RELATION_SUPPLIER">
                                <input class="addSupplierBtn hoverReminder" id="sentInvitationCodePromotionalSms" type="button" value="推荐使用软件" style="margin-right: 10px;">
                            </bcgogo:hasPermission>
                        </div>

                        本店供应商共有：<span id="totalNum" style="margin-right: 5px;">0</span>名<span style="color:#999999;margin: 0px; "><%--（按消费金额排序）--%></span>
                        共有应付款：¥<span id="totalDebt" style="margin-right: 5px;">0</span>，已付定金合计：¥<span id="totalDeposit" style="margin: 5px;">0</span>
                        <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.RELATION_SUPPLIER">
                            在线店铺:<span id="relatedNum" style="margin: 5px;" class="guanlian blue_col">0</span>家&nbsp;&nbsp;
                        </bcgogo:hasPermission>
                        <input id="relationType" value="" type="hidden"/>
                    </div>

                    <input type="hidden" name="rowStart" id="rowStart" value="0">
                    <input type="hidden" name="pageRows" id="pageRows" value="15">
                    <input type="hidden" name="totalRows" id="totalRows" value="0">
                    <input type="hidden" name="sortStatus" id="sortStatus">
                    <input type="hidden" name="sortStr" id="sortStr" value="">
                    <table class="cus_table clear table2" cellpadding="0" cellspacing="0" id="supplierDatas">
                    </table>
                    <div class="height"></div>
                    <div style="float:left;padding-top: 5px">
                        <jsp:include page="/common/pageAJAXForSolr.jsp">
                            <jsp:param name="dynamical" value="supplierSuggest"></jsp:param>
                            <jsp:param name="buttonId" value="searchSupplierDataAction"></jsp:param>
                        </jsp:include>
                    </div>
                    <div class="height"></div>
                    <bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.SUPPLIER_MERGE">
                        <div>
                            <input type="button" class="buttonBig add_user" id="mergeSupplierBtn" value="合并供应商" onfocus="this.blur();" />
                        </div>
                    </bcgogo:hasPermission>
                </div>


            </div>

        </div>
    </div>

    <div id="isInvo"></div>
    <div id="supplierinfotoreload"></div>
</div>
</div>
<div id="mask" style="display:block;position: absolute;">
</div>

<div class="alert" id="payableReceivableAlert" style="display: none;">
    点击后对账
</div>

<!-- 合并普通供应商弹出框-->
<div id="mergeSupplierDetail" style="display: none;">
    <jsp:include page="merge/mergeSupplierDetail.jsp"></jsp:include>
</div>

<!-- 合并批发商弹出框-->
<div id="mergeWholesalerDetail" style="display: none;">
    <jsp:include page="merge/mergeWholesalerDetail.jsp"></jsp:include>
</div>

<iframe id="iframe_PopupBox" style="position:absolute;z-index:6;top:210px;left:87px;display:none; "
        allowtransparency="true" width="850px" height="480px" frameborder="0" src=""></iframe>
<iframe id="iframe_PopupBox_1" style="position:absolute;z-index:5; left:200px; top:400px; display:none;"
        allowtransparency="true" width="1000px" height="1500px" frameborder="0" src="" scrolling="no"></iframe>
<div pop-window-name="input-mobile" style="display: none;">
    <div style="margin-left: 10px;margin-top: 10px">
        <label>手机号：</label>
        <input type="text" pop-window-input-name="mobile" maxlength="11" style="width:125px;height: 20px">
    </div>
</div>
<div id="cancelShopRelationDialog" style="display: none;">
    该用户是您的关联供应商，取消关联后您将无法在线处理订单，是否确认取消？若确认，请填写取消理由
    <textarea id="cancel_msg" init_word="取消关联理由" maxLength=70 style="width:270px;height: 63px;margin-top: 7px;"
              class="gray_color">取消关联理由</textarea>
</div>
<div class="tixing alert" id="multi_alert" style="left: 820px; margin-top: 0px; display: none;">
    <div class="ti_top"></div>
    <div class="ti_body alertBody">
        <div>您可推荐未使用一发软件的供应商使用:</div>
        <div>1、成功推荐1家，即可获得500元短信返利！</div>
        <div>2、一站式比价采购，节省成本！</div>
        <div>3、海量供应商供您选择！</div>
    </div>
    <div class="ti_bottom"></div>
</div>
<div class="tixing alert" id="single_alert" style="left: 820px; margin-top: 0px; display: none;">
    <div class="ti_top tiTop_top"></div>
    <div class="ti_body alertBody">
        <div>您可推荐未使用一发软件的供应商使用:</div>
        <div>1、成功推荐1家，即可获得500元短信返利！</div>
        <div>2、一站式比价采购，节省成本！</div>
        <div>3、海量供应商供您选择！</div>
    </div>
    <div class="ti_bottom"></div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>