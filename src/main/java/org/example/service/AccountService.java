package org.example.service;

import org.example.entity.Account;
import org.example.exception.BusinessException;
import org.example.exception.ValidationException;

import java.util.List;
import java.util.Optional;

/**
 * Service xử lý nghiệp vụ liên quan đến Tài khoản: đăng nhập, tạo tài khoản,
 * đổi mật khẩu, khoá/mở khoá tài khoản.
 */
public interface AccountService {

    /**
     * Xác thực đăng nhập.
     *
     * @param tenDangNhap tên đăng nhập người dùng nhập
     * @param matKhauTho  mật khẩu người dùng nhập
     * @return Account nếu đăng nhập thành công
     * @throws BusinessException nếu sai tài khoản/mật khẩu hoặc tài khoản bị khoá
     */
    Account login(String tenDangNhap, String matKhauTho) throws BusinessException;

    /**
     * Tạo tài khoản mới cho một nhân viên.
     *
     * @throws ValidationException nếu dữ liệu đầu vào không hợp lệ
     * @throws BusinessException   nếu tên đăng nhập đã tồn tại hoặc nhân viên đã có tài khoản
     */
    Account createAccount(String maNv, String tenDangNhap, String matKhauTho, Account.Role vaiTro)
            throws ValidationException, BusinessException;

    /**
     * Đổi mật khẩu - yêu cầu xác thực mật khẩu cũ trước.
     *
     * @throws BusinessException   nếu mật khẩu cũ không đúng
     * @throws ValidationException nếu mật khẩu mới không hợp lệ (quá ngắn...)
     */
    void changePassword(Integer maTk, String matKhauCu, String matKhauMoi)
            throws BusinessException, ValidationException;

    /**
     * Đặt lại mật khẩu (dành cho ADMIN/HR, không cần mật khẩu cũ).
     */
    void resetPassword(Integer maTk, String matKhauMoi) throws ValidationException, BusinessException;

    /**
     * Khoá tài khoản.
     */
    void lockAccount(Integer maTk) throws BusinessException;

    /**
     * Mở khoá tài khoản.
     */
    void unlockAccount(Integer maTk) throws BusinessException;

    /**
     * Lấy thông tin tài khoản theo mã nhân viên.
     */
    Optional<Account> getByMaNv(String maNv);

    /**
     * Lấy danh sách toàn bộ tài khoản (dùng cho màn hình quản lý tài khoản - ADMIN).
     */
    List<Account> getAllAccounts();
}
