package org.example.view;

import org.example.controller.HopDongController;
import org.example.controller.LuongController;
import org.example.controller.NhanVienController;
import org.example.entity.BangLuong;
import org.example.entity.HopDong;
import org.example.entity.NhanVien;
import org.example.security.SessionManager;
import org.example.view.util.CurrencyRenderer; // <-- Đã import

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class MyProfileView extends JPanel {

    private final NhanVienController nhanVienController;
    private final HopDongController hopDongController;
    private final LuongController luongController;

    private final NhanVien currentUser;

    public MyProfileView() {
        this.nhanVienController = new NhanVienController();
        this.hopDongController = new HopDongController();
        this.luongController = new LuongController();
        this.currentUser = nhanVienController.getNhanVienById(SessionManager.getInstance().getCurrentAccount().getMaNv()).orElse(new NhanVien());
        
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Thông tin Cá nhân", createInfoPanel());
        tabbedPane.addTab("Lịch sử Hợp đồng", createHopDongPanel());
        tabbedPane.addTab("Bảng lương của tôi", createLuongPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        addField(panel, gbc, 0, "Mã nhân viên:", currentUser.getMaNv());
        addField(panel, gbc, 1, "Họ và tên:", currentUser.getHoTen());
        addField(panel, gbc, 2, "Ngày sinh:", currentUser.getNgaySinh() != null ? currentUser.getNgaySinh().toString() : "");
        addField(panel, gbc, 3, "Giới tính:", currentUser.getGioiTinh() != null ? currentUser.getGioiTinh().toString() : "");
        addField(panel, gbc, 4, "Địa chỉ:", currentUser.getDiaChi());
        addField(panel, gbc, 5, "Email:", currentUser.getEmail());
        addField(panel, gbc, 6, "Số điện thoại:", currentUser.getSoDienThoai());
        addField(panel, gbc, 7, "Phòng ban:", currentUser.getMaPb());
        addField(panel, gbc, 8, "Chức vụ:", currentUser.getMaChucVu());
        
        return panel;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int y, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = y;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD));
        panel.add(valueLabel, gbc);
    }

    private JPanel createHopDongPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnNames = {"Mã HĐ", "Loại", "Trạng thái", "Ngày BĐ", "Ngày KT", "Lương CB"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        
        // Sử dụng CurrencyRenderer công khai
        table.getColumnModel().getColumn(5).setCellRenderer(new CurrencyRenderer());

        List<HopDong> hopDongs = hopDongController.getHopDongByMaNv(currentUser.getMaNv());
        for (HopDong hd : hopDongs) {
            model.addRow(new Object[]{
                    hd.getMaHd(), hd.getLoaiHd(), hd.getTrangThai(), hd.getNgayBatDau(),
                    hd.getNgayKetThuc() != null ? hd.getNgayKetThuc() : "Vô thời hạn",
                    hd.getLuongCoBan()
            });
        }
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLuongPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Tháng:"));
        JComboBox<Integer> cbThang = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
        cbThang.setSelectedItem(LocalDate.now().getMonthValue());
        topPanel.add(cbThang);

        topPanel.add(new JLabel("Năm:"));
        JComboBox<Integer> cbNam = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear; i >= currentYear - 5; i--) cbNam.addItem(i);
        topPanel.add(cbNam);

        JButton btnSearch = new JButton("Xem bảng lương");
        topPanel.add(btnSearch);
        panel.add(topPanel, BorderLayout.NORTH);

        JTextArea payslipArea = new JTextArea();
        payslipArea.setEditable(false);
        payslipArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        payslipArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JScrollPane(payslipArea), BorderLayout.CENTER);

        btnSearch.addActionListener(e -> {
            int thang = (int) cbThang.getSelectedItem();
            int nam = (int) cbNam.getSelectedItem();
            Optional<BangLuong> blOpt = luongController.findMyPayroll(currentUser.getMaNv(), thang, nam);
            
            if (blOpt.isPresent()) {
                displayPayslip(payslipArea, blOpt.get());
            } else {
                payslipArea.setText("Không tìm thấy dữ liệu bảng lương cho tháng " + thang + "/" + nam);
            }
        });

        return panel;
    }

    private void displayPayslip(JTextArea textArea, BangLuong bl) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        StringBuilder sb = new StringBuilder();

        sb.append("============================================\n");
        sb.append(String.format("        PHIẾU LƯƠNG THÁNG %d/%d\n", bl.getThang(), bl.getNam()));
        sb.append("============================================\n\n");
        sb.append(String.format("%-20s: %s\n", "Mã nhân viên", bl.getMaNv()));
        sb.append(String.format("%-20s: %s\n", "Họ và tên", bl.getHoTen()));
        sb.append("\n--------------------------------------------\n\n");
        sb.append("CÁC KHOẢN THU NHẬP:\n");
        sb.append(String.format("  %-18s: %18s\n", "Lương cơ bản", formatter.format(bl.getLuongCb())));
        sb.append(String.format("  %-18s: %18s\n", "Phụ cấp", formatter.format(bl.getPhuCap())));
        sb.append(String.format("  %-18s: %18s\n", "Thưởng", formatter.format(bl.getThuong())));
        sb.append("\nCÁC KHOẢN KHẤU TRỪ:\n");
        sb.append(String.format("  %-18s: %18s\n", "Khấu trừ", formatter.format(bl.getKhauTru())));
        sb.append("\n--------------------------------------------\n");
        sb.append(String.format("%-20s: %18s\n", "THỰC LĨNH", formatter.format(bl.getThucLinh())));
        sb.append("--------------------------------------------\n\n");
        sb.append(String.format("Số ngày công trong tháng: %d\n", bl.getSoNgayCong()));

        textArea.setText(sb.toString());
    }
}