package com.taracdia.minesweeper.MineSquares;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.taracdia.minesweeper.R;

import java.util.ArrayList;

public class MineSquareAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private ArrayList<MineSquare> mineSquares;
    private boolean gameOver = false;
    private int squareDimension = 90;

    public MineSquareAdapter(Context context, ArrayList<MineSquare> mineSquares) {
        this.mineSquares = mineSquares;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setSquareDimension(int dimension){
        this.squareDimension = dimension;
    }

    @Override
    public int getCount() {
        return mineSquares.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MineSquare currentSquare = mineSquares.get(position);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.minesquare_layout, null);
        }
        ImageView icon = convertView.findViewById(R.id.image);

        icon.getLayoutParams().height = squareDimension;
        icon.getLayoutParams().width = squareDimension;

        if (gameOver) {
            if (currentSquare.isBomb()) {
                if (currentSquare.isClicked()) {
                    icon.setImageResource(R.drawable.bomb_exploded);
                } else {
                    icon.setImageResource(R.drawable.bomb_normal);
                }
            } else {
                if (currentSquare.isFlagged()) {
                    icon.setImageResource(R.drawable.bomb_not);
                } else {
                    setNumberSquares(currentSquare, icon);
                }
            }
        } else {
            if (currentSquare.isClicked()) {
                setNumberSquares(currentSquare, icon);
            } else if (currentSquare.isFlagged()) {
                icon.setImageResource(R.drawable.flag);
            } else {
                icon.setImageResource(R.drawable.button);
//                icon.setImageResource(R.drawable.happyface);
            }
        }
        return convertView;
    }

    private void setNumberSquares(MineSquare mineSquare, ImageView icon) {
        switch (mineSquare.getNeighborMines()) {
            case 0:
                icon.setImageResource(R.drawable.number_0);
                break;
            case 1:
                icon.setImageResource(R.drawable.number_1);
                break;
            case 2:
                icon.setImageResource(R.drawable.number_2);
                break;
            case 3:
                icon.setImageResource(R.drawable.number_3);
                break;
            case 4:
                icon.setImageResource(R.drawable.number_4);
                break;
            case 5:
                icon.setImageResource(R.drawable.number_5);
                break;
            case 6:
                icon.setImageResource(R.drawable.number_6);
                break;
            case 7:
                icon.setImageResource(R.drawable.number_7);
                break;
            case 8:
                icon.setImageResource(R.drawable.number_8);
                break;


        }
    }

}
