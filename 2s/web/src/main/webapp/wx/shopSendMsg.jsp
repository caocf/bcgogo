<%@ include file="/WEB-INF/views/includes.jsp" %>
<%
    //会员个性化
    boolean isMemberSwitchOn = WebUtil.checkMemberStatus(request);
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>发送微信</title>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/message<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" href="js/extension/jquery/plugin/sliptree-tokenfield/bootstrap-tokenfield.css"/>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.10.2.js"></script>
    <script>
        var _$10 = jQuery.noConflict(true);
    </script>

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript"
            src="js/extension/jquery/plugin/sliptree-tokenfield/bootstrap-tokenfield-v0.12.0-bg.js"></script>
    <script type="text/javascript" src="js/wx/shopSendMsg<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        $(function () {
            var wxUsers = $.parseJSON('${wxUsers}');
            if (!G.isEmpty(wxUsers)) {
                for (var i = 0; i < wxUsers.length; i++) {
                    var wxUser = wxUsers[i];
                    var nickName = G.isEmpty(wxUser.remark) ? wxUser.nickname : wxUser.remark;
                    var openId = G.Lang.normalize(wxUser.openid);
                    _$10(".J_selected_fans").tokenfield('createToken', {value: openId, label: nickName});
                }
            }
        });
    </script>

</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>


<div class="i_main clear">
    <div class="mainTitles">
        <div class="titleWords">微信管理</div>
    </div>
    <div class="messageContent">

        <jsp:include page="wxNavi.jsp">
            <jsp:param name="currPage" value="wxWrite"/>
        </jsp:include>

        <div class="messageRight">
            <div class="messageRight_radius">
                <form class="J_wx_msg_form" action="weChat.do?method=saveArticle" method="post"
                      enctype="multipart/form-data" autocomplete="off">

                    <input type="hidden" class="J_wxReceiverGroupType" name="wxReceiverGroupType">
                    <input type="hidden" class="J_wx_article_picUrl" name="picUrl" autocomplete="off">
                    <input type="file" name="imgFileTemp" class="J_wxImgFile" style="display: none" autocomplete="off"/>

                    <div class="content_01">
                        <div class="mes_content">
                            <div class="wechat_line">
                                <div class="wechat_t_left"><a class=" blue_color J_to_select_template">选择模板</a></div>
                                <%--<div class="status">发送状态：<span class="red_color"></span></div>--%>
                                <div class="clear"></div>
                            </div>
                            <div class="wechat_line">
                                <div class="wechat_t_left">发送方式</div>
                                <div class="wechat_t_right">
                                    <a class="a_select J_send">正式发送</a>
                                    <a class="a_select J_test_send">测试/演示</a></div>
                                <div class="clear"></div>
                            </div>
                            <div class="wechat_line">
                                <div class="wechat_t_left">已选粉丝</div>
                                <div class="wechat_t_right" style="width: 428px">
                                    <input name="" type="text" class="J_selected_fans" autocomplete="off"
                                           style="width: 100%;"/>
                                </div>
                                <div class="clear"></div>
                            </div>
                            <div class="wechat_line">
                                <div class="wechat_t_left">标题</div>
                                <div class="wechat_t_right">
                                    <div class="input_boder J_wx_title_container">
                                        <input class="J_wx_title" name="title" type="text" autocomplete="off"
                                               maxlength="50"/>

                                        <div class="clear"></div>
                                    </div>
                                </div>
                                <div class="clear"></div>
                            </div>
                            <div class="wechat_line">
                                <div class="wechat_t_right" style="margin-left: 55px;line-height:4px">
                                    (图片格式必须是 jpg,pnd,jpeg,图片大小控制在2M以内)
                                    <div class="clear"></div>
                                </div>
                                <div class="clear"></div>
                            </div>
                            <%--<div class="wechat_line">--%>
                            <%--<div class="wechat_t_left">图片</div>--%>
                            <%--<div class="wechat_t_right">--%>
                            <%--图片格式必须是 jpg,pnd,jpeg,最大为2M--%>
                            <%--</div>--%>
                            <%--</div>--%>

                            <div class="wechat_line">

                                <div class="wechat_t_left">图片</div>
                                <div class="wechat_t_right">
                                    <p>

                                    <div class="choose_picture J_img_select J_img_name"></div>
                                    <a class="grayBtn_64 balance_left8 J_img_select">选择图片</a> </p>
                                    <div class="clear"></div>
                                    <p>

                                    <div class="larger_img">
                                        <img class="J_wx_article_img_show" width="400"/>
                                    </div>
                                    </p>
                                </div>
                                <div class="clear"></div>
                            </div>
                            <div class="wechat_line">
                                <div class="wechat_t_left">描述</div>
                                <div class="wechat_t_right">
                                    <div class="mes_textarea" style=" height:148px;">
                                        <textarea id="w_description" name="description" maxlength="500"
                                                  autocomplete="off" class="J_wx_description"
                                                  style="height:120px;"></textarea>

                                        <div class="bottom">
                                            已输入<strong id="contentLength" class="red_color">0</strong>个字/剩余<span
                                                id="leftLength" class="orange_color">500</span>字
                                            <a id="clearBtn" class="blue_color" style="float:right">清空内容</a>
                                        </div>
                                    </div>
                                </div>
                                <div class="clear"></div>
                            </div>
                            <div class="mes_send"><a class="blueBtn_64 balance_left8 J_wx_msg_submit">发送</a></div>
                            <div class="clear height"></div>
                            <div class="clear"></div>
                        </div>
                        <div style="margin-top: 30px;margin-left:30px;">
                            <span>说明：正式发送，提交审核后，经审核通过后正式发送 </span><br/>
                            <span style="margin-left: 35px;">测试发送接收的用户应48小时内操作过(点击菜单或发送消息)公共号,仅作为测试用途。</span>
                        </div>
                        <div class="clear"></div>
                    </div>
                </form>

                <div class="content_02">
                    <div class="">
                        <div class="contact_body">
                            <input type="text" class="search-scope" placeholder="备注名/昵称" id="serachWord" autocomplete="off">
                            <input id="searchBtn" type="button" class="search-btn" value="查找" style="cursor: pointer;float: right">

                            <div class="clear"></div>
                        </div>
                        <div class="contact_title2" style="padding-left:10px">我的粉丝(<span
                                class="J_total_fans_num">0</span>)
                            <a class="J_all_fans opr-btn" style="padding-right:10px">添加全部</a></div>
                        <div class="clear height"></div>
                        <div class="clear"></div>
                        <div class="personal_list contact_body">
                            <ul class="j_wxUserContainer">
                            </ul>
                        </div>
                        <div class="height"></div>
                        <div style="float: right;">
                            <bcgogo:ajaxPagingUpAndDown url="weChat.do?method=getShopWXUsers" dynamical="_wxUser"
                                                        postFn="drawWXUserList"
                                                        data="{\\\"currentPage\\\":1,\\\"pageSize\\\":10}"/>
                        </div>
                        <div class="clear"></div>
                    </div>
                </div>
                <div class="clear"></div>
            </div>
        </div>


    </div>
