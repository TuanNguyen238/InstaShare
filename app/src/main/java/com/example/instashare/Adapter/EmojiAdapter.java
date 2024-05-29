package com.example.instashare.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instashare.Model.Comment;
import com.example.instashare.Model.User;
import com.example.instashare.R;
import com.example.instashare.Utils.FirebaseUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.MyViewHolder> {
    private List<Comment> listArray;
    private Context mcontext;
//    private String cuid;
    public EmojiAdapter(Context mcontext) {
        this.mcontext = mcontext;
    }
    public void setData(List<Comment> List){
        this.listArray = List;
        notifyDataSetChanged();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_interac,parent,false );
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Comment comment = listArray.get(position);
        User user = new User();
        user.setUid(comment.getSendId());
        FirebaseUtils.getName(user,mcontext, new FirebaseUtils.UserNameCallback() {
            @Override
            public void onUserNameLoaded() {
                // Gọi getProfileImage() khi dữ liệu tên người dùng đã được cập nhật
                FirebaseUtils.getProfileImage(user, mcontext, new FirebaseUtils.ProfileImageCallback() {
                    @Override
                    public void onProfileImageLoaded(Uri uri) {
                        user.setUri(uri.toString());
                        holder.name.setText(user.getFirstName() + " " + user.getLastName());
                        Glide.with(mcontext).load(user.getUri()).circleCrop().into(holder.img);
                    }
                });
            }
        });
        holder.setImgEmoji(comment.getEmoji());
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        ImageView emoji;
        CircleImageView img;
        public MyViewHolder(View itemView) {
            super ( itemView );
            name = itemView.findViewById( R.id.tv_name);
            emoji = itemView.findViewById(R.id.emoji);
            img = itemView.findViewById(R.id.img_user);
        }
        public void setImgEmoji(String e){
            switch(e){
                case "1":
                    emoji.setBackgroundResource(R.drawable.emoji_laughing);
                    break;
                case "2":
                    emoji.setBackgroundResource(R.drawable.emoji_love);
                    break;
                case "3":
                    emoji.setBackgroundResource(R.drawable.emoji_wow);
                    break;
                case "4":
                    emoji.setBackgroundResource(R.drawable.emoji_cry);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return listArray.size ();
    }
}
