package ueh.edu.vn.md.micro4nerds.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import ueh.edu.vn.md.micro4nerds.data.model.User;

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "Micro4Nerds_Pref";
    private static final String KEY_USER = "key_user";
    
    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    // 1. Lưu User vào máy (Login thành công thì gọi cái này)
    public void saveUser(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Chuyển Object User thành chuỗi JSON để lưu
        String userJson = gson.toJson(user);
        editor.putString(KEY_USER, userJson);
        editor.apply();
    }

    // 2. Lấy User ra (Để hiển thị lên Profile, Header...)
    public User getUser() {
        String userJson = sharedPreferences.getString(KEY_USER, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    // 3. Kiểm tra đã đăng nhập chưa
    public boolean isLoggedIn() {
        return sharedPreferences.getString(KEY_USER, null) != null;
    }

    // 4. Đăng xuất (Xóa data)
    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
