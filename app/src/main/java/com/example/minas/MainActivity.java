package com.example.minas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int DIMENSION_FACIL = 8;
    private static final int MINAS_FACIL = 10;
    private static final int DIMENSION_AMATEUR = 12;
    private static final int MINAS_AMATEUR = 30;
    private static final int DIMENSION_PRO = 16;
    private static final int MINAS_PRO = 60;
    private Resources res;

    private static Random rnd;

    Button buttons[][];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        rnd = new Random();
        Resources res = getResources();
        inicializar(DIMENSION_PRO);
    }

    // Hace más comodo reiniciar el juego y añadir el método a los diálogos.
    public void inicializar(int dimension) {
        switch (dimension) {
            // POR DEFECTO DIMENSION_FACIL
            case DIMENSION_AMATEUR:
                buttons = new Button[DIMENSION_AMATEUR][DIMENSION_AMATEUR];
                crearBotonesDinamico(DIMENSION_AMATEUR);
                crearTableroConMinas(DIMENSION_AMATEUR, MINAS_AMATEUR);
                calcularNumeros(DIMENSION_AMATEUR);
                break;
            case DIMENSION_PRO:
                buttons = new Button[DIMENSION_PRO][DIMENSION_PRO];
                crearBotonesDinamico(DIMENSION_PRO);
                crearTableroConMinas(DIMENSION_PRO, MINAS_PRO);
                calcularNumeros(DIMENSION_PRO);
                break;
            default:
                buttons = new Button[DIMENSION_FACIL][DIMENSION_FACIL];
                crearBotonesDinamico(DIMENSION_FACIL);
                crearTableroConMinas(DIMENSION_FACIL, MINAS_FACIL);
                calcularNumeros(DIMENSION_FACIL);
                break;
        }
    }

    /**
     * Se encarga de crear un TableLayout y rellenarlo de los botones del tablero.
     * @param dimension
     */
    public void crearBotonesDinamico(int dimension) {
        TableLayout table = (TableLayout) findViewById(R.id.tableForButtons);
        table.setPadding(0,0,0,0);

        // ESTO ES LO QUE HACE QUE SEA MÁS CUADRADO, PERO CAMBIAR PORQUE ES UNA TREMENDA CHAPUZA
        table.setPadding(0,0,0,400);

        for (int row = 0; row < dimension; row++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.WRAP_CONTENT,
                    TableLayout.LayoutParams.WRAP_CONTENT,
                    1.0f));
            tableRow.setPadding(0, 0, 0, 0);
            table.addView(tableRow);

            for (int col = 0; col < dimension; col++){
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

/*    private void crearTableroConMinas() {
        // HashMap para que no se repitan los valores y no haya dos minas
        // superpuestas
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
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
*/

    /**
     * Asigna las minas necesarias para cada dificultad de juego, cambiando su texto a -1 (String).
     * @param dimension
     * @param numMinasDificultad
     */
    private void crearTableroConMinas(int dimension, int numMinasDificultad) {
        int numMinas = 0;
        do {
            for(int i = 0; i < dimension; i++) {
                for(int j = 0;  j < dimension; j++) {
                    int fila = rnd.nextInt(dimension);
                    int columna = rnd.nextInt(dimension);
                    // Nos aseguramos de que no se superpongan minas y de que haya
                    // un número adecuado al nivel
                    if (!buttons[fila][columna].getText().equals("-1") && numMinas < numMinasDificultad) {
                        numMinas++;
                        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tile03);
                        Resources resource = getResources();
                        buttons[fila][columna].setText("-1");
                        buttons[fila][columna].setBackground(new BitmapDrawable(resource, originalBitmap));
                    }
                }
            }
        } while(numMinas < numMinasDificultad);
    }

    private void calcularNumeros(int dimension) {
        for(int i = 0; i < dimension; i++) {
            for(int j = 0; j < dimension; j++) {
                comprobar(i, j, dimension);
            }
        }
    }

    /**
     * Implementar recursividad aquí
     * @param i
     * @param j
     * @param dimension
     */
    private void comprobar(int i, int j, int dimension) {
        int contMinas = 0;

        if(i == 0 && j == 0) { // Estamos en la esquina superior izquierda
            if(buttons[i][j].getText().equals("-1")) {
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tile05);
                Resources resource = getResources();
                buttons[i][j].setBackground(new BitmapDrawable(resource, originalBitmap));
            } else if(buttons[i][j].getText().equals("0")) {
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tile15);
                Resources resource = getResources();
                buttons[i][j].setBackground(new BitmapDrawable(resource, originalBitmap));
            }

            if(buttons[i+1][j].getText().equals("-1")) {
                contMinas++;
            } else if(buttons[i+1][j].getText().equals("0")) {
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tile15);
                Resources resource = getResources();
                buttons[i+1][j].setBackground(new BitmapDrawable(resource, originalBitmap));
            }

            if(buttons[i+1][j+1].getText().equals("-1")) {
                contMinas++;
            } else if(buttons[i+1][j+1].getText().equals("0")) {
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tile15);
                Resources resource = getResources();
                buttons[i+1][j+1].setBackground(new BitmapDrawable(resource, originalBitmap));
            }

            if(buttons[i][j+1].getText().equals("-1")) {
                contMinas++;
            } else if(buttons[i][j+1].getText().equals("0")) {
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tile15);
                Resources resource = getResources();
                buttons[i][j+1].setBackground(new BitmapDrawable(resource, originalBitmap));
            }
            // -------------------------------------------------------------------------------------------
        } else if((i == dimension - 1) && (j == 0)) { // Esquina inferior izquierda
            if(buttons[i][j].getText().equals("-1")) {
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tile05);
                Resources resource = getResources();
                buttons[i][j].setBackground(new BitmapDrawable(resource, originalBitmap));
            } else if(buttons[i][j].getText().equals("0")) {
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tile15);
                Resources resource = getResources();
                buttons[i][j].setBackground(new BitmapDrawable(resource, originalBitmap));
            }

            if(buttons[i-1][j].getText().equals("-1")) {
                contMinas++;
            } else if(buttons[i-1][j].getText().equals("0")) {
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tile15);
                Resources resource = getResources();
                buttons[i-1][j].setBackground(new BitmapDrawable(resource, originalBitmap));
            }

            if(buttons[i-1][j+1].getText().equals("-1")) {
                contMinas++;
            } else if(buttons[i-1][j+1].getText().equals("0")) {
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tile15);
                Resources resource = getResources();
                buttons[i-1][j+1].setBackground(new BitmapDrawable(resource, originalBitmap));
            }

            if(buttons[i][j+1].getText().equals("-1")) {
                contMinas++;
            } else if(buttons[i][j+1].getText().equals("0")) {
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tile15);
                Resources resource = getResources();
                buttons[i][j+1].setBackground(new BitmapDrawable(resource, originalBitmap));
            }
        // ------------------------------------------------------------------------------------------------
        } else if((i == 0) && (j == dimension - 1)) { // Esquina superior derecha
            if(buttons[i][j].getText().equals("-1")) {
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tile05);
                Resources resource = getResources();
                buttons[i][j].setBackground(new BitmapDrawable(resource, originalBitmap));
            } else if(buttons[i][j].getText().equals("0")) {
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tile15);
                Resources resource = getResources();
                buttons[i][j].setBackground(new BitmapDrawable(resource, originalBitmap));
            }

            if(buttons[i][j-1].getText().equals("-1")) {
                contMinas++;
            } else if(buttons[i][j-1].getText().equals("0")) {
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tile15);
                Resources resource = getResources();
                buttons[i][j-1].setBackground(new BitmapDrawable(resource, originalBitmap));
            }

            if(buttons[i+1][j-1].getText().equals("-1")) {
                contMinas++;
            } else if(buttons[i+1][j-1].getText().equals("0")) {
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tile15);
                Resources resource = getResources();
                buttons[i+1][j-1].setBackground(new BitmapDrawable(resource, originalBitmap));
            }

            if(buttons[i+1][j].getText().equals("-1")) {
                contMinas++;
            } else if(buttons[i+1][j].getText().equals("0")) {
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tile15);
                Resources resource = getResources();
                buttons[i+1][j].setBackground(new BitmapDrawable(resource, originalBitmap));
            }
        // -------------------------------------------------------------------------------------------------
        } else if((i == dimension - 1) && (j == dimension - 1)) { // Esquina inferior derecha
            if(buttons[i][j].getText().equals("-1")) {
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tile05);
                Resources resource = getResources();
                buttons[i][j].setBackground(new BitmapDrawable(resource, originalBitmap));
            } else if(buttons[i][j].getText().equals("0")) {
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tile15);
                Resources resource = getResources();
                buttons[i][j].setBackground(new BitmapDrawable(resource, originalBitmap));
            }

            if(buttons[i-1][j].getText().equals("-1")) {
                contMinas++;
            } else if(buttons[i-1][j].getText().equals("0")) {
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tile15);
                Resources resource = getResources();
                buttons[i-1][j].setBackground(new BitmapDrawable(resource, originalBitmap));
            }

            if(buttons[i-1][j-1].getText().equals("-1")) {
                contMinas++;
            } else if(buttons[i-1][j-1].getText().equals("0")) {
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tile15);
                Resources resource = getResources();
                buttons[i-1][j-1].setBackground(new BitmapDrawable(resource, originalBitmap));
            }

            if(buttons[i][j-1].getText().equals("-1")) {
                contMinas++;
            } else if(buttons[i][j-1].getText().equals("0")) {
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tile15);
                Resources resource = getResources();
                buttons[i][j-1].setBackground(new BitmapDrawable(resource, originalBitmap));
            }
        // ------------------------------------------------------------------------------------------------
        } else if(j == 0) { // Estamos en la primera columna
            //if(buttons[i][j])
        }
        // SWITCH FINAL -----------------------------------------------------------------------------------
        switch (contMinas) {
            case 1:
                Bitmap bit1 = BitmapFactory.decodeResource(getResources(), R.drawable.tile14);
                buttons[i][j].setBackground(new BitmapDrawable(res, bit1));
                break;
            case 2:
                Bitmap bit2 = BitmapFactory.decodeResource(getResources(), R.drawable.tile13);
                buttons[i][j].setBackground(new BitmapDrawable(res, bit2));
                break;
            case 3:
                Bitmap bit3 = BitmapFactory.decodeResource(getResources(), R.drawable.tile12);
                buttons[i][j].setBackground(new BitmapDrawable(res, bit3));
                break;
            case 4:
                Bitmap bit4 = BitmapFactory.decodeResource(getResources(), R.drawable.tile11);
                buttons[i][j].setBackground(new BitmapDrawable(res, bit4));
                break;
            case 5:
                Bitmap bit5 = BitmapFactory.decodeResource(getResources(), R.drawable.tile10);
                buttons[i][j].setBackground(new BitmapDrawable(res, bit5));
                break;
            case 6:
                Bitmap bit6 = BitmapFactory.decodeResource(getResources(), R.drawable.tile09);
                buttons[i][j].setBackground(new BitmapDrawable(res, bit6));
                break;
            case 7:
                Bitmap bit7 = BitmapFactory.decodeResource(getResources(), R.drawable.tile08);
                buttons[i][j].setBackground(new BitmapDrawable(res, bit7));
                break;
            case 8:
                Bitmap bit8 = BitmapFactory.decodeResource(getResources(), R.drawable.tile07);
                buttons[i][j].setBackground(new BitmapDrawable(res, bit8));
                break;
        }
    }

    private void gridButtonClicked(int col, int row) {
        // Toast.makeText(this, "Button clicked: " + col + "," + row,
        //        Toast.LENGTH_SHORT).show();
        Button button = buttons[row][col];
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}