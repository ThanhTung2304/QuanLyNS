package org.example.view;

import org.example.controller.AccountController;
import org.example.controller.DashboardController;
import org.example.dto.DashboardStats;
import org.example.entity.Account;
import org.example.security.SessionManager;
import org.example.view.layout.Footer;
import org.example.view.layout.Header;
import org.example.view.layout.Sidebar;
import org.example.view.util.ViewStyles;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MainFrame extends JFrame {

    private final AccountController accountController;
    private final DashboardController dashboardController;
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private Sidebar sidebar;

    private final Map<String, String> menuCardMap = new HashMap<>();

    public MainFrame() {
        this.accountController = new AccountController();
        this.dashboardController = new DashboardController();
        this.cardLayout = new CardLayout();
        this.contentPanel = new JPanel(cardLayout);
        
        initMenuCardMap();
        initComponents();
    }

    private void initMenuCardMap() {
        menuCardMap.put("Trang chủ", "HOME");
        menuCardMap.put("Hồ sơ của tôi", "MY_PROFILE");
        menuCardMap.put("Quản lý Nhân viên", "EMPLOYEE");
        menuCardMap.put("Quản lý Hợp đồng", "HOP_DONG");
        menuCardMap.put("Chấm công", "CHAM_CONG");
        menuCardMap.put("Tính lương", "LUONG");
        menuCardMap.put("Quản lý Phòng ban", "DEPARTMENT");
        menuCardMap.put("Quản lý Chức vụ", "CHUC_VU");
        menuCardMap.put("Quản lý Tài khoản", "ACCOUNT");
        menuCardMap.put("Xin nghỉ phép", "NGHI_PHEP");
        menuCardMap.put("Duyệt nghỉ phép", "DUYET_NGHI_PHEP");
        menuCardMap.put("Cấu hình Chấm công", "CAU_HINH");
    }

    private void initComponents() {
        setTitle("Hệ thống Quản lý Nhân sự");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1280, 800));
        setLayout(new BorderLayout());
        getContentPane().setBackground(ViewStyles.APP_BG);

        Account.Role currentUserRole = SessionManager.getInstance().getCurrentRole();
        
        sidebar = new Sidebar(currentUserRole);
        Header header = new Header(this::onLogout);
        Footer footer = new Footer();

        setJMenuBar(createMainMenuBar());
        add(header, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        buildContentCards();
        setupMenuActions();

        cardLayout.show(contentPanel, "HOME");
        sidebar.setActiveMenu("Trang chủ");
    }

    private JMenuBar createMainMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ViewStyles.BORDER));
        menuBar.setBackground(Color.WHITE);
        JMenu systemMenu = new JMenu("Hệ thống");

        if (SessionManager.getInstance().getCurrentRole() == Account.Role.ADMIN) {
            JMenuItem cauHinhItem = new JMenuItem("Cấu hình Chấm công");
            cauHinhItem.addActionListener(e -> switchCard("Cấu hình Chấm công"));
            systemMenu.add(cauHinhItem);
            systemMenu.addSeparator();
        }

        JMenuItem logoutItem = new JMenuItem("Đăng xuất");
        logoutItem.addActionListener(e -> onLogout());
        systemMenu.add(logoutItem);
        
        JMenuItem exitItem = new JMenuItem("Thoát");
        exitItem.addActionListener(e -> System.exit(0));
        systemMenu.add(exitItem);
        
        menuBar.add(systemMenu);
        return menuBar;
    }

    private void buildContentCards() {
        contentPanel.setBackground(ViewStyles.APP_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        
        // Chỉ Admin mới thấy Dashboard, các vai trò khác thấy trang chào mừng
        if (SessionManager.getInstance().getCurrentRole() == Account.Role.ADMIN) {
            contentPanel.add(buildAdminDashboard(), "HOME");
        } else {
            contentPanel.add(buildWelcomePanel(), "HOME");
        }
        
        contentPanel.add(new MyProfileView(), "MY_PROFILE");
        contentPanel.add(new NhanVienView(), "EMPLOYEE");
        contentPanel.add(new HopDongView(), "HOP_DONG");
        contentPanel.add(new ChamCongView(), "CHAM_CONG");
        contentPanel.add(new LuongView(), "LUONG");
        contentPanel.add(new PhongBanView(), "DEPARTMENT");
        contentPanel.add(new ChucVuView(), "CHUC_VU");
        contentPanel.add(new AccountView(), "ACCOUNT");
        contentPanel.add(new MyNghiPhepView(), "NGHI_PHEP");
        contentPanel.add(new DuyetNghiPhepView(), "DUYET_NGHI_PHEP");
        contentPanel.add(new CauHinhView(), "CAU_HINH");

        ViewStyles.applyTree(contentPanel);
    }

    private JPanel buildAdminDashboard() {
        JPanel dashboardPanel = new JPanel(new BorderLayout(0, 18));
        dashboardPanel.setBackground(ViewStyles.APP_BG);
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        DashboardStats stats = dashboardController.getDashboardStats();
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        JLabel titleLabel = new JLabel("Tổng quan hệ thống");
        ViewStyles.styleTitle(titleLabel);
        dashboardPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel statGrid = new JPanel(new GridLayout(0, 2, 18, 18));
        statGrid.setOpaque(false);

        statGrid.add(createStatCard(
            "TỔNG QUAN NHÂN SỰ",
            String.valueOf(stats.getTotalActiveEmployees()),
            "Tổng số nhân viên",
            new String[]{
                "Nhân viên mới trong tháng: " + stats.getNewEmployeesThisMonth(),
                "Nhân viên đã nghỉ: " + stats.getResignedEmployeesThisMonth()
            }
        ));

        statGrid.add(createStatCard(
            "CHẤM CÔNG HÔM NAY",
            stats.getCheckInsToday() + " / " + stats.getTotalActiveEmployees(),
            "Đã check-in",
            new String[]{
                "Đi muộn: " + stats.getLateCheckInsToday(),
                "Vắng mặt: " + (stats.getTotalActiveEmployees() - stats.getCheckInsToday())
            }
        ));

        statGrid.add(createStatCard(
            "CHI PHÍ LƯƠNG (Tháng này)",
            currencyFormatter.format(stats.getTotalSalaryThisMonth()),
            "Tổng lương thực nhận",
            new String[]{
                "Tổng thưởng: " + currencyFormatter.format(stats.getTotalBonusThisMonth()),
                "Tổng khấu trừ: " + currencyFormatter.format(stats.getTotalDeductionThisMonth())
            }
        ));

        statGrid.add(createStatCard(
            "CÔNG VIỆC CẦN XỬ LÝ",
            String.valueOf(stats.getPendingLeaveRequests()),
            "Đơn nghỉ phép chờ duyệt",
            new String[]{}
        ));

        dashboardPanel.add(statGrid, BorderLayout.CENTER);
        return dashboardPanel;
    }

    private JPanel createStatCard(String title, String mainStat, String mainStatLabel, String[] subStats) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ViewStyles.BORDER),
            BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));
        card.setBackground(ViewStyles.SURFACE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(ViewStyles.MUTED_TEXT);
        card.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        JLabel mainStatLabelComponent = new JLabel(mainStat);
        mainStatLabelComponent.setFont(new Font("Segoe UI", Font.BOLD, 34));
        mainStatLabelComponent.setForeground(ViewStyles.TEXT);
        mainStatLabelComponent.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel mainStatSubLabel = new JLabel(mainStatLabel);
        mainStatSubLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainStatSubLabel.setForeground(ViewStyles.MUTED_TEXT);
        mainStatSubLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(mainStatLabelComponent);
        centerPanel.add(mainStatSubLabel);
        centerPanel.add(Box.createVerticalGlue());
        card.add(centerPanel, BorderLayout.CENTER);

        if (subStats.length > 0) {
            JPanel bottomPanel = new JPanel();
            bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
            bottomPanel.setOpaque(false);
            for (String subStat : subStats) {
                JLabel subLabel = new JLabel(subStat);
                subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                subLabel.setForeground(ViewStyles.MUTED_TEXT);
                bottomPanel.add(subLabel);
            }
            card.add(bottomPanel, BorderLayout.SOUTH);
        }

        return card;
    }

    private JPanel buildWelcomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ViewStyles.APP_BG);

        JPanel welcomeCard = ViewStyles.createSurfacePanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 6, 4, 6);

        JLabel titleLabel = new JLabel("Chào mừng đến với hệ thống Quản lý Nhân sự", SwingConstants.CENTER);
        ViewStyles.styleTitle(titleLabel);
        welcomeCard.add(titleLabel, gbc);

        Account account = SessionManager.getInstance().getCurrentAccount();
        JLabel subLabel = new JLabel(
                account != null
                        ? "Bạn đang đăng nhập với tài khoản " + account.getTenDangNhap() + " - vai trò " + account.getVaiTro()
                        : "Vui lòng chọn chức năng ở thanh điều hướng bên trái",
                SwingConstants.CENTER
        );
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subLabel.setForeground(ViewStyles.MUTED_TEXT);
        gbc.gridy = 1;
        welcomeCard.add(subLabel, gbc);

        panel.add(welcomeCard);
        return panel;
    }

    private void setupMenuActions() {
        Set<String> availableMenus = sidebar.getAvailableMenus();
        for (String menuTitle : availableMenus) {
            JButton menuButton = sidebar.getMenuButton(menuTitle);
            if (menuButton != null) {
                menuButton.addActionListener(e -> switchCard(menuTitle));
            }
        }
    }

    private void switchCard(String menuTitle) {
        String cardName = menuCardMap.get(menuTitle);
        if (cardName != null) {
            cardLayout.show(contentPanel, cardName);
            sidebar.setActiveMenu(menuTitle);
        }
    }

    private void onLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            accountController.logout();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
        }
    }
}
