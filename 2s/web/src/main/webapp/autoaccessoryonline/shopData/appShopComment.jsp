<%--
  Created by IntelliJ IDEA.
  User: mayan
  Date: 14-1-16
  Time: 上午10:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>评价中心</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>

    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/shopData<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
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

            $(".hover").hover(function(event){
                var _currentTarget=$(event.target).parent().find(".alert");
                _currentTarget.show();

                //因为有2px的空隙,所以绑定在parent上.
                _currentTarget.parent().mouseleave(function(event){
                    event.stopImmediatePropagation();

                    if($(event.relatedTarget).find(".alert")[0]!=_currentTarget[0]) {
                        _currentTarget.hide();
                    }
                });

            },function(event){
                var _currentTarget=$(event.target).parent().find(".alert");

                if($(event.relatedTarget).find(".alert")[0]!=_currentTarget[0]) {
                    $(event.target).parent().find(".alert").hide();
                }

            });


            $(".icon").hover(function(event){
                var _currentTarget=$(event.target).parent().find(".alert");
                _currentTarget.show();

                //因为有2px的空隙,所以绑定在parent上.
                _currentTarget.parent().mouseleave(function(event){
                    event.stopImmediatePropagation();

                    if($(event.relatedTarget).find(".alert")[0]!=_currentTarget[0]) {
                        _currentTarget.hide();
                    }
                });

            },function(event){
                var _currentTarget=$(event.target).parent().find(".alert");

                if($(event.relatedTarget).find(".alert")[0]!=_currentTarget[0]) {
                    $(event.target).parent().find(".alert").hide();
                }

            });

        })
    </script>
    <%--在此处对样式进行微调，对公用css不做改变，以防影响其他页面效果--%>
    <style type="text/css">
        .height{
            height: 10px;
        }
        .gray-radius{
            padding: 11px;
        }
        .tip .tipBody{
            color: #4C4C4C;
        }
        a{
            color: #000000;
        }
        a:hover{
            cursor: pointer;
        }
        .cuSearch .tab_cuSearch tr.titBody_Bg td{
            color: #000000;
            cursor:auto;
        }
        .gray_color{
            color: #999999;
        }
        .shopevaluation b{
            font-size: 14px;
        }
        .shopevaluation{
            margin: 0px 0 0;
        }

    </style>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>

