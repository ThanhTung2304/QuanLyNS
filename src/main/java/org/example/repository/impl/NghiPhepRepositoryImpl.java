package org.example.repository.impl;

import org.example.config.AppContext;
import org.example.database.DatabaseConnection;
import org.example.entity.NghiPhep;
import org.example.exception.DataAccessException;
import org.example.repository.NghiPhepRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NghiPhepRepositoryImpl implements NghiPhepRepository {

    private final DatabaseConnection databaseConnection;
    private static final String SELECT_BASE = "SELECT ma_np, ma_nv, ngay_bat_dau, ngay_ket_thuc, ly_do, trang_thai, nguoi_duyet, ngay_tao FROM nghi_phep";

    public NghiPhepRepositoryImpl() {
        this(AppContext.getInstance().getDatabaseConnection());
    }

    public NghiPhepRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    // ... (các phương thức cũ giữ nguyên)
    @Override
    public List<NghiPhep> findAll() {
        List<NghiPhep> list = new ArrayList<>();
        String sql = SELECT_BASE + " ORDER BY ngay_tao DESC";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi lấy danh sách nghỉ phép", e);
        }
    }

    @Override
    public List<NghiPhep> findByMaNv(String maNv) {
        List<NghiPhep> list = new ArrayList<>();
        String sql = SELECT_BASE + " WHERE ma_nv = ? ORDER BY ngay_tao DESC";
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
            throw new DataAccessException("Lỗi khi tìm nghỉ phép theo mã nhân viên: " + maNv, e);
        }
    }

    @Override
    public Optional<NghiPhep> findById(Integer maNp) {
        String sql = SELECT_BASE + " WHERE ma_np = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNp);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tìm đơn nghỉ phép: " + maNp, e);
        }
    }

    @Override
    public NghiPhep save(NghiPhep nghiPhep) {
        String sql = "INSERT INTO nghi_phep (ma_nv, ngay_bat_dau, ngay_ket_thuc, ly_do, trang_thai, nguoi_duyet) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nghiPhep.getMaNv());
            ps.setDate(2, Date.valueOf(nghiPhep.getNgayBatDau()));
            ps.setDate(3, Date.valueOf(nghiPhep.getNgayKetThuc()));
            ps.setString(4, nghiPhep.getLyDo());
            ps.setString(5, nghiPhep.getTrangThai().name());
            ps.setString(6, nghiPhep.getNguoiDuyet());
            
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new DataAccessException("Tạo đơn nghỉ phép thất bại", null);
            }
            
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    nghiPhep.setMaNp(keys.getInt(1));
                }
            }
            return nghiPhep;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tạo đơn nghỉ phép mới", e);
        }
    }

    @Override
    public boolean update(NghiPhep nghiPhep) {
        String sql = "UPDATE nghi_phep SET ngay_bat_dau = ?, ngay_ket_thuc = ?, ly_do = ?, trang_thai = ?, nguoi_duyet = ? WHERE ma_np = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(nghiPhep.getNgayBatDau()));
            ps.setDate(2, Date.valueOf(nghiPhep.getNgayKetThuc()));
            ps.setString(3, nghiPhep.getLyDo());
            ps.setString(4, nghiPhep.getTrangThai().name());
            ps.setString(5, nghiPhep.getNguoiDuyet());
            ps.setInt(6, nghiPhep.getMaNp());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi cập nhật đơn nghỉ phép: " + nghiPhep.getMaNp(), e);
        }
    }

    @Override
    public boolean deleteById(Integer maNp) {
        String sql = "DELETE FROM nghi_phep WHERE ma_np = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNp);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi xóa đơn nghỉ phép: " + maNp, e);
        }
    }

    @Override
    public long countPendingRequests() {
        String sql = "SELECT COUNT(*) FROM nghi_phep WHERE trang_thai = 'CHO_DUYET'";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi đếm đơn chờ duyệt", e);
        }
        return 0;
    }

    private NghiPhep mapRow(ResultSet rs) throws SQLException {
        NghiPhep np = new NghiPhep();
        np.setMaNp(rs.getInt("ma_np"));
        np.setMaNv(rs.getString("ma_nv"));
        np.setNgayBatDau(rs.getDate("ngay_bat_dau").toLocalDate());
        np.setNgayKetThuc(rs.getDate("ngay_ket_thuc").toLocalDate());
        np.setLyDo(rs.getString("ly_do"));
        np.setTrangThai(NghiPhep.TrangThaiNghiPhep.valueOf(rs.getString("trang_thai")));
        np.setNguoiDuyet(rs.getString("nguoi_duyet"));
        Timestamp ngayTao = rs.getTimestamp("ngay_tao");
        if(ngayTao != null) {
            np.setNgayTao(ngayTao.toLocalDateTime());
        }
        return np;
    }
}