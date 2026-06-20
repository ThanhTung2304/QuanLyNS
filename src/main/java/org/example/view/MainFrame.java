package org.example.view;

import org.example.controller.AccountController;
import org.example.entity.Account;
import org.example.security.RoleChecker;
import org.example.security.SessionManager;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;

public class MainFrame extends JFrame {
    private static final String CARD_HOME = "HOME";
    private static final String CARD_EMPLOYEE = "EMPLOYEE";
    private static final String CARD_DEPARTMENT = "DEPARTMENT";
    private static final String CARD_ACCOUNT = "ACCOUNT";

    private final AccountController accountController;
    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    public MainFrame() {
        this.accountController = new AccountController();
        this.cardLayout = new CardLayout();
        this.contentPanel = new JPanel(cardLayout);
        initComponents();
    }

    private void initComponents() {
        Account account = SessionManager.getInstance().getCurrentAccount();
        if (account == null) {
            account = new Account();
            account.setTenDangNhap("guest");
        }

        setTitle("Quan ly nhan su");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        setJMenuBar(buildMenuBar());
        add(buildHeader(account), BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);
        buildContentCards();
        add(contentPanel, BorderLayout.CENTER);

        cardLayout.show(contentPanel, CARD_HOME);
    }

    private JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu systemMenu = new JMenu("He thong");

        JMenuItem logoutItem = new JMenuItem("Dang xuat");
        logoutItem.addActionListener(e -> onLogout());

        JMenuItem exitItem = new JMenuItem("Thoat");
        exitItem.addActionListener(e -> System.exit(0));

        systemMenu.add(logoutItem);
        systemMenu.add(exitItem);
        menuBar.add(systemMenu);
        return menuBar;
    }

    private JPanel buildHeader(Account account) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        header.setBackground(new Color(33, 47, 61));

        JLabel welcomeLabel = new JLabel(
                "Xin chao, " + account.getTenDangNhap() + " (" + account.getVaiTro() + ")"
        );
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.add(welcomeLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Dang xuat");
        logoutButton.addActionListener(e -> onLogout());

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionPanel.setOpaque(false);
        actionPanel.add(logoutButton);
        header.add(actionPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(16, 10, 16, 10));
        sidebar.setBackground(new Color(245, 246, 248));

        sidebar.add(createSidebarButton("Trang chu", CARD_HOME));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createSidebarButton("Nhan vien", CARD_EMPLOYEE));

        if (RoleChecker.canManageHr()) {
            sidebar.add(Box.createVerticalStrut(8));
            sidebar.add(createSidebarButton("Phong ban", CARD_DEPARTMENT));
        }

        if (RoleChecker.isAdmin()) {
            sidebar.add(Box.createVerticalStrut(8));
            sidebar.add(createSidebarButton("Tai khoan", CARD_ACCOUNT));
        }

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JButton createSidebarButton(String label, String cardName) {
        JButton button = new JButton(label);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        button.setFocusPainted(false);
        button.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        return button;
    }

    private void buildContentCards() {
        contentPanel.add(buildHomePanel(), CARD_HOME);
        contentPanel.add(placeholderPanel("Quan ly nhan vien"), CARD_EMPLOYEE);
        contentPanel.add(placeholderPanel("Quan ly phong ban"), CARD_DEPARTMENT);
        contentPanel.add(placeholderPanel("Quan ly tai khoan"), CARD_ACCOUNT);
    }

    private JPanel buildHomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel label = new JLabel("Chao mung den voi he thong Quan ly nhan su");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        panel.add(label);
        return panel;
    }

    private JPanel placeholderPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel label = new JLabel(title + " - chua trien khai");
        label.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        panel.add(label);
        return panel;
    }

    private void onLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Ban co chac muon dang xuat?",
                "Xac nhan",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            accountController.logout();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
        }
    }
}
