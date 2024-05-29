package com.example.instashare.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.example.instashare.Adapter.VPagerAdapter;
import com.example.instashare.Model.AccountFragment;
import com.example.instashare.Model.FriendBottomSheetDialog;
import com.example.instashare.Model.FriendFragment;
import com.example.instashare.Model.Request;
import com.example.instashare.Model.UserFragment;
import com.example.instashare.Model.User;
import com.example.instashare.Model.VerticalViewPager;
import com.example.instashare.Model.Widget;
import com.example.instashare.R;
import com.example.instashare.Utils.FirebaseUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.units.qual.A;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainPageActivity extends AppCompatActivity {
    private VerticalViewPager verticalViewPager;
    private PagerAdapter pagerAdapter;
    private CircleImageView cmvProfile;
    private LinearLayout lnHeading, iclCustomTakePhoto;
    private User user;
    private StorageReference storageRef;
    private StorageReference imagesRef;
    private Map<Date, StorageReference> listStorage;
    private DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private List<Date> keys;
    private List<String> listUid = new ArrayList<>();
    private List<String> listTemp = new ArrayList<>();
    private List<String> listName = new ArrayList<>();
    private List<String> listIdSong = new ArrayList<>();
    private List<String> listNameSong = new ArrayList<>();
    private Map<String, String> listMap = new HashMap<>();
    private List<Fragment> list;
    private List<Uri> list_uri;
    private List<String> list_delete;
    private List<String> list_name;
    private ImageButton imbBackCapture, imbWidgetCus, imbChat, imbMoreImage;
    private Spinner spinFriend;
    private ArrayAdapter<String> adapterName;
    private boolean flag = false;
    private Button btn_download, btn_delete;
    private int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        iclCustomTakePhoto = findViewById(R.id.icltakephoto);
        iclCustomTakePhoto.setVisibility(View.GONE);
        lnHeading = findViewById(R.id.iclHeading);
        lnHeading.setVisibility(View.GONE);

        cmvProfile = findViewById(R.id.cmvProfileAcc);
        imbBackCapture = findViewById(R.id.imbBackCapture);
        imbWidgetCus = findViewById(R.id.imbWidgetCus);
        imbMoreImage = findViewById(R.id.imbUploadCus);

        imbChat = findViewById(R.id.imbChat);
        spinFriend = findViewById(R.id.spinFriend);

        user = (User) getIntent().getParcelableExtra("user");
        Glide.with(this).load(user.getUri()).circleCrop().into(cmvProfile);

        list = new ArrayList<>();
        list_name = new ArrayList<>();
        list.add(new AccountFragment(user, this));

        list_uri = new ArrayList<>();
        list_delete = new ArrayList<>();

        verticalViewPager = findViewById(R.id.vvpMainPage);
        pagerAdapter = new VPagerAdapter(getSupportFragmentManager(),list);

        imbMoreImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoreImage();
            }
        });

        imbChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChat();
            }
        });
        verticalViewPager.addOnPageChangeListener(new VerticalViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                index=position;
                if (position == 0) {
                    lnHeading.setVisibility(View.GONE);
                    iclCustomTakePhoto.setVisibility(View.GONE);
                } else {
                    lnHeading.setVisibility(View.VISIBLE);
                    iclCustomTakePhoto.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        verticalViewPager.setAdapter(pagerAdapter);

        storageRef = FirebaseStorage.getInstance().getReference();
        imagesRef = storageRef.child("Images");
        listStorage = new TreeMap<>(new Comparator<Date>() {
            @Override
            public int compare(Date o1, Date o2) {
                return o2.compareTo(o1);
            }
        });
        listName.add("Mọi người");
        getListFriend();

        imbBackCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToAccount();
            }
        });

        cmvProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile();
            }
        });
        
        imbWidgetCus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWidget();
            }
        });

        listIdSong.add("2CFC7iLurCyDYuXQcfem2l");
        listIdSong.add("1TiZWEsxN85yLJBq56K8mG");
        listIdSong.add("6UfijnZIhfKCZYUofWsqPD");
        listIdSong.add("014DA3BdnmD3kI5pBogH7c");
        listIdSong.add("24XfIDHxpJgi9VxyWoIyfU");
        listIdSong.add("05QUYSOApWLr8oBbpONl7p");
        listIdSong.add("2vLXhqMVYQD3aqfdd1iXsm");
        listIdSong.add("7sKeO4FYQzjMUCnoyTo3dh");
        listIdSong.add("5BOFFL7w8uIMmz7pCSNgvK");
        listIdSong.add("12Hn6I3DfH7bWx60fUtGlR");
        listNameSong.add("Thủy triều - Quang Hùng MasterD");
        listNameSong.add("Like i do - J.Tajor");
        listNameSong.add("Adventure Time 2 - G Sounds");
        listNameSong.add("Cứ chill thôi - Chillies");
        listNameSong.add("Sắp vào đông - Juky San");
        listNameSong.add("24/7 - Elijah Woods");
        listNameSong.add("Moshi moshi - Nozomi Kitay");
        listNameSong.add("Hạ còn vương nắn - DatKaa");
        listNameSong.add("Hông về tình iu - Khoi Vu");
        listNameSong.add("Có sao cũng đành - DatKaa");
    }

    private void downloadImage() throws IOException {
        if(index==0)
            return;

    }

    private void deleteImage(){
        if(index==0)
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn có muốn xóa ảnh này không?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Xử lý đăng xuất nếu người dùng chọn Có
                        if(list_delete.get(index-1).equals("user")){
                            FirebaseUtils.Instance().deleteImage(user.getUid(), list_name.get(index-1));
                            verticalViewPager.setCurrentItem(0);

                        }
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
    private void openMoreImage() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_more_image, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);
