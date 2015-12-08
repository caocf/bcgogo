update sales_order so, bcuser.user u set so.editor=u.name where so.editor_id = u.id and so.editor is null;

-- 初始化receivable, reception_record中的结算人
update receivable set last_payee = null, last_payee_id = null;
update reception_record set payee = null, payee_id = null;

update receivable r, sales_order so
set r.last_payee = so.editor, r.last_payee_id = so.editor_id
where r.order_id=so.id and r.shop_id = so.shop_id
and r.order_type_enum = 'SALE' and r.last_payee is null;

update receivable r, sales_return sr
set r.last_payee=sr.reviewer, r.last_payee_id = sr.reviewer_id
where sr.id = r.order_id and r.shop_id = sr.shop_id
and r.order_type_enum='SALE_RETURN' and r.last_payee is null;

update reception_record rr, receivable r
set rr.payee_id = r.last_payee_id, rr.payee= r.last_payee
where rr.receivable_id = r.id and rr.payee is null;

-- purchase_inventory.editor
update bcuser.user u, purchase_inventory pi
set pi.editor= u.name
where pi.editor_id = u.id
and (pi.editor is null or pi.editor!='');

-- 初始化payable, payable_history, payable_history_record中的结算人

update payable p, purchase_inventory pi
set p.last_payer = pi.editor, p.last_payer_id = pi.editor_id
where p.purchase_inventory_id = pi.id
and p.shop_id = pi.shop_id and p.last_payer is null;

update payable_history_record phr, payable p
set phr.payer = p.last_payer, phr.payer_id = p.last_payer_id
where phr.payable_id = p.id
and phr.payer is null;


-- 初始化supplier_payable_record中的结算人
update supplier_return_payable p, purchase_return pr
set p.payee = pr.editor, p.payee_id = pr.editor_id
where p.purchase_return_id = pr.id
and p.shop_id = pr.shop_id and p.payee is null;

