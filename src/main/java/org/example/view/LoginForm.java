package org.example.view;

import org.example.controller.AccountController;
import org.example.entity.Account;
import org.example.exception.BusinessException;
import org.example.exception.DataAccessException;

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
        setSize(420, 280);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tiêu đề
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP HỆ THỐNG", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(lblTitle, gbc);

        // Tên đăng nhập
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Tên đăng nhập:"), gbc);

        txtTenDangNhap = new JTextField(18);
        gbc.gridx = 1;
        mainPanel.add(txtTenDangNhap, gbc);

        // Mật khẩu
        gbc.gridy = 2;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Mật khẩu:"), gbc);

        txtMatKhau = new JPasswordField(18);
        gbc.gridx = 1;
        mainPanel.add(txtMatKhau, gbc);

        // Nút hành động
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnDangNhap = new JButton("Đăng nhập");
        btnThoat = new JButton("Thoát");
        buttonPanel.add(btnDangNhap);
        buttonPanel.add(btnThoat);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);

        // Sự kiện
        btnDangNhap.addActionListener(this::onDangNhap);
        btnThoat.addActionListener(e -> System.exit(0));

        // Cho phép nhấn Enter ở ô mật khẩu để đăng nhập luôn
        txtMatKhau.addActionListener(this::onDangNhap);
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
