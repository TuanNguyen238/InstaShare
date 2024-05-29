package com.example.instashare.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.instashare.Adapter.ViewPagerAdapter_Upload;
import com.example.instashare.Model.User;
import com.example.instashare.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class UploadActivity extends AppCompatActivity {
    private ImageButton btnBack, btnUpload, imbCaption;
    private ImageView imageView;
    private Uri imageUri;
    private StorageReference storageReference;
    private User user;
    private ViewPager viewPager;
    private LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;
    private static final String CLIENT_ID = "3532ece69f254db0a72badd68815c544";
    private static final String REDIRECT_URI = "https://open.spotify.com";
    private SpotifyAppRemote mSpotifyAppRemote;
    private int state = 0;
    private String textImage = "";
    private String idSong = "";
    private String currentTime = "";
    private ViewPagerAdapter_Upload viewpageradapter;
    private boolean flag = false;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        btnUpload = findViewById(R.id.btnUpload);
        btnBack = findViewById(R.id.btnCloseUpload);
        imageView = findViewById(R.id.imgUpload);
        viewPager = findViewById(R.id.viewpager);
        sliderDotspanel = findViewById(R.id.SliderDots);
        imbCaption = findViewById(R.id.imbCaption);

        // Nhận đường dẫn của ảnh từ Intent
        String imageUriString = getIntent().getStringExtra("imageUri");
        int facing = getIntent().getIntExtra("len", 0);
        user = (User) getIntent().getParcelableExtra("user");
        // Kiểm tra xem đường dẫn có tồn tại không
        if (imageUriString != null) {
            flag = true;
            // Chuyển đổi đường dẫn từ String sang Uri
            imageUri = Uri.parse(imageUriString);

            // Xoay ảnh sang phải 90 độ và hiển thị trong ImageView
            try {
                Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                Bitmap rotatedBitmap = rotateBitmap(originalBitmap, 90);
                if(facing == CameraSelector.LENS_FACING_FRONT)
                    rotatedBitmap = rotateBitmap(originalBitmap, 270);
                imageView.setImageBitmap(rotatedBitmap);
                //imageView.setImageURI(imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        viewpageradapter = new ViewPagerAdapter_Upload(this);

        viewpageradapter.setButtonClickListener(new ViewPagerAdapter_Upload.ButtonClickListener() {
            @Override
            public void onButtonClick(int position) {
                state = position;
                if(state == 2) {
                    showSpotifyDialog();
                }
                else if(state == 3)
                {
                    LocalDateTime localDateTime = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                    currentTime = localDateTime.format(formatter);
                }
            }
        });

        viewPager.setAdapter(viewpageradapter);

        dotscount = viewpageradapter.getCount();
        dots = new ImageView[dotscount];
        for (int i = 0; i < dotscount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);
            sliderDotspanel.addView(dots[i], params);

        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < dotscount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backAccountActivity();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        imbCaption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCaption();
            }
        });
    }

    private void openCaption() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.activity_effect, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.show();

        Button btnTextEffect = bottomSheetView.findViewById(R.id.btnTextEffect);
        Button btnMusicEffect = bottomSheetView.findViewById(R.id.btnMusicEffect);
        Button btnTimeEffect = bottomSheetView.findViewById(R.id.btnTimeEffect);

        btnTextEffect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = 1;
                bottomSheetDialog.dismiss();
            }
        });

        btnMusicEffect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = 2;
                bottomSheetDialog.dismiss();
            }
        });

        btnTimeEffect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = 3;
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                viewPager.setCurrentItem(state);
            }
        });
    }

    private void showSpotifyDialog() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.activity_spotify, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
        idSong = "2CFC7iLurCyDYuXQcfem2l";
        Button btnSpotify1, btnSpotify2, btnSpotify3, btnSpotify4, btnSpotify5,
                btnSpotify6, btnSpotify7, btnSpotify8, btnSpotify9, btnSpotify10;
        btnSpotify1 = bottomSheetView.findViewById(R.id.btnSpotify1);
        btnSpotify2 = bottomSheetView.findViewById(R.id.btnSpotify2);
        btnSpotify3 = bottomSheetView.findViewById(R.id.btnSpotify3);
        btnSpotify4 = bottomSheetView.findViewById(R.id.btnSpotify4);
        btnSpotify5 = bottomSheetView.findViewById(R.id.btnSpotify5);
        btnSpotify6 = bottomSheetView.findViewById(R.id.btnSpotify6);
        btnSpotify7 = bottomSheetView.findViewById(R.id.btnSpotify7);
        btnSpotify8 = bottomSheetView.findViewById(R.id.btnSpotify8);
        btnSpotify9 = bottomSheetView.findViewById(R.id.btnSpotify9);
        btnSpotify10 = bottomSheetView.findViewById(R.id.btnSpotify10);

        btnSpotify1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connection("2CFC7iLurCyDYuXQcfem2l");
            }
        });

        btnSpotify2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connection("1TiZWEsxN85yLJBq56K8mG");
            }
        });

        btnSpotify3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connection("6UfijnZIhfKCZYUofWsqPD");
            }
        });

        btnSpotify4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connection("014DA3BdnmD3kI5pBogH7c");
            }
        });

        btnSpotify5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connection("24XfIDHxpJgi9VxyWoIyfU");
            }
        });

        btnSpotify6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connection("05QUYSOApWLr8oBbpONl7p");
            }
        });

        btnSpotify7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connection("2vLXhqMVYQD3aqfdd1iXsm");
            }
        });

        btnSpotify8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connection("7sKeO4FYQzjMUCnoyTo3dh");
            }
        });

        btnSpotify9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connection("5BOFFL7w8uIMmz7pCSNgvK");
            }
        });

        btnSpotify10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connection("12Hn6I3DfH7bWx60fUtGlR");
            }
        });

    }

    private Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void backAccountActivity()
    {
        Intent i = new Intent(UploadActivity.this, MainPageActivity.class);
        i.putExtra("user", user);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        finish();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        backAccountActivity();
    }

    private void uploadImage() {
        if(flag) {
            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String fileName = dateFormat.format(currentDate) + "_" + user.getUid();
            storageReference = FirebaseStorage.getInstance().getReference().child("Images");

            storageReference.child(fileName).putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(UploadActivity.this, "Đăng ảnh thành công", Toast.LENGTH_SHORT).show();
                            uploadCaption(fileName);
                            backAccountActivity();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UploadActivity.this, "Đăng ảnh thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
        else
            Toast.makeText(UploadActivity.this, "Không tìm thấy ảnh", Toast.LENGTH_SHORT).show();
    }

    private void uploadCaption(String fileName)
    {
        HashMap<Object, String> hashMap = new HashMap<>();
        if(!viewpageradapter.getEditText().getText().toString().isEmpty()) {
            textImage = viewpageradapter.getEditText().getText().toString();
            state = 1;
        }
        if(state == 2)
            textImage = idSong;
        else if(state == 3)
            textImage = currentTime;
        hashMap.put("state", String.valueOf(state));
        hashMap.put("text", textImage);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Caption");
        reference.child(fileName).setValue(hashMap);
    }

    private void connection(String id) {
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        connected(id);
                        idSong = id;

                        if (mSpotifyAppRemote.isConnected()) {
                        } else {
                            Toast.makeText(UploadActivity.this, "Kết nối với Spotify thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    public void onFailure(Throwable throwable) {
                        Toast.makeText(UploadActivity.this, "Spotify không khả dụng", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    private void connected(String idSong) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            mSpotifyAppRemote.getPlayerApi().play("spotify:track:" + idSong);

            mSpotifyAppRemote.getPlayerApi()
                    .subscribeToPlayerState()
                    .setEventCallback(playerState -> {
                        final Track track = playerState.track;
                        if (track != null) {
                            Log.d("MainActivity_TRACK", track.name + " by " + track.artist.name);
                        }
                    });
        } else {
            Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
        }

    }
}
