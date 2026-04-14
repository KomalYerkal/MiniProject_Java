
# Supermarket Billing System

A Java Swing desktop application to manage supermarket inventory and billing with a MySQL database.

## ✨ Features
- Add products with name, category, price and stock
- Search products by keyword
- Update stock quantity
- Delete products with confirmation
- Generate bills with GST and Discount calculations
- Automatic stock deduction on generated bills
- View and print formatted receipt
- Total item counter and stock status indicators

## 🛠️ Technologies Used
- Java Swing (GUI)
- MySQL (Database)
- JDBC (Database Connection)

## ⚙️ Database Setup
```sql
CREATE DATABASE supermarket_db;
USE supermarket_db;

-- Run the included schema.sql script to create tables and insert sample data:
-- mysql -u root -p < schema.sql
```

## ▶️ How to Run
```bash
# Compile
javac -cp ".:mysql-connector-j-9.6.0.jar" supermarket/connect.java supermarket/SupermarketBilling.java

# Run
java -cp ".:mysql-connector-j-9.6.0.jar" supermarket.SupermarketBilling
```

## 👤 Author
Komal Yerkal
