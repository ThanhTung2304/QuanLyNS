package org.example.view;

import com.toedter.calendar.JDateChooser;
import org.example.controller.ChucVuController;
import org.example.controller.NhanVienController;
import org.example.controller.PhongBanController;
import org.example.entity.ChucVu;
import org.example.entity.NhanVien;
import org.example.entity.PhongBan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

public class NhanVienView extends JPanel {

    private final NhanVienController nhanVienController;
    private final PhongBanController phongBanController;
    private final ChucVuController chucVuController;

    private JTable nhanVienTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private boolean isDataLoaded = false;

    public NhanVienView() {
        this.nhanVienController = new NhanVienController();
        this.phongBanController = new PhongBanController();
        this.chucVuController = new ChucVuController();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Tìm kiếm theo tên: "), BorderLayout.WEST);
        txtSearch = new JTextField();
        topPanel.add(txtSearch, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"Mã NV", "Họ Tên", "Email", "Mã Chức Vụ", "Mã Phòng Ban", "Trạng thái"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        nhanVienTable = new JTable(tableModel);
        nhanVienTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(nhanVienTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JButton btnAdd = new JButton("Thêm mới");
        JButton btnUpdate = new JButton("Cập nhật");
        JButton btnDelete = new JButton("Xóa");
        JButton btnDetails = new JButton("Xem chi tiết");
        JButton btnRefresh = new JButton("Refresh");

        Dimension buttonSize = new Dimension(120, 30);
        btnAdd.setMaximumSize(buttonSize);
        btnUpdate.setMaximumSize(buttonSize);
        btnDelete.setMaximumSize(buttonSize);
        btnDetails.setMaximumSize(buttonSize);
        btnRefresh.setMaximumSize(buttonSize);

        buttonPanel.add(btnAdd);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnUpdate);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnDelete);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnDetails);
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(btnRefresh);

