(function () {
    APP_BCGOGO.namespace("Module.wjl.bcgogoValidator");

    /**
     * 用于处理confirm确认框
     */
    var ConfirmUtil = {
        message:null,
        //message不为空时，弹出确认框
        popUpConfirm:function () {
            if (!this.message) {
                return true;
            } else {
                return window.confirm(this.message);
            }
        }
    }


    /**
     * 验证工具类ValidateUtil的 验证参数
     */
    var validateParams = {
	      disabledDom:{},
        wrapper:"li",
        rules:{},
        messages:{},
        submitHandler:function (form) {
            if (ConfirmUtil.popUpConfirm()){
	           $(validateParams.disabledDom).attr("disabled","disabled");
                if (jQuery("#saveBtn").length > 0) {
                    jQuery("#saveBtn").attr("disabled", "disabled");
                }
            if (jQuery("#saveBtn").length > 0) {
                jQuery("#printBtn").attr("disabled", "disabled");
            }
            if (jQuery("#saveBtn").length > 0) {
                jQuery("#cancelBtn").attr("disabled", "disabled");
            }
                form.submit();
            }else{
	              $(form).attr("submitStatus","submitCancel");
            }
        },
        showErrors:function (errorMap, errorList) {
            var msg = "";
            $.each(errorList, function (i, v) {
                msg += (v.message + "\r\n");
            });
            if (msg != "") alert(msg);
        },
        onkeyup:false,
        onfocusout:false,
        focusInvalid:true
    };

    /**
     * 验证工具类
     */
    var ValidateUtil = {
        validate:function (formDom) {
            $(formDom).validate(validateParams);
        },
        setRules:function (p) {
            validateParams.rules = p;
        },
        setMessages:function (p) {
            validateParams.messages = p;
        },
        setConfirmMessage:function (m) {
            ConfirmUtil.message = m;
        },
        setSubmitHandler:function(fn) {
            validateParams.submitHandler = fn;
        },
	      setDisabledDom:function(fn){
		      validateParams.disabledDom = fn;
        }
    }

    $.validator.addMethod("mobile", function (value, element, params) {
        if ( !value )
            return true;

        var matchMobile = /^1(\d){10}$/g;
        var matchPhone = /^([0-9]|[-*]){5,30}$/g;
        return APP_BCGOGO.Validator.stringIsMobilePhoneNumber(value) || matchPhone.test(value);
    }, "联系方式不正确!");

    APP_BCGOGO.Module.wjl.bcgogoValidator = ValidateUtil;
})();