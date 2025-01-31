package com.example.instashare.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instashare.Model.User;
import com.example.instashare.R;
import com.example.instashare.Utils.FirebaseUtils;
import com.example.instashare.Utils.InstaShareUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.MyViewHolder> {
    private List<User> listArray;
    private Context mcontext;
    private String cuid;
    public FriendAdapter(Context mcontext, String cuid) {
        this.mcontext = mcontext;
        this.cuid = cuid;
    }
    public void setData (List<User> List){
        this.listArray = List;
        notifyDataSetChanged();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_friends,parent,false );
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        User user = listArray.get(position);
        holder.name.setText(user.getFullName());
        Glide.with(mcontext).load(user.getUri()).circleCrop().into(holder.img);
        holder.uid = user.getUid();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        ImageButton btnState;
        CircleImageView img;
        String uid;
        public MyViewHolder(View itemView) {
            super ( itemView );
            name = itemView.findViewById( R.id.tv_name);
            btnState = itemView.findViewById(R.id.btn_delete);
            img = itemView.findViewById(R.id.img_user);
            btnState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete();
                }
            });
        }
        private void delete(){
            AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
            builder.setMessage("Bạn có muốn hủy kết bạn không?")
                    .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Xử lý đăng xuất nếu người dùng chọn Có
                            String idrequest = InstaShareUtils.createId(uid, cuid);
                            FirebaseUtils.Instance().unFriend(idrequest, uid, cuid);
                        }
                    })
                    .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }

    }

    @Override
    public int getItemCount() {
        return listArray.size ();
    }
}
