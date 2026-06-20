package org.example.exception;

/**
 * Exception ném ra khi dữ liệu đầu vào KHÔNG hợp lệ
 * (sai format, thiếu trường bắt buộc, độ dài không đúng...).
 *
 * Đây là checked exception - buộc Controller/Panel phải catch và
 * hiển thị thông báo lỗi cho người dùng (thường qua JOptionPane).
 */
public class ValidationException extends Exception {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}