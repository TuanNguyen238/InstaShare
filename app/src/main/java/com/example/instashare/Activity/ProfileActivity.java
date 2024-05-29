package com.example.instashare.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instashare.Adapter.FriendAdapter;
import com.example.instashare.Adapter.RequestAdapter;
import com.example.instashare.Adapter.SearchUserAdapter;
import com.example.instashare.Model.FriendBottomSheetDialog;
import com.example.instashare.Model.Request;
import com.example.instashare.Model.User;
import com.example.instashare.R;
import com.example.instashare.Utils.FirebaseUtils;
import com.example.instashare.Utils.InstaShareUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private Button btnSignOut, btnFixPI, btnFixName, btnTiktok, btnInsta, btnFriend;
    private ImageButton imbBackToAccount;
    private FirebaseAuth myAuth;
    private CircleImageView cmvProfile;
    private TextView tvName;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        btnSignOut = findViewById(R.id.btnSignOut);
        btnFixPI = findViewById(R.id.btnFixPI);
        btnFixName = findViewById(R.id.btnFixName);
        btnTiktok = findViewById(R.id.btnTiktok);
        btnInsta = findViewById(R.id.btnInsta);
        btnFriend = findViewById(R.id.btn_friend);

        imbBackToAccount = findViewById(R.id.imbBackToAccount);
        cmvProfile = findViewById(R.id.cmvProfile);
        tvName = findViewById(R.id.tvName);
        myAuth = FirebaseAuth.getInstance();
        user = getIntent().getParcelableExtra("user");
        Glide.with(ProfileActivity.this).load(user.getUri()).circleCrop().into(cmvProfile);
        tvName.setText(user.getFullName());

        btnFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showlistfriend();
            }
        });
        cmvProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfileImageBottomSheetDialog();
            }
        });
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmationDialog();
            }
        });

        imbBackToAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToAccount();
            }
        });

        btnFixPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfileImageBottomSheetDialog();
            }
        });

        btnFixName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfileNameBottomSheetDialog();
            }
        });

        btnTiktok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTiktok();
            }
        });

        btnInsta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInsta();
            }
        });
        setupCountFriend();
    }

    private void setupCountFriend(){
        FirebaseUtils.Instance().getAllRequest()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int count = 0;
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            Request request = ds.getValue(Request.class);
                            if(request.getState().equals("2")){
                                if (request.getIdreceive().equals(user.getUid()) || request.getIdsend().equals(user.getUid())){
                                    count = count + 1;
                                }
                            }
                        }
                        String text = count + " bạn bè";
                        btnFriend.setText(text);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
    private void openInsta() {
        Uri web = Uri.parse("https://www.instagram.com/instasharecamera");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(web);
        startActivity(intent);
    }

    private void showlistfriend(){

        FriendBottomSheetDialog friendBottomSheetDialog = new FriendBottomSheetDialog(this, user);
        friendBottomSheetDialog.show();
    }

    private void openTiktok() {
        Uri web = Uri.parse("https://www.tiktok.com/@instashare20");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(web);
        startActivity(intent);
    }

    private void showProfileNameBottomSheetDialog() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.activity_profile_name, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.show();

        EditText edtTenProfile = bottomSheetView.findViewById(R.id.edtTenProfile);
        EditText edtHoProfile =  bottomSheetView.findViewById(R.id.edtHoProfile);
        edtHoProfile.setText(user.getLastName());
        edtTenProfile.setText(user.getFirstName());
        Button btnConfPN = bottomSheetView.findViewById(R.id.btnSave);

        btnConfPN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setLastName(edtHoProfile.getText().toString());
                user.setFirstName(edtTenProfile.getText().toString());
                changeProfileName();
                tvName.setText(user.getFullName());
                bottomSheetDialog.dismiss();
            }
        });
    }

    private void changeProfileName() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("firstName", user.getFirstName());
        hashMap.put("lastName", user.getLastName());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Users");
        reference.child(user.getUid()).updateChildren(hashMap);
    }

    private void showProfileImageBottomSheetDialog() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.activity_profile_image, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
        Button btnCancel = bottomSheetView.findViewById(R.id.btnCancelPI);
        Button btnGallery = bottomSheetView.findViewById(R.id.btnGalleryPI);
        Button btnTake = bottomSheetView.findViewById(R.id.btnTakePI);
        Button btnDelete = bottomSheetView.findViewById(R.id.btnDeletePI);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalleryImage();
                bottomSheetDialog.dismiss();
            }
        });

        btnTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
                bottomSheetDialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeletePIDialog();
                bottomSheetDialog.dismiss();
            }
        });
    }

    private void showDeletePIDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn có chắc chắn muốn xóa ảnh hồ sơ hay không?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePI();
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void deletePI() {
        user.setUri(InstaShareUtils.createUri(ProfileActivity.this).toString());
        Toast.makeText(ProfileActivity.this, "Xóa ảnh thành công", Toast.LENGTH_SHORT).show();
        FirebaseUtils.uploadProfileImage(Uri.parse(user.getUri()));
        Glide.with(ProfileActivity.this).load(user.getUri()).circleCrop().into(cmvProfile);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 101);
    }

    private void openGalleryImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK) {
            user.setUri(data.getData().toString());
            if(user.getUri() == null)
            {
                Toast.makeText(ProfileActivity.this, "Lỗi ảnh", Toast.LENGTH_SHORT).show();
                return;
            }
            FirebaseUtils.uploadProfileImage(Uri.parse(user.getUri()));
            Toast.makeText(ProfileActivity.this, "Sửa ảnh thành công", Toast.LENGTH_SHORT).show();
            Glide.with(ProfileActivity.this).load(user.getUri()).circleCrop().into(cmvProfile);
        }
        else if(requestCode == 101 && resultCode == RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            user.setUri(getImageUri(getApplicationContext(), photo).toString());
            FirebaseUtils.uploadProfileImage(Uri.parse(user.getUri()));
            Toast.makeText(ProfileActivity.this, "Sửa ảnh thành công", Toast.LENGTH_SHORT).show();
            Glide.with(ProfileActivity.this).load(user.getUri()).circleCrop().into(cmvProfile);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void backToAccount() {
        Intent intent = new Intent(ProfileActivity.this, MainPageActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        finish();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        backToAccount();
    }

    private void signOut() {
        myAuth.signOut();
        Intent intent = new Intent(ProfileActivity.this, SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn có muốn đăng xuất không?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Xử lý đăng xuất nếu người dùng chọn Có
                        signOut();
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Đóng Dialog nếu người dùng chọn Không
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
