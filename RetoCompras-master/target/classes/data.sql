INSERT INTO products (id, name, price) VALUES (1, 'Teléfono', 350000);
INSERT INTO products (id, name, price) VALUES (2, 'Televisor', 430000);
INSERT INTO products (id, name, price) VALUES (3, 'Computador', 3500000);
INSERT INTO products (id, name, price) VALUES (4, 'Impresora', 300000);
INSERT INTO products (id, name, price) VALUES (5, 'Cámara', 500000);
INSERT INTO products (id, name, price) VALUES (6, 'Reloj', 35000);
INSERT INTO products (id, name, price) VALUES (7, 'Mouse', 50000);
INSERT INTO products (id, name, price) VALUES (8, 'Teclado', 10000);
INSERT INTO products (id, name, price) VALUES (9, 'Cargador de portatil', 30000);
INSERT INTO products (id, name, price) VALUES (10, 'Protector celular', 3000);

INSERT INTO clients (id, name, last_name, identification_number, address) VALUES (1, 'Andrés', 'González', '12345', 'carrera 11 # 4-12');
INSERT INTO clients (id, name, last_name, identification_number, address) VALUES (2, 'Felipe', 'Cardona', '23451', 'carrera 12 # 3-09');
INSERT INTO clients (id, name, last_name, identification_number, address) VALUES (3, 'María', 'Cuarán', '34512', 'carrera 13 # 21-10');
INSERT INTO clients (id, name, last_name, identification_number, address) VALUES (4, 'Javier', 'Echeverría', '45123', 'carrera 14 # 12-08');

INSERT INTO other_accounts (id, concept, value, type) VALUES (1, 'IVA', 19, 'PORCENTAJE');
INSERT INTO other_accounts (id, concept, value, type) VALUES (2, 'DOMICILIO', 3000, 'PESOS');

INSERT INTO orders (id, created_date, state, client_id, total) VALUES (1, '2021-02-11T014:49:45', 'REALIZADA', 1, 80000);
INSERT INTO orders (id, created_date, state, client_id, total) VALUES (2, '2021-02-11T01:49:45', 'REALIZADA', 2, 40000);

INSERT INTO order_items (order_id, quantity, total, product_id) VALUES (1, 2, 70000, 6);
INSERT INTO order_items (order_id, quantity, total, product_id) VALUES (1, 1, 10000, 8);
INSERT INTO order_items (order_id, quantity, total, product_id) VALUES (2, 4, 40000, 8);

INSERT INTO orders_items (order_id, items_id) VALUES (1, 1);
INSERT INTO orders_items (order_id, items_id) VALUES (1, 2);
INSERT INTO orders_items (order_id, items_id) VALUES (2, 3);

INSERT INTO invoices (id, total_to_pay, delivery, iva, order_id) VALUES (1, 98200, 3000, 19, 1);
INSERT INTO invoices (id, total_to_pay, delivery, iva, order_id) VALUES (2, 50600, 3000, 19, 2);

UPDATE orders SET invoice_id = 1 WHERE id = 1;
UPDATE orders SET invoice_id = 2 WHERE id = 2;

INSERT INTO order_other_accounts (order_id, other_account_id) VALUES (1, 1);
INSERT INTO order_other_accounts (order_id, other_account_id) VALUES (1, 2);
INSERT INTO order_other_accounts (order_id, other_account_id) VALUES (2, 1);
