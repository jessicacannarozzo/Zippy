package divideandconquer.zippy;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import divideandconquer.zippy.models.Game;
import divideandconquer.zippy.models.ListItem;
import divideandconquer.zippy.models.User;

/**
 * Created by geoff on 2017-10-10.
 */

public class GameModeActivity extends BaseActivity {

    private static final String TAG = "GameModeActivity";
    private static final String REQUIRED = "Required";

    public static final String EXTRA_POST_KEY = "post_key";

    private Chronometer mChrono;
    private Button mStartButton;
    private ValueEventListener mGameModeListener;
    private String listKey;
    private DatabaseReference mDatabase;
    private DatabaseReference mGameDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_mode);

        listKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (listKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        mChrono = (Chronometer) findViewById(R.id.chronometer);
        mStartButton = findViewById(R.id.start_chrono);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGame();
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mGameDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("games").child(listKey);

    }

    @Override
    public void onStart() {
        super.onStart();

        ValueEventListener gameModeListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Game game = dataSnapshot.getValue(Game.class);
                if(game == null) {
                    mStartButton.setText("START");
                } else {

                    DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
                    connectedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            double offset = snapshot.getValue(Double.class);
                            long estimatedServerTimeMs = System.currentTimeMillis() + (long) offset;
                            mChrono.setBase(SystemClock.elapsedRealtime() - (estimatedServerTimeMs - game.getStartTimeLong()));
                            mChrono.start();
                            mStartButton.setVisibility(View.INVISIBLE);

                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            System.err.println("Listener was cancelled");
                        }
                    });
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(GameModeActivity.this, "Failed to load TodoList.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mGameDatabaseReference.addValueEventListener(gameModeListener);

        mGameModeListener = gameModeListener;


    }

    private void newGame() {
        Game game = new Game();
        game.active = true;
        game.uid = FirebaseAuth.getInstance().getUid();
        mGameDatabaseReference.setValue(game);

    }




}
