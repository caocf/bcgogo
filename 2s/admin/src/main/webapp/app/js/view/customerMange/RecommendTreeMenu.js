/**
 * @author ZhangJuntao
 * @class Ext.view.dataMaintenance.permission.ModuleTreeMenu
 * @extends Ext.menu.Menu
 * @description 模块树右键菜单栏 view
 */
Ext.define('Ext.view.customerMange.RecommendTreeMenu', {
    extend:'Ext.menu.Menu',
    xtype:'recommendTreeMenu',
    items:[
//        {
//            iconCls:'tasks-new-list',
//            id:'addFirstRecommendCategory',
//            text:'添加一级分类'
//        },
        {
            iconCls:'tasks-new-folder',
            id:'addSecondRecommendCategory',
            text:'添加二级分类'
        },
//        {
//            id:'editFirstRecommendCategory',
//            text:'编辑一级分类'
//        },
//        {
//            id:'editSecondRecommendCategory',
//            text:'编辑二级分类'
//        },
        {
            text:'查看图片',
            id:'showRecommendImg'
        },
        {
            text:'添加图片',
            id:'addRecommendImg'
        },
        {
            text:'删除',
            id:'deleteFirstRecommendCategory'
        },
        {
            text:'删除二级分类',
            id:'deleteSecondRecommendCategory'
        }
//        ,
//        {
//            text:'刷新',
//            id:'refreshRecommendTree'
//        }
    ],

    setRecordData:function (recordData) {
        this.recordData = recordData;
    },

    getRecordData:function () {
        return this.recordData;
    }

});
