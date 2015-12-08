$(function () {
    if (window.parent != null) {
        var parent = window.parent.document.body;
        var serviceTotal = $('#serviceTotal', parent).val();
        var salesTotal = $('#salesTotal', parent).val();
        var otherIncomeTotal = $('#otherIncomeTotal', parent).val();
        var total = GLOBAL.Number.filterZero($('#total', parent).val());

        $('#serviceTotal').text(GLOBAL.Number.filterZero(serviceTotal));
        $('#salesTotal').text(GLOBAL.Number.filterZero(salesTotal));
        $('#otherTotal').text(GLOBAL.Number.filterZero(otherIncomeTotal));
        $('#orderTotal').text(total);
        $('#settledAmount').val(total);
        $('#totalLabel').text(total);
    }

    $('#confirmBtn').click(function () {
        if (!$('#confirmBtn').attr('disabled')) {
            var parent = window.parent.document.body;
            $('#settledAmount', parent).val($('#settledAmount').val());
            $('#accountDebtAmount', parent).val($('#accountDebtAmount').val());
            $('#accountDiscount', parent).val($('#accountDiscount').val());
            $('#isPrint', parent).val($('#print').attr('checked'));
            window.parent.submitForm();
        }
    });

    $('#settledAmount,#accountDebtAmount,#accountDiscount').bind('input',function () {
        $(this).val() != null && $(this).val($(this).val().replace(/[^0-9.]/g, '')).val(new RegExp('\\d+.?\\d{0,2}').exec($(this).val()));
        Number($(this).val()) > Number($('#orderTotal').text()) && $(this).val($('#orderTotal').text());
    }).bind('input',function () {
            var map = {settledAmount: true, accountDebtAmount: true, accountDiscount: true};
            delete map[this.id];
            var list = [];
            for (var item in map) {
                list.push(item);
            }
            var id = null;
            if ($('#' + list[0]).val() && !$('#' + list[1]).val()) {
                id = list[0];
            }
            if (!$('#' + list[0]).val() && $('#' + list[1]).val()) {
                id = list[1];
            }
            if (id) {
                $('#' + id).val(GLOBAL.Number.filterZero(Number($('#orderTotal').text()) - Number($(this).val())));
            } else {
                if (this.id == 'settledAmount') {
                    var val = GLOBAL.Number.filterZero(Number($('#orderTotal').text()) - Number($('#settledAmount').val()) - Number($('#accountDiscount').val()));
                    $('#accountDebtAmount').val(val < 0 ? 0 : val);
                } else if (this.id == 'accountDebtAmount') {
                    var val = GLOBAL.Number.filterZero(Number($('#orderTotal').text()) - Number($('#settledAmount').val()) - Number($('#accountDebtAmount').val()));
                    $('#accountDiscount').val(val < 0 ? 0 : val);
                }
            }
        }).bind('input',function () {
            var total1 = GLOBAL.Number.filterZero(Number($('#settledAmount').val()) + Number($('#accountDebtAmount').val()) + Number($('#accountDiscount').val()));
            $('#totalLabel').text(total1);
            var total2 = Number($('#orderTotal').text());
            if (total1 > total2) {
                $('#errorInfo').text('合计金额大于应收金额').show();
                $('#confirmBtn').attr('disabled', true);
            } else if (total1 < total2) {
                $('#confirmBtn').attr('disabled', true);
                $('#errorInfo').text('合计金额小于应收金额').show();
            } else {
                $('#confirmBtn').attr('disabled', false);
                $('#errorInfo').text('').hide();
            }
        }).bind('dblclick', function () {
            $('#settledAmount,#accountDebtAmount,#accountDiscount').val('');
            $('#confirmBtn').attr('disabled', false);
            $(this).val($('#orderTotal').text());
        });

    var closeDialog = function () {
        var parent = window.parent.document.body;
        $('#iframe_PopupBox_account', parent).add('#mask', parent).hide();
    }

    $('#div_close').add('#cancelBtn').bind('click', closeDialog);
});