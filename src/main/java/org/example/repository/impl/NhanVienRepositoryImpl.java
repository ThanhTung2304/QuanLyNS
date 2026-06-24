package org.example.repository.impl;

import org.example.config.AppContext;
import org.example.database.DatabaseConnection;
import org.example.entity.NhanVien;
import org.example.exception.DataAccessException;
import org.example.repository.NhanVienRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.Date;

public class NhanVienRepositoryImpl implements NhanVienRepository {

    private final DatabaseConnection databaseConnection;

    private static final String SELECT_BASE =
            "SELECT ma_nv, ho_ten, ngay_sinh, gioi_tinh, dia_chi, so_dien_thoai, email, " +
                    "ma_phong, ma_chuc_vu, ngay_vao_lam, trang_thai FROM nhan_vien";

    public NhanVienRepositoryImpl() {
        this(AppContext.getInstance().getDatabaseConnection());
    }

    public NhanVienRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }
    
    // ... (các phương thức cũ giữ nguyên)

    @Override
    public Optional<NhanVien> findById(String maNv) {
        String sql = SELECT_BASE + " WHERE ma_nv = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tìm nhân viên theo mã: " + maNv, e);
        }
    }

    @Override
    public List<NhanVien> findAll() {
        String sql = SELECT_BASE + " ORDER BY ma_nv";
        List<NhanVien> list = new ArrayList<>();
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi lấy danh sách nhân viên", e);
        }
    }

    @Override
    public List<NhanVien> findByMaPhong(String maPhong) {
        String sql = SELECT_BASE + " WHERE ma_phong = ?";
        List<NhanVien> list = new ArrayList<>();
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhong);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tìm nhân viên theo phòng ban: " + maPhong, e);
        }
    }

    @Override
    public NhanVien save(NhanVien nhanVien) {
        String sql = "INSERT INTO nhan_vien (ma_nv, ho_ten, ngay_sinh, gioi_tinh, dia_chi, so_dien_thoai, email, ma_phong, ma_chuc_vu, ngay_vao_lam, trang_thai) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nhanVien.getMaNv());
            ps.setString(2, nhanVien.getHoTen());
            ps.setDate(3, nhanVien.getNgaySinh() != null ? Date.valueOf(nhanVien.getNgaySinh()) : null);
            ps.setString(4, nhanVien.getGioiTinh() != null ? nhanVien.getGioiTinh().name() : null);
            ps.setString(5, nhanVien.getDiaChi());
            ps.setString(6, nhanVien.getSoDienThoai());
            ps.setString(7, nhanVien.getEmail());
            ps.setString(8, nhanVien.getMaPb());
            ps.setString(9, nhanVien.getMaChucVu());
            ps.setDate(10, nhanVien.getNgayVaoLam() != null ? Date.valueOf(nhanVien.getNgayVaoLam()) : null);
            ps.setString(11, nhanVien.getTrangThai() != null ? nhanVien.getTrangThai().name() : NhanVien.TrangThaiLamViec.DANG_LAM_VIEC.name());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new DataAccessException("Tạo nhân viên thất bại", null);
            }
            return nhanVien;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tạo nhân viên mới: " + nhanVien.getMaNv(), e);
        }
    }

    @Override
    public boolean update(NhanVien nhanVien) {
        String sql = "UPDATE nhan_vien SET ho_ten = ?, ngay_sinh = ?, gioi_tinh = ?, dia_chi = ?, " +
                "so_dien_thoai = ?, email = ?, ma_phong = ?, ma_chuc_vu = ?, ngay_vao_lam = ?, trang_thai = ? " +
                "WHERE ma_nv = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nhanVien.getHoTen());
            ps.setDate(2, nhanVien.getNgaySinh() != null ? Date.valueOf(nhanVien.getNgaySinh()) : null);
            ps.setString(3, nhanVien.getGioiTinh() != null ? nhanVien.getGioiTinh().name() : null);
            ps.setString(4, nhanVien.getDiaChi());
            ps.setString(5, nhanVien.getSoDienThoai());
            ps.setString(6, nhanVien.getEmail());
            ps.setString(7, nhanVien.getMaPb());
            ps.setString(8, nhanVien.getMaChucVu());
            ps.setDate(9, nhanVien.getNgayVaoLam() != null ? Date.valueOf(nhanVien.getNgayVaoLam()) : null);
            ps.setString(10, nhanVien.getTrangThai() != null ? nhanVien.getTrangThai().name() : null);
            ps.setString(11, nhanVien.getMaNv());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi cập nhật nhân viên: " + nhanVien.getMaNv(), e);
        }
    }

    @Override
    public boolean updateChucVu(String maNv, String maChucVuMoi) {
        String sql = "UPDATE nhan_vien SET ma_chuc_vu = ? WHERE ma_nv = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maChucVuMoi);
            ps.setString(2, maNv);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi cập nhật chức vụ cho nhân viên: " + maNv, e);
        }
    }

    @Override
    public boolean deleteById(String maNv) {
        String sql = "DELETE FROM nhan_vien WHERE ma_nv = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNv);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi xoá nhân viên: " + maNv, e);
        }
    }

    @Override
    public List<NhanVien> findByNameContaining(String name) {
        String sql = SELECT_BASE + " WHERE ho_ten LIKE ? ORDER BY ho_ten";
        List<NhanVien> list = new ArrayList<>();
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tìm kiếm nhân viên theo tên: " + name, e);
        }
    }
    
    @Override
    public boolean existsById(String maNv) {
        String sql = "SELECT 1 FROM nhan_vien WHERE ma_nv = ? LIMIT 1";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNv);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi kiểm tra mã nhân viên: " + maNv, e);
        }
    }

    @Override
    public long countActiveEmployees() {
        String sql = "SELECT COUNT(*) FROM nhan_vien WHERE trang_thai = 'DANG_LAM_VIEC'";
        return executeCountQuery(sql);
    }

    @Override
    public long countNewEmployeesInCurrentMonth() {
        String sql = "SELECT COUNT(*) FROM nhan_vien WHERE MONTH(ngay_vao_lam) = MONTH(CURRENT_DATE()) AND YEAR(ngay_vao_lam) = YEAR(CURRENT_DATE())";
        return executeCountQuery(sql);
    }

    @Override
    public long countResignedEmployeesInCurrentMonth() {
        // Giả sử có một cột `ngay_nghi_viec`, nếu không có, logic này cần được điều chỉnh
        // Hiện tại, chúng ta có thể đếm những người có trạng thái DA_NGHI_VIEC
        String sql = "SELECT COUNT(*) FROM nhan_vien WHERE trang_thai = 'DA_NGHI_VIEC'"; // Query đơn giản
        return executeCountQuery(sql);
    }

    private long executeCountQuery(String sql) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi thực thi câu lệnh đếm", e);
        }
        return 0;
    }

    private NhanVien mapRow(ResultSet rs) throws SQLException {
        NhanVien nv = new NhanVien();
        nv.setMaNv(rs.getString("ma_nv"));
        nv.setHoTen(rs.getString("ho_ten"));
        Date ngaySinh = rs.getDate("ngay_sinh");
        if (ngaySinh != null) {
            nv.setNgaySinh(ngaySinh.toLocalDate());
        }
        String gioiTinhStr = rs.getString("gioi_tinh");
        if (gioiTinhStr != null) {
            nv.setGioiTinh(NhanVien.GioiTinh.valueOf(gioiTinhStr));
        }
        nv.setDiaChi(rs.getString("dia_chi"));
        nv.setSoDienThoai(rs.getString("so_dien_thoai"));
        nv.setEmail(rs.getString("email"));
        nv.setMaPb(rs.getString("ma_phong"));
        nv.setMaChucVu(rs.getString("ma_chuc_vu"));
        Date ngayVaoLam = rs.getDate("ngay_vao_lam");
        if (ngayVaoLam != null) {
            nv.setNgayVaoLam(ngayVaoLam.toLocalDate());
        }
        String trangThaiStr = rs.getString("trang_thai");
        if (trangThaiStr != null) {
            nv.setTrangThai(NhanVien.TrangThaiLamViec.valueOf(trangThaiStr));
        }
        return nv;
    }
}