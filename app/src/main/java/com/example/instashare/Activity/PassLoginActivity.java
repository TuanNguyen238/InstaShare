package com.example.instashare.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instashare.Model.User;
import com.example.instashare.R;
import com.example.instashare.Utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class PassLoginActivity extends AppCompatActivity {
    private EditText edtPass;
    private Button btnLogin, btnRecovery;
    private ImageButton btnBack, imbEye;
    private FirebaseAuth myAuth;
    private String email;
    private Boolean flag = true;
    private User user;
    private boolean checkPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_pass);
        edtPass = findViewById(R.id.edtPassLogin);
        myAuth = FirebaseAuth.getInstance();
        btnLogin = findViewById(R.id.btnLogin);
        btnBack = findViewById(R.id.btnBackLoginPass);
        btnRecovery = findViewById(R.id.btnRecoveryPass);
        imbEye = findViewById(R.id.imbEye);
        email = getIntent().getStringExtra("email");
        checkPass = true;

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = edtPass.getText().toString().trim();
                if (pass.length() < 8) {
                    Toast.makeText(PassLoginActivity.this, "Mật khẩu phải tối thiểu 8 kí tự", Toast.LENGTH_SHORT).show();
                    edtPass.setFocusable(true);
                    return;
                }
                loginUser(email, pass);
            }
        });

        imbEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPass){
                    edtPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imbEye.setBackgroundResource(R.drawable.view);
                } else {
                    edtPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imbEye.setBackgroundResource(R.drawable.invisible);
                }
                checkPass = !checkPass;
            }
        });

        // Recover Your Password using email
        btnRecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginRecovery();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
    }

    private void back()
    {
        Intent intent = new Intent(PassLoginActivity.this, EmailLoginActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        finish();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        back();
    }

    private void beginRecovery() {

        // send reset password email
        myAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(PassLoginActivity.this, "Email gửi thành công", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(PassLoginActivity.this, "Email không tồn tại", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PassLoginActivity.this, "Email gửi thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loginUser(String email, String pass) {
        // sign in with email and password after authenticating
        myAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    ProgressDialog progressDialog = new ProgressDialog(PassLoginActivity.this);
                    progressDialog.setTitle("Đang đăng nhập");
                    progressDialog.setMessage("Vui lòng đợi...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    FirebaseUser fbUser = myAuth.getCurrentUser();
                    changePassword(fbUser.getUid(), pass);

                    user = new User();
                    user.setUid(fbUser.getUid());
                    user.setEmail(fbUser.getEmail());
                    user.setPassword(pass);

                    FirebaseUtils.getName(user, PassLoginActivity.this, new FirebaseUtils.UserNameCallback() {
                        @Override
                        public void onUserNameLoaded() {
                            // Gọi getProfileImage() khi dữ liệu tên người dùng đã được cập nhật
                            FirebaseUtils.getProfileImage(user, PassLoginActivity.this, new FirebaseUtils.ProfileImageCallback() {
                                @Override
                                public void onProfileImageLoaded(Uri uri) {
                                    user.setUri(uri.toString());
                                    Toast.makeText(PassLoginActivity.this, "Đăng nhâp thành công: " + user.getEmail(), Toast.LENGTH_LONG).show();

                                    FirebaseUtils.checkState(user.getUid(), new FirebaseUtils.StateCallback() {
                                                @Override
                                                public void onStateChanged(boolean flag) {
                                                    Intent mainIntent;
                                                    if (flag)
                                                        mainIntent = new Intent(PassLoginActivity.this, MainPageActivity.class);
                                                    else
                                                        mainIntent = new Intent(PassLoginActivity.this, FirstActivity.class);
                                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    mainIntent.putExtra("user", user);
                                                    progressDialog.dismiss();
                                                    startActivity(mainIntent);
                                                    overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                                                    finish();
                                                }
                                            });
                                }
                            });
                        }
                    });
                } else {
                    if(flag) {
                        Toast.makeText(PassLoginActivity.this, "Email hoặc mật khẩu không đúng", Toast.LENGTH_LONG).show();
                        flag = false;
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(flag)
                    Toast.makeText(PassLoginActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void changePassword(String uid, String password)
    {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("password", password);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Users");
        reference.child(uid).updateChildren(hashMap);
    }
}
