-- item_index.order_type_enum
UPDATE item_index SET order_type_enum='PURCHASE' where order_type='1';
UPDATE item_index SET order_type_enum='INVENTORY' where order_type='2';
UPDATE item_index SET order_type_enum='SALE' where order_type='3';
UPDATE item_index SET order_type_enum='REPAIR' where order_type='4';
UPDATE item_index SET order_type_enum='WASH' where order_type='5';
UPDATE item_index SET order_type_enum='RECHARGE' where order_type='7';
UPDATE item_index SET order_type_enum='REPAIR_SALE' where order_type='6';
UPDATE item_index SET order_type_enum='RETURN' where order_type='8';
UPDATE item_index SET order_type_enum='WASH_MEMBER' where order_type='9';

-- item_index.item_type_enum
UPDATE item_index SET item_type_enum='SERVICE' WHERE item_type='1';
UPDATE item_index SET item_type_enum='MATERIAL' WHERE item_type='2';
UPDATE item_index SET item_type_enum='WASH' WHERE item_type='3';
UPDATE item_index SET item_type_enum='RECHARGE' WHERE item_type='4';
UPDATE item_index SET item_type_enum='WASH_MEMBER' WHERE item_type='5';

-- item_index.order_status_enum
UPDATE item_index SET order_status_enum='PURCHASE_ORDER_WAITING' WHERE order_status='1' AND order_type='1';
UPDATE item_index SET order_status_enum='PURCHASE_ORDER_DONE' WHERE order_status='2' AND order_type='1';
UPDATE item_index SET order_status_enum='PURCHASE_ORDER_REPEAL' WHERE order_status='3' AND order_type='1';
UPDATE item_index SET order_status_enum='PURCHASE_INVENTORY_DONE' WHERE order_status='1' AND order_type='2';
UPDATE item_index SET order_status_enum='PURCHASE_INVENTORY_REPEAL' WHERE order_status='2' AND order_type='2';
UPDATE item_index SET order_status_enum='SALE_DONE' WHERE order_status='1' AND order_type='3';
UPDATE item_index SET order_status_enum='SALE_REPEAL' WHERE order_status='2' AND order_type='3';
UPDATE item_index SET order_status_enum='REPAIR_DISPATCH' WHERE order_status='1' AND order_type='4';
UPDATE item_index SET order_status_enum='REPAIR_DONE' WHERE order_status='2' AND order_type='4';
UPDATE item_index SET order_status_enum='REPAIR_SETTLED' WHERE order_status='3' AND order_type='4';
UPDATE item_index SET order_status_enum='REPAIR_REPEAL' WHERE order_status='4' AND order_type='4';
UPDATE item_index SET order_status_enum='WASH_SETTLED' WHERE order_status='3' AND (order_type='5' or order_type='7' or order_type='9');


-- order_index.order_type_enum
UPDATE order_index SET order_type_enum='PURCHASE' where order_type='1';
UPDATE order_index SET order_type_enum='INVENTORY' where order_type='2';
UPDATE order_index SET order_type_enum='SALE' where order_type='3';
UPDATE order_index SET order_type_enum='REPAIR' where order_type='4';
UPDATE order_index SET order_type_enum='WASH' where order_type='5';
UPDATE order_index SET order_type_enum='RECHARGE' where order_type='7';
UPDATE order_index SET order_type_enum='REPAIR_SALE' where order_type='6';
UPDATE order_index SET order_type_enum='RETURN' where order_type='8';
UPDATE order_index SET order_type_enum='WASH_MEMBER' where order_type='9';

-- order_index.order_status_enum
UPDATE order_index SET order_status_enum='PURCHASE_ORDER_WAITING' WHERE order_status=1 AND order_type='1';
UPDATE order_index SET order_status_enum='PURCHASE_ORDER_DONE' WHERE order_status=2 AND order_type='1';
UPDATE order_index SET order_status_enum='PURCHASE_ORDER_REPEAL' WHERE order_status=3 AND order_type='1';
UPDATE order_index SET order_status_enum='PURCHASE_INVENTORY_DONE' WHERE order_status=1 AND order_type='2';
UPDATE order_index SET order_status_enum='PURCHASE_INVENTORY_REPEAL' WHERE order_status=2 AND order_type='2';
UPDATE order_index SET order_status_enum='SALE_DONE' WHERE order_status=1 AND order_type='3';
UPDATE order_index SET order_status_enum='SALE_REPEAL' WHERE order_status=2 AND order_type='3';
UPDATE order_index SET order_status_enum='REPAIR_DISPATCH' WHERE order_status=1 AND order_type='4';
UPDATE order_index SET order_status_enum='REPAIR_DONE' WHERE order_status=2 AND order_type='4';
UPDATE order_index SET order_status_enum='REPAIR_SETTLED' WHERE order_status=3 AND order_type='4';
UPDATE order_index SET order_status_enum='REPAIR_REPEAL' WHERE order_status=4 AND order_type='4';
UPDATE order_index SET order_status_enum='WASH_SETTLED' WHERE order_status=3 AND (order_type='5' or order_type='7' or order_type='9');