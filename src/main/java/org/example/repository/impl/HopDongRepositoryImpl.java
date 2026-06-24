package org.example.repository.impl;

import org.example.config.AppContext;
import org.example.database.DatabaseConnection;
import org.example.entity.HopDong;
import org.example.exception.DataAccessException;
import org.example.repository.HopDongRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HopDongRepositoryImpl implements HopDongRepository {

    private final DatabaseConnection databaseConnection;
    private static final String SELECT_BASE = "SELECT ma_hd, ma_nv, loai_hd, ngay_bat_dau, ngay_ket_thuc, luong_co_ban, trang_thai FROM hop_dong";

    public HopDongRepositoryImpl() {
        this(AppContext.getInstance().getDatabaseConnection());
    }

    public HopDongRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public List<HopDong> findByMaNv(String maNv) {
        List<HopDong> list = new ArrayList<>();
        String sql = SELECT_BASE + " WHERE ma_nv = ? ORDER BY ngay_bat_dau DESC";
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
            throw new DataAccessException("Lỗi khi tìm hợp đồng theo mã nhân viên: " + maNv, e);
        }
    }

    @Override
    public Optional<HopDong> findById(String maHd) {
        String sql = SELECT_BASE + " WHERE ma_hd = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHd);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tìm hợp đồng: " + maHd, e);
        }
    }

    @Override
    public Optional<HopDong> findLatestByMaNv(String maNv) {
        String sql = SELECT_BASE + " WHERE ma_nv = ? ORDER BY ngay_bat_dau DESC LIMIT 1";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tìm hợp đồng mới nhất: " + maNv, e);
        }
    }

    @Override
    public HopDong save(HopDong hopDong) {
        String sql = "INSERT INTO hop_dong (ma_hd, ma_nv, loai_hd, ngay_bat_dau, ngay_ket_thuc, luong_co_ban, trang_thai) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hopDong.getMaHd());
            ps.setString(2, hopDong.getMaNv());
            ps.setString(3, hopDong.getLoaiHd().name());
            ps.setDate(4, Date.valueOf(hopDong.getNgayBatDau()));
            ps.setDate(5, hopDong.getNgayKetThuc() != null ? Date.valueOf(hopDong.getNgayKetThuc()) : null);
            ps.setBigDecimal(6, hopDong.getLuongCoBan());
            ps.setString(7, hopDong.getTrangThai().name());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new DataAccessException("Tạo hợp đồng thất bại", null);
            }
            return hopDong;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tạo hợp đồng mới: " + hopDong.getMaHd(), e);
        }
    }

    @Override
    public boolean update(HopDong hopDong) {
        String sql = "UPDATE hop_dong SET loai_hd = ?, ngay_bat_dau = ?, ngay_ket_thuc = ?, luong_co_ban = ?, trang_thai = ? WHERE ma_hd = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hopDong.getLoaiHd().name());
            ps.setDate(2, Date.valueOf(hopDong.getNgayBatDau()));
            ps.setDate(3, hopDong.getNgayKetThuc() != null ? Date.valueOf(hopDong.getNgayKetThuc()) : null);
            ps.setBigDecimal(4, hopDong.getLuongCoBan());
            ps.setString(5, hopDong.getTrangThai().name());
            ps.setString(6, hopDong.getMaHd());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi cập nhật hợp đồng: " + hopDong.getMaHd(), e);
        }
    }

    @Override
    public boolean deleteById(String maHd) {
        String sql = "DELETE FROM hop_dong WHERE ma_hd = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHd);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi xóa hợp đồng: " + maHd, e);
        }
    }

    @Override
    public boolean existsById(String maHd) {
        String sql = "SELECT 1 FROM hop_dong WHERE ma_hd = ? LIMIT 1";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHd);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi kiểm tra mã hợp đồng: " + maHd, e);
        }
    }

    private HopDong mapRow(ResultSet rs) throws SQLException {
        HopDong hd = new HopDong();
        hd.setMaHd(rs.getString("ma_hd"));
        hd.setMaNv(rs.getString("ma_nv"));
        hd.setLoaiHd(HopDong.LoaiHopDong.valueOf(rs.getString("loai_hd")));
        hd.setNgayBatDau(rs.getDate("ngay_bat_dau").toLocalDate());
        Date ngayKetThuc = rs.getDate("ngay_ket_thuc");
        if (ngayKetThuc != null) {
            hd.setNgayKetThuc(ngayKetThuc.toLocalDate());
        }
        hd.setLuongCoBan(rs.getBigDecimal("luong_co_ban"));
        hd.setTrangThai(HopDong.TrangThaiHopDong.valueOf(rs.getString("trang_thai")));
        return hd;
    }
}