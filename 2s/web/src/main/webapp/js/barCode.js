function barcodeSearchProduct(idPrefix, barcode) {
    $.ajax({
            type:"POST",
            url:"searchInventoryIndex.do?method=ajaxSearchBarcode",
            data:{barcode:barcode},
            dataType:"json",
            async:false,
            cache:false,
            success:function (data) {
                initProduct(idPrefix, data);
            },
            error:function() {
                barcodeNotExist(idPrefix, barcode);
            }
        }
    );
}

function initProduct(idPrefix, data) {
    if (data.barcode != "") {
        var idPrefixSame = "";
        idPrefixSame = checkBarCode(idPrefix, data);
        if (idPrefixSame != "") {//检查重复商品
            document.getElementById(idPrefixSame + ".amount").value = document.getElementById(idPrefixSame + ".amount").value * 1 + 1;
            document.getElementById(idPrefixSame + ".total").value = document.getElementById(idPrefixSame + ".purchasePrice").value *
                document.getElementById(idPrefixSame + ".amount").value;
            document.getElementById(idPrefix + ".productName").value = "";
        } else {   //新增商品      ($(idPrefix + ".amount").value != "" || $(idPrefix + ".amount").value != 0)
            document.getElementById(idPrefix + ".productName").value = data.productName;
            document.getElementById(idPrefix + ".brand").value = data.productBrand;
            document.getElementById(idPrefix + ".spec").value = data.productSpec;
            document.getElementById(idPrefix + ".model").value = data.productModel;
            document.getElementById(idPrefix + ".vehicleBrand").value = data.vehicleBrand;
            document.getElementById(idPrefix + ".vehicleModel").value = data.vehicleModel;
            document.getElementById(idPrefix + ".vehicleYear").value = data.vehicleYear;
            document.getElementById(idPrefix + ".vehicleEngine").value = data.vehicleEngine;
            document.getElementById(idPrefix + ".purchasePrice").value = data.purchasePrice;
            document.getElementById(idPrefix + ".amount").value = 1;
            document.getElementById(idPrefix + ".total").value = data.purchasePrice;
            document.getElementById(idPrefix + ".barcode").value = data.barcode;
            document.getElementById(idPrefix + ".barcode").type="hidden";
            if (data.inventoryAmount) {
                document.getElementById(idPrefix + ".inventoryAmount").value = data.inventoryAmount;
                document.getElementById(idPrefix + ".plusbutton").click();
                var NextidPrefix = idPrefix.substring(0, 8) + (idPrefix.substring(8) * 1 + 1);
                document.getElementById(NextidPrefix + ".productName").focus();
            } else {
                document.getElementById(idPrefix + ".purchasePrice").value = "";
                document.getElementById(idPrefix + ".inventoryAmount").style.display = "none";
                var parentNode = window.parent.document.getElementById(idPrefix + ".inventoryAmount").parentNode;
                var childrenCnt = parentNode.childNodes.length;
                for (var i = 0; i < childrenCnt; i++) {
                    var childNode = parentNode.childNodes[i];
                    if (childNode.tagName.toLowerCase() == "span") {
                        childNode.style.display = "block";
                    }
                }
                document.getElementById(idPrefix + ".purchasePrice").focus();
            }
        }
    }
}
function checkBarCode(idPrefix, data) {   //重复返回重复行前缀
    var varCode = data.barcode;
    var idPrefixSame = "";
    $(".item").not($(".item").last()).each(function() {
        if (varCode == $(this).find("td:first>input").eq(0).val()) {
            idPrefixSame = $(this).find("td:first>input").eq(0).attr("id").substring(0, 9);
        }
    });
    return idPrefixSame;
}
function barcodeNotExist(idPrefix, barcode) {
    var data = new Array();
    data["barcode"] = barcode;
    var idPrefixSame = "";
    idPrefixSame = checkBarCode(idPrefix, data);
    if (idPrefixSame != "") {//检查重复商品
        document.getElementById(idPrefixSame + ".amount").value = document.getElementById(idPrefixSame + ".amount").value * 1 + 1;
        document.getElementById(idPrefix + ".productName").value = "";
        document.getElementById(idPrefix + ".productName").focus();
    } else {
        alert("该条码"+barcode+"信息还未收录，请手动添加商品信息。");
        document.getElementById(idPrefix + ".barcode").value = barcode;
        if(!document.getElementById(idPrefix+".barcodeSpan")){
        var span=document.createElement("span");
        span.setAttribute("id",idPrefix+".barcodeSpan");
        span.innerHTML=barcode;
        document.getElementById(idPrefix+".barcode").parentNode.appendChild(span);
        }else{
          document.getElementById(idPrefix+".barcodeSpan").innerHTML=barcode;
        }
        document.getElementById(idPrefix + ".productName").value = "";
        document.getElementById(idPrefix + ".productName").focus();
    }
}