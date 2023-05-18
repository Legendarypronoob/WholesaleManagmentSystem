package GUI.Frames;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import Types.*;
import src.Main;

public class Cart extends JFrame{

    protected JFrame jframe;
    protected JPanel jpanel;
    protected JLabel backgroundImageLabel, purchaseButtonLabel, jlabelback;
    protected JTable table;
    private int quantity;
    private JButton addButton,subtractButton;
    private ArrayList<Product> cartItems;
    String quantityString;

    JLabel totalLabel;

    private int total = 0;
    public Cart() {
        
        setTitle("Cart");
        setSize(new Dimension(1016, 638));
        jpanel = new JPanel();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().add(jpanel);
        jpanel.setLayout(null);
        setResizable(false);
        setExtendedState(JFrame.MAXIMIZED_HORIZ);

        cartItems = Main.CartDatabase.getCart();

        //setIcon
        ImageIcon icon = new ImageIcon("res\\icon.png");
        setIconImage(icon.getImage());

        totalLabel = new JLabel("Total: 0 Taka");
        totalLabel.setBounds(133, 540, 150, 14);
        jpanel.add(totalLabel);

        purchaseButtonLabel = new JLabel("");
        purchaseButtonLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (Product item : cartItems) {
                    Main.purchaseHistoryDatabase.add("productId=" + item.productID + ",productName=" + item.productName + ",productPrice=" + item.productPrice + ",quantity=" + (item.productQuantity == null ? "1" : item.productQuantity) + ",manufacturer=" + item.productManufacturer + ",manufactureDate=" + item.manufacturingDate + ",expireDate=" + item.expiryDate);
                }
                Main.CartDatabase.clear();
                JOptionPane.showMessageDialog(null, "You have purchased products!");
                dispose();
                new Menu();

            }
        });
        purchaseButtonLabel.setBounds(426, 527, 153, 40);
        jpanel.add(purchaseButtonLabel);

        JButton removeButton = new JButton("Remove");
        removeButton.setBounds(702, 482, 82, 27);
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == 0){
                    return;
                }
                if (selectedRow != -1) {
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    String productId = model.getValueAt(selectedRow, 0).toString();
                    deleteProduct(productId);
                    model.removeRow(selectedRow);

                    updateCart();
                }
            }
        });
        jpanel.add(removeButton);

        addButton = new JButton("+");
        addButton.setBounds(794, 482, 67, 27);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == 0){
                    return;
                }
                if (selectedRow != -1) {
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    int quantityInteger = Integer.parseInt(model.getValueAt(selectedRow, 3).toString());
                    quantityString = Integer.toString(quantityInteger);
                    if (quantityString != null && !quantityString.isEmpty() && !quantityString.equals("null")) {
                        quantity = Integer.parseInt(quantityString);
                        model.setValueAt(quantity + 1, selectedRow, 3); // Increase quantity by 1);
                        updateTotal();
                        updateCart();
                    }
                }
            }
        });
        jpanel.add(addButton);

        subtractButton = new JButton("-");
        subtractButton.setBounds(625, 482, 67, 27);
        subtractButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == 0){
                    return;
                }
                if (selectedRow != -1) {
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    int quantityInteger = Integer.parseInt(model.getValueAt(selectedRow, 3).toString());
                    quantityString = Integer.toString(quantityInteger);
                    if (quantityString != null && !quantityString.isEmpty()) {
                        quantity = Integer.parseInt(quantityString);
                        if (quantity > 1) {
                            model.setValueAt(quantity - 1, selectedRow, 3); // Decrease quantity by 1
                            updateTotal();
                            updateCart();
                        }
                    }
                }
            }
        });

        jpanel.add(subtractButton);

        table = new JTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        DefaultTableModel model = new DefaultTableModel(
                new Object[][]{{"Product ID","Product Name", "Product Price", "Product Quantity"}},
                new String[]{"Product ID", "Product Name", "Product Price", "Product Quantity"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setModel(model);

        for (Product p : cartItems) {
            model.addRow(new Object[]{p.productID, p.productName, p.productPrice, p.productQuantity}); // Set initial quantity to 1
        }


        table.setBounds(100, 140, 800, 340);
        jpanel.add(table);

        jlabelback = new JLabel("");
        jlabelback.setIcon(new ImageIcon(""));
        jlabelback.setBounds(42, 41, 59, 60);
        jlabelback.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new Menu();
            }
        });
        jpanel.add(jlabelback);

        backgroundImageLabel = new JLabel();
        backgroundImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundImageLabel.setSize(1000, 600);
        backgroundImageLabel.setIcon(new ImageIcon("res\\Cart.png"));
        jpanel.add(backgroundImageLabel);
        setBounds(0, 0, 1016, 637);
        setLocationRelativeTo(null);
        setVisible(true);
        updateCart();
        updateTotal();
    }

    public void deleteProduct(String productId){
        System.out.println("Product ID: " + productId + " has been deleted from the cart.");
        Main.CartDatabase.removeProduct(productId);
    }

    private void updateTotal() {
        total = 0;
        /*
        for (Product p : cartItems) {
            System.out.println(p.productPrice + " " + p.productQuantity);
            total += Integer.parseInt(p.productPrice) * Integer.parseInt(p.productQuantity);
        }

         */

        //update from table
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        for (int i = 1; i < model.getRowCount(); i++) {

            int price = Integer.parseInt(model.getValueAt(i, 2).toString());
            int quantity = Integer.parseInt(model.getValueAt(i, 3).toString());

            total += price * quantity;
        }


        System.out.println("Total: " + total);
        totalLabel.setText("Total: " + total);
    }

    public void updateCart() {
        try{
            DefaultTableModel model = (DefaultTableModel) table.getModel();

            //get the first column of the table row
            int selectedRow = table.getSelectedRow();

            if (selectedRow == -1) {
                return;
            }

            String productID = model.getValueAt(selectedRow, 0).toString();
            String productCount = model.getValueAt(selectedRow, 3).toString();

            //update the cart
            Main.CartDatabase.update(productID, "quantity", productCount);

            updateTotal();

        }catch (Exception e){
            System.out.println("Error: " + e);
        }
    }

}