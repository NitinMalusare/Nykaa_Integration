-- Insert sample orders for testing (using INSERT IGNORE to prevent duplicate key violations)

INSERT IGNORE INTO orders (order_no, order_status, order_date, order_last_update_date, on_hold, seller_id, seller_type, created_at, updated_at) VALUES
('59878966-1', '15', '2019-06-14 16:53:44', '2020-06-03 17:47:07', '0', 'TEST4', 'DROPSHIP', NOW(), NOW()),
('53210664-1', '5', '2019-06-14 16:53:44', '2020-06-04 16:16:50', '0', 'TEST4', 'DROPSHIP', NOW(), NOW()),
('34033344-1', '8', '2019-06-14 16:53:44', '2020-06-09 13:29:29', '0', 'TEST4', 'DROPSHIP', NOW(), NOW()),
('12345678-1', '5', '2020-07-15 10:30:00', '2020-07-30 17:55:09', '0', 'mwhc2', 'DROPSHIP', NOW(), NOW()),
('87654321-1', '8', '2020-08-20 14:20:00', '2020-08-20 15:30:00', '0', 'mwhc2', 'DROPSHIP', NOW(), NOW()),
('11223344-1', '10', '2020-09-25 09:15:00', '2020-09-25 10:25:00', '0', 'mwhc2', 'DROPSHIP', NOW(), NOW()),
('55667788-1', '18', '2020-10-30 16:40:00', '2020-10-30 17:50:00', '0', 'mwhc2', 'DROPSHIP', NOW(), NOW()),
('99887766-1', '25', '2020-11-05 12:10:00', '2020-11-05 13:20:00', '1', 'mwhc2', 'DROPSHIP', NOW(), NOW()),
('11111111-1', '15', '2020-12-01 10:00:00', '2020-12-01 11:00:00', '0', 'TEST4', 'DROPSHIP', NOW(), NOW()),
('22222222-1', '10', '2020-12-15 14:00:00', '2020-12-15 15:00:00', '0', 'mwhc2', 'DROPSHIP', NOW(), NOW());

-- Insert sample JIT orders for testing
INSERT IGNORE INTO orders (order_no, order_status, order_date, order_last_update_date, on_hold, seller_id, seller_type, created_at, updated_at) VALUES
('JIT001-1', '5', '2020-06-10 10:00:00', '2020-06-10 11:00:00', '0', 'JITSELLER1', 'JIT', NOW(), NOW()),
('JIT002-1', '8', '2020-06-15 14:30:00', '2020-06-15 15:30:00', '0', 'JITSELLER1', 'JIT', NOW(), NOW()),
('JIT003-1', '10', '2020-07-01 09:00:00', '2020-07-01 10:00:00', '0', 'JITSELLER1', 'JIT', NOW(), NOW()),
('JIT004-1', '15', '2020-07-10 16:00:00', '2020-07-10 17:00:00', '0', 'JITSELLER2', 'JIT', NOW(), NOW()),
('JIT005-1', '18', '2020-08-05 12:00:00', '2020-08-05 13:00:00', '0', 'JITSELLER2', 'JIT', NOW(), NOW());

-- Insert sample Dropship orders with complete data
INSERT IGNORE INTO orders (
    order_no, order_status, order_date, order_last_update_date, on_hold, seller_id, seller_type,
    sla, customer_name, payment_method, order_amount, order_currency, order_tax_amount,
    shipping_charge, discount_amount, disc_coupon_code, store_credit, gv_amount, cod_charge,
    address1, address2, address3, phone, email, country_code, state, city, pin_code,
    bill_to_name, bill_address1, bill_address2, bill_address3, bill_phone, bill_email,
    bill_country_code, bill_state, bill_city, bill_zip_code,
    cancel_remark, reason_for_cancellation, priority, master_order_no, gstin,
    created_at, updated_at
) VALUES
('59878966-1', '15', '2019-06-14 16:53:44', '2020-06-03 17:47:07', '0', 'TEST4', 'DROPSHIP',
 NULL, 'saswati p', '1', '1479.00', 'INR', '0.00',
 '200.00', '120.00', NULL, '0.00', '0.00', '0.00',
 'flat D - 10, jrm pearl', NULL, NULL, '0085980000', 'pani@gmail.com', 'IND', 'Haryana', 'Humla', '786007',
 'saswati p', 'flat D - 10, jrm pearl', NULL, NULL, '0085980000', 'pani@gmail.com',
 'IND', 'Haryana', 'Humla', '786007',
 NULL, NULL, NULL, 'NYKL00003378', NULL,
 NOW(), NOW()),

