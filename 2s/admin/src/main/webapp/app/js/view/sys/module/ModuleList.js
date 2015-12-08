Ext.define('Ext.view.sys.module.ModuleList', {
    extend:'Ext.view.View',
    alias:'widget.modulelist',
    store:Ext.create('Ext.store.sys.Modules'),
    trackOver:true,                  //True to enable mouseenter and mouseleave events
    cls:'sys-module-list',
    itemSelector:'.sys-module-list-item',
    overItemCls:'sys-module-list-item-hover',
    tpl:'<tpl for="."><div class="sys-module-list-item">{value}</div></tpl>'
});