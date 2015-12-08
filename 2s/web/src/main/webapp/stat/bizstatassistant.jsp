<%@ include file="/WEB-INF/views/includes.jsp" %>
<%--
  User: liuWei
  Date: 12-4-16
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>员工业绩统计</title>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/personnelRecruit<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css"
          href="styles/performanceStatistics<%=ConfigController.getBuildVersion()%>.css"/>

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/bizstatassistant<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/Highcharts-2.3.2/js/highcharts.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/Highcharts-2.3.2/js/modules/exporting.js"></script>
    <script type="text/javascript" src="js/txnbase<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        <bcgogo:permissionParam resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION,WEB.VERSION.MEMBER_STORED_VALUE">
        APP_BCGOGO.Permission.Version.VehicleConstruction=${WEB_VERSION_VEHICLE_CONSTRUCTION};
        APP_BCGOGO.Permission.Version.MemberStoredValue=${WEB_VERSION_MEMBER_STORED_VALUE};
        </bcgogo:permissionParam>
            $(document).ready(function() {
                var txtId;

                function doClickProxyByNode(id, thisInst) {
                    var divId,divLeft,aWidth;
                    switch (id) {
                        case "btnFromYear":
                            divId = "menuYear";
//                            divLeft = G.getX(thisInst) - 178 + "px";
                            divLeft = G.getX(thisInst) - 39 + "px";
                            aWidth = "44px";
                            txtId = "txtYear";
                            break;

                        case "btnFromMonth":
                            divId = "menuMonth";
//                            divLeft = G.getX(thisInst) - 168 + "px";
                            divLeft = G.getX(thisInst) - 29 + "px";
                            aWidth = "34px";
                            txtId = "txtFromMonth";
                            break;

                        case "btnToMonth":
                            divId = "menuMonth";
//                            divLeft = G.getX(thisInst) - 168 + "px";
                            divLeft = G.getX(thisInst) - 29 + "px";
                            aWidth = "34px";
                            txtId = "txtToMonth";
                            break;
                        default:
                            break;
                    }

                    //清除其他同时显示的下拉菜单
                    divId == "menuMonth" ? $("#menuYear").css("display", "none") : $("#menuMonth").css("display",
                                                                                                       "none");

                    //显示下拉菜单
                    $("#" + divId).css({
                                           "display":"block",
                                           "left":divLeft,
                                           "top":GLOBAL.Display.getY(thisInst) + thisInst.offsetHeight +1 + "px"
                                       });

                    //设定下拉菜单选项的宽度
                    $("a", "#" + divId).css({
                                                "width":aWidth
                                            });
                }

                $("a", "#menuYear,#menuMonth").click(function(event) {
                    $("#" + txtId).val(event.target.innerHTML).css("color", "#000000");
                    $("#" + event.target.parentNode.id).css("display", "none");

                    //若endMonth比startMonth要大,则改为同startMonth相同的值
                    if (Number($("#txtFromMonth").val()) > Number($("#txtToMonth").val())) {
                        $("#txtToMonth").val($("#txtFromMonth").val());
                    }
                });

                $("#btnFromYear,#btnFromMonth,#btnToMonth").bind("click", function() {
                    doClickProxyByNode(this.id, this);
                });
            });

            function getSalesManData() {
                window.location.href = "member.do?method=salesManData";
            }
    </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>

<input type="hidden" value="${queryYear}" name="queryYear" id="queryYear"/>
<input type="hidden" value="${startMonth}" name="startMonth" id="startMonth"/>
<input type="hidden" value="${endMonth}" name="endMonth" id="endMonth"/>
<input type="hidden" value="${isSearch}" name="isSearch" id="isSearch"/>

