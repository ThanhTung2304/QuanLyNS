package org.example.repository.impl;

import org.example.config.AppContext;
import org.example.database.DatabaseConnection;
import org.example.entity.ChucVu;
import org.example.exception.DataAccessException;
import org.example.repository.ChucVuRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChucVuRepositoryImpl implements ChucVuRepository {

    private final DatabaseConnection databaseConnection;

    private static final String SELECT_BASE = "SELECT ma_chuc_vu, ten_chuc_vu, mo_ta FROM chuc_vu";

    public ChucVuRepositoryImpl() {
        this(AppContext.getInstance().getDatabaseConnection());
    }

    public ChucVuRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public List<ChucVu> findAll() {
        List<ChucVu> list = new ArrayList<>();
        String sql = SELECT_BASE + " ORDER BY ma_chuc_vu";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi lấy danh sách chức vụ", e);
        }
    }

    @Override
    public Optional<ChucVu> findById(String maChucVu) {
        String sql = SELECT_BASE + " WHERE ma_chuc_vu = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maChucVu);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tìm chức vụ: " + maChucVu, e);
        }
    }

    @Override
    public ChucVu save(ChucVu chucVu) {
        String sql = "INSERT INTO chuc_vu (ma_chuc_vu, ten_chuc_vu, mo_ta) VALUES (?, ?, ?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, chucVu.getMaChucVu());
            ps.setString(2, chucVu.getTenChucVu());
            ps.setString(3, chucVu.getMoTa());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new DataAccessException("Tạo chức vụ thất bại", null);
            }
            return chucVu;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tạo chức vụ mới: " + chucVu.getMaChucVu(), e);
        }
    }

    @Override
    public boolean update(ChucVu chucVu) {
        String sql = "UPDATE chuc_vu SET ten_chuc_vu = ?, mo_ta = ? WHERE ma_chuc_vu = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, chucVu.getTenChucVu());
            ps.setString(2, chucVu.getMoTa());
            ps.setString(3, chucVu.getMaChucVu());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi cập nhật chức vụ: " + chucVu.getMaChucVu(), e);
        }
    }

    @Override
    public boolean deleteById(String maChucVu) {
        String sql = "DELETE FROM chuc_vu WHERE ma_chuc_vu = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maChucVu);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi xóa chức vụ: " + maChucVu, e);
        }
    }

    @Override
    public boolean existsById(String maChucVu) {
        String sql = "SELECT 1 FROM chuc_vu WHERE ma_chuc_vu = ? LIMIT 1";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maChucVu);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi kiểm tra mã chức vụ: " + maChucVu, e);
        }
    }

    private ChucVu mapRow(ResultSet rs) throws SQLException {
        ChucVu cv = new ChucVu();
        cv.setMaChucVu(rs.getString("ma_chuc_vu"));
        cv.setTenChucVu(rs.getString("ten_chuc_vu"));
        cv.setMoTa(rs.getString("mo_ta"));
        return cv;
    }
}