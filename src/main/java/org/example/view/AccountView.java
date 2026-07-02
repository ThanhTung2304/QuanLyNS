package org.example.view;

import org.example.controller.AccountController;
import org.example.controller.NhanVienController;
import org.example.entity.Account;
import org.example.entity.NhanVien;
import org.example.security.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AccountView extends JPanel {

    private JTable accountTable;
    private DefaultTableModel tableModel;
    private JButton btnAddAccount;
    private JButton btnResetPassword;
    private JButton btnToggleLock;
    private JButton btnChangeRole;
    private JButton btnDetails;
    private JButton btnRefresh;

    private final AccountController accountController;
    private final NhanVienController nhanVienController;
    private boolean isDataLoaded = false;

    public AccountView() {
        this.accountController = new AccountController();
        this.nhanVienController = new NhanVienController();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Quản lý Tài khoản", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Mã TK", "Tên đăng nhập", "Mã nhân viên", "Vai trò", "Trạng thái"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        accountTable = new JTable(tableModel);
        accountTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(accountTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnAddAccount = new JButton("Tạo tài khoản");
        btnResetPassword = new JButton("Đặt lại mật khẩu");
        btnToggleLock = new JButton("Khóa/Mở khóa");
        btnChangeRole = new JButton("Đổi vai trò");
        btnDetails = new JButton("Chi tiết");
        btnRefresh = new JButton("Refresh");

        buttonPanel.add(btnAddAccount);
        buttonPanel.add(btnResetPassword);
        buttonPanel.add(btnToggleLock);
        buttonPanel.add(btnChangeRole);
        buttonPanel.add(btnDetails);
        buttonPanel.add(btnRefresh);

        add(buttonPanel, BorderLayout.SOUTH);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if (!isDataLoaded) {
                    refreshData();
                    isDataLoaded = true;
                }
            }
        });

        btnAddAccount.addActionListener(e -> addAccount());
        btnResetPassword.addActionListener(e -> resetPassword());
        btnToggleLock.addActionListener(e -> toggleAccountLock());
        btnChangeRole.addActionListener(e -> changeRole());
        btnDetails.addActionListener(e -> showDetailsDialog());
        btnRefresh.addActionListener(e -> {
            refreshData();
            JOptionPane.showMessageDialog(this, "Đã tải lại danh sách tài khoản.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        try {
            List<Account> accounts = accountController.getAllAccounts();
            for (Account acc : accounts) {
                tableModel.addRow(new Object[]{
                        acc.getMaTk(),
                        acc.getTenDangNhap(),
                        acc.getMaNv(),
                        acc.getVaiTro(),
                        acc.getTrangThai()
                });
            }
        } catch (Exception ex) {
            Throwable cause = ex.getCause();
            String detailedMessage = "Lỗi khi tải dữ liệu tài khoản: " + ex.getMessage();
            if (cause != null) {
                detailedMessage += "\n\nNguyên nhân gốc: " + cause.getMessage();
            }
            JOptionPane.showMessageDialog(this, detailedMessage, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showDetailsDialog() {
        int selectedRow = accountTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một tài khoản để xem chi tiết.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer maTk = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        Optional<Account> accountOpt = accountController.getAllAccounts().stream()
                .filter(acc -> acc.getMaTk().equals(maTk))
                .findFirst();

        if (accountOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin tài khoản.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Account account = accountOpt.get();

        JPanel detailsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        detailsPanel.add(new JLabel("Mã tài khoản:"));
        detailsPanel.add(new JLabel(String.valueOf(account.getMaTk())));
        detailsPanel.add(new JLabel("Tên đăng nhập:"));
        detailsPanel.add(new JLabel(account.getTenDangNhap()));
        detailsPanel.add(new JLabel("Mã nhân viên:"));
        detailsPanel.add(new JLabel(account.getMaNv()));
        detailsPanel.add(new JLabel("Vai trò:"));
        detailsPanel.add(new JLabel(account.getVaiTro().toString()));
        detailsPanel.add(new JLabel("Trạng thái:"));
        detailsPanel.add(new JLabel(account.getTrangThai().toString()));

        detailsPanel.add(new JLabel("Mật khẩu:"));
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JPasswordField passwordField = new JPasswordField(account.getMatKhau());
        passwordField.setEditable(false);
        JToggleButton showHideButton = new JToggleButton("Hiện");
        char defaultEchoChar = passwordField.getEchoChar();

        showHideButton.addActionListener(e -> {
            if (showHideButton.isSelected()) {
                passwordField.setEchoChar((char) 0);
                showHideButton.setText("Ẩn");
            } else {
                passwordField.setEchoChar(defaultEchoChar);
                showHideButton.setText("Hiện");
            }
        });
        passwordPanel.add(passwordField);
        passwordPanel.add(showHideButton);
        detailsPanel.add(passwordPanel);

        JOptionPane.showMessageDialog(this, detailsPanel, "Chi tiết tài khoản", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addAccount() {
        if (SessionManager.getInstance().getCurrentRole() != Account.Role.ADMIN) {
            JOptionPane.showMessageDialog(this,
                    "Chỉ tài khoản ADMIN mới được tạo tài khoản cho nhân viên khác.",
                    "Không có quyền",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<NhanVien> availableEmployees = getEmployeesWithoutAccount();
        if (availableEmployees.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Không có nhân viên nào đủ điều kiện tạo tài khoản.\n"
                            + "Mỗi nhân viên chỉ được có một tài khoản. Hãy thêm nhân viên mới trước, hoặc kiểm tra danh sách tài khoản hiện có.",
                    "Không thể tạo tài khoản",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<NhanVien> cbNhanVien = new JComboBox<>(availableEmployees.toArray(new NhanVien[0]));
        cbNhanVien.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof NhanVien nhanVien) {
                    setText(nhanVien.getMaNv() + " - " + nhanVien.getHoTen());
                }
                return this;
            }
        });

        JTextField txtTenDangNhap = new JTextField();
        JPasswordField txtMatKhau = new JPasswordField();
        JComboBox<Account.Role> cbVaiTro = new JComboBox<>(Account.Role.values());

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.add(new JLabel("Nhân viên:"));
        formPanel.add(cbNhanVien);
        formPanel.add(new JLabel("Tên đăng nhập:"));
        formPanel.add(txtTenDangNhap);
        formPanel.add(new JLabel("Mật khẩu:"));
        formPanel.add(txtMatKhau);
        formPanel.add(new JLabel("Vai trò:"));
        formPanel.add(cbVaiTro);

        int result = JOptionPane.showConfirmDialog(this, formPanel, "Tạo tài khoản mới",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                NhanVien selectedNhanVien = (NhanVien) cbNhanVien.getSelectedItem();
                if (selectedNhanVien == null) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String maNv = selectedNhanVien.getMaNv();
                String tenDangNhap = txtTenDangNhap.getText().trim();
                String matKhau = new String(txtMatKhau.getPassword());
                Account.Role vaiTro = (Account.Role) cbVaiTro.getSelectedItem();

                accountController.createAccount(maNv, tenDangNhap, matKhau, vaiTro);

                JOptionPane.showMessageDialog(this, "Tạo tài khoản thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi tạo tài khoản: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private List<NhanVien> getEmployeesWithoutAccount() {
        List<Account> accounts = accountController.getAllAccounts();
        Set<String> employeeIdsWithAccount = new HashSet<>();
        for (Account account : accounts) {
            employeeIdsWithAccount.add(account.getMaNv());
        }

        return nhanVienController.getAllNhanVien().stream()
                .filter(nhanVien -> nhanVien.getMaNv() != null)
                .filter(nhanVien -> !employeeIdsWithAccount.contains(nhanVien.getMaNv()))
                .toList();
    }

    private void resetPassword() {
        int selectedRow = accountTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một tài khoản để đặt lại mật khẩu.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer maTk = (Integer) tableModel.getValueAt(selectedRow, 0);
        String tenDangNhap = (String) tableModel.getValueAt(selectedRow, 1);

        JPasswordField txtMatKhauMoi = new JPasswordField();
        Object[] message = {
            "Nhập mật khẩu mới cho tài khoản '" + tenDangNhap + "':", txtMatKhauMoi
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Đặt lại mật khẩu", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            String matKhauMoi = new String(txtMatKhauMoi.getPassword());
            try {
                accountController.resetPassword(maTk, matKhauMoi);
                JOptionPane.showMessageDialog(this, "Đặt lại mật khẩu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void toggleAccountLock() {
        int selectedRow = accountTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một tài khoản để khóa hoặc mở khóa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Integer maTk = (Integer) tableModel.getValueAt(selectedRow, 0);
        Account.Status currentStatus = (Account.Status) tableModel.getValueAt(selectedRow, 4);
        
        String action = (currentStatus == Account.Status.HOAT_DONG) ? "Khóa" : "Mở khóa";
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn " + action.toLowerCase() + " tài khoản này không?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (currentStatus == Account.Status.HOAT_DONG) {
                    accountController.lockAccount(maTk);
                } else {
                    accountController.unlockAccount(maTk);
                }
                JOptionPane.showMessageDialog(this, "Đã " + action.toLowerCase() + " tài khoản thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void changeRole() {
        int selectedRow = accountTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một tài khoản để đổi vai trò.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer maTk = (Integer) tableModel.getValueAt(selectedRow, 0);
        Account.Role currentRole = (Account.Role) tableModel.getValueAt(selectedRow, 3);

        JComboBox<Account.Role> cbRoles = new JComboBox<>(Account.Role.values());
        cbRoles.setSelectedItem(currentRole);

        int result = JOptionPane.showConfirmDialog(this, cbRoles, "Chọn vai trò mới",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            Account.Role newRole = (Account.Role) cbRoles.getSelectedItem();
            if (newRole != currentRole) {
                try {
                    accountController.changeRole(maTk, newRole);
                    JOptionPane.showMessageDialog(this, "Đổi vai trò thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    refreshData();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi đổi vai trò: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
