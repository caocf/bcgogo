<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%--
  Created by IntelliJ IDEA.
  User: monrove
  Date: 11-12-14
  Time: 下午5:28
  To change this template use File | Settings | File Templates.
--%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>单位联系人-客户</title>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/uncleUser<%=ConfigController.getBuildVersion()%>.css"/>

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/uncleIndex<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        APP_BCGOGO.Page="uncleIndex";
        <bcgogo:permissionParam resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION,WEB.VERSION.MEMBER_STORED_VALUE">
        APP_BCGOGO.Permission.Version.VehicleConstruction=${WEB_VERSION_VEHICLE_CONSTRUCTION};
        </bcgogo:permissionParam>

        var customerId, supplierId;
        function enterPhoneSendSms(objEnterPhoneMobile) {
            smsHistory(objEnterPhoneMobile, customerId, supplierId);
        }
        function smsHistory(mobile, customerId, supplierId) {
            if (mobile == null || jQuery.trim(mobile) == "") {
                $("#enterPhoneCustomerId").val(customerId);
                $("#enterPhoneSupplierId").val(supplierId);
                Mask.Login();
                $("#enterPhoneSetLocation").fadeIn("slow");
                return;
            }
            window.location = "sms.do?method=smswrite&mobile=" + mobile+"&customerId=" + customerId+"&supplierId=" + supplierId;
        }

        function page(pageNo, sPageNo) {
            var searchKey = document.getElementById("searchKey").value;
            window.location = "unitlink.do?method=index&ucmValue=" + encodeURI(encodeURI(searchKey)) + "&pageNo=" + pageNo + "&spNo=" + sPageNo;
        }

        function prePage(pageNo, sPageNo, type) {
            if (type * 1 == 0) {     //客户
                if (pageNo * 1 == 1) {
                    alert("已经是第一页!");
                    return;
                }
                pageNo = pageNo * 1 - 1;
            } else if (type * 1 == 1) {    //供应商
                if (sPageNo * 1 == 1) {
                    alert("已经是第一页!");
                    return;
                }
                sPageNo = sPageNo * 1 - 1;
            }

            var searchKey = document.getElementById("searchKey").value;
            window.location = "unitlink.do?method=index&ucmValue=" + encodeURI(encodeURI(searchKey)) + "&pageNo=" + pageNo + "&spNo=" + sPageNo;
        }

        function nextPage(pageNo, pageCount, sPageNo, sPageCount, type) {
            if (type * 1 == 0) {     //客户
                if (pageNo * 1 == pageCount * 1) {
                    alert("已经是最后一页!");
                    return;
                }
                pageNo = pageNo * 1 + 1;
            } else if (type * 1 == 1) {    //供应商
                if (sPageNo * 1 == sPageCount * 1) {
                    alert("已经是最后一页!");
                    return;
                }
                sPageNo = sPageNo * 1 + 1;
            }
            var searchKey = document.getElementById("searchKey").value;
            window.location = "unitlink.do?method=index&ucmValue=" + encodeURI(encodeURI(searchKey)) + "&pageNo=" + pageNo + "&spNo=" + sPageNo;
        }
        /**
         * 权限控制页面供应商与客户显示数量
         */
        <bcgogo:permissionParam permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_DATA,WEB.SUPPLIER_MANAGER.SUPPLIER_DATA">
        $(document).ready(function() {
            if (${permissionParam1&&permissionParam2}) {
                $("#pageNumber").val(5);
            } else {
                $("#pageNumber").val(15);
            }
            $(".blue_col").live("mouseover",function(){
                $(this).css({"color":"#fd5300","textDecoration":"underline"});
            });
            $(".blue_col").live("mouseout",function(){
                $(this).css({"color":"#0094ff","textDecoration":"none"});
            });
            App.Menu.Function.doNavigate("客户/供应商");
        });
        </bcgogo:permissionParam>
    </script>
</head>
<body class="bodyMain">
<input type="hidden" id="ucmValue" value="${ucmValue}"/>
<input type="hidden" id="count1" value="${customerSize}"/>
<input type="hidden" id="count2" value="${supplierSize}"/>
<input type="hidden" id="basePath" value="<%=basePath%>"/>
<input type="hidden" id="pageNumber"/>
<%@include file="/WEB-INF/views/header_html.jsp" %>
<bcgogo:permissionParam permissions="WEB.CUSTOMER_MANAGER.SMS_SEND">
    <input type="hidden" id="smsSendPermission" value="${permissionParam1}"/>
</bcgogo:permissionParam>


