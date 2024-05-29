package com.example.instashare.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.instashare.Utils.InstaShareUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class NameRegistrationActivity extends AppCompatActivity {
    private EditText edtTen, edtHo;
    private Button btnRegister;
    private ImageButton btnBack;
    private FirebaseAuth myAuth;
    private FirebaseUser fbUser;
    private User user;
    private String pass, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_name);
        edtTen = findViewById(R.id.edtTenRegister);
        edtHo = findViewById(R.id.edtHoRegister);
        btnRegister = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBackRegisterName);
        pass = getIntent().getStringExtra("pass");
        email = getIntent().getStringExtra("email");
        myAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
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
        Intent i = new Intent(NameRegistrationActivity.this, PasswordRegistrationActivity.class);
        i.putExtra("email", email);
        i.putExtra("pass", pass);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        finish();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        back();
    }
    private void register() {
        myAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    ProgressDialog progressDialog = new ProgressDialog(NameRegistrationActivity.this);
                    progressDialog.setTitle("Đang đăng nhập");
                    progressDialog.setMessage("Vui lòng đợi...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    setUser();
                    addUserInfo();
                    FirebaseUtils.checkState(user.getUid(), new FirebaseUtils.StateCallback() {
                        @Override
                        public void onStateChanged(boolean flag) {
                            Intent mainIntent;
                            if (flag)
                                mainIntent = new Intent(NameRegistrationActivity.this, MainPageActivity.class);
                            else
                                mainIntent = new Intent(NameRegistrationActivity.this, FirstActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            mainIntent.putExtra("user", user);
                            progressDialog.dismiss();
                            startActivity(mainIntent);
                            overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                            finish();
                        }
                    });
                } else {
                    Toast.makeText(NameRegistrationActivity.this, "Lỗi: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Toast.makeText(NameRegistrationActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addUserInfo() {
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("uid", user.getUid());
        hashMap.put("firstName", user.getFirstName());
        hashMap.put("lastName", user.getLastName());
        hashMap.put("email", user.getEmail());
        hashMap.put("password", user.getPassword());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Users");
        reference.child(user.getUid()).setValue(hashMap);
        Toast.makeText(NameRegistrationActivity.this, "Đăng ký thành công: " + email, Toast.LENGTH_LONG).show();
    }

    private void setUser()
    {
        user = new User();
        fbUser = myAuth.getCurrentUser();
        user.setUid(fbUser.getUid());
        user.setFirstName(edtTen.getText().toString());
        user.setLastName(edtHo.getText().toString());
        user.setUri(InstaShareUtils.createUri(NameRegistrationActivity.this).toString());
        user.setPassword(pass);
        user.setEmail(email);
        FirebaseUtils.uploadProfileImage(Uri.parse(user.getUri()));
    }
}
