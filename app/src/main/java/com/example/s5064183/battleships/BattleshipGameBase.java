package com.example.s5064183.battleships;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

//import org.jetbrains.annotations.NotNull;

//import androidx.annotation.CallSuper;

/**
 * Baseclass for the BattleshipGame class.
 *
 */
public abstract class BattleshipGameBase<D extends BattleshipGameBase.ShipData> {

    private final int rows;
    private final D[] ships;
    private final int columns;
    private final List<BattleshipGameListener> onGameChangeListeners = new ArrayList<>();
    private final boolean[] shipsSunk;

    protected BattleshipGameBase(int columns, int rows, D[] ships) {
        this.columns = columns;
        this.rows = rows;
        this.ships = ships;
        shipsSunk = new boolean[ships.length];
    }

    /**
     * @return The amount of rows in the game
     */
    public final int getRows() {
        return rows;
    }

    /**
     * @return The amount of columns in the game
     */
    public final int getColumns() {
        return columns;
    }

    public final D[] getShips() {
        return ships;
    }

    /**
     * An array determining whether the ship with the given index was sunk. This can be used for
     * various purposes, including to determine whether the game has been won.
     *
     * @return An array with the status of sinking of each ship
     */
    //@NotNull
    public final boolean[] getShipsSunk() {
        return shipsSunk;
    }

    public abstract int currentGuessAt(int column, int row);

    /**
     * Determine the status of the cell in the solution space. The values are as follows:
     * <table>
     * <tr><td>{@link #CELL_UNSET}</td><td>-1</td><td>Empty cell without a ship in it</td></tr>
     * <tr><td>0</td><td>0</td><td>This cell is part of ship 0</td></tr>
     * <tr><td>1</td><td>1</td><td>This cell is part of ship 1</td></tr>
     * <tr><td>2</td><td>2</td><td>This cell is part of ship 2</td></tr>
     * <tr><td>n</td><td>n</td><td>This cell is part of ship n</td></tr>
     * </table>
     *
     * @param column The horizontal position, required to be between 0 and {@link #getColumns()} exclusive
     * @param row    The vertical position, required to be between 0 and {@link #getRows()} exclusive
     * @return The value at the position per the main description.
     */
    public abstract int placedCell(int column, int row);


    /*
     * Infrastructure to allow listening to game change events (and update the display
     * correspondingly)
     */

    /**
     * Interface implemented by listeners to game change events.
     */
    @FunctionalInterface
    interface BattleshipGameListener<G extends BattleshipGameBase<?>> {
        /**
         * When the game is changed this method is called.
         *
         * @param game   The game object that is the source of the event.
         * @param column The column changed
         * @param row    The row changed
         */
        void onGameChanged(G game, int column, int row);
    }

    /**
     * Register a listener for game changes
     *
     * @param listener The listener to register.
     */
    void addOnGameChangeListener(BattleshipGameListener<? extends BattleshipGameBase<D>> listener) {
        if (!onGameChangeListeners.contains(listener)) onGameChangeListeners.add(listener);
    }

    /**
     * Unregister a listener so that it no longer receives notifications of game changes
     *
     * @param listener The listener to unregister.
     */
    void removeOnGameChangeListener(BattleshipGameListener<? extends BattleshipGameBase<D>> listener) {
        onGameChangeListeners.remove(listener);
    }

    /**
     * Send a game change event to all registered listeners.
     *
     * @param column The column changed
     * @param row    The row changed
     */
    void fireOnGameChangeEvent(int column, int row) {
        for (BattleshipGameListener listener : onGameChangeListeners) {
            // This cannot actually be safe, but should normally be fine
            //noinspection unchecked
            listener.onGameChanged(this, column, row);
        }
    }

    // Constants for use in other parts of the game

    static final int CELL_UNSET = -1;
    static final int CELL_EMPTY = 0;
    static final int SHIP_HIT = 1;
    static final int SHIP_SUNK_BASE = 2;
    static final int[] DEFAULT_SHIP_SIZES = {
            5, // Carrier
            4, // Battleship"
            3, // Cruiser"
            3, // Submarine"
            2 // Destroyer
    };

    /**
     * Base class for ship related data.
     */
    public static class ShipData implements Parcelable {
        private final int size;
        private final boolean isHorizontal;
        private final int left;
        private final int top;

        public ShipData(int size, boolean isHorizontal, int left, int top) {
            this.size = size;
            this.isHorizontal = isHorizontal;
            this.left = left;
            this.top = top;
        }

        /**
         * Constructor that initialises the fields from the given parcel.
         * @param source The parcel to read it from.
         */
        protected ShipData(Parcel source) {
            int tmpSize = source.readByte();
            isHorizontal = tmpSize >= 0;
            size = isHorizontal ? tmpSize : -tmpSize;
            left = source.readByte();
            top = source.readByte();
        }

        /**
         * @return The length/size of the ship in cells
         */
        public int getSize() {
            return size;
        }

        /**
         * @return {@code true} if the ship is placed horizontally, {@code false} if it is placed
         * vertically
         */
        public boolean isHorizontal() {
            return isHorizontal;
        }

        /**
         * @return The leftmost position of the ship.
         */
        public int getLeft() {
            return left;
        }

        /**
         * @return The topmost position of the ship
         */
        public int getTop() {
            return top;
        }

        /**
         * @return The rightmost position of the ship
         */
        public int getRight() {
            return isHorizontal ? left + size - 1 : left;
        }

        /**
         * @return The bottommost position of the ship
         */
        public int getBottom() {
            return isHorizontal ? top : top + size - 1;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        /**
         * Writes the data so it can be read back from storage by {@link ShipData#CREATOR} /
         * {@link #ShipData(Parcel)}
         * @param dest Parcel to write to
         * @param flags Flags used for writing. Irrelevant here
         */
        @Override
        //@CallSuper
        public void writeToParcel(Parcel dest, int flags) {
            if (isHorizontal) {
                dest.writeByte((byte) size);
            } else {
                dest.writeByte((byte) (-size));
            }
            dest.writeByte((byte) getLeft());
            dest.writeByte((byte) getTop());

        }

        /**
         * Object that can create ShipData from a parcel.
         */
        public static final Creator<ShipData> CREATOR = new Creator<ShipData>() {
            /**
             * Create a new object from the parcel. This method just invokes the constructor. This
             * allows child classes to reuse the implementation for the parent and to add their own
             * data at the end.
             * @param source The parcel to get the data from.
             * @return The new data
             */
            @Override
            public ShipData createFromParcel(Parcel source) {
                return new ShipData(source);
            }

            @Override
            public ShipData[] newArray(int size) {
                return new ShipData[size];
            }
        };
    }

}
