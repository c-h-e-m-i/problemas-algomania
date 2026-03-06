import java.io.*;
import java.util.*;

public class P23_ProyectoSemanal {
    static Lector sc;
    static StringBuilder output;
    static HashMap<Character, ArrayList<Pair>> tablero;
    static ArrayList<char[]> entrada;
    static ArrayList<Character> regiones;
    static int m;
    static ArrayList<Pair> sol;
    static boolean[] filas, columnas;

    public static void main(String[] args) throws IOException {
        // Inicializamos entrada y salida:
        inicializar(23);

        // Resolvemos el problema:
        solve();

        // Imprimimos la salida y cerramos el flujo de entrada:
        System.out.print(output);
        sc.cerrar();
    }

    public static void solve() throws IOException {
        // Creamos el tablero:
        crearTablero();

        /*
         * Nuestras soluciones serán arrays de 'r' elementos de tipo 'Pair' (siendo 'r'
         * el número de regiones del tablero) que contendrán la posición de la reina
         * colocada en cada región:
         */
        sol = new ArrayList<>();

        /*
         * Emplearemos un método de backtracking para resolver este problema:
         * 
         * - Iremos probando cada posición posible en cada región y ramificaremos
         * colocando sucesivamente una reina en una región tras otra, de modo que no
         * interfiera con las anteriores.
         * 
         * - Nos guardaremos dos arrays -'filas' y 'columnas'- que recojan,
         * respectivamente, en qué filas y columnas ya se han colocado reinas.
         * 
         * - Para asegurar que las reinas no estén colocadas de forma adyacente
         * diagonalmente respecto de otras reinas, verificaremos las 4 posiciones que
         * hagan esquina con cada una y recorreremos la lista de las reinas ya colocadas
         * para comprobar que en ninguna de esas 4 posiciones haya ya una reina:
         */
        if (!backtracking())
            output.append("IMPOSIBLE");
        else {
            output.append("POSIBLE\n");
            for (Pair reina : sol)
                entrada.get(reina.a)[reina.b] = 'X';

            for (char[] linea : entrada)
                output.append(linea).append('\n');
        }
    }

    public static void crearTablero() throws IOException {
        tablero = new HashMap<>();

        // Guardaremos la entrada de forma literal para poder imprimirla al final:
        entrada = new ArrayList<>();

        /*
         * Almacenaremos el tablero como un hash map que tendrá como claves el carácter
         * identificador de la región y como valores una lista de tuplas que marcarán
         * los intervalos de cada fila del tablero que corresponden a dicha región.
         *
         * Por ejemplo, si la primera fila del tablero es "111222211", existirá una
         * entrada en el hash map de clave '2' y valor {(0, 3), (0, 6)}, indicando que
         * las posiciones 3-6 (ambas inclusive) de la fila 0 del tablero están dentro de
         * la región '2':
         */

        /*
         * > Nos empezamos guardando el número de columnas ('m') del tablero para no
         * tener que estar consultando la longitud de cada línea que leamos y rellenamos
         * el hash map con la primera fila:
         */
        char[] linea;

        if (sc.hasNext()) {
            linea = sc.nextLine().toCharArray();
            m = linea.length;

            entrada.add(linea);
            rellenarTablero(linea, 0);
        }

        /* > A continuación, rellenamos el resto de filas de 'tablero': */
        int fila = 1;
        while (sc.hasNext()) {
            linea = sc.nextLine().toCharArray();
            entrada.add(linea);
            rellenarTablero(linea, fila);
            fila++;
        }

        // Ahora, nos guardamos los identificadores de las regiones:
        regiones = new ArrayList<>(tablero.keySet());

        // Finalmente, definimos arrays de filas y columnas visitadas:
        filas = new boolean[fila];
        columnas = new boolean[m];
    }

    public static void rellenarTablero(char[] linea, int fila) {
        /*
         * Si estamos al principio de la fila, guardamos la posición '(fila, 0)' en la
         * lista de intervalos de 'actual' en 'tablero':
         */
        Pair posInicial = new Pair(fila, 0);
        char actual = linea[0];

        if (!tablero.containsKey(actual))
            tablero.put(actual, new ArrayList<>(Arrays.asList(posInicial)));
        else
            tablero.get(actual).add(posInicial);

        /*
         * Carácter que guarda la última región que ha aparecido en la fila que estamos
         * leyendo (es decir, el carácter anterior al actual):
         */
        char ultRegion = actual;

        /*
         * Si no estamos al principio de la fila, comprobamos si hemos cambiado de
         * región, en cuyo caso añadimos la última posición visitada de la fila a la
         * lista de intervalos de 'ultRegion' y la posición actual a la lista de
         * intervalos de 'actual' en 'tablero':
         */
        for (int i = 1; i < m; i++) {
            // Leemos el carácter actual:
            actual = linea[i];

            if (ultRegion != actual) {

                // > Actualizamos los intervalos de 'ultRegion':
                Pair posAnterior = new Pair(fila, i - 1);

                /*
                 * >> 'ultRegion' siempre va a existir como clave en 'tablero', pues para cerrar
                 * su intervalo hemos tenido que abrirlo en algún momento previo:
                 */
                tablero.get(ultRegion).add(posAnterior);

                // > Actualizamos los intervalos de 'actual':
                Pair posActual = new Pair(fila, i);

                if (!tablero.containsKey(actual))
                    tablero.put(actual, new ArrayList<>(Arrays.asList(posActual)));
                else
                    tablero.get(actual).add(posActual);

            }

            // Nos guardamos el carácter anterior:
            ultRegion = actual;

        }

        /*
         * Si estamos al final de la fila, guardamos la posición '(fila, m - 1)' en la
         * lista de intervalos de 'actual' en 'tablero':
         */
        Pair posFinal = new Pair(fila, m - 1);

        /*
         * > Ya no tenemos que comprobar si existe o no la clave de 'actual', pues para
         * cerrar su intervalo en algún momento hemos tenido que abrirlo:
         */
        tablero.get(actual).add(posFinal);
    }

