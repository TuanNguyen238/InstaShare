package com.example.instashare.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instashare.Activity.MessageActivity;
import com.example.instashare.Model.Chatroom;
import com.example.instashare.Model.User;
import com.example.instashare.R;
import com.example.instashare.Utils.FirebaseUtils;
import com.example.instashare.Utils.InstaShareUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.MyViewHolder> {
    private List<Chatroom> listArray;
    private List<String> listState;
    private Context mcontext;
    private User cuser;
    public ChatRoomAdapter(Context mcontext, User cuser) {
        this.mcontext = mcontext;
        this.cuser = cuser;
    }
    public void setData (List<Chatroom> List, List<String> listState){
        this.listArray = List;
        this.listState = listState;
        notifyDataSetChanged();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_chatrooms,parent,false );
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Chatroom chatroom = listArray.get(position);
        String message = chatroom.getLastMessage();
        if(message.length()>= 30){
            message = message.substring(0, 30) +"...";
        }
        holder.time.setText(InstaShareUtils.timestampToString(chatroom.getLastMessageTimestamp()));
        holder.message.setText(message);

        User user = new User();
        user.setUid(chatroom.getUid1());
        if (chatroom.getUid1().equals(cuser.getUid()))
            user.setUid(chatroom.getUid2());

        FirebaseUtils.getName(user, mcontext, new FirebaseUtils.UserNameCallback() {
            @Override
            public void onUserNameLoaded() {
                // Gọi getProfileImage() khi dữ liệu tên người dùng đã được cập nhật
                FirebaseUtils.getProfileImage(user, mcontext, new FirebaseUtils.ProfileImageCallback() {
                    @Override
                    public void onProfileImageLoaded(Uri uri) {
                        user.setUri(uri.toString());
                        holder.name.setText(user.getFullName());
                        Glide.with(mcontext).load(user.getUri()).circleCrop().into(holder.img);
                    }
                });
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mcontext, MessageActivity.class);
            intent.putExtra("idchatroom", chatroom.getIdchatroom());
            intent.putExtra("name", user.getFullName());
            intent.putExtra("user", cuser);
            intent.putExtra("uri", user.getUri());

            if(chatroom.getUid1().equals(cuser.getUid())){
                intent.putExtra("uid1", "state1");
                intent.putExtra("uid2", "state2");
            } else{
                intent.putExtra("uid1", "state2");
                intent.putExtra("uid2", "state1");
            }
            mcontext.startActivity(intent);
        });
        holder.setState(listState.get(position));
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView message;
        TextView name;
        TextView time;
        CircleImageView img;
        public MyViewHolder(View itemView) {
            super ( itemView );
            name = itemView.findViewById(R.id.tv_name);
            message = itemView.findViewById( R.id.tv_last_message);
            time = itemView.findViewById(R.id.tv_last_message_time);
            img = itemView.findViewById(R.id.img_user);
        }

        public void setState(String state){
            if(state.equals("1")){
                img.setBackgroundResource(R.drawable.custombuttonupdatestatusseen);
                message.setTextColor(Color.GRAY);
            }
            else{
                img.setBackgroundResource(R.drawable.custombuttonupdatestatus);
                message.setTextColor(Color.WHITE);
            }
        }
    }
    @Override
    public int getItemCount() {
        return listArray.size ();
    }
}
