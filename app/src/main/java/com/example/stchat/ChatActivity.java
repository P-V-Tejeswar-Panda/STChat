package com.example.stchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "DFF";
    private RecyclerView mChat;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;

    private RecyclerView mImageList;
    private RecyclerView.Adapter mImageListAdapter;
    private RecyclerView.LayoutManager mImageListLayoutManager;

    ArrayList<String> imageURIList;


    private EditText mMessageBox;
    private Button mSendButton;
    private Button mAddMedia;
    public ArrayList<ChatModel> mChatList;

    private DatabaseReference dbRef;

    String chatID = "", name = "", phoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatList = new ArrayList<>();
        imageURIList = new ArrayList<>();



        chatID = getIntent().getExtras().getString("chatID");
        name = getIntent().getExtras().getString("name");
        phoneNumber = getIntent().getExtras().getString("phoneNumber");

        dbRef = FirebaseDatabase.getInstance().getReference().child("chat").child(chatID);

        mMessageBox = findViewById(R.id.messageBox);
        mSendButton = findViewById(R.id.messageSendButton);
        mAddMedia = findViewById(R.id.addImage);

        mAddMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        initializeRecyclerView();
        initializeMediaRecyclerView();
        getChatMessages();
    }



    private void getChatMessages() {

        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    String message = "", sender = "", senderID = "";

                    ArrayList<String> mediaUriList = new ArrayList<>();

                    if(dataSnapshot.child("media").getChildrenCount() > 0){
                        for(DataSnapshot dataSnapshotChild : dataSnapshot.child("media").getChildren()){
                            mediaUriList.add(dataSnapshotChild.getValue().toString());
                        }
                    }

                    if(dataSnapshot.child("text").exists()){
                        message = dataSnapshot.child("text").getValue().toString();
                    }
                    sender = dataSnapshot.child("senderPhone").getValue().toString();
                    senderID = dataSnapshot.child("senderID").getValue().toString();

                    ChatModel model = new ChatModel(senderID, message, sender, mediaUriList);
                    mChatList.add(model);
                    mChatLayoutManager.scrollToPosition(mChatList.size()-1);
                    mChatAdapter.notifyDataSetChanged();


                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    int totalMediaUploaded = 0;
    private void sendMessage() {
            final DatabaseReference messageDB = FirebaseDatabase.getInstance().getReference().child("chat").child(chatID).push();
            final String messageID = messageDB.getKey();

            final Map<String, Object> messageMap = new HashMap<>();

            if(!mMessageBox.getText().toString().isEmpty())
                messageMap.put("text", mMessageBox.getText().toString());
            messageMap.put("senderID", FirebaseAuth.getInstance().getUid());
            messageMap.put("senderPhone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());


            if(!imageURIList.isEmpty()){
                for(String mediaURI : imageURIList){
                    final String mediaID = messageDB.child("media").push().getKey();
                    final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("chat").child(chatID).child(messageID).child(mediaID);

                    UploadTask uploadTask = filePath.putFile(Uri.parse(mediaURI));

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    messageMap.put("/media/"+mediaID+"/", uri.toString());
                                    totalMediaUploaded++;

                                    if(totalMediaUploaded == imageURIList.size()){
                                        messageDB.updateChildren(messageMap);
                                        imageURIList.clear();
                                        mImageListAdapter.notifyDataSetChanged();
                                        mMessageBox.setText(null);
                                    }
                                }
                            });
                        }
                    });
                }
            }
            else if(!mMessageBox.getText().toString().isEmpty()) {
                messageDB.updateChildren(messageMap);
                mMessageBox.setText(null);
            }

    }

    private void initializeRecyclerView() {
        mChat = findViewById(R.id.messageList);
        mChat.setNestedScrollingEnabled(false);
        mChat.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mChat.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatAdapter(mChatList);
        mChat.setAdapter(mChatAdapter);
    }

    int PICK_IMAGE_INTENT = 1;


    private void openGallery() {

        Intent intent = new Intent();

        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Images"), PICK_IMAGE_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            //Toast toast = Toast.makeText(getApplicationContext(), "Hey!!", Toast.LENGTH_SHORT);
            //toast.show();
            if(requestCode == PICK_IMAGE_INTENT){
                if(data.getClipData() == null){
                    imageURIList.add(data.getData().toString());
                }
                else {
                    for(int i = 0; i < data.getClipData().getItemCount(); i++){
                        imageURIList.add(data.getClipData().getItemAt(i).getUri().toString());
                    }
                }
            }

            mImageListAdapter.notifyDataSetChanged();
        }
    }

    private void initializeMediaRecyclerView() {
        mImageList = findViewById(R.id.imageList);
        mImageList.setNestedScrollingEnabled(false);
        mImageList.setHasFixedSize(false);
        mImageListLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false);
        mImageList.setLayoutManager(mImageListLayoutManager);
        mImageListAdapter = new ImageListAdapter(getApplicationContext(), imageURIList);
        mImageList.setAdapter(mImageListAdapter);
    }
}
