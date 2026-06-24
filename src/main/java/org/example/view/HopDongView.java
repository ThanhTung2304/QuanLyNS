package org.example.view;

import com.toedter.calendar.JDateChooser;
import org.example.controller.HopDongController;
import org.example.controller.NhanVienController;
import org.example.entity.HopDong;
import org.example.entity.NhanVien;
import org.example.view.util.CurrencyRenderer; // <-- Đã import

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class HopDongView extends JPanel {

    private final HopDongController hopDongController;
    private final NhanVienController nhanVienController;

    private JTable nhanVienTable;
    private DefaultTableModel nvTableModel;
    private JTextField txtSearchNv;

    private JTable hopDongTable;
    private DefaultTableModel hdTableModel;
    
    private String selectedMaNv = null;
    private boolean isDataLoaded = false;

    public HopDongView() {
        this.hopDongController = new HopDongController();
        this.nhanVienController = new NhanVienController();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Quản lý Hợp Đồng", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.4);

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

        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Hợp đồng của nhân viên"));

        hdTableModel = new DefaultTableModel(new String[]{"Mã HĐ", "Loại HĐ", "Trạng thái", "Ngày BĐ", "Ngày KT", "Lương CB"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        hopDongTable = new JTable(hdTableModel);
        hopDongTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        hopDongTable.getColumnModel().getColumn(5).setCellRenderer(new CurrencyRenderer());
        
        rightPanel.add(new JScrollPane(hopDongTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAddHd = new JButton("Tạo Hợp đồng mới");
        JButton btnUpdateHd = new JButton("Cập nhật / Gia hạn");
        JButton btnDeleteHd = new JButton("Xóa Hợp đồng");
        buttonPanel.add(btnAddHd);
        buttonPanel.add(btnUpdateHd);
        buttonPanel.add(btnDeleteHd);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        splitPane.setRightComponent(rightPanel);

        add(splitPane, BorderLayout.CENTER);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if (!isDataLoaded) {
                    loadNhanVienData();
                    isDataLoaded = true;
                }
            }
        });

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
                    loadHopDongData();
                } else {
                    selectedMaNv = null;
                    hdTableModel.setRowCount(0);
                }
            }
        });

        btnAddHd.addActionListener(e -> {
            if (selectedMaNv == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhân viên ở danh sách bên trái trước.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showHopDongDialog(null);
        });

        btnUpdateHd.addActionListener(e -> {
            int row = hopDongTable.getSelectedRow();
            if (row != -1) {
                String maHd = (String) hdTableModel.getValueAt(row, 0);
                hopDongController.getHopDongById(maHd).ifPresentOrElse(
                        this::showHopDongDialog,
                        () -> JOptionPane.showMessageDialog(this, "Không tìm thấy hợp đồng.", "Lỗi", JOptionPane.ERROR_MESSAGE)
                );
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một hợp đồng để cập nhật.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnDeleteHd.addActionListener(e -> deleteHopDong());
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

    private void loadHopDongData() {
        if (selectedMaNv == null) return;
        try {
            List<HopDong> list = hopDongController.getHopDongByMaNv(selectedMaNv);
            hdTableModel.setRowCount(0);
            for (HopDong hd : list) {
                hdTableModel.addRow(new Object[]{
                        hd.getMaHd(),
                        hd.getLoaiHd(),
                        hd.getTrangThai(),
                        hd.getNgayBatDau(),
                        hd.getNgayKetThuc() != null ? hd.getNgayKetThuc() : "Vô thời hạn",
                        hd.getLuongCoBan()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải hợp đồng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteHopDong() {
        int row = hopDongTable.getSelectedRow();
        if (row != -1) {
            String maHd = (String) hdTableModel.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this, "Xóa hợp đồng này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try {
                    hopDongController.deleteHopDong(maHd);
                    loadHopDongData();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void showHopDongDialog(HopDong hopDong) {
        boolean isUpdating = hopDong != null;

        JTextField txtMaHd = new JTextField(isUpdating ? hopDong.getMaHd() : "");
        JTextField txtMaNv = new JTextField(selectedMaNv);
        txtMaNv.setEditable(false);

        JComboBox<HopDong.LoaiHopDong> cbLoaiHd = new JComboBox<>(HopDong.LoaiHopDong.values());
        JDateChooser dateNgayBatDau = new JDateChooser();
        JDateChooser dateNgayKetThuc = new JDateChooser();
        JTextField txtLuongCoBan = new JTextField(isUpdating ? hopDong.getLuongCoBan().toPlainString() : "0");
        JComboBox<HopDong.TrangThaiHopDong> cbTrangThai = new JComboBox<>(HopDong.TrangThaiHopDong.values());

        if (isUpdating) {
            txtMaHd.setEditable(false);
            cbLoaiHd.setSelectedItem(hopDong.getLoaiHd());
            dateNgayBatDau.setDate(Date.from(hopDong.getNgayBatDau().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            if (hopDong.getNgayKetThuc() != null) {
                dateNgayKetThuc.setDate(Date.from(hopDong.getNgayKetThuc().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
            cbTrangThai.setSelectedItem(hopDong.getTrangThai());
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Mã Hợp Đồng (*):"));
        panel.add(txtMaHd);
        panel.add(new JLabel("Mã Nhân Viên:"));
        panel.add(txtMaNv);
        panel.add(new JLabel("Loại Hợp Đồng:"));
        panel.add(cbLoaiHd);
        panel.add(new JLabel("Ngày bắt đầu (*):"));
        panel.add(dateNgayBatDau);
        panel.add(new JLabel("Ngày kết thúc:"));
        panel.add(dateNgayKetThuc);
        panel.add(new JLabel("Lương cơ bản (*):"));
        panel.add(txtLuongCoBan);
        panel.add(new JLabel("Trạng thái:"));
        panel.add(cbTrangThai);

        int result = JOptionPane.showConfirmDialog(this, panel, isUpdating ? "Cập nhật Hợp Đồng" : "Tạo Hợp Đồng Mới", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                HopDong hd = new HopDong();
                hd.setMaHd(txtMaHd.getText().trim());
                hd.setMaNv(txtMaNv.getText());
                hd.setLoaiHd((HopDong.LoaiHopDong) cbLoaiHd.getSelectedItem());
                
                if (dateNgayBatDau.getDate() == null) throw new Exception("Vui lòng chọn ngày bắt đầu.");
                hd.setNgayBatDau(dateNgayBatDau.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                
                if (dateNgayKetThuc.getDate() != null) {
                    hd.setNgayKetThuc(dateNgayKetThuc.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                }
                
                try {
                    hd.setLuongCoBan(new BigDecimal(txtLuongCoBan.getText().trim()));
                } catch (NumberFormatException e) {
                    throw new Exception("Lương cơ bản không hợp lệ.");
                }
                
                hd.setTrangThai((HopDong.TrangThaiHopDong) cbTrangThai.getSelectedItem());

                if (isUpdating) {
                    hopDongController.updateHopDong(hd);
                } else {
                    hopDongController.createHopDong(hd);
                }
                loadHopDongData();
                JOptionPane.showMessageDialog(this, "Lưu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}