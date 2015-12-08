<%--
  Created by IntelliJ IDEA.
  User: XinyuQiu
  Date: 14-9-24
  Time: 下午8:43
  To change this template use File | Settings | File Templates.
--%>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/message<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
    <%--<link rel="stylesheet" type="text/css" href="styles/zTreeStyle<%=ConfigController.getBuildVersion()%>.css"/>--%>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.10.2.js"></script>
    <script>
        var _$10 = jQuery.noConflict(true);
    </script>

    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <%--<link href="js/extension/jquery/plugin/sliptree-tokenfield/tokenfield-typeahead.css" type="text/css" rel="stylesheet">--%>
    <link href="js/extension/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="js/extension/jquery/plugin/sliptree-tokenfield/bootstrap-tokenfield.css"/>
    <%--<link rel="stylesheet" type="text/css" href="styles/message<%=ConfigController.getBuildVersion()%>.css"/>--%>
    <%--<link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>--%>
    <%--<link href="js/extension/jquery/1.10.3/jquery-ui-1.10.3.custom.min.css" type="text/css" rel="stylesheet">--%>
    <%--<style type="text/css">--%>
        <%--.smsContentFocus {--%>
            <%--border-color: #66afe9;--%>
            <%--outline: 0;--%>
            <%---webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075), 0 0 8px rgba(102, 175, 233, 0.6);--%>
            <%--box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075), 0 0 8px rgba(102, 175, 233, 0.6);--%>
        <%--}--%>
    <%--</style>--%>

    <%--<script type="text/javascript" src="js/extension/jquery/1.10.3/jquery-ui.js"></script>--%>
    <%--<script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <%--<script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <%--<script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <%--<script type="text/javascript" src="js/module/bcgogo-paging<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <%--<script type="text/javascript" src="js/extension/jquery/1.10.3/jquery-ui-timepicker-addon.js"></script>--%>
    <%--<script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.min.js"></script>--%>
    <script type="text/javascript" src="js/extension/jquery/plugin/sliptree-tokenfield/bootstrap-tokenfield-v0.12.0-bg.js"></script>
    <%--<script type="text/javascript" src="js/extension/jquery/plugin/sliptree-tokenfield/typeahead.min.js"></script>--%>
    <%--<script type="text/javascript" src="js/extension/jquery/plugin/sliptree-tokenfield/scrollspy.js"></script>--%>


    <%--<script type="text/javascript" src="js/extension/jquery/plugin/jquery.ztree.core-3.5.min.js"></script>--%>
    <%--<script type="text/javascript" src="js/customer<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <%--<script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <%--<script type="text/javascript" src="js/txnbase<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <%--<script type="text/javascript" src="js/components/ui/bcgogo-customerSms-input<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <%--<script type="text/javascript" src="js/components/ui/bcgogo-iframe-post<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <%--<script type="text/javascript" src="js/smsWrite<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <%--<script type="text/javascript" src="js/smsWriteUtil<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <%--<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <script type="text/javascript" src="js/wx/wxSendMessage<%=ConfigController.getBuildVersion()%>.js"></script>

</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<div style="color: black;">
    <a id="selectWxTemplate" class="J_to_select_template">选择模板  </a>
    <form class="J_wx_msg_form" action="weChat.do?method=saveArticle" method="post" enctype="multipart/form-data" autocomplete="off">


    <table style="color: black;">
        <tr>
            <td style="color: black;">收件人</td>
            <td><input class="J_all_fans a-click" type="button" value="全部粉丝">
                <input class="J_part_fans a-click" type="button" value="部分粉丝">
                <input class="J_test_fans" type="button" value="测试/演示">
                <input type="text" class="J_wxReceiverGroupType" name="wxReceiverGroupType">
                </td>
        </tr>
        <tr>
            <td style="color: black;">已选粉丝</td>
            <td>
                <input type="text" id="wxReceiver" autocomplete="off">
            </td>
        </tr>
        <tr>
            <td>标题</td>
            <td><input type="text" class="J_wx_title" id="wxTitle" name="title" autocomplete="off"></td>
        </tr>
        <tr>
            <td>图片</td>
            <td>
                <input type="file" class="J_wxImgFile" name="imgFile" id="wxImg"  autocomplete="off">
                <%--<input type="file" class="J_wxImgFile_lastFile"  autocomplete="off">--%>
                <input type="text" class="J_wx_article_picUrl" name="picUrl">
            </td>
        </tr>
        <tr>
            <td>图片</td>
            <td>
                <div>
                    <img width="100" class="J_wx_article_img_show">
                </div>
            </td>
        </tr>
        <tr>
            <td>描述</td>
            <td><input type="text" class="J_wx_description" id="wxDescrip" name="description"></td>
        </tr>
        <tr>
            <td>id</td>
            <td><input type="text" class="J_wx_template_id" name="wxArticleTemplateId" ></td>
        </tr>
        <tr>
            <td>URL</td>
            <td><input type="text" class="J_wx_template_URL" name="url" ></td>
        </tr>
    </table>
    </form>
    <input type="button" class="J_wx_msg_submit" value="提交审核">
</div>

<div>
    <div style="float: right">
    <ul class="j_wxUserContainer">

    </ul>

        <bcgogo:ajaxPagingUpAndDown url="weChat.do?method=getShopWXUsers" dynamical="_wxUser"
                                    postFn="drawWXUserList" data="{\\\"currentPage\\\":1,\\\"pageSize\\\":10}"/>
    </div>
</div>


<div  class="alertMain J_wx_template_container" style="display: none">
    <div class="height"></div>
    <table class="tabRecord template-table J_wx_template_tb">
        <col width="120">
        <col>
        <tr class="tabTitle">
            <td style="padding-left:10px;">模板名称</td>
            <td>短信内容</td>
        </tr>


    </table>
    <div class="height"></div>
    <div class="i_pageBtn" style="float:right">
        <bcgogo:ajaxPaging
                url="weChat.do?method=getWXMsgTemplate"
                postFn="drawWXArticleTemplate"
                display="none"
                dynamical="_wxArticleTemplate"/>
    </div>
</div>

</body>
</html>
