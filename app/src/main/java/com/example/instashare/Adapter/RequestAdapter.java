package com.example.instashare.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instashare.Model.User;
import com.example.instashare.R;
import com.example.instashare.Utils.FirebaseUtils;
import com.example.instashare.Utils.InstaShareUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.MyViewHolder> {
    private List<User> listArray;
    private Context mcontext;
    private String cuid;
    public RequestAdapter(Context mcontext, String cuid) {
        this.mcontext = mcontext;
        this.cuid = cuid;
    }
    public void setData (List<User> List){
        this.listArray = List;
        notifyDataSetChanged();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_requests,parent,false );
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        User user = listArray.get(position);
        holder.name.setText(user.getFullName());
        holder.id = InstaShareUtils.createId(user.getUid(), cuid);
        holder.uid = user.getUid();
        Glide.with(mcontext).load(user.getUri()).circleCrop().into(holder.img);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        Button btnState;
        CircleImageView img;
        String id;
        ImageButton btnnot;

        String uid;
        public MyViewHolder(View itemView) {
            super ( itemView );
            name = itemView.findViewById( R.id.tv_name);
            btnState = itemView.findViewById(R.id.btn_accept);
            img = itemView.findViewById(R.id.img_user);
            btnnot = itemView.findViewById(R.id.btn_not_accept);

            btnState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptRequest();
                }
            });
            btnnot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseUtils.Instance().setStateRequest(id, "0");
                }
            });
        }
        private void acceptRequest(){
            FirebaseUtils.Instance().acceptRequest(id);
            FirebaseUtils.Instance().createChatRoom(id, cuid, uid);
        }

    }

    @Override
    public int getItemCount() {
        return listArray.size ();
    }
}
