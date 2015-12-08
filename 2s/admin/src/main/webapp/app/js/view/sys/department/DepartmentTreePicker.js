/**
 * @class Ext.view.sys.department.DepartmentTreePicker
 * @extends Ext.ux.TreePicker
 * @author ZhangJuntao
 * 部门 和 职位 下拉树
 */
Ext.define('Ext.view.sys.department.DepartmentTreePicker', {
    extend: 'Ext.ux.TreePicker',
    xtype: 'departmentTreePicker',
    requires:[
        'Ext.ux.TreePicker'
    ],
    displayField:'text',
    rootVisible: false,
    initComponent:function () {
        var me = this;
        Ext.apply(me, {
            store:Ext.create('Ext.store.sys.Departments')
        });
        this.callParent();
    },
    /**
     * Associates this select with a specific Department.
     * @param {Ext.model.sys.Department} department
     */
    setDepartment:function (department) {
        this.department = department;
    },

    /**
     * Gets the department associated with this select
     * @return {Ext.model.sys.Department} department
     */
    getDepartment:function () {
        return this.department;
    },


    /**
     * Associates this select with a specific Department.
     * @param {Ext.model.sys.Department} occupation
     */
    setOccupation:function (occupation) {
        this.occupation = occupation;
    },


    /**
     * Gets the department associated with this select
     * @return {Ext.model.sys.Department} department
     */
    getOccupation:function () {
        return this.occupation;
    }
});