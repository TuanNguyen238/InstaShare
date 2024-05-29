package com.example.instashare.Model;

import android.content.Context;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instashare.Activity.ProfileActivity;
import com.example.instashare.Adapter.FriendAdapter;
import com.example.instashare.Adapter.RequestAdapter;
import com.example.instashare.Adapter.SearchUserAdapter;
import com.example.instashare.R;
import com.example.instashare.Utils.FirebaseUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendBottomSheetDialog extends BottomSheetDialog {
    private Context context;
    private User user;
    private ImageButton imv_search;
    private LinearLayout ln_addfriend;
    private LinearLayout ln_list_friends;
    private LinearLayout ln_list_requests;
    private LinearLayout ln_searchuser;
    private LinearLayout ln_list_user;
    private Button btn_addfriend;
    private Button btn_cancel;
    private EditText edt_input_search;
    private List<User> list_search_users = new ArrayList<>();
    private List<User> list_requests = new ArrayList<>();
    private List<User> list_friends = new ArrayList<>();
    private SearchUserAdapter searchUserAdapter;
    private RequestAdapter requestAdapter;
    private FriendAdapter friendAdapter;
    private RecyclerView rcv_user;
    private RecyclerView rcv_request;
    private RecyclerView rcv_friend;
    public FriendBottomSheetDialog(Context context, User user) {
        super(context);
        this.context = context;
        this.user = user;
        setUpView();
    }

    private void setUpView() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.activity_friends, null);
        setContentView(bottomSheetView);
        getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

        ln_addfriend = bottomSheetView.findViewById(R.id.ln_addfriend);
        ln_list_friends = bottomSheetView.findViewById(R.id.ln_list_friends);
        ln_list_requests = bottomSheetView.findViewById(R.id.ln_list_requests);
        ln_searchuser = bottomSheetView.findViewById(R.id.ln_searchuser);
        ln_list_user= bottomSheetView.findViewById(R.id.ln_list_user);
        btn_addfriend = bottomSheetView.findViewById(R.id.btn_addfriend);
        btn_cancel = bottomSheetView.findViewById(R.id.btn_cancel);
        edt_input_search = bottomSheetView.findViewById(R.id.edt_input_search);
        searchUserAdapter= new SearchUserAdapter(context, user.getUid());
        requestAdapter = new RequestAdapter(context, user.getUid());
        friendAdapter = new FriendAdapter(context, user.getUid());
        rcv_user = bottomSheetView.findViewById(R.id.rcv_listuser);
        rcv_request = bottomSheetView.findViewById(R.id.rcv_requests);
        rcv_friend = bottomSheetView.findViewById(R.id.rcv_friends);
        imv_search = bottomSheetView.findViewById(R.id.imv_search);

        searchUserAdapter.setData(list_search_users);
        requestAdapter.setData(list_requests);
        friendAdapter.setData(list_friends);
        setup();

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        rcv_user.setLayoutManager(linearLayoutManager1);

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        rcv_request.setLayoutManager(linearLayoutManager2);

        LinearLayoutManager linearLayoutManager3= new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        rcv_friend.setLayoutManager(linearLayoutManager3);

        btn_addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ln_addfriend.setVisibility(View.GONE);
                ln_list_friends.setVisibility(View.GONE);
                ln_list_requests.setVisibility(View.GONE);

                ln_searchuser.setVisibility(View.VISIBLE);
                ln_list_user.setVisibility(View.VISIBLE);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ln_addfriend.setVisibility(View.VISIBLE);
                ln_list_friends.setVisibility(View.VISIBLE);
                ln_list_requests.setVisibility(View.VISIBLE);

                ln_searchuser.setVisibility(View.GONE);
                ln_list_user.setVisibility(View.GONE);
                edt_input_search.setText("");
            }
        });

        imv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list_search_users.clear();
                rcv_user.setAdapter(searchUserAdapter);

                String searchTerm = edt_input_search.getText().toString();
                if(searchTerm.isEmpty() || searchTerm.length()<1){
                    return;
                }
                setupSearchUser(searchTerm.trim());
            }
        });

        edt_input_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                list_search_users.clear();
                rcv_user.setAdapter(searchUserAdapter);
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                list_search_users.clear();
                rcv_user.setAdapter(searchUserAdapter);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private boolean checkListUser(String uid){
        for (User user: list_search_users) {
            if(user.getUid().equals(uid))
                return false;
        }
        return true;
    }

    private void setupSearchUser(String searchText){
        FirebaseUtils.Instance().searchUser(searchText)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list_search_users.clear();
                        rcv_user.setAdapter(searchUserAdapter);
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            User user = ds.getValue(User.class);
                            if(!user.equals(user.getUid()) && checkListUser(user.getUid())){
                                FirebaseUtils.getProfileImage(user, context, new FirebaseUtils.ProfileImageCallback() {
                                    @Override
                                    public void onProfileImageLoaded(Uri uri) {
                                        user.setUri(uri.toString());
                                        list_search_users.add(user);
                                        rcv_user.setAdapter(searchUserAdapter);
                                    }
                                });
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void setup(){
        FirebaseUtils.Instance().getAllRequest()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list_friends.clear();
                        rcv_friend.setAdapter(friendAdapter);
                        list_requests.clear();
                        rcv_request.setAdapter(requestAdapter);
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            Request request = ds.getValue(Request.class);
                            if(request.getState().equals("2")){
                                if (request.getIdreceive().equals(user.getUid()) || request.getIdsend().equals(user.getUid())){
                                    setupUser(request,"friend");
                                }
                            }
                            if(request.getState().equals("1") && request.getIdreceive().equals(user.getUid())){
                                setupUser(request,"request");
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void setupUser(Request request, String layout){
        User nuser = new User();
        nuser.setUid(request.getIdsend());
        if (request.getIdsend().equals(user.getUid()))
            nuser.setUid(request.getIdreceive());
        FirebaseUtils.getName(nuser, context, new FirebaseUtils.UserNameCallback() {
            @Override
            public void onUserNameLoaded() {
                // Gọi getProfileImage() khi dữ liệu tên người dùng đã được cập nhật
                FirebaseUtils.getProfileImage(nuser, context, new FirebaseUtils.ProfileImageCallback() {
                    @Override
                    public void onProfileImageLoaded(Uri uri) {
                        nuser.setUri(uri.toString());
                        if(layout.equals("friend")){
                            list_friends.add(nuser);
                            rcv_friend.setAdapter(friendAdapter);
                        }
                        if(layout.equals("request")){
                            list_requests.add(nuser);
                            rcv_request.setAdapter(requestAdapter);
                        }
                    }
                });
            }
        });
    }
}
