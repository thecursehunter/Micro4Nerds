package ueh.edu.vn.md.micro4nerds.ui.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import ueh.edu.vn.md.micro4nerds.R;
import ueh.edu.vn.md.micro4nerds.data.local.SharedPrefManager;
import ueh.edu.vn.md.micro4nerds.data.model.User;
import ueh.edu.vn.md.micro4nerds.databinding.ActivityProfileBinding;
import ueh.edu.vn.md.micro4nerds.ui.auth.LoginActivity;
import ueh.edu.vn.md.micro4nerds.ui.base.BaseActivity;
import ueh.edu.vn.md.micro4nerds.ui.viewmodel.CartViewModel;

public class ProfileActivity extends BaseActivity {

    private ActivityProfileBinding binding;
    private CartViewModel cartViewModel;
    private SharedPrefManager sharedPrefManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    private ActivityResultLauncher<String> pickImageLauncher;
    private String base64Image = null;
    private ImageView dialogAvatarPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPrefManager = new SharedPrefManager(this);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (!sharedPrefManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setupImagePicker();
        loadUserProfile();
        setupMenuListeners();
        observeViewModel();

        binding.btnLogout.setOnClickListener(v -> {
            firebaseAuth.signOut();
            sharedPrefManager.logout();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void setupImagePicker() {
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                processImage(uri);
            }
        });
    }

    private void processImage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            
            // Nén ảnh xuống kích thước nhỏ (ví dụ 300x300) để lưu vào database
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
            byte[] bytes = outputStream.toByteArray();
            
            base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);
            
            if (dialogAvatarPreview != null) {
                dialogAvatarPreview.setImageBitmap(resizedBitmap);
            }
        } catch (Exception e) {
            Log.e("ProfileActivity", "Lỗi xử lý ảnh", e);
            Toast.makeText(this, "Không thể xử lý ảnh này", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserProfile() {
        User user = sharedPrefManager.getUser();
        if (user != null) {
            binding.tvEmail.setText(user.getEmail());
            binding.tvName.setText(user.getName() != null ? user.getName() : "User");

            displayAvatar(user.getAvatar(), binding.imgProfile);
        }
    }

    private void displayAvatar(String avatarData, ImageView imageView) {
        if (avatarData != null && !avatarData.isEmpty()) {
            if (avatarData.startsWith("http")) {
                // Link URL thông thường
                Glide.with(this).load(avatarData).placeholder(R.mipmap.ic_launcher).circleCrop().into(imageView);
            } else {
                // Dạng Base64 (lưu trong database)
                try {
                    byte[] decodedString = Base64.decode(avatarData, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    Glide.with(this).load(decodedByte).placeholder(R.mipmap.ic_launcher).circleCrop().into(imageView);
                } catch (Exception e) {
                    imageView.setImageResource(R.mipmap.ic_launcher);
                }
            }
        } else {
            imageView.setImageResource(R.mipmap.ic_launcher);
        }
    }

    private void setupMenuListeners() {
        binding.tvEditProfile.setOnClickListener(v -> showEditProfileDialog());
        binding.tvMyOrders.setOnClickListener(v -> startActivity(new Intent(this, OrderHistoryActivity.class)));
        binding.tvSettings.setOnClickListener(v -> Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show());
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialogAvatarPreview = view.findViewById(R.id.imgEditAvatar);
        TextInputEditText edtName = view.findViewById(R.id.edtName);
        TextInputEditText edtPhone = view.findViewById(R.id.edtPhone);     
        TextInputEditText edtAddress = view.findViewById(R.id.edtAddress); 
        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);

        base64Image = null; // Reset

        User currentUser = sharedPrefManager.getUser();
        if (currentUser != null) {
            edtName.setText(currentUser.getName());
            edtPhone.setText(currentUser.getPhone());
            edtAddress.setText(currentUser.getAddress());
            displayAvatar(currentUser.getAvatar(), dialogAvatarPreview);
        }

        dialogAvatarPreview.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        btnSave.setOnClickListener(v -> {
            String newName = edtName.getText().toString().trim();
            String newPhone = edtPhone.getText().toString().trim();
            String newAddress = edtAddress.getText().toString().trim();

            if (newName.isEmpty()) {
                edtName.setError("Tên không được để trống");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            btnSave.setEnabled(false);

            updateProfileInFirestore(currentUser, newName, newPhone, newAddress, base64Image != null ? base64Image : currentUser.getAvatar(), dialog, progressBar, btnSave);
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void updateProfileInFirestore(User currentUser, String newName, String newPhone, String newAddress, String avatarData, AlertDialog dialog, ProgressBar progressBar, Button btnSave) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", newName);
        updates.put("phone", newPhone);
        updates.put("address", newAddress);
        updates.put("avatar", avatarData);

        db.collection("users").document(currentUser.getUid())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    currentUser.setName(newName);
                    currentUser.setPhone(newPhone);
                    currentUser.setAddress(newAddress);
                    currentUser.setAvatar(avatarData);
                    sharedPrefManager.saveUser(currentUser);

                    loadUserProfile();
                    progressBar.setVisibility(View.GONE);
                    dialog.dismiss();
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void observeViewModel() {
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        cartViewModel.getCartItems().observe(this, cartItems -> {
            int total = 0;
            if (cartItems != null) {
                for (ueh.edu.vn.md.micro4nerds.data.model.CartItem item : cartItems) total += item.getQuantity();
            }
            updateCartCount(total);
        });
    }
}
