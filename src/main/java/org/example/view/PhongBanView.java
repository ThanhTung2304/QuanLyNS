package org.example.view;

import org.example.controller.PhongBanController;
import org.example.entity.NhanVien;
import org.example.entity.PhongBan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PhongBanView extends JPanel {

    private final PhongBanController phongBanController;
    private JTable phongBanTable;
    private DefaultTableModel tableModel;
    private boolean isDataLoaded = false;

    public PhongBanView() {
        this.phongBanController = new PhongBanController();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Quản lý Phòng Ban", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Mã Phòng", "Tên Phòng", "Trưởng phòng", "Số lượng NV"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        phongBanTable = new JTable(tableModel);
        phongBanTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(phongBanTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdd = new JButton("Thêm mới");
        JButton btnUpdate = new JButton("Cập nhật");
        JButton btnDelete = new JButton("Xóa");
        JButton btnAssign = new JButton("Gán Trưởng phòng");
        JButton btnDetails = new JButton("Xem chi tiết");
        JButton btnRefresh = new JButton("Refresh");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnAssign);
        buttonPanel.add(btnDetails);
        buttonPanel.add(btnRefresh);
        add(buttonPanel, BorderLayout.SOUTH);

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                if (!isDataLoaded) {
                    loadPhongBanData();
                    isDataLoaded = true;
                }
            }
        });

        btnAdd.addActionListener(e -> showPhongBanDialog(null));
        btnUpdate.addActionListener(e -> {
            int selectedRow = phongBanTable.getSelectedRow();
            if (selectedRow != -1) {
                String maPhong = (String) tableModel.getValueAt(selectedRow, 0);
                phongBanController.getPhongBanById(maPhong)
                        .ifPresentOrElse(
                            this::showPhongBanDialog,
                            () -> JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin chi tiết cho phòng ban này.", "Lỗi", JOptionPane.ERROR_MESSAGE)
                        );
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng ban để cập nhật.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });
        btnDelete.addActionListener(e -> deletePhongBan());
        btnAssign.addActionListener(e -> showAssignTruongPhongDialog());
        btnDetails.addActionListener(e -> showDetailsDialog());
        btnRefresh.addActionListener(e -> loadPhongBanData());
    }

    private void loadPhongBanData() {
        try {
            List<PhongBan> phongBanList = phongBanController.getAllPhongBan();
            updateTable(phongBanList);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            String detailedMessage = "Lỗi tải dữ liệu phòng ban: " + e.getMessage();
            if (cause != null) {
                detailedMessage += "\n\nNguyên nhân gốc: " + cause.getMessage();
            }
            JOptionPane.showMessageDialog(this, detailedMessage, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<PhongBan> phongBanList) {
        tableModel.setRowCount(0);
        for (PhongBan pb : phongBanList) {
            tableModel.addRow(new Object[]{
                    pb.getMaPhong(),
                    pb.getTenPhong(),
                    pb.getTenTruongPhong() != null ? pb.getTenTruongPhong() : "Chưa có",
                    pb.getSoLuongNhanVien()
            });
        }
    }

    private void deletePhongBan() {
        int selectedRow = phongBanTable.getSelectedRow();
        if (selectedRow != -1) {
            String maPhong = (String) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa phòng ban này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    phongBanController.deletePhongBan(maPhong);
                    loadPhongBanData();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng ban để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showPhongBanDialog(PhongBan phongBan) {
        boolean isUpdating = phongBan != null;
        JTextField txtMaPhong = new JTextField(isUpdating ? phongBan.getMaPhong() : "");
        JTextField txtTenPhong = new JTextField(isUpdating ? phongBan.getTenPhong() : "");
        JTextArea txtMoTa = new JTextArea(isUpdating ? phongBan.getMoTa() : "", 3, 20);

        if (isUpdating) {
            txtMaPhong.setEditable(false);
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Mã Phòng (*):"));
        panel.add(txtMaPhong);
        panel.add(new JLabel("Tên Phòng (*):"));
        panel.add(txtTenPhong);
        panel.add(new JLabel("Mô tả:"));
        panel.add(new JScrollPane(txtMoTa));

        int result = JOptionPane.showConfirmDialog(this, panel, isUpdating ? "Cập nhật Phòng Ban" : "Thêm mới Phòng Ban", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                PhongBan pb = new PhongBan(
                        txtMaPhong.getText(),
                        txtTenPhong.getText(),
                        txtMoTa.getText()
                );
                if (isUpdating) {
                    phongBanController.updatePhongBan(pb);
                } else {
                    phongBanController.createPhongBan(pb);
                }
                loadPhongBanData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showDetailsDialog() {
        int selectedRow = phongBanTable.getSelectedRow();
        if (selectedRow != -1) {
            String maPhong = (String) tableModel.getValueAt(selectedRow, 0);
            phongBanController.getPhongBanById(maPhong).ifPresent(pb -> {
                JTextArea textArea = new JTextArea(10, 40);
                textArea.setEditable(false);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                
                StringBuilder details = new StringBuilder();
                details.append("Mã phòng ban:\t").append(pb.getMaPhong()).append("\n");
                details.append("Tên phòng ban:\t").append(pb.getTenPhong()).append("\n");
                details.append("Trưởng phòng:\t").append(pb.getTenTruongPhong() != null ? pb.getTenTruongPhong() : "Chưa có").append("\n");
                details.append("Số lượng nhân viên:\t").append(pb.getSoLuongNhanVien()).append("\n\n");
                details.append("Mô tả:\n").append(pb.getMoTa() != null && !pb.getMoTa().isBlank() ? pb.getMoTa() : "Không có mô tả.");
                
                textArea.setText(details.toString());
                JScrollPane scrollPane = new JScrollPane(textArea);
                JOptionPane.showMessageDialog(this, scrollPane, "Chi tiết Phòng ban", JOptionPane.INFORMATION_MESSAGE);
            });
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng ban để xem chi tiết.", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showAssignTruongPhongDialog() {
        int selectedRow = phongBanTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng ban để gán trưởng phòng.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maPhong = (String) tableModel.getValueAt(selectedRow, 0);
        String tenPhong = (String) tableModel.getValueAt(selectedRow, 1);

        try {
            List<NhanVien> nhanVienList = phongBanController.getNhanVienByPhongBan(maPhong);
            if (nhanVienList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Phòng ban này chưa có nhân viên nào.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JComboBox<NhanVien> cbNhanVien = new JComboBox<>(nhanVienList.toArray(new NhanVien[0]));
            cbNhanVien.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof NhanVien) {
                        NhanVien nv = (NhanVien) value;
                        setText(nv.getHoTen() + " (" + nv.getMaNv() + ")");
                    }
                    return this;
                }
            });

            int result = JOptionPane.showConfirmDialog(this, cbNhanVien, "Chọn trưởng phòng cho " + tenPhong, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                NhanVien selectedNhanVien = (NhanVien) cbNhanVien.getSelectedItem();
                if (selectedNhanVien != null) {
                    int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn bổ nhiệm " + selectedNhanVien.getHoTen() + " làm trưởng phòng?\nTrưởng phòng cũ (nếu có) sẽ bị hạ cấp.", "Xác nhận", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        phongBanController.assignTruongPhong(maPhong, selectedNhanVien.getMaNv());
                        JOptionPane.showMessageDialog(this, "Gán trưởng phòng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        loadPhongBanData();
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy danh sách nhân viên: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}