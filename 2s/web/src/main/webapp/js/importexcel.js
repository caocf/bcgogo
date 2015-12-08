/**
 * 导入数据页面专用js
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-4-11
 * Time: 上午9:44
 * To change this template use File | Settings | File Templates.
 */
function map() {
    var struct = function(key, value) {
        this.key = key;
        this.value = value;
    }

    var put = function(key, value) {
            for(var i = 0; i < this.arr.length; i++) {
                if(this.arr[i].key === key) {
                this.arr[i].value = value;
                return;
            }
        }
        this.arr[this.arr.length] = new struct(key, value);
    }

    var get = function(key) {
            for(var i = 0; i < this.arr.length; i++) {
                if(this.arr[i].key === key) {
                return this.arr[i].value;
            }
        }
        return null;
    }

    var remove = function(key) {
        var v;
            for(var i = 0; i < this.arr.length; i++) {
            v = this.arr.pop();
                if(v.key === key) {
                break;
            }
            this.arr.unshift(v);
        }
    }

    var size = function() {
        return this.arr.length;
    }

    var isEmpty = function() {
        return this.arr.length <= 0;
    }

    var clearMap = function() {
        this.arr = [];
    }
    this.arr = new Array();
    this.get = get;
    this.put = put;
    this.remove = remove;
    this.size = size;
    this.isEmpty = isEmpty;
    this.clearMap = clearMap;
}
var jsonStrMap = new map();
/**
 * 绑定事件
 */
$(document).ready(

    function(){
        //清除页面出事值，防止缓存
        cleanImportConfig();

        $("#importType").val("customer");

        //点击上传按钮
        $("#upFileBtn").click(function(){
            var fileName = $("#selectfile").val();
            if(fileName == undefined || fileName == "" || (fileName.search(/(.*xls$)|(.*xlsx$)/g) === -1)){
                nsDialog.jAlert("请选择EXCEL文件！");
                return ;
            }
            $("#uploading").css("display", "block");
            ajaxFileUpload();
            $("#uploading").css("display", "none");
        });

        $("#input1,#selectfileBtn").bind("click",function(){
            if($.browser.msie) {
                var noticeInfo = "" +
                    "<div style='line-height:26px;'>" +
                    "    请使用 火狐浏览器 进行文件上传。 <br>如果您电脑未安装火狐浏览器，请联系客服。<br>" +
                    "    客服电话：" +
                    "    <p style='padding-left:30px;'>0512-66733331</p>" +
                    "    客服QQ：" +
                    "    <p style='padding-left:30px;'>1754061146 1362756627</p>" +
                    "    <p style='padding-left:30px;'>2390356460</p>" +
                    "</div>";

                nsDialog.jAlert(noticeInfo);
                return true;
            }
            $("#selectfile").click();
        });

        $("#selUp").bind("click",function(){
//            var fileName = $("#selectfile").val();
            var fileName = $("#input1").val();
            if(fileName == undefined || fileName == "" || (fileName.search(/(.*xls$)|(.*xlsx$)/g) === -1)){
                nsDialog.jAlert("请选择EXCEL文件！");
                return ;
            }
            $("#selUp").hide();
            $("#uploading").css("display", "block");

            ajaxFileUpload();
            $("#uploading").css("display", "none");
            $("#selUp").show();
        });
        //点击确认按钮
        $("#submit_import").click(function(){
            submitImport();
            $("#submit_import").removeAttr("disabled");
            $("#cancel_import").removeAttr("disabled");
        });

        //点击取消按钮
        $("#cancel_import").click(function(){
            cleanImportConfig();
        });

        $("input[type='radio']").bind("click",function(){
            initRadioCss();
        });

        initRadioCss();

        $(".close").hide();
        $("#systemFieldList > div").live("mouseover",function(){

                if($(this).find(".close") && $(this).find(".close")[0])
                {
                        $(this).find(".close").show();
                }

            }
        );
        $("#systemFieldList > div").live("mouseout",function(){

                if($(this).find(".close") && $(this).find(".close")[0])
                {
                    $(this).find(".close").hide();
                }

            }
        );

        $(".deleteFiledMapping").live("click",function(){
            var obj = $(this).closest("div").find("span");
            if(!obj)
            {
                return;
            }
            var systemField =$(this).closest("div")[0].id.substring(7,$(this).closest("div")[0].id.length);
            var str = obj.html().split("&lt;==&gt;");

            var fieldMapping = $("#fieldMapping").val();
            var mappingObj;
            if(fieldMapping == undefined || fieldMapping == ""){
                mappingObj = {};
            }else{
                mappingObj = JSON.parse(fieldMapping);
                delete mappingObj[systemField];
            }
            addHeadListElement(str[1]);
            initHeadList();
            $("#system_" + systemField).html($("#system_desc_" + systemField).val()+"");
            $("#fieldMapping").val(JSON.stringify(mappingObj));

            $("#selectedField").val("");
            $("#system_" + systemField).removeClass("div_hover")
        });
    }
);

/**
 * 提交导入
 */