('53210664-1', '5', '2019-06-14 16:53:44', '2020-06-04 16:16:50', '0', 'TEST4', 'DROPSHIP',
 NULL, 'Rahul Kumar', '2', '2500.00', 'INR', '100.00',
 '150.00', '200.00', 'SAVE200', '0.00', '0.00', '50.00',
 'B-101, Green Valley', 'Near City Mall', NULL, '9876543210', 'rahul@example.com', 'IND', 'Maharashtra', 'Mumbai', '400001',
 'Rahul Kumar', 'B-101, Green Valley', 'Near City Mall', NULL, '9876543210', 'rahul@example.com',
 'IND', 'Maharashtra', 'Mumbai', '400001',
 NULL, NULL, NULL, 'NYKL00003379', '27AABCU9603R1ZX',
 NOW(), NOW()),

('34033344-1', '8', '2019-06-14 16:53:44', '2020-06-09 13:29:29', '0', 'TEST4', 'DROPSHIP',
 NULL, 'Priya Sharma', '1', '3200.00', 'INR', '150.00',
 '180.00', '300.00', NULL, '50.00', '0.00', '0.00',
 'C-202, Sunshine Apartments', 'Koramangala', NULL, '9123456789', 'priya@example.com', 'IND', 'Karnataka', 'Bangalore', '560001',
 'Priya Sharma', 'C-202, Sunshine Apartments', 'Koramangala', NULL, '9123456789', 'priya@example.com',
 'IND', 'Karnataka', 'Bangalore', '560001',
 NULL, NULL, NULL, 'NYKL00003380', '29AABCT1332L1Z0',
 NOW(), NOW()),

('12345678-1', '5', '2020-07-15 10:30:00', '2020-07-30 17:55:09', '0', 'mwhc2', 'DROPSHIP',
 NULL, 'Amit Patel', '1', '1800.00', 'INR', '80.00',
 '120.00', '150.00', 'FIRST150', '0.00', '0.00', '0.00',
 '45, Park Street', 'Satellite Area', NULL, '9988776655', 'amit@example.com', 'IND', 'Gujarat', 'Ahmedabad', '380001',
 'Amit Patel', '45, Park Street', 'Satellite Area', NULL, '9988776655', 'amit@example.com',
 'IND', 'Gujarat', 'Ahmedabad', '380001',
 NULL, NULL, NULL, 'NYKL00003381', '24AABCU9603R1ZV',
 NOW(), NOW()),

('87654321-1', '8', '2020-08-20 14:20:00', '2020-08-20 15:30:00', '0', 'mwhc2', 'DROPSHIP',
 NULL, 'Sneha Reddy', '2', '4500.00', 'INR', '200.00',
 '250.00', '400.00', 'MEGA400', '100.00', '0.00', '75.00',
 '12, MG Road', 'Beside Metro Station', NULL, '9876512345', 'sneha@example.com', 'IND', 'Telangana', 'Hyderabad', '500001',
 'Sneha Reddy', '12, MG Road', 'Beside Metro Station', NULL, '9876512345', 'sneha@example.com',
 'IND', 'Telangana', 'Hyderabad', '500001',
 NULL, NULL, NULL, 'NYKL00003382', '36AABCT1332L1ZP',
 NOW(), NOW());

-- Insert sample JIT orders with complete data
INSERT IGNORE INTO orders (
    order_no, order_status, order_date, order_last_update_date, on_hold, seller_id, seller_type,
    sla, customer_name, payment_method, order_amount, order_currency, order_tax_amount,
    shipping_charge, discount_amount, disc_coupon_code, store_credit, gv_amount, cod_charge,
    address1, address2, address3, phone, email, country_code, state, city, pin_code,
    bill_to_name, bill_address1, bill_address2, bill_address3, bill_phone, bill_email,
    bill_country_code, bill_state, bill_city, bill_zip_code,
    cancel_remark, reason_for_cancellation, priority, master_order_no, gstin,
    created_at, updated_at
) VALUES
('JIT001-1', '5', '2020-06-10 10:00:00', '2020-06-10 11:00:00', '0', 'JITSELLER1', 'JIT',
 NULL, 'Vikram Singh', '1', '5000.00', 'INR', '250.00',
 '300.00', '500.00', 'JIT500', '0.00', '0.00', '0.00',
 '78, Nehru Place', 'Near Metro', NULL, '9123456780', 'vikram@example.com', 'IND', 'Delhi', 'New Delhi', '110019',
 'Vikram Singh', '78, Nehru Place', 'Near Metro', NULL, '9123456780', 'vikram@example.com',
 'IND', 'Delhi', 'New Delhi', '110019',
 NULL, NULL, NULL, 'NYKL00003383', '07AABCU9603R1ZM',
 NOW(), NOW()),

