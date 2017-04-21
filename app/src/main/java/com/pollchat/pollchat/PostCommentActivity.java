package com.pollchat.pollchat;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

public class PostCommentActivity extends AppCompatActivity {

    String mPostKey = null;
    String uid_key = null;
    private ProgressDialog mProgress;
    private RecyclerView mCommentList;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseComment;
    private DatabaseReference mDatabaseUser;
    private DatabaseReference mDatabaseUser2;
    private DatabaseReference mDatabasePostChats;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;
    private ImageButton mSendBtn;
    private EditText mCommentField;
    Context context = this;


    /** Called when the activity is first created. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comment);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mAuth = FirebaseAuth.getInstance();
        mPostKey = getIntent().getExtras().getString("heartraise_id");
        uid_key = getIntent().getExtras().getString("poll_uid");

        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        mDatabaseUser2 = FirebaseDatabase.getInstance().getReference().child("Users");
        mCommentList = (RecyclerView) findViewById(R.id.comment_list);
        mCommentList.setHasFixedSize(true);
        mCommentList.setLayoutManager(new LinearLayoutManager(this));
        mDatabaseComment = FirebaseDatabase.getInstance().getReference().child("Comments");
        mDatabaseComment.keepSynced(true);
        mDatabaseUser.keepSynced(true);

        mCommentField = (EditText) findViewById(R.id.commentField);
        mSendBtn = (ImageButton) findViewById(R.id.sendBtn);
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });



    }


    private void startPosting() {
        // mProgress.setMessage("Posting...");

        Date date = new Date();
        final String stringDate = DateFormat.getDateTimeInstance().format(date);

        final String message_val = mCommentField.getText().toString().trim();
        if (!TextUtils.isEmpty(message_val)) {

            //mProgress.show();

            final DatabaseReference newPost = mDatabaseComment.push();


            mDatabaseUser.child(mPostKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    mDatabaseUser.addValueEventListener(new ValueEventListener() {


                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            newPost.child("message").setValue(message_val);
                            newPost.child("uid").setValue(mCurrentUser.getUid());
                            newPost.child("name").setValue(dataSnapshot.child("name").getValue());
                            newPost.child("image").setValue(dataSnapshot.child("image").getValue());
                            newPost.child("date").setValue(stringDate);
                            newPost.child("post_key").setValue(mPostKey);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            // mProgress.dismiss();

        }

    }



}

