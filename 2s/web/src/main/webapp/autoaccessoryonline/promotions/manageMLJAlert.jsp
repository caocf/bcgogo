<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script>

    $().ready(function(){
        $("input[name='promotions_rule']").click(function(){
            $(".promotionsRuleType").val($(this).val());
            $("#promotionsRuleType").val($(this).val());
            $("[name^='EXAMPLE_']").hide();
            $("#addMLJRuleLevel").show();
            var promotionsRuleType=$(this).val();
            if(promotionsRuleType=="DISCOUNT_FOR_OVER_MONEY"){
                $(".t2").text("元");
                $(".t3").text("打");
                $(".t4").text("折");
                $("[name='EXAMPLE_DISCOUNT_FOR_OVER_MONEY']").show();
            }else if(promotionsRuleType=="DISCOUNT_FOR_OVER_AMOUNT"){
                $(".t2").text("件");
                $(".t3").text("打");
                $(".t4").text("折");
                $("[name='EXAMPLE_DISCOUNT_FOR_OVER_AMOUNT']").show();
            }else if(promotionsRuleType=="REDUCE_FOR_OVER_MONEY"){
                $(".t2").text("元");
                $(".t3").text("减");
                $(".t4").text("元");
                $("[name='EXAMPLE_REDUCE_FOR_OVER_MONEY']").show();
            }else if(promotionsRuleType=="REDUCE_FOR_OVER_AMOUNT"){
                $(".t2").text("件");
                $(".t3").text("减");
                $(".t4").text("元");
                $("[name='EXAMPLE_REDUCE_FOR_OVER_AMOUNT']").show();
            }
        });
        if(G.isEmpty($("#promotionsId").val())){
            $(".table_mlj .promotions_rule_default").click();
        }else{
            var promotionsRuleType=$(".promotionsRuleType").val();
            $("input[name='promotions_rule']").each(function(){
                if($(this).val()==promotionsRuleType){
                    $(this).click();
                }
            });

        }
    });

</script>


