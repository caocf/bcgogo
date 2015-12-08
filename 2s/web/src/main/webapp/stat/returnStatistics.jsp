<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  Created by IntelliJ IDEA.
  User: lw
  Date: 12-10-30
  Time: 下午8:08
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<title>退货</title>

	<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
	<link rel="stylesheet" type="text/css" href="styles/personnelRecruit<%=ConfigController.getBuildVersion()%>.css"/>
	<link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
	<link rel="stylesheet" type="text/css"
		  href="styles/performanceStatistics<%=ConfigController.getBuildVersion()%>.css"/>
	<link rel="stylesheet" type="text/css"
		  href="js/extension/jquery/plugin/jQuery-jRating/jRating.jquery.css"/>

	<style>
		@media print {
			#printBtn, .statisticsRight, .m_topMain, .s_topMain, .title, .mainTitles, .title_statistics a.normalOne, .title_statistics a.normalTwo, .title_statistics a.normalThree, .title_statistics a.hoverThree, .title_statistics a.hoverTwo {
				display: none;
			}

			.i_main {
				margin: 0;
				width: 650px !important;
			}

			.statisticsMain {
				width: 650px;
			}

			.titleBody {
				width: 632px !important;
			}

			.bodyMain {
				width: 650px !important;
			}

			#noTitle {
				width: 30px !important;
			}

			#codeTitle {
				width: 80px !important;
			}

			#nameTitle {
				width: 80px !important;
			}

			#brandTitle {
				width: 80px !important;
			}

			#modelTile {
				width: 80px !important;
			}

			#vehicleTitle {
				width: 80px !important;
			}

			#averagePrice {
				width: 80px !important;
			}

			#returnAmount {
				width: 50px !important;
			}

			#returnTotal {
				width: 70px !important;
			}

			#supplierNoTitle {
				width: 30px !important;
			}

			#supplierCodeTitle {
				width: 150px !important;
			}

			#supplierNameTitle {
				width: 80px !important;
			}

			#supplierBrandTitle {
				width: 80px !important;
			}

			#supplierModelTitle {
				width: 80px !important;
			}

			#supplierVehicleTitle {
				width: 80px !important;
			}

			#supplierAveragePrice {
				width: 80px !important;
			}

			#supplierReturnAmount {
				width: 50px !important;
			}
		}
	</style>

	<%@include file="/WEB-INF/views/header_script.jsp" %>

	<script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/extension/jquery/plugin/jQuery-jRating/jRating.jquery<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/head<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/extension/jquery/plugin/Highcharts-2.3.2/js/highcharts.js"></script>
	<script type="text/javascript" src="js/extension/jquery/plugin/Highcharts-2.3.2/js/modules/exporting.js"></script>
	<script type="text/javascript" src="js/module/bcgogoPieChart<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/returnStatistics<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/page/stat/statSelect<%=ConfigController.getBuildVersion()%>.js"></script>

	<script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "COST_STAT_GET_RETURN_STAT");

		var year = ${salesStatCondition.year};
		$("#yearHidden").val(year);

		var type = $("#queryStr").text();

		var total = ${result};
		var amount = ${totalAmount};
		var dataAry = [];


		$(function () {

			<c:forEach var="item" items="${itemDTOs}" >
			dataAry.push({
				name: '${item.queryResultStr}',
				y: ${item.queryResult}/total
			});
			</c:forEach>
			<c:forEach var="other" items="${other}" >
			dataAry.push({
				name: '${other.queryResultStr}',
				y: ${other.queryResult}/total
			});
			</c:forEach>

			if (dataAry != null && dataAry.length > 0) {
				var myChart = new pieChart({
					chartBasicConfig: {
						//用于显示图表的div
						container: 'chart_div',
						//标题文本
						titleText: '',
						//数据数组
						data: dataAry
					},

					//设置图表样式
					chartStyleConfig: {
						chartWidth: 680,
						chartHeight: 400,
						legendPosition: {x: 0, y: 50},
						legendWidth: 310,
						//条目间距
						itemMargin: {
							top: 0,
							bottom: 3
						}
					},
					seriseConfig: {
					}
				});
				myChart.setSize(640, 400);
			} else {
				$("#chart_div").css('display', 'none');
				$("#noData").css('display', 'block');
			}

			$(".basic").jRating({
				//rate,idbox
				step: true,
				showRateInfo: true,//是否显示提示信息
				length: 4,      //显示的星星个数
				rateMax: 4,   //满分分值
				requestPath: 'supplier.do?method=updateSupplierScore'
			});

		})

	</script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main clear">
    <div class="mainTitles clear">
	<jsp:include page="statNavi.jsp">
		<jsp:param name="currPage" value="costStat"/>
	</jsp:include>

	<jsp:include page="costStatNavi.jsp">
		<jsp:param name="currPage" value="returnStat"/>
	</jsp:include>
    </div>

	<div class="clear"></div>
	<div class="statisticsMain">
		<div class="height"></div>
		<div class="statistics_Title"><span>${salesStatCondition.year} 年<c:choose> <c:when
			test="${salesStatCondition.allYear}">全年</c:when><c:otherwise>${salesStatCondition.monthStr}</c:otherwise></c:choose></span>${salesStatCondition.returnQueryStr}入库退货TOP10
		</div>
		<div class="height"></div>
		<div id="chart_div" style="width:550px; height:400px; float:left; clear:both;"></div>
		<div id="noData"
			 style="color:#F00;width:200px; height:200px; float:left; clear:both; padding:0px 0px 0px 150px; display:none;">
			您查询的日期沒有数据
		</div>
		<div class="statisticsRight">
			<form:form action="costStat.do?method=getReturnStat" modelAttribute="salesStatCondition"
					   id="salesStatConditionForm">
				<div class="statisticsTab">
					<div class="tabAll">
						统计年份:
						<div class="select" id="year"><span>${salesStatCondition.year}年</span><a class="selImg"></a>
						</div>
						<div id="div_years" class="div_year" style="display:none;">
							<a>2015年</a>
							<a>2014年</a>
							<a>2013年</a>
							<a>2012年</a>
						</div>
						<form:hidden id="yearHidden" path="year"/>
					</div>
					<div class="tabAll">
						统计月份:
						<div class="select" id="month"><span>${salesStatCondition.monthStr}</span><a class="selImg"></a>
						</div>
						<div id="div_months" class="div_year" style="display:none;">
							<a>1月</a>
							<a>2月</a>
							<a>3月</a>
							<a>4月</a>
							<a>5月</a>
							<a>6月</a>
							<a>7月</a>
							<a>8月</a>
							<a>9月</a>
							<a>10月</a>
							<a>11月</a>
							<a>12月</a>
							<a>所有月份</a>
						</div>
						<form:hidden id="monthHidden" path="monthStr" value="${salesStatCondition.monthStr}"/>
					</div>


					<div class="tabAll">
						统计方式:
						<div class="select" id="way"><span
							id="queryStr">${salesStatCondition.productOrSupplier}</span><a
							class="selImg"></a></div>
						<div id="div_way" class="div_year" style="display:none;">
							<a>按商品统计</a>
							<a>按供应商统计</a>
						</div>
						<form:hidden id="queryWay" path="productOrSupplier"/>
					</div>
					<div class="tabAll" style="padding-bottom:20px;padding-left:70px;">
						<a class="buttonBig" id="costStatSubmit">统&nbsp;计</a>
					</div>
				</div>
			</form:form>

		</div>
		<div class="goodSalesStat">入库退货总计：<span>${total}</span>元</div>
		<div class="statisticsWord"><span>${salesStatCondition.year} 年<c:choose> <c:when
			test="${salesStatCondition.allYear}">全年</c:when><c:otherwise>${salesStatCondition.monthStr}</c:otherwise></c:choose></span>${salesStatCondition.returnQueryStr}入库退货TOP10
		</div>
		<div class="clear"></div>

		<c:choose>
			<c:when test="${salesStatCondition.queryProduct}">
				<div id="productInfo">
					<table cellpadding="0" cellspacing="0" class="tab_Statistics" id="tabList">
						<col width="50">
						<col width="100">
						<col width="150">
						<col width="120">
						<col width="150">
						<col width="170">
						<col width="100">
						<col width="88">
						<tr class="table_title">
							<th class="txt_left first-padding">NO</th>
							<th class="txt_left">商品编号</th>
							<th class="txt_left">品 名</th>
							<th class="txt_left">品牌/产地</th>
							<th class="txt_left">规格/型号</th>
							<th class="txt_left">车型/车辆品牌</th>
							<th>入库退货量</th>
							<th class="last-padding">退货金额</th>
						</tr>
						<c:forEach items="${itemDTOs}" var="item" varStatus="status">
							<tr class="table-row-original">
								<td title="${status.count}" class="txt_left first-padding">${status.count}</td>
								<td title="${item.commodityCode}" class="txt_left">${item.commodityCode}</td>
								<td title="${item.name}" class="txt_left">${item.name}</td>
								<td title="${item.brand}" class="txt_left">${item.brand}</td>
								<td title="${item.spec}" class="txt_left">${item.spec}/${item.model}</td>
								<td title="${item.productVehicleModel}"
									class="txt_left">${item.productVehicleModel}/${item.productVehicleBrand}</td>
								<td title="${item.salesAmount}" align="right">${item.salesAmount}</td>
								<td title="${item.salesTotal}" class="last-padding" align="right">${item.salesTotal}</td>
							</tr>
						</c:forEach>
					</table>
				</div>
			</c:when>
			<c:otherwise>
				<div id="supplierInfo" class="table_title">
					<div class="tab_left"></div>
					<div class="titleBody">
						<div id="supplierNoTitle" style="width:50px; float:left;">NO</div>
						<div id="supplierCodeTitle" style="width:290px; float:left;">供应商</div>
						<div id="supplierNameTitle" style="width:100px; float:left;">退货商品种类</div>
						<div id="supplierBrandTitle" style="width:110px; float:left;">退货总量</div>
						<div id="supplierModelTitle" style="width:140px; float:left;">退货总次数</div>
						<div id="supplierVehicleTitle" style="width:150px; float:left;">退货金额</div>
						<div id="supplierReturnAmount" style="width:90px; float:right;">评定</div>
					</div>
					<div class="tab_right"></div>
					<table cellpadding="0" cellspacing="0" class="tab_Statistics" id="supplierTabList">
						<col width="50">
						<col width="250">
						<col width="80">
						<col width="110">
						<col width="80">
						<col width="150">
						<col width="90">
						<c:forEach items="${itemDTOs}" var="item" varStatus="status">
							<tr class="table-row-original">
								<td title="${status.count}" class="txt_left first-padding">${status.count}</td>
								<td title="${item.name}" class="txt_left">${item.name}</td>
								<td title="${item.returnProductCategories}"
									class="txt_left">${item.returnProductCategories}</td>
								<td title="${item.returnAmount}" class="txt_left">${item.returnAmount}</td>
								<td title="${item.returnTimes}" class="txt_left">${item.returnTimes}</td>
								<td title="${item.returnTotal}" class="txt_left">${item.returnTotal}</td>
								<td class="last-padding">
									<div class="basic" rateInit="${item.score}_${item.idString}"></div>
								</td>
							</tr>
						</c:forEach>
					</table>
				</div>
			</c:otherwise>
		</c:choose>
		<div class="clear"></div>
		<div class="height"></div>
        <bcgogo:hasPermission permissions="WEB.STAT.PURCHASE_ANALYST.RETURN_PRINT">
		    <div class="btnPrint" id="printBtn"><a class="buttonBig">打&nbsp;印</a></div>
        </bcgogo:hasPermission>

	</div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>