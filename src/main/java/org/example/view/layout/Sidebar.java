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

    private static final Color BG        = new Color(17, 24, 39);
    private static final Color BG_ITEM   = new Color(31, 41, 55);
    private static final Color BG_HOVER  = new Color(55, 65, 81);
    private static final Color BG_ACTIVE = new Color(37, 99, 235);
    private static final Color ACCENT    = new Color(96, 165, 250);

    public Sidebar(Account.Role role) {
        container.setLayout(new BorderLayout());
        container.setBackground(BG);

        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(BG);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 14, 10));

        JPanel brandPanel = new JPanel(new BorderLayout());
        brandPanel.setOpaque(false);
        brandPanel.setBorder(BorderFactory.createEmptyBorder(18, 14, 18, 14));
        JLabel brandLabel = new JLabel("Quản lý nhân sự");
        brandLabel.setForeground(Color.WHITE);
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JLabel roleLabel = new JLabel(role != null ? role.toString() : "GUEST");
        roleLabel.setForeground(new Color(203, 213, 225));
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        brandPanel.add(brandLabel, BorderLayout.NORTH);
        brandPanel.add(roleLabel, BorderLayout.SOUTH);

        container.add(brandPanel, BorderLayout.NORTH);
        container.add(menuPanel, BorderLayout.CENTER);

        setViewportView(container);
        setPreferredSize(new Dimension(260, 0));
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
        JButton btn = new JButton(title);
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(BG_ITEM);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

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
        menuPanel.add(Box.createVerticalStrut(6));
    }

    public void setActiveMenu(String menuLabel) {
        menuMap.forEach((k, v) -> {
            boolean active = k.equals(menuLabel);
            v.setBackground(active ? BG_ACTIVE : BG_ITEM);
            if (active) {
                v.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 0, 0, ACCENT),
                        BorderFactory.createEmptyBorder(10, 10, 10, 14)
                ));
            } else {
                v.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
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
