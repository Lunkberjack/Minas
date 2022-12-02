package com.example.minas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int DIMENSION_FACIL = 8;
    private static final int MINAS_FACIL = 10;
    private static final int DIMENSION_AMATEUR = 12;
    private static final int MINAS_AMATEUR = 30;
    private static final int DIMENSION_PRO = 16;
    private static final int MINAS_PRO = 60;
    private Resources resource;
    private Bitmap mina, vacio;
    private int contMinas;
    private static Random rnd;

    // El array bidimensional de int y el de Button están relacionados
    // íntimamente. El número de minas alrededor de cada Button se
    // incluirá como valor en la casilla correspondiente de la plantilla,
    // a través del método comprobar().
    private int[][] plantilla;
    private Button buttons[][];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resource = getResources();
        mina = BitmapFactory.decodeResource(resource, R.drawable.tile05);
        vacio = BitmapFactory.decodeResource(resource, R.drawable.tile15);

        setContentView(R.layout.activity_main);
        rnd = new Random();
        Resources res = getResources();
        inicializar(DIMENSION_PRO);
        for(int i = 0; i < DIMENSION_PRO; i++){
            for(int j = 0; j < DIMENSION_PRO; j++) {
                comprobar(i,j,DIMENSION_PRO);
            }
        }
    }

    // Hace más comodo reiniciar el juego y añadir el método a los diálogos.
    public void inicializar(int dimension) {
        switch (dimension) {
            // POR DEFECTO DIMENSION_FACIL
            case DIMENSION_AMATEUR:
                buttons = new Button[DIMENSION_AMATEUR][DIMENSION_AMATEUR];
                plantilla = new int[DIMENSION_AMATEUR][DIMENSION_AMATEUR];
                crearBotonesDinamico(DIMENSION_AMATEUR);
                crearTableroConMinas(DIMENSION_AMATEUR, MINAS_AMATEUR);
                // Crea la plantilla (int[][]) para pruebas
                for(int i = 0; i < DIMENSION_AMATEUR; i++){
                    for(int j = 0; j < DIMENSION_AMATEUR; j++) {
                        comprobar(i,j,DIMENSION_AMATEUR);
                    }
                }
                break;
            case DIMENSION_PRO:
                buttons = new Button[DIMENSION_PRO][DIMENSION_PRO];
                plantilla = new int[DIMENSION_PRO][DIMENSION_PRO];
                crearBotonesDinamico(DIMENSION_PRO);
                crearTableroConMinas(DIMENSION_PRO, MINAS_PRO);
                for(int i = 0; i < DIMENSION_PRO; i++){
                    for(int j = 0; j < DIMENSION_PRO; j++) {
                        comprobar(i,j,DIMENSION_PRO);
                    }
                }
                break;
            default:
                buttons = new Button[DIMENSION_FACIL][DIMENSION_FACIL];
                plantilla = new int[DIMENSION_FACIL][DIMENSION_FACIL];
                crearBotonesDinamico(DIMENSION_FACIL);
                crearTableroConMinas(DIMENSION_FACIL, MINAS_FACIL);
                for(int i = 0; i < DIMENSION_FACIL; i++){
                    for(int j = 0; j < DIMENSION_FACIL; j++) {
                        comprobar(i,j,DIMENSION_FACIL);
                    }
                }
                break;
        }
    }

    /**
     * Se encarga de crear un TableLayout y rellenarlo de los botones del tablero.
     *
     * @param dimension
     */
    public void crearBotonesDinamico(int dimension) {
        TableLayout table = (TableLayout) findViewById(R.id.tableForButtons);
        table.removeAllViewsInLayout(); // Actualiza la tabla cada vez que se llama al método
        table.setPadding(0, 0, 0, 0);

        // ESTO ES LO QUE HACE QUE SEA MÁS CUADRADO, PERO CAMBIAR PORQUE ES UNA TREMENDA CHAPUZA
        table.setPadding(0, 0, 0, 450);

        for (int row = 0; row < dimension; row++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.WRAP_CONTENT,
                    TableLayout.LayoutParams.WRAP_CONTENT,
                    1.0f));
            tableRow.setPadding(0, 0, 0, 0);
            table.addView(tableRow);

            for (int col = 0; col < dimension; col++) {
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
                        gridButtonClicked(FINAL_COL, FINAL_ROW, dimension);
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
     *
     * @param dimension
     * @param numMinasDificultad
     */
    private void crearTableroConMinas(int dimension, int numMinasDificultad) {
        int numMinas = 0;
        do {
            for (int i = 0; i < dimension; i++) {
                for (int j = 0; j < dimension; j++) {
                    int fila = rnd.nextInt(dimension);
                    int columna = rnd.nextInt(dimension);
                    // Nos aseguramos de que no se superpongan minas y de que haya
                    // un número adecuado al nivel
                    if (!buttons[fila][columna].getText().equals("-1") && numMinas < numMinasDificultad) {
                        numMinas++;
                        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tile03);
                        Resources resource = getResources();
                        buttons[fila][columna].setText("-1");
                        plantilla[fila][columna] = -1;
                        buttons[fila][columna].setBackground(new BitmapDrawable(resource, originalBitmap));
                    }
                }
            }
        } while (numMinas < numMinasDificultad);
    }

    /**
     * Para hacer pruebas sobre la lógica, no se implementa
     *
     * @param dimension private void calcularNumeros(int dimension) {
     *                  for(int i = 0; i < dimension; i++) {
     *                  for(int j = 0; j < dimension; j++) {
     *                  comprobar(i, j, dimension);
     *                  }
     *                  }
     *                  }
     */
    public void comprobarEsqIzqSup(int i, int j, int dimension) {
 /*       if (buttons[i - 1][j - 1].getText().equals("-1")) {
            contMinas++;
        } else if (buttons[i - 1][j - 1].getText().equals("0")) {
            buttons[i - 1][j - 1].setBackground(new BitmapDrawable(resource, vacio));
        }
  */
        if (plantilla[i - 1][j - 1] == -1) {
            contMinas++;
        }
    }

    public void comprobarEsqDerSup(int i, int j, int dimension) {
        /*if (buttons[i - 1][j + 1].getText().equals("-1")) {
            contMinas++;
        } else if (buttons[i - 1][j + 1].getText().equals("0")) {
            buttons[i - 1][j + 1].setBackground(new BitmapDrawable(resource, vacio));
        }*/

        if (plantilla[i - 1][j + 1] == -1) {
            contMinas++;
        }
    }

    public void comprobarEsqIzqInf(int i, int j, int dimension) {
        /*if (buttons[i + 1][j - 1].getText().equals("-1")) {
            contMinas++;
        } else if (buttons[i + 1][j - 1].getText().equals("0")) {
            buttons[i + 1][j - 1].setBackground(new BitmapDrawable(resource, vacio));
        }*/
        if (plantilla[i + 1][j - 1] == -1) {
            contMinas++;
        }
    }

    public void comprobarEsqDerInf(int i, int j, int dimension) {
        /*if (buttons[i + 1][j + 1].getText().equals("-1")) {
            contMinas++;
        } else if (buttons[i + 1][j + 1].getText().equals("0")) {
            buttons[i + 1][j + 1].setBackground(new BitmapDrawable(resource, vacio));
        }*/
        if (plantilla[i + 1][j + 1] == -1) {
            contMinas++;
        }
    }

    public void comprobarSup(int i, int j, int dimension) {
       /* if (buttons[i - 1][j].getText().equals("-1")) {
            contMinas++;
        } else if (buttons[i - 1][j].getText().equals("0")) {
            buttons[i - 1][j].setBackground(new BitmapDrawable(resource, vacio));
        }*/
        if (plantilla[i - 1][j] == -1) {
            contMinas++;
        }
    }

    public void comprobarInf(int i, int j, int dimension) {
        /*if (buttons[i + 1][j].getText().equals("-1")) {
            contMinas++;
        } else if (buttons[i + 1][j].getText().equals("0")) {
            buttons[i + 1][j].setBackground(new BitmapDrawable(resource, vacio));
        }*/
        if (plantilla[i + 1][j] == -1) {
            contMinas++;
        }
    }

    public void comprobarIzq(int i, int j, int dimension) {
        /*if (buttons[i][j - 1].getText().equals("-1")) {
            contMinas++;
        } else if (buttons[i][j - 1].getText().equals("0")) {
            buttons[i][j - 1].setBackground(new BitmapDrawable(resource, vacio));
        }*/
        if (plantilla[i][j - 1] == -1) {
            contMinas++;
        }
    }

    public void comprobarDer(int i, int j, int dimension) {
        /*if (buttons[i][j + 1].getText().equals("-1")) {
            contMinas++;
        } else if (buttons[i][j + 1].getText().equals("0")) {
            buttons[i][j + 1].setBackground(new BitmapDrawable(resource, vacio));
        }*/
        if (plantilla[i][j + 1] == -1) {
            contMinas++;
        }

    }

    /**
     * AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
     * ME VOY A PEGAR UN TIRO EN LAS PELOTAS Y NI SIQUIERA TENGO jefe
     * @param i
     * @param j
     * @param dimension
     */
    private void comprobar(int i, int j, int dimension) {
        contMinas = 0;
        if ((i == 0) && (j == 0)) { //----------------------------------- Estamos en la esquina superior izquierda
                comprobarDer(i, j, dimension);
                comprobarInf(i, j, dimension);
                comprobarEsqDerInf(i, j, dimension);
        } else if ((i == dimension - 1) && (j == 0)) { // ------------------------------Esquina inferior izquierda
            comprobarSup(i, j, dimension);
            comprobarDer(i, j, dimension);
            comprobarEsqDerSup(i, j, dimension);
        } else if ((i == 0) && (j == dimension - 1)) { // --------------------------------Esquina superior derecha
                comprobarIzq(i, j, dimension);
                comprobarInf(i, j, dimension);
                comprobarEsqIzqInf(i, j, dimension);
        } else if ((i == dimension - 1) && (j == dimension - 1)) {//--------------------- Esquina inferior derecha
                comprobarIzq(i, j, dimension);
                comprobarSup(i, j, dimension);
                comprobarEsqIzqSup(i, j, dimension);
        }else if((j == 0) && (i != 0 && (i < dimension - 1))) { //------------------ Estamos en la primera columna
                comprobarSup(i,j, dimension);
                comprobarInf(i,j, dimension);
                comprobarDer(i,j, dimension);
                comprobarEsqDerSup(i,j, dimension);
                comprobarEsqDerInf(i,j, dimension);
        } else if((j == dimension - 1) && (i != 0) && (i != dimension - 1)) { // -----Estamos en la última columna
                comprobarSup(i,j, dimension);
                comprobarInf(i,j, dimension);
                comprobarIzq(i,j, dimension);
                comprobarEsqIzqSup(i,j, dimension);
                comprobarEsqIzqInf(i,j, dimension);
        } else if((i == 0) && (j != 0) && (j != dimension - 1)) { //------------------- Estamos en la primera fila
                comprobarIzq(i,j, dimension);
                comprobarDer(i,j, dimension);
                comprobarInf(i,j, dimension);
                comprobarEsqIzqInf(i,j, dimension);
                comprobarEsqDerInf(i,j, dimension);
        } else if((i == dimension - 1) && (j != 0) && (j != dimension - 1)) { //-------- Estamos en la última fila
                comprobarSup(i,j, dimension);
                comprobarIzq(i,j, dimension);
                comprobarDer(i,j, dimension);
                comprobarEsqDerSup(i,j, dimension);
                comprobarEsqIzqSup(i,j, dimension);
        } else { // -----------------------------------------------------------------Cualquier casilla del interior
                comprobarEsqDerSup(i,j, dimension);
                comprobarEsqIzqInf(i,j, dimension);
                comprobarEsqIzqSup(i,j,dimension);
                comprobarEsqDerInf(i,j, dimension);
                comprobarSup(i,j, dimension);
                comprobarInf(i,j, dimension);
                comprobarIzq(i,j, dimension);
                comprobarDer(i,j, dimension);
            }

        // SWITCH FINAL -----------------------------------------------------------------------------------
        if (!buttons[i][j].getText().equals("-1")) {
            switch (contMinas) {
                case 0:
                    Bitmap bit0 = BitmapFactory.decodeResource(getResources(), R.drawable.tile15);
                    buttons[i][j].setBackground(new BitmapDrawable(resource, bit0));
                    buttons[i][j].setText("");
                    plantilla[i][j] = 0;
                    break;
                case 1:
                    Bitmap bit1 = BitmapFactory.decodeResource(getResources(), R.drawable.tile14);
                    buttons[i][j].setBackground(new BitmapDrawable(resource, bit1));
                    buttons[i][j].setText("");
                    plantilla[i][j] = 1;
                    break;
                case 2:
                    Bitmap bit2 = BitmapFactory.decodeResource(getResources(), R.drawable.tile13);
                    buttons[i][j].setBackground(new BitmapDrawable(resource, bit2));
                    buttons[i][j].setText("");
                    plantilla[i][j] = 2;

                    break;
                case 3:
                    Bitmap bit3 = BitmapFactory.decodeResource(getResources(), R.drawable.tile12);
                    buttons[i][j].setBackground(new BitmapDrawable(resource, bit3));
                    buttons[i][j].setText("");
                    plantilla[i][j] =3;

                    break;
                case 4:
                    Bitmap bit4 = BitmapFactory.decodeResource(getResources(), R.drawable.tile11);
                    buttons[i][j].setBackground(new BitmapDrawable(resource, bit4));
                    buttons[i][j].setText("");
                    plantilla[i][j] = 4;

                    break;
                case 5:
                    Bitmap bit5 = BitmapFactory.decodeResource(getResources(), R.drawable.tile10);
                    buttons[i][j].setBackground(new BitmapDrawable(resource, bit5));
                    buttons[i][j].setText("");
                    plantilla[i][j] = 5;

                    break;
                case 6:
                    Bitmap bit6 = BitmapFactory.decodeResource(getResources(), R.drawable.tile09);
                    buttons[i][j].setBackground(new BitmapDrawable(resource, bit6));
                    buttons[i][j].setText("");
                    plantilla[i][j] =6;

                    break;
                case 7:
                    Bitmap bit7 = BitmapFactory.decodeResource(getResources(), R.drawable.tile08);
                    buttons[i][j].setBackground(new BitmapDrawable(resource, bit7));
                    buttons[i][j].setText("");
                    plantilla[i][j] = 7;

                    break;
                case 8:
                    Bitmap bit8 = BitmapFactory.decodeResource(getResources(), R.drawable.tile07);
                    buttons[i][j].setBackground(new BitmapDrawable(resource, bit8));
                    buttons[i][j].setText("");
                    plantilla[i][j] = 8;

                    break;
            }
        }
    }

    private void gridButtonClicked(int col, int row, int dimension) {
        comprobar(row, col, dimension);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void seleccionarDificultad(View view) {
        registerForContextMenu(view);
        openContextMenu(view);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo info) {
        super.onCreateContextMenu(menu, v, info);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.dificultad_menu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.instrucciones:
                AlertDialog.Builder instrucciones = new AlertDialog.Builder(this);
                instrucciones.setTitle("Instrucciones de juego");
                instrucciones.setMessage("· Se presenta un campo de minas, de dimensiones: \n\t8x8 - fácil\n\t12x12 - amateur\n\t16x16 - pro" +
                        "\n\n· Cada casilla que no contenga mina mostrará el número de minas en sus casillas contiguas." +
                        "\n\n· El objetivo es evitar todas las minas, pulsando solo las casillas que no contengan una." +
                        "\n\n· El juego finaliza cuando todas las casillas sin mina han sido descubiertas, cuando se pulsa una mina o cuando " +
                        "se añade un 'aviso de mina' donde no hay una mina.");
                instrucciones.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(MainActivity.this, "¡Diviértete!", Toast.LENGTH_LONG).show();
                    }
                });
                AlertDialog alert = instrucciones.create();
                alert.show();
                return true;
            case R.id.nuevo:
                inicializar(DIMENSION_FACIL);
                Toast.makeText(MainActivity.this, "Puedes cambiar la dificultad cuando prefieras", Toast.LENGTH_LONG).show();
                return true;
            case R.id.config:
                seleccionarDificultad(findViewById(R.id.tableForButtons));
                return true;
        }
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.facil:
                inicializar(DIMENSION_FACIL);
                return true;
            case R.id.amateur:
                inicializar(DIMENSION_AMATEUR);
                return true;
            case R.id.pro:
                inicializar(DIMENSION_PRO);
                return true;
        }
        return true;
    }
}