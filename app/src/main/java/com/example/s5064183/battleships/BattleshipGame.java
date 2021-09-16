package com.example.s5064183.battleships;

import java.util.Random;


public class BattleshipGame extends BattleshipGameBase<BattleshipGameBase.ShipData> {

    boolean validShot;// Variable used to check if a shot is in a valid position
    boolean turn = true; // User plays first.


    ShipData[] enemyShips= randomPlaceShips();

    /**Fill the friendly ships array with data to be changed later. Left position is assigned to -1
     * initially and will be overwritten later.
     */
    ShipData[] myShips = new ShipData[] {
            new ShipData(5 , false , -1 , 3 ),
            new ShipData(4 , false , -1 , 0 ),
            new ShipData(3 , false , -1 , 6 ),
            new ShipData(3 , false , -1 , 6 ),
            new ShipData(2 , false , -1 , 9 )
    };

    int [][] myGrid;
    int [][] placeGrid;//Grid to setup ships.
    int[][] enemyGrid;
    int[][] guessGrid;// Grid where the player will shoot at

    protected BattleshipGame(int columns, int rows, ShipData[] ships){
        super (columns, rows, ships);

        guessGrid = new int[columns][rows];
        enemyGrid = new int[columns][rows];
        myGrid = new int[columns][rows];
        placeGrid = new int[columns][rows];

    }

    /**checks whether the current friendly ship overlaps with another one or is outside the grid
     *
     * @param column
     * @param row
     * @param i
     * @return validPlacement
     */
    public boolean checkShips(int column, int row, int i){
        int[] shipSize = new int[]{5,4,3,3,2};
        boolean validPlacement;
        validPlacement=true;

        if (myGrid[column][row] == 1){
            validPlacement = false;
        }
        else{
            for (int j=0; j<shipSize[i]; j++){

                //Check if ship position is outside the grid
                if (myShips[i].isHorizontal() == true){
                    if(column+j >=10){
                        validPlacement = false;
                        break;
                    }
                    else if(myGrid[column+j][row] == 1){
                        validPlacement = false;
                        break;
                    }
                }
                else if (myShips[i].isHorizontal() == false){
                    if(row+j >=10){
                        validPlacement = false;
                        break;
                    }
                    else if(myGrid[column][row+j] == 1){
                        validPlacement = false;
                        break;
                    }
                }
            }
        }

        return validPlacement;
    }

    /**Method that overwrites a ship that was already placed before
     *
     * @param i the kind of ship
     * @param pos
     */
    public void overwriteShip(int i, boolean pos){
        int[] shipSizes = new int[]{5,4,3,3,2};
        if (myShips[i].getLeft() != -1){ // check that ship was placed before
            for (int j=0; j<myShips[i].getSize(); j++){
                if(myShips[i].isHorizontal() == true){
                    myGrid[myShips[i].getLeft()+j][myShips[i].getTop()]=0;
                }
                else if(myShips[i].isHorizontal() == false){
                    myGrid[myShips[i].getLeft()][myShips[i].getTop()+j]=0;
                }
                //Removes same ship from location
            }
            myShips[i] = new ShipData(shipSizes[i],pos,-1,-1);
        }
        else{
            myShips[i] = new ShipData(myShips[i].getSize(),pos,-1,-1);
        }
    }

    // Method that gets the value of the cell for the player's array grid.
    public int placeOnGrid (int column, int row){

        return myGrid[column][row];
    }


    /**Places the selected ship at the position the user touches and stores it into the ships array
     *
     * @param column
     * @param row
     * @param i the kind of ship
     * @param pos if it is horizontal or vertical
     * @return myShips[i]
     */
     public ShipData placeShips(int column, int row,int i, boolean pos){
        int[]shipSizes = new int[]{5,4,3,3,2};

        myShips[i] = new BattleshipGameBase.ShipData(shipSizes[i],pos,column,row);

        myGrid[column][row]=1;
        //Marks the cells for the ship to be placed
        for(int j=myShips[i].getSize()-1; j>0; j--){

            if(myShips[i].isHorizontal() == true){
                myGrid[column+j][row]=1;
            }
            else if(myShips[i].isHorizontal() == false){
                myGrid[column][row+j]=1;
            }
        }

        return myShips[i];
    }

