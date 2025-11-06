package com.ecommerce;

import com.ecommerce.view.LoginFrame;
import javax.swing.SwingUtilities;

public class ECommerceApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {new LoginFrame().setVisible(true);});

    }
}