function submitImport(){
    $("#submit_import").attr("disabled", "true");
    $("#cancel_import").attr("disabled", "true");
    var importRecordId = $("#importRecordId").val();
    var fieldMapping = $("#fieldMapping").val();
    if(importRecordId == ""){
        nsDialog.jAlert("文件未上传或上传文件记录丢失，请重新上传！");
        return ;
    }
    if(fieldMapping == "" || fieldMapping =="{}"){
        nsDialog.jAlert("请先处理字段匹配！")
        return ;
    }
    var importType = $("input:radio:checked").val();
    var importUrl;
    if(importType == "CUSTOMER"){
        importUrl = "importCustomer.do?method=importCustomerFromExcel";
    }else if(importType == "SUPPLIER"){
        importUrl = "importSupplier.do?method=importSupplierFromExcel";
    }else if(importType == "INVENTORY"){
        importUrl = "importInventory.do?method=importInventoryFromExcel";
    }else if(importType == "MEMBER_SERVICE"){
        importUrl = "importMemberService.do?method=importMemberServiceFromExcel";
    }else if(importType == "ORDER"){
        importUrl = "importOrder.do?method=importOrderFromExcel";
    }else{
        nsDialog.jAlert("请选择导入类型！");
        return ;
    }
    $("#submit_import").hide();
    $("#cancel_import").hide();
    $("#importing").show();
    importDataFromExcel(importRecordId, fieldMapping, importUrl);
    $("#submit_import").show();
    $("#cancel_import").show();
    $("#importing").hide();

}

/**
 * 发送导入请求
 * @param importRecordId
 * @param fieldMapping
 * @param importUrl
 */
function importDataFromExcel(importRecordId, fieldMapping, importUrl){
    $.ajax({
        type:"POST",
        url:importUrl,
        async:false,
        data:{
            importRecordId:importRecordId,
            fieldMapping:fieldMapping,
            importToDefault:$("#importToDefault").val()
        },
        cache:false,
        success:function(data){
            if(data != null || data != undefined){
                var alertMessage = "";
                var resultJson = JSON.parse(data);
                if(resultJson.isSuccess == true){
                    alertMessage += "导入成功！";
                    alertMessage += " 总数：" + resultJson.totalCount;
                    alertMessage += " 成功：" + resultJson.successCount;
                    alertMessage += " 失败：" + resultJson.failCount;
                    $("#importToDefault").val('');
                    nsDialog.jAlert(alertMessage);
                    cleanImportConfig();
                }else if("storeHouseEmpty" == resultJson.message) {
                    nsDialog.jConfirm("您有商品未填写仓库，若未填写则该商品将导入到默认仓库！是否继续上传？",null,function(result) {
                       if(result) {
                           $("#importToDefault").val('true');
                           $("#submit_import").click();
                       }
                    });
                } else {
                    alertMessage += "导入失败！<br>";
                    alertMessage += resultJson.message;
                    alertMessage += "<br>请修改导入文件重新导入";
                    $("#importToDefault").val('');
                    nsDialog.jAlert(alertMessage);
                    cleanImportConfig();
                }

            }else{
                nsDialog.jAlert("导入结束，未返回导入结果信息，请联系后台确认！");
                $("#importToDefault").val('');
                cleanImportConfig();
            }
        },
        error:function(){
            nsDialog.jAlert("导入出错，请联系技术人员！");
            cleanImportConfig();
        }
    });

}

/**
 * 清除导入设置
 */
function cleanImportConfig(){
    $("#selectfile").val("");
    $("#systemFieldList").html("");
    $("#uploadFieldList").html("");
    $("#importRecordId").val("");
    $("#selectedField").val("");
    $("#fieldMapping").val("");
    $("#input1").val("");
    $("#submit_import").removeAttr("disabled");
    $("#cancel_import").removeAttr("disabled");
    jsonStrMap.clearMap();
    $("input[type='radio']")[0].checked = true;
    initRadioCss();
}

/**
 * 使用ajax上传文件
 */
