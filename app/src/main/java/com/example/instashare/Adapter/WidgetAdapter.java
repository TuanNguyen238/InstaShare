package com.example.instashare.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instashare.Model.Widget;
import com.example.instashare.R;

import java.util.List;

public class WidgetAdapter extends RecyclerView.Adapter<WidgetAdapter.ListImageViewHolder>{
    private Context mcontext;
    private List<Widget> listWidget;
    private OnItemClickListener mListener;
    private int index;
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public WidgetAdapter(Context mcontext) {
        this.mcontext = mcontext;
    }

    public void setData(List<Widget> list){
        this.listWidget = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_widget,parent, false);

        return new ListImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListImageViewHolder holder, int position) {
        Widget widget = listWidget.get(position);
        if (widget == null){
            return;
        }

        Glide.with(mcontext).load(Uri.parse(widget.getImageURL())).into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedPosition = holder.getAdapterPosition();
                if (clickedPosition != RecyclerView.NO_POSITION && mListener != null) {
                    mListener.onItemClick(clickedPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (listWidget != null)
        {
            return listWidget.size();
        }
        return 0;
    }

    public class ListImageViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        public ListImageViewHolder(@NonNull View itemView)
        {
            super(itemView);
            imageView = itemView.findViewById(R.id.imganh);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}