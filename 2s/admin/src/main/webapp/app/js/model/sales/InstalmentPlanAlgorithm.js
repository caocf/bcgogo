/**
 * 分期算法 model
 */
Ext.define('Ext.model.sales.InstalmentPlanAlgorithm', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'name', type: 'string'},
        {name: 'periods', type: 'string'},
        {name: 'periodsMonthRate', type: 'string'},
        {name: 'terminallyRatio', type: 'string'},
        {name: 'memo', type: 'string'}
    ],
    listeners: {
        exception: function (proxy, response, operation) {
            Ext.MessageBox.show({
                title: '错误异常',
                msg: operation.getError(),
                icon: Ext.MessageBox.ERROR,
                buttons: Ext.Msg.OK
            });
        }
    }
});