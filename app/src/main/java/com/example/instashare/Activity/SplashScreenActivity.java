package com.example.instashare.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.instashare.Adapter.SliderAdapter;
import com.example.instashare.Model.Slider;
import com.example.instashare.Model.User;
import com.example.instashare.R;
import com.example.instashare.Utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class SplashScreenActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button btnCreate, btnSignIn;
    private FirebaseUser fbUser;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        imageSlidering();
        mAuth = FirebaseAuth.getInstance();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 1);
        }
        fbUser = mAuth.getCurrentUser();
        if (fbUser != null) {
            autoLogin();
        }
        else
        {
            btnCreate = (Button) findViewById(R.id.btnCreateAccount);
            btnSignIn = (Button) findViewById(R.id.btnSignIn);

            btnCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createAcc();
                }
            });
            btnSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signIn();
                }
            });
        }
    }

    private void imageSlidering() {
        String url1 = "https://i.imgur.com/AzkePG3.png";
        String url2 = "https://i.imgur.com/ixPucsP.png";
        String url3 = "https://i.imgur.com/b5a6Tjl.png";
        ArrayList<com.example.instashare.Model.Slider> sliderDataArrayList = new ArrayList<>();

        SliderView sliderView = findViewById(R.id.slider);

        sliderDataArrayList.add(new Slider(url1));
        sliderDataArrayList.add(new Slider(url2));
        sliderDataArrayList.add(new Slider(url3));

        SliderAdapter adapter = new SliderAdapter(this, sliderDataArrayList);
        sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
        sliderView.setSliderAdapter(adapter);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();
    }

    private void autoLogin()
    {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Đang đăng nhập");
        progressDialog.setMessage("Vui lòng đợi...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        user = new User();
        user.setUid(fbUser.getUid());
        user.setEmail(fbUser.getEmail());

        // Gọi phương thức getName() với callback
        FirebaseUtils.getName(user, SplashScreenActivity.this, new FirebaseUtils.UserNameCallback() {
            @Override
            public void onUserNameLoaded() {
                // Gọi getProfileImage() khi dữ liệu tên người dùng đã được cập nhật
                FirebaseUtils.getProfileImage(user, SplashScreenActivity.this, new FirebaseUtils.ProfileImageCallback() {
                    @Override
                    public void onProfileImageLoaded(Uri uri) {
                        user.setUri(uri.toString());
                        Toast.makeText(SplashScreenActivity.this, "Đăng nhâp thành công: " + user.getEmail(), Toast.LENGTH_LONG).show();

                        FirebaseUtils.checkState(user.getUid(), new FirebaseUtils.StateCallback() {
                            @Override
                            public void onStateChanged(boolean flag) {
                                Intent mainIntent;
                                if (flag)
                                    mainIntent = new Intent(SplashScreenActivity.this, MainPageActivity.class);
                                else
                                    mainIntent = new Intent(SplashScreenActivity.this, FirstActivity.class);
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
    }

    private void createAcc()
    {
        Intent intent = new Intent(SplashScreenActivity.this, EmailRegistrationActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        finish();
    }

    private void signIn()
    {
        Intent intent = new Intent(SplashScreenActivity.this, EmailLoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        finish();
    }
}