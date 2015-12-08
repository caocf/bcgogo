<%@ page import="com.bcgogo.config.ConfigController" %>
<script type="text/javascript" src="js/messagePrompt<%=ConfigController.getBuildVersion()%>.js"></script>
<style type="text/css">
    body {
        background: none repeat scroll 0 0 transparent;
    }
</style>

<script type="text/javascript">
    var simpleJsAlertTitle = "${util_simpleJsAlertMessage.title}";
    var simpleJsAlertMsg = "${util_simpleJsAlertMessage.msg}";
    $(function(){
        if(!GLOBAL.Lang.isEmpty(simpleJsAlertTitle) || !GLOBAL.Lang.isEmpty(simpleJsAlertMsg)){
            nsDialog.jAlert(simpleJsAlertMsg, simpleJsAlertTitle, null, true);
        }
    });
</script>
<div class="tab_repay" style="position:fixed; display:none;z-index:5;float: left;width: 300px;" id="messageShowPrompt">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>

    <div class="i_upCenter">
    </div>

    <div class="i_upRight"></div>
    <div class="i_upBody">
        <div class="boxContent" id="promptContent" style="color:#000;font-weight:400;text-align: center;">
        </div>
    </div>
    <div class="i_upBottom">
        <div class="i_upBottomLeft"></div>
        <div class="i_upBottomCenter"></div>
        <div class="i_upBottomRight"></div>
    </div>
</div>