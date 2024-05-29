package com.example.instashare.Model;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.instashare.R;
import com.example.instashare.Utils.FirebaseUtils;
import com.example.instashare.Utils.InstaShareUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;
import java.util.List;

public class FriendFragment extends Fragment {
    private static final String CLIENT_ID = "3532ece69f254db0a72badd68815c544";
    private static final String REDIRECT_URI = "https://open.spotify.com";
    private SpotifyAppRemote mSpotifyAppRemote;
    private Uri uri;
    private User user, friend_user;
    private String uid, firstName, lastName, time;
    private Context context;
    private ImageView imgFriend;
    private TextView tvFriendName, tvTimeFriend, tvPageFriend;
    private ImageView  btn_cry, btn_wow, btn_laughing, btn_love;
    private ViewGroup container;
    private Button btnbinhluan;
    private ViewGroup rootView;
    private String fileName;
    private Integer state_img;
    private String text_img;
    private List<String> listIdSong = new ArrayList<>();
    private List<String> listNameSong = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
    Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.page_friend, container, false);
        imgFriend = rootView.findViewById(R.id.imgFriend);
        tvFriendName = rootView.findViewById(R.id.tvFriendName);
        tvTimeFriend = rootView.findViewById(R.id.tvTimeFriend);

        btn_cry = rootView.findViewById(R.id.emoji_cry);
        btn_laughing = rootView.findViewById(R.id.emoji_laughing);
        btn_love = rootView.findViewById(R.id.emoji_love);
        btn_wow = rootView.findViewById(R.id.emoji_wow);
        btnbinhluan = rootView.findViewById(R.id.btnComment);
        tvPageFriend = rootView.findViewById(R.id.tvPageFriend);

        Glide.with(context).load(this.uri).into(imgFriend);
        getName();

        tvTimeFriend.setText(InstaShareUtils.getDistanceTime(time));

        btn_wow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                emoji("3");
            }
        });
        btn_cry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                emoji("4");
            }
        });
        btn_laughing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                emoji("1");
            }
        });
        btn_love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                emoji("2");
            }
        });

        btnbinhluan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentDialog();
            }
        });

        checkState();

        return rootView;
    }

    private void showCommentDialog() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.activity_comment, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        EditText edtReply = bottomSheetView.findViewById(R.id.edtReply);
        ImageButton imbReply = bottomSheetDialog.findViewById(R.id.imbReply);

        edtReply.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty())
                    imbReply.setVisibility(View.GONE);
                else
                    imbReply.setVisibility(View.VISIBLE);
            }
        });
        imbReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment(edtReply.getText().toString().trim());
                edtReply.setText("");
                bottomSheetView.setVisibility(View.GONE);
            }
        });
    }

    private void checkState() {
        FirebaseUtils.Instance().getAllCaption().child(fileName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Kiểm tra xem có dữ liệu tồn tại hay không
                if (dataSnapshot.exists()) {
                    try {
                        state_img = Integer.parseInt(dataSnapshot.child("state").getValue(String.class));
                    }catch (Exception e)
                    {
                        state_img = dataSnapshot.child("state").getValue(Long.class).intValue();
                    }
                    text_img = dataSnapshot.child("text").getValue(String.class);
                    tvPageFriend.setText(text_img);
                    if(state_img != 0)
                        tvPageFriend.setBackgroundResource(R.drawable.customrectangle);
                    if(state_img == 2)
                    {
                        tvPageFriend.setText(listNameSong.get(listIdSong.indexOf(text_img)));
                        tvPageFriend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                connection(text_img);
                            }
                        });
                    }
                } else {
                    Toast.makeText(context, "Dữ liệu không tồn tại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "Lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void comment(String input){
        String idchatroom = InstaShareUtils.createId(user.getUid(), friend_user.getUid());
        Message newMessage = new Message(input, user.getUid(), Timestamp.now(), uri);
        FirebaseUtils.Instance().getChats(idchatroom)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        for (QueryDocumentSnapshot doc : value) {
                            Message message1= new Message(doc);
                            if(message1.getUri()==uri){
                                newMessage.setUri(null);
                                break;
                            }
                        }
                    }
                });
        if(newMessage!= null)
            FirebaseUtils.Instance().sendMessage(idchatroom, newMessage);
    }
    public FriendFragment(User user, Context context, Uri uri, String uid, String time, String fileName, List<String> listIdSong, List<String> listNameSong)
    {
        this.user = user;
        this.context = context;
        this.uri = uri;
        this.uid = uid;
        this.time = time;
        this.fileName = fileName;
        this.listIdSong.addAll(listIdSong);
        this.listNameSong.addAll(listNameSong);
    }

    private void getName()
    {
        friend_user = new User();
        friend_user.setUid(uid);
        FirebaseUtils.getName(friend_user, context, new FirebaseUtils.UserNameCallback() {
            @Override
            public void onUserNameLoaded() {
                firstName = friend_user.getFirstName();
                lastName = friend_user.getLastName();
                tvFriendName.setText(firstName + " " + lastName);
            }
        });
    }

    public void flyEmoji(final int resId) {
        ZeroGravityAnimation animation = new ZeroGravityAnimation();
        animation.setCount(1);
        animation.setScalingFactor(2.0f);
        animation.setOriginationDirection(Direction.BOTTOM);
        animation.setDestinationDirection(Direction.TOP);
        animation.setImage(resId);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        container = rootView.findViewById(R.id.animation_holder);
        animation.play((Activity) context,container);
    }
    public void emoji(String e){
        Comment comment = new Comment("0",Timestamp.now(), e, uri,  user.getUid());
        FirebaseUtils.Instance().sendIcon(friend_user.getUid(),comment );
        for (int i = 0; i < 15; i++) {
            switch(e){
                case "1":
                    flyEmoji(R.drawable.emoji_laughing);
                    break;
                case "2":
                    flyEmoji(R.drawable.emoji_love);
                    break;
                case "3":
                    flyEmoji(R.drawable.emoji_wow);
                    break;
                case "4":
                    flyEmoji(R.drawable.emoji_cry);
                    break;
            }
        }
    }

    private void connection(String id) {
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(context, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        connected(id);

                        if (mSpotifyAppRemote.isConnected()) {
                        } else {
                            Toast.makeText(context, "Kết nối với Spotify thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    public void onFailure(Throwable throwable) {
                        Toast.makeText(context, "Spotify không khả dụng", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void connected(String idSong) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
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
            Toast.makeText(context, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
        }

    }
}
