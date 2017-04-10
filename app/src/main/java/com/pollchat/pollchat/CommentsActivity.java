package com.pollchat.pollchat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

public class CommentsActivity extends AppCompatActivity {

    private String mPostKey = null;
    private TextView mNoPostTxt;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressDialog mProgress;
    private RecyclerView mCommentList;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseComment;
    private DatabaseReference mDatabaseUser;
    private DatabaseReference mDatabasePostComments;
    private Query mQueryPostComment;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;
    private ImageButton mCommentBtn;
    private ImageView mBtnCapture;
    private EditText mCommentField;
    private DatabaseReference mDatabaseCurrentPost;
    private Uri mImageUri = null;
    private static int GALLERY_REQUEST =1;
    private Menu menu;
    Context context = this;

    private Query mQueryComments;


    /** Called when the activity is first created. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mNoPostTxt = (TextView) findViewById(R.id.noPostTxt);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Polls");
        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mPostKey = getIntent().getExtras().getString("heartraise_id");

        mDatabasePostComments = FirebaseDatabase.getInstance().getReference().child("Comments");
        mQueryPostComment = mDatabasePostComments.orderByChild("post_key").equalTo(mPostKey);

        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        mCommentList = (RecyclerView) findViewById(R.id.comment_list);
        mCommentList.setHasFixedSize(true);
        mCommentList.setLayoutManager(new LinearLayoutManager(this));
        mDatabaseComment = FirebaseDatabase.getInstance().getReference().child("Comments");
        mDatabaseComment.keepSynced(true);
        mDatabase.keepSynced(true);
        mDatabaseUser.keepSynced(true);

        mCommentField = (EditText) findViewById(R.id.commentField);
        mCommentBtn = (ImageButton) findViewById(R.id.commentBtn);
        mCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });


        mQueryPostComment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){

                    mNoPostTxt.setVisibility(View.VISIBLE);
                } else {
                    mNoPostTxt.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mQueryPostComment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){


                } else {
                    mNoPostTxt.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mDatabaseComment.keepSynced(true);
        mDatabase.keepSynced(true);


    }



    void refreshItems() {
        // Load items
        // ...

        // Load complete
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...

        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
    }


    private void startPosting() {
        mProgress.setMessage("Posting...");

        Date date = new Date();
        final String stringDate = DateFormat.getDateTimeInstance().format(date);

        final String comment_val = mCommentField.getText().toString().trim();
        if (!TextUtils.isEmpty(comment_val)) {

            mProgress.show();


            final DatabaseReference newPost = mDatabaseComment.push();


            mQueryComments = mDatabaseComment.orderByChild("post_key").equalTo(mPostKey);
            mQueryComments.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    mDatabase.child(mPostKey).child("comments_val").setValue(dataSnapshot.getChildrenCount() + "");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            mDatabaseUser.addValueEventListener(new ValueEventListener() {


                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    newPost.child("comment").setValue(comment_val);
                    newPost.child("uid").setValue(mCurrentUser.getUid());
                    newPost.child("name").setValue(dataSnapshot.child("name").getValue());
                    newPost.child("image").setValue(dataSnapshot.child("image").getValue());
                    newPost.child("time").setValue(stringDate);
                    newPost.child("post_key").setValue(mPostKey);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            AlertDialog.Builder builder = new AlertDialog.Builder(CommentsActivity.this);
            builder.setTitle("Post Alert!");
            builder.setMessage("Your comment has been posted SUCCESSFULLY!")
                    .setCancelable(true)
                    .setPositiveButton("OK",null);
            AlertDialog dialog = builder.create();
            dialog.show();

            mProgress.dismiss();

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Comment, CommentViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comment, CommentViewHolder>(

                Comment.class,
                R.layout.comments_row,
                CommentViewHolder.class,
                mQueryPostComment


        ) {
            @Override
            protected void populateViewHolder(final CommentViewHolder viewHolder, final Comment model, int position) {

                final String post_key = getRef(position).getKey();

                viewHolder.setComment(model.getComment());
                viewHolder.setTime(model.getTime());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setUsername(model.getName());

                mDatabaseComment.child(post_key).addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String post_photo = (String) dataSnapshot.child("photo").getValue();

                        if (post_photo != null) {

                            viewHolder.setPhoto(getApplicationContext(), model.getPhoto());

                            viewHolder.mCardPhoto.setVisibility(View.VISIBLE);
                            viewHolder.mProgressBar.setVisibility(View.VISIBLE);
                            viewHolder.mInside.setVisibility(View.VISIBLE);

                        } else {


                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



                viewHolder.mCardPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent cardonClick = new Intent(CommentsActivity.this, OpenPhotoOnPollActivity.class);
                        cardonClick.putExtra("heartraise_id", post_key );
                        startActivity(cardonClick);

                    }
                });


            }
        };

        mCommentList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        View mView;

        DatabaseReference mDatabase;
        ImageView mCardPhoto, mInside, mImage, mAnonymous;
        TextView  mAnonymousText;
        ProgressBar mProgressBar;

        public CommentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mCardPhoto = (ImageView) mView.findViewById(R.id.post_photo);
            mInside = (ImageView) mView.findViewById(R.id.inside_view2);
            mImage = (ImageView) mView.findViewById(R.id.post_image);

            mProgressBar = (ProgressBar) mView.findViewById(R.id.progressBar);

        }

        public void setComment(String comment) {

            TextView post_comment = (TextView) mView.findViewById(R.id.post_comment);
            post_comment.setText(comment);

        }

        public void setUsername(String username) {

            TextView post_username = (TextView) mView.findViewById(R.id.post_username);
            post_username.setText(username);
        }

        public void setTime(String time) {

            RelativeTimeTextView v = (RelativeTimeTextView)mView.findViewById(R.id.timestamp);
            v.setText(time);
        }

        public void setImage(final Context ctx, final String image) {
            final ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);

            Picasso.with(ctx)
                    .load(image)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(post_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {


                            Picasso.with(ctx).load(image).into(post_image);
                        }
                    });
        }

        public void setPhoto(final Context ctx, final String photo) {
            final ImageView post_photo = (ImageView) mView.findViewById(R.id.post_photo);

            Picasso.with(ctx).load(photo).networkPolicy(NetworkPolicy.OFFLINE).into(post_photo, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {


                    Picasso.with(ctx).load(photo).into(post_photo);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.comment_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (item.getItemId()) {

            case android.R.id.home:
                this.finish();
                return true;
            default:
                if (id == R.id.action_settings) {

                   // Intent cardonClick = new Intent(CommentsActivity.this, SendPhotoActivity.class);
                   // cardonClick.putExtra("heartraise_id", mPostKey );
                   // startActivity(cardonClick);
                }

                return super.onOptionsItemSelected(item);
        }
    }

}
