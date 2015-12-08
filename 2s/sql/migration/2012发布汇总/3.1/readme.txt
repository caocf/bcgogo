更新数据库步骤:
1. 在txn Schema执行before_liquid_txn.sql
2. liquid更新数据库结构
3. 分别在相应schema执行update_bcuser.sql, update_search.sql 和 update_txn.sql
4. 发布后在admin中执行Solr的单据重新索引
5. 发布后清空print_template和shop_print_template表. 然后通知陈方雷上传新模板.