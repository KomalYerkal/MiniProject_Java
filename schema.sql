-- ============================================
--   SUPERMARKET BILLING SYSTEM - DATABASE
-- ============================================

CREATE DATABASE IF NOT EXISTS supermarket_db;
USE supermarket_db;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(50) UNIQUE NOT NULL,
    password  VARCHAR(100) NOT NULL,
    full_name VARCHAR(100),
    role      VARCHAR(20) DEFAULT 'cashier'
);

-- Categories table
CREATE TABLE IF NOT EXISTS categories (
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- Products table (supports both category_id and plain category string)
CREATE TABLE IF NOT EXISTS products (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    category    VARCHAR(50),
    category_id INT,
    price       DECIMAL(10,2) NOT NULL,
    stock       INT DEFAULT 0,
    unit        VARCHAR(20) DEFAULT 'pcs',
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

-- Customers table
CREATE TABLE IF NOT EXISTS customers (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    name   VARCHAR(100) NOT NULL,
    phone  VARCHAR(15),
    email  VARCHAR(100),
    points INT DEFAULT 0
);

-- Bills table
CREATE TABLE IF NOT EXISTS bills (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    bill_number    VARCHAR(30) UNIQUE NOT NULL,
    customer_id    INT,
    cashier_id     INT DEFAULT 1,
    bill_date      DATETIME DEFAULT NOW(),
    subtotal       DECIMAL(10,2),
    discount       DECIMAL(10,2) DEFAULT 0,
    gst            DECIMAL(10,2),
    grand_total    DECIMAL(10,2),
    payment_method VARCHAR(20) DEFAULT 'Cash',
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

-- Bill items table
CREATE TABLE IF NOT EXISTS bill_items (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    bill_id    INT NOT NULL,
    product_id INT NOT NULL,
    quantity   INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal   DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (bill_id) REFERENCES bills(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- ============================================
--   SAMPLE DATA
-- ============================================

INSERT IGNORE INTO users (username, password, full_name, role) VALUES
('admin', 'admin123', 'Administrator', 'admin');

INSERT IGNORE INTO categories (id, name) VALUES
(1,'Groceries'),(2,'Beverages'),(3,'Dairy'),
(4,'Bakery'),(5,'Snacks'),(6,'Personal Care');

INSERT INTO products (name, category, category_id, price, stock, unit) VALUES
('Rice (1 kg)',         'Groceries',     1,  45.00, 100, 'kg'),
('Wheat Flour (1 kg)', 'Groceries',     1,  35.00, 150, 'kg'),
('Toor Dal (1 kg)',    'Groceries',     1, 120.00,  80, 'kg'),
('Sunflower Oil (1L)', 'Groceries',     1,  95.00,  60, 'bottle'),
('Coca Cola (500ml)',  'Beverages',     2,  30.00, 200, 'bottle'),
('Pepsi (1 L)',        'Beverages',     2,  50.00, 150, 'bottle'),
('Mineral Water (1L)','Beverages',     2,  20.00, 300, 'bottle'),
('Milk (500 ml)',      'Dairy',         3,  25.00, 100, 'packet'),
('Butter (100 g)',     'Dairy',         3,  55.00,  60, 'pcs'),
('Amul Cheese',       'Dairy',         3, 130.00,  40, 'pack'),
('White Bread',        'Bakery',        4,  35.00,  70, 'loaf'),
('Whole Wheat Bread',  'Bakery',        4,  45.00,  50, 'loaf'),
('Lays Chips (26g)',   'Snacks',        5,  20.00, 200, 'pack'),
('Biscuits (250g)',    'Snacks',        5,  30.00, 180, 'pack'),
('Chocolate Bar',      'Snacks',        5,  40.00, 120, 'pcs'),
('Shampoo (200ml)',    'Personal Care', 6, 150.00,  60, 'bottle'),
('Toothpaste (100g)', 'Personal Care', 6,  75.00,  90, 'tube'),
('Soap (100g)',        'Personal Care', 6,  30.00, 150, 'pcs');

INSERT IGNORE INTO customers (name, phone, email) VALUES
('Walk-in Customer', '0000000000', '');