function ajaxFileUpload() {
    $("#importRecordId").val("");
    $("#selectedField").val("");
    $("#fieldMapping").val("");
    $("#systemFieldList").html("");
    $("#uploadFieldList").html("");

    var importType = $("input:radio:checked").val();

    var uploadUrl;
    if(importType == "CUSTOMER"){
        uploadUrl = "importCustomer.do?method=uploadExcel";
    }else if(importType == "SUPPLIER"){
        uploadUrl = "importSupplier.do?method=uploadExcel";
    }else if(importType == "INVENTORY"){
        uploadUrl = "importInventory.do?method=uploadExcel";
    }else if(importType == "MEMBER_SERVICE"){
        uploadUrl = "importMemberService.do?method=uploadExcel";
    }else if(importType == "ORDER"){
        uploadUrl = "importOrder.do?method=uploadExcel";
    }else{
        nsDialog.jAlert("请选择导入类型！");
        return ;
    }

    $.ajaxFileUpload({
            url:uploadUrl,
            secureuri:false,
            fileElementId:'selectfile',
            dataType: 'json',
            error: function (data) {
                var alertMessage = "后台解析文件出现异常：" + data;
                alert(alertMessage);
            },
            success: function(data) {
                var result = data;
                if(result == undefined){
                    return ;
                }
                //单据导入信息
                if(typeof(result) == "object"&&result.orderImportMsg){
                    alert(result.orderImportMsg);
                    return ;
                }
                if(typeof(result) == "String"){
                    alert(result);
                    return ;
                }
                var importRecordId = result.importRecordId;
                var systemFieldList = result.systemFieldList;
                var headList = result.headList;
                renderFieldList(systemFieldList, headList);
                $("#importRecordId").val(importRecordId);
            }
        }
    );


    mouseOver = function(field){
//        $(field).addClass("div_hover");
    }

    mouseOut = function(field){
//        $(field).removeClass("div_hover");
    }


    /**
     * 将获取的系统和excel字段列表渲染到页面上
     * @param systemFieldList
     * @param headList
     */
    function renderFieldList(systemFieldList, headList){
        if(typeof headList != "object" || typeof headList.length != "number"){
            return ;
        }

        var systemFieldHtml = "";
        var systemFieldPos;
        var systemFieldKey;
        var systemFieldDesc;
        for(var i = 0; i < systemFieldList.length; i ++){
            systemFieldPos = systemFieldList[i].indexOf("_");
            systemFieldKey = systemFieldList[i].substring(0, systemFieldPos);
            systemFieldDesc = systemFieldList[i].substring(systemFieldPos + 1, systemFieldList[i].length);

            systemFieldHtml += "<div id='system_" + systemFieldKey + "' name='" + systemFieldKey + "' onmouseover='mouseOver(this)' onmouseout='mouseOut(this)' onclick='selectSystemField(this)'>" + systemFieldDesc + "</div>";
            systemFieldHtml += "<input type='hidden' id='system_desc_" + systemFieldKey + "' value='" + systemFieldDesc + "' />";
        }
        $("#systemFieldList").html(systemFieldHtml);

        var uploadFieldHtml = "";
        jsonStrMap.put("headList",headList);
        for(var j = 0; j < headList.length; j ++){
            uploadFieldHtml += "<div id='upload_" + headList[j] + "' name='" + headList[j] + "' onmouseover='mouseOver(this)' onmouseout='mouseOut(this)' onclick='selectUploadField(this)'>" + headList[j] + "</div>";
        }
        $("#uploadFieldList").html(uploadFieldHtml);
    }
}

/**
 * 点击系统字段
 * @param field
 */
function selectSystemField(field){
    $("#systemFieldList").children("div").removeClass("div_hover");
    $("#uploadFieldList").children("div").removeClass("div_hover");
    $(field).addClass("div_hover");
    $("#selectedField").val($(field).attr("name"));
}

/**
 * 选中上传文件字段
 * @param field
 */
function selectUploadField(field){

    var systemField = $("#selectedField").val();
    if(systemField == undefined || systemField == ""){
        nsDialog.jAlert("请先选择系统字段！");
        return ;
    }

    $("#uploadFieldList").children("div").removeClass("div_hover");
    $(field).addClass("div_hover");

    var fieldMapping = $("#fieldMapping").val();
    var mappingObj;
    if(fieldMapping == undefined || fieldMapping == ""){
        mappingObj = {};
    }else{
        mappingObj = JSON.parse(fieldMapping);
    }
    var oldName = mappingObj[systemField];
    var mappingCommand = "mappingObj." + systemField + " = '" + $(field).attr("name") + "'";
    eval(mappingCommand);
    $("#system_" + systemField).html("<span>"+$("#system_desc_" + systemField).val() + " <==> " + $(field).attr("name") + "</span><img src=\"images/tiClose.png\" class=\"close deleteFiledMapping\" style=\"display:none\"/>");
    $("#fieldMapping").val(JSON.stringify(mappingObj));

    addHeadListElement(oldName);
    removeHeadListElement($(field).attr("name"));
    initHeadList();
}

function initRadioCss(){
    $("input[type='radio']").each(function(i){
        if($(this)[0].checked)
        {
            $(this).next("label").attr("class","selected");
        }
        else
        {
            $(this).next("label").removeAttr("class");
        }
    });
}

function initHeadList()
{
    var uploadFieldHtml = "";
    var headList = jsonStrMap.get("headList");
    if(headList && headList.length>0)
    {
        for(var j = 0; j < headList.length; j ++){
            uploadFieldHtml += "<div id='upload_" + headList[j] + "' name='" + headList[j] + "' onmouseover='mouseOver(this)' onmouseout='mouseOut(this)' onclick='selectUploadField(this)'>" + headList[j] + "</div>";
        }
    }

    $("#uploadFieldList").html(uploadFieldHtml);
}

function removeHeadListElement(str)
{
    if(!str)
    {
        return;
    }

    var headList = jsonStrMap.get("headList");

    if(headList && headList.length>0)
    {
        var index = headList.indexOf(str);
        if (index > -1) {
            headList.splice(index, 1);
        }
    }

    jsonStrMap.put("headList",headList);
}

function addHeadListElement(str)
{
    if(!str)
    {
        return;
    }

    var headList = jsonStrMap.get("headList");
    headList.unshift(str);
    jsonStrMap.put("headList",headList);
}