package org.example.view.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import java.awt.*;

public final class ViewStyles {
    public static final Color APP_BG = new Color(245, 247, 251);
    public static final Color SURFACE = Color.WHITE;
    public static final Color BORDER = new Color(222, 228, 237);
    public static final Color TEXT = new Color(31, 41, 55);
    public static final Color MUTED_TEXT = new Color(107, 114, 128);
    public static final Color PRIMARY = new Color(37, 99, 235);
    public static final Color PRIMARY_DARK = new Color(29, 78, 216);
    public static final Color DANGER = new Color(220, 38, 38);

    private static final Font BASE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);

    private ViewStyles() {
    }

    public static void applyTree(Component component) {
        if (component == null) {
            return;
        }

        styleComponent(component);
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                applyTree(child);
            }
        }
    }

    public static void stylePagePanel(JPanel panel) {
        panel.setBackground(APP_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
    }

    public static void styleTitle(JLabel label) {
        label.setFont(TITLE_FONT);
        label.setForeground(TEXT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
    }

    public static void stylePrimaryButton(AbstractButton button) {
        styleButtonBase(button);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY);
    }

    public static void styleSecondaryButton(AbstractButton button) {
        styleButtonBase(button);
        button.setForeground(TEXT);
        button.setBackground(SURFACE);
        button.setBorder(compoundBorder(BorderFactory.createLineBorder(BORDER), 10, 14));
    }

    public static void styleDangerButton(AbstractButton button) {
        styleButtonBase(button);
        button.setForeground(Color.WHITE);
        button.setBackground(DANGER);
    }

    public static JPanel createSurfacePanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(SURFACE);
        panel.setBorder(compoundBorder(BorderFactory.createLineBorder(BORDER), 16, 16));
        return panel;
    }

    private static void styleComponent(Component component) {
        component.setFont(BASE_FONT);

        if (component instanceof JPanel panel) {
            if (panel.getBackground() == null || panel.getBackground().equals(new Color(238, 238, 238))) {
                panel.setBackground(APP_BG);
            }
        } else if (component instanceof JTable table) {
            styleTable(table);
        } else if (component instanceof JScrollPane scrollPane) {
            scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
            scrollPane.getViewport().setBackground(SURFACE);
        } else if (component instanceof JTextField textField) {
            styleTextField(textField);
        } else if (component instanceof JTextArea textArea) {
            textArea.setFont(BASE_FONT);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        } else if (component instanceof JComboBox<?> comboBox) {
            comboBox.setBackground(SURFACE);
            comboBox.setForeground(TEXT);
            comboBox.setFont(BASE_FONT);
        } else if (component instanceof JLabel label) {
            label.setForeground(TEXT);
            label.setFont(BASE_FONT);
        } else if (component instanceof JButton button) {
            styleSecondaryButton(button);
            if (isPrimaryAction(button.getText())) {
                stylePrimaryButton(button);
            } else if (isDangerAction(button.getText())) {
                styleDangerButton(button);
            }
        }
    }

    private static void styleTable(JTable table) {
        table.setFont(BASE_FONT);
        table.setForeground(TEXT);
        table.setBackground(SURFACE);
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        if (header != null) {
            header.setFont(new Font("Segoe UI", Font.BOLD, 13));
            header.setForeground(TEXT);
            header.setBackground(new Color(241, 245, 249));
            header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
            header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));
        }
    }

    private static void styleTextField(JTextField textField) {
        textField.setFont(BASE_FONT);
        textField.setForeground(TEXT);
        textField.setBackground(SURFACE);
        textField.setBorder(compoundBorder(BorderFactory.createLineBorder(BORDER), 8, 10));
        textField.setPreferredSize(new Dimension(textField.getPreferredSize().width, 34));
    }

    private static void styleButtonBase(AbstractButton button) {
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(compoundBorder(BorderFactory.createEmptyBorder(), 10, 14));
    }

    private static boolean isPrimaryAction(String text) {
        if (text == null) {
            return false;
        }
        String lower = text.toLowerCase();
        return lower.contains("thêm") || lower.contains("tạo") || lower.contains("đăng nhập")
                || lower.contains("check-in") || lower.contains("check-out") || lower.contains("refresh");
    }

    private static boolean isDangerAction(String text) {
        if (text == null) {
            return false;
        }
        String lower = text.toLowerCase();
        return lower.contains("xóa") || lower.contains("thoát") || lower.contains("đăng xuất");
    }

    private static Border compoundBorder(Border outer, int topBottom, int leftRight) {
        return BorderFactory.createCompoundBorder(
                outer,
                BorderFactory.createEmptyBorder(topBottom, leftRight, topBottom, leftRight)
        );
    }
}
