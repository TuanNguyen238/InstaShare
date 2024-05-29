package com.example.instashare.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instashare.Model.Message;
import com.example.instashare.R;
import com.example.instashare.Utils.InstaShareUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    private List<Message> listArray;
    private Context mcontext;
    private String cuid;
    private Uri uri;
    public MessageAdapter(Context mcontext, String cuid, Uri uri) {
        this.mcontext = mcontext;
        this.cuid = cuid;
        this.uri = uri;
    }
    public void setData (List<Message> List){
        this.listArray = List;
        notifyDataSetChanged();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent,false );
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Message message = listArray.get(position);
        if(message == null)
        {
            return;
        }
        if (message.getTimestamp().getNanoseconds()==0 && message.getTimestamp().getSeconds()==0 ){
            holder.setTimeText();
        } else{
            holder.timeText.setText(InstaShareUtils.timestampToString(message.getTimestamp()));
        }
        Glide.with(mcontext).load(uri).circleCrop().into(holder.circleImageView);
        if(message.getSenderId().equals(cuid)){
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatTextview.setText(message.getMessage());
        }else{
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.leftChatTextview.setText(message.getMessage());
        }

        try{
            if (!message.getUri().toString().equals("null")){
                holder.relingComment.setVisibility(View.VISIBLE);
                Glide.with(mcontext).load(message.getUri()).into(holder.img);
            } else{
                holder.relingComment.setVisibility(View.GONE);
            }
        } catch (Exception e){

        }

    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftChatLayout,rightChatLayout;
        TextView leftChatTextview,rightChatTextview;
        TextView timeText;
        ImageView img;
        RelativeLayout relingComment;
        CircleImageView circleImageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            timeText = itemView.findViewById(R.id.time);
            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
            img = itemView.findViewById(R.id.imgComment);
            relingComment = itemView.findViewById(R.id.relimgComment);
            circleImageView = itemView.findViewById(R.id.cmvProfileAcc1);
        }
        public void setTimeText() {
            timeText.setVisibility(itemView.GONE);
        }
        public void setImg(){
            relingComment.setVisibility(itemView.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listArray.size ();
    }
}
