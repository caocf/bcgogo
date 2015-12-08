-- 营业统计相关
update  txn.expend_detail set other_fee_year = 0,other_fee_month = 0,other_fee_day = 0,other_expend_year = 0,other_expend_month = 0,other_expend_day = 0;
update txn.business_stat set member_income = 0, other_income = 0;

-- 营业统计相关
ALTER TABLE biz_stat CHANGE stat_sum stat_sum double;
ALTER TABLE assistant_stat CHANGE stat_sum stat_sum double;

-- 服务初始化
update txn.repair_order_service set consume_type= 'MONEY';

update stat.assistant_stat  set member_income = 0;