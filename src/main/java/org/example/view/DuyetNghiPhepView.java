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
    private JComboBox<String> cbTrangThai;
    private boolean isDataLoaded = false;

    public DuyetNghiPhepView() {
        this.nghiPhepController = new NghiPhepController();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Duyệt Đơn Nghỉ Phép", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setOpaque(false);
        cbTrangThai = new JComboBox<>(new String[]{"Chờ duyệt", "Đã duyệt", "Từ chối", "Tất cả"});
        filterPanel.add(new JLabel("Trạng thái:"));
        filterPanel.add(cbTrangThai);
        topPanel.add(filterPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"Mã Đơn", "Mã NV", "Từ ngày", "Đến ngày", "Lý do", "Trạng thái", "Người duyệt"};
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
        cbTrangThai.addActionListener(e -> loadData());
    }

    private void loadData() {
        try {
            String selectedStatus = (String) cbTrangThai.getSelectedItem();
            List<NghiPhep> list = nghiPhepController.getAll().stream()
                    .filter(np -> matchesStatus(np, selectedStatus))
                    .collect(Collectors.toList());
            
            tableModel.setRowCount(0);
            for (NghiPhep np : list) {
                tableModel.addRow(new Object[]{
                        np.getMaNp(),
                        np.getMaNv(),
                        np.getNgayBatDau(),
                        np.getNgayKetThuc(),
                        np.getLyDo(),
                        np.getTrangThai(),
                        np.getNguoiDuyet() != null ? np.getNguoiDuyet() : ""
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean matchesStatus(NghiPhep nghiPhep, String selectedStatus) {
        if ("Tất cả".equals(selectedStatus)) {
            return true;
        }
        if ("Chờ duyệt".equals(selectedStatus)) {
            return nghiPhep.getTrangThai() == NghiPhep.TrangThaiNghiPhep.CHO_DUYET;
        }
        if ("Đã duyệt".equals(selectedStatus)) {
            return nghiPhep.getTrangThai() == NghiPhep.TrangThaiNghiPhep.DA_DUYET;
        }
        if ("Từ chối".equals(selectedStatus)) {
            return nghiPhep.getTrangThai() == NghiPhep.TrangThaiNghiPhep.TU_CHOI;
        }
        return true;
    }

    private void handleApproval(boolean isApproved) {
        int row = table.getSelectedRow();
        if (row != -1) {
            Integer maNp = (Integer) tableModel.getValueAt(row, 0);
            NghiPhep.TrangThaiNghiPhep trangThai = (NghiPhep.TrangThaiNghiPhep) tableModel.getValueAt(row, 5);
            if (trangThai != NghiPhep.TrangThaiNghiPhep.CHO_DUYET) {
                JOptionPane.showMessageDialog(this,
                        "Đơn này đã được xử lý, không thể phê duyệt hoặc từ chối lại.",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

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
