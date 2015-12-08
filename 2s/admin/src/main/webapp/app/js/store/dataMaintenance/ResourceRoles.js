/**
 * author:zhangjuntao
 * description:resource 被那些role包含
 * resource->roles
 */
Ext.define('Ext.store.dataMaintenance.ResourceRoles', {
    extend:'Ext.data.Store',
    model:"Ext.model.dataMaintenance.Role",
    proxy:{
        type:'ajax',
        api:{
            read:'role.do?method=getRolesByResourceId'
        },
        reader:{
            type:'json',
            root:"results"
        }
    }
});