    /**Determine which cells will display the friendly ships in the PlayActivity
     *
     * @param column
     * @param row
     * @param gShips Ships array that contain the placed ships in SetupView
     */
    public void shipLocation (int column, int row, ShipData[] gShips){
        for (int i = 0; i<5; i++){
            //checks that the ship was placed by the user in the Setup View
            if (gShips[i].getLeft() != -1){

                //checks that the cell is not already marked as a hit or miss
                if (myGrid[column][row] !=1 && myGrid[column][row] != 2){
                    if (column == gShips[i].getLeft() && row == gShips[i].getTop()){
                        myGrid[column][row]=3;
                    }
                    //Checks if the cell is part of a ship location
                    for (int size=0; size<gShips[i].getSize(); size++){
                        if (gShips[i].isHorizontal() == true && column-size == gShips[i].getLeft()
                                && row == gShips[i].getTop()){

                            myGrid[column][row]=3;
                        }
                        else if (gShips[i].isHorizontal() == false && column == gShips[i].getLeft()
                                && row-size == gShips[i].getTop() ){

                            myGrid[column][row]=3;
                        }
                    }
                }


            }
        }
    }

    /**Method that allows the enemy AI to shoot at the friendly grid
     *
     * @return hitShip
     */
    public int onEnemyShot(){
        Random rand = new Random();
        int hitShip = 0;
        int randomCol;
        int randomRow;

        // Set random location to shoot as long as it was not shot before
        do {
            randomCol = rand.nextInt(10);
            randomRow = rand.nextInt(10);
        }while(myGrid[randomCol][randomRow] == 1 || myGrid[randomCol][randomRow] == 2);

        //Mark the shot location as hit if it hits a friendly ship or miss if it misses
        if (myGrid[randomCol][randomRow] == 3){
            myGrid[randomCol][randomRow] = 1;
            hitShip = 1;
        }
        else{
            myGrid[randomCol][randomRow] = 2;
        }
        return hitShip; // add its value to the total hits in MyGridView
    }

    //Change the turn every time the enemy makes a shot. True for user and false for enemy
    public void setTurn(){
        if(turn == true){
            turn = false;
        }
        else if (turn == false){
            turn = true;
        }
    }

    //Get the value of the turn
    public boolean getTurn(){

        return turn;
    }


    /**Method that randomly assigns positions for the enemy ships.
     *
     * @return The enemy ships array
     */
    private ShipData[] randomPlaceShips(){
        int[] shipSize = new int[]{5,4,3,3,2};
        enemyShips = new ShipData[5];
        int shipCol;
        int shipRow;
        Random rand = new Random();
        boolean horizontal;
        boolean overlapX;
        boolean overlapY;
        boolean overlap;
        boolean validPlacement;

        for (int i=0; i<shipSize.length; i++){
            //reassign position to the enemy ship if it does not have a valid placement or overlaps
            do{
                validPlacement = true;
                shipCol = rand.nextInt(10);
                shipRow = rand.nextInt(10);
                horizontal = rand.nextBoolean();
                enemyShips[i] = new BattleshipGameBase.ShipData(shipSize[i],horizontal,shipCol,shipRow);

                //Checks that the enemy ship is placed outside the grid
                if (enemyShips[i].getTop()>=10 || enemyShips[i].getBottom()>=10 || enemyShips[i].getLeft()>=10 || enemyShips[i].getRight()>=10){
                    validPlacement = false;
                }

                if (i>0){
                    for (int j=0; j<i; j++){
                        //Checks if the current ship overlaps with an already placed one or is in
                        //the perimeter cells of that ship.
                        overlapX = ( enemyShips[i].getRight() >= enemyShips[j].getLeft()-1
                                && enemyShips[i].getLeft() <= enemyShips[j].getRight()+1 );

                        overlapY = ( enemyShips[i].getBottom() >= enemyShips[j].getTop()-1
                                && enemyShips[i].getTop() <= enemyShips[j].getBottom()+1 );

                        overlap = (overlapX && overlapY);

                        if (overlap == true){
                            validPlacement = false;
                        }
                    }
                }

            }while (validPlacement == false);

        }
        return enemyShips;
    }




