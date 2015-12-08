(function() {
    APP_BCGOGO.namespace("Module.wjl.ajaxQuery");

    var jsonParams = {
        type:"POST",
        async:true,
        cache:false,
        url:"",
        data:{},
        dataType:"json",
        success:function(jsonStr) {
            GLOBAL.debug("ajax 返回成功!");
        },
        error:function(jsonStr) {
            GLOBAL.debug("url:" + this.url);
            GLOBAL.debug("ajax 返回失败!");
        }
    };

    /**
     * 简单的AJAX查询验证类
     */
    var AjaxQueryValidator = {
        checkUrl:function() {
            if (jsonParams.url == "") {
                GLOBAL.debug("jsonParams.url is null");
                return false;
            }
            return true;
        }
    };
    /**
     * AJAX通用查询类
     */
    var AjaxQuery = function() {
        //初始化URL和DATA
        this.setUrlData = function(url, data) {
            jsonParams.url = url;
            jsonParams.data = data;
        }
        //初始化返回类型
        this.setDataType = function(dataType) {
            jsonParams.dataType = dataType;
        }

        this.setAsyncAndCache = function(async, cache) {
            jsonParams.async = async;
            jsonParams.cache = cache;
        }

        //AJAX基本查询方法
        this.ajaxQuery = function() {
            if (arguments[0]) jsonParams.success = arguments[0];
            if (arguments[1]) jsonParams.error = arguments[1];
            if (AjaxQueryValidator.checkUrl())
                $.ajax(jsonParams);
        }
    }

    APP_BCGOGO.Module.wjl.ajaxQuery = new AjaxQuery();
})();