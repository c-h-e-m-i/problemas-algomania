import java.io.*;
import java.util.*;

public class P49_AlgoMarket {
    static Lector sc;
    static StringBuilder output;
    static final int PROB = 49, CASOS = 0, ARCHIVO = 1;

    // Variables del problema:
    static int num_art, num_ofertas, precios[], uds[], precio_ofertas[], uds_ofertas[][], ahorros[], coste_sin_oferta,
            ahorro_union;
    static ArrayList<Integer> mejores_ofertas, union_ofertas;

    @SuppressWarnings("unused")
    public static void main(String[] args) throws IOException {
        // Inicializamos entrada y salida:
        if (ARCHIVO > 0)
            inicializar(PROB);
        else
            inicializar();

        // Resolvemos el problema:
        int t = 1;
        if (CASOS > 0)
            t = sc.nextInt();

        while (t-- > 0)
            solve();

        // Imprimimos la salida y cerramos el flujo de entrada:
        System.out.print(output);
        sc.cerrar();
    }

    public static void solve() throws IOException {
        while (true) {
            // Leemos la entrada:
            String aux = sc.next();

            if (aux == null)
                return;

            num_art = Integer.parseInt(aux);

            precios = new int[num_art];
            uds = new int[num_art];
            rellenar(precios);
            rellenar(uds);

            num_ofertas = sc.nextInt();
            precio_ofertas = new int[num_ofertas];
            uds_ofertas = new int[num_ofertas][num_art];
            rellenar(precio_ofertas);
            int fila = 0;
            while (fila < num_ofertas)
                rellenar(uds_ofertas[fila++]);

            /*
             * Procederemos de la siguiente manera:
             * - Empezaremos ordenando cada oferta según su ahorro.
             * - Por ejemplo, si un objeto cuesta 5 monedas, y la oferta nos quita 2
             * unidades de este, el dinero ahorrado será de 10 monedas.
             * - Solo nos quedaremos con aquellas ofertas cuyo ahorro sea ppsitivo, pues el
             * resto no nos ayudarán a reducir el precio de los artículos.
             * - Meteremos las ofertas de coste negativo en un array ordenado, y obtendremos
             * una solución inicial VORAZ restando sucesivamente las mejores ofertas del
             * array siempre que no nos hagan comprar más unidades de las que necesitamos de
             * algún objeto.
             * 
             * - Acto seguido, emplearemos un algoritmo de RAMIFICACIÓN Y PODA para resolver
             * el problema, cuyos estados serán tuplas de valores (con posible repetición)
             * en [1, ..., num_ofertas], que recogerán las ofertas que hemos aplicado.
             * - Si nuestro estado a ramificar es, por ejemplo, [1, 3, 4] y tenemos 6
             * ofertas, sus hijos serán solo {[1, 3, 4, 4], [1, 3, 4, 5], [1, 3, 4, 6]}. Es
             * decir, los valores nuevos a añadir siempre serán mayores o iguales que el
             * último añadido. Esto lo haremos así para evitar estados repetidos (pues el
             * orden de las ofertas no es relevante aquí).
             * 
             * - El valor de la FUNCIÓN OBJETIVO será el precio base de los artículos que
             * necesitamos sumado al descuento que nos proporcionan las ofertas que hemos
             * canjeado.
             * 
             * - La COTA OPTIMISTA la calcularemos de la siguiente manera:
             * > Precalcularemos el conjunto mínimo de ofertas que, canjeadas juntas, nos
             * quitan al menos 1 unidad de todos los artículos. Esto es: OFERTA1 U OFERTA2 U
             * ... U OFERTAk >= {1, 1, ..., 1}.
             * > La excepción de esto será cuando algún artículo no esté cubierto por
             * ninguna oferta, en cuyo caso su número de unidades será 0.
             * > Iremos introduciendo las ofertas en la unión según el orden del array
             * mencionado más arriba (ahorro).
             * > Una vez obtenida dicha unión, calcularemos el precio total que se nos resta
             * del importe usando esa unión de ofertas, y lo multiplicaremos por el número
             * de unidades del artículo del que más unidades necesitemos actualmente.
             * > Por ejemplo, si necesitamos las unidades recogidas en este array para cada
             * artículo i: {1, 1, 2, 3, 1}, tendríamos que multiplicar el precio de la unión
             * por 3.
             * > IMPORTANTE: Si el artículo del que más unidades necesitamos no está
             * incluido en ninguna oferta, cogemos el siguiente. Y así sucesivamente hasta
             * que alguno esté cubierto.
             * > Hacemos esto pues, en el peor caso, todos los artículos tendrán {3, 3, 3,
             * 3, 3} unidades pendientes, y la unión será {1, 1, 1, 1, 1}, así que tendremos
             * que aplicar la unión de ofertas 3 veces.
             */

            // > Calculamos el precio total de los artículos sin ofertas:
            coste_sin_oferta = 0;
            int articulo = 0;
            while (articulo < num_art) {
                coste_sin_oferta += precios[articulo] * uds[articulo];
                articulo++;
            }

            // > Precalculamos los ahorros de cada oferta y las ordenamos según su ahorro:
            ahorros = new int[num_ofertas];
            mejores_ofertas = new ArrayList<>();
            int oferta = 0;
            while (oferta < num_ofertas) {
                int ahorro = ahorro(oferta);
                ahorros[oferta] = ahorro;
                if (ahorro > 0)
                    mejores_ofertas.add(oferta);
                oferta++;
            }

            Collections.sort(mejores_ofertas, (a, b) -> ahorros[b] - ahorros[a]);

            // > Calculamos la unión de ofertas que nos quite al menos 1 artículo de cada
            // tipo:
            union_ofertas = new ArrayList<>();
            ahorro_union = 0;
            BitSet utilizados = new BitSet(num_art);
            for (int ind_oferta : mejores_ofertas) {
                int of[] = uds_ofertas[ind_oferta];
                boolean utilizada = false;
                for (int artic = 0; artic < num_art; artic++)
                    if (of[artic] > 0) {
                        utilizados.set(artic);
                        utilizada = true;
                    }

                if (utilizada) {
                    union_ofertas.add(ind_oferta);
                    ahorro_union += ahorros[ind_oferta];
                }

                if (utilizados.cardinality() == num_art)
                    break;
            }

            // > Calculamos la solución voraz inicial:
            int f = sol_ini().fx;

            // > Aplicamos ramificación y poda para encontrar la solución óptima:
            Pair estado_ini = new Pair(new ArrayList<>(), 0);
            estado_ini.ult_ind = 0;
            System.arraycopy(uds, 0, estado_ini.uds_copia, 0, num_art);
            estado_ini.cota = cota_optimista(estado_ini);

            PriorityQueue<Pair> estados_pendientes = new PriorityQueue<>();
            estados_pendientes.offer(estado_ini);

            while (!estados_pendientes.isEmpty()) {
                Pair estado = estados_pendientes.poll();

                if (estado.cota <= f)
                    break;

                if (estado.fx > f)
                    f = estado.fx;

                ArrayList<Pair> hijos = ramificar(estado);
                for (Pair hijo : hijos)
                    estados_pendientes.offer(hijo);
            }

            // > Lo que tengamos en 'f' es el ahorro máximo que hemos conseguido con las
            // ofertas. Por tanto, obtendremos el resultado final restando ese ahorro del
            // precio inicial de los artículos:
            output.append(coste_sin_oferta - f).append('\n');
        }
    }

