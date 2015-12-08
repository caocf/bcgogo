<%@ page import="com.bcgogo.config.ConfigController" %>
<%--
  Created by IntelliJ IDEA.
  User: mayan
  Date: 13-11-29
  Time: 下午1:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>保险理赔</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/register<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>

    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/inventorySearchIndex<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/invoiceCustomerSearch<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/vehiclelicenceNo<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/invoicesolr<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/checkMobileAndTelphone<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuCurrentItem,"详情");

     $(function(){
         $(".tabSlip tr").css({"border":"1px solid #bbbbbb","border-width":"1px 0px"});
         $(".tabSlip tr:nth-child(odd)").css("background","#eaeaea");
         $(".tabSlip tr").not(".titleBg").hover(
                 function () {
                     $(this).find("td").css({"background":"#fceba9","border":"1px solid #ff4800","border-width":"1px 0px","color":"#ff4800"});

                     $(this).css("cursor","pointer");
                 },
                 function () {
                     $(this).find("td").css({"background-Color":"#FFFFFF","border":"1px solid #bbbbbb","border-width":"1px 0px 0px 0px","color":"#272727"});
                     $(".tabSlip tr:nth-child(odd)").not(".titleBg" ).find("td").css("background","#eaeaea");
                 }
         );

         $("#addUp").click(function(){location.href="add_innerPicking.html";})


         $(".alert").hide();
         $(".cuxiao").hover(
                 function(){
                     $(this).parent().find(".alert").show();
                     $(this).parent().find(".alert").hover(
                             function(){
                                 $(this).parent().find(".alert").show();
                             },
                             function(){
                                 $(this).parent().find(".alert").hide();
                             }
                     )
                 }
         )


         $("#printBtn").bind("click",function(){
             if ($("#id").val()) {
                 window.open("insurance.do?method=printInsuranceOrderPreview&insuranceOrderId=" + $("#id").val(), "_blank");
                 window.print();
             }
         });




     });

    </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<div class="title_left"> </div>
<div class="title_center"></div>
<div class="title_right"></div>
</div>
<div class="i_main clear">
<div class="mainTitles">
    <div class="titleWords">保险理赔</div>
</div>
<div class="booking-management">
<input id="id" type="hidden" value="${insuranceOrderDTO.id}"/>
    <div class="insurance_font14">
        <span>保险单号：${insuranceOrderDTO.policyNo}</span>
        <span>报案编号：${insuranceOrderDTO.reportNo}</span>
        <span>保险公司：${insuranceOrderDTO.insuranceCompany}</span>
        <span>投保日期：${insuranceOrderDTO.insureStartDateStr}</span>
        <span>到期日期：${insuranceOrderDTO.insureEndDateStr}</span>
    </div>

</div>
<div class="titBody">
    <div class="lineTitle">
        索赔信息 </div>
    <div class="clear"></div>
    <div class="customer">
        <table width="100%" border="0" class="order-table">
            <colgroup>
                <col  width="100"/>
                <col>
            </colgroup>

            <tr>

                <td valign="top">被保险人：</td>
                <td>${insuranceOrderDTO.customer}</td>
                <td align="right">车牌号：</td>
                <td>${insuranceOrderDTO.licenceNo}</td>
                <td align="right">驾驶人：</td>
                <td>${insuranceOrderDTO.driver}</td>
                <td align="right">驾驶证号：</td>
                <td>${insuranceOrderDTO.drivingNo}</td>
                <td align="right">手机：</td>
                <td>${insuranceOrderDTO.mobile}</td>
            </tr>
            <tr>
                <td>车牌品牌：</td>
                <td>${insuranceOrderDTO.brand}</td>
                <td>车型：</td>
                <td>${insuranceOrderDTO.brand}${insuranceOrderDTO.model}</td>
                <td align="right">发动机号：</td>
                <td>${insuranceOrderDTO.engineNumber}</td>
                <td align="right">车架号：</td>
                <td>${insuranceOrderDTO.chassisNumber}</td>


            </tr>
            <tr>
                <td>报案人：</td>
                <td>${insuranceOrderDTO.reporter}</td>
                <td align="right">联系方式：</td>
                <td>${insuranceOrderDTO.reporterContact}</td>
                <td align="right">报案时间：</td>
                <td>${insuranceOrderDTO.reportDateStr}</td>
                <td align="right">出险时间：</td>
                <td>${insuranceOrderDTO.accidentDateStr}</td>
                <td align="right">出险地点：</td>
                <td>${insuranceOrderDTO.accidentAddress}</td>
            </tr>
            </div>
        </table>
        <div class="clear"></div>
    </div>
