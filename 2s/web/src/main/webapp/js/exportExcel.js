function initDownloadUI(exportResult) {
    var htm = '<div id="downloadExportExcel">';
    htm += '系统最多一次可导出' + exportResult.totalRowsPerExcel + '条数据，您共有' + exportResult.totalNum + '条，系统将按顺序自动为您分为' + exportResult.fileNum + '个文件，请选择需导出的文件：';
    htm += '<div class="height"></div><div><table style="width:100%"><colgroup><col width="250"><col></colgroup>';
    for(var i = 1; i < exportResult.exportFileDTOList.length + 1; i++) {
       if('INVENTORY' == exportResult.exportScene) {
           htm += '<tr><td>' + '文 件 ' + i + ' : <a class="blue_col"  href="download.do?method=downloadExportFile&exportFileName=库存信息(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">库 存 信 息 ( ' + i + ' )</a></td><td class="blue_col"><a href="download.do?method=downloadExportFile&exportFileName=库存信息(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">导 出</a></td></tr>';
       } else if('CUSTOMER' == exportResult.exportScene) {
          htm += '<tr><td>' + '文 件 ' + i + ' : <a class="blue_col"  href="download.do?method=downloadExportFile&exportFileName=会员信息(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">会 员 信 息 ( ' + i + ' )</a></td><td class="blue_col"><a href="download.do?method=downloadExportFile&exportFileName=会员信息(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">导 出</a></td></tr>';
       } else if('ORDER' == exportResult.exportScene) {
           htm += '<tr><td>' + '文 件 ' + i + ' : <a class="blue_col"  href="download.do?method=downloadExportFile&exportFileName=单据信息(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">单 据 信 息 ( ' + i + ' )</a></td><td class="blue_col"><a href="download.do?method=downloadExportFile&exportFileName=单据信息(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">导 出</a></td></tr>';
       } else if('CUSTOMER_TRANSACTION' == exportResult.exportScene) {
           htm += '<tr><td>' + '文 件 ' + i + ' : <a class="blue_col"  href="download.do?method=downloadExportFile&exportFileName=客户交易统计(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">客 户 交 易 统 计 ( ' + i + ' )</a></td><td class="blue_col"><a href="download.do?method=downloadExportFile&exportFileName=客户交易统计(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">导 出</a></td></tr>';
       } else if('REPAIR_BUSINESS_STAT' == exportResult.exportScene) {
           htm += '<tr><td>' + '文 件 ' + i + ' : <a class="blue_col"  href="download.do?method=downloadExportFile&exportFileName=车辆施工营业统计(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">车辆施工营业统计 ( ' + i + ' )</a></td><td class="blue_col"><a href="download.do?method=downloadExportFile&exportFileName=车辆施工营业统计(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">导 出</a></td></tr>';
       } else if('SALES_BUSINESS_STAT' == exportResult.exportScene) {
           htm += '<tr><td>' + '文 件 ' + i + ' : <a class="blue_col"  href="download.do?method=downloadExportFile&exportFileName=商品销售营业统计(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">商品销售营业统计 ( ' + i + ' )</a></td><td class="blue_col"><a href="download.do?method=downloadExportFile&exportFileName=商品销售营业统计(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">导 出</a></td></tr>';
       } else if('WASH_BUSINESS_STAT' == exportResult.exportScene) {
           htm += '<tr><td>' + '文 件 ' + i + ' : <a class="blue_col"  href="download.do?method=downloadExportFile&exportFileName=洗车营业统计(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">洗车营业统计 ( ' + i + ' )</a></td><td class="blue_col"><a href="download.do?method=downloadExportFile&exportFileName=洗车营业统计(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">导 出</a></td></tr>';
       } else if("REPAIR_ASSISTANT_STAT" == exportResult.exportScene) {
           htm += '<tr><td>' + '文 件 ' + i + ' : <a class="blue_col"  href="download.do?method=downloadExportFile&exportFileName=员工业绩统计-车辆施工(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">员工业绩统计-车辆施工 ( ' + i + ' )</a></td><td class="blue_col"><a href="download.do?method=downloadExportFile&exportFileName=员工业绩统计-车辆施工(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">导 出</a></td></tr>';
       } else if("WASH_ASSISTANT_STAT" == exportResult.exportScene) {
           htm += '<tr><td>' + '文 件 ' + i + ' : <a class="blue_col"  href="download.do?method=downloadExportFile&exportFileName=员工业绩统计-洗车美容(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">员工业绩统计-洗车美容 ( ' + i + ' )</a></td><td class="blue_col"><a href="download.do?method=downloadExportFile&exportFileName=员工业绩统计-洗车美容(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">导 出</a></td></tr>';
       } else if("SALES_ASSISTANT_STAT" == exportResult.exportScene) {
           htm += '<tr><td>' + '文 件 ' + i + ' : <a class="blue_col"  href="download.do?method=downloadExportFile&exportFileName=员工业绩统计-商品销售(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">员工业绩统计-商品销售 ( ' + i + ' )</a></td><td class="blue_col"><a href="download.do?method=downloadExportFile&exportFileName=员工业绩统计-商品销售(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">导 出</a></td></tr>';
       } else if("MEMBER_ASSISTANT_STAT" == exportResult.exportScene) {
           htm += '<tr><td>' + '文 件 ' + i + ' : <a class="blue_col"  href="download.do?method=downloadExportFile&exportFileName=员工业绩统计-会员卡销售(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">员工业绩统计-会员卡销售 ( ' + i + ' )</a></td><td class="blue_col"><a href="download.do?method=downloadExportFile&exportFileName=员工业绩统计-会员卡销售(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">导 出</a></td></tr>';
       } else if("BUSINESS_ACCOUNT_ASSISTANT_STAT" == exportResult.exportScene) {
           htm += '<tr><td>' + '文 件 ' + i + ' : <a class="blue_col"  href="download.do?method=downloadExportFile&exportFileName=员工业绩统计-营业外收入(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">员工业绩统计-营业外收入 ( ' + i + ' )</a></td><td class="blue_col"><a href="download.do?method=downloadExportFile&exportFileName=员工业绩统计-营业外收入(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">导 出</a></td></tr>';
       } else if("CUSTOMER_REMIND" == exportResult.exportScene) {
           htm += '<tr><td>' + '文 件 ' + i + ' : <a class="blue_col"  href="download.do?method=downloadExportFile&exportFileName=客户提醒服务(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">客户提醒服务 ( ' + i + ' )</a></td><td class="blue_col"><a href="download.do?method=downloadExportFile&exportFileName=客户提醒服务(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">导 出</a></td></tr>';
       }else if("VEHICLE_LIST" == exportResult.exportScene) {
           htm += '<tr><td>' + '文 件 ' + i + ' : <a class="blue_col"  href="download.do?method=downloadExportFile&exportFileName=车辆信息(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">车辆信息 ( ' + i + ' )</a></td><td class="blue_col"><a href="download.do?method=downloadExportFile&exportFileName=车辆信息(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">导 出</a></td></tr>';
       }else if("SHOP_FAULT_INFO" == exportResult.exportScene) {
           htm += '<tr><td>' + '文 件 ' + i + ' : <a class="blue_col"  href="download.do?method=downloadExportFile&exportFileName=事故故障提醒(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">事故故障提醒 ( ' + i + ' )</a></td><td class="blue_col"><a href="download.do?method=downloadExportFile&exportFileName=事故故障提醒(' + i + ').xls&exportFileId=' + exportResult.exportFileDTOList[i-1].idStr + '">导 出</a></td></tr>';
       }

    }
    htm += '</table></div>';
    htm += '</div>';
    return htm;
}

function showDownLoadUI(exportResult) {
    var htm = initDownloadUI(exportResult);
    $(htm).dialog({
        autoOpen:true,
        resizable: false,
        title:"友情提示",
        height:200,
        width:400,
        modal: true,
        closeOnEscape: false,
        showButtonPanel:true,
        buttons:[ { text: "关闭", "class": "closeButton" ,click: function() { $( this ).dialog( "close" ); } } ]
    });
}



