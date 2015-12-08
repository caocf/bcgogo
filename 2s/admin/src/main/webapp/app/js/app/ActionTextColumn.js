/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-11-5
 * Time: 上午10:40
 * To change this template use File | Settings | File Templates.
 */
Ext.define('ActionTextColumn', {
    extend: 'Ext.grid.column.Action',
    alias: ['widget.actiontextcolumn'],
    constructor: function(config) {
        var me = this,
            cfg = Ext.apply({}, config),
            items = cfg.items || [me],
            l = items.length,
            i,
            item;
        delete cfg.items;
        me.callParent([cfg]);
        me.items = items;
        me.renderer = function(v, meta) {
            v = Ext.isFunction(cfg.renderer) ? cfg.renderer.apply(this, arguments)||'' : '';
            meta.tdCls += ' ' + Ext.baseCSSPrefix + 'action-col-cell';
            for (i = 0; i < l; i++) {
                item = items[i];
                if(Ext.isFunction(item.getClass)) item.getClass.apply(item.scope||me.scope||me, arguments);

                item.disable = Ext.Function.bind(me.disableAction, me, [i]);
                item.enable = Ext.Function.bind(me.enableAction, me, [i]);

                v += '<table style="padding-left:6px;float:left;display:'+(Ext.isEmpty(item.text)?'none':'block')+'"><tr>';
                if(!Ext.isEmpty(item.icon)){
                    v += '<td><img alt="' + (item.altText || me.altText) + '" src="' + (item.icon || Ext.BLANK_IMAGE_URL) +
                        '" class="' + Ext.baseCSSPrefix + 'action-col-icon ' + Ext.baseCSSPrefix + 'action-col-' + String(i) + ' ' + (item.disabled ? Ext.baseCSSPrefix + 'item-disabled' : ' ') + (item.iconCls || '') +
                        ' ' + (Ext.isFunction(item.getClass) ? item.getClass.apply(item.scope||me.scope||me, arguments) : (me.iconCls || '')) + '"' +
                        ((item.tooltip) ? ' data-qtip="' + item.tooltip + '"' : '') + ' /></td>';

                }
                v += '<td style="height:16px;line-height:16px;"><a style="cursor: pointer" href="javascript:void(0);"' +
                    ' class="' + Ext.baseCSSPrefix + 'action-col-icon ' + Ext.baseCSSPrefix + 'action-col-' + String(i) + ' ' + (item.disabled ? Ext.baseCSSPrefix + 'item-disabled' : ' ') + (item.cls || '') +
                    ' ' + (Ext.isFunction(item.getClass) ? item.getClass.apply(item.scope||me.scope||me, arguments) : (me.iconCls || '')) + '"' +
                    ((item.tooltip) ? ' data-qtip="' + item.tooltip + '"' : '') + '>' + (item.text || '') + '</a></td>';
                v += '</tr></table>';
            }
            return v;
        };
    }
});
