CREATE DATABASE inventory_system;
USE inventory_system;

CREATE TABLE role (
    role_id INT PRIMARY KEY,
    role_name VARCHAR(20) UNIQUE NOT NULL
);
INSERT INTO role VALUES
(1,'Manager'),
(2,'HOD'),
(3,'Dean');
CREATE TABLE user (
    user_id VARCHAR(5) PRIMARY KEY,
    user_name VARCHAR(30) NOT NULL,
    password VARCHAR(30) NOT NULL,
    role_id INT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES role(role_id)
);
INSERT INTO user VALUES
('H01','Rajiv Sir','pass123',2),
('D01','CK Jha Sir','pass123',3),
('M01','Himanshu sir','pass123',1);

CREATE TABLE product_type (
    ptype_id VARCHAR(5) PRIMARY KEY,
    ptype_name VARCHAR(30) UNIQUE NOT NULL
);
INSERT INTO product_type VALUES
('STN','Stationery'),
('FUR','Furniture'),
('CS','Computer sets'),
('Misc','Miscellaneous');

CREATE TABLE manager_product_type (
    user_id VARCHAR(5),
    ptype_id VARCHAR(5),
    PRIMARY KEY (user_id, ptype_id),
    FOREIGN KEY (user_id) REFERENCES user(user_id),
    FOREIGN KEY (ptype_id) REFERENCES product_type(ptype_id)
);
INSERT INTO manager_product_type VALUES
('M01','STN'),
('M01','Misc');

CREATE TABLE supplier (
    sid INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(50) UNIQUE,
    contact_no VARCHAR(15) NOT NULL,
    address VARCHAR(100) NOT NULL,
    ptype_id VARCHAR(5) NOT NULL,
    FOREIGN KEY (ptype_id) REFERENCES product_type(ptype_id)
);
CREATE TABLE product (
    pid INT AUTO_INCREMENT PRIMARY KEY,
    product_name varchar(50) NOT NULL,
    description VARCHAR(100) NOT NULL,
    qty_in_stock INT NOT NULL CHECK (qty_in_stock >= 0),
    min_qty_required INT NOT NULL CHECK (min_qty_required >= 5),
    qty_updated_date DATE NOT NULL,
    ptype_id VARCHAR(5) NOT NULL,
    FOREIGN KEY (ptype_id) REFERENCES product_type(ptype_id)
);
CREATE TABLE supplies (
    sid INT,
    pid INT,
    PRIMARY KEY (sid, pid),
    FOREIGN KEY (sid) REFERENCES supplier(sid),
    FOREIGN KEY (pid) REFERENCES product(pid)
);

CREATE TABLE bill_invoice (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    bill_no VARCHAR(20) UNIQUE NOT NULL,
    date DATE NOT NULL,
    bill_received_by VARCHAR(50) NOT NULL,
    qty_ordered INT NOT NULL CHECK (qty_ordered > 0),
    bill_amount DECIMAL(10,2) NOT NULL CHECK (bill_amount > 0),
    pid INT NOT NULL,
    sid INT NOT NULL,
    FOREIGN KEY (pid) REFERENCES product(pid),
    FOREIGN KEY (sid) REFERENCES supplier(sid)
);

CREATE TABLE issue (
    issue_id INT AUTO_INCREMENT PRIMARY KEY,
    pid INT NOT NULL,
    issue_to VARCHAR(50) NOT NULL,
    issued_by VARCHAR(50) NOT NULL,
    dept_name VARCHAR(50) NOT NULL,
    qty_issued INT NOT NULL CHECK (qty_issued > 0),
    reason VARCHAR(100),
    date DATE NOT NULL,
    FOREIGN KEY (pid) REFERENCES product(pid)
);

INSERT INTO supplies VALUES
(1,1),(2,2),(3,3),(4,4),(5,5);

INSERT INTO bill_invoice
(bill_no,date,bill_received_by,qty_ordered,bill_amount,pid,sid)
VALUES
('BILL1001','2025-01-05','Store Keeper',50,12000,1,1),
('BILL1002','2025-01-06','Store Keeper',100,24000,2,2),
('BILL1003','2025-01-07','Store Keeper',30,9000,3,3),
('BILL1004','2025-01-08','Store Keeper',40,16000,10,5);

SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM bill_invoice;
DELETE FROM supplies;
DELETE FROM product;
DELETE FROM supplier;

SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 0;

DELETE FROM supplies;

SET SQL_SAFE_UPDATES = 1;

INSERT INTO issue
(pid,issue_to,issued_by,dept_name,qty_issued,reason,date)
VALUES
(2,'Clerk','Store','Admin',20,'Office use','2025-01-15'),
(5,'Staff','Store','Exam Cell',50,'Exam work','2025-01-15');


SHOW TABLES;
SHOW TABLE STATUS;
SELECT * FROM bill_invoice;
SELECT * FROM user;
SELECT * FROM issue;
SELECT * FROM product WHERE status = 'ACTIVE';
SELECT * FROM product_type;
SELECT * FROM role;
SELECT * FROM supplier;
SELECT * FROM supplies;
SELECT * FROM manager_product_type;


SET FOREIGN_KEY_CHECKS = 0;


