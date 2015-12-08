Ext.define('Ext.model.dataMaintenance.Role', {
    extend:'Ext.data.Model',
    fields:[
        {name:'id', type:'string'},
        {name:'name', type:'string'},
        {name:'value', type:'string'},
        {name:'memo', type:'string'},
        {name:'status', type:'string'},
        {name:'type', type:'string'}
    ]
})