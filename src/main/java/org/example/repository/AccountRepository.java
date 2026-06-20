package org.example.repository;

import org.example.entity.Account;
import org.example.exception.DataAccessException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface cho Account - tương ứng bảng `tai_khoan`.
 */
public interface AccountRepository {

    /**
     * Tìm account theo tên đăng nhập (dùng cho chức năng đăng nhập).
     */
    Optional<Account> findByTenDangNhap(String tenDangNhap);

    /**
     * Tìm account theo mã tài khoản (PK).
     */
    Optional<Account> findById(Integer maTk);

    /**
     * Tìm account theo mã nhân viên (quan hệ 1-1 với NhanVien).
     */
    Optional<Account> findByMaNv(String maNv);

    /**
     * Lấy danh sách toàn bộ tài khoản.
     */
    List<Account> findAll();

    /**
     * Tạo tài khoản mới, trả về Account đã có maTk (do DB sinh ra).
     */
    Account save(Account account);

    /**
     * Cập nhật thông tin tài khoản (mật khẩu, vai trò, trạng thái...).
     */
    boolean update(Account account);

    /**
     * Cập nhật thời điểm đăng nhập gần nhất.
     */
    boolean updateLastLogin(Integer maTk, LocalDateTime time);

    /**
     * Xoá tài khoản theo mã.
     */
    boolean deleteById(Integer maTk);

    /**
     * Kiểm tra tên đăng nhập đã tồn tại chưa (dùng khi tạo account mới).
     */
    boolean existsByTenDangNhap(String tenDangNhap);
}
