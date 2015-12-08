/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-6-6
 * Time: 上午11:32
 * To change this template use File | Settings | File Templates.
 */

$(window).bind("load",function(){
//消息推送  右下角  弹出框
    if(window===window.top){
        var activityChecker = new App.Module.ActivityChecker();
        activityChecker
            .init({
                interval:10000,
                timeout:3*60*1000
            })
            .start();

        var messageBottom = new App.Module.MessageBottomPush();
        APP_BCGOGO.LogoutStopApp.MessageBottomPush = messageBottom;

        messageBottom.init({
            "onAutoHide":function(data){
                if(activityChecker.status()=="activity"){
                    var itemDataList = data ? data.data : undefined;
                    if(!G.Lang.isEmpty(itemDataList) && itemDataList.length>0){
                        $.each(itemDataList, function (index, itemData) {
                            if(!G.Lang.isEmpty(itemData) && !G.Lang.isEmpty(itemData.redirectUrl) && !G.Lang.isEmpty(itemData.currentPushMessageReceiverDTO)){
                                //反馈
                                processPushMessageFeedback(itemData,"READ","WEB_DEFAULT_HIT");
                            }
                        });
                    }
                }else{
                    var itemDataList = data ? data.data : undefined;
                    if(!G.Lang.isEmpty(itemDataList) && itemDataList.length>0){
                        $.each(itemDataList, function (index, itemData) {
                            if(!G.Lang.isEmpty(itemData) && !G.Lang.isEmpty(itemData.redirectUrl) && !G.Lang.isEmpty(itemData.currentPushMessageReceiverDTO)){
                                //无反馈
                                processPushMessageFeedback(itemData,"UNREAD");
                            }
                        });
                    }
                }
            },
            "onItemClick": function (data, itemData, $inst, event) {
                messageBottom.hide();
                if(!G.Lang.isEmpty(itemData) && !G.Lang.isEmpty(itemData.redirectUrl) && !G.Lang.isEmpty(itemData.currentPushMessageReceiverDTO)){
                    //反馈
                    processPushMessageFeedback(itemData,"READ","WEB_HIT");
                    //跳转
                    window.open(itemData.redirectUrl);//TODO 以后扩展可以用params字段
                }
            },
            "onClose": function (data, event) {
                var itemDataList = data ? data.data : undefined;
                if(!G.Lang.isEmpty(itemDataList) && itemDataList.length>0){
                    $.each(itemDataList, function (index, itemData) {
                        if(!G.Lang.isEmpty(itemData) && !G.Lang.isEmpty(itemData.redirectUrl) && !G.Lang.isEmpty(itemData.currentPushMessageReceiverDTO)){
                            //反馈
                            processPushMessageFeedback(itemData,"READ","WEB_NO_HIT");
                        }
                    });
                }
            },
            "url": "pushMessage.do?method=getPushMessageData"
        });
    }

    function processPushMessageFeedback(messageItem,pushMessageReceiverStatus,pushMessageFeedbackType){
        App.Net.asyncPost({
            url: "pushMessage.do?method=processPushMessageFeedback",
            dataType: "json",
            data: {
                messageId:messageItem.idStr,
                pushMessageReceiverStatus:pushMessageReceiverStatus,
                pushMessageType:messageItem.type,
                pushMessageFeedbackType: pushMessageFeedbackType,
                pushMessageReceiverId: messageItem.currentPushMessageReceiverDTO.idStr

            }
        });
    }
});
