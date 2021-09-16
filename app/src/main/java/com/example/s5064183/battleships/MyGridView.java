package com.example.s5064183.battleships;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.DKGRAY;

public class MyGridView extends View implements BattleshipGameBase.BattleshipGameListener<BattleshipGame>{
    int countSunk = 0;


    int startShip; // variable to decide what colour each cell will be.
    public BattleshipGame mGame;
    private Paint mGridPaint, mHitPaint,mMissPaint, mShipPaint, mBGPaint;

    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise();
        mGame = new BattleshipGame(10, 10, BattleshipGameBase.ShipData.CREATOR.newArray(5));
        mGame.addOnGameChangeListener(this);
    }

    private void initialise() {
        mGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGridPaint.setStyle(Paint.Style.FILL);
        mGridPaint.setColor(BLACK);
        mHitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHitPaint.setStyle(Paint.Style.FILL);
        mHitPaint.setColor(0xffff0000);
        mMissPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMissPaint.setStyle(Paint.Style.FILL);
        mMissPaint.setColor(0xffcccccc);
        mShipPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShipPaint.setStyle(Paint.Style.FILL);
        mShipPaint.setColor(BLUE);
        mBGPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBGPaint.setStyle(Paint.Style.FILL);
        mBGPaint.setColor(DKGRAY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        double SEPARATOR_RATIO = 0.05;
        float diameterX, diameterY, chosenDiameter, separatorSize;
        int rowCount, colCount, noOfColSeparators, noOfRowSeparators;
        Paint paint;

        rowCount = mGame.getRows();
        colCount = mGame.getColumns();

        noOfColSeparators = colCount + 1;
        noOfRowSeparators = rowCount + 1;

        diameterX = (float) (getWidth() / (colCount + noOfColSeparators * SEPARATOR_RATIO));
        diameterY = (float) (getHeight() / (rowCount + noOfRowSeparators * SEPARATOR_RATIO));

        if (diameterX < diameterY) {
            chosenDiameter = diameterX;
        } else {
            chosenDiameter = diameterY;
        }
        separatorSize = (float) (chosenDiameter * SEPARATOR_RATIO);


        for (int col=0; col<colCount; col++)
        {
            for(int row=0; row<rowCount; row++)
            {

                mGame.shipLocation(col,row,mGame.getShips());

                //If it's the computer's turn, makes move and changes turns again to the player
                if (mGame.getTurn() == false){
                    int shoot = mGame.onEnemyShot();
                    countSunk = countSunk + shoot;
                    mGame.setTurn();
                }
                startShip = mGame.placeOnGrid(col, row);
                // Choose the correct colour for each cell
                if (startShip == 1) {
                    paint = mHitPaint;
                }
                else if (startShip==2){
                    paint = mMissPaint;
                }
                else if (startShip == 3){

                    paint = mShipPaint;
                }
                else{
                    paint = mBGPaint;
                }

                float cx = (chosenDiameter + separatorSize)*col;
                float cy = (chosenDiameter + separatorSize)*row;
                canvas.drawRect(cx, cy, cx+chosenDiameter, cy+chosenDiameter, paint);

            }
        }

    }

    //Replace mGame with SetupView mGame which contains the updated friendly ships
    public void setGame(BattleshipGame newGame){
        if (mGame != newGame){
            mGame.removeOnGameChangeListener(this);
            mGame = newGame;
            newGame.addOnGameChangeListener(this);
            invalidate();
        }
    }

    @Override
    public void onGameChanged(BattleshipGame game, int column, int row){

        invalidate();
    }
}
