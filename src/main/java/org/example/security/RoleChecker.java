package org.example.security;

import org.example.entity.Account;
import org.example.exception.BusinessException;

/**
 * Kiểm tra quyền hạn dựa trên vai trò (vai_tro) của tài khoản đang đăng nhập.
 * Thay thế cho cơ chế @PreAuthorize của Spring Security, nhưng làm thủ công
 * vì ứng dụng Swing không có annotation-based security.
 *
 * Dùng để: ẩn/hiện nút trên UI, hoặc chặn hành động ở tầng Controller
 * trước khi gọi Service.
 */
public final class RoleChecker {

    private RoleChecker() {
        // Utility class
    }

    /**
     * Kiểm tra người dùng hiện tại có đúng 1 vai trò cụ thể không.
     */
    public static boolean hasRole(Account.Role role) {
        Account.Role current = SessionManager.getInstance().getCurrentRole();
        return current == role;
    }

    /**
     * Kiểm tra người dùng hiện tại có vai trò thuộc danh sách cho phép không.
     */
    public static boolean hasAnyRole(Account.Role... roles) {
        Account.Role current = SessionManager.getInstance().getCurrentRole();
        if (current == null) {
            return false;
        }
        for (Account.Role role : roles) {
            if (current == role) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAdmin() {
        return hasRole(Account.Role.ADMIN);
    }

    public static boolean isHr() {
        return hasRole(Account.Role.HR);
    }

    public static boolean isTruongPhong() {
        return hasRole(Account.Role.TRUONG_PHONG);
    }

    /**
     * Trưởng phòng hoặc Admin được duyệt đơn nghỉ phép.
     */
    public static boolean canApproveLeave() {
        return hasAnyRole(Account.Role.TRUONG_PHONG, Account.Role.ADMIN);
    }

    /**
     * Admin hoặc HR được quản lý tài khoản, hợp đồng, lương.
     */
    public static boolean canManageHr() {
        return hasAnyRole(Account.Role.ADMIN, Account.Role.HR);
    }

    /**
     * Bắt buộc người dùng hiện tại phải có vai trò chỉ định,
     * nếu không sẽ ném BusinessException để Controller/Panel xử lý.
     */
    public static void requireRole(Account.Role role) throws BusinessException {
        if (!hasRole(role)) {
            throw new BusinessException("Bạn không có quyền thực hiện chức năng này");
        }
    }

    /**
     * Bắt buộc người dùng hiện tại phải có 1 trong các vai trò chỉ định.
     */
    public static void requireAnyRole(Account.Role... roles) throws BusinessException {
        if (!hasAnyRole(roles)) {
            throw new BusinessException("Bạn không có quyền thực hiện chức năng này");
        }
    }

    /**
     * Bắt buộc đã đăng nhập, dùng ở các Panel cần xác thực trước khi mở.
     */
    public static void requireLoggedIn() throws BusinessException {
        if (!SessionManager.getInstance().isLoggedIn()) {
            throw new BusinessException("Vui lòng đăng nhập để tiếp tục");
        }
    }
}