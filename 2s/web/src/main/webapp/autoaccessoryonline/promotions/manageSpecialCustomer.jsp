<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>客户消费优惠</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/promotions<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "PROMOTIONS_MANAGER_MENU");
        $().ready(function(){

            $("#last_date").change(function(){
                $("#startTime").val(getTodayOfPromotions());
                var lastDate=$(this).val();
                if(lastDate=="date_select_one_month"){
                    var endTime=dateUtil.getNDayCloseToday(30)+" 23:59"
                    $("#endTime").val(endTime);
                    $(".last_date").text("一个月内");
                }else if(lastDate=="date_select_three_month"){
                    var endTime=dateUtil.getNDayCloseToday(90)+" 23:59"
                    $("#endTime").val(endTime);
                    $(".last_date").text("三个月内");
                }else if(lastDate=="date_select_half_year"){
                    var endTime=dateUtil.getNDayCloseToday(180)+" 23:59"
                    $("#endTime").val(endTime);
                    $(".last_date").text("半年内");
                }else if(lastDate=="date_select_one_year"){
                    var endTime=dateUtil.getNDayCloseToday(365)+" 23:59"
                    $("#endTime").val(endTime);
                    $(".last_date").text("一年内");
                }

            });
            //init date select
            var startTimeStr = $("#startTime").val();
            var endTimeStr = $("#endTime").val();
            if(!G.Lang.isEmpty(startTimeStr) && !G.Lang.isEmpty(endTimeStr)){
               var startTime =  dateUtil.convertDateStrToDate(startTimeStr,dateUtil.dateStringFormatDayHourMin);
               var endTime =  dateUtil.convertDateStrToDate(endTimeStr,dateUtil.dateStringFormatDayHourMin);
               var days = parseInt(dateUtil.getDayBetweenTwoDate(startTime,endTime));
               if(days<=30){
                   $("#last_date").val("date_select_one_month");
                      $(".last_date").text("一个月内");
               }else if(days<=90 && days>30){
                   $("#last_date").val("date_select_three_month");
                      $(".last_date").text("三个月内");
               }else if(days<=180 && days>90){
                   $("#last_date").val("date_select_half_year");
                      $(".last_date").text("半年内");
               }else if(days<=365 && days>180){
                   $("#last_date").val("date_select_one_year");
                    $(".last_date").text("一年内");
               }
            }
        });

        function toManageSpecialCustomer(promotionsId){
            window.location.href="promotions.do?method=toManageSpecialCustomer&promotionsId="+promotionsId;
        }

        function saveSpecialCustomer(){
            var promotionsRuleType=$(".promotionsRuleType").val();
            var flag=true;
            $(".promotion_content").each(function(i){
                var discountAmount=G.rounding($(this).find(".discountAmount").val(),2);
                var minAmount=G.rounding($(this).find(".minAmount").val());
                if((minAmount!=0&&discountAmount==0)||(minAmount==0&&discountAmount!=0)){
                    nsDialog.jAlert("第"+(i+1)+"级优惠信息不完整！");
                    flag=false;
                }
            });
            if(!flag){
                return;
            }

            var $discountAmounts=$(".promotion_content .discountAmount");
            var lastDiscountAmount=10;
            for(var i=0;i<$discountAmounts.length;i++,lastDiscountAmount=discountAmount){
                var discountAmount=$discountAmounts[i];
                discountAmount=G.rounding($(discountAmount).val());
                if(discountAmount==0){
                    continue;
                }
                if(promotionsRuleType=="DISCOUNT_FOR_OVER_MONEY"){    //
                    if(discountAmount<0||discountAmount>=10){
                        nsDialog.jAlert("请在折扣输入框,输入0到10之间的整数或小数！");
                        return;
                    }
                    if(discountAmount>=lastDiscountAmount && i>0){
                        nsDialog.jAlert("第"+(i+1)+"级折扣应小于"+i+"级的折扣！");
                        return;
                    }
                }else if(promotionsRuleType=="REDUCE_FOR_OVER_MONEY"){    //
                    if(discountAmount==0){
                        nsDialog.jAlert("第"+(i+1)+"级优惠金额应不为0！");
                        return;
                    }
                    if(discountAmount<=lastDiscountAmount && i>0){
                        nsDialog.jAlert("第"+(i+1)+"级优惠金额应大于"+i+"级的优惠金额！");
                        return;
                    }
                }
            }

            var $minAmounts=$(".promotion_content .minAmount");
            var lastMinAmount=-1;
            for(var i=0;i<$discountAmounts.length;i++,lastMinAmount=minAmount){
                var minAmount=$minAmounts[i];
                minAmount=G.rounding($(minAmount).val());
                if(minAmount==0){
                    continue;
                }
                if(promotionsRuleType=="DISCOUNT_FOR_OVER_MONEY"){
                    if(minAmount<=0) {
                        nsDialog.jAlert("金额应大于0！");
                        return;
                    }
                }
                if(minAmount<=lastMinAmount){
                    nsDialog.jAlert("第"+(i+1)+"级累计消费金额应大于第"+i+"级的累计消费金额！");
                    return;
                }
            }

            $("#promotionsForm").ajaxSubmit({
                url:"promotions.do?method=savePromotions",
                dataType: "json",
                type: "POST",
                success: function(result){
                    if(!G.isEmpty(result)&&!result.success){
                        nsDialog.jAlert(result.msg);
                        return;
                    }
                    toSpecialCustomerList();
                },
                error:function(){
                    nsDialog.jAlert("网络异常！");
                }

            });
        }


    </script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
    <div class="clear i_height"></div>
    <div class="titBody">
        <jsp:include page="../supplyCenterLeftNavi.jsp">
            <jsp:param name="biMenu" value="specialCustomer"/>
            <jsp:param name="currPage" value="promotions"/>
        </jsp:include>
        <div class="bodyLeft">
            <h3 class="title">客户优惠设置</h3>
            <div class="cuSearch" style="float:left;">
                <form:form commandName="promotionsDTO" id="promotionsForm" action="promotions.do?method=savePromotions" method="post" name="thisform">
                    <input type="hidden" name="type" value="${promotionsDTO.type}"/>
                    <input id="promotionsId" type="hidden" name="id" value="${promotionsDTO.idStr}"/>
                    <input type="hidden" id="startTime" name="startTimeStr" value="${promotionsDTO.startTimeStr}" />
                    <input type="hidden" id="endTime" name="endTimeStr"  value="${promotionsDTO.endTimeStr}"/>
                    <table cellpadding="0" cellspacing="0" class="table_promotion">
                        <col width="85" >
                        <col >
                        <tr>
                            <td class="name_right">第一级</td>
                            <td>
                                <div class="promotion_content promotion_set" style="width:90%;">
							<span class="set_left">
                            	时间限制：
                            	<select id="last_date">
                                    <option value="date_select_one_month">一个月内</option>
                                    <option value="date_select_three_month">三个月内</option>
                                    <option value="date_select_half_year">半年内</option>
                                    <option value="date_select_one_year">一年内</option>
                                </select>
                            </span>
                                    <div class="set_right">
                                        <input class="promotionsRuleType" type="hidden" name="promotionsRuleDTOList[0].promotionsRuleType" id="promotionsRuleTypeHidden" value="${promotionsDTO.promotionsRuleDTOList[0].promotionsRuleType}"/>
                                        <span>累计消费满&nbsp;<input value="${promotionsDTO.promotionsRuleDTOList[0].minAmount}" name="promotionsRuleDTOList[0].minAmount" type="text" class="minAmount txt" style="width:80px;" />&nbsp;元</span>
                                <span>
                                	可享受优惠&nbsp;<input value="${promotionsDTO.promotionsRuleDTOList[0].discountAmount}" name="promotionsRuleDTOList[0].discountAmount" type="text" class="discountAmount txt" style="width:80px;" />
                                    <select id="promotionsRuleTypeSelect">
                                        <option value="DISCOUNT_FOR_OVER_MONEY">折扣</option>
                                        <option value="REDUCE_FOR_OVER_MONEY">金额</option>
                                    </select>
                                </span>
                                    </div>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td class="name_right">第二级</td>
                            <td>
                                <div class="promotion_content promotion_set" style="width:90%;">
							<span class="set_left">
                            	时间限制：
								<span class="last_date">一个月内</span>
                            </span>
                                    <div class="set_right">
                                        <input class="promotionsRuleType" type="hidden" name="promotionsRuleDTOList[1].promotionsRuleType" value="DISCOUNT_FOR_OVER_MONEY"/>
                                        <span>累计消费满&nbsp;<input value="${promotionsDTO.promotionsRuleDTOList[1].minAmount}" name="promotionsRuleDTOList[1].minAmount" type="text" class="minAmount txt" style="width:80px;" />&nbsp;元</span>
                                <span>
                                	可享受优惠&nbsp;<input value="${promotionsDTO.promotionsRuleDTOList[1].discountAmount}" name="promotionsRuleDTOList[1].discountAmount" type="text" class="discountAmount txt" style="width:80px;" />&nbsp;<label class="rText">折扣</label>
                                </span>
                                    </div>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td class="name_right">第三级</td>
                            <td>
                                <div class="promotion_content promotion_set" style="width:90%;">
							<span class="set_left">
                            	时间限制：
								<span class="last_date">一个月内</span>
                            </span>
                                    <div class="set_right">
                                        <input class="promotionsRuleType" type="hidden" name="promotionsRuleDTOList[2].promotionsRuleType" value="DISCOUNT_FOR_OVER_MONEY"/>
                                        <span>累计消费满&nbsp;<input value="${promotionsDTO.promotionsRuleDTOList[2].minAmount}" name="promotionsRuleDTOList[2].minAmount" type="text" class="minAmount txt" style="width:80px;" />&nbsp;元</span>
                                <span>
                                	可享受优惠&nbsp;<input value="${promotionsDTO.promotionsRuleDTOList[2].discountAmount}" name="promotionsRuleDTOList[2].discountAmount" type="text" class="discountAmount txt" style="width:80px;" />&nbsp;<label class="rText">折扣</label>
                                </span>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </table>
                    <div class="bodyRight">
                        <div class="help_bg"><a class="icon_help">帮助教程</a><a style="float:right; padding-right:10px;color:#007CDA">客户优惠</a></div>
                        <div class="titles promotion_titles">
                            <div class="list">客户优惠设置根据客户的累积消费金额进行设置！客户的累计消费金额将从设置完成后重新开始累计！</div>
                        </div>
                    </div>
                </form:form>

                <div class="clear i_height"></div>
                <div class="divTit" style="margin-left:211px;">
                    <a class="button" onclick="saveSpecialCustomer()">保存设置</a>
                    <a class="button" onclick="toSpecialCustomerList()">取&nbsp;消</a>
                </div>

            </div>
        </div>
    </div>
</div>
<div id="mask" style="display:block;position: absolute;"></div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>