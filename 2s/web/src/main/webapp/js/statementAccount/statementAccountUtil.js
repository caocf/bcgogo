/**
 * 对账单常用js
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-1-8
 * Time: 下午4:17
 * To change this template use File | Settings | File Templates.
 */

//跳转页面
function redirectCustomerBill(type) {
  if (type == "supplier") {
      window.location.href = "statementAccount.do?method=redirectSearchCustomerBill&type=supplier&customerOrSupplierIdStr=" + $("#supplierId").val();
    return;
  } else if (type == "supplierBill") {
      window.location.href = "statementAccount.do?method=redirectSearchCustomerBill&type=supplier&customerOrSupplierIdStr=" + $("#customerOrSupplierId").val();
    return;
  }

  var customerId = "";
  if (type == "customer") {
    customerId = $("#customerId").val();
  } else if (type == "customerBill") {
    customerId = $("#customerOrSupplierId").val();
  }
  if (customerId && customerId != "null") {
      window.location.href = "statementAccount.do?method=redirectSearchCustomerBill&type=customer&customerOrSupplierIdStr=" + customerId;
  }
}


//跳转到客户详细信息页面
function redirectUncleUser(type) {
  if (type == "supplier") {
    if ($("#supplierId").val()) {
      window.location = "unitlink.do?method=supplier&supplierId=" + $("#supplierId").val();
    }
    return;
  } else if (type == "supplierBill") {
    if ($("#customerOrSupplierId").val()) {
      window.location = "unitlink.do?method=supplier&supplierId=" + $("#customerOrSupplierId").val();
    }
    return;
  }

  var customerId = "";
  if (type == "customer") {
    customerId = $("#customerId").val();
  } else if (type == "customerBill") {
    customerId = $("#customerOrSupplierId").val();
  }
  if (customerId && customerId != "null") {
    window.location = "unitlink.do?method=customer&customerId=" + customerId;
  }
}

function openStatementOrder(orderId) {
  window.open("statementAccount.do?method=showStatementAccountOrderById&statementOrderId=" + orderId);
}
function openSalesReturn(orderId) {
  window.open("salesReturn.do?method=showSalesReturnOrderBySalesReturnOrderId&salesReturnOrderId=" + orderId);
}


function redirectCurrentStatement(customerOrSupplierId, orderType) {
  if (customerOrSupplierId && customerOrSupplierId != "null") {
    window.location.href = "statementAccount.do?method=redirectCreateCustomerBill&customerOrSupplierIdStr=" + customerOrSupplierId + "&orderType=" + orderType;
  }
}

function redirectReceivable() {
  window.open("arrears.do?method=toReceivableStat");
}

function redirectPayable() {
  window.open("arrears.do?method=toPayableStat");
}

function redirectPrintBill() {
  if ($("#id").val()) {
    window.showModalDialog("statementAccount.do?method=print&id=" + $("#id").val() + "&now=" + new Date(), '', "dialogWidth=1024px;dialogHeight=768px");
    return;
  }
}