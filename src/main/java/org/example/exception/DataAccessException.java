package org.example.exception;

/**
 * Exception ném ra khi có lỗi ở TẦNG DỮ LIỆU
 * (SQLException, mất kết nối DB, vi phạm constraint FK/UNIQUE...).
 *
 * Là RuntimeException (unchecked) vì lỗi hạ tầng/DB thường không thể
 * lường trước ở chỗ gọi, và ta không muốn ép mọi Service/Controller phải
 * khai báo "throws" cho loại lỗi hệ thống này. Repository/Impl bắt
 * SQLException và bọc lại thành DataAccessException để tầng trên không
 * cần biết chi tiết JDBC.
 */
public class DataAccessException extends RuntimeException {

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}