package org.example.view;

import com.toedter.calendar.JDateChooser;
import org.example.controller.ChamCongController;
import org.example.controller.NhanVienController;
import org.example.entity.ChamCong;
import org.example.entity.NhanVien;
import org.example.security.RoleChecker;
import org.example.security.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class ChamCongView extends JPanel {

    private final ChamCongController chamCongController;
    private final NhanVienController nhanVienController;

    // Components cho giao diện quản lý
    private JTable nhanVienTable;
    private DefaultTableModel nvTableModel;
    private JTextField txtSearchNv;

    // Components chung
    private JTable chamCongTable;
    private DefaultTableModel ccTableModel;
    
    private String selectedMaNv = null;
    private boolean isDataLoaded = false;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public ChamCongView() {
        this.chamCongController = new ChamCongController();
        this.nhanVienController = new NhanVienController();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Quản lý Chấm Công", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // Kiểm tra vai trò để quyết định giao diện
        if (RoleChecker.canManageHr()) {
            // Giao diện cho Quản lý (Admin/HR) - Master-Detail
            setupManagerUI();
        } else {
            // Giao diện cho Nhân viên thường - Chỉ xem của mình
            setupEmployeeUI();
        }

        // Sự kiện lazy loading
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if (!isDataLoaded) {
                    if (RoleChecker.canManageHr()) {
                        loadNhanVienData(); // Quản lý thì load danh sách NV
                    } else {
                        loadMyChamCongData(); // Nhân viên thì load chấm công của mình
                    }
                    isDataLoaded = true;
                }
            }
        });
    }

    /**
     * Thiết lập giao diện cho Quản lý (chia đôi màn hình)
     */
    private void setupManagerUI() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.4);

        // --- BÊN TRÁI: MASTER (Danh sách Nhân viên) ---
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Chọn Nhân viên"));

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(new JLabel("Tìm tên NV: "), BorderLayout.WEST);
        txtSearchNv = new JTextField();
        searchPanel.add(txtSearchNv, BorderLayout.CENTER);
        leftPanel.add(searchPanel, BorderLayout.NORTH);

        nvTableModel = new DefaultTableModel(new String[]{"Mã NV", "Họ Tên", "Phòng ban"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        nhanVienTable = new JTable(nvTableModel);
        nhanVienTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        leftPanel.add(new JScrollPane(nhanVienTable), BorderLayout.CENTER);
        splitPane.setLeftComponent(leftPanel);

        // --- BÊN PHẢI: DETAIL (Danh sách Chấm công) ---
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Lịch sử chấm công của nhân viên"));

        ccTableModel = new DefaultTableModel(new String[]{"Mã CC", "Ngày", "Giờ vào", "Giờ ra", "Số giờ làm", "Trạng thái"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        chamCongTable = new JTable(ccTableModel);
        chamCongTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rightPanel.add(new JScrollPane(chamCongTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAddCc = new JButton("Chấm công bù");
        JButton btnUpdateCc = new JButton("Sửa");
        JButton btnDeleteCc = new JButton("Xóa");
        buttonPanel.add(btnAddCc);
        buttonPanel.add(btnUpdateCc);
        buttonPanel.add(btnDeleteCc);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        splitPane.setRightComponent(rightPanel);

        add(splitPane, BorderLayout.CENTER);

        // --- SỰ KIỆN CHO GIAO DIỆN QUẢN LÝ ---
        txtSearchNv.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                searchNhanVien();
            }
        });

        nhanVienTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = nhanVienTable.getSelectedRow();
                if (row != -1) {
                    selectedMaNv = (String) nvTableModel.getValueAt(row, 0);
                    loadChamCongData();
                } else {
                    selectedMaNv = null;
                    ccTableModel.setRowCount(0);
                }
            }
        });

        btnAddCc.addActionListener(e -> {
            if (selectedMaNv == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhân viên trước.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showChamCongDialog(null);
        });

        btnUpdateCc.addActionListener(e -> {
            int row = chamCongTable.getSelectedRow();
            if (row != -1) {
                Integer maCc = (Integer) ccTableModel.getValueAt(row, 0);
                chamCongController.getChamCongById(maCc).ifPresent(this::showChamCongDialog);
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một mục chấm công để sửa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnDeleteCc.addActionListener(e -> deleteChamCong());
    }

    /**
     * Thiết lập giao diện cho Nhân viên (chỉ 1 bảng)
     */
    private void setupEmployeeUI() {
        ccTableModel = new DefaultTableModel(new String[]{"Mã CC", "Ngày", "Giờ vào", "Giờ ra", "Số giờ làm", "Trạng thái"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        chamCongTable = new JTable(ccTableModel);
        chamCongTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(chamCongTable), BorderLayout.CENTER);
        
        // Nhân viên không có nút bấm điều chỉnh
    }

    private void loadNhanVienData() {
        try {
            updateNhanVienTable(nhanVienController.getAllNhanVien());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu NV: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchNhanVien() {
        try {
            updateNhanVienTable(nhanVienController.searchNhanVien(txtSearchNv.getText()));
        } catch (Exception e) {
            // ...
        }
    }

    private void updateNhanVienTable(List<NhanVien> list) {
        nvTableModel.setRowCount(0);
        for (NhanVien nv : list) {
            nvTableModel.addRow(new Object[]{nv.getMaNv(), nv.getHoTen(), nv.getMaPb()});
        }
    }

    private void loadChamCongData() {
        if (selectedMaNv == null) return;
        try {
            List<ChamCong> list = chamCongController.getChamCongByMaNv(selectedMaNv);
            updateChamCongTable(list);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu chấm công: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadMyChamCongData() {
        String myMaNv = SessionManager.getInstance().getCurrentAccount().getMaNv();
        try {
            List<ChamCong> list = chamCongController.getChamCongByMaNv(myMaNv);
            updateChamCongTable(list);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu chấm công của bạn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateChamCongTable(List<ChamCong> list) {
        ccTableModel.setRowCount(0);
        for (ChamCong cc : list) {
            ccTableModel.addRow(new Object[]{
                    cc.getMaCc(),
                    cc.getNgay(),
                    cc.getGioVao() != null ? cc.getGioVao().format(timeFormatter) : "",
                    cc.getGioRa() != null ? cc.getGioRa().format(timeFormatter) : "",
                    cc.getSoGioLam(),
                    cc.getTrangThai()
            });
        }
    }

    private void deleteChamCong() {
        int row = chamCongTable.getSelectedRow();
        if (row != -1) {
            Integer maCc = (Integer) ccTableModel.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this, "Xóa mục chấm công này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try {
                    chamCongController.deleteChamCong(maCc);
                    loadChamCongData();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void showChamCongDialog(ChamCong chamCong) {
        boolean isUpdating = chamCong != null;

        JDateChooser dateNgay = new JDateChooser();
        JTextField txtGioVao = new JTextField("08:00:00");
        JTextField txtGioRa = new JTextField("17:00:00");
        JComboBox<ChamCong.TrangThaiChamCong> cbTrangThai = new JComboBox<>(ChamCong.TrangThaiChamCong.values());
        JTextArea txtGhiChu = new JTextArea(3, 20);

        if (isUpdating) {
            dateNgay.setDate(Date.from(chamCong.getNgay().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            if (chamCong.getGioVao() != null) txtGioVao.setText(chamCong.getGioVao().format(timeFormatter));
            if (chamCong.getGioRa() != null) txtGioRa.setText(chamCong.getGioRa().format(timeFormatter));
            cbTrangThai.setSelectedItem(chamCong.getTrangThai());
            txtGhiChu.setText(chamCong.getGhiChu());
        } else {
            dateNgay.setDate(new Date()); // Mặc định ngày hôm nay
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Ngày (*):"));
        panel.add(dateNgay);
        panel.add(new JLabel("Giờ vào (HH:mm:ss):"));
        panel.add(txtGioVao);
        panel.add(new JLabel("Giờ ra (HH:mm:ss):"));
        panel.add(txtGioRa);
        panel.add(new JLabel("Trạng thái:"));
        panel.add(cbTrangThai);
        panel.add(new JLabel("Ghi chú:"));
        panel.add(new JScrollPane(txtGhiChu));

        int result = JOptionPane.showConfirmDialog(this, panel, isUpdating ? "Sửa Chấm Công" : "Chấm Công Bù", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                ChamCong cc = isUpdating ? chamCong : new ChamCong();
                cc.setMaNv(selectedMaNv);
                
                if (dateNgay.getDate() == null) throw new Exception("Vui lòng chọn ngày.");
                cc.setNgay(dateNgay.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

                if (!txtGioVao.getText().isBlank()) cc.setGioVao(LocalTime.parse(txtGioVao.getText(), timeFormatter));
                if (!txtGioRa.getText().isBlank()) cc.setGioRa(LocalTime.parse(txtGioRa.getText(), timeFormatter));
                
                cc.setTrangThai((ChamCong.TrangThaiChamCong) cbTrangThai.getSelectedItem());
                cc.setGhiChu(txtGhiChu.getText());

                if (isUpdating) {
                    chamCongController.updateChamCong(cc);
                } else {
                    chamCongController.createChamCong(cc);
                }
                loadChamCongData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}