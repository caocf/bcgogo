DELIMITER $$

CREATE
    TRIGGER updateCustomerRecordFllowCustomer AFTER UPDATE
    ON customer
    FOR EACH ROW BEGIN
	UPDATE customer_record  SET NAME= new.name,mobile=new.mobile WHERE customer_id = new.id;
    END$$

DELIMITER ;