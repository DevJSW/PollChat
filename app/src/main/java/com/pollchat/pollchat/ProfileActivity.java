package com.pollchat.pollchat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    String user_uid = null;
    String user_id = null;
    private Button mEdit;
    private ImageButton mFollow, mUnfollow;
    private CircleImageView mCIV;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private Boolean mProcessVote = false;
    private DatabaseReference mDatabaseTotalVotes, mDatabasePostKey;
    private DatabaseReference mDatabaseVotesForFirstRow;
    private DatabaseReference mDatabaseVotesForSecondRow;
    private DatabaseReference mDatabaseVotesForThirdRow;
    private DatabaseReference mDatabaseVotesForFourthRow;
    private DatabaseReference mDatabaseComment;
    private DatabaseReference mDatabasePolls;
    private ProgressBar mProgressBar;
    private ImageView mUserImg;
    private TextView mUsername, mFollowersCount, mPollsCount,  mFollowingCount;
    private DatabaseReference mDatabaseFollowers, mDatabaseFollowing, mDatabase, mDatabaseUsers;
    private Boolean mProcessFollow = false;
    private RecyclerView mPollsList;
    private String mPostKey = null;
    private FirebaseAuth mAuth;
    private Query mQueryComments;
    private Query mQueryPolls;
    private Query mQueryResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPostKey = getIntent().getExtras().getString("heartraise_id");
        user_uid = getIntent().getExtras().getString("uid_id");

        mEdit = (Button) findViewById(R.id.editBtn);
        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();

        mFollowersCount = (TextView) findViewById(R.id.followersCount);
        mFollowingCount = (TextView) findViewById(R.id.followingCount);
        mPollsCount = (TextView) findViewById(R.id.pollsCount);
        mUsername = (TextView) findViewById(R.id.post_name);
        mCIV = (CircleImageView) findViewById(R.id.post_image);

        // will have the list of ppl who are following me
        mDatabaseFollowers = FirebaseDatabase.getInstance().getReference().child("Followers");
        mDatabaseFollowers.keepSynced(true);

        // will have list of ppl i'm following
        mDatabaseFollowing = FirebaseDatabase.getInstance().getReference().child("Following");
        mDatabaseFollowing.keepSynced(true);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);
        mDatabasePostKey = FirebaseDatabase.getInstance().getReference().child("Polls").child(mPostKey);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Polls");
        mDatabase.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();


        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabasePolls = FirebaseDatabase.getInstance().getReference().child("Polls");
        mDatabaseComment = FirebaseDatabase.getInstance().getReference().child("Comments");
        mDatabaseTotalVotes = FirebaseDatabase.getInstance().getReference().child("Total_Votes");
        mDatabaseVotesForFirstRow = FirebaseDatabase.getInstance().getReference().child("First_row_votes");
        mDatabaseVotesForSecondRow = FirebaseDatabase.getInstance().getReference().child("Second_row_votes");
        mDatabaseVotesForThirdRow = FirebaseDatabase.getInstance().getReference().child("Third_row_votes");
        mDatabaseVotesForFourthRow = FirebaseDatabase.getInstance().getReference().child("Fourth_row_votes");
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar2);
        mPollsList = (RecyclerView) findViewById(R.id.polls_list);
        mPollsList.setLayoutManager(new LinearLayoutManager(this));
        mPollsList.setHasFixedSize(true);

        mDatabasePolls.keepSynced(true);
        mDatabaseUsers.keepSynced(true);
        mDatabaseTotalVotes.keepSynced(true);
        mDatabaseVotesForFirstRow.keepSynced(true);
        mDatabaseVotesForSecondRow.keepSynced(true);
        mDatabaseVotesForThirdRow.keepSynced(true);
        mDatabaseVotesForFourthRow.keepSynced(true);



        mFollow = (ImageButton) findViewById(R.id.followBtn);
        mFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProcessFollow = true;

                    mDatabaseFollowers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (mProcessFollow) {

                                if (!dataSnapshot.child(mPostKey).hasChild(mAuth.getCurrentUser().getUid())) {

                                    mDatabaseFollowers.child(mPostKey).child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                                    mDatabaseFollowing.child(mAuth.getCurrentUser().getUid()).child(mPostKey).setValue(mAuth.getCurrentUser().getUid());
                                    mProcessFollow = false;

                                }

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            }
        });

        // COUNT NUMBER OF FOLLOWERS
        mDatabaseFollowers.child(mPostKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mFollowersCount.setText(dataSnapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // COUNT NUMBER OF PPL I'M FOLLOWING
        mDatabaseFollowing.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mFollowingCount.setText(dataSnapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        // COUNT NUMBER OF POLLS
        mQueryPolls = mDatabase.orderByChild("uid").equalTo(mAuth.getCurrentUser().getUid());
        mQueryPolls.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPollsCount.setText(dataSnapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mUnfollow = (ImageButton) findViewById(R.id.unfollowBtn);
        mUnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }

        });


                mDatabaseUsers.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String post_name = (String) dataSnapshot.child("name").getValue();
                        String post_image = (String) dataSnapshot.child("image").getValue();

                        mUsername.setText(post_name);

                        Picasso.with(ProfileActivity.this).load(post_image).into(mCIV);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        // check if i'm a follower
        checkFollowers();

    }

    private void checkFollowers() {

       mDatabaseFollowers.child(mPostKey).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {

           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });
    }



    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Poll, LetterViewHolder> firebaseRecyclerAdapter = new  FirebaseRecyclerAdapter<Poll, LetterViewHolder>(

                Poll.class,
                R.layout.poll_row,
                LetterViewHolder.class,
                mQueryPolls


        ) {
            @Override
            protected void populateViewHolder(final LetterViewHolder viewHolder, final Poll model, int position) {

                final String post_key = getRef(position).getKey();

                viewHolder.setPoll_question(model.getPoll_question());
                viewHolder.setFirst_row_username(model.getFirst_row_username());
                viewHolder.setSecond_row_username(model.getSecond_row_username());
                viewHolder.setThird_row_username(model.getThird_row_username());
                viewHolder.setFourth_row_username(model.getFourth_row_username());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setFirst_row_userimg(getApplicationContext(), model.getFirst_row_userimg());
                viewHolder.setSecond_row_userimg(getApplicationContext(), model.getSecond_row_userimg());
                viewHolder.setThird_row_userimg(getApplicationContext(), model.getThird_row_userimg());
                viewHolder.setFourth_row_userimg(getApplicationContext(), model.getFourth_row_userimg());
                viewHolder.setName(model.getName());
                viewHolder.setCreated_date(model.getCreated_date());

                viewHolder.setFirstRowVoteBtn(post_key);
                viewHolder.setSecondRowVoteBtn(post_key);
                viewHolder.setThirdRowVoteBtn(post_key);
                viewHolder.setFourthRowVoteBtn(post_key);

                mDatabaseTotalVotes.child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.equals(post_key)) {
                            viewHolder.mTotalVoteCounter.setText(dataSnapshot.getChildrenCount() + "");

                        } else {
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                viewHolder.mFirstRowVoteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mProcessVote = true;


                        mDatabaseVotesForFirstRow.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(mProcessVote) {

                                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                                        mDatabaseVotesForFirstRow.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        mDatabaseTotalVotes.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        viewHolder.mSecondRowVoteBtn.setVisibility(View.VISIBLE);
                                        viewHolder.mThirdRowVoteBtn.setVisibility(View.VISIBLE);
                                        viewHolder.mFourthRowVoteBtn.setVisibility(View.VISIBLE);
                                        mProcessVote = false;
                                    }else {

                                        mDatabaseVotesForFirstRow.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                                        mDatabaseTotalVotes.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                                        viewHolder.mSecondRowVoteBtn.setVisibility(View.GONE);
                                        viewHolder.mThirdRowVoteBtn.setVisibility(View.GONE);
                                        viewHolder.mFourthRowVoteBtn.setVisibility(View.GONE);
                                        mProcessVote = false;

                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                });


                viewHolder.mSecondRowVoteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mProcessVote = true;

                        mDatabaseVotesForSecondRow.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(mProcessVote) {

                                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                                        mDatabaseVotesForSecondRow.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        mDatabaseTotalVotes.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        viewHolder.mFirstRowVoteBtn.setVisibility(View.VISIBLE);
                                        viewHolder.mThirdRowVoteBtn.setVisibility(View.VISIBLE);
                                        viewHolder.mFourthRowVoteBtn.setVisibility(View.VISIBLE);
                                        mProcessVote = false;
                                    }else {

                                        mDatabaseVotesForSecondRow.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                                        mDatabaseTotalVotes.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                                        viewHolder.mFirstRowVoteBtn.setVisibility(View.GONE);
                                        viewHolder.mThirdRowVoteBtn.setVisibility(View.GONE);
                                        viewHolder.mFourthRowVoteBtn.setVisibility(View.GONE);
                                        mProcessVote = false;

                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                });


                viewHolder.mThirdRowVoteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mProcessVote = true;

                        mDatabaseVotesForThirdRow.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(mProcessVote) {

                                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                                        mDatabaseVotesForThirdRow.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        mDatabaseTotalVotes.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        viewHolder.mFirstRowVoteBtn.setVisibility(View.VISIBLE);
                                        viewHolder.mSecondRowVoteBtn.setVisibility(View.VISIBLE);
                                        viewHolder.mFourthRowVoteBtn.setVisibility(View.VISIBLE);
                                        mProcessVote = false;
                                    }else {

                                        mDatabaseVotesForThirdRow.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                                        mDatabaseTotalVotes.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                                        viewHolder.mFirstRowVoteBtn.setVisibility(View.GONE);
                                        viewHolder.mSecondRowVoteBtn.setVisibility(View.GONE);
                                        viewHolder.mFourthRowVoteBtn.setVisibility(View.GONE);
                                        mProcessVote = false;

                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                });



                viewHolder.mFourthRowVoteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mProcessVote = true;

                        mDatabaseVotesForFourthRow.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(mProcessVote) {

                                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                                        mDatabaseVotesForFourthRow.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        mDatabaseTotalVotes.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        viewHolder.mFirstRowVoteBtn.setVisibility(View.VISIBLE);
                                        viewHolder.mSecondRowVoteBtn.setVisibility(View.VISIBLE);
                                        viewHolder.mThirdRowVoteBtn.setVisibility(View.VISIBLE);
                                        mProcessVote = false;
                                    }else {

                                        mDatabaseVotesForFourthRow.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                                        mDatabaseTotalVotes.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                                        viewHolder.mFirstRowVoteBtn.setVisibility(View.GONE);
                                        viewHolder.mSecondRowVoteBtn.setVisibility(View.GONE);
                                        viewHolder.mThirdRowVoteBtn.setVisibility(View.GONE);
                                        mProcessVote = false;

                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                });






                mDatabasePolls.child(post_key).addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String third_row = (String) dataSnapshot.child("third_row_username").getValue();
                        final String fourth_row = (String) dataSnapshot.child("fourth_row_username").getValue();

                        RelativeLayout third_card_row = (RelativeLayout) findViewById(R.id.third_card_relative_layout);
                        RelativeLayout fourth_card_row = (RelativeLayout) findViewById(R.id.fourth_card_relative_layout);

                        if (third_row != null) {

                            third_card_row.setVisibility(View.GONE);

                        } else {

                            third_card_row.setVisibility(View.VISIBLE);
                        }
                        if (fourth_row != null) {

                            fourth_card_row.setVisibility(View.GONE);
                        } else {

                            fourth_card_row.setVisibility(View.VISIBLE);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



                viewHolder.mChatBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent cardonClick = new Intent(ProfileActivity.this, CommentsActivity.class);
                        cardonClick.putExtra("heartraise_id", post_key );
                        startActivity(cardonClick);
                    }
                });

                viewHolder.mUserImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent cardonClick = new Intent(ProfileActivity.this, ProfileActivity.class);
                        cardonClick.putExtra("heartraise_id", post_key );
                        startActivity(cardonClick);
                    }
                });

                mQueryComments = mDatabaseComment.orderByChild("post_key").equalTo(post_key);
                mQueryComments.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.mCommentCount.setText(dataSnapshot.getChildrenCount() + "");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                // COUNT FIRST ROW VOTES
                mDatabaseVotesForFirstRow.child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.first_row_votecounter.setText(dataSnapshot.getChildrenCount() + "");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                // COUNT second ROW VOTES
                mDatabaseVotesForSecondRow.child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.second_row_votecounter.setText(dataSnapshot.getChildrenCount() + "");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                // COUNT THIRD ROW VOTES
                mDatabaseVotesForThirdRow.child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.third_row_votecounter.setText(dataSnapshot.getChildrenCount() + "");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                // COUNT FOURTH ROW VOTES
                mDatabaseVotesForFourthRow.child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.fourth_row_votecounter.setText(dataSnapshot.getChildrenCount() + "");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                // COUNT TOTAL VOTES
                mDatabaseTotalVotes.child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.total_vote_count.setText(dataSnapshot.getChildrenCount() + "");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

        };

        mPollsList.setAdapter(firebaseRecyclerAdapter);


    }



    public static class LetterViewHolder extends RecyclerView.ViewHolder {

        View mView;

        ImageView mChatBtn, mFirstRowVoteBtn, mSecondRowVoteBtn, mThirdRowVoteBtn,  mFourthRowVoteBtn;
        ImageButton mUserImg;
        DatabaseReference mDatabaseTotalVotes,mDatabaseVotesForFirstRow, mDatabaseVotesForSecondRow, mDatabaseVotesForThirdRow, mDatabaseVotesForFourthRow;
        FirebaseAuth mAuth;
        TextView mCommentCount, mTotalVoteCounter, first_row_votecounter, second_row_votecounter, third_row_votecounter, fourth_row_votecounter, total_vote_count;
        DatabaseReference mDatabase;
        ProgressBar mProgressBar;

        public LetterViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mAuth = FirebaseAuth.getInstance();
            mDatabaseTotalVotes = FirebaseDatabase.getInstance().getReference().child("Total_votes");
            mDatabaseTotalVotes.keepSynced(true);
            mDatabaseVotesForFirstRow = FirebaseDatabase.getInstance().getReference().child("First_row_votes");
            mDatabaseVotesForFirstRow.keepSynced(true);
            mDatabaseVotesForSecondRow = FirebaseDatabase.getInstance().getReference().child("Second_row_votes");
            mDatabaseVotesForSecondRow.keepSynced(true);
            mDatabaseVotesForThirdRow = FirebaseDatabase.getInstance().getReference().child("Third_row_votes");
            mDatabaseVotesForThirdRow.keepSynced(true);
            mDatabaseVotesForFourthRow = FirebaseDatabase.getInstance().getReference().child("Fourth_row_votes");
            mDatabaseVotesForFourthRow.keepSynced(true);
            mCommentCount = (TextView) mView.findViewById(R.id.commentCount);
            mTotalVoteCounter = (TextView) mView.findViewById(R.id.total_vote_count);
            first_row_votecounter = (TextView) mView.findViewById(R.id. first_row_votecounter);
            second_row_votecounter = (TextView) mView.findViewById(R.id.second_row_votecounter);
            third_row_votecounter = (TextView) mView.findViewById(R.id.third_row_votecounter);
            fourth_row_votecounter = (TextView) mView.findViewById(R.id.fourth_row_votecounter);
            total_vote_count = (TextView) mView.findViewById(R.id.total_vote_count);
            mChatBtn = (ImageView) mView.findViewById(R.id.chatBtn);
            mUserImg = (ImageButton) mView.findViewById(R.id.post_image);
            mFirstRowVoteBtn = (ImageView) mView.findViewById(R.id.first_row_voteBtn);
            mSecondRowVoteBtn = (ImageView) mView.findViewById(R.id.second_row_voteBtn);
            mThirdRowVoteBtn = (ImageView) mView.findViewById(R.id.third_row_voteBtn);
            mFourthRowVoteBtn = (ImageView) mView.findViewById(R.id.fourth_row_voteBtn);
            mProgressBar = (ProgressBar) mView.findViewById(R.id.progressBar);

        }

        public void setFirstRowVoteBtn(final String post_key) {

            mDatabaseVotesForFirstRow.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                        mFirstRowVoteBtn.setImageResource(R.drawable.ic_vote_red);
                        mSecondRowVoteBtn.setVisibility(View.GONE);
                        mThirdRowVoteBtn.setVisibility(View.GONE);
                        mFourthRowVoteBtn.setVisibility(View.GONE);
                    } else {
                        mFirstRowVoteBtn.setImageResource(R.drawable.ic_vote_blue);
                        mSecondRowVoteBtn.setVisibility(View.VISIBLE);
                        mThirdRowVoteBtn.setVisibility(View.VISIBLE);
                        mFourthRowVoteBtn.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        public void setSecondRowVoteBtn(final String post_key) {

            mDatabaseVotesForSecondRow.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                        mSecondRowVoteBtn.setImageResource(R.drawable.ic_vote_red);
                        mFirstRowVoteBtn.setVisibility(View.GONE);
                        mThirdRowVoteBtn.setVisibility(View.GONE);
                        mFourthRowVoteBtn.setVisibility(View.GONE);
                    } else {
                        mSecondRowVoteBtn.setImageResource(R.drawable.ic_vote_blue);
                        mFirstRowVoteBtn.setVisibility(View.VISIBLE);
                        mThirdRowVoteBtn.setVisibility(View.VISIBLE);
                        mFourthRowVoteBtn.setVisibility(View.VISIBLE);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        public void setThirdRowVoteBtn(final String post_key) {

            mDatabaseVotesForThirdRow.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                        mThirdRowVoteBtn.setImageResource(R.drawable.ic_vote_red);
                        mFirstRowVoteBtn.setVisibility(View.GONE);
                        mSecondRowVoteBtn.setVisibility(View.GONE);
                        mFourthRowVoteBtn.setVisibility(View.GONE);

                    } else {
                        mThirdRowVoteBtn.setImageResource(R.drawable.ic_vote_blue);
                        mFirstRowVoteBtn.setVisibility(View.VISIBLE);
                        mSecondRowVoteBtn.setVisibility(View.VISIBLE);
                        mFourthRowVoteBtn.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        public void setFourthRowVoteBtn(final String post_key) {

            mDatabaseVotesForFourthRow.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                        mFourthRowVoteBtn.setImageResource(R.drawable.ic_vote_red);

                    } else {

                        mFourthRowVoteBtn.setImageResource(R.drawable.ic_vote_blue);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        public void setPoll_question(String poll_question) {

            TextView pollQuiz = (TextView) mView.findViewById(R.id.poll_question);
            pollQuiz.setText(poll_question);
        }

        public void setFirst_row_username(String first_row_username) {

            TextView post_first_row_username = (TextView) mView.findViewById(R.id.first_row_username);
            post_first_row_username.setText(first_row_username);
        }

        public void setSecond_row_username(String Second_row_username) {

            TextView post_Second_row_username = (TextView) mView.findViewById(R.id.second_row_username);
            post_Second_row_username.setText(Second_row_username);
        }

        public void setThird_row_username(String third_row_username) {

            TextView post_third_row_username = (TextView) mView.findViewById(R.id.third_row_username);
            post_third_row_username.setText(third_row_username);
        }

        public void setFourth_row_username(String fourth_row_username) {

            TextView post_fourth_row_username = (TextView) mView.findViewById(R.id.fourth_row_username);
            post_fourth_row_username.setText(fourth_row_username);
        }

        public void setName(String name) {

            TextView post_name = (TextView) mView.findViewById(R.id.post_name);
            post_name.setText(name);
        }

        public void setCreated_date(String created_date) {

            TextView post_date = (TextView) mView.findViewById(R.id.date);
            post_date.setText(created_date);
        }

        public void setImage(final Context ctx, final String image) {
            //final ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
            final CircleImageView civ = (CircleImageView) mView.findViewById(R.id.post_image);

            Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(civ, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {


                    Picasso.with(ctx).load(image).into(civ);
                }
            });
        }

        public void setFirst_row_userimg(final Context ctx, final String first_row_userimg) {
            final ImageView first_row_userim = (ImageView) mView.findViewById(R.id.first_row_userimg);

            Picasso.with(ctx).load(first_row_userimg).networkPolicy(NetworkPolicy.OFFLINE).into(first_row_userim, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {


                    Picasso.with(ctx).load(first_row_userimg).into(first_row_userim);
                }
            });
        }

        public void setSecond_row_userimg(final Context ctx, final String second_row_userimg) {
            final ImageView first_row_useri = (ImageView) mView.findViewById(R.id.second_row_userimg);

            Picasso.with(ctx).load(second_row_userimg).networkPolicy(NetworkPolicy.OFFLINE).into(first_row_useri, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {


                    Picasso.with(ctx).load(second_row_userimg).into(first_row_useri);
                }
            });
        }

        public void setThird_row_userimg(final Context ctx, final String third_row_userimg) {
            final ImageView first_row_user = (ImageView) mView.findViewById(R.id.third_row_userimg);

            Picasso.with(ctx).load(third_row_userimg).networkPolicy(NetworkPolicy.OFFLINE).into(first_row_user, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {


                    Picasso.with(ctx).load(third_row_userimg).into(first_row_user);
                }
            });
        }

        public void setFourth_row_userimg(final Context ctx, final String fourth_row_userimg) {
            final ImageView first_row_use = (ImageView) mView.findViewById(R.id.fourth_row_userimg);

            Picasso.with(ctx).load(fourth_row_userimg).networkPolicy(NetworkPolicy.OFFLINE).into(first_row_use, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {


                    Picasso.with(ctx).load(fourth_row_userimg).into(first_row_use);
                }
            });
        }


    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
