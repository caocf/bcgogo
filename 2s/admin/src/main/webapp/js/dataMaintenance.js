var dataMT = {};
var nextPageNo = 1;
var isLastPage = false;
$(function() {
    //控制标签头 "SOLR词库"、"索引重建"对应的页面的显示与隐藏
    $(".rightBody div").bind("click", function() {
        $(".rightBody div").attr("class", "title");
        $(this).attr("class", "titleHover");
        $(".fileInfo>form,.fileInfo>div").hide();
        $("#" + this.id + "_form").show();
    });
    $("#reindexSolr").click();
    //存放用户搜索店面帐号时取到的shopId,等同于 隐藏域
    var bcShop = {
        shopId:""
    }

    //如果 选择的命令是对本店面的商品进行操作，则显示店面帐号搜索框，以取得相应的shopId
    $("#reindexSolr_content").bind("change", function() {
        showBcgogoShop($(this).find("option:selected").attr("class") == "local_product");

        $("#orderType").val("");
        if("reindexOrder_All"==$(this).find("option:selected").val() || "reindexOrder_DS"==$(this).find("option:selected").val()){
            $("#bcgogoOrderType_span").show();
        }else{
            $("#bcgogoOrderType_span").hide();
        }
    });

    //提交 reindex 命令的主处理方法
    var reindexAction = {
        reindexByCommand:function(command) {
            if ($("#bcgogoShop_span").css("display") == "inline" && !bcShop.shopId) {
                alert("未搜索到该店面账户!");
                return;
            }
            var orderType = "";
            if($("#orderType") && $("#orderType")[0]){
                orderType = $("#orderType").val();
            }
            $("#reindexButton").attr("disabled", true);
            $.get("dataMaintenance.do?method=reindexsolr&_dc=" + new Date().getTime(),
              {shopId:bcShop.shopId,orderType:orderType,command:command}, function(json) {
                  alert(json.msg);
                  jQuery("#reindexButton").attr("disabled", false);
              }, "json");
        }
    }
    $("#yiMei_option").click(function() {
        var str = APP_BCGOGO.Net.syncAjax({
            url:"yiMei.do?method=options",
            data:{command:$("#yiMei_options").val()},
            cache:false,
            dataType:"json",
            success:function(str) {
                alert(str.info);
            },error:function(str) {
                alert("操作失败");
            }
        });

    });

    /**
     * 验证两个FILE控件为空,符合条件则提交
     * @param form //form表单
     */
    var submitFile = function(form) {
        var s1 = form.stFile[0].value;
        var s2 = form.stFile[1].value;
        if (!s1 || !s2) {
            alert("文件不可为空！");
            return;
        }
        form.submit();
    }

    /**
     * 显示或者隐藏 #bcgogoShop 元素
     * @param status   true:显示  false:隐藏
     */
    var showBcgogoShop = function(status) {
        if (status)
            $("#bcgogoShop_span").show();
        else
            $("#bcgogoShop_span").hide();
        bcShop.shopId = "";
        $("#bcgogoShop").val("");
    }

    /**
     * 延迟搜索器 当用户keyUp后，一定时间内未操作，则发送请求。利于减少下拉建议的请求次数
     */
    var LazzySearcher = {
        _timeId:null,
        _getTimeout:function(v) {
            return  setTimeout(function() {
                if (v.value) {
                    $.get("dataMaintenance.do?method=searchuserbyfuzzyuserno",
                      {searchWord:v.value,maxResults:10}, function(json) {
                          ajaxStyle(v, json);
                      }, "json");
                }
            }, 300);
        },
        lazzySearch:function(domObj) {
            if (!this._timeId) {
                this._timeId = this._getTimeout(domObj);
            } else {
                clearTimeout(this._timeId);
                this._timeId = this._getTimeout(domObj);
            }
        }
    }

    /**
     * 初始化下拉建议
     * @param domObject  文本框对象
     * @param jsonStr     JSON
     */
    var ajaxStyle = function(domObject, jsonStr) {
        var offset = $(domObject).offset();
        var offsetHeight = $(domObject).height();
        var offsetWidth = $(domObject).width();
        $(".suggestionMain").html("");
        $(".suggestionMain").css({
            'display':'block','position':'absolute',
            'left':offset.left + 'px',
            'top':offset.top + offsetHeight + 4 + 'px',
            'width':offsetWidth + 2 + "px",
            'background-color':'#FFFFFF',
            'border':'1px solid #CCC',
            'font':'12px/20px "微软雅黑",Arial'
        });
        for (var i = 0,len = jsonStr.length; i < len; i++) {
            var li = $("<li></li>");
            li.html(jsonStr[i].userNo + "+" + jsonStr[i].userName);
            li.attr("title", jsonStr[i].shopIdStr + "+" + jsonStr[i].userNo + "+" + jsonStr[i].userName);
            li.css({
                'text-align': 'left','padding-left': '6px','height':'20px','overflow':'hidden',
                'background-color':'#FFFFFF'
            });
            li.mouseenter(function() {
                $(".suggestionMain li").css({
                    'background-color':'#FFFFFF','color':'#000000'
                });
                $(this).css({'background-color':'#69C','color':'#FFFFFF'});
            });
            li.click(function() {
                domObject.value = this.innerHTML;
                bcShop.shopId = this.title.split("+")[0];
                $(".suggestionMain").hide();
            });
            $(".suggestionMain").append(li);
        }
    }

    /**
     * 点击下拉建议外围，隐藏下拉建议
     */
    $(document).click(function(event) {
        if ($(event.target).attr("class") != "suggestionMain") {
            $(".suggestionMain").hide();
        }
    });

    dataMT["reindexAction"] = reindexAction;
    dataMT["submitFile"] = submitFile;
    dataMT["LazzySearcher"] = LazzySearcher;


    jQuery("#div_close,#cancleBtn").click(function() {
        window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
    });

    jQuery("#addConfigBtn").bind("click", function() {
        bcgogo.checksession({"parentWindow":window.parent,'iframe_PopupBox':$("#iframe_PopupBox")[0],'src':"dataMaintenance.do?method=addConfig"});
    });

    //点击增加短信模板，将弹出增加界面
    jQuery("#addMsgTemplateBtn").bind("click", function() {
        bcgogo.checksession({"parentWindow":window.parent,'iframe_PopupBox':$("#iframe_PopupBox")[0],'src':"dataMaintenance.do?method=addMsgTemplate"});
    });

    jQuery("#configSearchBtn").bind("click", function() {
        nextPageNo = 1;
        searchConfig();
    });

    //点击查询短信模板
     jQuery("#msgTemplateSearchBtn").bind("click", function() {
        nextPageNo = 1;
        searchMsgTemplate();
    });

});


