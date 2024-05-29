package com.example.instashare.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.instashare.R;

public class PasswordRegistrationActivity extends AppCompatActivity {
    private EditText edtPass;
    private Button btnTiepTuc;
    private ImageButton btnBack;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        edtPass = findViewById(R.id.edtPasswordRegister);
        btnTiepTuc = findViewById(R.id.btnTiepTucRegisterPass);
        btnBack = findViewById(R.id.btnBackRegisterPass);
        email = getIntent().getStringExtra("email");
        String i_pass = getIntent().getStringExtra("pass");
        edtPass.setText(i_pass);


        TextView colorText2 = (TextView)findViewById(R.id.tvchinhsach);
        SpannableString text2 = new SpannableString("Mật khẩu phải có ít nhất 8 kí tự");
        text2.setSpan(new ForegroundColorSpan(Color.WHITE), 24, text2.length(), 0);
        colorText2.setText(text2, TextView.BufferType.SPANNABLE);

        btnTiepTuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = edtPass.getText().toString().trim();
                if(TextUtils.isEmpty(pass))
                {
                    Toast.makeText(PasswordRegistrationActivity.this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pass.length() < 8) {
                    Toast.makeText(PasswordRegistrationActivity.this, "Mật khẩu phải tối thiểu 8 kí tự", Toast.LENGTH_SHORT).show();
                    edtPass.setFocusable(true);
                    return;
                }
                setPass(pass);
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
        Intent intent = new Intent(PasswordRegistrationActivity.this, EmailRegistrationActivity.class);
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

    private void setPass(String pass) {
        Intent intent = new Intent(PasswordRegistrationActivity.this, NameRegistrationActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("pass", pass);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        finish();
    }
}