</div>
<div class="clear i_height"></div>
<div class="titBody">
    <div class="lineTitle">
        查勘意见 </div>
    <div class="clear"></div>
    <div class="customer">
        <table width="100%" border="0" class="order-table">
            <colgroup>
                <col  width="100"/>
                <col>
            </colgroup>
            <tr>
                <td>查勘时间：</td>
                <td>${insuranceOrderDTO.surveyDateStr}</td>
                </tr>
            <tr>
                <td>事故处理方式：</td>
                <td>${insuranceOrderDTO.accidentHandling}</td>
                </tr>
            <tr>
                <td >查勘地点：</td>
                <td>${insuranceOrderDTO.surveyAddress}</td>
                </tr>
            <tr>
                <td>出险原因：</td>
                <td>${insuranceOrderDTO.accidentCause}</td>
                </tr>
            <tr>
                <td>事故责任：</td>
                <td>${insuranceOrderDTO.accidentLiability}</td>
                </tr>
            <tr>
                <td>是否第一现场勘查：</td>
                <td>${insuranceOrderDTO.firstSurveyAddress}</td>
                </tr>
            <tr>
                <td>事故类型：</td>
                <td>${insuranceOrderDTO.accidentType}</td>
                 </tr>
            <tr>
                <td>查勘人员意见：</td>
                <td>${insuranceOrderDTO.surveyOpinion}</td>
                </tr>

            <tr>
                <td>涉及险种：</td>
                <td>${insuranceOrderDTO.relateInsuranceItems}</td>
            </tr>
        </table>
        <div class="clear"></div>
    </div>
