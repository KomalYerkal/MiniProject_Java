package supermarket;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SupermarketBilling extends JFrame {

    // ─── Fields ───────────────────────────────────────────────────────────────
    JTextField nameField, priceField, stockField;
    JComboBox<String> categoryCombo;
    JTable productTable;
    DefaultTableModel productModel;
    JLabel totalLabel;

    // ─── Constructor ─────────────────────────────────────────────────────────
    public SupermarketBilling() {

        setTitle("🛒 Supermarket Billing System");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(225, 240, 255));   // Light blue background
        header.setOpaque(true);
        header.setPreferredSize(new Dimension(0, 65));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(0, 102, 204)));

        JLabel title = new JLabel("  🛒  Supermarket Billing & Inventory System");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(0, 51, 153));    // Dark navy — always visible
        title.setOpaque(false);

        String today = new SimpleDateFormat("dd MMM yyyy").format(new Date());
        JLabel dateLabel = new JLabel("📅  " + today + "  ");
        dateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        dateLabel.setForeground(new Color(0, 72, 180));

        header.add(title, BorderLayout.WEST);
        header.add(dateLabel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ===== LEFT PANEL — Product Form =====
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(9, 2, 8, 8));
        leftPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
                "  📦 Add / Edit Product  ",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 13),
                new Color(0, 102, 204)));
        leftPanel.setBackground(new Color(240, 248, 255));
        leftPanel.setPreferredSize(new Dimension(300, 0));

        leftPanel.add(new JLabel("  Product Name:"));
        nameField = new JTextField();
        leftPanel.add(nameField);

        leftPanel.add(new JLabel("  Category:"));
        categoryCombo = new JComboBox<>(new String[]{
                "Groceries", "Beverages", "Dairy", "Bakery", "Snacks", "Personal Care"
        });
        leftPanel.add(categoryCombo);

        leftPanel.add(new JLabel("  Price (₹):"));
        priceField = new JTextField();
        leftPanel.add(priceField);

        leftPanel.add(new JLabel("  Stock Quantity:"));
        stockField = new JTextField();
        leftPanel.add(stockField);

        // Buttons
        JButton addBtn = new JButton("➕ Add Product");
        JButton deleteBtn = new JButton("🗑  Delete Product");
        JButton updateBtn = new JButton("📝 Update Stock");
        JButton billBtn   = new JButton("🧾 Generate Bill");

        styleButton(addBtn,    new Color(39, 174, 96));    // Green
        styleButton(deleteBtn, new Color(231, 76, 60));    // Red
        styleButton(updateBtn, new Color(155, 89, 182));   // Purple
        styleButton(billBtn,   new Color(230, 126, 34));   // Orange

        leftPanel.add(addBtn);
        leftPanel.add(deleteBtn);
        leftPanel.add(updateBtn);
        leftPanel.add(billBtn);

        // Spacer rows
        leftPanel.add(new JLabel(""));
        leftPanel.add(new JLabel(""));

        add(leftPanel, BorderLayout.WEST);

        // ===== CENTER — Product Table =====
        String[] columns = {"ID", "Product Name", "Category", "Price (₹)", "Stock", "Status"};
        productModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        productTable = new JTable(productModel);
        productTable.setRowHeight(30);
        productTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        productTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        productTable.getTableHeader().setBackground(new Color(0, 102, 204));
        productTable.getTableHeader().setForeground(Color.WHITE);
        productTable.setSelectionBackground(new Color(173, 216, 255));
        productTable.setGridColor(new Color(200, 210, 220));

        JScrollPane scroll = new JScrollPane(productTable);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 204), 1),
                "  📋 Product Inventory  ",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 13),
                new Color(0, 102, 204)));
        add(scroll, BorderLayout.CENTER);

        // ===== BOTTOM PANEL =====
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        bottomPanel.setBackground(new Color(240, 248, 255));
        bottomPanel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(0, 102, 204)));

        JButton refreshBtn  = new JButton("🔄 Refresh");
        JButton searchBtn   = new JButton("🔍 Search");
        JTextField searchField = new JTextField(14);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        totalLabel = new JLabel("  Total Products: 0");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalLabel.setForeground(new Color(0, 102, 204));

        styleButton(refreshBtn, new Color(52, 152, 219));  // Blue
        styleButton(searchBtn,  new Color(22, 160, 133));  // Teal

        bottomPanel.add(refreshBtn);
        bottomPanel.add(new JLabel("  Search:"));
        bottomPanel.add(searchField);
        bottomPanel.add(searchBtn);
        bottomPanel.add(Box.createHorizontalStrut(30));
        bottomPanel.add(totalLabel);

        add(bottomPanel, BorderLayout.SOUTH);

        // ===== BUTTON ACTIONS =====
        addBtn.addActionListener(e    -> addProduct());
        deleteBtn.addActionListener(e -> deleteProduct());
        updateBtn.addActionListener(e -> updateStock());
        billBtn.addActionListener(e   -> openBillingWindow());
        refreshBtn.addActionListener(e -> loadProducts(""));
        searchBtn.addActionListener(e  -> loadProducts(searchField.getText().trim()));
        searchField.addActionListener(e -> loadProducts(searchField.getText().trim()));

        // Row click → fill form
        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && productTable.getSelectedRow() >= 0) {
                int row = productTable.getSelectedRow();
                nameField.setText(productModel.getValueAt(row, 1).toString());
                String cat = productModel.getValueAt(row, 2).toString();
                for (int i = 0; i < categoryCombo.getItemCount(); i++)
                    if (categoryCombo.getItemAt(i).equals(cat)) { categoryCombo.setSelectedIndex(i); break; }
                priceField.setText(productModel.getValueAt(row, 3).toString());
                stockField.setText(productModel.getValueAt(row, 4).toString());
            }
        });

        loadProducts("");
        setVisible(true);
    }

    // ===== ADD PRODUCT =====
    public void addProduct() {
        if (nameField.getText().isEmpty() || priceField.getText().isEmpty() || stockField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Connection con = connect.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO products (name, category, price, stock, unit) VALUES (?,?,?,?,'pcs')");
            ps.setString(1, nameField.getText().trim());
            ps.setString(2, categoryCombo.getSelectedItem().toString());
            ps.setDouble(3, Double.parseDouble(priceField.getText().trim()));
            ps.setInt(4, Integer.parseInt(stockField.getText().trim()));
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ Product Added Successfully!");
            clearForm();
            loadProducts("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price and Stock must be numbers!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== DELETE PRODUCT =====
    public void deleteProduct() {
        int row = productTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String productName = productModel.getValueAt(row, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete: " + productName + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        int id = (int) productModel.getValueAt(row, 0);
        try {
            Connection con = connect.getConnection();
            PreparedStatement ps = con.prepareStatement("DELETE FROM products WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "🗑 Product Deleted Successfully!");
            clearForm();
            loadProducts("");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== UPDATE STOCK =====
    public void updateStock() {
        int row = productTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to update!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String input = JOptionPane.showInputDialog(this, "Enter new stock quantity:");
        if (input == null || input.trim().isEmpty()) return;

        int id = (int) productModel.getValueAt(row, 0);
        try {
            int newStock = Integer.parseInt(input.trim());
            Connection con = connect.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE products SET stock = ? WHERE id = ?");
            ps.setInt(1, newStock);
            ps.setInt(2, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "📦 Stock Updated Successfully!");
            loadProducts("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Stock must be a number!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== LOAD PRODUCTS =====
    public void loadProducts(String keyword) {
        productModel.setRowCount(0);
        try {
            Connection con = connect.getConnection();
            PreparedStatement ps;
            if (keyword == null || keyword.isEmpty()) {
                ps = con.prepareStatement(
                        "SELECT p.id, p.name, IFNULL(c.name, p.category) AS category, p.price, p.stock " +
                        "FROM products p LEFT JOIN categories c ON p.category_id = c.id ORDER BY p.name");
            } else {
                ps = con.prepareStatement(
                        "SELECT p.id, p.name, IFNULL(c.name, p.category) AS category, p.price, p.stock " +
                        "FROM products p LEFT JOIN categories c ON p.category_id = c.id " +
                        "WHERE p.name LIKE ? OR p.category LIKE ? OR c.name LIKE ? ORDER BY p.name");
                ps.setString(1, "%" + keyword + "%");
                ps.setString(2, "%" + keyword + "%");
                ps.setString(3, "%" + keyword + "%");
            }
            ResultSet rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                int stock  = rs.getInt("stock");
                String status = stock <= 0  ? "❌ Out of Stock"
                             : stock <= 5   ? "⚠ Low Stock"
                             : "✅ In Stock";
                productModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        String.format("%.2f", rs.getDouble("price")),
                        stock,
                        status
                });
                count++;
            }
            totalLabel.setText("  Total Products: " + count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== OPEN BILLING WINDOW =====
    public void openBillingWindow() {
        new BillingWindow(this);
    }

    // ===== CLEAR FORM =====
    private void clearForm() {
        nameField.setText("");
        priceField.setText("");
        stockField.setText("");
        categoryCombo.setSelectedIndex(0);
        productTable.clearSelection();
    }

    // ===== STYLE BUTTON HELPER =====
    private void styleButton(JButton btn, Color bg) {
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI()); // Bypass macOS Aqua UI
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setBorderPainted(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg.darker(), 2),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // ===== MAIN =====
    public static void main(String[] args) {
        try {
            // Use cross-platform L&F so custom button colors work on macOS
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new SupermarketBilling());
    }
}


// ═══════════════════════════════════════════════════════════════════════════
//   BILLING WINDOW  — appears when "Generate Bill" is clicked
// ═══════════════════════════════════════════════════════════════════════════
class BillingWindow extends JDialog {

    JTextField custNameField, custPhoneField, custQtyField;
    JComboBox<String> productCombo, paymentCombo;
    JTable  cartTable;
    DefaultTableModel cartModel;
    JLabel  subtotalLabel, gstLabel, discLabel, grandLabel;
    JTextField discountField;

    // Product IDs parallel to productCombo items
    java.util.List<Integer>  productIds    = new java.util.ArrayList<>();
    java.util.List<Double>   productPrices = new java.util.ArrayList<>();
    java.util.List<String>   productUnits  = new java.util.ArrayList<>();

    // Cart rows
    java.util.List<Integer> cartProductIds  = new java.util.ArrayList<>();
    java.util.List<String>  cartProductNms  = new java.util.ArrayList<>();
    java.util.List<Integer> cartQtys        = new java.util.ArrayList<>();
    java.util.List<Double>  cartPrices      = new java.util.ArrayList<>();

    SupermarketBilling parent;

    public BillingWindow(SupermarketBilling parent) {
        super(parent, "🧾 Generate Bill", true);
        this.parent = parent;
        setSize(980, 680);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // ── Header ──
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 12));
        header.setBackground(new Color(230, 126, 34));
        header.setOpaque(true);
        JLabel title = new JLabel("🛒  SuperMart — Billing Counter");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // ── Left: Customer + Add Product ──
        JPanel leftPanel = new JPanel(new GridLayout(11, 2, 8, 8));
        leftPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 126, 34), 2),
                "  Customer & Product  ",
                0, 0, new Font("Segoe UI", Font.BOLD, 13), new Color(230, 126, 34)));
        leftPanel.setBackground(new Color(255, 248, 240));
        leftPanel.setPreferredSize(new Dimension(300, 0));

        leftPanel.add(new JLabel("  Customer Name:"));
        custNameField = new JTextField("Walk-in Customer");
        leftPanel.add(custNameField);

        leftPanel.add(new JLabel("  Phone:"));
        custPhoneField = new JTextField("0000000000");
        leftPanel.add(custPhoneField);

        leftPanel.add(new JLabel("  Payment Method:"));
        paymentCombo = new JComboBox<>(new String[]{"Cash", "Card", "UPI", "Net Banking"});
        leftPanel.add(paymentCombo);

        leftPanel.add(new JLabel("  Select Product:"));
        productCombo = new JComboBox<>();
        leftPanel.add(productCombo);

        leftPanel.add(new JLabel("  Quantity:"));
        custQtyField = new JTextField("1");
        leftPanel.add(custQtyField);

        leftPanel.add(new JLabel("  Discount (₹):"));
        discountField = new JTextField("0");
        leftPanel.add(discountField);

        JButton addToCartBtn  = new JButton("➕ Add to Cart");
        JButton removeCartBtn = new JButton("🗑  Remove Item");
        JButton calcBtn       = new JButton("🔄 Recalculate");
        JButton genBillBtn    = new JButton("✅ Generate Bill");
        JButton clearCartBtn  = new JButton("✖ Clear Cart");

        styleBtn(addToCartBtn,  new Color(39, 174, 96));
        styleBtn(removeCartBtn, new Color(231, 76, 60));
        styleBtn(calcBtn,       new Color(52, 152, 219));
        styleBtn(genBillBtn,    new Color(230, 126, 34));
        styleBtn(clearCartBtn,  new Color(127, 140, 141));

        leftPanel.add(addToCartBtn);
        leftPanel.add(removeCartBtn);
        leftPanel.add(calcBtn);
        leftPanel.add(genBillBtn);
        leftPanel.add(clearCartBtn);
        leftPanel.add(new JLabel(""));

        add(leftPanel, BorderLayout.WEST);

        // ── Center: Cart Table ──
        String[] cols = {"#", "Product", "Qty", "Unit Price (₹)", "Subtotal (₹)"};
        cartModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        cartTable = new JTable(cartModel);
        cartTable.setRowHeight(30);
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cartTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        cartTable.getTableHeader().setBackground(new Color(230, 126, 34));
        cartTable.getTableHeader().setForeground(Color.WHITE);
        cartTable.setSelectionBackground(new Color(255, 220, 150));

        JScrollPane scroll = new JScrollPane(cartTable);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 126, 34), 1),
                "  🧾 Cart Items  ",
                0, 0, new Font("Segoe UI", Font.BOLD, 13), new Color(230, 126, 34)));
        add(scroll, BorderLayout.CENTER);

        // ── Bottom: Totals ──
        JPanel bottomPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        bottomPanel.setBackground(new Color(255, 248, 240));
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(230, 126, 34)),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));

        subtotalLabel = totalLabel("Subtotal: ₹ 0.00", new Color(0, 102, 204));
        discLabel     = totalLabel("Discount: -₹ 0.00", new Color(231, 76, 60));
        gstLabel      = totalLabel("GST (18%): ₹ 0.00", new Color(155, 89, 182));
        grandLabel    = totalLabel("GRAND TOTAL: ₹ 0.00", new Color(39, 174, 96));

        bottomPanel.add(subtotalLabel);
        bottomPanel.add(discLabel);
        bottomPanel.add(gstLabel);
        bottomPanel.add(grandLabel);
        add(bottomPanel, BorderLayout.SOUTH);

        // ── Button Actions ──
        addToCartBtn.addActionListener(e  -> addToCart());
        removeCartBtn.addActionListener(e -> removeFromCart());
        calcBtn.addActionListener(e       -> recalculate());
        genBillBtn.addActionListener(e    -> generateBill());
        clearCartBtn.addActionListener(e  -> clearCart());
        discountField.addActionListener(e -> recalculate());

        loadProductsIntoCombo();
        setVisible(true);
    }

    // ===== LOAD PRODUCTS INTO COMBO =====
    void loadProductsIntoCombo() {
        productCombo.removeAllItems();
        productIds.clear();
        productPrices.clear();
        productUnits.clear();
        try {
            Connection con = connect.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, name, price, IFNULL(unit,'pcs') AS unit FROM products WHERE stock > 0 ORDER BY name");
            while (rs.next()) {
                productIds.add(rs.getInt("id"));
                productPrices.add(rs.getDouble("price"));
                productUnits.add(rs.getString("unit"));
                productCombo.addItem(rs.getString("name") + "  [₹" + String.format("%.0f", rs.getDouble("price")) + "]");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== ADD TO CART =====
    void addToCart() {
        if (productCombo.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product!"); return;
        }
        int qty;
        try { qty = Integer.parseInt(custQtyField.getText().trim()); }
        catch (NumberFormatException e) { JOptionPane.showMessageDialog(this, "Enter valid quantity!"); return; }
        if (qty <= 0) { JOptionPane.showMessageDialog(this, "Quantity must be > 0"); return; }

        int    idx    = productCombo.getSelectedIndex();
        int    prodId = productIds.get(idx);
        double price  = productPrices.get(idx);
        String name   = productCombo.getItemAt(idx).toString().split("  \\[")[0];

        // Check if already in cart
        for (int i = 0; i < cartProductIds.size(); i++) {
            if (cartProductIds.get(i) == prodId) {
                cartQtys.set(i, cartQtys.get(i) + qty);
                refreshCartTable();
                recalculate();
                custQtyField.setText("1");
                return;
            }
        }
        cartProductIds.add(prodId);
        cartProductNms.add(name);
        cartQtys.add(qty);
        cartPrices.add(price);
        refreshCartTable();
        recalculate();
        custQtyField.setText("1");
    }

    // ===== REMOVE FROM CART =====
    void removeFromCart() {
        int row = cartTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an item to remove."); return; }
        cartProductIds.remove(row);
        cartProductNms.remove(row);
        cartQtys.remove(row);
        cartPrices.remove(row);
        refreshCartTable();
        recalculate();
    }

    // ===== CLEAR CART =====
    void clearCart() {
        cartProductIds.clear(); cartProductNms.clear();
        cartQtys.clear(); cartPrices.clear();
        cartModel.setRowCount(0);
        subtotalLabel.setText("Subtotal: ₹ 0.00");
        discLabel.setText("Discount: -₹ 0.00");
        gstLabel.setText("GST (18%): ₹ 0.00");
        grandLabel.setText("GRAND TOTAL: ₹ 0.00");
    }

    // ===== REFRESH CART TABLE =====
    void refreshCartTable() {
        cartModel.setRowCount(0);
        for (int i = 0; i < cartProductIds.size(); i++) {
            double sub = cartQtys.get(i) * cartPrices.get(i);
            cartModel.addRow(new Object[]{
                    i + 1,
                    cartProductNms.get(i),
                    cartQtys.get(i),
                    String.format("%.2f", cartPrices.get(i)),
                    String.format("%.2f", sub)
            });
        }
    }

    // ===== RECALCULATE =====
    void recalculate() {
        double sub = 0;
        for (int i = 0; i < cartQtys.size(); i++) sub += cartQtys.get(i) * cartPrices.get(i);
        double disc  = 0;
        try { disc = Double.parseDouble(discountField.getText().trim()); } catch (Exception ignored) {}
        double gst   = (sub - disc) * 0.18;
        double grand = sub - disc + gst;

        subtotalLabel.setText(String.format("Subtotal: ₹ %.2f", sub));
        discLabel.setText(String.format("Discount: -₹ %.2f", disc));
        gstLabel.setText(String.format("GST (18%%): ₹ %.2f", gst));
        grandLabel.setText(String.format("GRAND TOTAL: ₹ %.2f", grand));
    }

    // ===== GENERATE BILL =====
    void generateBill() {
        if (cartProductIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty! Add products first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String custName  = custNameField.getText().trim();
        String custPhone = custPhoneField.getText().trim();
        String payment   = paymentCombo.getSelectedItem().toString();

        if (custName.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter customer name!"); return; }

        double sub = 0;
        for (int i = 0; i < cartQtys.size(); i++) sub += cartQtys.get(i) * cartPrices.get(i);
        double disc  = 0;
        try { disc = Double.parseDouble(discountField.getText().trim()); } catch (Exception ignored) {}
        double gst   = (sub - disc) * 0.18;
        double grand = sub - disc + gst;

        try {
            Connection con = connect.getConnection();
            con.setAutoCommit(false);

            // 1. Find or create customer
            int custId;
            PreparedStatement findCust = con.prepareStatement("SELECT id FROM customers WHERE phone = ?");
            findCust.setString(1, custPhone);
            ResultSet rs = findCust.executeQuery();
            if (rs.next()) {
                custId = rs.getInt("id");
            } else {
                PreparedStatement insCust = con.prepareStatement(
                        "INSERT INTO customers (name, phone, email, points) VALUES (?,?,'',0)",
                        Statement.RETURN_GENERATED_KEYS);
                insCust.setString(1, custName);
                insCust.setString(2, custPhone);
                insCust.executeUpdate();
                ResultSet keys = insCust.getGeneratedKeys();
                keys.next();
                custId = keys.getInt(1);
            }

            // 2. Bill number
            ResultSet cnt = con.createStatement().executeQuery(
                    "SELECT COUNT(*) FROM bills WHERE DATE(bill_date) = CURDATE()");
            cnt.next();
            String billNo = "BILL-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) +
                           "-" + String.format("%03d", cnt.getInt(1) + 1);

            // 3. Save bill header
            PreparedStatement psBill = con.prepareStatement(
                    "INSERT INTO bills (bill_number, customer_id, cashier_id, bill_date, subtotal, discount, gst, grand_total, payment_method) " +
                    "VALUES (?,?,1,NOW(),?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            psBill.setString(1, billNo);
            psBill.setInt(2, custId);
            psBill.setDouble(3, sub);
            psBill.setDouble(4, disc);
            psBill.setDouble(5, gst);
            psBill.setDouble(6, grand);
            psBill.setString(7, payment);
            psBill.executeUpdate();
            ResultSet billKeys = psBill.getGeneratedKeys();
            billKeys.next();
            int billId = billKeys.getInt(1);

            // 4. Save bill items + reduce stock
            PreparedStatement psItem  = con.prepareStatement(
                    "INSERT INTO bill_items (bill_id, product_id, quantity, unit_price, subtotal) VALUES (?,?,?,?,?)");
            PreparedStatement psStock = con.prepareStatement(
                    "UPDATE products SET stock = stock - ? WHERE id = ?");

            for (int i = 0; i < cartProductIds.size(); i++) {
                double itemSub = cartQtys.get(i) * cartPrices.get(i);
                psItem.setInt(1, billId);
                psItem.setInt(2, cartProductIds.get(i));
                psItem.setInt(3, cartQtys.get(i));
                psItem.setDouble(4, cartPrices.get(i));
                psItem.setDouble(5, itemSub);
                psItem.addBatch();

                psStock.setInt(1, cartQtys.get(i));
                psStock.setInt(2, cartProductIds.get(i));
                psStock.addBatch();
            }
            psItem.executeBatch();
            psStock.executeBatch();

            con.commit();
            con.setAutoCommit(true);

            // 5. Show receipt
            showReceipt(billNo, custName, custPhone, payment, sub, disc, gst, grand);
            parent.loadProducts("");
            clearCart();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving bill: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== SHOW RECEIPT =====
    void showReceipt(String billNo, String name, String phone, String payment,
                     double sub, double disc, double gst, double grand) {
        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("        🛒   S U P E R M A R T\n");
        sb.append("      Billing & Inventory System\n");
        sb.append("==========================================\n");
        sb.append("Bill No  : ").append(billNo).append("\n");
        sb.append("Date     : ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())).append("\n");
        sb.append("Customer : ").append(name).append("\n");
        sb.append("Phone    : ").append(phone).append("\n");
        sb.append("Payment  : ").append(payment).append("\n");
        sb.append("------------------------------------------\n");
        sb.append(String.format("%-20s %4s %7s %9s\n", "Product", "Qty", "Rate", "Amount"));
        sb.append("------------------------------------------\n");
        for (int i = 0; i < cartProductIds.size(); i++) {
            double itemSub = cartQtys.get(i) * cartPrices.get(i);
            String n = cartProductNms.get(i);
            if (n.length() > 20) n = n.substring(0, 20);
            sb.append(String.format("%-20s %4d %7.2f %9.2f\n", n, cartQtys.get(i), cartPrices.get(i), itemSub));
        }
        sb.append("------------------------------------------\n");
        sb.append(String.format("%-24s %10.2f\n", "Subtotal:", sub));
        sb.append(String.format("%-24s %10.2f\n", "Discount:", -disc));
        sb.append(String.format("%-24s %10.2f\n", "GST (18%):", gst));
        sb.append("==========================================\n");
        sb.append(String.format("%-24s %10.2f\n", "GRAND TOTAL:", grand));
        sb.append("==========================================\n");
        sb.append("   Thank you for shopping with us! 🙏\n");
        sb.append("==========================================\n");

        JTextArea ta = new JTextArea(sb.toString());
        ta.setFont(new Font("Monospaced", Font.PLAIN, 13));
        ta.setEditable(false);
        ta.setBackground(new Color(250, 250, 240));
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(460, 460));
        JOptionPane.showMessageDialog(this, sp, "🧾 Receipt — " + billNo, JOptionPane.PLAIN_MESSAGE);
    }

    // ===== HELPERS =====
    private void styleBtn(JButton btn, Color bg) {
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI()); // Bypass macOS Aqua UI
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setBorderPainted(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg.darker(), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JLabel totalLabel(String text, Color color) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(color);
        return lbl;
    }
}
