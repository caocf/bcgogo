Ext.define('Ext.store.dataMaintenance.Resources', {
    extend:'Ext.data.Store',
    model:"Ext.model.dataMaintenance.Resource" ,
    proxy:{
        type:'ajax',
        api:{
            read:'resource.do?method=getResourcesByCondition',
            update:'resource.do?method=saveOrUpdateResource'
        },
        reader:{
            type:'json',
            root:"results",
            totalProperty:"totalRows"
        },
        writer:{
            writeAllFields:true,
            type:'json',
            root:"results"
        }
    }
});