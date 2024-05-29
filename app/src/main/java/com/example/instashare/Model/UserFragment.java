package com.example.instashare.Model;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instashare.Activity.Emoji;
import com.example.instashare.Activity.UploadActivity;
import com.example.instashare.Adapter.EmojiAdapter;
import com.example.instashare.R;
import com.example.instashare.Utils.FirebaseUtils;
import com.example.instashare.Utils.InstaShareUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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

public class UserFragment extends Fragment {
    private static final String CLIENT_ID = "3532ece69f254db0a72badd68815c544";
    private static final String REDIRECT_URI = "https://open.spotify.com";
    private SpotifyAppRemote mSpotifyAppRemote;
    private Uri uri;
    private User user;
    private Context context;
    private ImageView imgUser;
    private String time;
    private TextView tvTimeUser, tvPageUser;
    private List<Comment> list_comment;
    private Button btn_interac;
    private Emoji emoji;
    private String state;
    private RecyclerView rcv_user;
    private EmojiAdapter emojiAdapter;
    private boolean check;
    private boolean openList=false;
    private String fileName;
    private Integer state_img;
    private String text_img;
    private List<String> listIdSong = new ArrayList<>();
    private List<String> listNameSong = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
    Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.page_user, container, false);
        imgUser = rootView.findViewById(R.id.imgUser);
        tvTimeUser = rootView.findViewById(R.id.tvTimeUser);
        btn_interac = rootView.findViewById(R.id.btn_interac);
        tvPageUser = rootView.findViewById(R.id.tvPageUser);
        state = "user";
        check = false;

        emoji = new Emoji(container, rootView, context);

        Glide.with(context).load(uri).into(imgUser);
        tvTimeUser.setText(InstaShareUtils.getDistanceTime(time));
        list_comment = new ArrayList<>();

        btn_interac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check)
                    openInterac();
            }
        });

        setupInterac();
        checkState();
        return rootView;
    }
    private void setButton(){
        btn_interac.setText("Có hoạt động");
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
                    if(state_img != 0)
                        tvPageUser.setBackgroundResource(R.drawable.customrectangle);
                    tvPageUser.setText(text_img);
                    if(state_img == 2)
                    {
                        tvPageUser.setText(listNameSong.get(listIdSong.indexOf(text_img)));
                        tvPageUser.setOnClickListener(new View.OnClickListener() {
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

    private void setupInterac(){
        FirebaseUtils.Instance().getIcon(user.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        list_comment.clear();
                        if(openList){
                            rcv_user.setAdapter(emojiAdapter);
                        }
                        for (QueryDocumentSnapshot doc : value) {
                            Comment comment = new Comment(doc);
                            if(comment.getUri().toString().equals(uri.toString())){
                                list_comment.add(comment);
                                emoji.emoji(comment.getEmoji(),state);
                                if(openList){
                                    rcv_user.setAdapter(emojiAdapter);
                                }
                            }
                        }
                        if (list_comment.size() > 0){
                            setButton();
                            check = true;
                        } else{
                            check= false;
                        }
                    }
                });
    }
    private void openInterac(){
        View bottomSheetView = getLayoutInflater().inflate(R.layout.activity_reaction, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        openList=!openList;

        rcv_user = bottomSheetView.findViewById(R.id.rcv_user);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        rcv_user.setLayoutManager(linearLayoutManager1);

        emojiAdapter = new EmojiAdapter(context);
        emojiAdapter.setData(list_comment);
        rcv_user.setAdapter(emojiAdapter);
    }
    public  UserFragment(User user, Context context, Uri uri, String time, String fileName, List<String> listIdSong, List<String> listNameSong)
    {
        this.user = user;
        this.context = context;
        this.uri = uri;
        this.time = time;
        this.fileName = fileName;
        this.listIdSong.addAll(listIdSong);
        this.listNameSong.addAll(listNameSong);
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
