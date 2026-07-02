package org.example.view.layout;

import org.example.controller.ChamCongController;
import org.example.entity.Account;
import org.example.entity.ChamCong;
import org.example.security.SessionManager;
import org.example.view.util.ViewStyles;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Header extends JPanel {

    private final ChamCongController chamCongController;
    private JButton btnChamCong; // Một nút duy nhất
    private JLabel lblCheckInStatus;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Header(Runnable onLogout) {
        this.chamCongController = new ChamCongController();
        initComponents(onLogout);
        updateCheckInStatus();
    }

    private void initComponents(Runnable onLogout) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        setBackground(Color.WHITE);

        Account account = SessionManager.getInstance().getCurrentAccount();
        if (account == null) {
            account = new Account();
            account.setTenDangNhap("guest");
        }

        JLabel welcomeLabel = new JLabel(
                "Xin chào, " + account.getTenDangNhap() + " (" + account.getVaiTro() + ")"
        );
        welcomeLabel.setForeground(ViewStyles.TEXT);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        add(welcomeLabel, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        btnChamCong = new JButton("Chấm Công");
        lblCheckInStatus = new JLabel();
        lblCheckInStatus.setForeground(new Color(22, 101, 52));
        lblCheckInStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        actionPanel.add(btnChamCong);
        actionPanel.add(lblCheckInStatus);
        
        actionPanel.add(new JSeparator(SwingConstants.VERTICAL));
        actionPanel.add(Box.createHorizontalStrut(5));

        JButton logoutButton = new JButton("Đăng xuất");
        actionPanel.add(logoutButton);

        ViewStyles.stylePrimaryButton(btnChamCong);
        ViewStyles.styleDangerButton(logoutButton);
        
        add(actionPanel, BorderLayout.EAST);

        logoutButton.addActionListener(e -> onLogout.run());
        btnChamCong.addActionListener(e -> handleChamCong());
    }

    private void handleChamCong() {
        String currentAction = btnChamCong.getText();
        String maNv = SessionManager.getInstance().getCurrentAccount().getMaNv();

        try {
            if ("Check-in".equals(currentAction)) {
                chamCongController.checkIn(maNv);
                JOptionPane.showMessageDialog(this, "Check-in thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else if ("Check-out".equals(currentAction)) {
                chamCongController.checkOut(maNv);
                JOptionPane.showMessageDialog(this, "Check-out thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
            // Sau mỗi hành động thành công, cập nhật lại trạng thái
            updateCheckInStatus();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi Chấm Công", JOptionPane.ERROR_MESSAGE);
            // Nếu có lỗi (ví dụ: admin vừa xóa), cũng cập nhật lại trạng thái cho chắc
            updateCheckInStatus();
        }
    }

    private void updateCheckInStatus() {
        try {
            String maNv = SessionManager.getInstance().getCurrentAccount().getMaNv();
            Optional<ChamCong> todayChamCongOpt = chamCongController.getTodayChamCong(maNv);

            if (todayChamCongOpt.isPresent()) {
                ChamCong todayChamCong = todayChamCongOpt.get();
                if (todayChamCong.getGioRa() != null) {
                    // Đã check-in và check-out
                    btnChamCong.setText("Đã chấm công");
                    btnChamCong.setEnabled(false);
                    lblCheckInStatus.setText(buildTodayStatusText(todayChamCong));
                } else {
                    // Đã check-in, chưa check-out
                    btnChamCong.setText("Check-out");
                    btnChamCong.setEnabled(true);
                    lblCheckInStatus.setText(buildTodayStatusText(todayChamCong));
                }
            } else {
                // Chưa check-in
                btnChamCong.setText("Check-in");
                btnChamCong.setEnabled(true);
                lblCheckInStatus.setText("Hôm nay chưa chấm công");
            }
        } catch (Exception e) {
            btnChamCong.setText("Lỗi");
            btnChamCong.setEnabled(false);
            lblCheckInStatus.setText("Lỗi tải trạng thái");
        }
        
        revalidate();
        repaint();
    }

    private String buildTodayStatusText(ChamCong chamCong) {
        String ngay = chamCong.getNgay() != null ? chamCong.getNgay().format(dateFormatter) : "";
        String gioVao = chamCong.getGioVao() != null ? chamCong.getGioVao().format(timeFormatter) : "--:--:--";
        String gioRa = chamCong.getGioRa() != null ? chamCong.getGioRa().format(timeFormatter) : "Chưa check-out";
        String trangThai = chamCong.getTrangThai() != null ? chamCong.getTrangThai().toString() : "";
        String soGioLam = chamCong.getGioRa() != null ? " | Số giờ: " + chamCong.getSoGioLam() : "";

        return "Ngày " + ngay
                + " | Vào: " + gioVao
                + " | Ra: " + gioRa
                + " | Trạng thái: " + trangThai
                + soGioLam;
    }
}
