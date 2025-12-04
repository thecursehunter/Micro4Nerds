package ueh.edu.vn.md.micro4nerds.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtils {
    public static String formatCurrency(double amount) {
        // Format tiền Việt Nam: 100000 -> 100.000 đ
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(amount);
    }
}