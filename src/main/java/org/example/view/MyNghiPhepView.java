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
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyNghiPhepView extends JPanel {

    private final NghiPhepController nghiPhepController;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<Integer> cbThang;
    private JComboBox<Integer> cbNam;
    private JComboBox<String> cbTrangThai;
    private JLabel lblSummary;
    private List<NghiPhep> currentRequests = new ArrayList<>();

    public MyNghiPhepView() {
        this.nghiPhepController = new NghiPhepController();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Đơn Nghỉ Phép Của Tôi", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setOpaque(false);
        cbThang = new JComboBox<>();
        for (int month = 1; month <= 12; month++) {
            cbThang.addItem(month);
        }

        cbNam = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int year = currentYear - 3; year <= currentYear + 1; year++) {
            cbNam.addItem(year);
        }

        cbThang.setSelectedItem(LocalDate.now().getMonthValue());
        cbNam.setSelectedItem(currentYear);

        JButton btnFilter = new JButton("Xem tháng");
        lblSummary = new JLabel();
        cbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Chờ duyệt", "Đã duyệt", "Từ chối"});
        filterPanel.add(new JLabel("Tháng:"));
        filterPanel.add(cbThang);
        filterPanel.add(new JLabel("Năm:"));
        filterPanel.add(cbNam);
        filterPanel.add(new JLabel("Trạng thái:"));
        filterPanel.add(cbTrangThai);
        filterPanel.add(btnFilter);
        filterPanel.add(lblSummary);
        topPanel.add(filterPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

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
                loadData();
            }
        });

        btnAdd.addActionListener(e -> showAddDialog());
        btnDelete.addActionListener(e -> deleteNghiPhep());
        btnRefresh.addActionListener(e -> loadData());
        btnFilter.addActionListener(e -> updateTableForSelectedMonth());
        cbThang.addActionListener(e -> updateTableForSelectedMonth());
        cbNam.addActionListener(e -> updateTableForSelectedMonth());
        cbTrangThai.addActionListener(e -> updateTableForSelectedMonth());
    }

    private void loadData() {
        String maNv = SessionManager.getInstance().getCurrentAccount().getMaNv();
        try {
            currentRequests = nghiPhepController.getByMaNv(maNv);
            updateTableForSelectedMonth();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTableForSelectedMonth() {
        if (tableModel == null || cbThang == null || cbNam == null) {
            return;
        }

        Integer selectedMonth = (Integer) cbThang.getSelectedItem();
        Integer selectedYear = (Integer) cbNam.getSelectedItem();
        String selectedStatus = (String) cbTrangThai.getSelectedItem();
        if (selectedMonth == null || selectedYear == null || selectedStatus == null) {
            return;
        }

        YearMonth selectedPeriod = YearMonth.of(selectedYear, selectedMonth);
        tableModel.setRowCount(0);

        int totalDays = 0;
        int requestCount = 0;
        int approvedCount = 0;
        for (NghiPhep np : currentRequests) {
            if (!isInMonth(np, selectedPeriod)) {
                continue;
            }
            if (!matchesStatus(np, selectedStatus)) {
                continue;
            }

            requestCount++;
            if (np.getTrangThai() == NghiPhep.TrangThaiNghiPhep.DA_DUYET) {
                approvedCount++;
            }
            totalDays += countDaysInMonth(np, selectedPeriod);
            tableModel.addRow(new Object[]{
                    np.getMaNp(), np.getNgayBatDau(), np.getNgayKetThuc(),
                    np.getLyDo(), np.getTrangThai(), np.getNguoiDuyet() != null ? np.getNguoiDuyet() : ""
            });
        }

        lblSummary.setText("Có " + requestCount + " đơn trong tháng, đã duyệt " + approvedCount + ", tổng " + totalDays + " ngày");
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

    private boolean isInMonth(NghiPhep nghiPhep, YearMonth period) {
        if (nghiPhep.getNgayBatDau() == null || nghiPhep.getNgayKetThuc() == null) {
            return false;
        }

        LocalDate monthStart = period.atDay(1);
        LocalDate monthEnd = period.atEndOfMonth();
        return !nghiPhep.getNgayKetThuc().isBefore(monthStart)
                && !nghiPhep.getNgayBatDau().isAfter(monthEnd);
    }

    private int countDaysInMonth(NghiPhep nghiPhep, YearMonth period) {
        LocalDate start = nghiPhep.getNgayBatDau().isBefore(period.atDay(1))
                ? period.atDay(1)
                : nghiPhep.getNgayBatDau();
        LocalDate end = nghiPhep.getNgayKetThuc().isAfter(period.atEndOfMonth())
                ? period.atEndOfMonth()
                : nghiPhep.getNgayKetThuc();

        return (int) (end.toEpochDay() - start.toEpochDay()) + 1;
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
