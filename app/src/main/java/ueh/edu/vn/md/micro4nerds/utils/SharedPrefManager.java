package ueh.edu.vn.md.micro4nerds.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseUser;

public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "micro4nerds_shared_pref";
    private static final String KEY_USER_ID = "key_user_id";
    private static final String KEY_USER_EMAIL = "key_user_email";
    private static final String KEY_USER_NAME = "key_user_name";
    private static final String KEY_USER_AVATAR = "key_user_avatar";
    private static final String KEY_IS_LOGGED_IN = "key_is_logged_in";

    private static SharedPrefManager mInstance;
    private final SharedPreferences sharedPreferences;

    private SharedPrefManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    /**
     * Save user data to SharedPreferences after login.
     * @param user The FirebaseUser object containing user info.
     */
    public void userLogin(FirebaseUser user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, user.getUid());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_NAME, user.getDisplayName());
        if (user.getPhotoUrl() != null) {
            editor.putString(KEY_USER_AVATAR, user.getPhotoUrl().toString());
        }
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    /**
     * Check if the user is logged in.
     * @return true if logged in, false otherwise.
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Get user's email.
     * @return User's email, or null if not found.
     */
    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    /**
     * Get user's display name.
     * @return User's name, or null if not found.
     */
    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "User");
    }

    /**
     * Get user's avatar URL.
     * @return URL of the avatar as a String, or null if not found.
     */
    public String getUserAvatar() {
        return sharedPreferences.getString(KEY_USER_AVATAR, null);
    }

    /**
     * Clear all data from SharedPreferences and log the user out.
     */
    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
