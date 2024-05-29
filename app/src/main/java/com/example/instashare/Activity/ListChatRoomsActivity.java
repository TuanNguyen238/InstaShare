package com.example.instashare.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instashare.Adapter.ChatRoomAdapter;
import com.example.instashare.Model.Chatroom;
import com.example.instashare.Model.User;
import com.example.instashare.R;
import com.example.instashare.Utils.FirebaseUtils;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListChatRoomsActivity extends AppCompatActivity {
    private RecyclerView rcv_chatroom;
    private ImageButton btnback;
    private List<Chatroom> list_chatroom;
    private List<String> list_state;
    private ChatRoomAdapter chatRoomAdapter;
    private String cuid;
    private User cuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chatrooms);

        cuser = (User) getIntent().getParcelableExtra("user");
        cuid = cuser.getUid();

        btnback = findViewById(R.id.btnBack);
        rcv_chatroom = findViewById(R.id.rcv_chatroom);

        list_chatroom = new ArrayList<>();
        list_state = new ArrayList<>();
        chatRoomAdapter = new ChatRoomAdapter(this, cuser);
        chatRoomAdapter.setData(list_chatroom, list_state);

        setupChatRoom();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcv_chatroom.setLayoutManager(linearLayoutManager);

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
    }

    private void back() {
        Intent intent = new Intent(ListChatRoomsActivity.this, MainPageActivity.class);
        intent.putExtra("user", cuser);
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

    private void setupChatRoom()
    {
        FirebaseUtils.Instance().getAllChatroom()
            .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list_chatroom.clear();
                list_state.clear();
                rcv_chatroom.setAdapter(chatRoomAdapter);
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String uid1 = ds.child("uid1").getValue(String.class);
                    String uid2 = ds.child("uid2").getValue(String.class);
                    if(uid1.equals(cuid) || uid2.equals(cuid)){
                        Chatroom chatroom = new Chatroom(ds);
                        list_chatroom.add(chatroom);
                        if(uid1.equals(cuid))
                            list_state.add(chatroom.getState1());
                        else
                            list_state.add(chatroom.getState2());
                        rcv_chatroom.setAdapter(chatRoomAdapter);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //
            }
        });
    }
}