        add(buttonPanel, BorderLayout.EAST);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if (!isDataLoaded) {
                    loadNhanVienData();
                    isDataLoaded = true;
                }
            }
        });

        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                searchNhanVien();
            }
        });

        btnAdd.addActionListener(e -> showNhanVienDialog(null));
        btnUpdate.addActionListener(e -> {
            int selectedRow = nhanVienTable.getSelectedRow();
            if (selectedRow != -1) {
                String maNv = (String) tableModel.getValueAt(selectedRow, 0);
                nhanVienController.getNhanVienById(maNv)
                        .ifPresentOrElse(
                                this::showNhanVienDialog,
                                () -> JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin chi tiết cho nhân viên này.", "Lỗi", JOptionPane.ERROR_MESSAGE)
                        );
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhân viên để cập nhật.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });
        btnDelete.addActionListener(e -> deleteNhanVien());
        btnRefresh.addActionListener(e -> loadNhanVienData());
        btnDetails.addActionListener(e -> showDetailsDialog());
    }

    private void loadNhanVienData() {
        try {
            List<NhanVien> nhanVienList = nhanVienController.getAllNhanVien();
            updateTable(nhanVienList);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            String detailedMessage = e.getMessage();
            if (cause != null) {
                detailedMessage += "\n\nNguyên nhân gốc: " + cause.getMessage();
            }
            JOptionPane.showMessageDialog(this, detailedMessage, "Lỗi Tải Dữ Liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchNhanVien() {
        try {
            String keyword = txtSearch.getText();
            List<NhanVien> nhanVienList = nhanVienController.searchNhanVien(keyword);
            updateTable(nhanVienList);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm nhân viên: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<NhanVien> nhanVienList) {
        tableModel.setRowCount(0);
        for (NhanVien nv : nhanVienList) {
            tableModel.addRow(new Object[]{
                    nv.getMaNv(),
                    nv.getHoTen(),
                    nv.getEmail(),
                    nv.getMaChucVu(),
                    nv.getMaPb(),
                    nv.getTrangThai()
            });
        }
    }

    private void deleteNhanVien() {
        int selectedRow = nhanVienTable.getSelectedRow();
        if (selectedRow != -1) {
            String maNv = (String) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn xóa nhân viên '" + maNv + "' không?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    nhanVienController.deleteNhanVien(maNv);
                    loadNhanVienData();
                    JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa nhân viên: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhân viên để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showNhanVienDialog(NhanVien nhanVien) {
        boolean isUpdating = nhanVien != null;

        // --- Lấy dữ liệu cho các ComboBox ---
        List<PhongBan> phongBanList = phongBanController.getAllPhongBan();
        List<ChucVu> chucVuList = chucVuController.getAllChucVu();

        // --- Tạo các component cho form ---
        JTextField txtMaNv = new JTextField(15);
        JTextField txtHoTen = new JTextField();
        JDateChooser dateNgaySinh = new JDateChooser();
        JComboBox<NhanVien.GioiTinh> cbGioiTinh = new JComboBox<>(NhanVien.GioiTinh.values());
        JTextField txtDiaChi = new JTextField();
        JTextField txtSoDienThoai = new JTextField();
        JTextField txtEmail = new JTextField();
        JComboBox<PhongBan> cbPhongBan = new JComboBox<>(new Vector<>(phongBanList));
        JComboBox<ChucVu> cbChucVu = new JComboBox<>(new Vector<>(chucVuList));
        JDateChooser dateNgayVaoLam = new JDateChooser();
        JComboBox<NhanVien.TrangThaiLamViec> cbTrangThai = new JComboBox<>(NhanVien.TrangThaiLamViec.values());

        // --- Điền dữ liệu nếu là form cập nhật ---
        if (isUpdating) {
            txtMaNv.setText(nhanVien.getMaNv());
            txtMaNv.setEditable(false);
            txtHoTen.setText(nhanVien.getHoTen());
            if (nhanVien.getNgaySinh() != null)
                dateNgaySinh.setDate(Date.from(nhanVien.getNgaySinh().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            cbGioiTinh.setSelectedItem(nhanVien.getGioiTinh());
            txtDiaChi.setText(nhanVien.getDiaChi());
            txtSoDienThoai.setText(nhanVien.getSoDienThoai());
            txtEmail.setText(nhanVien.getEmail());
            if (nhanVien.getNgayVaoLam() != null)
                dateNgayVaoLam.setDate(Date.from(nhanVien.getNgayVaoLam().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            cbTrangThai.setSelectedItem(nhanVien.getTrangThai());

            // Tìm và chọn đúng phòng ban, chức vụ trong ComboBox
            phongBanList.stream().filter(pb -> pb.getMaPhong().equals(nhanVien.getMaPb())).findFirst().ifPresent(cbPhongBan::setSelectedItem);
            chucVuList.stream().filter(cv -> cv.getMaChucVu().equals(nhanVien.getMaChucVu())).findFirst().ifPresent(cbChucVu::setSelectedItem);
        } else {
            txtMaNv.setText(generateNextMaNv());
            txtMaNv.setEditable(false);
            txtMaNv.setToolTipText("Mã nhân viên được tự động tạo");
        }

        // --- Xây dựng panel form ---
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.add(new JLabel("Mã NV (*):"));
        formPanel.add(txtMaNv);
        formPanel.add(new JLabel("Họ Tên (*):"));
        formPanel.add(txtHoTen);
        formPanel.add(new JLabel("Ngày sinh:"));
        formPanel.add(dateNgaySinh);
        formPanel.add(new JLabel("Giới tính:"));
        formPanel.add(cbGioiTinh);
        formPanel.add(new JLabel("Phòng ban:"));
        formPanel.add(cbPhongBan);
        formPanel.add(new JLabel("Chức vụ:"));
        formPanel.add(cbChucVu);
        formPanel.add(new JLabel("Địa chỉ:"));
        formPanel.add(txtDiaChi);
        formPanel.add(new JLabel("SĐT:"));
        formPanel.add(txtSoDienThoai);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(txtEmail);
        formPanel.add(new JLabel("Ngày vào làm:"));
        formPanel.add(dateNgayVaoLam);
        formPanel.add(new JLabel("Trạng thái:"));
        formPanel.add(cbTrangThai);

        int result = JOptionPane.showConfirmDialog(this, formPanel,
                isUpdating ? "Cập nhật Nhân viên" : "Thêm mới Nhân viên",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        // --- Xử lý kết quả ---
        if (result == JOptionPane.OK_OPTION) {
            try {
                NhanVien nv = isUpdating ? nhanVien : new NhanVien();
                nv.setMaNv(txtMaNv.getText());
                nv.setHoTen(txtHoTen.getText());
                if (dateNgaySinh.getDate() != null)
                    nv.setNgaySinh(dateNgaySinh.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                nv.setGioiTinh((NhanVien.GioiTinh) cbGioiTinh.getSelectedItem());
                nv.setDiaChi(txtDiaChi.getText());
                nv.setSoDienThoai(txtSoDienThoai.getText());
                nv.setEmail(txtEmail.getText());

                // Lấy mã từ đối tượng được chọn trong ComboBox
                PhongBan selectedPhongBan = (PhongBan) cbPhongBan.getSelectedItem();
                if (selectedPhongBan != null) nv.setMaPb(selectedPhongBan.getMaPhong());

                ChucVu selectedChucVu = (ChucVu) cbChucVu.getSelectedItem();
                if (selectedChucVu != null) nv.setMaChucVu(selectedChucVu.getMaChucVu());

                if (dateNgayVaoLam.getDate() != null)
                    nv.setNgayVaoLam(dateNgayVaoLam.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                nv.setTrangThai((NhanVien.TrangThaiLamViec) cbTrangThai.getSelectedItem());

                if (isUpdating) {
                    nhanVienController.updateNhanVien(nv);
                } else {
                    nhanVienController.createNhanVien(nv);
                }
                loadNhanVienData();
                JOptionPane.showMessageDialog(this, (isUpdating ? "Cập nhật" : "Thêm mới") + " thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String generateNextMaNv() {
        int maxNumber = 0;
        try {
            for (NhanVien nv : nhanVienController.getAllNhanVien()) {
                String maNv = nv.getMaNv();
                if (maNv == null || !maNv.matches("NV\\d+")) {
                    continue;
                }

                int number = Integer.parseInt(maNv.substring(2));
                if (number > maxNumber) {
                    maxNumber = number;
                }
            }
        } catch (Exception e) {
            return "NV001";
        }

        return String.format("NV%03d", maxNumber + 1);
    }

    private void showDetailsDialog() {
        int selectedRow = nhanVienTable.getSelectedRow();
        if (selectedRow != -1) {
            String maNv = (String) tableModel.getValueAt(selectedRow, 0);
            Optional<NhanVien> nvOpt = nhanVienController.getNhanVienById(maNv);
            nvOpt.ifPresent(nv -> {
                JTextArea textArea = new JTextArea(15, 40);
                textArea.setEditable(false);
                StringBuilder details = new StringBuilder();
                details.append("Mã NV:\t").append(nv.getMaNv()).append("\n");
                details.append("Họ Tên:\t").append(nv.getHoTen()).append("\n");
                details.append("Ngày sinh:\t").append(nv.getNgaySinh()).append("\n");
                details.append("Giới tính:\t").append(nv.getGioiTinh()).append("\n");
                details.append("Địa chỉ:\t").append(nv.getDiaChi()).append("\n");
                details.append("SĐT:\t").append(nv.getSoDienThoai()).append("\n");
                details.append("Email:\t").append(nv.getEmail()).append("\n");
                details.append("--------------------------------\n");
                
                // Lấy tên phòng ban và chức vụ thay vì chỉ hiển thị mã
                String tenPhong = "Chưa có";
                if (nv.getMaPb() != null && !nv.getMaPb().isEmpty()) {
                    Optional<PhongBan> pbOpt = phongBanController.getPhongBanById(nv.getMaPb());
                    if (pbOpt.isPresent()) tenPhong = pbOpt.get().getTenPhong();
                }
                details.append("Phòng Ban:\t").append(tenPhong).append(" (").append(nv.getMaPb()).append(")\n");
                
                String tenChucVu = "Chưa có";
                if (nv.getMaChucVu() != null && !nv.getMaChucVu().isEmpty()) {
                    Optional<ChucVu> cvOpt = chucVuController.getChucVuById(nv.getMaChucVu());
                    if (cvOpt.isPresent()) tenChucVu = cvOpt.get().getTenChucVu();
                }
                details.append("Chức Vụ:\t").append(tenChucVu).append(" (").append(nv.getMaChucVu()).append(")\n");
                
                details.append("Ngày vào làm:\t").append(nv.getNgayVaoLam()).append("\n");
                details.append("Trạng thái:\t").append(nv.getTrangThai()).append("\n");
                
                textArea.setText(details.toString());
                JScrollPane scrollPane = new JScrollPane(textArea);
                JOptionPane.showMessageDialog(this, scrollPane, "Chi tiết hồ sơ nhân viên", JOptionPane.INFORMATION_MESSAGE);
            });
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhân viên để xem chi tiết.", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }
}
