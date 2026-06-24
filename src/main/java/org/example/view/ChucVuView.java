package org.example.view;

import org.example.controller.ChucVuController;
import org.example.entity.ChucVu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

public class ChucVuView extends JPanel {

    private final ChucVuController chucVuController;
    private JTable chucVuTable;
    private DefaultTableModel tableModel;
    private boolean isDataLoaded = false;

    public ChucVuView() {
        this.chucVuController = new ChucVuController();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Quản lý Chức Vụ", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Mã Chức Vụ", "Tên Chức Vụ", "Mô Tả"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        chucVuTable = new JTable(tableModel);
        chucVuTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(chucVuTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdd = new JButton("Thêm mới");
        JButton btnUpdate = new JButton("Cập nhật");
        JButton btnDelete = new JButton("Xóa");
        JButton btnRefresh = new JButton("Refresh");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        add(buttonPanel, BorderLayout.SOUTH);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if (!isDataLoaded) {
                    loadChucVuData();
                    isDataLoaded = true;
                }
            }
        });

        btnAdd.addActionListener(e -> showChucVuDialog(null));
        btnUpdate.addActionListener(e -> {
            int selectedRow = chucVuTable.getSelectedRow();
            if (selectedRow != -1) {
                String maChucVu = (String) tableModel.getValueAt(selectedRow, 0);
                chucVuController.getChucVuById(maChucVu)
                        .ifPresentOrElse(
                                this::showChucVuDialog,
                                () -> JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin chức vụ này.", "Lỗi", JOptionPane.ERROR_MESSAGE)
                        );
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một chức vụ để cập nhật.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });
        btnDelete.addActionListener(e -> deleteChucVu());
        btnRefresh.addActionListener(e -> loadChucVuData());
    }

    private void loadChucVuData() {
        try {
            List<ChucVu> chucVuList = chucVuController.getAllChucVu();
            updateTable(chucVuList);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            String detailedMessage = "Lỗi tải dữ liệu chức vụ: " + e.getMessage();
            if (cause != null) {
                detailedMessage += "\n\nNguyên nhân gốc: " + cause.getMessage();
            }
            JOptionPane.showMessageDialog(this, detailedMessage, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<ChucVu> chucVuList) {
        tableModel.setRowCount(0);
        for (ChucVu cv : chucVuList) {
            tableModel.addRow(new Object[]{
                    cv.getMaChucVu(),
                    cv.getTenChucVu(),
                    cv.getMoTa()
            });
        }
    }

    private void deleteChucVu() {
        int selectedRow = chucVuTable.getSelectedRow();
        if (selectedRow != -1) {
            String maChucVu = (String) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa chức vụ này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    chucVuController.deleteChucVu(maChucVu);
                    loadChucVuData();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một chức vụ để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showChucVuDialog(ChucVu chucVu) {
        boolean isUpdating = chucVu != null;
        JTextField txtMaChucVu = new JTextField(isUpdating ? chucVu.getMaChucVu() : "");
        JTextField txtTenChucVu = new JTextField(isUpdating ? chucVu.getTenChucVu() : "");
        JTextArea txtMoTa = new JTextArea(isUpdating ? chucVu.getMoTa() : "", 3, 20);

        if (isUpdating) {
            txtMaChucVu.setEditable(false);
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Mã Chức Vụ (*):"));
        panel.add(txtMaChucVu);
        panel.add(new JLabel("Tên Chức Vụ (*):"));
        panel.add(txtTenChucVu);
        panel.add(new JLabel("Mô tả:"));
        panel.add(new JScrollPane(txtMoTa));

        int result = JOptionPane.showConfirmDialog(this, panel, isUpdating ? "Cập nhật Chức Vụ" : "Thêm mới Chức Vụ", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                ChucVu cv = new ChucVu(
                        txtMaChucVu.getText(),
                        txtTenChucVu.getText(),
                        txtMoTa.getText()
                );
                if (isUpdating) {
                    chucVuController.updateChucVu(cv);
                } else {
                    chucVuController.createChucVu(cv);
                }
                loadChucVuData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}