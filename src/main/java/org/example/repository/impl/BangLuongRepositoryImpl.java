package org.example.repository.impl;

import org.example.config.AppContext;
import org.example.database.DatabaseConnection;
import org.example.entity.BangLuong;
import org.example.exception.DataAccessException;
import org.example.repository.BangLuongRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BangLuongRepositoryImpl implements BangLuongRepository {

    private final DatabaseConnection databaseConnection;

    public BangLuongRepositoryImpl() {
        this(AppContext.getInstance().getDatabaseConnection());
    }

    public BangLuongRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    // ... (các phương thức cũ giữ nguyên)
    @Override
    public void saveAll(List<BangLuong> bangLuongList) {
        String sql = "INSERT INTO bang_luong (ma_nv, thang, nam, so_ngay_cong, luong_cb, phu_cap, khau_tru, thuc_linh) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (BangLuong bl : bangLuongList) {
                    ps.setString(1, bl.getMaNv());
                    ps.setInt(2, bl.getThang());
                    ps.setInt(3, bl.getNam());
                    ps.setInt(4, bl.getSoNgayCong());
                    ps.setBigDecimal(5, bl.getLuongCb());
                    ps.setBigDecimal(6, bl.getPhuCap());
                    ps.setBigDecimal(7, bl.getKhauTru());
                    ps.setBigDecimal(8, bl.getThucLinh());
                    ps.addBatch();
                }
                ps.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new DataAccessException("Lỗi khi lưu bảng lương", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi kết nối cơ sở dữ liệu khi lưu bảng lương", e);
        }
    }

    @Override
    public boolean existsByMaNvAndThangAndNam(String maNv, int thang, int nam) {
        String sql = "SELECT 1 FROM bang_luong WHERE ma_nv = ? AND thang = ? AND nam = ? LIMIT 1";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNv);
            ps.setInt(2, thang);
            ps.setInt(3, nam);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi kiểm tra bảng lương đã tồn tại", e);
        }
    }

    @Override
    public List<BangLuong> findByThangAndNam(int thang, int nam) {
        List<BangLuong> list = new ArrayList<>();
        String sql = "SELECT bl.*, nv.ho_ten FROM bang_luong bl JOIN nhan_vien nv ON bl.ma_nv = nv.ma_nv WHERE bl.thang = ? AND bl.nam = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, thang);
            ps.setInt(2, nam);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tìm bảng lương theo tháng và năm", e);
        }
    }

    @Override
    public Optional<BangLuong> findByMaNvAndThangAndNam(String maNv, int thang, int nam) {
        String sql = "SELECT bl.*, nv.ho_ten FROM bang_luong bl JOIN nhan_vien nv ON bl.ma_nv = nv.ma_nv WHERE bl.ma_nv = ? AND bl.thang = ? AND bl.nam = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNv);
            ps.setInt(2, thang);
            ps.setInt(3, nam);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tìm bảng lương của nhân viên theo tháng", e);
        }
    }

    @Override
    public void deleteByThangAndNam(int thang, int nam) {
        String sql = "DELETE FROM bang_luong WHERE thang = ? AND nam = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, thang);
            ps.setInt(2, nam);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi xóa bảng lương theo tháng và năm", e);
        }
    }

    @Override
    public BigDecimal sumTotalSalaryForMonth(int thang, int nam) {
        String sql = "SELECT SUM(thuc_linh) FROM bang_luong WHERE thang = ? AND nam = ?";
        return executeSumQuery(sql, thang, nam);
    }

    @Override
    public BigDecimal sumTotalBonusForMonth(int thang, int nam) {
        // Giả sử "thưởng" không có trong bảng bang_luong, cần tính từ nguồn khác hoặc thêm cột
        // Hiện tại trả về 0
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal sumTotalDeductionForMonth(int thang, int nam) {
        String sql = "SELECT SUM(khau_tru) FROM bang_luong WHERE thang = ? AND nam = ?";
        return executeSumQuery(sql, thang, nam);
    }

    private BigDecimal executeSumQuery(String sql, int thang, int nam) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, thang);
            ps.setInt(2, nam);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal sum = rs.getBigDecimal(1);
                    return sum != null ? sum : BigDecimal.ZERO;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi thực thi câu lệnh tính tổng", e);
        }
        return BigDecimal.ZERO;
    }

    private BangLuong mapRow(ResultSet rs) throws SQLException {
        BangLuong bl = new BangLuong();
        bl.setMaBl(rs.getInt("ma_bl"));
        bl.setMaNv(rs.getString("ma_nv"));
        bl.setThang(rs.getInt("thang"));
        bl.setNam(rs.getInt("nam"));
        bl.setSoNgayCong(rs.getInt("so_ngay_cong"));
        bl.setLuongCb(rs.getBigDecimal("luong_cb"));
        bl.setPhuCap(rs.getBigDecimal("phu_cap"));
        bl.setKhauTru(rs.getBigDecimal("khau_tru"));
        bl.setThucLinh(rs.getBigDecimal("thuc_linh"));
        bl.setNgayTao(rs.getTimestamp("ngay_tao").toLocalDateTime());
        
        if (rs.getMetaData().getColumnCount() > 9) {
             bl.setHoTen(rs.getString("ho_ten"));
        }
       
        return bl;
    }
}