<div class="i_main clear">
    <div class="mainTitles clear">
        <jsp:include page="statNavi.jsp">
            <jsp:param name="currPage" value="agentAchievements"/>
        </jsp:include>
    </div>
    <%--<div class="personalTitle">员工业绩统计</div>--%>
    <%--<div class="btnEmployers"><input type="button" onclick="getSalesManData()" onfocus="this.blur();" value="员工管理"></div>--%>
    <div class="clear"></div>
    <div class="i_height"></div>
    <div class="performanceTitleList">
        <div id="thisMonth" class="listWords"><a href="#" onclick="thisMonth()">本月业绩</a></div>
        <div id="lastMonth" class="listWords"><a href="#" onclick="lastMonth()">上月业绩</a></div>
        <div class="listHistory">
            <span>历史业绩&nbsp;&nbsp;</span>

            <div class="searchList">
                <div class="listIcon">
                    <input id="btnFromYear" type="button" onfocus="this.blur();">
                </div>
                <input id="txtYear" class="listTxt" type="text" value="${queryYear}">
            </div>
            <div class="searchList month">
                <div class="listIcon">
                    <input id="btnFromMonth" type="button" onfocus="this.blur();">
                </div>
                <input id="txtFromMonth" class="listTxt" type="text" value="${startMonth}">
            </div>
            <span>至</span>

            <div class="searchList month">
                <div class="listIcon">
                    <input id="btnToMonth" type="button" onfocus="this.blur();">
                </div>
                <input id="txtToMonth" class="listTxt" type="text" value="${endMonth}">
            </div>
            <input id="searchButton" type="button" onfocus="this.blur();" onclick="historySearch();" value="搜索"
                   class="buttonSmall" style="margin-top:-3px;"/>
        </div>
    </div>
    <div class="height"></div>

    <div class="statisticsLeft">
        <div id="chart_div" style="width:550px; height:400px; float:left; clear:both;"></div>
        <div id="noData"
             style="color:#F00;width:450px; height:250px; float:left; clear:both; display:none; position:relative;  padding:150px 0px 0px 150px; ">
            您查询的月份没有数据
        </div>
        <div id="performanceName" class="performanceName"><span>本月员工业绩前十统计图</span></div>
    </div>

    <div class="statisticsRight">
        <div class="statisticsTitle">
            本月员工业绩统计
        </div>
        <div class="titleBg">
            <div class="titleTop"></div>
            <div class="titleBottom"></div>
        </div>
            <table cellpadding="0" cellspacing="0" class="statisticsTable" id="histy">
              <col width="40"/>
              <col width="50"/>
                <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
              <col width="100"/>
              <col width="100"/>
                </bcgogo:hasPermission>
              <col width="100"/>
                <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
              <col width="100"/>
                </bcgogo:hasPermission>
              <col width="120"/>
              <col width="55"/>
              <tr class="tdTitle">
                <td style="width:7%">NO</td>
                <td style="width:15%;text-align:right;">姓名</td>
                  <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
                <td style="width:14%;text-align:right;">洗车金额</td>
                <td style="width:14%;text-align:right;">服务金额</td>
                  </bcgogo:hasPermission>
                <td style="width:14%;text-align:right;">销售金额</td>
                  <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
                <td style="width:14%;text-align:right;">会员金额</td>
                  </bcgogo:hasPermission>
                <td style="width:14%;text-align:right;">合计金额</td>
                <td style="width:8%;">明细</td>
              </tr>
            </table>

        <div class="i_leftBtn" id="pageNo_id1">
            <div class="lastPage">上一页</div>
            <div class="onlin_his" id="thisPageNo1">1</div>
            <div class="nextPage">下一页</div>
        </div>
    </div>
</div>
<div id="menuYear" class="selectDegree" style="display:none;">
    <a>2010</a>
    <a>2011</a>
    <a>2012</a>
    <a>2013</a>
</div>
<div id="menuMonth" class="selectDegree" style="display:none;">
    <a>01</a>
    <a>02</a>
    <a>03</a>
    <a>04</a>
    <a>05</a>
    <a>06</a>
    <a>07</a>
    <a>08</a>
    <a>09</a>
    <a>10</a>
    <a>11</a>
    <a>12</a>
</div>
<%@ include file="/common/messagePrompt.jsp" %>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>