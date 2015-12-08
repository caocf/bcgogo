<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page import="com.bcgogo.config.util.ConfigUtils" %>
<%@ page import="com.bcgogo.enums.config.ImageScene" %>
<script type="text/javascript">
    APP_BCGOGO.UpYun = {
        UP_YUN_UPLOAD_DOMAIN_URL:"<%=ConfigUtils.getUpYunUploadDomainUrl()%>",
        UP_YUN_BUCKET:"<%=ConfigUtils.getUpYunBucket()%>",
        UP_YUN_DOMAIN_URL:"<%=ConfigUtils.getUpYunDomainUrl()%>",
        UP_YUN_SEPARATOR:"<%=ConfigUtils.getUpYunSeparator()%>"
    };
    APP_BCGOGO.ImageScene = {
        IMAGE_AUTO:"<%=ImageScene.valueOf("IMAGE_AUTO").getImageVersion()%>",
        PRODUCT_LIST_IMAGE_SMALL:"<%=ImageScene.valueOf("PRODUCT_LIST_IMAGE_SMALL").getImageVersion()%>",
        PRODUCT_INFO_IMAGE_SMALL:"<%=ImageScene.valueOf("PRODUCT_INFO_IMAGE_SMALL").getImageVersion()%>",
        PRODUCT_INFO_IMAGE_BIG:"<%=ImageScene.valueOf("PRODUCT_INFO_IMAGE_BIG").getImageVersion()%>",
        PRODUCT_INFO_DESCRIPTION_IMAGE:"<%=ImageScene.valueOf("PRODUCT_INFO_DESCRIPTION_IMAGE").getImageVersion()%>",
        PRODUCT_RECOMMEND_LIST_IMAGE_SMALL:"<%=ImageScene.valueOf("PRODUCT_RECOMMEND_LIST_IMAGE_SMALL").getImageVersion()%>",
        PRE_BUY_PRODUCT_INFO_IMAGE_SMALL:"<%=ImageScene.valueOf("PRE_BUY_PRODUCT_INFO_IMAGE_SMALL").getImageVersion()%>",
        SHOP_BUSINESS_LICENSE_IMAGE:"<%=ImageScene.valueOf("SHOP_BUSINESS_LICENSE_IMAGE").getImageVersion()%>",
        SHOP_IMAGE:"<%=ImageScene.valueOf("SHOP_IMAGE").getImageVersion()%>",
        SHOP_IMAGE_BIG:"<%=ImageScene.valueOf("SHOP_IMAGE_BIG").getImageVersion()%>",
        SHOP_IMAGE_SMALL:"<%=ImageScene.valueOf("SHOP_IMAGE_SMALL").getImageVersion()%>",
        SHOP_REGISTER:"<%=ImageScene.valueOf("SHOP_REGISTER").getImageVersion()%>",
        SHOP_MANAGE_UPLOAD_IMAGE:"<%=ImageScene.valueOf("SHOP_MANAGE_UPLOAD_IMAGE").getImageVersion()%>",
        CUSTOMER_IDENTIFICATION_IMAGE_UPLOAD:"<%=ImageScene.valueOf("CUSTOMER_IDENTIFICATION_IMAGE_UPLOAD").getImageVersion()%>",
        CUSTOMER_IDENTIFICATION_IMAGE:"<%=ImageScene.valueOf("CUSTOMER_IDENTIFICATION_IMAGE").getImageVersion()%>"
    };
//    console.log(APP_BCGOGO.ImageScene);


    function saveUpLoadImageErrorInfo(data){
        APP_BCGOGO.Net.asyncPost({"url":"upYun.do?method=saveImageErrorLog", "data":data});
    }
</script>