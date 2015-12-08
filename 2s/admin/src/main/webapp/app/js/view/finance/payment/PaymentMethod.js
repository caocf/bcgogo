Ext.define('Ext.view.finance.payment.PaymentMethod', {
    extend:'Ext.form.ComboBox',
    alias:'widget.paymentMethod',
    editable:false,
    store:Ext.create('Ext.store.finance.PaymentMethod'),
    queryMode:'local',
    displayField:'label',
    valueField:'value'
});