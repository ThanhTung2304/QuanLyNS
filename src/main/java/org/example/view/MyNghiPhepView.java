package org.example.view;

import com.toedter.calendar.JDateChooser;
import org.example.controller.NghiPhepController;
import org.example.entity.NghiPhep;
import org.example.security.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class MyNghiPhepView extends JPanel {

    private final NghiPhepController nghiPhepController;
    private JTable table;
    private DefaultTableModel tableModel;
    private boolean isDataLoaded = false;

    public MyNghiPhepView() {
        this.nghiPhepController = new NghiPhepController();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Đơn Nghỉ Phép Của Tôi", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Mã Đơn", "Từ ngày", "Đến ngày", "Lý do", "Trạng thái", "Người duyệt"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdd = new JButton("Tạo đơn mới");
        JButton btnDelete = new JButton("Xóa đơn (Chưa duyệt)");
        JButton btnRefresh = new JButton("Refresh");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);
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

        btnAdd.addActionListener(e -> showAddDialog());
        btnDelete.addActionListener(e -> deleteNghiPhep());
        btnRefresh.addActionListener(e -> loadData());
    }

    private void loadData() {
        String maNv = SessionManager.getInstance().getCurrentAccount().getMaNv();
        try {
            List<NghiPhep> list = nghiPhepController.getByMaNv(maNv);
            tableModel.setRowCount(0);
            for (NghiPhep np : list) {
                tableModel.addRow(new Object[]{
                        np.getMaNp(), np.getNgayBatDau(), np.getNgayKetThuc(),
                        np.getLyDo(), np.getTrangThai(), np.getNguoiDuyet() != null ? np.getNguoiDuyet() : ""
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddDialog() {
        JDateChooser dateBatDau = new JDateChooser(new Date());
        JDateChooser dateKetThuc = new JDateChooser(new Date());
        JTextArea txtLyDo = new JTextArea(3, 20);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Từ ngày (*):"));
        panel.add(dateBatDau);
        panel.add(new JLabel("Đến ngày (*):"));
        panel.add(dateKetThuc);
        panel.add(new JLabel("Lý do (*):"));
        panel.add(new JScrollPane(txtLyDo));

        int result = JOptionPane.showConfirmDialog(this, panel, "Xin Nghỉ Phép", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                NghiPhep np = new NghiPhep();
                np.setMaNv(SessionManager.getInstance().getCurrentAccount().getMaNv());
                if (dateBatDau.getDate() == null || dateKetThuc.getDate() == null) throw new Exception("Vui lòng chọn đủ ngày.");
                np.setNgayBatDau(dateBatDau.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                np.setNgayKetThuc(dateKetThuc.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                np.setLyDo(txtLyDo.getText());

                nghiPhepController.create(np);
                loadData();
                JOptionPane.showMessageDialog(this, "Đã gửi đơn thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteNghiPhep() {
        int row = table.getSelectedRow();
        if (row != -1) {
            Integer maNp = (Integer) tableModel.getValueAt(row, 0);
            NghiPhep.TrangThaiNghiPhep trangThai = (NghiPhep.TrangThaiNghiPhep) tableModel.getValueAt(row, 4);
            
            if (trangThai != NghiPhep.TrangThaiNghiPhep.CHO_DUYET) {
                JOptionPane.showMessageDialog(this, "Chỉ có thể xóa đơn đang chờ duyệt.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (JOptionPane.showConfirmDialog(this, "Hủy đơn xin nghỉ này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try {
                    nghiPhepController.delete(maNp);
                    loadData();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}