    public static boolean backtracking() {
        // Si la solución actual es completa (tiene tamaño 'r'), retornamos:
        if (sol.size() == regiones.size())
            return true;

        /*
         * Nos guardamos qué intervalos de posiciones del tablero se corresponden con la
         * región actual y cogemos el primero de ellos:
         */
        ArrayList<Pair> intervalos = tablero.get(regiones.get(sol.size()));
        Intervalo inter = new Intervalo(intervalos.get(0), intervalos.get(1));

        /*
         * Mientras siga habiendo intervalos por probar, iteramos dentro de sus
         * elementos:
         */
        int i = 0;
        while (true) {
            Pair reina = inter.sigValor();

            // Si nos hemos salido de un intervalo, pasamos al siguiente:
            if (reina == null) {
                i += 2;

                // Si nos quedamos sin intervalos, significa que no hay solución:
                if (i >= intervalos.size())
                    return false;

                inter = new Intervalo(intervalos.get(i), intervalos.get(i + 1));
                reina = inter.sigValor();
            }

            /*
             * Comprobamos que la posición de la reina actual sea válida y, si lo es, la
             * añadimos a la solución y pasamos a la región siguiente:
             */
            if (!filas[reina.a] && !columnas[reina.b] && !diagonal(reina)) {
                sol.add(reina);
                filas[reina.a] = true;
                columnas[reina.b] = true;

                if (backtracking())
                    return true;

                sol.remove(sol.size() - 1);
                filas[reina.a] = false;
                columnas[reina.b] = false;
            }
        }
    }

    /*
     * Devuelve 'true' si ya hay alguna reina adyacente diagonal a 'reina' en el
     * tablero:
     */
    static boolean diagonal(Pair reina) {
        int[] signos = { -1, 1 };
        for (int i : signos)
            for (int j : signos)
                if (sol.contains(new Pair(reina.a + i, reina.b + j)))
                    return true;

        return false;
    }

    // -------- CLASES Y MÉTODOS AUXILIARES --------
    public static void inicializar(int cap) throws IOException {
        File f = new File(String.format("inputs/%02d.txt", cap));
        sc = new Lector();
        sc.leerArchivo(f);
        output = new StringBuilder();
    }

    public static void inicializar() {
        sc = new Lector();
        sc.leerStd();
        output = new StringBuilder();
    }

    @SuppressWarnings("unused")
    private static class Lector {
        BufferedReader br;
        StringTokenizer st;

        Lector() {
            br = null;
            st = new StringTokenizer("");
        }

        void leerArchivo(File f) throws IOException {
            br = new BufferedReader(new FileReader(f));
        }

        void leerStd() {
            br = new BufferedReader(new InputStreamReader(System.in));
        }

        void cerrar() throws IOException {
            br.close();
        }

        boolean hasNext() throws IOException {
            if (st.hasMoreTokens())
                return true;
            String aux = br.readLine();
            if (aux == null)
                return false;
            st = new StringTokenizer(aux);
            return true;
        }

        String next() throws IOException {
            if (!st.hasMoreTokens()) {
                st = new StringTokenizer(br.readLine());
            }
            return st.nextToken();
        }

        String nextLine() throws IOException {
            if (!st.hasMoreTokens()) {
                return br.readLine();
            } else {
                StringBuilder resto = new StringBuilder();
                while (st.hasMoreTokens()) {
                    resto.append(st.nextToken()).append(" ");
                }
                return resto.toString().trim();
            }
        }

        byte nextByte() throws IOException {
            return Byte.parseByte(next());
        }

        short nextShort() throws IOException {
            return Short.parseShort(next());
        }

        int nextInt() throws IOException {
            return Integer.parseInt(next());
        }

        long nextLong() throws IOException {
            return Long.parseLong(next());
        }

        float nextFloat() throws IOException {
            return Float.parseFloat(next());
        }

        double nextDouble() throws IOException {
            return Double.parseDouble(next());
        }

        boolean nextBoolean() throws IOException {
            return Boolean.parseBoolean(next());
        }
    }

    @SuppressWarnings("unused")
    private static class Pair implements Comparable<Pair> {
        int a, b;

        Pair(int a, int b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public int compareTo(Pair p) {
            return this.a - p.a;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof Pair))
                return false;
            Pair p = (Pair) o;
            return p.a == this.a && p.b == this.b;
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b);
        }
    }

    @SuppressWarnings("unused")
    private static class Intervalo {
        Pair izq, der, actual;

        Intervalo(Pair izq, Pair der) {
            this.izq = izq;
            this.der = der;
            this.actual = izq;
        }

        /*
         * Es como un iterador de Python: va moviendo el par 'actual' de izquierda a
         * derecha en el intervalo hasta salirse de este, momento en el cual devuelve
         * 'null':
         */
        Pair sigValor() {
            if (actual.b <= der.b) {
                Pair aux = actual;
                actual = new Pair(actual.a, actual.b + 1);
                return aux;
            }

            return null;
        }
    }
}