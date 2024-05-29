package com.example.instashare.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instashare.Adapter.WidgetAdapter;
import com.example.instashare.Model.FriendBottomSheetDialog;
import com.example.instashare.Model.Request;
import com.example.instashare.Model.User;
import com.example.instashare.Model.Widget;
import com.example.instashare.R;
import com.example.instashare.Utils.FirebaseUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class WidgetActivity extends AppCompatActivity {
    private RecyclerView rcvAnh;
    private WidgetAdapter widgetAdapter;
    private StorageReference storageRef;
    private StorageReference imagesRef;
    private List<Widget> list;
    private ImageButton imbBackCapture;
    private CircleImageView cmvProfile;
    private User user;
    private List<String> listUid = new ArrayList<>();
    private Map<Date, StorageReference> listStorage;
    private DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private List<Date> keys;
    private Button btnListFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget);

        cmvProfile = findViewById(R.id.cmvProfileAccWid);
        user = (User) getIntent().getParcelableExtra("user");
        Glide.with(WidgetActivity.this).load(user.getUri()).circleCrop().into(cmvProfile);
        imbBackCapture = findViewById(R.id.imbBackCaptureWid);
        btnListFriend = findViewById(R.id.btnListFriendWid);
        storageRef = FirebaseStorage.getInstance().getReference();
        imagesRef = storageRef.child("Images");
        list = new ArrayList<>();
        rcvAnh = findViewById(R.id.rcv_anh);
        listStorage = new TreeMap<>(new Comparator<Date>() {
            @Override
            public int compare(Date o1, Date o2) {
                return o2.compareTo(o1);
            }
        });
        widgetAdapter = new WidgetAdapter(this);
        getListFriend();
        setupCountFriend();

        cmvProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile();
            }
        });

        imbBackCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAccount();
            }
        });

        btnListFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openListFriend();
            }
        });
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
                        btnListFriend.setText(text);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void openListFriend() {
        FriendBottomSheetDialog friendBottomSheetDialog = new FriendBottomSheetDialog(this, user);
        friendBottomSheetDialog.show();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        openAccount();
    }

    private void openAccount() {
        Intent intent = new Intent(WidgetActivity.this, MainPageActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        finish();
    }

    private void openProfile() {
        Intent intent = new Intent(WidgetActivity.this, ProfileActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        finish();
    }

    private void getAdapter() {
        imagesRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            // Lấy đường dẫn URL của ảnh
                            String[] parts = item.getName().split("_");
                            if(listUid.contains(parts[1])) {
                                try {
                                    Date date = format.parse(parts[0]);
                                    listStorage.put(date, item);
                                } catch (ParseException e) {
                                    Log.d("TAG_DATE", e.getMessage());
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        getWidget();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xảy ra lỗi khi lấy danh sách ảnh
                    }
                });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        rcvAnh.setLayoutManager(gridLayoutManager);

        rcvAnh.setLayoutManager(gridLayoutManager);

        widgetAdapter.setOnItemClickListener(new WidgetAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                imageClick(position+1);
            }
        });

        rcvAnh.setAdapter(widgetAdapter);
    }

    private void imageClick(int position) {
        Intent intent = new Intent(WidgetActivity.this, MainPageActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("index", position);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        finish();
    }

    private void getListFriend() {
        FirebaseUtils.Instance().getAllRequest().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Request request = snapshot.getValue(Request.class);
                    if(request.getState().equals("2"))
                    {
                        if(request.getIdreceive().equals(user.getUid()))
                            listUid.add(request.getIdsend());
                        else if(request.getIdsend().equals(user.getUid()))
                            listUid.add(request.getIdreceive());
                    }
                }
                listUid.add(user.getUid());
                getAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra trong quá trình truy vấn cơ sở dữ liệu
                Log.e("DatabaseError", "Mạng không ổn định: " + databaseError.getMessage());
            }
        });
    }


    private void getWidget() {
        keys = new ArrayList<>(listStorage.keySet());
        startFetchingUrls(0);
    }

    private void startFetchingUrls(final int index) {
        if (index >= keys.size())
            return;
        listStorage.get(keys.get(index)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                list.add(new Widget(imageURL));
                widgetAdapter.setData(list);
                startFetchingUrls(index + 1);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Xảy ra lỗi khi lấy đường dẫn ảnh
            }
        });
    }
}