    // MÉTODO AUXILIAR: Calculamos la función objetivo (el ahorro total de las
    // ofertas) de una solución dada:
    public static int fx(ArrayList<Integer> sol) {
        int ahorro_total = 0;

        for (int i : sol)
            ahorro_total += ahorros[i];

        return ahorro_total;
    }

    // MÉTODO AUXILIAR: Calculamos la cota optimista de un estado:
    static int cota_optimista(Pair estado) {
        // Empezamos calculando el artículo del que necesitemos más unidades
        // actualmente:
        int max = -1;
        for (int ud : estado.uds_copia)
            if (ud > max)
                max = ud;

        // A continuación, multiplicamos 'max' por el ahorro de la unión de ofertas que
        // calculamos antes:
        return estado.fx + max * ahorro_union;
    }

    // MÉTODO AUXILIAR: Ramificamos el estado actual añadiendo las ofertas en
    // 'mejores_ofertas' de índice mayor a la última oferta añadida:
    static ArrayList<Pair> ramificar(Pair estado) {
        ArrayList<Pair> hijos = new ArrayList<>();

        for (int i = estado.ult_ind; i < mejores_ofertas.size(); i++) {
            int ind_oferta = mejores_ofertas.get(i);
            if (aplicable(ind_oferta, estado.uds_copia))
                hijos.add(estado.nueva_oferta(ind_oferta, i));
        }

        return hijos;
    }