('JIT002-1', '8', '2020-06-15 14:30:00', '2020-06-15 15:30:00', '0', 'JITSELLER1', 'JIT',
 NULL, 'Anjali Verma', '2', '3500.00', 'INR', '180.00',
 '200.00', '350.00', NULL, '0.00', '50.00', '60.00',
 '56, Lake Road', 'Opposite Park', NULL, '9988112233', 'anjali@example.com', 'IND', 'West Bengal', 'Kolkata', '700001',
 'Anjali Verma', '56, Lake Road', 'Opposite Park', NULL, '9988112233', 'anjali@example.com',
 'IND', 'West Bengal', 'Kolkata', '700001',
 NULL, NULL, NULL, 'NYKL00003384', '19AABCU9603R1ZK',
 NOW(), NOW()),

('JIT003-1', '10', '2020-07-01 09:00:00', '2020-07-01 10:00:00', '0', 'JITSELLER1', 'JIT',
 NULL, 'Karthik Iyer', '1', '6200.00', 'INR', '310.00',
 '350.00', '620.00', 'SUMMER620', '0.00', '0.00', '0.00',
 '89, Beach Road', 'Marina Area', NULL, '9876598765', 'karthik@example.com', 'IND', 'Tamil Nadu', 'Chennai', '600001',
 'Karthik Iyer', '89, Beach Road', 'Marina Area', NULL, '9876598765', 'karthik@example.com',
 'IND', 'Tamil Nadu', 'Chennai', '600001',
 NULL, NULL, NULL, 'NYKL00003385', '33AABCU9603R1ZT',
 NOW(), NOW()),

('JIT004-1', '15', '2020-07-10 16:00:00', '2020-07-10 17:00:00', '0', 'JITSELLER2', 'JIT',
 NULL, 'Meera Nair', '2', '2800.00', 'INR', '140.00',
 '180.00', '280.00', NULL, '50.00', '0.00', '45.00',
 '23, Hill View', 'Near Temple', NULL, '9871234567', 'meera@example.com', 'IND', 'Kerala', 'Kochi', '682001',
 'Meera Nair', '23, Hill View', 'Near Temple', NULL, '9871234567', 'meera@example.com',
 'IND', 'Kerala', 'Kochi', '682001',
 NULL, NULL, NULL, 'NYKL00003386', '32AABCU9603R1ZS',
 NOW(), NOW()),

('JIT005-1', '18', '2020-08-05 12:00:00', '2020-08-05 13:00:00', '0', 'JITSELLER2', 'JIT',
 NULL, 'Rohan Kapoor', '1', '7500.00', 'INR', '375.00',
 '400.00', '750.00', 'MEGA750', '100.00', '0.00', '0.00',
 '67, Mall Road', 'Shimla Center', NULL, '9123459876', 'rohan@example.com', 'IND', 'Himachal Pradesh', 'Shimla', '171001',
 'Rohan Kapoor', '67, Mall Road', 'Shimla Center', NULL, '9123459876', 'rohan@example.com',
 'IND', 'Himachal Pradesh', 'Shimla', '171001',
 NULL, NULL, NULL, 'NYKL00003387', '02AABCU9603R1ZH',
 NOW(), NOW());

-- Insert order items for Dropship orders
INSERT IGNORE INTO order_items (
    order_no, line_no, delivery_mode, sku_code, sku_name, order_qty, rejected_qty, cancelled_qty,
    shipped_qty, returned_qty, delivered_qty, line_amount, line_tax_amount, unit_price, mrp,
    discount_amount, shipping_charge, cod_charge, invoice_no, trans_code, trans_name, awb_no,
    imei_nos, confirm_date, gv_amount, store_credit, line_status, cst, vat, created_at, updated_at
) VALUES
-- Items for order 59878966-1
('59878966-1', '1', '1', 'TEST-55829-00006', 'Test4 vendor SKU6', '1', NULL, NULL, NULL, NULL, NULL,
 '698.93', '0.00', '699.00', '699.00', '100.00', '99.93', NULL, NULL, '999904', 'NykaaSelf', NULL,
 NULL, '2019-06-26 17:22:13', NULL, '0.00', '5', NULL, NULL, NOW(), NOW()),

('59878966-1', '2', '1', 'TEST-55829-00005', 'Test4 vendor SKU5', '1', NULL, NULL, NULL, NULL, NULL,
 '780.07', '0.00', '700.00', '700.00', '20.00', '100.07', NULL, NULL, '999904', 'NykaaSelf', NULL,
 NULL, '2019-06-26 17:22:13', NULL, '0.00', '5', NULL, NULL, NOW(), NOW()),

