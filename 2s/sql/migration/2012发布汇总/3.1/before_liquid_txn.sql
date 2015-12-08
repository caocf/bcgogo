-- 为将全部表中version字段改为NOT NULL作准备
UPDATE service SET version = 0 WHERE version IS NULL;