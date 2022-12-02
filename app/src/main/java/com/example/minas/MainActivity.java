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
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int DIMENSION_FACIL = 8;
    private static final int DIMENSION_AMATEUR = 12;
    private static final int DIMENSION_PRO = 16;
    private static int minasTotales;
    private Resources resource;
    private Bitmap mina, vacio;
    private int contMinas;
    private static Random rnd;

    // El array bidimensional de int y el de Button están relacionados
    // íntimamente. El número de minas alrededor de cada Button se
    // incluirá como valor en la casilla correspondiente de la plantilla,
    // a través del método generarNumeros().
    private int[][] plantilla;
    private Button buttons[][];

    // Strings de info en pantalla y botón de reinicio rápido.
    private TextView totales;
    private TextView restantes;
    private TextView dificultad;
    private Button reiniciar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resource = getResources();
        mina = BitmapFactory.decodeResource(resource, R.drawable.tile05);
        vacio = BitmapFactory.decodeResource(resource, R.drawable.tile15);
        totales = findViewById(R.id.totales);
        restantes = findViewById(R.id.restantes);
        dificultad = findViewById(R.id.dificultad);

        reiniciar = findViewById(R.id.botonReiniciar);
        reiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inicializar(DIMENSION_FACIL);
            }
        });

        rnd = new Random();
        Resources res = getResources();
        inicializar(DIMENSION_FACIL);
    }

    /**
     * Crea el tablero, genera los números según las minas contiguas
     * y añade valores a los Strings de info.
     *
     * También "oculta" las casillas
     * asignándoles el típico tile del Buscaminas de WindowsXP.
     * @param dimension
     */
    public void inicializar(int dimension) {
        // El tile de casilla sin descubrir (16x16px)
        Bitmap original = BitmapFactory.decodeResource(getResources(), R.drawable.tile00);
        Resources resource = getResources();

        switch (dimension) {
            // POR DEFECTO DIMENSION_FACIL
            case DIMENSION_AMATEUR:
                minasTotales = 30;
                buttons = new Button[DIMENSION_AMATEUR][DIMENSION_AMATEUR];
                plantilla = new int[DIMENSION_AMATEUR][DIMENSION_AMATEUR];
                totales.setText("Minas totales: " + minasTotales);
                restantes.setText("Minas restantes: " + minasTotales);
                dificultad.setText("Modo: Amateur");

                crearBotonesDinamico(DIMENSION_AMATEUR);
                crearTableroConMinas(DIMENSION_AMATEUR, minasTotales);
                // Crea la plantilla (int[][]) para pruebas
                for(int i = 0; i < DIMENSION_AMATEUR; i++){
                    for(int j = 0; j < DIMENSION_AMATEUR; j++) {
                        generarNumeros(i,j,DIMENSION_AMATEUR);
                    }
                }
                // Oculta casillas
                for(int i = 0; i < DIMENSION_AMATEUR; i++) {
                    for(int j = 0; j < DIMENSION_AMATEUR; j++) {
                        buttons[i][j].setBackground(new BitmapDrawable(resource, original));
                    }
                }
                break;
            case DIMENSION_PRO:
                minasTotales = 60;
                buttons = new Button[DIMENSION_PRO][DIMENSION_PRO];
                plantilla = new int[DIMENSION_PRO][DIMENSION_PRO];
                totales.setText("Minas totales: " + minasTotales);
                restantes.setText("Minas restantes: " + minasTotales);
                dificultad.setText("Modo: Pro");

                crearBotonesDinamico(DIMENSION_PRO);
                crearTableroConMinas(DIMENSION_PRO, minasTotales);
                for(int i = 0; i < DIMENSION_PRO; i++){
                    for(int j = 0; j < DIMENSION_PRO; j++) {
                        generarNumeros(i,j,DIMENSION_PRO);
                    }
                }
                // Oculta casillas
                for(int i = 0; i < DIMENSION_PRO; i++) {
                    for(int j = 0; j < DIMENSION_PRO; j++) {
                        buttons[i][j].setBackground(new BitmapDrawable(resource, original));
                    }
                }
                break;
            default:
                minasTotales = 10;
                buttons = new Button[DIMENSION_FACIL][DIMENSION_FACIL];
                plantilla = new int[DIMENSION_FACIL][DIMENSION_FACIL];
                totales.setText("Minas totales: " + minasTotales);
                restantes.setText("Minas restantes: " + minasTotales);
                dificultad.setText("Modo: Fácil");

                crearBotonesDinamico(DIMENSION_FACIL);
                crearTableroConMinas(DIMENSION_FACIL, minasTotales);
                for(int i = 0; i < DIMENSION_FACIL; i++){
                    for(int j = 0; j < DIMENSION_FACIL; j++) {
                        generarNumeros(i,j,DIMENSION_FACIL);
                    }
                }
                // Oculta casillas
                for(int i = 0; i < DIMENSION_FACIL; i++) {
                    for(int j = 0; j < DIMENSION_FACIL; j++) {
                        buttons[i][j].setBackground(new BitmapDrawable(resource, original));
                    }
                }
                break;
        }
    }

    /**
     * Se encarga de crear un TableLayout y rellenarlo de los botones del tablero.
     * @param dimension
     */
    public void crearBotonesDinamico(int dimension) {
        TableLayout table = (TableLayout) findViewById(R.id.tableForButtons);
        table.removeAllViewsInLayout(); // Actualiza la tabla cada vez que se llama al método
        table.setPadding(0, 0, 0, 0);

        // HACE QUE SEA MÁS CUADRADO, PERO CAMBIAR SI LA PANTALLA DEL MÓVIL ES DE POCA RESOLUCIÓN
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

                button.setPadding(0, 0, 0, 0);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        logicaBoton(FINAL_COL, FINAL_ROW, dimension);
                    }
                });

                button.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        logicaBotonLargo(FINAL_COL, FINAL_ROW, dimension);
                        return true;
                    }
                });

                tableRow.addView(button);
                buttons[row][col] = button;
            }
        }
    }

    /**
     * Lógica de click corto sobre casilla.
     *
     * Asigna un tile diferente del tileset según se trate
     * de una mina, un espacio vacío o un número.
     *
     * @param col
     * @param row
     * @param dimension
     */
    private void logicaBoton(int col, int row, int dimension) {
        Button button = buttons[row][col];
        if(plantilla[row][col] == -1) {
            Bitmap bitMina = BitmapFactory.decodeResource(getResources(), R.drawable.tile04);
            buttons[row][col].setBackground(new BitmapDrawable(resource, bitMina));
            AlertDialog.Builder gameOver = new AlertDialog.Builder(this);

            gameOver.setTitle("HAS PERDIDO");
            gameOver.setMessage("Parece ser que has pisado una mina :(");
            gameOver.setPositiveButton("Jugar de nuevo", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Toast.makeText(MainActivity.this, "¡No te rindas!", Toast.LENGTH_LONG).show();
                    inicializar(DIMENSION_FACIL);
                }
            });
            gameOver.create();
            gameOver.show();
        } else if(plantilla[row][col] > 0) {
            switch (plantilla[row][col]) {
                case 1:
                    Bitmap bit1 = BitmapFactory.decodeResource(getResources(), R.drawable.tile14);
                    buttons[row][col].setBackground(new BitmapDrawable(resource, bit1));
                    break;
                case 2:
                    Bitmap bit2 = BitmapFactory.decodeResource(getResources(), R.drawable.tile13);
                    buttons[row][col].setBackground(new BitmapDrawable(resource, bit2));
                    break;
                case 3:
                    Bitmap bit3 = BitmapFactory.decodeResource(getResources(), R.drawable.tile12);
                    buttons[row][col].setBackground(new BitmapDrawable(resource, bit3));
                    break;
                case 4:
                    Bitmap bit4 = BitmapFactory.decodeResource(getResources(), R.drawable.tile11);
                    buttons[row][col].setBackground(new BitmapDrawable(resource, bit4));
                    break;
                case 5:
                    Bitmap bit5 = BitmapFactory.decodeResource(getResources(), R.drawable.tile10);
                    buttons[row][col].setBackground(new BitmapDrawable(resource, bit5));
                    break;
                case 6:
                    Bitmap bit6 = BitmapFactory.decodeResource(getResources(), R.drawable.tile09);
                    buttons[row][col].setBackground(new BitmapDrawable(resource, bit6));
                    break;
                case 7:
                    Bitmap bit7 = BitmapFactory.decodeResource(getResources(), R.drawable.tile08);
                    buttons[row][col].setBackground(new BitmapDrawable(resource, bit7));
                    break;
                case 8:
                    Bitmap bit8 = BitmapFactory.decodeResource(getResources(), R.drawable.tile07);
                    buttons[row][col].setBackground(new BitmapDrawable(resource, bit8));
                    break;
            }
        } else {
            Bitmap bit9 = BitmapFactory.decodeResource(getResources(), R.drawable.tile15);
            buttons[row][col].setBackground(new BitmapDrawable(resource, bit9));
            generarNumeros(row,col,dimension);
        }
    }

    /**
     * Lógica de click largo sobre casilla.
     *
     * Coloca una bandera y notifica de que se ha encontrado una mina o finaliza el juego
     * si se ha colocado en una casilla que no contiene mina.
     *
     * @param col
     * @param row
     * @param dimension
     */
    public void logicaBotonLargo(int col, int row, int dimension){
        if(plantilla[row][col] != -1) {
            // Si no se pulsa jugar de nuevo sino otra zona de la pantalla, se permite volver al juego.
            // Sé que esto no es lo más indicado, pero sirve para comprobar que el funcionamiento ha sido correcto.
            // Si quisiéramos que el juego se reiniciara sin dar esta opción, inicializaríamos justo aquí:
            // inicializar(DIMENSION_FACIL);

            AlertDialog.Builder gameOver = new AlertDialog.Builder(this);
            gameOver.setTitle("HAS PERDIDO");
            gameOver.setMessage("No, eso no era una mina :(");
            gameOver.setPositiveButton("Jugar de nuevo", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Toast.makeText(MainActivity.this, "¡No te rindas!", Toast.LENGTH_LONG).show();
                    inicializar(DIMENSION_FACIL);
                }
            });

            gameOver.create();
            gameOver.show();
        } else {
            restantes.setText("Minas restantes: " + --minasTotales);
            Bitmap bitFlag = BitmapFactory.decodeResource(getResources(), R.drawable.tile01);
            buttons[row][col].setBackground(new BitmapDrawable(resource, bitFlag));
            Toast.makeText(MainActivity.this, "¡Has encontrado una mina!", Toast.LENGTH_SHORT).show();

            // Esto podría mejorar muchísimo si tuviera algo más de tiempo para implementarlo
            if(restantes.getText().equals("Minas restantes: 0")) {
                AlertDialog.Builder victoria = new AlertDialog.Builder(this);
                victoria.setTitle("HAS GANADO");
                victoria.setMessage("Has marcado todas las minas :)\n¡Bien hecho!");
                victoria.setPositiveButton("¿Otra?", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        inicializar(DIMENSION_FACIL);
                    }
                });

                victoria.create();
                victoria.show();
            }
        }
    }

    /**
     * Asigna las minas necesarias para cada dificultad de juego, cambiando el valor a -1.
     *
     * @param dimension
     * @param numMinasDificultad
     */
    private void crearTableroConMinas(int dimension, int numMinasDificultad) {
        int numMinas = 0;
        do {
            // Generamos una posición (x,y) aleatoria donde colocar la mina
            int fila = rnd.nextInt(dimension);
            int columna = rnd.nextInt(dimension);
            // Nos aseguramos de que no se superpongan minas y de que haya
            // un número adecuado al nivel
            if (!(plantilla[fila][columna] == -1) && numMinas != numMinasDificultad) {
                numMinas++;
                plantilla[fila][columna] = -1;
            }
        } while (numMinas != numMinasDificultad);
    }

    // MÉTODOS QUE COMPRUEBAN LOS 8 CASOS POSIBLES DE CASILLA CONTIGUA ---------------------------------
    public void comprobarEsqIzqSup(int i, int j, int dimension) {
        if (plantilla[i - 1][j - 1] == -1) {
            contMinas++;
        }
    }
    public void comprobarEsqDerSup(int i, int j, int dimension) {
        if (plantilla[i - 1][j + 1] == -1) {
            contMinas++;
        }
    }
    public void comprobarEsqIzqInf(int i, int j, int dimension) {
        if (plantilla[i + 1][j - 1] == -1) {
            contMinas++;
        }
    }
    public void comprobarEsqDerInf(int i, int j, int dimension) {
        if (plantilla[i + 1][j + 1] == -1) {
            contMinas++;
        }
    }
    public void comprobarSup(int i, int j, int dimension) {
        if (plantilla[i - 1][j] == -1) {
            contMinas++;
        }
    }
    public void comprobarInf(int i, int j, int dimension) {
        if (plantilla[i + 1][j] == -1) {
            contMinas++;
        }
    }
    public void comprobarIzq(int i, int j, int dimension) {
        if (plantilla[i][j - 1] == -1) {
            contMinas++;
        }
    }
    public void comprobarDer(int i, int j, int dimension) {
        if (plantilla[i][j + 1] == -1) {
            contMinas++;
        }
    }

    /**
     * Añade a la plantilla los datos de todos los números del tablero, siendo 0
     * para las casillas vacías y -1 para las minas.
     *
     * @param i
     * @param j
     * @param dimension
     */
    private void generarNumeros(int i, int j, int dimension) {
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
        } else if ((j == 0) && (i != 0 && (i < dimension - 1))) { //------------------ Estamos en la primera columna
            comprobarSup(i, j, dimension);
            comprobarInf(i, j, dimension);
            comprobarDer(i, j, dimension);
            comprobarEsqDerSup(i, j, dimension);
            comprobarEsqDerInf(i, j, dimension);
        } else if ((j == dimension - 1) && (i != 0) && (i != dimension - 1)) { // -----Estamos en la última columna
            comprobarSup(i, j, dimension);
            comprobarInf(i, j, dimension);
            comprobarIzq(i, j, dimension);
            comprobarEsqIzqSup(i, j, dimension);
            comprobarEsqIzqInf(i, j, dimension);
        } else if ((i == 0) && (j != 0) && (j != dimension - 1)) { //------------------- Estamos en la primera fila
            comprobarIzq(i, j, dimension);
            comprobarDer(i, j, dimension);
            comprobarInf(i, j, dimension);
            comprobarEsqIzqInf(i, j, dimension);
            comprobarEsqDerInf(i, j, dimension);
        } else if ((i == dimension - 1) && (j != 0) && (j != dimension - 1)) { //-------- Estamos en la última fila
            comprobarSup(i, j, dimension);
            comprobarIzq(i, j, dimension);
            comprobarDer(i, j, dimension);
            comprobarEsqDerSup(i, j, dimension);
            comprobarEsqIzqSup(i, j, dimension);
        } else { // -----------------------------------------------------------------Cualquier casilla del interior
            comprobarEsqDerSup(i, j, dimension);
            comprobarEsqIzqInf(i, j, dimension);
            comprobarEsqIzqSup(i, j, dimension);
            comprobarEsqDerInf(i, j, dimension);
            comprobarSup(i, j, dimension);
            comprobarInf(i, j, dimension);
            comprobarIzq(i, j, dimension);
            comprobarDer(i, j, dimension);
        }

        // SWITCH FINAL -----------------------------------------------------------------------------------
        // Realmente no había que implementarlo otra vez, pero iba a usar este método para el siguiente
        // intento de recursividad, así que lo dejaré para versiones posteriores.
        if (!(plantilla[i][j] == -1)) {
            switch (contMinas) {
                case 0:
                    Bitmap bit0 = BitmapFactory.decodeResource(getResources(), R.drawable.tile15);
                    buttons[i][j].setBackground(new BitmapDrawable(resource, bit0));
                    plantilla[i][j] = 0;
                    break;
                case 1:
                    Bitmap bit1 = BitmapFactory.decodeResource(getResources(), R.drawable.tile14);
                    buttons[i][j].setBackground(new BitmapDrawable(resource, bit1));
                    plantilla[i][j] = 1;
                    break;
                case 2:
                    Bitmap bit2 = BitmapFactory.decodeResource(getResources(), R.drawable.tile13);
                    buttons[i][j].setBackground(new BitmapDrawable(resource, bit2));
                    plantilla[i][j] = 2;
                    break;
                case 3:
                    Bitmap bit3 = BitmapFactory.decodeResource(getResources(), R.drawable.tile12);
                    buttons[i][j].setBackground(new BitmapDrawable(resource, bit3));
                    plantilla[i][j] = 3;
                    break;
                case 4:
                    Bitmap bit4 = BitmapFactory.decodeResource(getResources(), R.drawable.tile11);
                    buttons[i][j].setBackground(new BitmapDrawable(resource, bit4));
                    plantilla[i][j] = 4;
                    break;
                case 5:
                    Bitmap bit5 = BitmapFactory.decodeResource(getResources(), R.drawable.tile10);
                    buttons[i][j].setBackground(new BitmapDrawable(resource, bit5));
                    plantilla[i][j] = 5;
                    break;
                case 6:
                    Bitmap bit6 = BitmapFactory.decodeResource(getResources(), R.drawable.tile09);
                    buttons[i][j].setBackground(new BitmapDrawable(resource, bit6));
                    plantilla[i][j] = 6;
                    break;
                case 7:
                    Bitmap bit7 = BitmapFactory.decodeResource(getResources(), R.drawable.tile08);
                    buttons[i][j].setBackground(new BitmapDrawable(resource, bit7));
                    plantilla[i][j] = 7;
                    break;
                case 8:
                    Bitmap bit8 = BitmapFactory.decodeResource(getResources(), R.drawable.tile07);
                    buttons[i][j].setBackground(new BitmapDrawable(resource, bit8));
                    plantilla[i][j] = 8;
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // Un error se corrigió declarando este método sobre el siguiente.
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

    /**
     * Funcionalidad del menú de los tres puntos.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.instrucciones:
                AlertDialog.Builder instrucciones = new AlertDialog.Builder(this);
                instrucciones.setTitle("Instrucciones de juego");
                instrucciones.setMessage("· Se presenta un campo de minas, de dimensiones: \n\t8x8 - fácil\n\t12x12 - amateur\n\t16x16 - pro" +
                        "\n\n· Cada casilla que no contenga mina mostrará el número de minas en sus casillas contiguas." +
                        "\n\n· El objetivo es evitar las minas MARCÁNDOLAS TODAS CON UN CLICK LARGO (bandera de aviso de mina). " +
                        "Esta parte es necesaria tal y como está implementado el juego por el momento." +
                        "\n\n· El juego finaliza cuando todas las casillas con mina han sido marcadas, cuando se pulsa una mina o cuando " +
                        "se añade un 'aviso de mina' donde no hay una mina.");
                instrucciones.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(MainActivity.this, "¡Diviértete!", Toast.LENGTH_LONG).show();
                    }
                });
                instrucciones.setNeutralButton("Pista", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(MainActivity.this, "Céntrate en los espacios vacíos, son tu mejor amigo", Toast.LENGTH_LONG).show();
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

    /**
     * Funcionalidad de cada elemento del ContextMenu
     * (los RadioButton) que cambian la dificultad.
     *
     * @param item
     * @return
     */
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