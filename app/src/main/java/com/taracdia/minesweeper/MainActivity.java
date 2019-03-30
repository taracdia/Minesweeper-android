package com.taracdia.minesweeper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.taracdia.minesweeper.MineSquares.MineSquare;
import com.taracdia.minesweeper.MineSquares.MineSquareAdapter;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    // TODO: databinding for flagsLeft
    // TODO: 3/28/2019 improve layout
    // TODO: 3/28/2019 add menu to make different levels and custom levels
    // TODO: 3/30/2019 save col/row/bomb number settings
    // TODO: 3/28/2019 high scores of each level with option to clear it
    // TODO: 3/28/2019 make sure that they can resume a game or start a new one
    // TODO: 3/29/2019 handle rotation
    GridView mineGrid;
    ArrayList<MineSquare> mineSquares;
    int numberOfColumns;
    int numberOfRows;
    int numberOfBombs;
    int flagsLeft;

    TextView flagsLeftTextView;
    ImageButton imageButton;
    MyTimer timer;

    int maxGridHeight = 0;
    int maxGridWidth = 0;

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

        mineGrid.setAdapter(adapter);
        mineGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!adapter.isGameOver()) {
                    timer.start();
                    MineSquare square = mineSquares.get(position);
                    if (!square.isFlagged()) {
                        if (square.isBomb()) {
                            timer.stop();
                            square.setClicked(true);
                            imageButton.setBackgroundResource(R.drawable.deadface);
                            adapter.setGameOver(true);
                        } else {
                            squareClick(square);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }

                if (isGameWon()) {
                    timer.stop();
                    Toast.makeText(MainActivity.this, "game won", Toast.LENGTH_SHORT).show();
                    imageButton.setBackgroundResource(R.drawable.coolface);
                    adapter.setGameOver(true);
                }

            }
        });

        mineGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!adapter.isGameOver()) {
                    MineSquare square = mineSquares.get(position);

                    if (!square.isClicked()) {
                        if (square.isFlagged()) {
                            square.setFlagged(false);
                            flagsLeft++;
                        } else {
                            square.setFlagged(true);
                            flagsLeft--;
                        }
                        updateFlagsView();

                        adapter.notifyDataSetChanged();
                    }
                }
                return true;
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButton.setBackgroundResource(R.drawable.happyface);
                adapter.setGameOver(false);
                setUpGame();
                adapter.notifyDataSetChanged();
            }
        });

        final LinearLayout layout = findViewById(R.id.mainLayout);
        final ViewTreeObserver observer = layout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (maxGridHeight == 0 || maxGridWidth == 0) {
                            //-10 for padding
                            maxGridWidth = layout.getWidth() - 10;
                            //At most, the grid can be the height of the main layout minus the height of the button bar
                            maxGridHeight = layout.getHeight() - findViewById(R.id.buttonBar).getHeight() - 10;
                            changeLevel(10, 10, 10, adapter);
                        }
                    }
                });

    }

    private void updateFlagsView() {
        String s = String.format(Locale.US, "%1$03d", flagsLeft);
        flagsLeftTextView.setText(s);
    }

    private void changeLevel(int colNo, int rowNo, int bombNo, MineSquareAdapter adapter) {
        if (maxGridHeight == 0 || maxGridWidth == 0) {
            LinearLayout mainLayout = findViewById(R.id.mainLayout);
            //-10 for padding
            maxGridWidth = mainLayout.getWidth() - 10;
            //At most, the grid can be the height of the main layout minus the height of the button bar
            maxGridHeight = mainLayout.getHeight() - findViewById(R.id.buttonBar).getHeight() - 10;
        }

        if (bombNo > rowNo * colNo) {
            Toast.makeText(MainActivity.this, "Too many bombs!", Toast.LENGTH_SHORT).show();
            return;
        }

        int squareHeight = maxGridHeight / rowNo;
        int squareWidth = maxGridWidth / colNo;

        int finalSquareDimension = squareWidth;
        if (squareHeight < squareWidth) {
            finalSquareDimension = squareHeight;
        }

        if (finalSquareDimension < 5) {
            Toast.makeText(MainActivity.this, "This would make the squares too small", Toast.LENGTH_SHORT).show();
            return;
        }

        mineGrid.getLayoutParams().height = finalSquareDimension * rowNo;
        mineGrid.getLayoutParams().width = finalSquareDimension * colNo;

        adapter.setSquareDimension(finalSquareDimension);
        adapter.notifyDataSetChanged();

        numberOfColumns = colNo;
        numberOfRows = rowNo;
        numberOfBombs = bombNo;
        flagsLeft = bombNo;

        setUpGame();
    }

    private void handleLevelChange() {
        //        if (changeLevel(#, #, #, adapter)) {
//            setUpGame();
//        } else {
//        }
    }


    private void setUpGame() {
        timer.stop();
        timer.resetCount();

        updateFlagsView();
        mineGrid.setNumColumns(numberOfColumns);
        mineSquares.clear();

        int totalSquares = numberOfColumns * numberOfRows;

        for (int i = 0; i < totalSquares; i++) {
            mineSquares.add(new MineSquare(i));
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

        ArrayList<Integer> neighborPositions;
        for (int squarePosition = 0; squarePosition < totalSquares; squarePosition++) {
            if (mineSquares.get(squarePosition).isBomb()) {
                neighborPositions = getNeighborPositions(squarePosition);
                for (Integer neighborPosition : neighborPositions) {
                    mineSquares.get(neighborPosition).incrementNeighborMines();
                }
            }
        }

    }

    private ArrayList<Integer> getNeighborPositions(int targetSquarePosition) {
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

    private void squareClick(MineSquare targetSquare) {
        if (!(targetSquare.getNeighborMines() != 0 || targetSquare.isClicked())) {
            targetSquare.setClicked(true);

            ArrayList<Integer> neighborPositions = getNeighborPositions(targetSquare.getPosition());

            for (Integer neighborPosition : neighborPositions) {
                MineSquare neighborSquare = mineSquares.get(neighborPosition);
                if (!neighborSquare.isFlagged() && !neighborSquare.isBomb()) {
                    if (neighborSquare.getNeighborMines() == 0) {
                        squareClick(neighborSquare);
                    } else {
                        neighborSquare.setClicked(true);
                    }
                }
            }
        }
        targetSquare.setClicked(true);
    }

    private boolean isGameWon() {
        for (MineSquare square : mineSquares) {
            if (!square.isBomb() && !square.isClicked()) {
                return false;
            }
        }
        return true;
    }

}
