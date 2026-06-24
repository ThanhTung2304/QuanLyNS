package org.example.view.layout;

import org.example.entity.Account;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Sidebar extends JScrollPane {

    private final JPanel container = new JPanel();
    private final JPanel menuPanel = new JPanel();

    private final Map<String, JButton> menuMap = new LinkedHashMap<>();
    private final Map<Account.Role, List<String>> rolePermissions = new HashMap<>();

    private static final Color BG        = new Color(33, 47, 61);
    private static final Color BG_ITEM   = new Color(45, 58, 75);
    private static final Color BG_HOVER  = new Color(60, 75, 95);
    private static final Color BG_ACTIVE = new Color(85, 120, 200);
    private static final Color ACCENT    = new Color(0, 150, 255);

    public Sidebar(Account.Role role) {
        container.setLayout(new BorderLayout());
        container.setBackground(BG);

        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(BG);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        container.add(menuPanel, BorderLayout.CENTER);

        setViewportView(container);
        setPreferredSize(new Dimension(240, 0));
        setBorder(null);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        initPermissions();

        List<String> menus = rolePermissions.getOrDefault(role, new ArrayList<>());
        for (String menu : menus) {
            addMenu(menu);
        }
    }

    private void initPermissions() {
        rolePermissions.put(Account.Role.ADMIN, Arrays.asList(
                "Trang chủ", "Hồ sơ của tôi", "Xin nghỉ phép", "Chấm công", "Duyệt nghỉ phép",
                "Tính lương", "Quản lý Nhân viên", "Quản lý Hợp đồng", "Quản lý Phòng ban",
                "Quản lý Chức vụ", "Quản lý Tài khoản"
        ));
        rolePermissions.put(Account.Role.HR, Arrays.asList(
                "Trang chủ", "Hồ sơ của tôi", "Xin nghỉ phép", "Chấm công", "Duyệt nghỉ phép",
                "Tính lương", "Quản lý Nhân viên", "Quản lý Hợp đồng", "Quản lý Phòng ban"
        ));
        rolePermissions.put(Account.Role.TRUONG_PHONG, Arrays.asList(
                "Trang chủ", "Hồ sơ của tôi", "Xin nghỉ phép", "Chấm công", "Duyệt nghỉ phép"
        ));
        rolePermissions.put(Account.Role.NHAN_VIEN, Arrays.asList(
                "Trang chủ", "Hồ sơ của tôi", "Xin nghỉ phép", "Chấm công"
        ));
    }

    private void addMenu(String title) {
        JButton btn = new JButton("  " + title);
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(BG_ITEM);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!btn.getBackground().equals(BG_ACTIVE))
                    btn.setBackground(BG_HOVER);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!btn.getBackground().equals(BG_ACTIVE))
                    btn.setBackground(BG_ITEM);
            }
        });

        menuMap.put(title, btn);
        menuPanel.add(btn);
        menuPanel.add(Box.createVerticalStrut(4));
    }

    public void setActiveMenu(String menuLabel) {
        menuMap.forEach((k, v) -> {
            boolean active = k.equals(menuLabel);
            v.setBackground(active ? BG_ACTIVE : BG_ITEM);
            if (active) {
                v.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 0, 0, ACCENT),
                        BorderFactory.createEmptyBorder(10, 16, 10, 20)
                ));
            } else {
                v.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            }
        });
    }

    public JButton getMenuButton(String title) {
        return menuMap.get(title);
    }

    public Set<String> getAvailableMenus() {
        return menuMap.keySet();
    }
}