package com.taracdia.minesweeper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.taracdia.minesweeper.MineSquares.MineSquare;
import com.taracdia.minesweeper.MineSquares.MineSquareAdapter;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    GridView mineGrid;
    ArrayList<MineSquare> mineSquares;
    int numberOfColumns = 10;
    int numberOfRows = 10;
    int numberOfBombs = 10;
    int flagsLeft = numberOfBombs;

    TextView flagsLeftTextView;
    ImageButton imageButton;
    MyTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timer = findViewById(R.id.timer);
        timer.resetCount();
        
        flagsLeftTextView = findViewById(R.id.flagsLeftTextView);
        flagsLeftTextView.setText(Integer.toString(flagsLeft));

        imageButton = findViewById(R.id.imageButton);
        imageButton.setBackgroundResource(R.drawable.happyface);


        mineSquares = new ArrayList<>();
        mineGrid = findViewById(R.id.mineGrid);
        final MineSquareAdapter adapter = new MineSquareAdapter(this, mineSquares);
        mineGrid.setAdapter(adapter);

        setUpGrid();

        mineGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MineSquare square = mineSquares.get(position);
                if (!square.isFlagged()) {
                    if (square.isBomb()) {
                        square.setClicked(true);
                        imageButton.setBackgroundResource(R.drawable.deadface);
                        adapter.setGameOver(true);
                    } else {
                        squareClick(position);
                    }
                    adapter.notifyDataSetChanged();
                }

                if (isGameWon()){
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
                flagsLeft = numberOfBombs;
                flagsLeftTextView.setText(Integer.toString(flagsLeft));
                imageButton.setBackgroundResource(R.drawable.happyface);
                adapter.setGameOver(false);
                setUpGrid();
                adapter.notifyDataSetChanged();
            }
        });

    }

    private void setUpGrid() {
        //TODO
        //set check
        mineGrid.setNumColumns(numberOfColumns);
        mineSquares.clear();

        int totalSquares = numberOfColumns * numberOfRows;

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
