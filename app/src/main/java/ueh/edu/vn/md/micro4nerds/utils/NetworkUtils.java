package ueh.edu.vn.md.micro4nerds.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;

public class NetworkUtils {

    /**
     * Kiểm tra xem thiết bị có đang kết nối internet hay không.
     * @param context Context của ứng dụng.
     * @return true nếu có mạng, false nếu không.
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) return false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                }
            } else {
                // Hỗ trợ các phiên bản Android cũ hơn
                android.net.NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
        }
        return false;
    }
}
