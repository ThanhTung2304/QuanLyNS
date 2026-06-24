package org.example.repository.impl;

import org.example.config.AppContext;
import org.example.database.DatabaseConnection;
import org.example.entity.CauHinh;
import org.example.exception.DataAccessException;
import org.example.repository.CauHinhRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class CauHinhRepositoryImpl implements CauHinhRepository {

    private final DatabaseConnection databaseConnection;

    public CauHinhRepositoryImpl() {
        this(AppContext.getInstance().getDatabaseConnection());
    }

    public CauHinhRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Optional<CauHinh> findByKey(String key) {
        String sql = "SELECT setting_key, setting_value, mo_ta FROM cau_hinh WHERE setting_key = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tìm cấu hình: " + key, e);
        }
    }

    @Override
    public boolean saveOrUpdate(CauHinh cauHinh) {
        // Lệnh INSERT ... ON DUPLICATE KEY UPDATE sẽ tự động chèn mới nếu chưa có, hoặc cập nhật nếu đã có.
        String sql = "INSERT INTO cau_hinh (setting_key, setting_value, mo_ta) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cauHinh.getSettingKey());
            ps.setString(2, cauHinh.getSettingValue());
            ps.setString(3, cauHinh.getMoTa());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi lưu cấu hình: " + cauHinh.getSettingKey(), e);
        }
    }

    private CauHinh mapRow(ResultSet rs) throws SQLException {
        return new CauHinh(
                rs.getString("setting_key"),
                rs.getString("setting_value"),
                rs.getString("mo_ta")
        );
    }
}