-- Items for order 53210664-1
('53210664-1', '1', '2', 'NYK-001234', 'Nykaa Face Cream 50ml', '2', NULL, NULL, NULL, NULL, NULL,
 '1200.00', '50.00', '600.00', '650.00', '100.00', '75.00', '25.00', 'INV-001', '999905', 'BlueDart', 'BD123456789',
 NULL, '2020-06-04 16:16:50', NULL, '0.00', '5', NULL, NULL, NOW(), NOW()),

('53210664-1', '2', '2', 'NYK-001235', 'Nykaa Lipstick Matte Red', '1', NULL, NULL, NULL, NULL, NULL,
 '1300.00', '50.00', '500.00', '550.00', '100.00', '75.00', '25.00', 'INV-001', '999905', 'BlueDart', 'BD123456789',
 NULL, '2020-06-04 16:16:50', NULL, '0.00', '5', NULL, NULL, NOW(), NOW()),

-- Items for order 34033344-1
('34033344-1', '1', '1', 'NYK-002345', 'Nykaa Foundation Natural', '1', NULL, NULL, NULL, NULL, NULL,
 '1600.00', '75.00', '1500.00', '1600.00', '150.00', '90.00', NULL, 'INV-002', '999906', 'DTDC', 'DT987654321',
 NULL, '2020-06-09 13:29:29', NULL, '25.00', '8', NULL, NULL, NOW(), NOW()),

('34033344-1', '2', '1', 'NYK-002346', 'Nykaa Mascara Volumizing', '2', NULL, NULL, NULL, NULL, NULL,
 '1600.00', '75.00', '800.00', '900.00', '150.00', '90.00', NULL, 'INV-002', '999906', 'DTDC', 'DT987654321',
 NULL, '2020-06-09 13:29:29', NULL, '25.00', '8', NULL, NULL, NOW(), NOW()),

-- Items for order 12345678-1
('12345678-1', '1', '1', 'NYK-003456', 'Nykaa Serum Vitamin C', '1', NULL, NULL, NULL, NULL, NULL,
 '900.00', '40.00', '850.00', '900.00', '75.00', '60.00', NULL, 'INV-003', '999907', 'Delhivery', 'DL456789123',
 NULL, '2020-07-30 17:55:09', NULL, '0.00', '5', NULL, NULL, NOW(), NOW()),

('12345678-1', '2', '1', 'NYK-003457', 'Nykaa Eye Shadow Palette', '1', NULL, NULL, NULL, NULL, NULL,
 '900.00', '40.00', '800.00', '850.00', '75.00', '60.00', NULL, 'INV-003', '999907', 'Delhivery', 'DL456789123',
 NULL, '2020-07-30 17:55:09', NULL, '0.00', '5', NULL, NULL, NOW(), NOW()),

-- Items for order 87654321-1
('87654321-1', '1', '2', 'NYK-004567', 'Nykaa Nail Polish Set', '1', NULL, NULL, NULL, NULL, NULL,
 '2250.00', '100.00', '2000.00', '2200.00', '200.00', '125.00', '37.50', 'INV-004', '999908', 'Ecom Express', 'EE789456123',
 NULL, '2020-08-20 15:30:00', NULL, '50.00', '8', NULL, NULL, NOW(), NOW()),

('87654321-1', '2', '2', 'NYK-004568', 'Nykaa Perfume Floral', '1', NULL, NULL, NULL, NULL, NULL,
 '2250.00', '100.00', '2000.00', '2200.00', '200.00', '125.00', '37.50', 'INV-004', '999908', 'Ecom Express', 'EE789456123',
 NULL, '2020-08-20 15:30:00', NULL, '50.00', '8', NULL, NULL, NOW(), NOW());

-- Insert order items for JIT orders
INSERT IGNORE INTO order_items (
    order_no, line_no, delivery_mode, sku_code, sku_name, order_qty, rejected_qty, cancelled_qty,
    shipped_qty, returned_qty, delivered_qty, line_amount, line_tax_amount, unit_price, mrp,
    discount_amount, shipping_charge, cod_charge, invoice_no, trans_code, trans_name, awb_no,
    imei_nos, confirm_date, gv_amount, store_credit, line_status, cst, vat, created_at, updated_at
) VALUES
-- Items for JIT order JIT001-1
('JIT001-1', '1', '1', 'JIT-SKU-001', 'Premium Skincare Set', '2', NULL, NULL, NULL, NULL, NULL,
 '2500.00', '125.00', '1250.00', '1500.00', '250.00', '150.00', NULL, 'JIT-INV-001', '999910', 'FedEx', 'FX123456789',
 NULL, '2020-06-10 11:00:00', NULL, '0.00', '5', NULL, NULL, NOW(), NOW()),

