package org.example.exception;

/**
 * Exception ném ra khi VI PHẠM QUY TẮC NGHIỆP VỤ
 * (vd: sai tài khoản/mật khẩu, tài khoản bị khoá, không đủ ngày phép,
 * không có quyền thực hiện hành động, hợp đồng còn hiệu lực...).
 *
 * Được ném chủ yếu từ tầng Service. Là checked exception vì Controller/Panel
 * luôn cần biết và xử lý các trường hợp này một cách rõ ràng.
 */
public class BusinessException extends Exception {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}