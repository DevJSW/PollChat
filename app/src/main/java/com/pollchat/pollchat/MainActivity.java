package com.pollchat.pollchat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private String post_name = null;
    String post_key = null;
    private String uid_key = null;
    private TextView mNoPostTxt;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private ImageView mCallBtn, mImg;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseComment, mDatabaseFollowing;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabasePolls;
    private ProgressBar mProgressBar;
    private RecyclerView mPollsList;
    private String user_uid = null;
    private Uri mImageUri = null;
    private static int GALLERY_REQUEST =1;
    private Boolean mProcessVote = false;
    private DatabaseReference mDatabaseTotalVotes;
    private DatabaseReference mDatabaseVotesForFirstRow;
    private DatabaseReference mDatabaseVotesForSecondRow;
    private DatabaseReference mDatabaseVotesForThirdRow;
    private DatabaseReference mDatabaseVotesForFourthRow;

    private Query mQueryFollowing;
    private Query mQueryComments;
    private Query mQueryUidInPoll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mImg = (ImageView) findViewById(R.id.post_userimg);
       // mNoPostTxt = (TextView) findViewById(R.id.noPostTxt);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent cardonClick = new Intent(MainActivity.this, CreatepollchatActivity.class);
                startActivity(cardonClick);

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabasePolls = FirebaseDatabase.getInstance().getReference().child("Polls");
        mDatabaseFollowing = FirebaseDatabase.getInstance().getReference().child("Following");
        mDatabaseComment = FirebaseDatabase.getInstance().getReference().child("Comments");
        mDatabaseTotalVotes = FirebaseDatabase.getInstance().getReference().child("Total_Votes");
        mDatabaseVotesForFirstRow = FirebaseDatabase.getInstance().getReference().child("First_row_votes");
        mDatabaseVotesForSecondRow = FirebaseDatabase.getInstance().getReference().child("Second_row_votes");
        mDatabaseVotesForThirdRow = FirebaseDatabase.getInstance().getReference().child("Third_row_votes");
        mDatabaseVotesForFourthRow = FirebaseDatabase.getInstance().getReference().child("Fourth_row_votes");
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar2);
        mPollsList = (RecyclerView) findViewById(R.id.letters_list);
        mPollsList.setLayoutManager(new LinearLayoutManager(this));
        mPollsList.setHasFixedSize(true);

        mDatabaseFollowing.keepSynced(true);
        mDatabasePolls.keepSynced(true);
        mDatabaseUsers.keepSynced(true);
        mDatabaseTotalVotes.keepSynced(true);
        mDatabaseVotesForFirstRow.keepSynced(true);
        mDatabaseVotesForSecondRow.keepSynced(true);
        mDatabaseVotesForThirdRow.keepSynced(true);
        mDatabaseVotesForFourthRow.keepSynced(true);

        // show polls from ppl i'm following
        mQueryFollowing = mDatabaseFollowing.child(mAuth.getCurrentUser().getUid()).orderByChild("following_post_key").equalTo(post_key);

        mDatabaseUsers.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_image = (String) dataSnapshot.child("image").getValue();
                post_name = (String) dataSnapshot.child("name").getValue();

                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                View hView =  navigationView.getHeaderView(0);

                ImageView nav_user = (ImageView)hView.findViewById(R.id.nav_userImg);
                TextView nav_username = (TextView)hView.findViewById(R.id.nav_username);

                nav_username.setText(post_name);

                Picasso.with(MainActivity.this).load(post_image).into(nav_user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        checkUserExists();
        checkForNetwork();

    }


    private void checkForNetwork() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ninfo = cm.getActiveNetworkInfo();

        if (ninfo != null && ninfo.isConnected()) {


        } else {


            Toast.makeText(MainActivity.this, "You are not connected to Internet... Please Enable Internet!", Toast.LENGTH_LONG)
                    .show();

        }
    }

    private void checkUserExists() {

        mProgressBar.setVisibility(View.VISIBLE);
        final String user_id = mAuth.getCurrentUser().getUid();

        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (!dataSnapshot.hasChild(user_id)) {

                    mProgressBar.setVisibility(View.GONE);

                    Intent intent = new Intent(MainActivity.this, EditActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                }else {

                    mProgressBar.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                mProgressBar.setVisibility(View.GONE);
            }
        });
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



    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Poll, LetterViewHolder> firebaseRecyclerAdapter = new  FirebaseRecyclerAdapter<Poll, LetterViewHolder>(

                Poll.class,
                R.layout.poll_row,
                LetterViewHolder.class,
                mDatabasePolls


        ) {
            @Override
            protected void populateViewHolder(final LetterViewHolder viewHolder, final Poll model, int position) {

                post_key = getRef(position).getKey();

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

                mDatabasePolls.child(post_key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        uid_key = (String) dataSnapshot.child("uid").getValue();
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
                                                    mProcessVote = false;
                                                }else {

                                                    mDatabaseVotesForFirstRow.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                                                    mDatabaseTotalVotes.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
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
                                        mProcessVote = false;
                                    }else {

                                        mDatabaseVotesForSecondRow.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                                        mDatabaseTotalVotes.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
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
                                        mProcessVote = false;
                                    }else {

                                        mDatabaseVotesForThirdRow.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                                        mDatabaseTotalVotes.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
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
                                        mProcessVote = false;
                                    }else {

                                        mDatabaseVotesForFourthRow.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                                        mDatabaseTotalVotes.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
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

                mDatabaseVotesForFourthRow.child(post_key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(mProcessVote) {

                            if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                                mDatabaseVotesForFourthRow.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                mDatabaseTotalVotes.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                mProcessVote = false;
                            }else {

                                mDatabaseVotesForFourthRow.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                                mDatabaseTotalVotes.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                                mProcessVote = false;

                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

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

                // open comments activity
                viewHolder.mChatBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent cardonClick = new Intent(MainActivity.this, CommentsActivity.class);
                        cardonClick.putExtra("heartraise_id", post_key );
                        startActivity(cardonClick);
                    }
                });


                //open profile page through post user image
                viewHolder.mCIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent cardonClick = new Intent(MainActivity.this, MyProfileActivity.class);
                        cardonClick.putExtra("heartraise_id", post_key );
                        cardonClick.putExtra("uid_key", uid_key );
                        startActivity(cardonClick);
                    }
                });

                // count total # of comments and display on post
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
        CircleImageView mCIV;
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
            mChatBtn = (ImageView) mView.findViewById(R.id.chatBtn);
            mCIV = (CircleImageView) mView.findViewById(R.id.post_image);
            //mUserImg = (ImageButton) mView.findViewById(R.id.post_image);
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

                    } else {
                        mFirstRowVoteBtn.setImageResource(R.drawable.ic_vote_blue);

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

                    } else {
                        mSecondRowVoteBtn.setImageResource(R.drawable.ic_vote_blue);


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


                    } else {
                        mThirdRowVoteBtn.setImageResource(R.drawable.ic_vote_blue);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {

            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("heartraise_id", post_key );
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }else if (id == R.id.nav_discover) {

            Intent intent = new Intent(MainActivity.this, DiscoverPeopleActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else if (id == R.id.nav_gallery) {

            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
