package com.example.s5064183.battleships;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class SetupView extends View implements BattleshipGameBase.BattleshipGameListener<BattleshipGame> {

    public boolean checkHor;
    public int currentShip;


    public GestureDetector mGestureDetector;
    public BattleshipGame mGame;
    private int mBGColor, mShipColor;
    private Paint mShipPaint, mBGPaint;

    public SetupView(Context context, AttributeSet attrs) {
        super(context, attrs);



        mGame = new BattleshipGame(10, 10, BattleshipGameBase.ShipData.CREATOR.newArray(5));
        mGame.addOnGameChangeListener(this);
        mGestureDetector = new GestureDetector(context, new SetupGestureListener());

        mBGPaint = new Paint();
        mShipPaint = new Paint();

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SetupView,0,0);
        try {
            mBGColor = a.getInteger(R.styleable.SetupView_gridColor,0);
            mShipColor = a.getInteger(R.styleable.SetupView_shipColor,0);
        }
        finally {
            a.recycle();
        }

    }

    /**Replaces game object from BattleshipGame with the array set in the GameActivity
     *
     * @param newGame game object from GameActivity
     */
    public void setGame(BattleshipGame newGame){
        if (mGame != newGame){
            mGame.removeOnGameChangeListener(this);
            mGame = newGame;
            newGame.addOnGameChangeListener(this);
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        double SEPARATOR_RATIO = 0.05;
        float diameterX, diameterY, chosenDiameter, separatorSize;
        int rowCount, colCount, noOfColSeparators, noOfRowSeparators, startShip;
        Paint paint;

        rowCount = mGame.getRows();
        colCount = mGame.getColumns();

        noOfColSeparators = colCount + 1;
        noOfRowSeparators = rowCount + 1;

        // Given the no of rows and cols, and the screen size, which choice of diameter
        // will best fit everything onto the screen
        diameterX = (float) (getWidth() / (colCount + noOfColSeparators * SEPARATOR_RATIO));
        diameterY = (float) (getHeight() / (rowCount + noOfRowSeparators * SEPARATOR_RATIO));

        // Choose the smallest of the two and save that in the variable chosenDiameter
        if (diameterX < diameterY) {
            chosenDiameter = diameterX;
        } else {
            chosenDiameter = diameterY;
        }
        separatorSize = (float) (chosenDiameter * SEPARATOR_RATIO);




        mBGPaint.setStyle(Paint.Style.FILL);
        mBGPaint.setAntiAlias(true);
        mBGPaint.setColor(mBGColor);

        mShipPaint.setStyle(Paint.Style.FILL);
        mShipPaint.setAntiAlias(true);
        mShipPaint.setColor(mShipColor);

        for (int col = 0; col < colCount; col++) {
            for (int row = 0; row < rowCount; row++) {

                startShip = mGame.placeOnGrid(col, row);

                // Choose the correct colour for the cell
                if (startShip == 1) {
                    paint = mShipPaint;
                }
                else{
                   paint = mBGPaint;
                }


                // Calculate the co-ordinates of the cell
                float cx = (chosenDiameter + separatorSize) * col;
                float cy = (chosenDiameter + separatorSize) * row;
                canvas.drawRect(cx, cy, cx+chosenDiameter, cy+chosenDiameter, paint);

            }
        }
    }
    //checks the direction of the ship based on which button is pressed
    public void setPos(boolean pos){
        checkHor = pos;
    }

    //checks which ship will be placed based on the one selected in GameActivity
    public void setShip(int pShip){
        currentShip = pShip;
    }

    public boolean onTouchEvent(MotionEvent ev){

        boolean r = this.mGestureDetector.onTouchEvent(ev);

        return super.onTouchEvent(ev) || r;
    }


    class SetupGestureListener extends GestureDetector.SimpleOnGestureListener{
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

            // Choose the smallest of the two and save that in the variable chosenDiameter
            if (diameterX < diameterY) {
                chosenDiameter = diameterX;
            } else {
                chosenDiameter = diameterY;
            }


            int touchPosX;
            int touchPosY;
            int x;
            int y;



            x = (int)ev.getX();
            y = (int)ev.getY();

            touchPosX = (int) (x/((chosenDiameter)));
            touchPosY = (int) (y/((chosenDiameter)));


            mGame.overwriteShip(currentShip,checkHor);

            //Checks if the new ship placement is valid
            if(mGame.checkShips(touchPosX,touchPosY,currentShip) == true){
                //Overwrites the details of the ship if it was placed before.
                mGame.myShips[currentShip] = mGame.placeShips(touchPosX,touchPosY,currentShip,checkHor);
            }

            invalidate();

            return true;

        }

    }
    @Override
    public void onGameChanged(BattleshipGame game, int column, int row){

        invalidate();
    }

}
