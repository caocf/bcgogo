-- supplier.last_order_type_enum
UPDATE supplier SET last_order_type_enum='PURCHASE' WHERE last_order_type='采购单';
UPDATE supplier SET last_order_type_enum='INVENTORY' WHERE last_order_type='入库单';
UPDATE supplier SET last_order_type_enum='SALE' WHERE last_order_type='销售单';
UPDATE supplier SET last_order_type_enum='REPAIR' WHERE last_order_type='施工单';
UPDATE supplier SET last_order_type_enum='WASH' WHERE last_order_type='洗车单';
UPDATE supplier SET last_order_type_enum='RETURN' WHERE last_order_type='退货单';