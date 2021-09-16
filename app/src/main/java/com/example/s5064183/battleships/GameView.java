package com.example.s5064183.battleships;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.DKGRAY;
import static android.graphics.Color.RED;

public class GameView extends View implements BattleshipGameBase.BattleshipGameListener<BattleshipGame> {

    int countSunk = 0;

    private GestureDetector mGestureDetector;
    public BattleshipGame mGame;
    private Paint mGridPaint, mHitPaint,mMissPaint, mSinkPaint, mBGPaint;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise();
        mGame = new BattleshipGame(10, 10, BattleshipGameBase.ShipData.CREATOR.newArray(5));
        mGame.addOnGameChangeListener(this);
        mGestureDetector = new GestureDetector(context, new GameGestureListener());
    }

    private void initialise() {
        mGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGridPaint.setStyle(Paint.Style.FILL);
        mGridPaint.setColor(BLACK);
        mHitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHitPaint.setStyle(Paint.Style.FILL);
        mHitPaint.setColor(RED);
        mMissPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMissPaint.setStyle(Paint.Style.FILL);
        mMissPaint.setColor(0xffcccccc);
        mSinkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSinkPaint.setStyle(Paint.Style.FILL);
        mSinkPaint.setColor(BLUE);
        mBGPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBGPaint.setStyle(Paint.Style.FILL);
        mBGPaint.setColor(DKGRAY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        double SEPARATOR_RATIO = 0.05;
        float diameterX, diameterY, chosenDiameter, separatorSize, canvasWidth, canvasHeight;
        int rowCount, colCount, noOfColSeparators, noOfRowSeparators, startShip, enemyShip;
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

                mGame.placedCellEnemy(col,row);//store the enemy ships positions into the array
                startShip = mGame.placedCell(col, row);

                //Checks if any enemy ship is sunk
                mGame.checkIfSunk();

                if (startShip == 1) {
                    paint = mHitPaint;
                }
                else if (startShip==2){
                    paint = mMissPaint;
                }
                else if (startShip == 3){

                    paint = mSinkPaint;
                }
                else{
                    paint = mBGPaint;
                }

                // Calculate the co-ordinates of the cell
                float cx = (chosenDiameter + separatorSize)*col;
                float cy = (chosenDiameter + separatorSize)*row;
                canvas.drawRect(cx, cy, cx+chosenDiameter, cy+chosenDiameter, paint);

            }
        }
    }

    public boolean onTouchEvent(MotionEvent ev){

        boolean r = this.mGestureDetector.onTouchEvent(ev);

        return super.onTouchEvent(ev) || r;
    }

    class GameGestureListener extends GestureDetector.SimpleOnGestureListener{

        int [][] hitPositions = new int[10][10];
        double SEPARATOR_RATIO = 0.05;
        float diameterX, diameterY, chosenDiameter;
        int rowCount, colCount, noOfColSeparators, noOfRowSeparators;

        public boolean onDown(MotionEvent ev){

            return true;
        }

        public boolean onSingleTapUp(MotionEvent ev){



            rowCount = mGame.getRows();
            colCount = mGame.getColumns();

            diameterX = (float) (getWidth() / (colCount + noOfColSeparators * SEPARATOR_RATIO));
            diameterY = (float) (getHeight() / (rowCount + noOfRowSeparators * SEPARATOR_RATIO));

            if (diameterX < diameterY) {
                chosenDiameter = diameterX;
            } else {
                chosenDiameter = diameterY;
            }

            if(countSunk < 17){
                mGame.validShot = false;
                int x = (int)ev.getX();
                int y = (int)ev.getY();

                //get the grid position of the touch
                int touchPosX = (int) (x/((chosenDiameter)));
                int touchPosY = (int) (y/((chosenDiameter)));

                //check that the player shoots inside the grid and the position they shoot has not
                //already been shot at before.
                if (touchPosX<10 && touchPosX>=0 && touchPosY<10 && touchPosY>=0 && hitPositions[touchPosX][touchPosY] !=1){

                    //if the shot is in a valid position make placement true
                    mGame.validShot = true;
                    hitPositions[touchPosX][touchPosY] = 1;
                    mGame.currentGuessAt(touchPosX, touchPosY);
                    if (mGame.currentGuessAt(touchPosX,touchPosY) == 1){
                        countSunk = countSunk+1;
                    }
                    invalidate();
                    //change the turn when the player shoots at the enemy grid.
                    mGame.setTurn();
                    return true;
                }
                else{
                    return false;
                }
            }
            else{
                return true;
            }

        }

    }

    @Override
    public void onGameChanged(BattleshipGame game, int column, int row){

        invalidate();
    }

}
