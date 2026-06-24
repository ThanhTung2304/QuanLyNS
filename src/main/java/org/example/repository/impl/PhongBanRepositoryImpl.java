package org.example.repository.impl;

import org.example.config.AppContext;
import org.example.database.DatabaseConnection;
import org.example.entity.PhongBan;
import org.example.exception.DataAccessException;
import org.example.repository.PhongBanRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PhongBanRepositoryImpl implements PhongBanRepository {

    private final DatabaseConnection databaseConnection;

    // Câu SQL đã được sửa lại hoàn toàn để khớp với DB gốc của bạn
    private static final String SELECT_ALL_INFO =
        "SELECT " +
        "    pb.ma_phong, " +
        "    pb.ten_phong, " +
        "    pb.mo_ta, " +
        "    (SELECT nv.ho_ten FROM nhan_vien nv WHERE nv.ma_phong = pb.ma_phong AND nv.ma_chuc_vu = 'CV01' LIMIT 1) AS ten_truong_phong, " +
        "    (SELECT COUNT(*) FROM nhan_vien nv WHERE nv.ma_phong = pb.ma_phong) AS so_luong_nhan_vien " +
        "FROM phong_ban pb";

    public PhongBanRepositoryImpl() {
        this(AppContext.getInstance().getDatabaseConnection());
    }

    public PhongBanRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public List<PhongBan> findAll() {
        List<PhongBan> list = new ArrayList<>();
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_INFO);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi lấy danh sách phòng ban", e);
        }
    }

    @Override
    public Optional<PhongBan> findById(String maPhong) {
        String sql = SELECT_ALL_INFO + " WHERE pb.ma_phong = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhong);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tìm phòng ban: " + maPhong, e);
        }
    }

    @Override
    public PhongBan save(PhongBan phongBan) {
        String sql = "INSERT INTO phong_ban (ma_phong, ten_phong, mo_ta) VALUES (?, ?, ?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phongBan.getMaPhong());
            ps.setString(2, phongBan.getTenPhong());
            ps.setString(3, phongBan.getMoTa());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new DataAccessException("Tạo phòng ban thất bại", null);
            }
            return phongBan;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tạo phòng ban: " + phongBan.getMaPhong(), e);
        }
    }

    @Override
    public boolean update(PhongBan phongBan) {
        String sql = "UPDATE phong_ban SET ten_phong = ?, mo_ta = ? WHERE ma_phong = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phongBan.getTenPhong());
            ps.setString(2, phongBan.getMoTa());
            ps.setString(3, phongBan.getMaPhong());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi cập nhật phòng ban: " + phongBan.getMaPhong(), e);
        }
    }

    @Override
    public boolean deleteById(String maPhong) {
        String sql = "DELETE FROM phong_ban WHERE ma_phong = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhong);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi xóa phòng ban: " + maPhong, e);
        }
    }

    @Override
    public boolean existsById(String maPhong) {
        String sql = "SELECT 1 FROM phong_ban WHERE ma_phong = ? LIMIT 1";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhong);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi kiểm tra mã phòng ban: " + maPhong, e);
        }
    }

    private PhongBan mapRow(ResultSet rs) throws SQLException {
        PhongBan pb = new PhongBan();
        pb.setMaPhong(rs.getString("ma_phong"));
        pb.setTenPhong(rs.getString("ten_phong"));
        pb.setMoTa(rs.getString("mo_ta"));
        pb.setTenTruongPhong(rs.getString("ten_truong_phong"));
        pb.setSoLuongNhanVien(rs.getInt("so_luong_nhan_vien"));
        return pb;
    }
}