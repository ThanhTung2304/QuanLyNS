package org.example.view.layout;

import javax.swing.*;
import java.awt.*;

public class Footer extends JPanel {

    public Footer() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new FlowLayout(FlowLayout.CENTER));
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(222, 228, 237)));
        setBackground(Color.WHITE);
        
        JLabel footerLabel = new JLabel("© 2026 - Phần mềm Quản lý Nhân sự");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(107, 114, 128));
        add(footerLabel);
    }
}
