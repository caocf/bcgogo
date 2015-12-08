<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
@description web 登陆页面， 现在不做用户名、密码cookie自定义存储， 使用默认浏览器存储功能
--%>
<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.springframework.security.core.AuthenticationException" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>

    <script type="text/javascript">
        var sampling = Math.random() < 0.001;
        var page_begintime = (+new Date());

        (sampling) && ((new Image()).src = "http://isdspeed.qq.com/cgi-bin/r.cgi?flag1=7839&flag2=7&flag3=8&15=1000&r=" + Math.random());
    </script>

    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=0" />
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no">

    <title>消息详细</title>
   <link rel="stylesheet" type="text/css" href="styles/wx_article_detail<%=ConfigController.getBuildVersion()%>.css"/>

</head>
<body id="activity-detail" class="zh_CN ">

<script type="text/javascript">
    var write_sceen_time = (+new Date());
    (sampling) && ((new Image()).src = "http://isdspeed.qq.com/cgi-bin/r.cgi?flag1=7839&flag2=7&flag3=8&16=1000&r=" + Math.random());
</script>
<div class="rich_media">
    <div class="rich_media_inner">

        <h2 class="rich_media_title" id="activity-name">
            ${article.title}
        </h2>

        <div class="rich_media_meta_list">
            <em id="post-date" class="rich_media_meta text">${article.sendTimeStr}</em>
            <em class="rich_media_meta text">${shopName}</em>
            <a class="rich_media_meta link nickname" href="javascript:void(0);" id="post-user">${publicName}</a>
        </div>

        <div id="page-content">
            <div id="img-content">


                <div class="rich_media_thumb" id="media">
                    <img onerror="this.parentNode.removeChild(this)" src="${article.picUrl}" />
                </div>

                <div class="rich_media_content" id="js_content">${article.description}</div>

                <div class="rich_media_tool" id="js_toobar">

                    <div id="js_read_area" class="media_tool_meta link_primary meta_primary" >阅读 <span id="readNum"></span></div>

                    <a  class="media_tool_meta meta_primary link_primary meta_praise" href="javascript:void(0);" id="like">
                        <i class="icon_praise_gray"></i><span class="praise_num" id="likeNum"></span>
                    </a>

                    <a id="js_report_article" class="media_tool_meta link_primary meta_extra" href="javascript:void(0);">举报</a>
                </div>
            </div>

        </div>
    </div>
</div>

</body>
</html>
