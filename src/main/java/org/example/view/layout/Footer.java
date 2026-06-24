package org.example.view.layout;

import javax.swing.*;
import java.awt.*;

public class Footer extends JPanel {

    public Footer() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new FlowLayout(FlowLayout.CENTER));
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        setBackground(new Color(248, 249, 250));
        
        JLabel footerLabel = new JLabel("© 2023 - Phần mềm Quản lý Nhân sự");
        footerLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        add(footerLabel);
    }
}