package com.example.avery.chamberofwizards.Forum;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avery.chamberofwizards.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference usersRef;
    private DatabaseReference messagesRef;
    private DatabaseReference rootRef;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter messagesAdapter;

    private Toolbar mToolbar;
    private ImageButton btnSendImage;
    private ImageButton btnSendMessage;
    private EditText txtMessage;
    private RecyclerView userMessagesList;

    private String messageReceiverID, messageReceiverName;

    private TextView receiverName;
    private CircleImageView receiverProfileImage;

    private String imgURL;
    private String saveCurrentDate, saveCurrentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initializeFields();
        loadUsernameAndImage();

        btnSendMessage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        txtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() == 0) {
                    btnSendMessage.setEnabled(false);
                    btnSendMessage.setVisibility(View.INVISIBLE);
                } else {
                    btnSendMessage.setEnabled(true);
                    btnSendMessage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        fetchMessages();

    }

    public void initializeFields() {
        messageReceiverID = getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("fullname").toString();

        mToolbar = findViewById(R.id.chat_bar_layout);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.chat_custom_layout, null);
        actionBar.setCustomView(action_bar_view);
        actionBar.setTitle(messageReceiverName);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(messageReceiverID);
        messagesRef = FirebaseDatabase.getInstance().getReference().child("Messages");
        rootRef = FirebaseDatabase.getInstance().getReference();

        btnSendImage = findViewById(R.id.send_image_file_button);
        btnSendMessage = findViewById(R.id.send_message_button);
        txtMessage = findViewById(R.id.send_message);

        receiverName = findViewById(R.id.custom_profile_name);
        receiverProfileImage = findViewById(R.id.custom_profile_image);

        messagesAdapter = new MessagesAdapter(messagesList);
        userMessagesList = (RecyclerView) findViewById(R.id.messages_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setHasFixedSize(true);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messagesAdapter);
    }

    public void loadUsernameAndImage()
    {
        //receiverName.setText(messageReceiverName);

        usersRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    imgURL = dataSnapshot.child("profile_image").getValue().toString();

                    Picasso.get().load(imgURL).into(receiverProfileImage);
                }
                else {
                    Toast.makeText(ChatActivity.this, "Error retrieving data.", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    public void sendMessage()
    {

        String messageText = txtMessage.getText().toString();

        String message_sender_ref = "Messages/" + currentUserID + "/" + messageReceiverID;
        String message_receiver_ref = "Messages/" + messageReceiverID + "/" + currentUserID;
        DatabaseReference userKeyRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserID).child(messageReceiverID).push();

        String user_key = userKeyRef.getKey();

        //Getting current date
        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd", Locale.US);
        saveCurrentDate = currentDate.format(callForDate.getTime());
///
        //Getting current Time
        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
        saveCurrentTime = currentTime.format(callForTime.getTime());

        HashMap<String, Object> messageMap = new HashMap<>();
        messageMap.put("message",messageText);
        messageMap.put("time",saveCurrentTime);
        messageMap.put("date",saveCurrentDate);
        messageMap.put("type", "text");
        messageMap.put("from",currentUserID);

        HashMap<String,Object> messageBodyMap = new HashMap<>();
        messageBodyMap.put(message_sender_ref + "/" + user_key , messageMap);
        messageBodyMap.put(message_receiver_ref + "/" + user_key , messageMap);

        rootRef.updateChildren(messageBodyMap).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(ChatActivity.this, "Message sent succesfully!", Toast.LENGTH_SHORT).show();
                    txtMessage.setText("");
                }
                else
                {
                    Toast.makeText(ChatActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    txtMessage.setText("");
                }
            }
        });
    }

    public void fetchMessages()
    {
        rootRef.child("Messages").child(currentUserID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener()
                {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {
                        if(dataSnapshot.exists())
                        {
                            Messages messages = dataSnapshot.getValue(Messages.class);
                            messagesList.add(messages);
                            messagesAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s)
                    {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

}


