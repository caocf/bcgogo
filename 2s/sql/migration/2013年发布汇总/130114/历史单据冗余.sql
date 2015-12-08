-- TXN schema
-- 第一条product_history
insert into product_history(id,created,last_update,version,product_id,uuid,name,name_en,state,memo,kind_id,kind_name,brand,model,spec,mfr,mfr_en,origin_no,product_vehicle_status,origin,unit,shop_id,first_letter,first_letter_combination,product_vehicle_brand,product_vehicle_model,product_vehicle_year,product_vehicle_engine,parent_id,check_status,barcode,commodity_code,status,product_version,product_local_info_id,storage_unit,sell_unit,rate,percentage,percentage_amount,points_exchangeable,storage_bin,trade_price,business_category_id,business_category_name,product_local_info_version,inventory_amount,lower_limit,upper_limit,no_order_inventory,sales_price,latest_inventory_price,inventory_average_price,inventory_version)
select
pli.id, pli.created, i.last_update, 0, p.id as product_id, p.uuid,p.name,p.name_en,p.state, p.memo, p.kind_id,k.`name` as kind_name, p.brand, p.model, p.spec, p.mfr, p.mfr_en, p.origin_no, p.product_vehicle_status, p.origin, p.unit, p.shop_id, p.first_letter, p.first_letter_combination, p.product_vehicle_brand, p.product_vehicle_model,p.product_vehicle_year,p.product_vehicle_engine, p.parent_id, p.check_status, p.barcode, p.commodity_code, p.status, p.version as product_version,
pli.id as product_local_info_id, pli.storage_unit, pli.sell_unit,pli.rate,pli.percentage,pli.percentage_amount,pli.points_exchangeable,pli.storage_bin,pli.trade_price,pli.business_category_id,cat.category_name as business_category_name,pli.version as product_local_info_version,
i.amount as inventory_amount,i.lower_limit,i.upper_limit,i.no_order_inventory,i.sales_price,i.latest_inventory_price,i.inventory_average_price,i.version as inventory_version
from product.product p inner join product.product_local_info pli on pli.product_id = p.id
inner join inventory i on i.id = pli.id
left join product.kind k on p.kind_id = k.id
left join category cat on pli.business_category_id=cat.id;

-- 第一条service_history
insert into service_history(id,created,last_update,version,service_id,history_version,shop_id,name,price,memo,percentage,percentage_amount,points_exchangeable,status,time_type)
select
s.id, s.created, s.last_update, 0, s.id, s.version as history_version, s.shop_id, s.name, s.price, s.memo, s.percentage,s.percentage_amount,s.points_exchangeable,s.status,s.time_type
from service s;

-- 采购单
update purchase_order po, bcuser.supplier s set po.supplier = s.name, po.supplier_contact = s.contact, po.supplier_mobile = s.mobile, po.supplier_landline = s.landline, po.supplier_address = s.address where po.supplier_id = s.id;
update purchase_order_item set product_history_id = product_id;

-- 入库单
update purchase_inventory pi, bcuser.supplier s set pi.supplier = s.name, pi.supplier_contact = s.contact, pi.supplier_mobile = s.mobile, pi.supplier_landline = s.landline, pi.supplier_address = s.address where pi.supplier_id = s.id;
update purchase_inventory_item set product_history_id = product_id;

-- 入库退货单
update purchase_return pr, bcuser.supplier s set pr.supplier = s.name, pr.supplier_contact = s.contact, pr.supplier_mobile = s.mobile, pr.supplier_landline = s.landline, pr.supplier_address = s.address where pr.supplier_id = s.id;
update purchase_return_item set product_history_id = product_id;

