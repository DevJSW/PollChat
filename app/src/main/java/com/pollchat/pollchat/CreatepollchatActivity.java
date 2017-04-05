package com.pollchat.pollchat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

public class CreatepollchatActivity extends AppCompatActivity {

    private EditText mFirstRowEdit, mSecondRowEdit, mPollQuiz;
    private TextView mFirstRowVoteTxt, mSecondRowVoteTxt;
    private ImageView mFirstRowVoteBtn, mSecondRowVoteBtn;
    private Button mStartPoll;
    private DatabaseReference mDatabaseUser;
    private DatabaseReference mDatabasePolls;
    private ProgressDialog mProgress;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createpollchat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // contestant's name
        mFirstRowEdit = (EditText) findViewById(R.id.input_first_row_username);
        mSecondRowEdit = (EditText) findViewById(R.id.input_second_row_username);

        mPollQuiz = (EditText) findViewById(R.id.poll_question);
        auth = FirebaseAuth.getInstance();

        // firebase databases
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabasePolls = FirebaseDatabase.getInstance().getReference().child("Polls");


        // vote'n btn's
        mFirstRowVoteBtn = (ImageView) findViewById(R.id.first_row_voteBtn);
        mSecondRowVoteBtn = (ImageView) findViewById(R.id.second_row_voteBtn);

        //vote: 0 text
        mFirstRowVoteTxt = (TextView) findViewById(R.id.first_row_vote_txt);
        mSecondRowVoteTxt = (TextView) findViewById(R.id.second_row_vote_txt);

        // start poll button
        mStartPoll = (Button) findViewById(R.id.startPollBtn);
        mStartPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });

        mProgress = new ProgressDialog(this);

        checkifEditIsClear();

    }

    //posting poll data to firebase...
    private void startPosting() {
        mProgress.setMessage("Posting...");

        Date date = new Date();
        final String stringDate = DateFormat.getDateInstance().format(date);

        final String poll_quiz = mPollQuiz.getText().toString().trim();
        final String first_row_username = mFirstRowEdit.getText().toString().trim();
        final String second_row_username = mSecondRowEdit.getText().toString().trim();

        final String user_id = auth.getCurrentUser().getUid();

        if (!TextUtils.isEmpty(poll_quiz) && !TextUtils.isEmpty(first_row_username) && !TextUtils.isEmpty(second_row_username)) {

            mProgress.show();


                        final DatabaseReference newPost = mDatabasePolls.push();

                        mDatabaseUser.child(user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                newPost.child("poll_question").setValue(poll_quiz);
                                newPost.child("first_row_username").setValue(first_row_username);
                                newPost.child("second_row_username").setValue(second_row_username);
                               // newPost.child("photo").setValue(downloadUrl.toString());
                                newPost.child("uid").setValue(user_id);
                                newPost.child("created_date").setValue(stringDate);
                                //newPost.child("community_sent").setValue(community);

                                AlertDialog.Builder builder = new AlertDialog.Builder(CreatepollchatActivity.this);
                                builder.setTitle("Congratulations!");
                                builder.setMessage("Your PollChat is LIVE!")
                                        .setCancelable(true)
                                        .setPositiveButton("OK", null);
                                AlertDialog dialog = builder.create();
                                dialog.show();


                                mProgress.dismiss();


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


        }
    }

    private void checkifEditIsClear() {

        String inputName = mFirstRowEdit.getText().toString().trim();

        // if editTXT is EMPTY!
        if (TextUtils.isEmpty(inputName)) {

            // make vote icon DISAPPEAR
            mFirstRowVoteBtn.setVisibility(View.GONE);
            mSecondRowVoteBtn.setVisibility(View.GONE);

            // make votes: 0 text VANISH!
            mFirstRowVoteTxt.setVisibility(View.GONE);
            mSecondRowVoteTxt.setVisibility(View.GONE);



        } else {

            // make vote icons VISIBLE
            mFirstRowVoteBtn.setVisibility(View.VISIBLE);
            mSecondRowVoteBtn.setVisibility(View.VISIBLE);

            // make votes: 0 text visible
            mFirstRowVoteTxt.setVisibility(View.VISIBLE);
            mSecondRowVoteTxt.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
