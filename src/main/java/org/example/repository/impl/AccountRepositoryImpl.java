package org.example.repository.impl;

import org.example.config.AppContext;
import org.example.database.DatabaseConnection;
import org.example.entity.Account;
import org.example.exception.DataAccessException;
import org.example.repository.AccountRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

/**
 * Triển khai AccountRepository sử dụng JDBC thuần.
 * Mọi SQLException đều được bắt và bọc lại thành DataAccessException
 * để tầng Service không cần biết chi tiết JDBC.
 */
public class AccountRepositoryImpl implements AccountRepository {

    private final DatabaseConnection databaseConnection;

    private static final String SELECT_BASE =
            "SELECT ma_tk, ma_nv, ten_dang_nhap, mat_khau, vai_tro, trang_thai, " +
                    "lan_dang_nhap_cuoi, ngay_tao FROM tai_khoan";

    public AccountRepositoryImpl() {
        this(AppContext.getInstance().getDatabaseConnection());
    }

    public AccountRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Optional<Account> findByTenDangNhap(String tenDangNhap) {
        String sql = SELECT_BASE + " WHERE ten_dang_nhap = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tenDangNhap);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tìm tài khoản theo tên đăng nhập: " + tenDangNhap, e);
        }
    }

    @Override
    public Optional<Account> findById(Integer maTk) {
        String sql = SELECT_BASE + " WHERE ma_tk = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maTk);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tìm tài khoản theo mã: " + maTk, e);
        }
    }

    @Override
    public Optional<Account> findByMaNv(String maNv) {
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
            throw new DataAccessException("Lỗi khi tìm tài khoản theo mã nhân viên: " + maNv, e);
        }
    }

    @Override
    public List<Account> findAll() {
        String sql = SELECT_BASE + " ORDER BY ma_tk";
        List<Account> list = new ArrayList<>();
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi lấy danh sách tài khoản", e);
        }
    }

    @Override
    public Account save(Account account) {
        String sql = "INSERT INTO tai_khoan (ma_nv, ten_dang_nhap, mat_khau, vai_tro, trang_thai) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, account.getMaNv());
            ps.setString(2, account.getTenDangNhap());
            ps.setString(3, account.getMatKhau());
            ps.setString(4, account.getVaiTro().name());
            ps.setString(5, account.getTrangThai().name());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new DataAccessException("Tạo tài khoản thất bại, không có dòng nào được thêm", null);
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    account.setMaTk(keys.getInt(1));
                }
            }
            return account;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tạo tài khoản mới: " + account.getTenDangNhap(), e);
        }
    }

    @Override
    public boolean update(Account account) {
        String sql = "UPDATE tai_khoan SET ten_dang_nhap = ?, mat_khau = ?, vai_tro = ?, " +
                "trang_thai = ? WHERE ma_tk = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, account.getTenDangNhap());
            ps.setString(2, account.getMatKhau());
            ps.setString(3, account.getVaiTro().name());
            ps.setString(4, account.getTrangThai().name());
            ps.setInt(5, account.getMaTk());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi cập nhật tài khoản: " + account.getMaTk(), e);
        }
    }

    @Override
    public boolean updateLastLogin(Integer maTk, LocalDateTime time) {
        String sql = "UPDATE tai_khoan SET lan_dang_nhap_cuoi = ? WHERE ma_tk = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(time));
            ps.setInt(2, maTk);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi cập nhật lần đăng nhập cuối: " + maTk, e);
        }
    }

    @Override
    public boolean deleteById(Integer maTk) {
        String sql = "DELETE FROM tai_khoan WHERE ma_tk = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maTk);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi xoá tài khoản: " + maTk, e);
        }
    }

    @Override
    public boolean existsByTenDangNhap(String tenDangNhap) {
        String sql = "SELECT 1 FROM tai_khoan WHERE ten_dang_nhap = ? LIMIT 1";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tenDangNhap);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi kiểm tra tên đăng nhập: " + tenDangNhap, e);
        }
    }

    /**
     * Map một dòng ResultSet thành Account entity.
     */
    private Account mapRow(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setMaTk(rs.getInt("ma_tk"));
        account.setMaNv(rs.getString("ma_nv"));
        account.setTenDangNhap(rs.getString("ten_dang_nhap"));
        account.setMatKhau(rs.getString("mat_khau"));
        account.setVaiTro(Account.Role.valueOf(rs.getString("vai_tro")));
        account.setTrangThai(Account.Status.valueOf(rs.getString("trang_thai")));

        Timestamp lastLogin = rs.getTimestamp("lan_dang_nhap_cuoi");
        if (lastLogin != null) {
            account.setLanDangNhapCuoi(lastLogin.toLocalDateTime());
        }

        Timestamp ngayTao = rs.getTimestamp("ngay_tao");
        if (ngayTao != null) {
            account.setNgayTao(ngayTao.toLocalDateTime());
        }

        return account;
    }
}
