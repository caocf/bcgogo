<%@ page import="javax.imageio.ImageIO" %>
<%@ page import="java.awt.image.BufferedImage" %>
<%@ page import="java.awt.*" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<%--
  Created by IntelliJ IDEA.
  User: Jimuchen
  Date: 13-10-16
  Time: 上午10:13
  To change this template use File | Settings | File Templates.
--%>





<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <link rel="stylesheet" type="text/css" href="styles/code.css"/>
    <style type="text/css">
        *{ font-size: 12px; line-height: 20px;}
        .suitForImg{
            margin:-40px;
        }

    </style>
      <script type="text/javascript">
          window.onload = function(){
              window.print();
          }
      </script>
</head>
<%--<body>

<img src="shopData.do?method=getShopRqImage&size=300" /> //本店二维码
</body>--%>

<body>
<div class="codeContent">
    <div class="title"></div>
    <div class="code-img">
        <img class="suitForImg" src="shopData.do?method=getShopRqImage&size=280" />
        <div>【扫一扫，获取本店名称】</div>
        <div class="clear"></div>
    </div>
    <div class="codeTxt">
        <h1>操作说明：</h1>
        <table width="100%" border="0">
            <tr>
                <td><strong>第一步，</strong>打开手机APP【行车一键通】，系统设置~车辆管理~<br />
                    新增/修改车辆；</td>
            </tr>
            <tr>
                <td><strong>第二步，</strong>点击【扫描店铺】，扫描店铺二维码，显示店铺名称。</td>
            </tr>
        </table>
        <div class="clear"></div>
    </div>
    <div class="clear"></div>
    <div class="code-img">

        <bcgogo:permissionParam permissions="WEB.VERSION.FOUR_S_VERSION_BASE">
          <c:if test="${!WEB_VERSION_FOUR_S_VERSION_BASE}">
            <img src="images/rq/rq_down_app_200x200.png"/>

          </c:if>
          <c:if test="${WEB_VERSION_FOUR_S_VERSION_BASE}">
            <img src="images/gsm_app_down/gsm_app_200x200.png"/>
          </c:if>
        </bcgogo:permissionParam>

        <div>【行车一键通】</div>
        <div class="clear"></div>
    </div>
    <div class="codeTxt">
        <h1>操作说明：</h1>
        <table width="100%" border="0">
            <tr>
                <td><strong>方式一：</strong>在店铺扫描APP二维码，打开链接地址直接进行安装。 </td>
            </tr>
            <tr>
                <td> <strong>方式二：</strong>Andriod操作系统手机，下载360手机助手，搜索【行车一键通】，下载并安装。 </td>
            </tr>
           <%-- <tr>
                <td><strong>方式三：</strong>IOS操作系统手机，保持手机GPRS或者WIFI连接状态，点击手机上的APP STORE
                    ，进入程序下载界面，搜索【行车一键通】，即可下载程序，或通过数据线连接
                    手机和电脑，打开电脑itunes，进入app store，然后下载软件，下载完成后，
                    点击同步，即可安装程序至手机； </td>
            </tr>--%>
        </table>
        <div class="clear"></div>
    </div>
    <div class="clear"></div>
</div>
</body>

</html>