function searchConfig() {
    var config_key = jQuery("#config_key").val();
    var config_value = jQuery("#config_value").val();
    var shopId = -1;
    if (jQuery("#config_key").val() == "key") {
        config_key = "";
    }
    if (jQuery("#config_value").val() == "value") {
        config_value = "";
    }
    jQuery.ajax({
        type:"POST",
        url:"dataMaintenance.do?method=searchConfig",
        data:{name:config_key,value:config_value,shopId:shopId,pageNo:nextPageNo},
        cache:false,
        dataType:"json",
        success:function(jsonStr) {
            initConfigTable(jsonStr);
            refreshPage(jsonStr, "dynamical2", "initMsgTemplateTable");
        }
    });
}

function searchMsgTemplate() {
    var msgTemplate_type = jQuery("#msgTemplate_type").val();
    var shopId = -1;
    if (msgTemplate_type == "类型") {
        msgTemplate_type = "";
    }

    jQuery.ajax({
        type:"POST",
        url:"dataMaintenance.do?method=searchMessageTemplate",
        data:{type:msgTemplate_type,shopId:shopId,pageNo:1},
        cache:false,
        dataType:"json",
        success:function(jsonStr) {
            initMsgTemplateTable(jsonStr);
            refreshPage(jsonStr, "dynamical1", "initConfigTable");
        }
    });
}

