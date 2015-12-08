-- repair_order_service更新service_id
UPDATE repair_order_service ros, service s
SET ros.service_id = s.id
WHERE ros.service = s.name
AND (ros.service_id is null OR ros.service_id = 0);

-- debt.debt_status_enum,  debt.service_type_enum
UPDATE debt SET status_enum='ARREARS' WHERE status='欠款';
UPDATE debt SET status_enum='SETTLED' WHERE status='结清';
UPDATE debt SET status_enum='REPEAL' WHERE status='作废';

UPDATE debt SET order_type_enum='REPAIR' WHERE orderType='1';
UPDATE debt SET order_type_enum='SALE' WHERE orderType='2';
UPDATE debt SET order_type_enum='WASH' WHERE orderType='3';

-- purchase_inventory.status_enum
UPDATE purchase_inventory SET status_enum='PURCHASE_INVENTORY_DONE' WHERE status=1;
UPDATE purchase_inventory SET status_enum='PURCHASE_INVENTORY_REPEAL' WHERE status=2;

-- purchase_order.status_enum
UPDATE purchase_order SET status_enum='PURCHASE_ORDER_WAITING' WHERE status=1;
UPDATE purchase_order SET status_enum='PURCHASE_ORDER_DONE' WHERE status=2;
UPDATE purchase_order SET status_enum='PURCHASE_ORDER_REPEAL' WHERE status=3;

-- receivable.order_type_enum, receivable.status_enum
UPDATE receivable SET order_type_enum='SALE' WHERE order_type=3;
UPDATE receivable SET order_type_enum='REPAIR' WHERE order_type=4;

UPDATE receivable SET status_enum='FINISH' WHERE status=0;
UPDATE receivable SET status_enum='REPEAL' WHERE status=1;

-- repair_order.status_enum, repair_order.service_type_enum
UPDATE repair_order SET status_enum='REPAIR_DISPATCH' WHERE status=1;
UPDATE repair_order SET status_enum='REPAIR_DONE' WHERE status=2;
UPDATE repair_order SET status_enum='REPAIR_SETTLED' WHERE status=3;
UPDATE repair_order SET status_enum='REPAIR_REPEAL' WHERE status=4;

UPDATE repair_order SET service_type_enum='REPAIR' WHERE service_type='1';
UPDATE repair_order SET service_type_enum='SALE' WHERE service_type='2';
UPDATE repair_order SET service_type_enum='WASH' WHERE service_type='3';

-- repair_remind_event.event_type_enum
UPDATE repair_remind_event SET event_type_enum='PENDING' WHERE event_type=1;
UPDATE repair_remind_event SET event_type_enum='LACK' WHERE event_type=2;
UPDATE repair_remind_event SET event_type_enum='DEBT' WHERE event_type=3;
UPDATE repair_remind_event SET event_type_enum='FINISH' WHERE event_type=4;
UPDATE repair_remind_event SET event_type_enum='INCOMING' WHERE event_type=5;

-- repeal_order.order_type_enum,  repeal_order.status_enum
UPDATE repeal_order SET order_type_enum='PURCHASE' WHERE order_type='1';
UPDATE repeal_order SET order_type_enum='INVENTORY' WHERE order_type='2';
UPDATE repeal_order SET order_type_enum='SALE' WHERE order_type='3';
UPDATE repeal_order SET order_type_enum='REPAIR' WHERE order_type='4';
UPDATE repeal_order SET order_type_enum='WASH' WHERE order_type='5';
UPDATE repeal_order SET order_type_enum='RETURN' WHERE order_type='8';

UPDATE repeal_order SET status_enum='PURCHASE_ORDER_WAITING' WHERE status=1 AND order_type='1';
UPDATE repeal_order SET status_enum='PURCHASE_ORDER_DONE' WHERE status=2 AND order_type='1';
UPDATE repeal_order SET status_enum='PURCHASE_ORDER_REPEAL' WHERE status=3 AND order_type='1';
UPDATE repeal_order SET status_enum='PURCHASE_INVENTORY_DONE' WHERE status=1 AND order_type='2';
UPDATE repeal_order SET status_enum='PURCHASE_INVENTORY_REPEAL' WHERE status=2 AND order_type='2';
UPDATE repeal_order SET status_enum='SALE_DONE' WHERE status=1 AND order_type='3';
UPDATE repeal_order SET status_enum='SALE_REPEAL' WHERE status=2 AND order_type='3';
UPDATE repeal_order SET status_enum='REPAIR_DISPATCH' WHERE status=1 AND order_type='4';
UPDATE repeal_order SET status_enum='REPAIR_DONE' WHERE status=2 AND order_type='4';
UPDATE repeal_order SET status_enum='REPAIR_SETTLED' WHERE status=3 AND order_type='4';
UPDATE repeal_order SET status_enum='REPAIR_REPEAL' WHERE status=4 AND order_type='4';
UPDATE repeal_order SET status_enum='WASH_SETTLED' WHERE status=3 AND (order_type='5' or order_type='7' or order_type='9');

-- sales_order.status_enum
UPDATE sales_order SET status_enum='SALE_DONE' WHERE status=1;
UPDATE sales_order SET status_enum='SALE_REPEAL' WHERE status=2;

-- wash_order.order_type_enum
UPDATE wash_order SET order_type_enum='RECHARGE' WHERE order_type=0;
UPDATE wash_order SET order_type_enum='WASH_MEMBER' WHERE order_type=1;
UPDATE wash_order SET order_type_enum='WASH' WHERE order_type=2;

UPDATE print_template SET order_type_enum='PURCHASE' WHERE order_type='采购单';
UPDATE print_template SET order_type_enum='INVENTORY' WHERE order_type='入库单';
UPDATE print_template SET order_type_enum='SALE' WHERE order_type='销售单';
UPDATE print_template SET order_type_enum='REPAIR' WHERE order_type='施工单';
UPDATE print_template SET order_type_enum='WASH' WHERE order_type='洗车单';
UPDATE print_template SET order_type_enum='RETURN' WHERE order_type='退货单';
UPDATE print_template SET order_type_enum='DEBT' WHERE order_type='欠款结算单';
UPDATE print_template SET order_type_enum='BIZSTAT' WHERE order_type='营收统计单';
UPDATE print_template SET order_type_enum='WASH_TICKET' WHERE order_type='洗车小票';

UPDATE shop_print_template SET order_type_enum='PURCHASE' WHERE order_type='采购单';
UPDATE shop_print_template SET order_type_enum='INVENTORY' WHERE order_type='入库单';
UPDATE shop_print_template SET order_type_enum='SALE' WHERE order_type='销售单';
UPDATE shop_print_template SET order_type_enum='REPAIR' WHERE order_type='施工单';
UPDATE shop_print_template SET order_type_enum='WASH' WHERE order_type='洗车单';
UPDATE shop_print_template SET order_type_enum='RETURN' WHERE order_type='退货单';
UPDATE shop_print_template SET order_type_enum='DEBT' WHERE order_type='欠款结算单';
UPDATE shop_print_template SET order_type_enum='BIZSTAT' WHERE order_type='营收统计单';
UPDATE shop_print_template SET order_type_enum='WASH_TICKET' WHERE order_type='洗车小票';
