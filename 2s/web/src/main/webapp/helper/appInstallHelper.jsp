<%--
  Created by IntelliJ IDEA.
  User: Sally_Ma
  Date: 14-2-18
  Time: 下午5:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.bcgogo.common.WebUtil" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>帮助公告</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/help<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/returnsStorage<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/extension/swfobject/swfobject.min.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-bcPlayer<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-busSwfLoader<%=ConfigController.getBuildVersion()%>.js"></script>

</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
    <div class="mainTitles">
        <div class="titleWords">帮助中心</div>
    </div>
    <div class="i_mainRight" id="i_mainRight">
        <jsp:include page="helperNav.jsp">
            <jsp:param name="currPage" value="appInstallHelper"/>
        </jsp:include>
        <input type="hidden" id="page_param" value="${page_param}" />
        <div class="right">
            <div class="request"> <a class="btnDis">手机APP安装说明</a>
                <div class="top"></div>
                <div class="body description">
                    <h3>一、下载安装手机端途径</h3>
                    <div>下载手机端的途径有两种：<br />
                        1.手机扫描二维码，用手机上的二维码扫描软件拍摄二维码； <br />
                        <div> 2.从PC端下载程序APK，将得到一个后缀为&ldquo;.apk&rdquo;的安装文件（不同下载方式下载获得的文件名可能不同）；<br />
                            &nbsp;&nbsp;&nbsp;&nbsp;安装方法一：通过USB数据线连接手机或使用读卡器将您的手机存储卡连接至电脑，把下载得到的安装包复制到手机存储卡根目录，在复制过程中电脑如果提示&ldquo;文件已存在，是否覆盖？&rdquo;，请选择&ldquo;是&rdquo;，断开USB连接或将手机存储卡放回手机，通过手机文件浏览器或资源管理器找到刚刚导入的安装文件，并点击开始安装；<br />
                            &nbsp;&nbsp;&nbsp;&nbsp;安装方法二：使用USB数据线连接手机与电脑后，打开使用手机助手类软件（360手机助手、腾讯应用宝等），成功连接后，安装导入刚刚下载的APK安装包进行安装。<br />
                        </div>
                    </div>
                    <div class="w_code"> <a><img src="images/s_code_01.jpg" /></a> <a><img src="images/s_code_02.jpg" /></a> </div>
                    <h3>二、功能说明</h3>
                    <div class="function">
                        <div class="img"><img src="images/az_app.jpg" width="207" /></div>
                        <div class="function_txt">轻松定位商家</div>
                    </div>
                    <div class="to_right"></div>
                    <div class="function">
                        <div class="img"><img src="images/az_app3.jpg"width="207"  /></div>
                        <div class="function_txt">一键预约服务</div>
                    </div>
                    <div class="to_right"></div>
                    <div class="function">
                        <div class="img"><img src="images/az_app2.jpg" width="207" /></div>
                        <div class="function_txt">实时账单查询</div>
                    </div>
                </div>
                <div class="bottom"></div>
            </div>
        </div>
    </div>
</div>

<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>