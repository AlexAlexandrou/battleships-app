package com.example.s5064183.battleships;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class PlayActivity extends Activity implements  BattleshipGameBase.BattleshipGameListener<BattleshipGame> {

    private MyGridView mMyGridView;
    private GameView mGameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        //Get the ship data from GameActivity and store them in a new ShipData array (ships)
        BattleshipGameBase.ShipData[] ships = BattleshipGameBase.ShipData.CREATOR.newArray(5);
        Bundle gameData = getIntent().getExtras();

        BattleshipGameBase.ShipData listobj = gameData.getParcelable("ship0");
        ships[0] = listobj;

        BattleshipGameBase.ShipData listobj1 = gameData.getParcelable("ship1");
        ships[1] = listobj1;

        BattleshipGameBase.ShipData listobj2 = gameData.getParcelable("ship2");
        ships[2] = listobj2;

        BattleshipGameBase.ShipData listobj3 = gameData.getParcelable("ship3");
        ships[3] = listobj3;

        BattleshipGameBase.ShipData listobj4 = gameData.getParcelable("ship4");
        ships[4] = listobj4;


        //Create a new game object that replaces the one of MyGridView using the setGame method
        BattleshipGame mBGame = new BattleshipGame(10,10,ships);
        mMyGridView = findViewById(R.id.myGridView);
        mMyGridView.setGame(mBGame);



    }

    //Method that changes the turn when the enemyGridView is clicked
    public void turns(View view){
        Intent endMatchIntent = new Intent(PlayActivity.this, MainActivity.class);
        mMyGridView = findViewById(R.id.myGridView);
        mGameView = findViewById(R.id.gameView);

        //change the turn only when the player makes a valid shot
        if (mGameView.mGame.validShot == true){
            mMyGridView.mGame.turn = false;
        }

        //If you win the game or lose, display the appropriate message and return back to Main Menu
        if (mGameView.countSunk >= 17){
            Toast.makeText(this, "You are the winner!You sunk the enemy fleet!",
                    Toast.LENGTH_LONG).show();
            finish();
            startActivity(endMatchIntent);
        }
        else if(mMyGridView.countSunk >=17){
            Toast.makeText(this, "You have lost the game. Your fleet was destroyed.",
                    Toast.LENGTH_LONG).show();
            finish();
            startActivity(endMatchIntent);
        }
        else{
            mMyGridView.invalidate();
            mGameView.invalidate();
        }

    }

    public void onGameChanged(BattleshipGame game, int column, int row) {

    }
}
