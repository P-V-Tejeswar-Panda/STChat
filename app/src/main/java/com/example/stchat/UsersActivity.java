package com.example.stchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import com.example.stchat.UserDataModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class UsersActivity extends AppCompatActivity {

    private RecyclerView mUserList;
    private RecyclerView.Adapter mUserListAdapter;
    private RecyclerView.LayoutManager mUserListLayoutManager;

    public ArrayList<com.example.stchat.UserDataModel> userList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        userList = new ArrayList<>();

        innitializeRecyclerView();
        getContactList();


    }

    private void getContactList(){

        String ISOPrefix = getCountryISO();

        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()){
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            phone.replace(" ", "");
            phone.replace("-", "");
            phone.replace("(", "");
            phone.replace(")", "");

            if(!String.valueOf(phone.charAt(0)).equals("+")){
                phone = ISOPrefix + phone;
            }


            UserDataModel mContact = new UserDataModel("", name, phone);
            //userList.add(mContact);
           // mUserListAdapter.notifyDataSetChanged();
            if(phone.equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) continue;
            getUserDetails(mContact);

        }
    }

    private void getUserDetails(final UserDataModel mContact) {
        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = mUserDB.orderByChild("phone").equalTo(mContact.getPhoneNumber());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String name = "", mUserId = "",
                            phone = "";

                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        if(childSnapshot.child("phone").getValue() != null)
                            phone = childSnapshot.child("phone").getValue().toString();
                        if(childSnapshot.child("name").getValue() != null)
                            name = childSnapshot.child("name").getValue().toString();
                        mUserId = childSnapshot.getKey();

                        UserDataModel dataModel = new UserDataModel(mUserId, mContact.getName(), phone);
                        userList.add(dataModel);
                        mUserListAdapter.notifyDataSetChanged();
                    }



                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private  String getCountryISO(){
        String ISO = null;

        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if(telephonyManager.getNetworkCountryIso() != null){
            if(!telephonyManager.getNetworkCountryIso().toString().equals("")){
                ISO = telephonyManager.getNetworkCountryIso().toString();
            }
        }

        return ISOtoCountryCode.getPhone(ISO);
    }

    private void innitializeRecyclerView() {
        mUserList = findViewById(R.id.userlist);
        mUserList.setNestedScrollingEnabled(false);
        mUserList.setHasFixedSize(false);
        mUserListLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mUserList.setLayoutManager(mUserListLayoutManager);
        mUserListAdapter = new UserListAdapter(userList);
        mUserList.setAdapter(mUserListAdapter);
    }
}
