package com.example.instashare.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instashare.Adapter.FriendAdapter;
import com.example.instashare.Adapter.RequestAdapter;
import com.example.instashare.Adapter.SearchUserAdapter;
import com.example.instashare.Model.Request;
import com.example.instashare.Model.User;
import com.example.instashare.R;
import com.example.instashare.Utils.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends AppCompatActivity {
    private LinearLayout ln_addfriend , ln_list_friends, ln_list_requests;
    private LinearLayout ln_searchuser, ln_list_user;
    private Button btn_addfriend, btn_cancel;
    private EditText edt_input_search;
    private List<User> list_search_users;
    private List<User> list_requests;
    private List<User> list_friends;
    private SearchUserAdapter searchUserAdapter;
    private RequestAdapter requestAdapter;
    private FriendAdapter friendAdapter;
    private RecyclerView rcv_user, rcv_request, rcv_friend;
    private User cuser;
    String cuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        cuser = getIntent().getParcelableExtra("user");
        cuid = cuser.getUid();


        ln_addfriend = findViewById(R.id.ln_addfriend);
        btn_addfriend = findViewById(R.id.btn_addfriend);

        ln_list_friends = findViewById(R.id.ln_list_friends);
        ln_list_requests = findViewById(R.id.ln_list_requests);

        // search user in add friends
        ln_searchuser = findViewById(R.id.ln_searchuser);
        ln_list_user = findViewById(R.id.ln_list_user);
        btn_cancel = findViewById(R.id.btn_cancel);
        edt_input_search = findViewById(R.id.edt_input_search);
        rcv_user = findViewById(R.id.rcv_listuser);

        //
        searchUserAdapter = new SearchUserAdapter(this,cuid);
        list_search_users = new ArrayList<>();
        searchUserAdapter.setData(list_search_users);

        // request
        rcv_request = findViewById(R.id.rcv_requests);
        list_requests = new ArrayList<>();
        requestAdapter = new RequestAdapter(this, cuid);
        requestAdapter.setData(list_requests);

        // friend
        list_friends = new ArrayList<>();
        rcv_friend = findViewById(R.id.rcv_friends);
        friendAdapter = new FriendAdapter(this,cuid);
        friendAdapter.setData(list_friends);
        setup();

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcv_user.setLayoutManager(linearLayoutManager1);

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcv_request.setLayoutManager(linearLayoutManager2);

        LinearLayoutManager linearLayoutManager3= new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcv_friend.setLayoutManager(linearLayoutManager3);

        // click "them ban be"
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

        // click "huy"
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

                String searchTerm = s.toString();
                if(searchTerm.isEmpty() || searchTerm.length()<1){
                    return;
                }
                setupSearchUser(searchTerm.trim());
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
        Query query = FirebaseUtils.Instance().searchUser(searchText);
        query
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list_search_users.clear();
                    rcv_user.setAdapter(searchUserAdapter);
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        User user = ds.getValue(User.class);
                        if(!cuid.equals(user.getUid()) && checkListUser(user.getUid())){
                            Log.i("search_3", String.valueOf(list_search_users.stream().count()));
                            FirebaseUtils.getProfileImage(user, FriendsActivity.this, new FirebaseUtils.ProfileImageCallback() {
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
                        if (request.getIdreceive().equals(cuid) || request.getIdsend().equals(cuid)){
                            setupUser(request,"friend" );
                        }
                    }
                    if(request.getState().equals("1") && request.getIdreceive().equals(cuid)){
                        setupUser(request,"request" );
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setupUser(Request request, String layout){
            User user = new User();
            user.setUid(request.getIdsend());
            if (request.getIdsend().equals(cuid))
                user.setUid(request.getIdreceive());
            FirebaseUtils.getName(user, FriendsActivity.this, new FirebaseUtils.UserNameCallback() {
                @Override
                public void onUserNameLoaded() {
                    // Gọi getProfileImage() khi dữ liệu tên người dùng đã được cập nhật
                    FirebaseUtils.getProfileImage(user, FriendsActivity.this, new FirebaseUtils.ProfileImageCallback() {
                        @Override
                        public void onProfileImageLoaded(Uri uri) {
                            user.setUri(uri.toString());
                            if(layout.equals("friend")){
                                list_friends.add(user);
                                rcv_friend.setAdapter(friendAdapter);
                            }
                            if(layout.equals("request")){
                                list_requests.add(user);
                                rcv_request.setAdapter(requestAdapter);
                            }
                        }
                    });
                }
            });
    }

}