<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>促销管理</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/promotionsUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/promotions<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "PROMOTIONS_MANAGER_MENU");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"列表");
        function toSendPromotionMsgByPromotionsId(promotionsId){
            if(G.isEmpty(promotionsId)){
                return;
            }
            var pContent=$("#item"+promotionsId).val();
            window.location.href="promotions.do?method=toSendPromotionMsg&promotionsIdStr="+promotionsId+"&pContent="+pContent;
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
            <jsp:param name="biMenu" value="null"/>
            <jsp:param name="currPage" value="promotions"/>
        </jsp:include>
        <div class="bodyLeft">
            <input id="serviceStartTime" type="hidden" value="${startTime}" />
            <h3 class="title">促销管理</h3>
            <div class="cuSearch">
                <div class="lineTitle">
                    我的所有促销<span class="gray_color">（共有<span id="total_promotions">0</span>条促销）</span>
                    <a id="addPromotionBtn" class="new_promotion">新建促销</a>
                </div>
                <div class="cartBody lineBody">
                    <div class="lineAll">
                        <div class="divTit">
                            促销名称
                            <input type="text" field="promotionsName"  class="txt"/>
                        </div>
                        <div class="divTit" style="padding-left: 15px">
                            促销类型
                            <select id="promotionsType" class="txt txt_color">
                                <option value="">所有</option>
                                <option value="MLJ">满立减</option>
                                <option value="MJS">满就送</option>
                                <option value="BARGAIN">特价商品</option>
                                <option value="FREE_SHIPPING">送货上门</option>
                            </select>
                        </div>
                        <div class="divTit" style="padding-left: 15px">
                            状态
                            <select id="promotionStatus" class="txt txt_color">
                                <option value="">所有</option>
                                <option value="UN_USED">未使用</option>
                                <option value="UN_STARTED">未开始</option>
                                <option value="USING">进行中</option>
                                <option value="SUSPEND"> 已暂停</option>
                                <option value="EXPIRE">已结束</option>
                            </select>
                        </div>
                        <div class="divTit" style="padding-left: 20px">
                            <a id="searchPromotionsBtn" class="button">搜 索</a>
                        </div>
                    </div>
                    <input type="hidden" id="sortCondition" currentSort="DESC" sortFiled="creationDate" />
                    <div class="clear i_height"></div>
                    <div class="line_develop line_history line_promotion promotions_info_td sort_title_min_width">
                        <div class="sort_label">排序方式：</div>
                        <a class="payTime J_promotions_sort"  ascContact="点击后按开始时间升序排列！" descContact="点击后按开始时间降序排列！" currentSort="DESC" sortFiled="startTime">开始时间<span class="J-sort-span arrowDown"></span>
                            <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
                                <span class="arrowTop" style="margin-left:20px;"></span>
                                <div class="alertAll">
                                    <div class="alertLeft"></div>
                                    <div class="alertBody">
                                        点击后按开始时间升序排列！
                                    </div>
                                    <div class="alertRight"></div>
                                </div>
                            </div>
                        </a>
                        <a class="payTime J_promotions_sort" ascContact="点击后按截止时间升序排列！" descContact="点击后按截止时间降序排列！" currentSort="DESC" sortFiled="endTime">截止时间<span class="J-sort-span arrowDown"></span>
                            <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
                                <span class="arrowTop" style="margin-left:20px;"></span>
                                <div class="alertAll">
                                    <div class="alertLeft"></div>
                                    <div class="alertBody">
                                        点击后按截止时间升序排列！
                                    </div>
                                    <div class="alertRight"></div>
                                </div>
                            </div>
                        </a>
                    </div>
                    <table id="promotionsTable" class="tab_cuSearch tabSales" cellpadding="0" cellspacing="0">
                        <col width="110">
                        <col>
                        <col width="110">
                        <col width="110">
                        <col width="80">
                        <col width="80">
                        <col width="115">
                        <tr class="titleBg">
                            <td style="padding-left:10px;">促销名称</td>
                            <td>内容</td>
                            <td>开始时间</td>
                            <td>截止时间</td>
                            <td>促销类型</td>
                            <td>状态</td>
                            <td>操作</td>
                        </tr>
                        <tr class="space"><td colspan="7"></td></tr>

                    </table>
                    <div class="clear i_height"></div>
                    <!----------------------------分页----------------------------------->
                    <div class="i_pageBtn">
                        <bcgogo:ajaxPaging
                                url="promotions.do?method=getPromotionsDTO"
                                data="{
                                startPageNo:1,maxRows:15,
                                 currentSort:$('#sortCondition').attr('currentSort'),
                                 sortFiled:$('#sortCondition').attr('sortFiled')
                                 }"
                                postFn="initPromotionList"
                                dynamical="_promotionList"/>
                    </div>
                    <div class="clear i_height"></div>
                </div>
                <div class="lineBottom"></div>
            </div>

        </div>
    </div>
</div>

<div id="reStartPromotionsConfirm" class="reStartPromotionsConfirm" style="display:none">
    <table id="reStartPromotionsTable">
        <tr><td colspan="2"><div>请重新选择促销活动时间</div></td> </tr>
        <tr> <td colspan="2">
        <div style="margin: 5px">开始时间<input name="startTimeStr" type="text" class="time_input txt"  style="width:120px;padding-left: 5px" /></div>
        <div style="margin: 5px">
            结束时间
        <label class="rad"><input type="radio" name="date_select" class="date_select_week date_select" />7天</label>
        <label class="rad"><input type="radio" name="date_select" class="date_select_month date_select"/>30天</label>
        <label class="rad"><input type="radio" name="date_select" class="date_select_three_month date_select"/>90天</label>
        <label class="rad"><input type="radio" name="date_select" class="date_select_unlimited date_select"/>不限时</label>
            <div style="padding-left: 55px">
                <input type="radio" name="date_select" checked="true" class="date_select_define date_select" id="restartCustomDateRadio"/><label for="restartCustomDateRadio">自定义时间</label>
                <input name="endTimeStr" type="text" class="time_input txt" style="width:120px;" />
        </div>
    </div>
            </td>
        </tr>
    </table>
</div>

<div id="mask" style="display:block;position: absolute;"></div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>