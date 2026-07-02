package org.example.view;

import org.example.controller.AccountController;
import org.example.entity.Account;
import org.example.exception.BusinessException;
import org.example.exception.DataAccessException;
import org.example.view.util.ViewStyles;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Màn hình đăng nhập - điểm khởi đầu của ứng dụng.
 * Gọi AccountController để xác thực, mở MainFrame nếu thành công.
 */
public class LoginForm extends JFrame {

    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private JButton btnDangNhap;
    private JButton btnThoat;

    private final AccountController accountController;

    public LoginForm() {
        this.accountController = new AccountController();
        initComponents();
    }

    private void initComponents() {
        setTitle("Đăng nhập - Hệ thống Quản lý Nhân sự");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(620, 420);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel rootPanel = new JPanel(new GridBagLayout());
        rootPanel.setBackground(ViewStyles.APP_BG);
        rootPanel.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JPanel mainPanel = ViewStyles.createSurfacePanel(new GridBagLayout());
        mainPanel.setPreferredSize(new Dimension(540, 320));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 8, 7, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel lblTitle = new JLabel("Đăng nhập hệ thống", SwingConstants.CENTER);
        ViewStyles.styleTitle(lblTitle);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(lblTitle, gbc);

        gbc.gridwidth = 2;
        gbc.gridy = 1;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Tên đăng nhập"), gbc);

        txtTenDangNhap = new JTextField(34);
        gbc.gridy = 2;
        gbc.gridx = 0;
        mainPanel.add(txtTenDangNhap, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Mật khẩu"), gbc);

        txtMatKhau = new JPasswordField(34);
        gbc.gridy = 4;
        gbc.gridx = 0;
        mainPanel.add(txtMatKhau, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);
        btnDangNhap = new JButton("Đăng nhập");
        btnThoat = new JButton("Thoát");
        buttonPanel.add(btnDangNhap);
        buttonPanel.add(btnThoat);

        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(18, 8, 7, 8);
        mainPanel.add(buttonPanel, gbc);

        ViewStyles.applyTree(mainPanel);
        Dimension inputSize = new Dimension(420, 42);
        txtTenDangNhap.setPreferredSize(inputSize);
        txtTenDangNhap.setMinimumSize(inputSize);
        txtMatKhau.setPreferredSize(inputSize);
        txtMatKhau.setMinimumSize(inputSize);
        ViewStyles.stylePrimaryButton(btnDangNhap);
        ViewStyles.styleDangerButton(btnThoat);

        rootPanel.add(mainPanel);
        add(rootPanel);

        btnDangNhap.addActionListener(this::onDangNhap);
        btnThoat.addActionListener(e -> System.exit(0));

        txtMatKhau.addActionListener(this::onDangNhap);
        getRootPane().setDefaultButton(btnDangNhap);
    }

    private void onDangNhap(ActionEvent e) {
        String tenDangNhap = txtTenDangNhap.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword());

        if (tenDangNhap.isEmpty() || matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu",
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnDangNhap.setEnabled(false);
        try {
            Account account = accountController.login(tenDangNhap, matKhau);

            JOptionPane.showMessageDialog(this,
                    "Đăng nhập thành công! Vai trò: " + account.getVaiTro(),
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);

            openMainFrame();

        } catch (BusinessException ex) {
            // Lỗi nghiệp vụ: sai tài khoản/mật khẩu, tài khoản bị khoá...
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
            txtMatKhau.setText("");

        } catch (DataAccessException ex) {
            // Lỗi hệ thống/DB: không lộ chi tiết kỹ thuật cho người dùng
            JOptionPane.showMessageDialog(this,
                    "Không thể kết nối đến hệ thống. Vui lòng thử lại sau.",
                    "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);

        } finally {
            btnDangNhap.setEnabled(true);
        }
    }

    private void openMainFrame() {
        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
        this.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
