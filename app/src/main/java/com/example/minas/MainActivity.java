package com.example.minas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int NUM_ROWS = 12;
    private static final int NUM_COLS = 12;
    private static final int MINAS_FACIL = 10;
    private static final int MINAS_MEDIO = 12;
    private static final int MINAS_DIFICIL = 15;
    private static Random rnd;

    Button buttons[][] = new Button[NUM_ROWS][NUM_COLS];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        crearBotonesDinamico();
        crearTableroConMinas();
    }

    public void crearBotonesDinamico() {
        TableLayout table = (TableLayout) findViewById(R.id.tableForButtons);
        table.setPadding(0,0,0,0);
        // ESTO ES LO QUE HACE QUE SEA MÁS CUADRADO, PERO CAMBIAR PORQUE ES UNA TREMENDA CHAPUZA

        table.setPadding(0,0,0,400);

        for (int row = 0; row < NUM_ROWS; row++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.WRAP_CONTENT,
                    TableLayout.LayoutParams.WRAP_CONTENT,
                    1.0f));
            tableRow.setPadding(0, 0, 0, 0);
            table.addView(tableRow);

            for (int col = 0; col < NUM_COLS; col++){
                final int FINAL_COL = col;
                final int FINAL_ROW = row;

                Button button = new Button(this);
                button.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT,
                        1.0f));

                // Make text not clip on small buttons
                button.setPadding(0, 0, 0, 0);
                button.setText("0");

                Bitmap original = BitmapFactory.decodeResource(getResources(), R.drawable.tile00);
                Resources resource = getResources();
                button.setBackground(new BitmapDrawable(resource, original));


                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gridButtonClicked(FINAL_COL, FINAL_ROW);
                    }
                });

                tableRow.addView(button);
                buttons[row][col] = button;
            }
        }
    }

    private void crearTableroConMinas() {
        // HashMap para que no se repitan los valores y no haya dos minas
        // superpuestas
        HashMap<Integer,Integer> hashMap = new HashMap<Integer, Integer>();
        rnd = new Random();
        while(hashMap.size() < 10) {
            hashMap.put(rnd.nextInt(NUM_COLS), rnd.nextInt(NUM_COLS));
        }
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tile03);
        Resources resource = getResources();
        // Colocamos las minas usando los valores no repetidos
        for (Map.Entry<Integer, Integer> set : hashMap.entrySet()) {
            buttons[set.getKey()][set.getValue()].setText("-1");
            buttons[set.getKey()][set.getValue()].setBackground(new BitmapDrawable(resource, originalBitmap));
        }
    }


    private void gridButtonClicked(int col, int row) {
        // Toast.makeText(this, "Button clicked: " + col + "," + row,
        //        Toast.LENGTH_SHORT).show();
        Button button = buttons[row][col];

        // Lock Button Sizes:
        lockButtonSizes();

        // Lógica de a ver qué cosiña ponemos

        // Scale image to button: Only works in JellyBean!
        // Image from Crystal Clear icon set, under LGPL
        // http://commons.wikimedia.org/wiki/Crystal_Clear
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tile04);
        Resources resource = getResources();
        button.setBackground(new BitmapDrawable(resource, originalBitmap));

    }

    private void lockButtonSizes() {
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                Button button = buttons[row][col];

                // Botones cuadrados
                int width = button.getWidth();
                button.setMinWidth(width);
                button.setMaxWidth(width);
                button.setMinHeight(width);
                button.setMaxHeight(width);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}