-- 销售单
update sales_order so, bcuser.customer c set so.customer=c.name, so.customer_company = c.company, so.customer_contact=c.contact, so.customer_mobile = c.mobile, so.customer_landline = c.landline, so.customer_address = c.address where so.customer_id = c.id;
update sales_order so, bcuser.member m set so.member_no = m.member_no, so.member_type=m.type, so.member_status=m.status where so.customer_id = m.customer_id;
update sales_order_item set product_history_id = product_id;

-- 施工单
update repair_order ro, bcuser.customer c set ro.customer=c.name, ro.customer_company = c.company, ro.customer_contact=c.contact, ro.customer_mobile = c.mobile, ro.customer_landline = c.landline, ro.customer_address = c.address where ro.customer_id = c.id;
update repair_order ro, bcuser.member m set ro.member_no = m.member_no, ro.member_type=m.type, ro.member_status=m.status where ro.customer_id = m.customer_id;

update repair_order ro, bcuser.vehicle v set ro.vechicle = v.licence_no, ro.vehicle_engine = v.engine, ro.vehicle_engine_no=v.engine_no, ro.vehicle_brand = v.brand, ro.vehicle_model = v.model, ro.vehicle_color = v.color, ro.vehicle_buy_date=v.car_date, ro.vehicle_chassis_no=v.chassis_number where ro.vechicle_id = v.id;

update repair_order_item set product_history_id = product_id;
update repair_order_service set service_history_id = service_id;

-- 洗车美容单 (vehicle, customer已冗余,故这2个字段不更新)
update wash_beauty_order wbo, bcuser.customer c set wbo.customer_company = c.company, wbo.customer_contact=c.contact, wbo.customer_mobile = c.mobile, wbo.customer_landline = c.landline, wbo.customer_address = c.address where wbo.customer_id = c.id;
update wash_beauty_order wbo, bcuser.member m set wbo.member_no = m.member_no, wbo.member_type=m.type, wbo.member_status=m.status where wbo.customer_id = m.customer_id;

update wash_beauty_order wbo, bcuser.vehicle v set wbo.vehicle_engine = v.engine, wbo.vehicle_engine_no=v.engine_no, wbo.vehicle_brand = v.brand, wbo.vehicle_model = v.model, wbo.vehicle_color = v.color, wbo.vehicle_buy_date=v.car_date, wbo.vehicle_chassis_no=v.chassis_number where wbo.vechicle_id = v.id;

update wash_beauty_order_item set service_history_id = service_id;

-- 销售退货单
update sales_return sr, bcuser.customer c set sr.customer = c.name, sr.customer_company = c.company, sr.customer_contact=c.contact, sr.customer_mobile = c.mobile, sr.customer_landline = c.landline, sr.customer_address = c.address where sr.customer_id = c.id;
update sales_return sr, bcuser.member m set sr.member_no = m.member_no, sr.member_type=m.type, sr.member_status=m.status where sr.customer_id = m.customer_id;
update sales_return_item set product_history_id = product_id;

-- 会员购卡续卡单
update member_card_order mco, bcuser.customer c set mco.customer = c.name, mco.customer_company = c.company, mco.customer_contact=c.contact, mco.customer_mobile = c.mobile, mco.customer_landline = c.landline, mco.customer_address = c.address where mco.customer_id = c.id;
update member_card_order_service set service_history_id = service_id;

-- 会员退卡单
update member_card_return mcr, bcuser.customer c set mcr.customer = c.name, mcr.customer_company = c.company, mcr.customer_contact=c.contact, mcr.customer_mobile = c.mobile, mcr.customer_landline = c.landline, mcr.customer_address = c.address where mcr.customer_id = c.id;
update member_card_return_service set service_history_id = service_id;

-- 库存盘点单
update inventory_check_item set product_history_id = product_id;


-- search schema
update item_index ii, txn.repair_order_service ros set ii.service_id = ros.service_id where ii.item_id = ros.id and ii.service_id is null;
update item_index ii, txn.wash_beauty_order_item woi set ii.service_id = woi.service_id where ii.item_id = woi.id and ii.service_id is null;
