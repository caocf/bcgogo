/**
 * @author ZhangJuntao
 * @class Ext.view.dataMaintenance.permission.ModuleTreeMenu
 * @extends Ext.menu.Menu
 * @description 模块树右键菜单栏 view
 */
Ext.define('Ext.view.dataMaintenance.permission.ModuleTreeMenu', {
    extend:'Ext.menu.Menu',
    xtype:'moduleTreeMenu',
    items:[
        {
            iconCls:'tasks-new-list',
            id:'addModule',
            text:'增加子模块'
        },
        {
            iconCls:'tasks-new-folder',
            id:'addRole',
            text:'增加角色'
        },
        {
            id:'copyRole',
            text:'复制'
        },
        {
            id:'pasteRole',
            text:'粘贴'
        },
        {
            id:'editModule',
            text:'编辑'
        },
        {
            id:'editRole',
            text:'编辑'
        },
        {
            text:'删除',
            id:'deleteModule'
        },
        {
            text:'删除',
            id:'deleteRole'
        },
        {
            text:'刷新',
            id:'refreshModuleTree'
        }
    ],

    setRole:function (role) {
        this.role = role;
    },

    getRole:function () {
        return this.role;
    },

    setModule:function (module) {
        this.module = module;
    },

    getModule:function () {
        return this.module;
    },

    getCopyRole:function () {
        return this.copyRole;
    },

    setCopyRole:function (copyRole) {
        this.copyRole = copyRole;
    }


});
