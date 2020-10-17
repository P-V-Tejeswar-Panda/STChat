package com.example.stchat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {

    private ArrayList<UserDataModel> userList;

    public UserListAdapter(ArrayList<UserDataModel> userList){
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserListAdapter.UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        UserListViewHolder rcv = new UserListViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull UserListAdapter.UserListViewHolder holder, final int position) {
        holder.mName.setText(userList.get(position).getName());
        holder.mPhone.setText(userList.get(position).getPhoneNumber());

        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("chat");
                Query query = dbRef.orderByChild("phone").equalTo(userList.get(position).getPhoneNumber());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            String key =  FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();

                            // The user Who clicks
                            DatabaseReference mChatDBOwn = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("chat").child(key);
                            // The user who is being clicked on
                            DatabaseReference mChatDBOther = FirebaseDatabase.getInstance().getReference().child("users").child(userList.get(position).getUid()).child("chat").child(key);


                            // The user Who clicks
                            Map<String , Object> mChatMap = new HashMap<>();
                            mChatMap.put("name", userList.get(position).getName());
                            mChatMap.put("phone", userList.get(position).getPhoneNumber());

                            mChatDBOwn.updateChildren(mChatMap);

                            // The user who is being clicked On
                            Map<String , Object> mChatMapAlt = new HashMap<>();
                            mChatMapAlt.put("name",FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                            mChatMapAlt.put("phone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

                            mChatDBOther.updateChildren(mChatMapAlt);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserListViewHolder extends RecyclerView.ViewHolder {
        public TextView mName, mPhone;
        LinearLayout mLayout;
        public UserListViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.name);
            mPhone = itemView.findViewById(R.id.phone);
            mLayout = itemView.findViewById(R.id.userlayout);

        }
    }
}
