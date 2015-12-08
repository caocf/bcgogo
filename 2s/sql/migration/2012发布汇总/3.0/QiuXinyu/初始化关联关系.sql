update  customer c,config.shop s set c.relation_type = 'SELF_RELATED' where c.shop_id = s.id and s.type in ( 'WHOLESALER_SHOP','TXN_SHOP') and c.customer_shop_id is not null;
update  customer c,config.shop s set c.relation_type = 'UNRELATED' where c.shop_id = s.id and s.type in ('WHOLESALER_SHOP','TXN_SHOP') and c.customer_shop_id is null;

update  supplier sp,config.shop s set sp.relation_type = 'SELF_RELATED' where sp.shop_id = s.id and s.type in('INTEGRATED_SHOP','REPAIR_SHOP','WASH_SHOP','ADVANCED_SHOP') and sp.supplier_shop_id is not null;
update  supplier sp,config.shop s set sp.relation_type = 'UNRELATED' where sp.shop_id = s.id and s.type in('INTEGRATED_SHOP','REPAIR_SHOP','WASH_SHOP','ADVANCED_SHOP') and sp.supplier_shop_id is  null;