<div class="i_main clear">
    <div class="mainTitles">
        <div class="titleWords">评价中心</div>
    </div>
    <div class="clear"></div>
    <div id="recordTitle"></div>
    <div class="clear height"></div>
    <input type="hidden" id="paramShopId" name="paramShopId" value="${paramShopId}"/>
    <div class="cuSearch titBody ">
        <div class="gray-radius" style="margin:0;">
            <div class="divTit" style="float:none"> <span class="spanName">日期：</span>&nbsp;
                <a class="btnList" id="my_date_yesterday" name="my_date_select">昨天</a>&nbsp;
                <a class="btnList" id="my_date_today" name="my_date_select">今天</a>&nbsp;
                <a class="btnList" id="my_date_thismonth" name="my_date_select">本月</a>&nbsp;
                <a class="btnList" id="my_date_thisyear" name="my_date_select">今年</a>&nbsp;
                <input id="startDate" type="text" readonly="readonly" name="startTimeStr" class="my_startdatetime txt"/>&nbsp;至&nbsp;
                <input id="endDate" type="text" readonly="readonly" name="endTimeStr" class='my_enddatetime txt'/>&nbsp;&nbsp;
            </div>
            <div class="clear"></div>
            <div class="divTit" style="float:none"> <span class="spanName">评价类型：</span>
                <div class="evaluationList" name="evaluationList">
                    &nbsp;<label class="rad" style="margin-left:6px;"><input type="checkbox" name="evaluation" id="badComment" value="1,2"/>差评</span>（1-2分）</label>&nbsp;
                    <label class="rad"><input type="checkbox" name="evaluation" id="mediumComment" value="3"/>中评（3分）</label>&nbsp;
                    <label class="rad"><input type="checkbox" name="evaluation" id="goodComment" value="4,5"/>好评（4-5分）</label>

                </div>
            </div>
            <div class="clear"></div>

            <div class="divTit" style="float:none"> <span class="spanName">单据类型：</span>
                <div class="orderTypeList" name="orderTypeList">
                    &nbsp;<label class="rad" style="margin-left:6px;"><input type="checkbox" name="orderType" id="repair" value="REPAIR"/>施工单</label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <label class="rad"><input type="checkbox" name="orderType" id="washBeauty" value="WASH_BEAUTY"/> 洗车美容</label>
                </div>
            </div>
            <div class="divTit" style="float:none">
                <span class="spanName">客户：</span>
                &nbsp;
                <input type="text" class="txt" style="width:195px;color: #ADADAD;"
                       id="customerInfoText"
                       pagetype="appCustomerData"
                       initialValue="手机号/车牌号/客户名" value="手机号/车牌号/客户名"/>
                &nbsp;单据号：
                    <input id="receiptNo" type="text" class="txt" value="单据号" style="color: #ADADAD;"/>
            </div>

            <div class="clear"></div>

            <div class="clear height"></div>
            <div class="divTit button_conditon button_search">
                <a class="blue_color clean" id="cleanCondition">清空条件</a>
                <a class="button" id="searchCommentData">查 询</a>
            </div>

            <div class="left" style="color:#333;" id="evaluationResult">
               <%-- <p><strong>共有6条记录：</strong>
                    <span><a>好评（<b class="yellow_color">5</b>）</a>&nbsp;</span>&nbsp;&nbsp;
                    <span><a>中评（<b class="yellow_color">5</b>）</a>&nbsp;</span>&nbsp;&nbsp;
                    <span><a>差评（<b class="yellow_color">5</b>）</a>&nbsp;</span></p>--%>
            </div>
            <input type="hidden" id="commentTimeStart" value="">
            <input type="hidden" id="commentTimeEnd" value="">
            <input type="hidden" id="commentScore" value="">
            <input type="hidden" id="addGoodCommentScore" value="">
            <input type="hidden" id="addMediumCommentScore" value="">
            <input type="hidden" id="addBadCommentScore" value="">
            <input type="hidden" id="orderType" value="">
            <input type="hidden" id="customer" value="">

            <div class="clear height"></div>
            <div class="shopevaluation" style="display:none;color:#999999;" id="noComment">暂无评价！</div>
            <table id="appUserCommentTable" class="tab_cuSearch" cellpadding="0" cellspacing="0" style="width:980px;">
                <colgroup>
                    <col width="100" />
                    <col width="80" />
                    <col width="100" />
                    <col width="110" />
                    <col width="70" />
                    <col width="300" />
                    <col width="100" />
                </colgroup>
                <%--鼠标移动到评价那一栏时，显示全部信息，此时因为分为2行显示，所以此功能去除
                --%>
                <%--<div class="tip" style="top:350px; left:530px;">
                    <div class="tipTop"></div>
                    <div class="tipBody">
                        感谢您的评价，我们会继续努力!
                        【江苏米其林轮胎公司】</div>
                    <div class="tipBottom"></div>
                </div>--%>
                <tr class="titleBg">
                    <td>单据号</td>
                    <td >单据类型</td>
                    <td>车牌号</td>
                    <td>客户</td>
                    <td>评分</td>
                    <td>详细评价</td>
                    <td>评价时间</td>
                </tr>

            </table>
            <div class="i_height"></div>
            <div class="i_pageBtn" id="pageRecord" style="float:right;margin: 10px 0 10px 0">
                <bcgogo:ajaxPaging url="supplier.do?method=getAppCommentRecordByKeyword" postFn="searchAppUserCommentRecord"
                                   dynamical="appShopComment" data="{startPageNo:'1',maxRows:15,paramShopId:$('#paramShopId').val()}"/>
            </div>
        </div>
        <div class="cartBottom"></div>
    </div>
</div>

        <div class="clear i_height"></div>
<!----------------------------页脚----------------------------------->
<%@include file="/WEB-INF/views/footer_html.jsp" %>
    </div>
</div>
<div id="mask"  style="display:block;position: absolute;"> </div>

</body>
</html>