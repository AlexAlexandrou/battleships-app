package com.example.s5064183.battleships;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

public class GameActivity extends Activity {
    private Button btnPlay;
    private Button btnHorizontal;
    private Button btnVertical;
    private RadioButton rbtnCarrier;
    private RadioButton rbtnBattleship;
    private RadioButton rbtnCruiser;
    private RadioButton rbtnSubmarine;
    private RadioButton rbtnDestroyer;

    SetupView mSetupView;
    BattleshipGame mGame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game);
        mSetupView = (SetupView) findViewById(R.id.setupView);
        mGame = new BattleshipGame(10, 10, BattleshipGameBase.ShipData.CREATOR.newArray(5));


        btnPlay = findViewById(R.id.button_Start);
        btnPlay.setBackgroundResource(android.R.drawable.btn_default);

        // Set vertical button to yellow as it is the default one
        btnVertical = (Button)findViewById(R.id.btn_Vertical);
        btnVertical.setBackgroundColor(Color.YELLOW);

        btnHorizontal = (Button)findViewById(R.id.btn_Horizontal);
        btnHorizontal.setBackgroundResource(android.R.drawable.btn_default);

        rbtnCarrier = (RadioButton)findViewById(R.id.rbtn_Carrier);
        rbtnCarrier.setChecked(true);

        rbtnBattleship = (RadioButton)findViewById(R.id.rbtn_Battleship);
        rbtnCruiser = (RadioButton)findViewById(R.id.rbtn_Cruiser);
        rbtnSubmarine = (RadioButton)findViewById(R.id.rbtn_Submarine);
        rbtnDestroyer = (RadioButton)findViewById(R.id.rbtn_Destroyer);


    }

    // Set each button to yellow when each direction is choosen
    public void changeVer(View view){
        btnVertical.setBackgroundColor(Color.YELLOW);
        mSetupView.setPos(false);
        btnHorizontal.setBackgroundResource(android.R.drawable.btn_default);

    }
    public void changeHor(View view){
        btnHorizontal.setBackgroundColor(Color.YELLOW);
        mSetupView.setPos(true);
        btnVertical.setBackgroundResource(android.R.drawable.btn_default);
    }

    // Set each radio button to select its assigned ship
    public void getCarrier(View view){
        mSetupView.setShip(0);

        rbtnBattleship.setChecked(false);
        rbtnCruiser.setChecked(false);
        rbtnSubmarine.setChecked(false);
        rbtnDestroyer.setChecked(false);

    }

    public void getBattleship(View view){
        mSetupView.setShip(1);

        rbtnCarrier.setChecked(false);
        rbtnCruiser.setChecked(false);
        rbtnSubmarine.setChecked(false);
        rbtnDestroyer.setChecked(false);
    }

    public void getCruiser(View view){
        mSetupView.setShip(2);

        rbtnCarrier.setChecked(false);
        rbtnBattleship.setChecked(false);
        rbtnSubmarine.setChecked(false);
        rbtnDestroyer.setChecked(false);
    }

    public void getSubmarine(View view){
        mSetupView.setShip(3);

        rbtnCarrier.setChecked(false);
        rbtnBattleship.setChecked(false);
        rbtnCruiser.setChecked(false);
        rbtnDestroyer.setChecked(false);
    }

    public void getDestroyer(View view){
        mSetupView.setShip(4);

        rbtnCarrier.setChecked(false);
        rbtnBattleship.setChecked(false);
        rbtnCruiser.setChecked(false);
        rbtnSubmarine.setChecked(false);
    }

    /**
     *  Method that checks that all the ships are placed into the SetupView grid
     * @param view
     */
    public void getShips(View view){
        boolean shipPlaced = true;
        Intent startMatchIntent = new Intent(GameActivity.this, PlayActivity.class);

        for (int i = 0; i<5; i++){
            if (mSetupView.mGame.myShips[i].getLeft() == -1){
                shipPlaced = false;
                break;
            }
        }

        /*If the ships are placed correctly, get the ship data and proceed to PlayActivity and
         *if the user tries to continue without placing all ships, display informational message
         */
        if (shipPlaced == true){
            BattleshipGameBase.ShipData test = mSetupView.mGame.myShips[0];
            startMatchIntent.putExtra("ship0", test);

            BattleshipGameBase.ShipData test1 = mSetupView.mGame.myShips[1];
            startMatchIntent.putExtra("ship1", test1);

            BattleshipGameBase.ShipData test2 = mSetupView.mGame.myShips[2];
            startMatchIntent.putExtra("ship2", test2);

            BattleshipGameBase.ShipData test3 = mSetupView.mGame.myShips[3];
            startMatchIntent.putExtra("ship3", test3);

            BattleshipGameBase.ShipData test4 = mSetupView.mGame.myShips[4];
            startMatchIntent.putExtra("ship4", test4);
            finish();
            GameActivity.this.startActivity(startMatchIntent);
        }
        else{
            Toast.makeText(this, "You must place all ships in order to proceed",
                    Toast.LENGTH_LONG).show();
        }
    }

}
