package com.example.instashare.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instashare.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class EmailRegistrationActivity extends AppCompatActivity {
    private EditText edtEmail;
    private Button btnTiepTuc;
    private ImageButton btnBack;
    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_email);
        edtEmail = findViewById(R.id.edtEmailRegister);
        btnTiepTuc = findViewById(R.id.btnTiepTucRegisterEmail);
        btnBack = findViewById(R.id.btnBackRegisterEmail);
        String i_email = getIntent().getStringExtra("email");
        edtEmail.setText(i_email);
        flag = true;

        TextView colorText2 = (TextView)findViewById(R.id.tvchinhsach);
        SpannableString text2 = new SpannableString("Thông qua việc chạm vào nút Tiếp tục, bạn đồng ý với các Điều khoản dịch vụ và Chính sách quyền riêng tư của chúng tôi");
        text2.setSpan(new ForegroundColorSpan(Color.WHITE), 57, 75, 0);
        text2.setSpan(new ForegroundColorSpan(Color.WHITE), 79, 104, 0);
        colorText2.setText(text2, TextView.BufferType.SPANNABLE);

        btnTiepTuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(EmailRegistrationActivity.this, "Vui lòng nhập Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(EmailRegistrationActivity.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                    edtEmail.setFocusable(true);
                    return;
                }
                setEmail(email);
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
        Intent intent = new Intent(EmailRegistrationActivity.this, SplashScreenActivity.class);
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

    private void setEmail(String email) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        Query query = firebaseDatabase.getReference("Users").orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if(flag)
                        Toast.makeText(EmailRegistrationActivity.this, "Email đã có người sử dụng", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(EmailRegistrationActivity.this, PasswordRegistrationActivity.class);
                    intent.putExtra("email", email);
                    flag = false;
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EmailRegistrationActivity.this, "Lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
