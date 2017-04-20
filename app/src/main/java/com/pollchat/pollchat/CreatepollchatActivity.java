package com.pollchat.pollchat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Date;

public class CreatepollchatActivity extends AppCompatActivity {

    private EditText mFirstRowEdit, mSecondRowEdit, mThirdRowEdit, mFourthRowEdit, mPollQuiz;
    private TextView mThirdRowVStxt, mFourthRowVStxt;
    private ImageView  mInputFirstRowUserImg, mInputSecondRowUserImg, mInputFourthRowUserImg, mInputThirdRowUserImg, mAddRowBtn, mAddRowBtn2;
    private Button mStartPoll;
    private DatabaseReference mDatabaseUser;
    private DatabaseReference mDatabasePolls;
    private ProgressDialog mProgress;
    private StorageReference mStorage;
    private Uri mImageUri = null;
    private Uri mImageUri2 = null;
    private Uri mImageUri3 = null;
    private Uri mImageUri4 = null;
    private static int GALLERY_REQUEST =1;
    private static int GALLERY_REQUEST2 =2;
    private static int GALLERY_REQUEST3 =3;
    private static int GALLERY_REQUEST4 =4;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createpollchat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // contestant's name
        mFirstRowEdit = (EditText) findViewById(R.id.input_first_row_username);
        mSecondRowEdit = (EditText) findViewById(R.id.input_second_row_username);
        mThirdRowEdit = (EditText) findViewById(R.id.input_third_row_username);
        mFourthRowEdit = (EditText) findViewById(R.id.input_fourth_row_username);

        mPollQuiz = (EditText) findViewById(R.id.poll_question);
        auth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();

        // firebase databases
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabasePolls = FirebaseDatabase.getInstance().getReference().child("Polls");


        final RelativeLayout third_row_layout = (RelativeLayout) findViewById(R.id.third_relative_layout);
        final RelativeLayout fourth_row_layout = (RelativeLayout) findViewById(R.id.fourth_relative_layout);

        // add row btn
        mAddRowBtn = (ImageView) findViewById(R.id.addrowBtn);
        mAddRowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // show third row
                third_row_layout.setVisibility(View.VISIBLE);
                mThirdRowVStxt.setVisibility(View.VISIBLE);
                //hide button first top btn
                mAddRowBtn.setVisibility(View.GONE);

            }
        });

        mAddRowBtn2 = (ImageView) findViewById(R.id.addrowBtn2);
        mAddRowBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // show third row
                fourth_row_layout.setVisibility(View.VISIBLE);
                mFourthRowVStxt.setVisibility(View.VISIBLE);
                //hide button first top btn
                mAddRowBtn2.setVisibility(View.GONE);
            }
        });

        mThirdRowVStxt = (TextView) findViewById(R.id.thirdrow_vs_txt);
        mFourthRowVStxt = (TextView) findViewById(R.id.fourthrow_vs_txt);


        // start poll button
        mStartPoll = (Button) findViewById(R.id.startPollBtn);
        mStartPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });

        // user images
        mInputFirstRowUserImg = (ImageView) findViewById(R.id.input_first_row_userimg);
        mInputFirstRowUserImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        mInputSecondRowUserImg = (ImageView) findViewById(R.id.input_second_row_userimg);
        mInputSecondRowUserImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST2);

            }
        });

        mInputThirdRowUserImg = (ImageView) findViewById(R.id.input_third_row_userimg);
        mInputThirdRowUserImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST3);

            }
        });

        mInputFourthRowUserImg = (ImageView) findViewById(R.id.input_fourth_row_userimg);
        mInputFourthRowUserImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST4);

            }
        });

        mProgress = new ProgressDialog(this);


    }

    //posting poll data to firebase...
    private void startPosting() {
        mProgress.setMessage("Your PollChat will be LIVE in a few minutes...");
        mProgress.setCancelable(false);

        Date date = new Date();
        final String stringDate = DateFormat.getDateInstance().format(date);

        final String poll_quiz = mPollQuiz.getText().toString().trim();
        final String first_row_username = mFirstRowEdit.getText().toString().trim();
        final String second_row_username = mSecondRowEdit.getText().toString().trim();
        final String third_row_username = mThirdRowEdit.getText().toString().trim();
        final String fourth_row_username = mFourthRowEdit.getText().toString().trim();

        final String user_id = auth.getCurrentUser().getUid();

        if (!TextUtils.isEmpty(poll_quiz) && !TextUtils.isEmpty(first_row_username) && !TextUtils.isEmpty(second_row_username)) {

            mProgress.show();

            if ( mImageUri != null) {

                StorageReference filepath = mStorage.child("polls_images").child(mImageUri.getLastPathSegment());

                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        final Uri downloadUrl1 = taskSnapshot.getDownloadUrl();


                        if ( mImageUri2 != null) {

                            StorageReference filepath = mStorage.child("polls_images").child(mImageUri2.getLastPathSegment());

                            filepath.putFile(mImageUri2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    final Uri downloadUrl2 = taskSnapshot.getDownloadUrl();


                                    if ( mImageUri3 != null) {

                                        StorageReference filepath = mStorage.child("polls_images").child(mImageUri3.getLastPathSegment());

                                        filepath.putFile(mImageUri3).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                final Uri downloadUrl3 = taskSnapshot.getDownloadUrl();


                                                if ( mImageUri4 != null) {

                                                    StorageReference filepath = mStorage.child("polls_images").child(mImageUri4.getLastPathSegment());

                                                    filepath.putFile(mImageUri4).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                            final Uri downloadUrl4 = taskSnapshot.getDownloadUrl();


                                                            final DatabaseReference newPost = mDatabasePolls.push();

                                                            mDatabaseUser.child(user_id).addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    newPost.child("poll_question").setValue(poll_quiz);
                                                                    newPost.child("first_row_username").setValue(first_row_username);
                                                                    newPost.child("second_row_username").setValue(second_row_username);
                                                                    newPost.child("third_row_username").setValue(third_row_username);
                                                                    newPost.child("fourth_row_username").setValue(fourth_row_username);
                                                                    newPost.child("first_row_userimg").setValue(downloadUrl1.toString());
                                                                    newPost.child("second_row_userimg").setValue(downloadUrl2.toString());
                                                                    newPost.child("third_row_userimg").setValue(downloadUrl3.toString());
                                                                    newPost.child("fourth_row_userimg").setValue(downloadUrl4.toString());
                                                                    // user photo
                                                                    newPost.child("name").setValue(dataSnapshot.child("name").getValue());
                                                                    // user image
                                                                    newPost.child("image").setValue(dataSnapshot.child("image").getValue());
                                                                    newPost.child("uid").setValue(user_id);
                                                                    newPost.child("created_date").setValue(stringDate);


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
                                                    });
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            mImageUri = data.getData();
            mInputFirstRowUserImg.setImageURI(mImageUri);


        } else if (requestCode == GALLERY_REQUEST2 && resultCode == RESULT_OK) {
            mImageUri2 = data.getData();
            mInputSecondRowUserImg.setImageURI(mImageUri2);

        } else if (requestCode == GALLERY_REQUEST3 && resultCode == RESULT_OK) {

            mImageUri3 = data.getData();
            mInputThirdRowUserImg.setImageURI(mImageUri3);

        } else if (requestCode == GALLERY_REQUEST4 && resultCode == RESULT_OK) {

            mImageUri4 = data.getData();
            mInputFourthRowUserImg.setImageURI(mImageUri4);

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
