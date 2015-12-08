/**
 * @author ZhangJuntao
 * @class Ext.view.sys.department.TreeMenu
 * @extends Ext.menu.Menu
 * @description 部门树右键菜单栏 view
 */
Ext.define('Ext.view.sys.department.TreeMenu', {
    extend:'Ext.menu.Menu',
    xtype:'sysDepartmentTreeMenu',
    items:[
        {
            iconCls:'tasks-new-list',
            id:'addDepartment',
            text:'增加部门'
        },
        {
            iconCls:'tasks-new-folder',
            id:'addOccupation',
            text:'增加职位'
        },
        {
            id:'editDepartment',
            text:'编辑'
        },
        {
            id:'editOccupation',
            text:'编辑'
        },
        {
            text:'删除',
            iconCls:'tasks-delete-folder',
            id:'deleteDepartment'
        },
        {
            text:'删除',
            iconCls:'tasks-delete-list',
            id:'deleteOccupation'
        }
    ],
    /**
     * Associates this menu with a specific Department.
     * @param {Ext.model.sys.Department} department
     */
    setDepartment:function (department) {
        this.department = department;
    },

    /**
     * Gets the department associated with this menu
     * @return {Ext.model.sys.Department} department
     */
    getDepartment:function () {
        return this.department;
    },

    setOccupation:function (occupation) {
        this.occupation = occupation;
    },

    getOccupation:function () {
        return this.occupation;
    }

});