function initMsgTemplateTable(jsonStr) {
  jQuery("#table_msgTemplate tr:not(:first)").remove();
  if (jsonStr.length > 0) {
    //jsonStr长度为2，第一个元素为MsgTemplateDTO
    jsonStr = jsonStr[0];
    for (var i = 0; i < jsonStr.length; i++) {
      var msgTemplate_type = jsonStr[i].type == null ? " " : jsonStr[i].type;
//            var msgTemplate_typeStr = jsonStr[i].typeStr == null ? " " : jsonStr[i].valueStr;
      var name = jsonStr[i].name == null ? " " : jsonStr[i].name;
      var scene = jsonStr[i].scene == null ? " " : jsonStr[i].scene;
      var necessary = jsonStr[i].necessary == null ? " " : jsonStr[i].necessary;
      var msgTemplate_content = jsonStr[i].content == null ? " " : jsonStr[i].content;
      var msgTemplate_contentStr = jsonStr[i].contentStr == null ? " " : jsonStr[i].contentStr;
      var shop_id = jsonStr[i].shopId == null ? " " : jsonStr[i].shopId;
      var tr = '<tr>';
      tr += '<td style="border-left:none;">' + (i + 1) + '&nbsp;</td>';
      tr += '<td>' + name + '</td>';
      tr += '<td title=  \'' + msgTemplate_type + '\'>' + msgTemplate_type + '</td>';
      tr += '<td>' + scene + '</td>';
      tr += '<td>' + necessary + '</td>'
      tr += '<td title= \'' + msgTemplate_content + '\'>' + msgTemplate_contentStr + '</td>';
      tr += '<td><a class="msgTemplate_modify" href="#"  onclick="modifyMsgTemplate(\'' + msgTemplate_type + '\',\'' + msgTemplate_content + '\',\'' + shop_id + '\',\'' + name + '\',\'' + scene + '\',\'' + necessary + '\')">修改</a></td>';
      tr += '</tr >';
      jQuery("#table_msgTemplate").append(jQuery(tr));
    }
  }

}
function initConfigTable(jsonStr) {
    jQuery("#table_config tr:not(:first)").remove();
    if (jsonStr.length > 0) {
        for (var i = 0; i < jsonStr.length - 1; i++) {
            var key_name = jsonStr[i].name == null ? " " : jsonStr[i].name;
            var key_value = jsonStr[i].value == null ? " " : jsonStr[i].value;
            var key_valueStr = jsonStr[i].valueStr == null ? " " : jsonStr[i].valueStr;
            var shop_id = jsonStr[i].shopId == null ? " " : jsonStr[i].shopId;
            var description = jsonStr[i].description == null ? " " : jsonStr[i].description;
            var key_descriptionStr = jsonStr[i].descriptionStr == null ? " " : jsonStr[i].descriptionStr;
            var tr = '<tr>';
            tr += '<td style="border-left:none;">' + (i + 1) + '&nbsp;</td>';
            tr += '<td>' + key_name + '</td>';
            tr += '<td title=  \'' + key_value + '\'>' + key_valueStr + '</td>';
            tr += '<td title= \'' + description + '\'>' + key_descriptionStr + '</td>';
            tr += '<td><a class="config_modify" href="#"  onclick="modifyConfig(\'' + key_name + '\',\'' + key_value + '\',\'' + shop_id + '\',\'' + description + '\')">修改</a></td>';
            tr += '</tr >';
            jQuery("#table_config").append(jQuery(tr));
        }
    }

}
function modifyConfig(key_name, key_value, shop_id, description) {
    url = encodeURI("dataMaintenance.do?method=modifyConfig" +
        "&name=" + key_name + "&value=" + key_value + "&shopId=" + shop_id + "&description=" + description);
    bcgogo.checksession({"parentWindow":window.parent,'iframe_PopupBox':$("#iframe_PopupBox")[0],'src':url});
}

//点击修改短信模板
function modifyMsgTemplate(type, content, shop_id,name,scene,necessary) {
    url = "dataMaintenance.do?method=modifyMessageTemplate" + "&type=" + type + "&content=" + encodeURIComponent(content) + "&shopId=" + shop_id+ "&name=" + name+ "&scene=" + scene+ "&necessary=" + necessary;
    bcgogo.checksession({"parentWindow":window.parent,'iframe_PopupBox':$("#iframe_PopupBox")[0],'src':url});

}