</div>
<div class="clear i_height"></div>
<div class="titBody">
    <div class="lineTitle">损失情况</div>
    <div class="lineBody bodys">

        <table width="100%" border="0" class="order-table">
            <colgroup>
                <col  width="100"/>
                <col>
            </colgroup>

            <tr>
                <td>定损时间：</td>
                <td>${insuranceOrderDTO.estimateDateStr}</td>
                <td>定损地点：</td>
                <td>${insuranceOrderDTO.estimateAddress}</td>
                <td>施救费：</td>
                <td>${insuranceOrderDTO.insuranceCost}</td>
                <td colspan="4"><label>残值处理方式：</label>
                <td>${insuranceOrderDTO.scrapApproach}</td>
                <td>扣减残值：</td>
                <td>${insuranceOrderDTO.scrapValue}</td>
            </tr>
        </table>


        <div class="cuSearch">
            <div class="gray-radius" style="margin:0;">

                <h3 class="titleName">更换项目</h3>
                <table id="replacementTable" class="tab_cuSearch" cellpadding="0" cellspacing="0" style="width:950px;">
                    <tr class="titleBg">
                        <td style="padding-left:10px;">序号</td>
                        <td>商品编号</td>
                        <td>更换部件名称</td>
                        <td>品牌/产地</td>
                        <td>规格</td>
                        <td>型号</td>
                        <td>单价</td>
                        <td>数量</td>
                        <td>单位</td>
                        <td>小计</td>
                    </tr>
                    <tr class="space">
                        <td colspan="10"></td>
                    </tr>

                    <c:forEach items="${insuranceOrderDTO.itemDTOs}" var="itemDTOs" varStatus="status">
                    <tr class="titBody_Bg">
                        <td style="padding-left:10px;">${status.index+1}</td>
                        <td>${itemDTOs.commodityCode}</td>
                        <td>${itemDTOs.productName}</td>
                        <td>${itemDTOs.brand}</td>
                        <td>${itemDTOs.spec}</td>
                        <td>${itemDTOs.model}</td>
                        <td><fmt:formatNumber pattern="#.##" value="${itemDTOs.price}" /></td>
                        <td><fmt:formatNumber pattern="#.##" value="${itemDTOs.amount}"/></td>
                        <td>${itemDTOs.unit}</td>
                        <td><fmt:formatNumber pattern="#.##" value="${itemDTOs.total}"/></td>



                    </tr>
                    <tr class="titBottom_Bg">
                        <td colspan="10"></td>
                    </tr>
                    </c:forEach>


                </table>
                <div class="clear i_height"></div>
                <h3 class="titleName">修理项目</h3>
                <table id="fixTable" class="tab_cuSearch" cellpadding="0" cellspacing="0" style="width:950px;">
                    <tr class="titleBg">
                        <td style="padding-left:10px;">序号</td>
                        <td>修理项目名称</td>
                        <td>金额</td>
                    </tr>
                    <tr class="space">
                        <td colspan="3"></td>
                    </tr>

                    <c:forEach items="${insuranceOrderDTO.serviceDTOs}" var="serviceDTOs" varStatus="status">
                        <tr class="titBody_Bg">
                            <td style="padding-left:10px;">${status.index+1}</td>
                            <td>${serviceDTOs.service}</td>
                            <td><fmt:formatNumber pattern="#.##" value="${serviceDTOs.total}"/></td>

                        </tr>
                        <tr class="titBottom_Bg">
                            <td colspan="3"></td>
                        </tr>
                    </c:forEach>


                </table>

                <div class="clear i_height"></div>
            </div>
            <div class="clear i_height"></div>
            <div class="insurance_font14">
                换件及修理费用总计：<br />
                <span>理赔金额：¥<fmt:formatNumber pattern="#.##" value="${insuranceOrderDTO.claims}"/></span>
                <span>赔付比例：${insuranceOrderDTO.claimsPercentage}%</span>
                <span>个人赔付金额：¥<fmt:formatNumber pattern="#.##" value="${insuranceOrderDTO.personalClaims}"/></span>
                <span>个人赔付比例：${insuranceOrderDTO.personalClaimsPercentage}%</span>
            </div>
        </div>
    </div>
    <div class="lineBottom"></div>
    <div class="clear i_height"></div>
</div>
<div id="printBtn" class="shopping_btn">
    <div class="divImg"> <img src="images/print.png" />
        <div class="sureWords">打印</div>
    </div>
</div>
</div>
<div class="clear i_height"></div>
</div>
</div>

<div ${dealingType == '已结算' ?"style='display:block'": "style='display:none'"} style="display: block;" id="nullify" class="jie_suan_wash"></div>


<div ${dealingType == '已作废' ?"style='display:block'": "style='display:none'"} style="display: block;" id="settled" class="zuofei_wash"></div>



<div id="mask"  style="display:block;position: absolute;"> </div>
<div id="commodityCode_dialog" style="display:none">
    <span id="commodityCode_dialog_msg"></span>
</div>
<div id="id-searchcomplete"></div>
<div id="id-searchcompleteMultiselect"></div>
<div id="div_brand" class="i_scroll" style="display:none;height:230px">
    <div class="Scroller-Container" id="Scroller-Container_id"></div>
</div>

<!-- 车牌号下拉菜单 -->
<div id="div_brandvehiclelicenceNo" class="i_scroll" style="display:none;width:132px;">
    <div class="Container" style="width:132px;">
        <div id="Scroller-1licenceNo" style="width:132px;">
            <div class="Scroller-Containerheader" id="Scroller-Container_idlicenceNo">
            </div>
        </div>
    </div>
</div>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:400px; display:none;" allowtransparency="true" width="630px" height="450px" frameborder="0" src=""></iframe>
</body>
</html>