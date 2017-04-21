package com.pollchat.pollchat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity {

    String mPostKey = null;
    String uid_key = null;
    private TextView mNoPostTxt;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressDialog mProgress;
    private RecyclerView mCommentList;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseComment;
    private DatabaseReference mDatabaseUser;
    private DatabaseReference mDatabaseUser2;
    private DatabaseReference mDatabasePostChats;
    private Query mQueryPostChats;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;
    private ImageButton mSendBtn;
    private EditText mCommentField;
    private Uri mImageUri = null;
    private static int GALLERY_REQUEST =1;
    private Menu menu;
    Context context = this;
    private Boolean mProcessLike = false;
    private DatabaseReference mDatabaseLike;
    private Boolean mProcessUnlike = false;
    private DatabaseReference mDatabaseUnlike;
    private Query mQueryChats;


    /** Called when the activity is first created. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Toolbar my_toolbar = (Toolbar) findViewById(R.id.mCustomToolbarChat);
        setSupportActionBar(my_toolbar);

        //mNoPostTxt = (TextView) findViewById(R.id.noPostTxt);
        final RelativeLayout hello = (RelativeLayout) findViewById(R.id.hello);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });


        mAuth = FirebaseAuth.getInstance();
        mPostKey = getIntent().getExtras().getString("heartraise_id");
        uid_key = getIntent().getExtras().getString("poll_uid");

        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseUnlike = FirebaseDatabase.getInstance().getReference().child("Unlikes");
        mDatabasePostChats = FirebaseDatabase.getInstance().getReference().child("Comments");
        mQueryPostChats = mDatabasePostChats.orderByChild("post_key").equalTo(mPostKey);

        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        mDatabaseUser2 = FirebaseDatabase.getInstance().getReference().child("Users");
        mCommentList = (RecyclerView) findViewById(R.id.comment_list);
        mCommentList.setHasFixedSize(true);
        mCommentList.setLayoutManager(new LinearLayoutManager(this));
        mDatabaseComment = FirebaseDatabase.getInstance().getReference().child("Comments");
        mDatabaseComment.keepSynced(true);
        mDatabaseUser.keepSynced(true);
        mDatabaseLike.keepSynced(true);

        mCommentField = (EditText) findViewById(R.id.commentField);
        mSendBtn = (ImageButton) findViewById(R.id.sendBtn);
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });

        // toolbar back button
        ImageView toolbar_back = (ImageView) findViewById(R.id.toolbar_back);
        toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentsActivity.this.finish();
            }
        });

        mDatabaseUser2.child(uid_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String userimg = (String) dataSnapshot.child("image").getValue();
                final String username = (String) dataSnapshot.child("name").getValue();

                // load image on toolbar
                CircleImageView userImgToolbar = (CircleImageView) findViewById(R.id.toolbarImg);
                Picasso.with(CommentsActivity.this).load(userimg).into(userImgToolbar);

                // set username on toolbar
                TextView toolbar_username = (TextView) findViewById(R.id.toolbar_username);
                toolbar_username.setText(username);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mQueryPostChats.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){

                    hello.setVisibility(View.VISIBLE);
                } else {
                    hello.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mDatabaseComment.keepSynced(true);


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

                    // getting user uid
                    final String reciever_uid = (String) dataSnapshot.child("uid").getValue();

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
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Chat, CommentViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Chat, CommentViewHolder>(

                Chat.class,
                R.layout.chat_row,
                CommentViewHolder.class,
                mQueryPostChats


        ) {
            @Override
            protected void populateViewHolder(final CommentViewHolder viewHolder, final Chat model, int position) {

                final String post_key = getRef(position).getKey();

                viewHolder.setMessage(model.getMessage());
                viewHolder.setDate(model.getDate());
                viewHolder.setName(model.getName());
                viewHolder.setImage(getApplicationContext(), model.getImage());

                viewHolder.setLikeBtn(post_key);


                mDatabaseLike.child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.mLikeCount.setText(dataSnapshot.getChildrenCount() + "");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.mLikeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mProcessLike = true;

                        mDatabaseLike.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(mProcessLike) {

                                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                                        mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        mProcessLike = false;
                                    }else {

                                        mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                                        // mDatabaseLike.child(post_key).child("post_key").setValue(mAuth.getCurrentUser().getUid());

                                        mProcessLike = false;

                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                });

                mDatabaseUnlike.child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.mUnlikeCount.setText(dataSnapshot.getChildrenCount() + "");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.mUnlikeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mProcessLike = true;

                        mDatabaseLike.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(mProcessUnlike) {

                                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                                        mDatabaseUnlike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        mProcessUnlike = false;
                                    }else {

                                        mDatabaseUnlike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                                        // mDatabaseLike.child(post_key).child("post_key").setValue(mAuth.getCurrentUser().getUid());

                                        mProcessUnlike = false;

                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                });


                mDatabasePostChats.child(post_key).addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String post_photo = (String) dataSnapshot.child("photo").getValue();

                        if (post_photo != null) {

                            viewHolder.setPhoto(getApplicationContext(), model.getPhoto());
                            viewHolder.mCardPhoto.setVisibility(View.VISIBLE);

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

                        Intent cardonClick = new Intent(CommentsActivity.this, OpenPhotoActivity.class);
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

        ImageView mCardPhoto, mImage, mLikeBtn, mUnlikeBtn;
        TextView mCommentCount, mLikeCount, mUnlikeCount;
        DatabaseReference mDatabaseLike;
        FirebaseAuth mAuth;
        LinearLayout liny;
        ProgressBar mProgressBar;

        public CommentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mAuth = FirebaseAuth.getInstance();
            mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
            mDatabaseLike.keepSynced(true);
            mCardPhoto = (ImageView) mView.findViewById(R.id.post_photo);
            mImage = (ImageView) mView.findViewById(R.id.post_image);
            mLikeBtn = (ImageView) mView.findViewById(R.id.likeBtn);
            mUnlikeBtn = (ImageView) mView.findViewById(R.id.unlikeBtn);
            liny = (LinearLayout) mView.findViewById(R.id.liny);
            mLikeCount = (TextView) mView.findViewById(R.id.likeCount);

            mCommentCount = (TextView) mView.findViewById(R.id.commentCount);

        }

        public void setLikeBtn(final String post_key) {

            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                       // mCall.setImageResource(R.drawable.like_btn_red);
                    } else {
                        //mCall.setImageResource(R.drawable.like_btn_black);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        public void setMessage(String message) {

            TextView post_message = (TextView) mView.findViewById(R.id.post_message);
            post_message.setText(message);


        }

        public void setName(String name) {

            TextView post_name = (TextView) mView.findViewById(R.id.post_name);
            post_name.setText(name);


        }


        public void setDate(String date) {

            TextView post_date = (TextView) mView.findViewById(R.id.post_date);
            post_date.setText(date);


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



}





