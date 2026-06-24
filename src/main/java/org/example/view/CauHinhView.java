package org.example.view;

import org.example.controller.CauHinhController;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CauHinhView extends JPanel {

    private final CauHinhController cauHinhController;
    private JTextField txtGioVao;
    private JTextField txtGioRa;
    private JTextField txtSoPhutDiMuon;

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public CauHinhView() {
        this.cauHinhController = new CauHinhController();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Cấu hình Chấm công", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        
        formPanel.add(new JLabel("Giờ bắt đầu làm việc (HH:mm:ss):"));
        txtGioVao = new JTextField();
        formPanel.add(txtGioVao);

        formPanel.add(new JLabel("Giờ kết thúc làm việc (HH:mm:ss):"));
        txtGioRa = new JTextField();
        formPanel.add(txtGioRa);

        formPanel.add(new JLabel("Số phút đi muộn cho phép:"));
        txtSoPhutDiMuon = new JTextField();
        formPanel.add(txtSoPhutDiMuon);

        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerWrapper.add(formPanel);
        add(centerWrapper, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnSave = new JButton("Lưu cấu hình");
        btnSave.addActionListener(e -> saveData());
        buttonPanel.add(btnSave);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        try {
            txtGioVao.setText(cauHinhController.getGioVaoSang().format(timeFormatter));
            txtGioRa.setText(cauHinhController.getGioRaChieu().format(timeFormatter));
            txtSoPhutDiMuon.setText(String.valueOf(cauHinhController.getSoPhutDiMuonChoPhep()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải cấu hình: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveData() {
        try {
            LocalTime gioVao = LocalTime.parse(txtGioVao.getText().trim(), timeFormatter);
            LocalTime gioRa = LocalTime.parse(txtGioRa.getText().trim(), timeFormatter);
            int soPhut = Integer.parseInt(txtSoPhutDiMuon.getText().trim());

            cauHinhController.setGioVaoSang(gioVao);
            cauHinhController.setGioRaChieu(gioRa);
            cauHinhController.setSoPhutDiMuonChoPhep(soPhut);

            JOptionPane.showMessageDialog(this, "Lưu cấu hình thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại định dạng.\nChi tiết: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}