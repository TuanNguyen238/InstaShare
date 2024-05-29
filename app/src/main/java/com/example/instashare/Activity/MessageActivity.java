package com.example.instashare.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instashare.Adapter.MessageAdapter;
import com.example.instashare.Model.Message;
import com.example.instashare.Model.User;
import com.example.instashare.R;
import com.example.instashare.Utils.FirebaseUtils;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessageActivity extends AppCompatActivity{
    private String cuid;
    private User cuser;
    private String idchatroom;
    private ImageButton btn_send, btn_back;
    private TextView input_message;
    private List<Message> list_message;
    private RecyclerView rcv_message;
    private CircleImageView circleImageView;
    private MessageAdapter messageAdapter;
    private TextView tv_name;
    private String uid1, uid2;
    private Uri uri;
    private boolean checkState;
    private String name;
    private LinearLayout lnSendText;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_messages);

        cuser = getIntent().getParcelableExtra("user");
        idchatroom = getIntent().getStringExtra("idchatroom");
        String uri_str = getIntent().getStringExtra("uri");
        if(uri_str != null)
            uri = Uri.parse(uri_str);
        name = getIntent().getStringExtra("name");
        uid1 = getIntent().getStringExtra("uid1");
        uid2 = getIntent().getStringExtra("uid2");
        checkState = true;

        cuid = cuser.getUid();
        input_message = findViewById(R.id.edt_message);
        btn_send = findViewById(R.id.btn_send);
        btn_back = findViewById(R.id.btnBack);
        tv_name = findViewById(R.id.tv_name);
        lnSendText = findViewById(R.id.lnSendText);
        circleImageView = findViewById(R.id.cmvProfileAcc1);
        Glide.with(this).load(uri).circleCrop().into(circleImageView);

        rcv_message = findViewById(R.id.rcv_message);
        list_message = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, cuser.getUid(), uri);
        messageAdapter.setData(list_message);

        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcv_message.setLayoutManager(linearLayoutManager);

        setupListMessage();

        tv_name.setText(name);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = String.valueOf(input_message.getText());
                if(message.isEmpty() || message.length() < 1)
                    return;
                sendMessage(message);
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        back();
    }

    private void back() {
        checkState = false;
        Intent intent = new Intent(MessageActivity.this, ListChatRoomsActivity.class);
        intent.putExtra("user", cuser);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        finish();
    }

    private void sendMessage(String in_message){
        Timestamp timesend = Timestamp.now();
        Message message = new Message(in_message, cuid, timesend, Uri.parse("null"));
        FirebaseUtils.Instance().sendMessage(idchatroom, message);
        FirebaseUtils.Instance().setReadMessage(idchatroom, uid1, "0");
        FirebaseUtils.Instance().setReadMessage(idchatroom, uid2, "0");
        input_message.setText("");
    }
    private void setList_message(@Nullable QuerySnapshot value){
        Date time = new Date();
        for (QueryDocumentSnapshot doc : value) {
            Message message1= new Message(doc);
            Date time2 = message1.getTimestamp().toDate();
            if(list_message.size() > 0){
                Message msg = list_message.get(list_message.size()-1);
                if(!(msg.getTimestamp().getNanoseconds()==0)
                        && !(msg.getTimestamp().getSeconds()==0))   {
                    time = msg.getTimestamp().toDate();
                }
                if(time.getHours()==time2.getHours()
                        &&time.getMinutes()==time2.getMinutes()){
                    message1.setTimestamp(new Timestamp(0, 0));
                }

                if(msg.getUri().toString().equals(message1.getUri().toString())){
                    message1.setUriNull();
                }
            }
            list_message.add(message1);
            rcv_message.setAdapter(messageAdapter);
        }
    }
    private void setupListMessage(){
        FirebaseUtils.Instance().getChats(idchatroom)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if(checkState)
                            FirebaseUtils.Instance().setReadMessage(idchatroom, uid1, "1");
                        list_message.clear();
                        rcv_message.setAdapter(messageAdapter);
                        if (e != null) {
                            return;
                        }
                        setList_message(value);
                    }
                });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                // Kiểm tra xem vị trí chạm có nằm ngoài lnedtextreply không
                if (lnSendText != null && !isTouchInsideView(event.getRawX(), event.getRawY(), lnSendText)) {
                    // Nếu chạm ra ngoài lnedtextreply, ẩn bàn phím mềm
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    // Phương thức kiểm tra xem một điểm cụ thể có nằm trong một View không
    private boolean isTouchInsideView(float x, float y, View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];

        // Xác định kích thước của view
        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();

        // Kiểm tra xem vị trí chạm có nằm trong view không
        return (x >= viewX && x <= (viewX + viewWidth)) && (y >= viewY && y <= (viewY + viewHeight));
    }
}