</div>

<div class="alertMain J_wx_template_container" style="display: none">
    <div class="height"></div>
    <table cellpadding="0" cellspacing="0" class="tabRecord J_wx_template_tb">
        <col width="50">
        <col width="120">
        <col>
        <col width="60">
        <tr class="tabTitle">
            <td style="padding-left:10px;">序号</td>
            <td>模板名称</td>
            <td>微信内容</td>
            <td>操作</td>
        </tr>
    </table>
    <div class="height"></div>
    <div class="height"></div>
    <div class="i_pageBtn" style="float:right">
        <bcgogo:ajaxPaging
                url="weChat.do?method=getWXMsgTemplate"
                postFn="drawWXArticleTemplate"
                display="none"
                dynamical="_wxArticleTemplate"/>
    </div>
</div>


<%@include file="/WEB-INF/views/footer_html.jsp" %>

<div id="wxUserEditor" class="wx-user-editor" style="display: none;padding-left:50px">
    <div class="height"></div>
    <div class="d-user-info">
        <div class="user-info-left">
            <img id="headImg" height="80" width="80"
                 src="http://wx.qlogo.cn/mmopen/J6BwNibiapBetFv8eVJFib2fvzOA0IrptiaicVTT1KG20gLHXNNyfz75icCuIdiazYBNshrSNssNsIwaPV3Q8gBJClkzbAMzrq3wCpN/0">
        </div>
        <div class="user-info-right">
            <div>
                昵称:<span id="nickName"></span>
            </div>
            <div>
                地区:<span id="city"></span>
            </div>
        </div>
    </div>
    <div>
        备注名:<input id="remark" class="i-input" placeholder="请输入用户备注名" maxlength="10"/>
    </div>
</div>

</body>

</html>