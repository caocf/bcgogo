$(function () {
    $('#invalid').click(function () {
        nsDialog.jConfirm("你确定该附表作废吗？", "提示", function (val) {
            val && $.get('repairOrderSecondary.do?method=invalidRepairOrderSecondary', {repairOrderSecondaryId: $('#id').val()}, function (response) {
                if (response.success) {
                    window.location.reload();
                } else {
                    alert('操作失败！');
                }
            }, 'json');
        });
    });

    $('#settlement').click(function () {
        bcgogo.checksession({
            "parentWindow": window.parent,
            'iframe_PopupBox': $("#iframe_PopupBox_account")[0],
            'src': 'repairOrderSecondary.do?method=settleAccounts&debt=true'
        });
    });

    $('#again').click(function () {
        nsDialog.jConfirm("重录后本结算附表将作废，并且复制生成新的结算附表！是否确定继续重录操作？", "友情提示", function (val) {
            val && (window.location.href = 'repairOrderSecondary.do?method=updateRepairOrderSecondary&repairOrderSecondaryId=' + $('#id').val() + '&again=true');
        });
    });

    window.submitForm = function () {
        $.post('repairOrderSecondary.do?method=debtRepairOrderSecondary', {
            repairOrderSecondaryId: $('#id').val(),
            settledAmount: $('#settledAmount').val(),
            accountDebtAmount: $('#accountDebtAmount').val(),
            accountDiscount: $('#accountDiscount').val()
        }, function (response) {
            if (response.success) {
                var repairOrderSecondaryDTO = response.data;
                if ($('#isPrint').val() == 'true') {
                    window.showModalDialog('repairOrderSecondary.do?method=printDebtRepairOrderSecondary&repairOrderSecondaryId=' + $('#id').val());
                    window.location.href = 'repairOrderSecondary.do?method=showRepairOrderSecondary&repairOrderSecondaryId=' + repairOrderSecondaryDTO.idStr;
                } else {
                    nsDialog.jConfirm("是否需要打印结算单？", "提示", function (val) {
                        val && window.showModalDialog('repairOrderSecondary.do?method=printDebtRepairOrderSecondary&repairOrderSecondaryId=' + $('#id').val());
                        window.location.href = 'repairOrderSecondary.do?method=showRepairOrderSecondary&repairOrderSecondaryId=' + repairOrderSecondaryDTO.idStr;
                    });
                }
            } else {
                alert('操作失败！');
            }
        }, 'json');
    }

    $('#print').click(function () {
        window.showModalDialog('repairOrderSecondary.do?method=printRepairOrderSecondary&repairOrderSecondaryId=' + $('#id').val());
    });
});