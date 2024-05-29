package com.example.instashare.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instashare.Model.User;
import com.example.instashare.R;
import com.example.instashare.Utils.FirebaseUtils;
import com.example.instashare.Utils.InstaShareUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.MyViewHolder> {
    private List<User> listArray;
    private Context mcontext;
    private String cuid;
    public SearchUserAdapter(Context mcontext, String cuid) {
        this.mcontext = mcontext;
        this.cuid = cuid;
    }
    public void setData (List<User> List){
        this.listArray = List;
        notifyDataSetChanged();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_search_user,parent,false );
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        User user = listArray.get(position);
        holder.name.setText(user.getFullName());
        holder.email.setText(user.getEmail());
        Glide.with(mcontext).load(user.getUri()).circleCrop().into(holder.img);
        String idrequest = InstaShareUtils.createId(cuid, user.getUid());
        holder.id = idrequest;
        checkState(holder, idrequest);
        holder.uid = user.getUid();
    }
    private void checkState(MyViewHolder holder, String idrequest){
        Query query = FirebaseUtils.Instance().getRequestById(idrequest);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String st= dataSnapshot1.child("state").getValue(String.class);
                    holder.setState(Integer.parseInt(st));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //
            }
        });
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView email;
        Button btnState;
        CircleImageView img;
        String uid;
        String id;
        final String[] state = {"+ Kết bạn", "Hủy gửi", "Bạn bè"};

        public MyViewHolder(View itemView) {
            super ( itemView );
            name = itemView.findViewById( R.id.tv_name);
            email = itemView.findViewById(R.id.tv_email);
            btnState = itemView.findViewById(R.id.btn_state);
            img = itemView.findViewById(R.id.img_user);

            btnState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendRequest();
                }
            });
        }
        private void setState(int ind){
            btnState.setText(state[ind].toString());
        }

        private void sendRequest(){
            if(btnState.getText().equals("Bạn bè")){
                return;
            }
            if(btnState.getText().equals("Hủy gửi")){
                setState(1);
                FirebaseUtils.Instance().setStateRequest(id,"0" );
                return;
            }
            FirebaseUtils.Instance().sendRequest(id, cuid, uid);
        }
    }

    @Override
    public int getItemCount() {
        return listArray.size ();
    }
}