    @Override
    //Determines the value of the cells that the user shoots at
    public int currentGuessAt(int column, int row){
        //check if position does not have a sunk ship so it does not overwrite that cells value
        if (guessGrid[column][row] != 3){
            //checks if there is an enemy ship at the users guess location and marks it as hit.
            if(enemyGrid[column][row] == 1){
                guessGrid[column][row] = 1;
            }
            else{ // If there isn't a ship it marks it as miss.
                guessGrid[column][row] = 2;
            }
        }

       return guessGrid[column][row];
    }


    @Override
    //Returns the values of the cells when building the game view
    public int placedCell(int column, int row) {

        return guessGrid[column][row];

    }

    /**Method that places the enemy ships to the enemy grid
     *
     * @param column
     * @param row
     * @return the cell that contains part of a ship
     */
    public int placedCellEnemy(int column, int row){

        for (int i=0; i<5; i++){
            if (column == enemyShips[i].getLeft() && row == enemyShips[i].getTop()){

                enemyGrid[column][row]=1;
            }
            for (int size=0; size<enemyShips[i].getSize(); size++){
                if (enemyShips[i].isHorizontal() == true && column-size == enemyShips[i].getLeft()
                        && row == enemyShips[i].getTop()){

                    enemyGrid[column][row]=1;
                }
                else if (enemyShips[i].isHorizontal() == false && column == enemyShips[i].getLeft()
                        && row-size == enemyShips[i].getTop() ){

                    enemyGrid[column][row]=1;
                }
            }
        }

        return enemyGrid[column][row];
    }

    /**Method that checks if an enemy ship was sunk
     *
     * @return if the ship is sunk
     */
    public boolean checkIfSunk(){
        int[] hitCount = new int[5]; // Array that stores the hits for each enemy ship
        boolean checkSunk = false;
        for (int i=0; i<5; i++){
            //Checks if the top left location of the enemy ship is already hit
            if (guessGrid[enemyShips[i].getLeft()] [enemyShips[i].getTop()] == 1){
                for (int j=0; j<enemyShips[i].getSize(); j++){
                    //if the other cells that contain the ship are 1(hit), increase the hitCount.

                    if (enemyShips[i].isHorizontal() == true && guessGrid[enemyShips[i].getLeft() + j] [enemyShips[i].getTop()] == 1){
                        hitCount[i] = hitCount[i] + 1;
                    }
                    else if (enemyShips[i].isHorizontal() == false && guessGrid[enemyShips[i].getLeft()] [enemyShips[i].getTop() + j] == 1){
                        hitCount[i] = hitCount[i] + 1;
                    }

                }
            }

            //Check if hitCount in specific index matches the enemy ship's size in that index
            if (hitCount[i] == enemyShips[i].getSize()){
                checkSunk = true;

                //Set checkSunk to true and mark the cells containing that ship to 3(sunk)
                for (int j=0; j<enemyShips[i].getSize(); j++){
                    if (enemyShips[i].isHorizontal() == true && guessGrid[enemyShips[i].getLeft() + j] [enemyShips[i].getTop()] == 1){

                        guessGrid[enemyShips[i].getLeft() + j] [enemyShips[i].getTop()] =3;
                    }
                    else if (enemyShips[i].isHorizontal() == false && guessGrid[enemyShips[i].getLeft()] [enemyShips[i].getTop() + j] == 1){

                        guessGrid[enemyShips[i].getLeft()] [enemyShips[i].getTop() + j] = 3;
                    }
                }
            }
        }
        return checkSunk;
    }


}