<div class="title">
</div>
<div class="i_main clear">

    <div class="mainTitle">
        <%--<c:choose>--%>
            <%--<c:when test="<%=customerdata&&supplierData%>">--%>
                <%--客户/供应商--%>
            <%--</c:when>--%>
            <%--<c:when test="<%=supplierData%>">--%>
                <%--供应商--%>
            <%--</c:when>--%>
            <%--<c:otherwise>--%>
                <%--客户--%>
            <%--</c:otherwise>--%>
        <%--</c:choose>--%>
      <bcgogo:permission>
          <bcgogo:if permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_DATA&&WEB.SUPPLIER_MANAGER.SUPPLIER_DATA">
            客户/供应商
          </bcgogo:if>
          <bcgogo:if permissions="WEB.SUPPLIER_MANAGER.SUPPLIER_DATA">
            供应商
          </bcgogo:if>
          <bcgogo:else>
            客户
          </bcgogo:else>
      </bcgogo:permission>
    </div>

    <input type="hidden" id="searchKey" value='<%=(request.getAttribute("searchKey"))%>'/>

    <div class="i_mainRight" id="i_mainRight">

        <div id="div_all" class="clear">
            <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_DATA">
                <div class="more_his uncle_cont clear"><strong>客户</strong>共有<span
                        id="customerCount" class="hover">0</span>条记录
                </div>
                <table cellpadding="0" cellspacing="0" class="table2" id="histy">
                    <col width="25"/>
                    <col width="137"/>
                    <col width="55"/>
                    <col width="90"/>
                    <col width="140"/>
                    <col width="50"/>
                    <col width="51"/>
                    <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
                    <col width="51"/>
                    <col width="77"/>
                    </bcgogo:hasPermission>
                    <col width="97"/>
                    <tr class="title_his">
                        <td style="color:black;">NO</td>
                        <td style="color:black;">客户</td>
                        <td style="color:black;">联系人</td>
                        <td style="color:black;">手机</td>

                        <td style="color:black;">地址</td>
                        <td style="color:black;">累计消费</td>
                        <td style="color:black;">累计欠款</td>
                        <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
                        <td style="color:black;">车辆数量</td>
                        <td style="color:black;">上次消费车牌</td>
                        </bcgogo:hasPermission>
                        <td style="color:black;">上次消费时间</td>

                    </tr>

                </table>
                <div class="his_bottom">
                    <jsp:include page="/common/pageAJAX.jsp">
                        <jsp:param name="url" value="unitlink.do?method=customerResponse"></jsp:param>
                        <jsp:param name="data"
                                   value="{startPageNo:1,maxRows:5,countStr:$('#count1').val(),ucmValue:$('#ucmValue').val()}"></jsp:param>
                        <jsp:param name="jsHandleJson" value="initTr1"></jsp:param>
                        <jsp:param name="dynamical" value="dynamical1"></jsp:param>
                    </jsp:include>
                    <div class="clear"></div>
                </div>
            </bcgogo:hasPermission>

            <bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.SUPPLIER_DATA">
                <div class="more_his uncle_cont clear"><strong>供应商</strong>共有
                    <span id="supplierCount" class="hover">0</span>条记录
                </div>

                <table cellpadding="0" cellspacing="0" class="table2" id="history">
                    <col width="26"/>
                    <col width="135"/>
                    <col width="50"/>
                    <col width="90"/>
                    <col width="145"/>
                    <col width="70"/>
                    <col width="70"/>
                    <col width="120"/>

                    <col width="90"/>
                    <tr class="title_his">
                        <td style="border-left:none;">NO</td>
                        <td style="color:black;">供应商</td>
                        <td style="color:black;">联系人</td>
                        <td style="color:black;">手机</td>
                        <td style="color:black;">地址</td>

                        <td style="color:black;">累计入库金额</td>
                        <td style="color:black;">上次交易单据</td>
                        <td style="color:black;">上次交易产品</td>
                        <td style="border-right:none;color:black;">上次交易时间</td>
                    </tr>
                    </col>
                </table>
                <div class="clear"></div>
                <div class="his_bottom">
                    <jsp:include page="/common/pageAJAX.jsp">
                        <jsp:param name="url" value="unitlink.do?method=supplierResponse"></jsp:param>
                        <jsp:param name="data"
                                   value="{startPageNo:1,maxRows:5,countStr:$('#count2').val(),ucmValue:$('#ucmValue').val()}"></jsp:param>
                        <jsp:param name="jsHandleJson" value="initTr2"></jsp:param>
                        <jsp:param name="dynamical" value="dynamical2"></jsp:param>
                    </jsp:include>
                    <div class="clear"></div>
                </div>
            </bcgogo:hasPermission>
        </div>
    </div>
</div>

<div id="mask" style="display:block;position: absolute;">
</div>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:200px; top:400px; display:none;"
        allowtransparency="true" width="630px" height="450px" frameborder="0" src="" scrolling="no"></iframe>
<%@ include file="/sms/enterPhone.jsp" %>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>