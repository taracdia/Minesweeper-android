package com.taracdia.minesweeper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.taracdia.minesweeper.MineSquares.MineSquare;
import com.taracdia.minesweeper.MineSquares.MineSquareAdapter;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    // TODO: databinding for flagsLeft
    // TODO: 3/28/2019 improve layout
    // TODO: 3/28/2019 add menu to make different levels and custom levels
    // TODO: 3/28/2019 data validation to prevent them from making squares too small or too full of bombs
    // TODO: 3/28/2019 high scores of each level with option to clear it
    // TODO: 3/28/2019 make sure that they can resume a game or start a new one 
    GridView mineGrid;
    ArrayList<MineSquare> mineSquares;
    int numberOfColumns;
    int numberOfRows;
    int numberOfBombs;
    int flagsLeft;

    TextView flagsLeftTextView;
    ImageButton imageButton;
    MyTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flagsLeftTextView = findViewById(R.id.flagsLeftTextView);
        imageButton = findViewById(R.id.imageButton);
        timer = findViewById(R.id.timer);
        mineSquares = new ArrayList<>();
        mineGrid = findViewById(R.id.mineGrid);

        final MineSquareAdapter adapter = new MineSquareAdapter(this, mineSquares);
        setUpGame(10, 10, 10);

        imageButton.setBackgroundResource(R.drawable.happyface);
        mineGrid.setAdapter(adapter);
        mineGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                timer.start();
                MineSquare square = mineSquares.get(position);
                if (!square.isFlagged()) {
                    if (square.isBomb()) {
                        timer.stop();
                        square.setClicked(true);
                        imageButton.setBackgroundResource(R.drawable.deadface);
                        adapter.setGameOver(true);
                    } else {
                        squareClick(position);
                    }
                    adapter.notifyDataSetChanged();
                }

                if (isGameWon()){
                    timer.stop();
                    Toast.makeText(MainActivity.this, "game won", Toast.LENGTH_SHORT).show();
                    imageButton.setBackgroundResource(R.drawable.coolface);
                }

            }
        });

        mineGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                MineSquare square = mineSquares.get(position);

                if (!square.isClicked()) {
                    if (square.isFlagged()) {
                        square.setFlagged(false);
                        flagsLeft++;
                    } else {
                        square.setFlagged(true);
                        flagsLeft--;
                    }
                    flagsLeftTextView.setText(Integer.toString(flagsLeft));
                    adapter.notifyDataSetChanged();
                }

                return true;
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.setGameOver(false);
                setUpGame(10,10,10);
                adapter.notifyDataSetChanged();
            }
        });

    }

    private void setUpGame(int colNo, int rowNo, int bombNo) {
        timer.stop();
        timer.resetCount();

        numberOfColumns = colNo;
        numberOfRows = rowNo;
        numberOfBombs = bombNo;
        flagsLeft = bombNo;

        flagsLeftTextView.setText(Integer.toString(bombNo));
        imageButton.setBackgroundResource(R.drawable.happyface);

        mineGrid.setNumColumns(colNo);
        mineSquares.clear();

        int totalSquares = colNo * rowNo;

        for (int i = 0; i < totalSquares; i++) {
            mineSquares.add(new MineSquare());
        }

        Random r = new Random();
        MineSquare mineSquare;
        int copyOfNumberOfBombs = numberOfBombs;

        while (copyOfNumberOfBombs > 0) {
            mineSquare = mineSquares.get(r.nextInt(totalSquares));
            if (!mineSquare.isBomb()) {
                mineSquare.setBomb(true);
                copyOfNumberOfBombs--;
            }
        }

        for (int pos = 0; pos < totalSquares; pos++) {
            mineSquare = mineSquares.get(pos);
            mineSquare.setNeighborMines(getNeighborNumber(pos));
        }
    }

    private int getNeighborNumber(int targetSquarePosition) {
        int neighborMineNumber = 0;

        ArrayList<Integer> neighborPositions = getNeighbors(targetSquarePosition);

        for (Integer neighborPosition: neighborPositions){
            if (mineSquares.get(neighborPosition).isBomb()){
                neighborMineNumber++;
            }
        }

        return neighborMineNumber;
    }

    private ArrayList<Integer> getNeighbors(int targetSquarePosition) {
        ArrayList<Integer> neighborPositions = new ArrayList<>();

        int targetRow = targetSquarePosition / numberOfColumns;
        int targetCol = targetSquarePosition % numberOfColumns;

        boolean isSquareToTheRight = targetCol > 0;
        boolean isSquareToTheBottom = targetRow > 0;
        boolean isSquareToTheTop = targetRow < numberOfRows - 1;
        boolean isSquareToTheLeft = targetCol < numberOfColumns - 1;

        //add squares to the right of the target square
        if (isSquareToTheRight) {
            //right center
            neighborPositions.add(targetSquarePosition - 1);
            //right bottom
            if (isSquareToTheBottom) {
                neighborPositions.add(targetSquarePosition - 1 - numberOfColumns);
            }
            //right top
            if (isSquareToTheTop) {
                neighborPositions.add(targetSquarePosition - 1 + numberOfColumns);
            }
        }

        //add squares to the left of the target square
        if (isSquareToTheLeft) {
            //left center
            neighborPositions.add(targetSquarePosition + 1);
            //left bottom
            if (isSquareToTheBottom) {
                neighborPositions.add(targetSquarePosition + 1 - numberOfColumns);
            }
            //left top
            if (isSquareToTheTop) {
                neighborPositions.add(targetSquarePosition + 1 + numberOfColumns);
            }
        }

        // center bottom
        if (isSquareToTheBottom) {
            neighborPositions.add(targetSquarePosition - numberOfColumns);
        }
        // center top
        if (isSquareToTheTop) {
            neighborPositions.add(targetSquarePosition + numberOfColumns);
        }

        return neighborPositions;
    }

    private void squareClick(int clickedSquarePosition) {
        MineSquare targetSquare = mineSquares.get(clickedSquarePosition);

        if (targetSquare.getNeighborMines() != 0 || targetSquare.isClicked()) {
            targetSquare.setClicked(true);
            return;
        }
        targetSquare.setClicked(true);

        ArrayList<Integer> neighborPositions = getNeighbors(clickedSquarePosition);

        for (Integer neighborPosition: neighborPositions){
            MineSquare neighborSquare = mineSquares.get(neighborPosition);
            if (!neighborSquare.isFlagged() && !neighborSquare.isBomb()) {
                if (neighborSquare.getNeighborMines() == 0) {
                    squareClick(neighborPosition);
                } else {
                    neighborSquare.setClicked(true);
                }
            }
        }
    }

    private boolean isGameWon(){
        for (MineSquare square: mineSquares){
            if (!square.isBomb() && !square.isClicked()){
                return false;
            }
        }

        return true;
    }

}