SET FOREIGN_KEY_CHECKS = 1;
INSERT INTO product
(product_name, description, qty_in_stock, min_qty_required, qty_updated_date, ptype_id)
VALUES
('A4 Paper','A4 size white paper',200,50,'2025-01-05','STN'),
('Ball Pen','Blue ball pen',400,100,'2025-01-05','STN'),
('Office Chair','Revolving chair',30,10,'2025-01-05','FUR'),
('Desktop PC','i5 Desktop computer',15,5,'2025-01-05','CS'),
('Calculator','Scientific calculator',45,10,'2025-01-05','Misc');

INSERT INTO supplier
(name, email, contact_no, address, ptype_id)
VALUES
('Stationery Hub','stat@hub.com','9876543210','Delhi','STN'),
('Pen World','pen@world.com','9876543211','Noida','STN'),
('Furniture House','furn@house.com','9876543212','Gurgaon','FUR'),
('Tech Supplies','tech@sup.com','9876543213','Delhi','CS'),
('Office Mart','office@mart.com','9876543214','Noida','Misc');

INSERT INTO supplies (sid, pid) VALUES
(1,1),
(1,2),
(3,3),
(4,4),
(5,5);

INSERT INTO bill_invoice
(bill_no, date, bill_received_by, qty_ordered, bill_amount, pid, sid)
VALUES
('B2001','2025-01-06','Store Clerk',100,5000,1,1),
('B2002','2025-01-06','Store Clerk',200,4000,2,1),
('B2003','2025-01-06','Store Clerk',10,15000,3,3),
('B2004','2025-01-06','Store Clerk',5,30000,4,4),
('B2005','2025-01-06','Store Clerk',20,6000,5,5);

CREATE TABLE return_table (
    return_id INT AUTO_INCREMENT PRIMARY KEY,
	ptype_id VARCHAR(5) NOT NULL,
    issue_id INT NOT NULL,
    pid INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    return_to VARCHAR(50) NOT NULL,
    date DATE NOT NULL,
	FOREIGN KEY (ptype_id) REFERENCES product_type(ptype_id),
    FOREIGN KEY (issue_id) REFERENCES issue(issue_id),
    FOREIGN KEY (pid) REFERENCES product(pid)
);

ALTER TABLE product
ADD status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE';

DELIMITER $$

CREATE TRIGGER trg_prevent_inactive_product_issue
BEFORE INSERT ON issue
FOR EACH ROW
BEGIN
    DECLARE p_status VARCHAR(10);

    SELECT status INTO p_status
    FROM product
    WHERE pid = NEW.pid;

    IF p_status = 'INACTIVE' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Cannot issue an inactive product';
    END IF;
END$$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER trg_prevent_inactive_product_bill
BEFORE INSERT ON bill_invoice
FOR EACH ROW
BEGIN
    DECLARE p_status VARCHAR(10);

    SELECT status INTO p_status
    FROM product
    WHERE pid = NEW.pid;

    IF p_status = 'INACTIVE' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Cannot bill an inactive product';
    END IF;
END$$

DELIMITER ;

SET SQL_SAFE_UPDATES = 0;

UPDATE user
SET user_id = TRIM(user_id),
    password = TRIM(password);

SET SQL_SAFE_UPDATES = 1;

SELECT CONCAT('[', user_id, ']') AS uid,
       LENGTH(user_id) AS uid_len,
       CONCAT('[', password, ']') AS pwd,
       LENGTH(password) AS pwd_len
FROM user;

ALTER TABLE user
ADD COLUMN secret_question VARCHAR(255),
ADD COLUMN secret_answer VARCHAR(255);

UPDATE user
SET secret_question = 'What is your senior secondary school name?',
    secret_answer = 'xyz'
WHERE user_id = 'M01';

UPDATE user
SET secret_question = 'What is your senior secondary school name?',
    secret_answer = 'abc'
WHERE user_id = 'D01';

UPDATE user
SET secret_question = 'What is your senior secondary school name?',
    secret_answer = 'def'
WHERE user_id = 'H01';

SHOW INDEX FROM supplier;

ALTER TABLE supplier
DROP INDEX email;

ALTER TABLE supplier DROP INDEX email_UNIQUE;

SHOW CREATE TABLE supplier;

ALTER TABLE bill_invoice
ADD COLUMN display_label VARCHAR(120);

SET SQL_SAFE_UPDATES = 0;
 
UPDATE bill_invoice
SET display_label = CONCAT(
    'Invoice#', bill_no,
    ' | Qty:', qty_ordered,
    ' | ₹', bill_amount
);

SET SQL_SAFE_UPDATES = 1;

DELIMITER $$

CREATE TRIGGER trg_mark_product_inactive_after_bill_delete
AFTER DELETE ON bill_invoice
FOR EACH ROW
BEGIN
    DECLARE remaining INT;

    SELECT COUNT(*)
    INTO remaining
    FROM bill_invoice
    WHERE pid = OLD.pid;

    IF remaining = 0 THEN
        UPDATE product
        SET status = 'INACTIVE'
        WHERE pid = OLD.pid;
    END IF;
END$$

DELIMITER ;

CREATE INDEX idx_product_status ON product(status);

ALTER TABLE product
ADD COLUMN initial_qty INT NOT NULL AFTER qty_in_stock;
SET SQL_SAFE_UPDATES = 0;

UPDATE product
SET initial_qty = qty_in_stock;

SET SQL_SAFE_UPDATES = 1;


DELIMITER $$

CREATE TRIGGER set_initial_qty
BEFORE INSERT ON product
FOR EACH ROW
BEGIN
    IF NEW.initial_qty IS NULL THEN
        SET NEW.initial_qty = NEW.qty_in_stock;
    END IF;
END$$

DELIMITER ;

