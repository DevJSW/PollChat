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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private String post_name = null;
    private TextView mNoPostTxt;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private ImageView mCallBtn, mImg;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseComment;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabasePolls;
    private ProgressBar mProgressBar;
    private RecyclerView mPollsList;
    private FirebaseUser mCurrentUser;
    private StorageReference mStorage;
    private Uri mImageUri = null;
    private static int GALLERY_REQUEST =1;
    private Boolean mProcessLike = false;
    private DatabaseReference mDatabaseLike;

    private Query mQueryLetters;
    private Query mQueryComments;
    private Query mQueryLikes;


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
        mDatabaseComment = FirebaseDatabase.getInstance().getReference().child("Comments");
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar2);
        mPollsList = (RecyclerView) findViewById(R.id.letters_list);
        mPollsList.setLayoutManager(new LinearLayoutManager(this));
        mPollsList.setHasFixedSize(true);

        mDatabasePolls.keepSynced(true);
        mDatabaseUsers.keepSynced(true);
        mDatabaseLike.keepSynced(true);



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


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


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

            }

        };

        mPollsList.setAdapter(firebaseRecyclerAdapter);


    }



    public static class LetterViewHolder extends RecyclerView.ViewHolder {

        View mView;

        ImageView mChatBtn;
        DatabaseReference mDatabaseLike;
        FirebaseAuth mAuth;
        TextView mCommentCount, mLikeCount, mAnonymousText;
        DatabaseReference mDatabase;
        ProgressBar mProgressBar;

        public LetterViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mAuth = FirebaseAuth.getInstance();
            mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
            mDatabaseLike.keepSynced(true);
            mProgressBar = (ProgressBar) mView.findViewById(R.id.progressBar);

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
            final ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);

            Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {


                    Picasso.with(ctx).load(image).into(post_image);
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
            // Handle the camera action
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
