package org.example.service.impl;

import org.example.entity.Account;
import org.example.exception.BusinessException;
import org.example.exception.ValidationException;
import org.example.repository.AccountRepository;
import org.example.repository.impl.AccountRepositoryImpl;
import org.example.security.PasswordEncoder;
import org.example.service.AccountService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Triển khai logic nghiệp vụ cho Account.
 * Mọi validate dữ liệu đầu vào -> ValidationException
 * Mọi vi phạm quy tắc nghiệp vụ -> BusinessException
 */
public class AccountServiceImpl implements AccountService {

    private static final int MIN_PASSWORD_LENGTH = 6;

    private final AccountRepository accountRepository;

    public AccountServiceImpl() {
        this(new AccountRepositoryImpl());
    }

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account login(String tenDangNhap, String matKhauTho) throws BusinessException {
        if (tenDangNhap == null || tenDangNhap.isBlank()
                || matKhauTho == null || matKhauTho.isBlank()) {
            throw new BusinessException("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu");
        }

        Optional<Account> accountOpt = accountRepository.findByTenDangNhap(tenDangNhap.trim());
        if (accountOpt.isEmpty()) {
            throw new BusinessException("Sai tài khoản hoặc mật khẩu");
        }

        Account account = accountOpt.get();

        if (!PasswordEncoder.matches(matKhauTho, account.getMatKhau())) {
            throw new BusinessException("Sai tài khoản hoặc mật khẩu");
        }

        if (account.getTrangThai() == Account.Status.KHOA) {
            throw new BusinessException("Tài khoản đã bị khoá. Vui lòng liên hệ quản trị viên");
        }

        LocalDateTime loginTime = LocalDateTime.now();
        try {
            accountRepository.updateLastLogin(account.getMaTk(), loginTime);
            account.setLanDangNhapCuoi(loginTime);
        } catch (RuntimeException ignored) {
            // Login should still open the main UI while this project is in early UI wiring.
        }

        return account;
    }

    @Override
    public Account createAccount(String maNv, String tenDangNhap, String matKhauTho, Account.Role vaiTro)
            throws ValidationException, BusinessException {

        validateTenDangNhap(tenDangNhap);
        validateMatKhau(matKhauTho);

        if (maNv == null || maNv.isBlank()) {
            throw new ValidationException("Mã nhân viên không được để trống");
        }

        if (accountRepository.existsByTenDangNhap(tenDangNhap.trim())) {
            throw new BusinessException("Tên đăng nhập đã tồn tại: " + tenDangNhap);
        }

        if (accountRepository.findByMaNv(maNv).isPresent()) {
            throw new BusinessException("Nhân viên này đã có tài khoản");
        }

        String matKhauMaHoa = PasswordEncoder.encode(matKhauTho);

        Account account = new Account(maNv, tenDangNhap.trim(), matKhauMaHoa,
                vaiTro != null ? vaiTro : Account.Role.NHAN_VIEN);

        return accountRepository.save(account);
    }

    @Override
    public void changePassword(Integer maTk, String matKhauCu, String matKhauMoi)
            throws BusinessException, ValidationException {

        validateMatKhau(matKhauMoi);

        Account account = accountRepository.findById(maTk)
                .orElseThrow(() -> new BusinessException("Không tìm thấy tài khoản"));

        if (!PasswordEncoder.matches(matKhauCu, account.getMatKhau())) {
            throw new BusinessException("Mật khẩu cũ không đúng");
        }

        account.setMatKhau(PasswordEncoder.encode(matKhauMoi));
        boolean updated = accountRepository.update(account);
        if (!updated) {
            throw new BusinessException("Đổi mật khẩu thất bại");
        }
    }

    @Override
    public void resetPassword(Integer maTk, String matKhauMoi) throws ValidationException, BusinessException {
        validateMatKhau(matKhauMoi);

        Account account = accountRepository.findById(maTk)
                .orElseThrow(() -> new BusinessException("Không tìm thấy tài khoản"));

        account.setMatKhau(PasswordEncoder.encode(matKhauMoi));
        boolean updated = accountRepository.update(account);
        if (!updated) {
            throw new BusinessException("Đặt lại mật khẩu thất bại");
        }
    }

    @Override
    public void lockAccount(Integer maTk) throws BusinessException {
        Account account = accountRepository.findById(maTk)
                .orElseThrow(() -> new BusinessException("Không tìm thấy tài khoản"));

        account.setTrangThai(Account.Status.KHOA);
        boolean updated = accountRepository.update(account);
        if (!updated) {
            throw new BusinessException("Khoá tài khoản thất bại");
        }
    }

    @Override
    public void unlockAccount(Integer maTk) throws BusinessException {
        Account account = accountRepository.findById(maTk)
                .orElseThrow(() -> new BusinessException("Không tìm thấy tài khoản"));

        account.setTrangThai(Account.Status.HOAT_DONG);
        boolean updated = accountRepository.update(account);
        if (!updated) {
            throw new BusinessException("Mở khoá tài khoản thất bại");
        }
    }

    @Override
    public Optional<Account> getByMaNv(String maNv) {
        return accountRepository.findByMaNv(maNv);
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    // ===== Validate helpers =====

    private void validateTenDangNhap(String tenDangNhap) throws ValidationException {
        if (tenDangNhap == null || tenDangNhap.isBlank()) {
            throw new ValidationException("Tên đăng nhập không được để trống");
        }
        if (tenDangNhap.trim().length() < 4) {
            throw new ValidationException("Tên đăng nhập phải có ít nhất 4 ký tự");
        }
    }

    private void validateMatKhau(String matKhau) throws ValidationException {
        if (matKhau == null || matKhau.isBlank()) {
            throw new ValidationException("Mật khẩu không được để trống");
        }
        if (matKhau.length() < MIN_PASSWORD_LENGTH) {
            throw new ValidationException("Mật khẩu phải có ít nhất " + MIN_PASSWORD_LENGTH + " ký tự");
        }
    }
}
