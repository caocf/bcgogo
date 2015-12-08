//todo 当用户40分钟内未操作页面，则弹出提示，并跳到登录页

/**
 * @description Alert notice dialog and redirect to page Login.jsp,
 *              when the customer have do nothing in current page in 20 minutes.
 * @author ??
 * @changer 潘震
 * @date 2012-09-19
 */
APP_BCGOGO.namespace("Module.Login");

;
(function () {
    var _MS = 1000,
        _S = 60,
        _M = 40,
        self = App.Module.Login;

    self._logoutTimerId = -1;

    self.isTimeout = function() {
        return self._logoutTimerId === -1;
    };
    self.stopApp = function(){
        if(!G.isEmpty(APP_BCGOGO.LogoutStopApp.MessageBottomPush)){
            APP_BCGOGO.LogoutStopApp.MessageBottomPush.dispose();
        }
    };
    self.startTimer = function() {
        self._logoutTimerId = setTimeout(function () {
                    var self = App.Module.Login;
                    Mask.Login();
                    $("#mask").css("z-index",1999);
                    var top = ($(window).scrollTop() + $(window).height()) / 2;
                    var left = ($(document).width() - $("#loginDiv").width()) / 2;
                    $("#loginDiv").css("top",top).css("left",left).show();
                    addSubmitButtonListener();
                    addUserNameInputTextListener();
                    addCancelButtonListener();
                    $("#j_username").val($.cookie("username"));
                    $("#lastUserNo").val($.cookie("username"));
                    $(document).unbind("mousemove keyup");
                    self.clearTimer();
//            var self = App.Module.Login;
//            $(document)
//                .unbind("mousemove keyup")
//                .bind("mousemove keyup", function () {
//                    nsDialog.jAlert("您很久未操作,系统已经自动退出!", null, function () {
//                        var cookieKeyListToBeClear = [
//                            "excludeFlowName",  "currentStepName" ,     "currentFlowName",  "currentStepStatus",
//                            "nextStepName",     "currentStepIsHead",    "url",              "hasUserGuide",
//                            "isContinueGuide",  "keepCurrentStep"
//                        ];
//
//                        for (var i = 0; i < cookieKeyListToBeClear.length; i++) {
//                            $.cookie(cookieKeyListToBeClear[i], null);
//                        }
//                        window.location = "login.jsp";
//                    }, false, '#id-top-Group');
//                });
//            self.clearTimer();
//            var self = App.Module.Login;
//            $(document)
//                .unbind("mousemove keyup")
//                .bind("mousemove keyup", function () {
//                    nsDialog.jAlert("您很久未操作,系统已经自动退出!", null, function () {
//                        var cookieKeyListToBeClear = [
//                            "excludeFlowName",  "currentStepName" ,     "currentFlowName",  "currentStepStatus",
//                            "nextStepName",     "currentStepIsHead",    "url",              "hasUserGuide",
//                            "isContinueGuide",  "keepCurrentStep"
//                        ];
//
//                        for (var i = 0; i < cookieKeyListToBeClear.length; i++) {
//                            $.cookie(cookieKeyListToBeClear[i], null);
//                        }
//                        window.location = "login.jsp";
//                    }, false, '#id-top-Group');
//                });
//            self.clearTimer();
        }, _MS * _S * _M);
    };

    self.clearTimer = function() {
        clearTimeout(self._logoutTimerId);
        self._logoutTimerId = -1;
    };
    function addSubmitButtonListener() {
        $("#input_submit").unbind().bind("click", function (e) {
            if (!$("#j_username").val()) {
                nsDialog.jAlert("用户名不能为空！");
                return false;
            }
            if (!$("#j_password").val()) {
                nsDialog.jAlert("密码不能为空！");
                return false;
            }
            $(this).attr("disabled","disabled");
            $.cookie("username", $.trim($("#j_username").val()), {"expires": 14});
            $("#loginForm").ajaxSubmit(function(result){
                $("#input_submit").removeAttr("disabled");
                if(result == 'success') {
                    $("#userNoWrong,#passwordWrong").css("display","none");
                    $("#loginDiv").hide();
                    $("#mask").css("display","none");
                    self.startTimer();
                } else if(result == 'userNoWrong'){
                    $("#userNoWrong,#passwordWrong").css("display","none");
                    $("#userNoWrong").css("display","block");
                } else if(result == 'passwordWrong') {
                    $("#userNoWrong,#passwordWrong").css("display","none");
                    $("#passwordWrong").css("display","block");
                } else {
                    window.location.href = result;
                }
            });
        });
    }


    function addCancelButtonListener() {
        $(".login_cancel").bind("click",function(){
            $("#loginDiv").hide();
            $("#mask").css("display","none");
            var cookieKeyListToBeClear = [
                "excludeFlowName",  "currentStepName" ,     "currentFlowName",  "currentStepStatus",
                "nextStepName",     "currentStepIsHead",    "url",              "hasUserGuide",
                "isContinueGuide",  "keepCurrentStep"
            ];

            for (var i = 0; i < cookieKeyListToBeClear.length; i++) {
                $.cookie(cookieKeyListToBeClear[i], null);
            }
            window.location = "login.jsp";
        });
    }

    function addUserNameInputTextListener() {
        $("#j_username").unbind()
            .bind("keyup", function (e) {
                filterUserName($(this), filterUsername($(this).val()));
            })
            .bind("blur", function (e) {
                filterUserName($(this), filterUsername($(this).val()));
            });
    }

    // 只能输 英文、数字、中文
    function filterUsername(s) {
        return s.replace(/[^a-zA-Z_\d\u4e00-\u9fa5]+/g, "");
    }

    function filterUserName($node, s) {
        if ($node.val() !== s) $node.val(s);
    }
}());

$(document).ready(function () {
    var oldTs = new Date().getTime(),
        curTs = 0,
        timeInterval = 10,
        multiPageSyncTimerId = 0,
        login = App.Module.Login;

    // when toggle "mousemove" and "keyup" events --->
    $(document).bind("mousemove keyup", function (event) {
        curTs = new Date().getTime();
    });

    login.startTimer();

    // start Poll to check and judge whether to launch a new logout timer.
    var pollId = setInterval(function () {
        var login = App.Module.Login;
        if (login.isTimeout()) {
            login.stopApp();
            clearInterval(pollId);
            clearInterval(multiPageSyncTimerId);
            return;
        }

        if (curTs > oldTs) {
            oldTs = curTs;
            login.clearTimer();
            login.startTimer();
        }
    }, timeInterval * 1000);


    // multi page sync
    ;
    (function () {
        var multiPageSyncTimerInterval = 30000,
            multiPageSyncCurrentTsForLogout = 0;

        multiPageSyncTimerId = setInterval(function() {
            if(!localStorage) return;

            multiPageSyncCurrentTsForLogout = parseFloat(localStorage.getItem("multiPageSyncCurrentTsForLogout")) || 0;
            curTs = Math.max(curTs, multiPageSyncCurrentTsForLogout);

            localStorage.setItem("multiPageSyncCurrentTsForLogout", curTs);
        }, multiPageSyncTimerInterval);
    }());

});

