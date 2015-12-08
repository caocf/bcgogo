<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<script>
    $(function(){
        $(".discount_amount").live("keyup", function(){
            var $target = $(this);
            if($target.closest(".bargainTD").find(".bargainType").val()=="DISCOUNT"){
                var tVal=G.rounding($target.val());
                if(tVal<0||tVal>=10){
                    $target.val(0);
                }
            }
        }).live("blur", function(){
                var $target = $(this);
                if($target.closest(".bargainTD").find(".bargainType").val() == 'BARGAIN'){
                    var tVal = G.rounding($target.val());
                    var inSalesPrice =G.rounding($target.closest("tr").find(".inSalesPrice").text());
                    if(tVal>=inSalesPrice){
                        nsDialog.jAlert("特价不可大于或等于原价！请重新设置。");
                        $target.val("");
                    }
                }
            });
    });

</script>

<div id="manageBargainAlert" class="alertMain promotionAlert" style="display: none">
    <div class="cuSearch">
        <form:form commandName="promotionsDTO" id="promotionsForm" action="promotions.do?method=savePromotions" method="post" name="thisform">
            <input id="promotionsType" type="hidden" name="type" value="BARGAIN"/>
            <input id="promotionsRanges" type="hidden" name="range" value="PARTLY"/>
            <input type="hidden" name="addPromotionsProductFlag" value="false"/>
            <table cellpadding="0" cellspacing="0" class="table_bargain table_promotion">
                <col width="85" >
                <col width="150" >
                <col >
                <tr>
                    <td class="name_right"><span class="red_color">*</span>促销名称</td>
                    <td colspan="2"><input  id="promotionsName"  name="name" value="${promotionsDTO.name}" type="text" class="txt txt_color" maxlength="20" />&nbsp;
                        <a class="right J_promotionsName_tip" style="display: none"></a><span class="gray_color">仅限20个字</span></td>
                </tr>
                <tr>
                    <td class="name_right" style="vertical-align:top;">促销描述</td>
                    <td colspan="2"><textarea id="description" name="description"  class="txt txt_color" maxlength="200">${promotionsDTO.description}</textarea>&nbsp;
                        <span class="gray_color" style="vertical-align:top;">仅限200个字</span></td>
                </tr>
                <tr>
                    <td class="name_right"><span class="red_color" >*</span>开始时间</td>
                    <td colspan="2">
                        <input  name="startTimeStr" type="text"  class="time_input startTimeStr txt"/>
                    </td>
                </tr>
                <tr>
                    <td class="name_right" style="vertical-align:top; padding-top:8px;"><span class="red_color">*</span>活动时间</td>
                    <td colspan="2" class="td_time">
                        <input id="serviceStartTime" type="hidden" value="${startTime}" />
                        <input id="timeFlag" type="hidden" value="${promotionsDTO.timeFlag}"/>
                        <label class="rad"><input class="date_select_week date_select" name="date_select" type="radio" />7天</label>
                        <label class="rad"><input class="date_select_month date_select" name="date_select" type="radio" />30天</label>
                        <label class="rad"><input class="date_select_three_month date_select" name="date_select" type="radio" />90天</label>
                        <label class="rad"><input class="date_select_unlimited date_select" name="date_select" type="radio" />不限时</label>
                        <span id="lastDate" class="yellow_color limited_Date">活动持续时间<span  id="cDay" class="red_color">30</span>天<span  id="cHour" class="red_color">0</span>时</span>
                                     <span  class="yellow_color un_limited" style="display: none">
                                        活动持续时间不限时
                                     </span>
                        <div>
                            <input type="radio"  name="date_select" class="date_select_define date_select"/>
                            自定义时间
                            <input value="${promotionsDTO.endTimeStr}" name="endTimeStr" type="text" class="time_input txt" style="display: none;" />
                                <%--<input value="${promotionsDTO.endTimeStr}" name="endTimeStr" type="hidden" />--%>

                        </div>
                    </td>
                </tr>
                <tr>
                    <td  class="name_right"><span class="red_color" >*</span>促销设置</td>
                    <td colspan="2"></td>
                </tr>
                <tr>
                    <td colspan="3" >
                        <table id="addPromotionsProductTable" class="bargain-addPromotionsProductTable tab_cuSearch tab_cuSearch_alert tabSales" cellpadding="0" cellspacing="0"
                               style="width: 640px;padding-left: 25px">

                        </table>
                    </td>
                </tr>
            </table>
            <div class="bargain-addPromotionsProduct"> </div>
            <div class="clear i_height"></div>
            <div class="divTit" style="margin-left:150px;">
                <a  pageType="manageBargain" fromType="inSales" class="savePromotionsBtn-InSales button">保存促销</a>
                <a class="button last-step-btn"  pageType="manageBargain" lastStepType="batch" onclick="backToPromotionManagerAlert(this)" class="button">返回上一步</a>
                <a class="button"  onclick="doCloseDialog(this)">关闭弹出框</a>
            </div>
        </form:form>
    </div>
</div>
