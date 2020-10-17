package com.example.stchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.UserListViewHolder> {



    private ArrayList<ChatObject> chatList;

    public ChatListAdapter(ArrayList<ChatObject> chatList) {
        this.chatList = chatList;
    }




    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        UserListViewHolder rcv = new UserListViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final UserListViewHolder holder, final int position) {
        holder.mUserPhone.setText(chatList.get(position).getPhoneNumber());
        holder.mUserName.setText(chatList.get(position).getName());

        holder.mChatListLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("chatID", chatList.get(holder.getAdapterPosition()).getChatID());
                bundle.putString("name", chatList.get(holder.getAdapterPosition()).getName());
                bundle.putString("phoneNumber", chatList.get(holder.getAdapterPosition()).getPhoneNumber());

                intent.putExtras(bundle);

                v.getContext().startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class UserListViewHolder extends RecyclerView.ViewHolder {
        TextView mUserName, mUserPhone;
        LinearLayout mChatListLayout;
        public UserListViewHolder(@NonNull View itemView) {
            super(itemView);

            mUserName = itemView.findViewById(R.id.username);
            mUserPhone = itemView.findViewById(R.id.userphone);
            mChatListLayout = itemView.findViewById(R.id.chatListlayout);
        }
    }
}
