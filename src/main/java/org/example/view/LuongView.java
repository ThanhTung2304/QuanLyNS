package org.example.view;

import org.example.controller.LuongController;
import org.example.entity.BangLuong;
import org.example.view.util.CurrencyRenderer; // <-- Đã import

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

public class LuongView extends JPanel {

    private final LuongController luongController;
    private JTabbedPane tabbedPane;

    private JTable tinhLuongTable;
    private DefaultTableModel tinhLuongTableModel;
    private JComboBox<Integer> cbThangTinh;
    private JComboBox<Integer> cbNamTinh;
    private List<BangLuong> currentPayrollList = new ArrayList<>();
    private boolean isUpdatingTable = false;

    private JTable lichSuTable;
    private DefaultTableModel lichSuTableModel;
    private JComboBox<Integer> cbThangLichSu;
    private JComboBox<Integer> cbNamLichSu;
    private JTextField txtSearchLichSu;
    private TableRowSorter<DefaultTableModel> sorter;

    public LuongView() {
        this.luongController = new LuongController();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Tính lương tháng", createTinhLuongPanel());
        tabbedPane.addTab("Lịch sử & Tra cứu", createLichSuPanel());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createTinhLuongPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Tháng:"));
        Integer[] months = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        cbThangTinh = new JComboBox<>(months);
        cbThangTinh.setSelectedItem(LocalDate.now().getMonthValue());
        topPanel.add(cbThangTinh);

        topPanel.add(new JLabel("Năm:"));
        Integer[] years = new Integer[10];
        int currentYear = LocalDate.now().getYear();
        for (int i = 0; i < 10; i++) years[i] = currentYear - 5 + i;
        cbNamTinh = new JComboBox<>(years);
        cbNamTinh.setSelectedItem(currentYear);
        topPanel.add(cbNamTinh);

        JButton btnCalculate = new JButton("Tải & Tính lương sơ bộ");
        topPanel.add(btnCalculate);
        panel.add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"Mã NV", "Họ Tên", "Lương CB", "Phụ Cấp", "Ngày Công", "Thưởng", "Khấu Trừ", "Thực Lĩnh"};
        tinhLuongTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 5 || column == 6;
            }
        };
        tinhLuongTable = new JTable(tinhLuongTableModel);
        setupCurrencyColumns(tinhLuongTable, true);

        tinhLuongTableModel.addTableModelListener(e -> {
            if (isUpdatingTable) return;
            if (e.getType() == TableModelEvent.UPDATE) {
                SwingUtilities.invokeLater(() -> recalculateThucLinh(e.getFirstRow()));
            }
        });
        panel.add(new JScrollPane(tinhLuongTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Chốt và Lưu Bảng Lương");
        bottomPanel.add(btnSave);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        btnCalculate.addActionListener(e -> calculatePayroll());
        btnSave.addActionListener(e -> finalizePayroll());

        return panel;
    }

    private JPanel createLichSuPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Tháng:"));
        Integer[] months = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        cbThangLichSu = new JComboBox<>(months);
        cbThangLichSu.setSelectedItem(LocalDate.now().getMonthValue());
        topPanel.add(cbThangLichSu);

        topPanel.add(new JLabel("Năm:"));
        Integer[] years = new Integer[10];
        int currentYear = LocalDate.now().getYear();
        for (int i = 0; i < 10; i++) years[i] = currentYear - 5 + i;
        cbNamLichSu = new JComboBox<>(years);
        cbNamLichSu.setSelectedItem(currentYear);
        topPanel.add(cbNamLichSu);

        JButton btnSearch = new JButton("Xem Lịch Sử");
        topPanel.add(btnSearch);
        
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(new JLabel("Lọc theo Tên/Mã NV:"));
        txtSearchLichSu = new JTextField(20);
        topPanel.add(txtSearchLichSu);
        panel.add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"Mã NV", "Họ Tên", "Ngày Công", "Lương CB", "Phụ Cấp", "Thưởng", "Khấu Trừ", "Thực Lĩnh"};
        lichSuTableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        lichSuTable = new JTable(lichSuTableModel);
        setupCurrencyColumns(lichSuTable, false);
        
        sorter = new TableRowSorter<>(lichSuTableModel);
        lichSuTable.setRowSorter(sorter);
        panel.add(new JScrollPane(lichSuTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnReopen = new JButton("Mở lại & Sửa đổi");
        JButton btnDelete = new JButton("Hủy & Tính lại từ đầu");
        bottomPanel.add(btnReopen);
        bottomPanel.add(btnDelete);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        btnSearch.addActionListener(e -> findPayrollHistory());
        txtSearchLichSu.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });
        btnReopen.addActionListener(e -> reopenPayroll());
        btnDelete.addActionListener(e -> deletePayroll());

        return panel;
    }

    private void calculatePayroll() {
        int thang = (Integer) cbThangTinh.getSelectedItem();
        int nam = (Integer) cbNamTinh.getSelectedItem();
        try {
            currentPayrollList = luongController.calculatePayroll(thang, nam);
            if (currentPayrollList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có dữ liệu hoặc lương tháng này đã được chốt.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                tinhLuongTableModel.setRowCount(0);
                return;
            }
            isUpdatingTable = true;
            tinhLuongTableModel.setRowCount(0);
            for (BangLuong bl : currentPayrollList) {
                tinhLuongTableModel.addRow(new Object[]{
                        bl.getMaNv(), bl.getHoTen(), bl.getLuongCb(), bl.getPhuCap(),
                        bl.getSoNgayCong(), bl.getThuong(), bl.getKhauTru(), bl.getThucLinh()
                });
            }
            isUpdatingTable = false;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tính lương: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void recalculateThucLinh(int row) {
        if (row < 0 || row >= tinhLuongTableModel.getRowCount()) return;
        try {
            BigDecimal luongCb = currentPayrollList.get(row).getLuongCb();
            int ngayCong = currentPayrollList.get(row).getSoNgayCong();
            
            String phuCapStr = tinhLuongTableModel.getValueAt(row, 3).toString().replaceAll("[^\\d.-]", "");
            BigDecimal phuCap = phuCapStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(phuCapStr);

            String thuongStr = tinhLuongTableModel.getValueAt(row, 5).toString().replaceAll("[^\\d.-]", "");
            BigDecimal thuong = thuongStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(thuongStr);
            
            String khauTruStr = tinhLuongTableModel.getValueAt(row, 6).toString().replaceAll("[^\\d.-]", "");
            BigDecimal khauTru = khauTruStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(khauTruStr);

            BigDecimal luongTheoNgay = luongCb.divide(new BigDecimal(26), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(ngayCong));
            BigDecimal thucLinh = luongTheoNgay.add(phuCap).add(thuong).subtract(khauTru);

            BangLuong bl = currentPayrollList.get(row);
            bl.setPhuCap(phuCap);
            bl.setThuong(thuong);
            bl.setKhauTru(khauTru);
            bl.setThucLinh(thucLinh);

            isUpdatingTable = true;
            tinhLuongTableModel.setValueAt(phuCap, row, 3);
            tinhLuongTableModel.setValueAt(thuong, row, 5);
            tinhLuongTableModel.setValueAt(khauTru, row, 6);
            tinhLuongTableModel.setValueAt(thucLinh, row, 7);
            isUpdatingTable = false;
        } catch (Exception e) { /* Bỏ qua lỗi khi đang gõ */ }
    }

    private void finalizePayroll() {
        if (currentPayrollList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để lưu.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (tinhLuongTable.isEditing()) {
            tinhLuongTable.getCellEditor().stopCellEditing();
        }
        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn chốt bảng lương này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                luongController.finalizePayroll(currentPayrollList);
                JOptionPane.showMessageDialog(this, "Chốt bảng lương thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                tinhLuongTableModel.setRowCount(0);
                currentPayrollList.clear();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void findPayrollHistory() {
        int thang = (Integer) cbThangLichSu.getSelectedItem();
        int nam = (Integer) cbNamLichSu.getSelectedItem();
        try {
            List<BangLuong> historyList = luongController.findPayrollHistory(thang, nam);
            lichSuTableModel.setRowCount(0);
            for (BangLuong bl : historyList) {
                lichSuTableModel.addRow(new Object[]{
                        bl.getMaNv(), bl.getHoTen(), bl.getSoNgayCong(), bl.getLuongCb(), bl.getPhuCap(),
                        bl.getThuong(), bl.getKhauTru(), bl.getThucLinh()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tra cứu lịch sử lương: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void filterTable() {
        String text = txtSearchLichSu.getText();
        if (text.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            try {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0, 1));
            } catch (PatternSyntaxException pse) {
                // Lỗi regex không hợp lệ
            }
        }
    }

    private void reopenPayroll() {
        int thang = (Integer) cbThangLichSu.getSelectedItem();
        int nam = (Integer) cbNamLichSu.getSelectedItem();
        if (JOptionPane.showConfirmDialog(this, "Mở lại bảng lương tháng " + thang + "/" + nam + " để sửa đổi?\nDữ liệu đã chốt sẽ bị xóa.", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                List<BangLuong> oldPayroll = luongController.findPayrollHistory(thang, nam);
                if (oldPayroll.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Không có dữ liệu lương để mở lại cho tháng này.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                luongController.reopenPayroll(thang, nam);
                
                currentPayrollList = oldPayroll;
                isUpdatingTable = true;
                tinhLuongTableModel.setRowCount(0);
                for (BangLuong bl : currentPayrollList) {
                    tinhLuongTableModel.addRow(new Object[]{
                            bl.getMaNv(), bl.getHoTen(), bl.getLuongCb(), bl.getPhuCap(),
                            bl.getSoNgayCong(), bl.getThuong(), bl.getKhauTru(), bl.getThucLinh()
                    });
                }
                isUpdatingTable = false;
                
                cbThangTinh.setSelectedItem(thang);
                cbNamTinh.setSelectedItem(nam);
                tabbedPane.setSelectedIndex(0);
                JOptionPane.showMessageDialog(this, "Đã mở lại bảng lương. Vui lòng sửa đổi và chốt lại.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                
                lichSuTableModel.setRowCount(0);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi mở lại bảng lương: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deletePayroll() {
        int thang = (Integer) cbThangLichSu.getSelectedItem();
        int nam = (Integer) cbNamLichSu.getSelectedItem();
        if (JOptionPane.showConfirmDialog(this, "HÀNH ĐỘNG NGUY HIỂM!\nBạn có chắc muốn HỦY VĨNH VIỄN bảng lương đã chốt của tháng " + thang + "/" + nam + "?", "Cảnh báo", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            try {
                luongController.reopenPayroll(thang, nam);
                JOptionPane.showMessageDialog(this, "Đã hủy bảng lương thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                lichSuTableModel.setRowCount(0);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi hủy bảng lương: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void setupCurrencyColumns(JTable table, boolean isTinhLuongTable) {
        CurrencyRenderer currencyRenderer = new CurrencyRenderer();
        if (isTinhLuongTable) {
            table.getColumnModel().getColumn(2).setCellRenderer(currencyRenderer);
            table.getColumnModel().getColumn(3).setCellRenderer(currencyRenderer);
            table.getColumnModel().getColumn(5).setCellRenderer(currencyRenderer);
            table.getColumnModel().getColumn(6).setCellRenderer(currencyRenderer);
            table.getColumnModel().getColumn(7).setCellRenderer(currencyRenderer);
        } else { // Bảng lịch sử
            table.getColumnModel().getColumn(3).setCellRenderer(currencyRenderer);
            table.getColumnModel().getColumn(4).setCellRenderer(currencyRenderer);
            table.getColumnModel().getColumn(5).setCellRenderer(currencyRenderer);
            table.getColumnModel().getColumn(6).setCellRenderer(currencyRenderer);
            table.getColumnModel().getColumn(7).setCellRenderer(currencyRenderer);
        }
    }
}