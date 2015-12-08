-- 执行日期: 2012/8/9
-- 1. 更新所有shop_id为null的repair_order_service, 赋上shop_id
UPDATE repair_order_service ros, repair_order ro
SET ros.shop_id = ro.shop_id
WHERE ros.shop_id is null
AND ros.repair_order_id = ro.id;

-- 2. 更新repair_order_service中service为空的值(重构后),为赋上service_id作准备
UPDATE txn.repair_order_service ros, search.item_index ii
SET ros.service = ii.item_name
WHERE ros.id = ii.item_id
AND ros.service is null;

-- 3. 重新设置所有repair_order_service的service_id
UPDATE repair_order_service ros, service s
SET ros.service_id = s.id
WHERE ros.service = s.name
AND ros.shop_id = s.shop_id;

-- 4. 查看是否仍存在service_id为空的记录
SELECT * from repair_order_service WHERE service_id is null OR service_id=0;