//        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.show();

        btn_download = bottomSheetView.findViewById(R.id.btn_download);
        btn_delete = bottomSheetDialog.findViewById(R.id.btn_delete);

        if(index>0){
            if(list_delete.get(index-1).equals("friend")){
                btn_delete.setVisibility(View.GONE);
            } else {
                btn_delete.setVisibility(View.VISIBLE);
            }
        }

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    downloadImage();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImage();

            }
        });

    }

    private void openChat() {
        Intent intent = new Intent(MainPageActivity.this, ListChatRoomsActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        finish();
    }

    private void openWidget() {
        Intent intent = new Intent(MainPageActivity.this, WidgetActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        finish();
    }

    private void openProfile() {
        Intent intent = new Intent(MainPageActivity.this, ProfileActivity.class);
        if(user == null)
        {
            Toast.makeText(this, "Mạng không ổn định", Toast.LENGTH_SHORT).show();
            return;
        }
        intent.putExtra("user", user);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        finish();
    }

    private void backToAccount() {
        verticalViewPager.setCurrentItem(0);
    }

    @Override
    public void onBackPressed()
    {
        int currentFragmentIndex = verticalViewPager.getCurrentItem();
        if (currentFragmentIndex == 0) {
            super.onBackPressed();
            finish();
        } else {
            verticalViewPager.setCurrentItem(0);
        }
    }

    private void getListFriend() {
        FirebaseUtils.Instance().getAllRequest()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listUid.clear();
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            Request request = ds.getValue(Request.class);
                            if(request.getState().equals("2")){
                                if (request.getIdreceive().equals(user.getUid())){
                                    listUid.add(request.getIdsend());
                                }
                                if (request.getIdsend().equals(user.getUid())){
                                    listUid.add(request.getIdreceive());
                                }
                            }
                        }

                        listUid.add(user.getUid());
                        listTemp.addAll(listUid);
                        createUserName();
                        getAdapter();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void createUserName() {
        for (String uid: listUid)
        {
            User nuser = new User();
            nuser.setUid(uid);
            FirebaseUtils.getName(nuser, MainPageActivity.this, new FirebaseUtils.UserNameCallback() {
                @Override
                public void onUserNameLoaded() {
                    listName.add(nuser.getFullName());
                    listMap.put(listName.get(listName.size()-1), uid);
                }
            });
        }

        adapterName = new ArrayAdapter<String>(this, R.layout.spinner_dropdown, listName);
        adapterName.setDropDownViewResource(R.layout.colorspinnerlayout);
        spinFriend.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!flag) {
                    flag = true;
                    return;
                }
                listUid.clear();
                listStorage.clear();
                while (list.size() > 1) {
                    list.remove(list.size() - 1);
                    pagerAdapter.notifyDataSetChanged();
                }
                if(position != 0) {
                    String name = spinFriend.getSelectedItem().toString();
                    listUid.add(listMap.get(name));
                }
                else if(position == 0)
                    listUid.addAll(listTemp);
                verticalViewPager.setCurrentItem(0);
                getAdapter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinFriend.setAdapter(adapterName);
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

                        getImage();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xảy ra lỗi khi lấy danh sách ảnh
                    }
                });
    }

    private void getImage() {
        keys = new ArrayList<>(listStorage.keySet());
        startFetchingUrls(0);
    }

    private void startFetchingUrls(final int index) {
        if (index >= keys.size()) {
            verticalViewPager.setAdapter(pagerAdapter);
            Integer position = getIntent().getIntExtra("index", 0);
            verticalViewPager.setCurrentItem(position);
            return;
        }
        listStorage.get(keys.get(index)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String fileName = listStorage.get(keys.get(index)).getName();
                String[] parts = fileName.split("_");
                String uid = parts[1];
                String time = parts[0];
                list_uri.add(uri);
                list_name.add(fileName);
                if(uid.equals(user.getUid())){
                    list_delete.add("user");
                    list.add(new UserFragment(user, MainPageActivity.this, uri, time, fileName, listIdSong, listNameSong));
                }
                else{
                    list_delete.add("friend");
                    list.add(new FriendFragment(user, MainPageActivity.this, uri, uid, time, fileName, listIdSong, listNameSong));
                }
                pagerAdapter.notifyDataSetChanged();
                startFetchingUrls(index+1);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Xảy ra lỗi khi lấy đường dẫn ảnh
            }
        });
    }
}
