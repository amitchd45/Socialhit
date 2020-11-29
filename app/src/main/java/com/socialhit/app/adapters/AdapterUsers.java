package com.socialhit.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.socialhit.app.R;
import com.socialhit.app.activities.ChatActivity;
import com.socialhit.app.modelsClass.ModelsUser;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyViewHolder> {
    private Context context;
    private List<ModelsUser>userList;
    private Select select;
    public interface Select{
        void onClick(String UID);
    }

    public AdapterUsers(Context context, List<ModelsUser> userList, Select select) {
        this.context = context;
        this.userList = userList;
        this.select = select;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.list_user,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String UID=userList.get(position).getUid();

        Glide.with(context).load(userList.get(position).getImage()).placeholder(R.drawable.ic_user).into(holder.userImage);
        holder.userNameTv.setText(userList.get(position).getName());
        holder.userEmailTv.setText(userList.get(position).getEmail());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select.onClick(UID);
//                Intent intent=new Intent(context, ChatActivity.class);
//                intent.putExtra("hisUid",UID);
//                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView userImage;
        private TextView userNameTv,userEmailTv;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage=itemView.findViewById(R.id.userImage);
            userNameTv=itemView.findViewById(R.id.userNameTv);
            userEmailTv=itemView.findViewById(R.id.userEmailTv);
        }
    }

    public void filterList(List<ModelsUser>list) {
        this.userList = list;
        notifyDataSetChanged();
    }
}