<div id="manageMLJAlert" class="alertMain promotionAlert" style="display: none">
    <div class="cuSearch">
    <div class="cuSearch">
        <form:form commandName="promotionsDTO" id="promotionsForm" action="promotions.do?method=savePromotions" method="post" name="thisform">
            <input id="promotionsType" type="hidden" name="type" value="MLJ"/>
            <input id="promotionsRanges" type="hidden" name="range" value="PARTLY"/>
             <input type="hidden" name="addPromotionsProductFlag" value="false"/>

            <table cellpadding="0" cellspacing="0" class="table_mlj table_promotion">
                <col width="85" >
                <col width="170" >
                <col width="460">
                <tr>
                    <td class="name_right"><span class="red_color">*</span>促销名称</td>
                    <td colspan="2"><input id="promotionsName" name="name" value="${promotionsDTO.name}" type="text" class="txt txt_color" maxlength="20"/>&nbsp;
                        <a class="right J_promotionsName_tip" style="display: none"></a><span class="gray_color">仅限20个字</span></td>
                </tr>
                <tr>
                    <td class="name_right" style="vertical-align:top;">促销描述</td>
                    <td colspan="2"><textarea id="description"  name="description" class="txt txt_color" maxlength="200">${promotionsDTO.description}</textarea>&nbsp;
                        <span class="gray_color" style="vertical-align:top;">仅限200个字</span></td>
                </tr>
                <tr>
                    <td class="name_right"><span class="red_color" >*</span>开始时间</td>
                    <td colspan="2">
                        <input value="${promotionsDTO.startTimeStr}" name="startTimeStr" type="text"  class="time_input startTimeStr txt" />
                    </td>
                </tr>
                <tr>
                    <td class="name_right" style="vertical-align: top;padding-top: 10px"><span class="red_color">*</span>结束时间</td>
                    <td colspan="2" class="td_time">
                        <input id="timeFlag" type="hidden" value="${promotionsDTO.timeFlag}"/>
                        <label class="rad"><input type="radio" name="date_select" class="date_select_week date_select" />7天</label>
                        <label class="rad"><input type="radio" name="date_select" class="date_select_month date_select"/>30天</label>
                        <label class="rad"><input type="radio" name="date_select" class="date_select_three_month date_select"/>90天</label>
                        <label class="rad"><input type="radio" name="date_select" class="date_select_unlimited date_select"/>不限时</label>
                        <span class="yellow_color limited_Date"> 活动持续时间
                                        <span class="red_color" id="cDay">0</span>天
                                        <span class="red_color" id="cHour">0</span>时
                                    </span>
                                     <span  class="yellow_color un_limited" style="display: none">
                                        活动持续时间不限时
                                     </span>
                        <div>
                            <input id="serviceStartTime" type="hidden" value="${startTime}" />
                            <input type="radio" name="date_select" class="date_select_define date_select"/>
                            自定义时间
                            <input value="${promotionsDTO.endTimeStr}" name="endTimeStr" type="text" class="time_input txt" style="display: none;" />
                            <%--<input value="${promotionsDTO.endTimeStr}" name="endTimeStr" type="hidden" />--%>

                        </div>
                    </td>
                </tr>
                <tr>
                    <td class="name_right" style="vertical-align:top;"><span class="red_color">*</span>促销内容</td>
                    <td colspan="2">
                        <div>
                            <input id="promotionsRuleType" type="hidden" />
                            <div>
                                <label class="rad"><input type="radio" checked="true" class="promotions_rule_default" name="promotions_rule" value="DISCOUNT_FOR_OVER_MONEY"/>满金额打折扣</label>
                                <label class="rad"><input type="radio" name="promotions_rule" value="DISCOUNT_FOR_OVER_AMOUNT"/>满数量打折扣</label>
                            </div>
                            <div style="margin-top: 2px">
                                <label class="rad"><input type="radio" name="promotions_rule" value="REDUCE_FOR_OVER_MONEY"/>满金额减金额</label>
                                <label class="rad"><input type="radio" name="promotions_rule" value="REDUCE_FOR_OVER_AMOUNT"/>满数量减金额</label>
                            </div>
                        </div>
                        <div class="example gray_color" name="EXAMPLE_DISCOUNT_FOR_OVER_MONEY">
                            <span class="example_name">参考示例：</span>
                            <div class="exampleList">满&nbsp;<span>100</span>&nbsp;元&nbsp;打&nbsp;<span>9</span>&nbsp;折</div>
                            <div class="exampleList">满&nbsp;<span>200</span>&nbsp;元&nbsp;打&nbsp;<span>8</span>&nbsp;折</div>
                            <div class="exampleList">满&nbsp;<span>300</span>&nbsp;元&nbsp;打&nbsp;<span>7</span>&nbsp;折</div>
                            <div class="exampleList">（满金额为单笔订单金额）</div>
                        </div>
                        <div class="example gray_color" name="EXAMPLE_DISCOUNT_FOR_OVER_AMOUNT" style="display: none">
                            <span class="example_name">参考示例：</span>
                            <div class="exampleList">满&nbsp;<span>100</span>&nbsp;件&nbsp;打&nbsp;<span>9</span>&nbsp;折</div>
                            <div class="exampleList">满&nbsp;<span>200</span>&nbsp;件&nbsp;打&nbsp;<span>8</span>&nbsp;折</div>
                            <div class="exampleList">满&nbsp;<span>300</span>&nbsp;件&nbsp;打&nbsp;<span>7</span>&nbsp;折</div>
                            <div class="exampleList">（件为设置时的通用单</div>
                            <div class="exampleList">位打折时会以商品的</div>
                            <div class="exampleList">销售单位为准）</div>
                        </div>
                        <div class="example gray_color" name="EXAMPLE_REDUCE_FOR_OVER_MONEY" style="display: none">
                            <span class="example_name">参考示例：</span>
                            <div class="exampleList">满&nbsp;<span>100</span>&nbsp;元&nbsp;减&nbsp;<span>10</span>&nbsp;元</div>
                            <div class="exampleList">满&nbsp;<span>200</span>&nbsp;元&nbsp;减&nbsp;<span>25</span>&nbsp;元</div>
                            <div class="exampleList">满&nbsp;<span>300</span>&nbsp;元&nbsp;减&nbsp;<span>40</span>&nbsp;元</div>
                            <div class="exampleList">（满金额为单笔订单金额）</div>
                        </div>
                        <div class="example gray_color" name="EXAMPLE_REDUCE_FOR_OVER_AMOUNT" style="display: none">
                            <span class="example_name">参考示例：</span>
                            <div class="exampleList">满&nbsp;<span>100</span>&nbsp;件&nbsp;减&nbsp;<span>10</span>&nbsp;元</div>
                            <div class="exampleList">满&nbsp;<span>200</span>&nbsp;件&nbsp;减&nbsp;<span>25</span>&nbsp;元</div>
                            <div class="exampleList">满&nbsp;<span>300</span>&nbsp;件&nbsp;减&nbsp;<span>40</span>&nbsp;元</div>
                            <div class="exampleList">（满金额为单笔订单金额）</div>
                        </div>

                        <div id="promotionsRuleDiv">
                            <c:if test="${empty promotionsDTO.promotionsRuleDTOList}">
                                <div class="promotion_content">
                                    <div class="line beforeEdit">
                                        <input class="promotionsRuleType" type="hidden" value="" name="promotionsRuleDTOList[0].promotionsRuleType"/>
                                        <input type="hidden" class="ruleLevel" name="promotionsRuleDTOList[0].level" value="0"/>
                                        <a class="icon_close"></a>
                                        <span>满</span>&nbsp;<input type="text" name="promotionsRuleDTOList[0].minAmount" class="txt txt_color minAmount" style="width:50px;" />&nbsp;<span class="t2">元</span>，
                                        <span class="t3">打</span>&nbsp;<input type="text" name="promotionsRuleDTOList[0].discountAmount" class="txt txt_color discountAmount" style="width:50px;" />&nbsp;<span class="t4">折</span>
                                    </div>

                                </div>
                            </c:if>
                            <c:if test="${not empty promotionsDTO.promotionsRuleDTOList}">
                                <c:forEach items="${promotionsDTO.promotionsRuleDTOList}" var="ruleDTO" varStatus="status">
                                    <div class="promotion_content">
                                        <div class="line beforeEdit">
                                            <input class="promotionsRuleType" type="hidden"
                                                   value='${ruleDTO.promotionsRuleType!=null?ruleDTO.promotionsRuleType:""}'
                                                   name="promotionsRuleDTOList[${status.index}].promotionsRuleType"/>
                                            <input type="hidden" class="ruleLevel" name="promotionsRuleDTOList[${status.index}].level" value="${status.index}"/>
                                            <a class="icon_close"></a>
                                            <span>满</span>&nbsp;<input type="text" name="promotionsRuleDTOList[${status.index}].minAmount"
                                                                       value='${ruleDTO.minAmount!=null?ruleDTO.minAmount:""}'
                                                                       class="txt txt_color minAmount" style="width:50px;" />&nbsp;<span class="t2">元</span>，
                                            <span class="t3">打</span>&nbsp;<input type="text" name="promotionsRuleDTOList[${status.index}].discountAmount"
                                                                                  value='${ruleDTO.discountAmount!=null?ruleDTO.discountAmount:""}'
                                                                                  class="txt txt_color discountAmount" style="width:50px;" />&nbsp;<span class="t4">折</span>
                                        </div>

                                    </div>
                                </c:forEach>
                            </c:if>
                        </div>

                        <div class="promotion_content_add add_content" id="addMLJRuleLevel">
                            +增加层级+
                        </div>
                    </td>
                </tr>
            </table>
            <div class="mlj-addPromotionsProduct"> </div>
        <div class="clear i_height"></div>
        <div class="divTit" style="margin-left:130px;">
            <a  class="savePromotionsBtn-InSales button" pageType="manageMLJ" fromType="inSales">确定</a>
            <a class="button last-step-btn" pageType="manageMLJ" lastStepType="batch" onclick="backToPromotionManagerAlert(this)">返回上一步</a>
            <a class="button"  onclick="doCloseDialog(this)">关闭弹出框</a>
        </div>
        </form:form>
    </div>

</div>
