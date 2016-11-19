package com.example.venkateshwar.tod;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.*;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;

    View header;

    static LearningCollection arrayList;
    ArrayList<Learning> cloudCopy;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    ImageButton gimme, all;
    TextView textView;
    DatabaseReference mDatabase;
    DatabaseReference mLearnings, mlastUpdated;
    //    SignInButton signInButton;
    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    private static FirebaseUser user;
    TextView uname;
    String username;

    static Date lastUpdated = new Date();
    Date mlast = new Date();

    void syncItOff(FirebaseUser tempuser) {
        if (tempuser != null) {
            Toast.makeText(MainActivity.this, "Trying to sync...", Toast.LENGTH_SHORT).show();
            user = tempuser;
            username = user.getDisplayName();
            DatabaseReference temp = mDatabase.child("users").child("nimda");

            mLearnings = mDatabase.child("users").child(user.getUid()).child("learnings");

            mlastUpdated = mDatabase.child("users").child(user.getUid()).child("lastUpdated");

            mlastUpdated.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mlast = dataSnapshot.getValue(Date.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mLearnings.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Toast.makeText(MainActivity.this, "Data fetched...", Toast.LENGTH_SHORT).show();
                    GenericTypeIndicator<ArrayList<Learning>> typeIndicator = new GenericTypeIndicator<ArrayList<Learning>>() {
                    };
                    ArrayList<Learning> cloudCopy = dataSnapshot.getValue(typeIndicator);
                    if(cloudCopy != null) {
                        for (Learning x : cloudCopy) {
                            arrayList.add(x);
                        }
                        Toast.makeText(MainActivity.this, "Data synced", Toast.LENGTH_SHORT).show();
                        arrayList.sort();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            // User is signed in
            if (temp == null)
                Toast.makeText(getApplicationContext(), "Logged in as " + user.getDisplayName() + mLearnings.toString(), Toast.LENGTH_LONG).show();
            uname.setText(user.getDisplayName());


        } else {
            // User is signed out
            uname.setText("");

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.nav_actionbar);
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);
        uname = (TextView) header.findViewById(R.id.uname);

        navigationView.setNavigationItemSelectedListener(this);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        gimme = (ImageButton) findViewById(R.id.button);
        all = (ImageButton) findViewById(R.id.button3);
        textView = (TextView) findViewById(R.id.textView);
        user = mAuth.getCurrentUser();
        arrayList = new LearningCollection();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                syncItOff(firebaseAuth.getCurrentUser());
            }
        };

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.webclientid))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d("AB", "Connection failed");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

//        syncCloud(mLearnings,arrayList);
        if (textView != null) {
            textView.setMovementMethod(new ScrollingMovementMethod());
            textView.setLongClickable(true);
            textView.setTextIsSelectable(true);
            textView.setFocusable(true);
            textView.setFocusableInTouchMode(true);
        }

        if (gimme != null) {
            gimme.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(getApplicationContext(), "Gimme one", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
        if (all != null) {
            all.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(getApplicationContext(), "Show All", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        if (all != null && textView != null) {
            all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TipListActivity.class);
                startActivity(intent);
                }
            });
        }

        if (gimme != null) {
            gimme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getATip();
                }
            });
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addATip();
            }
        });


    }

    void addATip() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter the tip");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                String s = input.getText().toString().trim();
                try {
                    if (s.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Oy! Write something.", Toast.LENGTH_LONG).show();
                        throw new Exception();
                    }
//                        for (Learning learning : arrayList) {
//                            if (learning.getData().equals(s)) {
//                                Toast.makeText(getApplicationContext(), "This one already exists.", Toast.LENGTH_LONG).show();
//                                throw new Exception();
//                            }
//                        }
                    arrayList.add(new Learning(s));
                    lastUpdated = new Date();

                    if (mLearnings != null) {
                        mlastUpdated.setValue(lastUpdated);
                        mLearnings.setValue(arrayList.getArrayList());
                        Log.d("AB:", "onClick: " + mLearnings.toString());
                        Log.d("AB:", "arrayList: " + arrayList.toString());
                    }

                    Log.d("AB", "Array List updated on " + lastUpdated.toString());
                } catch (Exception e) {
                    Log.e("Exception", "File write failed: " + e.toString());
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    boolean getATip() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        if (arrayList.size() > 0) {
            int x = (int) (Math.random() * arrayList.size());
            assert textView != null;
            textView.setText(arrayList.get(x).getData());
            return true;
        }

        return false;
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                Toast.makeText(getApplicationContext(), "Successfully Signed In", Toast.LENGTH_SHORT).show();
            } else {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(getApplicationContext(), "Couldn't sign in ", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }


    @Override
    protected void onStart() {
        mAuth.addAuthStateListener(mAuthListener);
        super.onStart();
        if (user != null) {
            uname.setText(user.getDisplayName());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item))
            return true;

        else
            return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_tips) {
            Intent intent = new Intent(MainActivity.this, TipListActivity.class);
            startActivity(intent);
        } else if(id == R.id.nav_account) {
            signIn();
        }
        return true;
    }
}
