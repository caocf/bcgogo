<%--
  Created by IntelliJ IDEA.
  User: jinyuan
  Date: 13-6-13
  Time: 上午10:13
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script type="text/javascript">
    function closeService() {
        $(".QQ_chat").fadeOut(2000);
    }
</script>
<div class="tip QQ_chat" style="margin-left:8px;display:none;">
    <div class="tipTop"></div>
    <div class="tipBody">
        <a class="iconClose" onclick="closeService()"></a>
        <a class="icon_QQchat"></a>
        <span class="tel_name">客服电话:</span>
        <div class="tel_list">
            <span>0512-66733331</span>

        </div>
    </div>
    <div class="tipBottom"></div>
</div>