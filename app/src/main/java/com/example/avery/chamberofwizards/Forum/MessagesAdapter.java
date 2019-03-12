package com.example.avery.chamberofwizards.Forum;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.avery.chamberofwizards.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>
{
    private List<Messages> userMessagesList;

    private FirebaseAuth mAuth;
    private DatabaseReference usersDatabaseRef;

    public MessagesAdapter (List<Messages> userMessagesList)
    {
        this.userMessagesList = userMessagesList;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView senderMessageText, receiverMessageText;
        public CircleImageView receiverProfileImage;

        public MessageViewHolder(View itemView)
        {
            super(itemView);

            senderMessageText = (TextView) itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage =  (CircleImageView) itemView.findViewById(R.id.message_profile_image);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View V = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_layout_of_user, parent, false) ;

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(V);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position)
    {
        String messageSenderID = mAuth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(position);

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();

        usersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);

        usersDatabaseRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String image = dataSnapshot.child("profile_image").getValue().toString();

                    Picasso.get().load(image).into(holder.receiverProfileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        if(fromMessageType.equals("text"))
        {
            holder.receiverMessageText.setVisibility(View.INVISIBLE);
            holder.receiverProfileImage.setVisibility(View.INVISIBLE);

                if(fromUserID.equals(messageSenderID))
                {
                    holder.senderMessageText.setBackgroundResource(R.drawable.sender_message_text_background);
                    holder.senderMessageText.setTextColor(Color.WHITE);
                    holder.senderMessageText.setGravity(Gravity.LEFT);
                    holder.senderMessageText.setText(messages.getMessage());
                }
                else
                {
                    holder.senderMessageText.setVisibility(View.INVISIBLE);

                    holder.receiverMessageText.setVisibility(View.VISIBLE);
                    holder.receiverProfileImage.setVisibility(View.VISIBLE);

                    holder.receiverMessageText.setBackgroundResource(R.drawable.receiver_message_text_background);
                    holder.receiverMessageText.setTextColor(Color.WHITE);
                    holder.receiverMessageText.setGravity(Gravity.LEFT);
                    holder.receiverMessageText.setText(messages.getMessage());
                }
        }



    }

    @Override
    public int getItemCount()
    {
        return userMessagesList.size();
    }
}
