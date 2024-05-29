package com.example.instashare.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.instashare.Model.User;
import com.example.instashare.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class FirstActivity extends AppCompatActivity {
    private ImageButton imbCapture, imbWidget, imChat;
    CircleImageView cmvProfile;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insta_first);
        imbCapture = findViewById(R.id.imbBackCapture);
        imbWidget = findViewById(R.id.imbWidgetCus);
        cmvProfile = findViewById(R.id.cmvProfileAcc);
        imChat = findViewById(R.id.imbChat);
        user = getIntent().getParcelableExtra("user");
        getUri();

        imbCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAccount();
            }
        });

        cmvProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile();
            }
        });

        imbWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWidget();
            }
        });
        imChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChat();
            }
        });
    }
    private void openChat() {
        Intent intent = new Intent(FirstActivity.this, ListChatRoomsActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        finish();
    }

    private void getUri()
    {
        Glide.with(FirstActivity.this).load(user.getUri()).circleCrop().into(cmvProfile);
    }

    private void openWidget() {
        Intent intent = new Intent(FirstActivity.this, WidgetActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        finish();
    }

    private void openProfile() {
        Intent intent = new Intent(FirstActivity.this, ProfileActivity.class);
        if(user == null)
        {
            Toast.makeText(FirstActivity.this, "Mạng không ổn định", Toast.LENGTH_SHORT).show();
            return;
        }
        intent.putExtra("user", user);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        finish();
    }

    private void openAccount() {
        Intent intent = new Intent(FirstActivity.this, MainPageActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        finish();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }
}