('JIT001-1', '2', '1', 'JIT-SKU-002', 'Luxury Hair Care Kit', '1', NULL, NULL, NULL, NULL, NULL,
 '2500.00', '125.00', '2000.00', '2300.00', '250.00', '150.00', NULL, 'JIT-INV-001', '999910', 'FedEx', 'FX123456789',
 NULL, '2020-06-10 11:00:00', NULL, '0.00', '5', NULL, NULL, NOW(), NOW()),

-- Items for JIT order JIT002-1
('JIT002-1', '1', '2', 'JIT-SKU-003', 'Makeup Combo Pack', '1', NULL, NULL, NULL, NULL, NULL,
 '1750.00', '90.00', '1500.00', '1700.00', '175.00', '100.00', '30.00', 'JIT-INV-002', '999911', 'BlueDart', 'BD987654321',
 NULL, '2020-06-15 15:30:00', '25.00', '0.00', '8', NULL, NULL, NOW(), NOW()),

('JIT002-1', '2', '2', 'JIT-SKU-004', 'Fragrance Collection', '1', NULL, NULL, NULL, NULL, NULL,
 '1750.00', '90.00', '1400.00', '1600.00', '175.00', '100.00', '30.00', 'JIT-INV-002', '999911', 'BlueDart', 'BD987654321',
 NULL, '2020-06-15 15:30:00', '25.00', '0.00', '8', NULL, NULL, NOW(), NOW()),

-- Items for JIT order JIT003-1
('JIT003-1', '1', '1', 'JIT-SKU-005', 'Complete Face Care Set', '2', NULL, NULL, NULL, NULL, NULL,
 '3100.00', '155.00', '1550.00', '1800.00', '310.00', '175.00', NULL, 'JIT-INV-003', '999912', 'DTDC', 'DT456123789',
 NULL, '2020-07-01 10:00:00', NULL, '0.00', '10', NULL, NULL, NOW(), NOW()),

('JIT003-1', '2', '1', 'JIT-SKU-006', 'Body Lotion Premium', '1', NULL, NULL, NULL, NULL, NULL,
 '3100.00', '155.00', '2500.00', '2800.00', '310.00', '175.00', NULL, 'JIT-INV-003', '999912', 'DTDC', 'DT456123789',
 NULL, '2020-07-01 10:00:00', NULL, '0.00', '10', NULL, NULL, NOW(), NOW()),

-- Items for JIT order JIT004-1
('JIT004-1', '1', '2', 'JIT-SKU-007', 'Eye Care Bundle', '1', NULL, NULL, NULL, NULL, NULL,
 '1400.00', '70.00', '1200.00', '1400.00', '140.00', '90.00', '22.50', 'JIT-INV-004', '999913', 'Ecom Express', 'EE321654987',
 NULL, '2020-07-10 17:00:00', NULL, '25.00', '15', NULL, NULL, NOW(), NOW()),

('JIT004-1', '2', '2', 'JIT-SKU-008', 'Lip Care Set', '1', NULL, NULL, NULL, NULL, NULL,
 '1400.00', '70.00', '1000.00', '1150.00', '140.00', '90.00', '22.50', 'JIT-INV-004', '999913', 'Ecom Express', 'EE321654987',
 NULL, '2020-07-10 17:00:00', NULL, '25.00', '15', NULL, NULL, NOW(), NOW()),

-- Items for JIT order JIT005-1
('JIT005-1', '1', '1', 'JIT-SKU-009', 'Premium Perfume Set', '1', NULL, NULL, NULL, NULL, NULL,
 '3750.00', '187.50', '3500.00', '4000.00', '375.00', '200.00', NULL, 'JIT-INV-005', '999914', 'FedEx', 'FX789456123',
 NULL, '2020-08-05 13:00:00', NULL, '50.00', '18', NULL, NULL, NOW(), NOW()),

('JIT005-1', '2', '1', 'JIT-SKU-010', 'Hair Styling Tools', '1', NULL, NULL, NULL, NULL, NULL,
 '3750.00', '187.50', '3200.00', '3600.00', '375.00', '200.00', NULL, 'JIT-INV-005', '999914', 'FedEx', 'FX789456123',
 NULL, '2020-08-05 13:00:00', NULL, '50.00', '18', NULL, NULL, NOW(), NOW());