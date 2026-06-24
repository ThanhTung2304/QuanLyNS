package org.example.repository.impl;

import org.example.config.AppContext;
import org.example.database.DatabaseConnection;
import org.example.entity.ChamCong;
import org.example.exception.DataAccessException;
import org.example.repository.ChamCongRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChamCongRepositoryImpl implements ChamCongRepository {

    private final DatabaseConnection databaseConnection;
    private static final String SELECT_BASE = "SELECT ma_cc, ma_nv, ngay, gio_vao, gio_ra, trang_thai, ghi_chu FROM cham_cong";

    public ChamCongRepositoryImpl() {
        this(AppContext.getInstance().getDatabaseConnection());
    }

    public ChamCongRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    // ... (các phương thức cũ giữ nguyên)
    @Override
    public List<ChamCong> findByMaNv(String maNv) {
        List<ChamCong> list = new ArrayList<>();
        String sql = SELECT_BASE + " WHERE ma_nv = ? ORDER BY ngay DESC";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNv);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tìm chấm công theo mã nhân viên: " + maNv, e);
        }
    }

    @Override
    public Optional<ChamCong> findById(Integer maCc) {
        String sql = SELECT_BASE + " WHERE ma_cc = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maCc);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tìm chấm công: " + maCc, e);
        }
    }
    
    @Override
    public Optional<ChamCong> findByMaNvAndNgay(String maNv, LocalDate ngay) {
        String sql = SELECT_BASE + " WHERE ma_nv = ? AND ngay = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNv);
            ps.setDate(2, Date.valueOf(ngay));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tìm chấm công theo NV và ngày: " + maNv, e);
        }
    }

    @Override
    public int countCongNgay(String maNv, int thang, int nam) {
        String sql = "SELECT COUNT(*) FROM cham_cong WHERE ma_nv = ? AND MONTH(ngay) = ? AND YEAR(ngay) = ? AND trang_thai IN ('DI_LAM', 'DI_MUON')";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNv);
            ps.setInt(2, thang);
            ps.setInt(3, nam);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi đếm ngày công", e);
        }
    }

    @Override
    public ChamCong save(ChamCong chamCong) {
        String sql = "INSERT INTO cham_cong (ma_nv, ngay, gio_vao, gio_ra, trang_thai, ghi_chu) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, chamCong.getMaNv());
            ps.setDate(2, Date.valueOf(chamCong.getNgay()));
            ps.setTime(3, chamCong.getGioVao() != null ? Time.valueOf(chamCong.getGioVao()) : null);
            ps.setTime(4, chamCong.getGioRa() != null ? Time.valueOf(chamCong.getGioRa()) : null);
            ps.setString(5, chamCong.getTrangThai().name());
            ps.setString(6, chamCong.getGhiChu());
            
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new DataAccessException("Tạo chấm công thất bại", null);
            }
            
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    chamCong.setMaCc(keys.getInt(1));
                }
            }
            return chamCong;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tạo chấm công mới", e);
        }
    }

    @Override
    public boolean update(ChamCong chamCong) {
        String sql = "UPDATE cham_cong SET ngay = ?, gio_vao = ?, gio_ra = ?, trang_thai = ?, ghi_chu = ? WHERE ma_cc = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(chamCong.getNgay()));
            ps.setTime(2, chamCong.getGioVao() != null ? Time.valueOf(chamCong.getGioVao()) : null);
            ps.setTime(3, chamCong.getGioRa() != null ? Time.valueOf(chamCong.getGioRa()) : null);
            ps.setString(4, chamCong.getTrangThai().name());
            ps.setString(5, chamCong.getGhiChu());
            ps.setInt(6, chamCong.getMaCc());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi cập nhật chấm công: " + chamCong.getMaCc(), e);
        }
    }

    @Override
    public boolean deleteById(Integer maCc) {
        String sql = "DELETE FROM cham_cong WHERE ma_cc = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maCc);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi xóa chấm công: " + maCc, e);
        }
    }

    @Override
    public long countCheckInsToday() {
        String sql = "SELECT COUNT(*) FROM cham_cong WHERE ngay = CURRENT_DATE()";
        return executeCountQuery(sql);
    }

    @Override
    public long countLateCheckInsToday() {
        String sql = "SELECT COUNT(*) FROM cham_cong WHERE ngay = CURRENT_DATE() AND trang_thai = 'DI_MUON'";
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

    private ChamCong mapRow(ResultSet rs) throws SQLException {
        ChamCong cc = new ChamCong();
        cc.setMaCc(rs.getInt("ma_cc"));
        cc.setMaNv(rs.getString("ma_nv"));
        cc.setNgay(rs.getDate("ngay").toLocalDate());
        
        Time gioVao = rs.getTime("gio_vao");
        if (gioVao != null) cc.setGioVao(gioVao.toLocalTime());
        
        Time gioRa = rs.getTime("gio_ra");
        if (gioRa != null) cc.setGioRa(gioRa.toLocalTime());
        
        cc.setTrangThai(ChamCong.TrangThaiChamCong.valueOf(rs.getString("trang_thai")));
        cc.setGhiChu(rs.getString("ghi_chu"));
        return cc;
    }
}