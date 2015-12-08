<%--
  Created by IntelliJ IDEA.
  User: king
  Date: 13-9-29
  Time: 下午4:35
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<script>

</script>
<div class="ui-dialog-titlebar ui-widget-header ui-corner-all ui-helper-clearfix">
    <span id="ui-dialog-title-manageFreeShippingAlert" class="ui-dialog-title">创建促销</span>
    <a class="ui-dialog-titlebar-close ui-corner-all" href="javascript:doHiddeDiv();" role="button">
        <span class="ui-icon ui-icon-closethick">close</span>
    </a>
</div>
<div id="manageFreeShippingAlert" class="alertMain promotionAlert ui-dialog-content ui-widget-content" style="width: auto; min-height: 0px; height: 560px;">
    <div class="cuSearch">
        <form:form commandName="promotionsDTO" id="promotionsForm" action="promotions.do?method=savePromotions" method="post" name="thisform">
            <input id="promotionsType" type="hidden" name="type" value="FREE_SHIPPING"/>
            <input id="promotionsRanges" type="hidden" name="range" value="PARTLY"/>
            <input type="hidden" name="addPromotionsProductFlag" value="false"/>
            <table cellpadding="0" cellspacing="0" class="table_free_shipping table_promotion">
                <col width="85" >
                <col width="150" >
                <col >
                <tr>
                    <td class="name_right"><span class="red_color">*</span>促销名称</td>
                    <td colspan="2"><input id="promotionsName" name="name" type="text" class="txt txt_color" maxlength="20"/>&nbsp;
                        <a class="right J_promotionsName_tip" style="display: none"></a><span class="gray_color">仅限20个字</span></td>
                </tr>
                <tr>
                    <td class="name_right" style="vertical-align:top;">促销描述</td>
                    <td colspan="2"><textarea id="description" name="description"   class="txt txt_color" maxlength="200"></textarea>&nbsp;
                        <span class="gray_color" style="vertical-align:top;">仅限200个字</span></td>
                </tr>
                <tr>
                    <td class="name_right"><span class="red_color" >*</span>开始时间</td>
                    <td colspan="2">
                        <input  name="startTimeStr" type="text"  class="time_input startTimeStr txt" />
                    </td>
                </tr>
                <tr>
                    <td class="name_right" style="vertical-align:top; padding-top:8px;"><span class="red_color">*</span>活动时间</td>
                    <td colspan="2" class="td_time">
                        <input id="serviceStartTime" type="hidden" value="${startTime}" />
                        <input id="timeFlag" type="hidden" />
                        <label class="rad"><input name="date_select" class="date_select_week date_select" type="radio" />7天</label>
                        <label class="rad"><input name="date_select" class="date_select_month date_select" type="radio" />30天</label>
                        <label class="rad"><input name="date_select" class="date_select_three_month date_select" type="radio" />90天</label>
                        <label class="rad"><input name="date_select" class="date_select_unlimited date_select" type="radio" />不限时</label>
                        <span class="yellow_color limited_Date">活动持续时间<span id="cDay" class="red_color">0</span>天<span id="cHour" class="red_color">0</span>时</span>
                                <span  class="yellow_color un_limited" style="display: none">
                                        活动持续时间不限时
                                     </span>
                        <div>
                            <input name="date_select" type="radio" class="date_select_define date_select"/>
                            自定义时间
                            <input name="endTimeStr" type="text" class="time_input txt" style="display: none;" />

                        </div>
                    </td>
                </tr>

                <tr>
                    <td class="name_right" style="vertical-align:top;"><span class="red_color">*</span>促销内容</td>
                    <td colspan="2">
                        <div id=areaData></div>
                        <div id="areaBoardDiv" class="promotion_content promotion_set">
                            <input id="postType" type="hidden" name="postType" value="POST">
                            <select class="postSelector">
                                <option value="POST">包邮地区</option>
                                <option value="UN_POST">不包邮地区</option>
                            </select>
                            &nbsp;<span class="yellow_color" id="postTitle">包邮地区：</span>
                            <div class="areaList"></div>
                            <div class="hr"></div>
                            <div>
                                <label class="rad checkbox"><input onclick="promotionsAreaClick(this)" name="gArea" id="countryArea" value="COUNTRY" areaNo="-1" type="checkbox" />全国</label>
                                <label class="rad checkbox"><input onclick="promotionsAreaClick(this)" name="gArea" value="PROVINCE" areaNo="${shop.province}" type="checkbox" class="j_owned_province"/>本省</label>
                                <label class="rad checkbox"><input onclick="promotionsAreaClick(this)" name="gArea" value="CITY" areaNo="${shop.city}" type="checkbox" class="j_owned_city"/>本市</label>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr style="height: 10px"></tr>
                <tr style="margin-top: 5px">
                    <td style="vertical-align: top;" class="name_right"><span class="red_color">*</span>促销条件</td>
                    <td style="vertical-align: top;" colspan="2">
                        <div>
                            <label class="rad">
                                <input type="radio" name="promotionsRuleRio" onclick="addPromotionsCondition(this)" value="YES"  checked="true"/>有条件
                            </label>
                            <label class="rad">
                                <input type="radio" name="promotionsRuleRio" onclick="addPromotionsCondition(this)" value="NO"  />无条件
                            </label>
                        </div>
                        <div id="promotionsRuleDiv">
                            满<input id="minAmount" name="promotionsRuleDTOList[0].minAmount" value="${limitAmount}" class="minAmount txt" type="text" style="width:100px;" autocomplete="off">
                            <span class="j_over_unit"> 元</span>
                            <%--<input id="promotionsLimiter" name="promotionsLimiter" type="hidden" value="OVER_MONEY"/>--%>
                            <select id="promotionsLimiterSelector" class="promotionsLimiterSelector" name="promotionsLimiter">
                                <option value="OVER_MONEY">金额（订单金额）</option>
                                <option value="OVER_AMOUNT">数量</option>
                            </select>
                        </div>
                    </td>
                </tr>
            </table>
            <div class="freeShipping-addPromotionsProduct"> </div>
            <div class="clear i_height"></div>
            <div class="divTit" style="margin-left:150px;">
                <a class="savePromotionsBtn-InSales button" fromType="inSales"  pageType="manageFreeShipping">确定</a>
                <a class="button last-step-btn" pageType="manageFreeShipping" lastStepType="" onclick="backToPromotionManagerAlert(this)">返回上一步</a>
                <a class="button"  onclick="doHiddeDiv()">关闭弹出框</a>
            </div>
        </form:form>
    </div>

</div>

<div class="ui-resizable-handle ui-resizable-n" style="z-index: 1000;"></div>
<div class="ui-resizable-handle ui-resizable-e" style="z-index: 1000;"></div>
<div class="ui-resizable-handle ui-resizable-s" style="z-index: 1000;"></div>
<div class="ui-resizable-handle ui-resizable-w" style="z-index: 1000;"></div>
<div class="ui-resizable-handle ui-resizable-se ui-icon ui-icon-gripsmall-diagonal-se ui-icon-grip-diagonal-se" style="z-index: 1000;"></div>
<div class="ui-resizable-handle ui-resizable-sw" style="z-index: 1000;"></div>
<div class="ui-resizable-handle ui-resizable-ne" style="z-index: 1000;"></div>
<div class="ui-resizable-handle ui-resizable-nw" style="z-index: 1000;"></div>
