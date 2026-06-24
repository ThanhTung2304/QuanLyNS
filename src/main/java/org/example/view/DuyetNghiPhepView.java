package org.example.view;

import org.example.controller.NghiPhepController;
import org.example.entity.NghiPhep;
import org.example.security.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import java.util.stream.Collectors;

public class DuyetNghiPhepView extends JPanel {

    private final NghiPhepController nghiPhepController;
    private JTable table;
    private DefaultTableModel tableModel;
    private boolean isDataLoaded = false;

    public DuyetNghiPhepView() {
        this.nghiPhepController = new NghiPhepController();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Duyệt Đơn Nghỉ Phép", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Mã Đơn", "Mã NV", "Từ ngày", "Đến ngày", "Lý do"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnApprove = new JButton("Phê duyệt");
        JButton btnReject = new JButton("Từ chối");
        JButton btnRefresh = new JButton("Refresh");

        buttonPanel.add(btnApprove);
        buttonPanel.add(btnReject);
        buttonPanel.add(btnRefresh);
        add(buttonPanel, BorderLayout.SOUTH);

        this.addComponentListener(new ComponentAdapter() {
            @Override public void componentShown(ComponentEvent e) {
                if (!isDataLoaded) {
                    loadData();
                    isDataLoaded = true;
                }
            }
        });

        btnApprove.addActionListener(e -> handleApproval(true));
        btnReject.addActionListener(e -> handleApproval(false));
        btnRefresh.addActionListener(e -> loadData());
    }

    private void loadData() {
        try {
            // Chỉ hiển thị các đơn đang chờ duyệt
            List<NghiPhep> list = nghiPhepController.getAll().stream()
                    .filter(np -> np.getTrangThai() == NghiPhep.TrangThaiNghiPhep.CHO_DUYET)
                    .collect(Collectors.toList());
            
            tableModel.setRowCount(0);
            for (NghiPhep np : list) {
                tableModel.addRow(new Object[]{
                        np.getMaNp(), np.getMaNv(), np.getNgayBatDau(), np.getNgayKetThuc(), np.getLyDo()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleApproval(boolean isApproved) {
        int row = table.getSelectedRow();
        if (row != -1) {
            Integer maNp = (Integer) tableModel.getValueAt(row, 0);
            String action = isApproved ? "phê duyệt" : "từ chối";
            
            if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn " + action + " đơn này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try {
                    String maNguoiDuyet = SessionManager.getInstance().getCurrentAccount().getMaNv();
                    if (isApproved) {
                        nghiPhepController.approve(maNp, maNguoiDuyet);
                    } else {
                        nghiPhepController.reject(maNp, maNguoiDuyet);
                    }
                    loadData();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi duyệt đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn để duyệt.", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }
}