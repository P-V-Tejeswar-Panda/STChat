package com.example.stchat;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    public ChatAdapter(ArrayList<ChatModel> mChatList) {
        this.mChatList = mChatList;
    }

    private ArrayList<ChatModel> mChatList;
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout, null, false);

        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        ChatViewHolder rcv = new ChatViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatViewHolder holder, int position) {

        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        lp1.weight = 0.4f;
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        lp2.weight = 0.4f;

        //holder.mMessageSender.setText(mChatList.get(position).sender);
        if(mChatList.get(position).sender.equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString())){
            holder.mMessageText.setBackgroundResource(R.drawable.chat_back_from);
            holder.mLeft.setLayoutParams(lp1);
            lp2.weight =0f;
            holder.mRight.setLayoutParams(lp2);

        }
        else{
            holder.mMessageText.setBackgroundResource(R.drawable.chat_back_to);
            lp1.weight = 0f;
            holder.mLeft.setLayoutParams(lp1);
            holder.mRight.setLayoutParams(lp2);

        }
        holder.mMessageText.setText(mChatList.get(position).text);

       /* if(mChatList.get(holder.getAdapterPosition()).getMediaUrlList().isEmpty()){
            holder.mViewMedia.setVisibility(View.GONE);
        }*/

        holder.mViewMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageViewer.Builder(v.getContext(), mChatList.get(holder.getAdapterPosition()).getMediaUrlList())
                        .setStartPosition(0)
                        .show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView mMessageText, mMessageSender;

        Space mLeft, mRight;

        Button mViewMedia;

        LinearLayout mMessageListCard;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            mMessageText = itemView.findViewById(R.id.messageText);
            mMessageSender = itemView.findViewById(R.id.messageSender);
            mMessageListCard = itemView.findViewById(R.id.messageListCard);

            mLeft = itemView.findViewById(R.id.spaceLeft);
            mRight = itemView.findViewById(R.id.spaceRight);

            mViewMedia = itemView.findViewById(R.id.viewMedia);
        }
    }
}