    // MÉTODO AUXILIAR: Calculamos la solución voraz inicial:
    static Pair sol_ini() {
        ArrayList<Integer> res = new ArrayList<>();
        int[] uds_copia = new int[num_art];
        System.arraycopy(uds, 0, uds_copia, 0, num_art);

        // Vamos sacando las mejores ofertas de la lista y las añadimos siempre
        // que sean aplicables:
        int i = 0;
        while (i < mejores_ofertas.size() && !ceros(uds_copia)) {
            int oferta = mejores_ofertas.get(i);

            while (aplicable(oferta, uds_copia)) {
                aplicar(oferta, uds_copia);
                res.add(oferta);
            }

            i++;
        }

        return new Pair(res, fx(res));
    }

    // MÉTODO AUXILIAR: Comprobamos si una oferta es aplicable a nuestra lista de
    // artículos o no (entiendo "aplicable" como que no nos da más unidades de las
    // que necesitamos para ningún artículo):
    public static boolean aplicable(int ind_oferta, int[] uds) {
        int[] oferta = uds_ofertas[ind_oferta];

        for (int i = 0; i < num_art; i++)
            if (uds[i] < oferta[i])
                return false;

        return true;
    }

    // MÉTODO AUXILIAR: Aplicamos la oferta de índice 'ind_oferta' a la lista de
    // artículos 'uds':
    public static void aplicar(int ind_oferta, int[] uds) {
        int[] oferta = uds_ofertas[ind_oferta];

        for (int i = 0; i < num_art; i++)
            uds[i] -= oferta[i];
    }

    // MÉTODO AUXILIAR: Comprueba si un array está todo a ceros:
    public static boolean ceros(int[] array) {
        for (int i : array)
            if (i != 0)
                return false;

        return true;
    }

    // MÉTODO AUXILIAR: Calculamos el ahorro asociado a una oferta:
    public static int ahorro(int ind_oferta) {
        int res = 0, oferta[] = uds_ofertas[ind_oferta];

        for (int i = 0; i < num_art; i++)
            res += oferta[i] * precios[i];

        return res - precio_ofertas[ind_oferta];
    }

    // MÉTODO AUXILIAR: Rellenamos un array de enteros:
    public static void rellenar(int[] array) throws IOException {
        for (int i = 0; i < array.length; i++)
            array[i] = sc.nextInt();
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
                String linea = br.readLine();
                if (linea == null)
                    return null;
                st = new StringTokenizer(linea);
            }
            return st.nextToken();
        }

        String nextLine() throws IOException {
            if (st.hasMoreTokens()) {
                StringBuilder resto = new StringBuilder();
                while (st.hasMoreTokens()) {
                    resto.append(st.nextToken()).append(" ");
                }
                return resto.toString();
            }
            return br.readLine();
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

    private static class Pair implements Comparable<Pair> {
        ArrayList<Integer> ofertas;
        int uds_copia[], fx, ult_ind, cota;

        Pair(ArrayList<Integer> a, int b) {
            this.uds_copia = new int[num_art];
            this.ofertas = a;
            this.fx = b;
        }

        Pair nueva_oferta(int ind_oferta, int ind_en_lista) {
            ArrayList<Integer> actualizado = new ArrayList<>(this.ofertas);
            actualizado.add(ind_oferta);
            Pair res = new Pair(actualizado, this.fx + ahorros[ind_oferta]);
            res.ult_ind = ind_en_lista;
            System.arraycopy(this.uds_copia, 0, res.uds_copia, 0, num_art);
            aplicar(ind_oferta, res.uds_copia);
            res.cota = cota_optimista(res);
            return res;
        }

        @Override
        public int compareTo(Pair p) {
            return p.cota - this.cota;
        }
    }
}