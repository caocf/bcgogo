/**
 * author:zhangjuntao
 * description:role下的resources
 * role->resource
 */
Ext.define('Ext.store.dataMaintenance.RoleResources', {
    extend:'Ext.data.Store',
    model:"Ext.model.dataMaintenance.Resource" ,
    proxy:{
        type:'ajax',
        api:{
            read:'resource.do?method=getResourcesByRoleId'
        },
        reader:{
            type:'json',
            root:"results"
        },
        writer:{
            writeAllFields:true,
            type:'json',
            root:"results"
        }
    }
});