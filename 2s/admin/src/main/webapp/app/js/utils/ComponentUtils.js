/**
 * 组件 工具类
 * author:zhangjuntao
 */
Ext.define('Ext.utils.ComponentUtils', {
    alias:'widget.componentutils',
    statics:{
        ACTION_COLUMN_FIRST:"first",
        ACTION_COLUMN_SECOND:"second",
        ACTION_COLUMN_THIRD:"third"
    },
    //初始化controller
    initController:function (extApplication, controller) {
        if (!extApplication || !controller) {
            console.log("application or controller is null,can't int this controller.");
            return;
        }
        var keys = extApplication.controllers.keys;
        keys = keys.join(",");
        var extController = extApplication.getController(controller);
        if (keys.search(controller) == -1) {
            extController.init();
        }
        return extController;
    },
    //根据controller 获得views
    getViewsByController:function (controller) {
        if (!controller) {
            console.log("controller is null.");
            return;
        }
        return controller.views;
    },
    //增强actionColumn 功能：支持多个按钮
    getActionColumnItemsIndex:function (e) {
        var action = e.target.getAttribute('class');
        if (action.indexOf("x-action-col-0") != -1) {  //where id is the name of a dataIndex
            return this.self.ACTION_COLUMN_FIRST;
        } else if (action.indexOf("x-action-col-1") != -1) {
            return this.self.ACTION_COLUMN_SECOND;
        } else if ((action.indexOf("x-action-col-2") != -1)) {
            return this.self.ACTION_COLUMN_THIRD;
        } else {
            console.log("action column only support 3 items!")
        }
    }
});
