package ueh.edu.vn.md.micro4nerds.ui.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import ueh.edu.vn.md.micro4nerds.R;
import ueh.edu.vn.md.micro4nerds.data.local.SharedPrefManager;
import ueh.edu.vn.md.micro4nerds.data.model.User;
import ueh.edu.vn.md.micro4nerds.databinding.ActivityProfileBinding;
import ueh.edu.vn.md.micro4nerds.ui.auth.LoginActivity;
import ueh.edu.vn.md.micro4nerds.ui.base.BaseActivity;
import ueh.edu.vn.md.micro4nerds.ui.viewmodel.CartViewModel;
import ueh.edu.vn.md.micro4nerds.utils.ViewUtils;

public class ProfileActivity extends BaseActivity {

    private ActivityProfileBinding binding;
    private CartViewModel cartViewModel;
    private SharedPrefManager sharedPrefManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private ActivityResultLauncher<String> pickImageLauncher;
    private Uri selectedImageUri;
    private ImageView dialogAvatarPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPrefManager = new SharedPrefManager(this);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

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
                selectedImageUri = uri;
                if (dialogAvatarPreview != null) {
                    dialogAvatarPreview.setImageURI(uri);
                }
            }
        });
    }

    private void loadUserProfile() {
        User user = sharedPrefManager.getUser();
        if (user != null) {
            binding.tvEmail.setText(user.getEmail());
            binding.tvName.setText(user.getName() != null ? user.getName() : "User");

            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                Glide.with(this)
                        .load(user.getAvatar())
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .into(binding.imgProfile);
            } else {
                binding.imgProfile.setImageResource(R.mipmap.ic_launcher);
            }
        }
    }

    private void setupMenuListeners() {
        binding.tvEditProfile.setOnClickListener(v -> showEditProfileDialog());

        binding.tvMyOrders.setOnClickListener(v -> {
            startActivity(new Intent(this, OrderHistoryActivity.class));
        });

        binding.tvSettings.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Ánh xạ View
        dialogAvatarPreview = view.findViewById(R.id.imgEditAvatar);
        TextInputEditText edtName = view.findViewById(R.id.edtName);
        TextInputEditText edtPhone = view.findViewById(R.id.edtPhone);     // Mới
        TextInputEditText edtAddress = view.findViewById(R.id.edtAddress); // Mới
        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);

        selectedImageUri = null;

        User currentUser = sharedPrefManager.getUser();
        if (currentUser != null) {
            edtName.setText(currentUser.getName());
            // Hiển thị Phone và Address hiện tại (nếu có)
            edtPhone.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "");
            edtAddress.setText(currentUser.getAddress() != null ? currentUser.getAddress() : "");
            
            if (currentUser.getAvatar() != null && !currentUser.getAvatar().isEmpty()) {
                Glide.with(this)
                        .load(currentUser.getAvatar())
                        .placeholder(R.mipmap.ic_launcher)
                        .into(dialogAvatarPreview);
            }
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

            if (selectedImageUri != null) {
                uploadImageAndSaveProfile(currentUser, newName, newPhone, newAddress, selectedImageUri, dialog, progressBar, btnSave);
            } else {
                updateProfileInFirestore(currentUser, newName, newPhone, newAddress, currentUser.getAvatar(), dialog, progressBar, btnSave);
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void uploadImageAndSaveProfile(User currentUser, String newName, String newPhone, String newAddress, Uri imageUri, AlertDialog dialog, ProgressBar progressBar, Button btnSave) {
        String uid = currentUser.getUid();
        String fileName = "profile_images/" + uid + "_" + System.currentTimeMillis() + ".jpg";
        
        StorageReference storageRef = storage.getReference().child(fileName);

        storageRef.putFile(imageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storageRef.getDownloadUrl();
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String downloadUrl = task.getResult().toString();
                        updateProfileInFirestore(currentUser, newName, newPhone, newAddress, downloadUrl, dialog, progressBar, btnSave);
                    } else {
                        Log.e("Upload", "Upload failed", task.getException());
                        progressBar.setVisibility(View.GONE);
                        btnSave.setEnabled(true);
                        Toast.makeText(this, "Lỗi upload: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateProfileInFirestore(User currentUser, String newName, String newPhone, String newAddress, String avatarUrl, AlertDialog dialog, ProgressBar progressBar, Button btnSave) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", newName);
        updates.put("phone", newPhone);
        updates.put("address", newAddress);
        if (avatarUrl != null) {
            updates.put("avatar", avatarUrl);
        }

        db.collection("users").document(currentUser.getUid())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Cập nhật Local
                    currentUser.setName(newName);
                    currentUser.setPhone(newPhone);
                    currentUser.setAddress(newAddress);
                    if (avatarUrl != null) {
                        currentUser.setAvatar(avatarUrl);
                    }
                    sharedPrefManager.saveUser(currentUser);

                    loadUserProfile();
                    progressBar.setVisibility(View.GONE);
                    dialog.dismiss();
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(this, "Lỗi cập nhật Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void observeViewModel() {
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        cartViewModel.getCartItems().observe(this, cartItems -> {
            ViewUtils.updateCartBadge(binding.bottomNav.cvBadge, binding.bottomNav.tvCartCount, cartItems);
        });
    }
}
