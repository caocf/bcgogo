UPDATE txn.sales_order SET status=1 WHERE status is null;
UPDATE txn.debt SET status='欠款' WHERE status is null;