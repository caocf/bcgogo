
update search.order_index so,txn.sales_order ts set so.receipt_no = ts.receipt_no where so.order_id = ts.id;

update search.order_index so,txn.purchase_order ts set so.receipt_no = ts.receipt_no where so.order_id = ts.id;

update search.order_index so,txn.purchase_inventory ts set so.receipt_no = ts.receipt_no where so.order_id = ts.id;

update search.order_index so,txn.repair_order ts set so.receipt_no = ts.receipt_no where so.order_id = ts.id;

update search.order_index so,txn.purchase_return ts set so.receipt_no = ts.receipt_no where so.order_id = ts.id;

update search.order_index so,txn.wash_beauty_order ts set so.receipt_no = ts.receipt_no where so.order_id = ts.id;
