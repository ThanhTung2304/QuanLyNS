package org.example.view.util;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Một TableCellRenderer tùy chỉnh để hiển thị giá trị BigDecimal dưới dạng tiền tệ Việt Nam.
 */
public class CurrencyRenderer extends DefaultTableCellRenderer {
    private final NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        // Gọi phương thức của lớp cha để lấy component mặc định
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Nếu giá trị là BigDecimal, định dạng nó
        if (value instanceof BigDecimal) {
            setText(formatter.format(value));
        } else {
            // Nếu không, hiển thị giá trị gốc
            setText((value == null) ? "" : value.toString());
        }
        
        // Căn lề phải cho các con số
        setHorizontalAlignment(SwingConstants.RIGHT);

        return this;
    }
}