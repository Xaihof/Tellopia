package com.xoksis.tellopia.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xoksis.tellopia.adapters.MessagesAdapter;
import com.xoksis.tellopia.databinding.ActivityChatBinding;
import com.xoksis.tellopia.models.MessageModel;

import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding activityChatBinding;
    MessagesAdapter adapter;
    ArrayList<MessageModel> messages;

    String senderRoom, receiverRoom;

    FirebaseDatabase Database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityChatBinding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(activityChatBinding.getRoot());

        setSupportActionBar(activityChatBinding.appBarMain.toolbar);

        messages = new ArrayList<>();

        String name = getIntent().getStringExtra("name");
        String receiverUid = getIntent().getStringExtra("uid");
        String senderUid = FirebaseAuth.getInstance().getUid();

        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;

        adapter = new MessagesAdapter(this, messages, senderRoom, receiverRoom);
        activityChatBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        activityChatBinding.recyclerView.setAdapter(adapter);

        Database = FirebaseDatabase.getInstance();

        Database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        messages.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            MessageModel message = snapshot1.getValue(MessageModel.class);
                            message.setMessageId(snapshot1.getKey());
                            messages.add(message);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        activityChatBinding.imageViewSendButton.setOnClickListener(view -> {
            String messageTxt = activityChatBinding.editTextMessageBox.getText().toString();

            Date date = new Date();
            MessageModel message = new MessageModel(messageTxt, senderUid, date.getTime());
            activityChatBinding.editTextMessageBox.setText("");

            String randomKey = Database.getReference().push().getKey();

            Database.getReference()
                    .child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(randomKey)
                    .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Database.getReference()
                                    .child("chats")
                                    .child(receiverRoom)
                                    .child("messages")
                                    .child(randomKey)
                                    .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                        }
                                    });
                        }
                    });


        });


        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return super.onSupportNavigateUp();
    }
}