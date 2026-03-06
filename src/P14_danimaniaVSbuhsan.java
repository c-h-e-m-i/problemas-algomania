import java.io.*;
import java.util.*;

public class P14_danimaniaVSbuhsan {
    static Lector sc;
    static StringBuilder output;

    public static void main(String[] args) throws IOException {
        // Inicializamos entrada y salida:
        inicializar(14);

        // Resolvemos el problema:
        int t = sc.nextInt();
        while (t-- > 0)
            solve();

        // Imprimimos la salida y cerramos el flujo de entrada:
        System.out.print(output);
        sc.cerrar();
    }

    public static void solve() throws IOException {
        // Empezamos calculando la suma de la secuencia de danimania:
        long n1 = sc.nextLong(), n2 = sc.nextLong(), k1 = sc.nextLong();

        /*
         * A continuación, para calcular 'e1', debemos tener en cuenta que su valor debe
         * ser igual a 'k1' + el número de múltiplos de 'n1' en el intervalo [1, 'e1'],
         * pues dicho número será la cantidad de valores que nos hemos saltado.
         *
         * > Escribiendo esto en forma de ecuación, obtenemos:
         * 
         * e1 - floor(e1 / n1) = k1
         * 
         * > Como no podemos despejar 'e1' en esa ecuación, debemos buscar otra manera.
         * 
         * > Una opción es realizar un cálculo incremental:
         * 
         * - Por ejemplo, si 'k1' = 20 y 'n1' = 3, partimos de que, como mínimo, el
         * valor de 'e1' será 20. A ese valor le sumamos todos los múltiplos de 3 en el
         * intervalo [1, 20] (la cantidad de veces que nos habremos saltado un valor en
         * ese rango).
         * 
         * - En este caso, obtendremos 26 de resultado. Si te das cuenta, al pasar de 20
         * a 26 hemos recorrido 2 nuevos múltiplos de 3: el 21 y el 24. Estos también
         * los tendremos que contabilizar. Por tanto, al 26 que teníamos le sumamos los
         * múltiplos de 3 en el intervalo [21, 26].
         * 
         * - Haremos esto sucesivamente hasta que no queden nuevos múltiplos que
         * contabilizar.
         */
        long e1 = k1, mult = multiplosEnIntervalo(n1, 1, k1);

        while (mult > 0) {
            long ant_e1 = e1;
            e1 += mult;
            mult = multiplosEnIntervalo(n1, ant_e1 + 1, e1);
        }

        /*
         * 'e1' nunca saldrá del bucle siendo múltiplo de 'n1', pues, si en la última
         * iteración 'e1' tomó un valor múltiplo, significa que 'ant_e1' era MENOR que
         * dicho múltiplo, así que 'multiplosEnIntervalo()' devolverá como mínimo 1.
         */

        /*
         * En la secuencia nueva de danimania, cada número será igual al anterior
         * multiplicado por 3, por lo que los 10 primeros elementos serán:
         * 
         * {e * 3^0, e * 3^1, e * 3^2, ... , e * 3^9}
         * 
         * > Por tanto, podemos calcular la suma de dichos elementos como:
         * 
         * suma = e * (3^0 + 3^1 + ... + 3^9)
         * 
         * > Podemos calcular el resultado del paréntesis partiendo de la siguiente
         * propiedad:
         * 
         * S = x^0 + x^2 + x^3 + ... + x^9
         * 
         * |
         * v
         * 
         * xS = x^1 + x^3 + x^4 + ... + x^10
         * 
         * |
         * v
         * 
         * xS - S = S * (x - 1) = x^10 - x^0
         * 
         * |
         * v
         * 
         * S = (x^10 - x^0) / (x - 1)
         */
        long danimania = e1 * ((long) Math.pow(3, 10) - 1) / 2;

        /*
         * La secuencia de buhsan será del tipo:
         *
         * {e + 8*0, e + 8*1, ... , e + 8*9}
         * 
         * > Por lo que podremos calcular su suma como:
         * 
         * suma = e * 10 + 8 * (0 + 1 + 2 + ... + 9)
         * 
         * > El resultado del paréntesis lo podemos calcular mediante la propiedad de
         * los números triangulares. En este caso, tenemos 5 parejas de valores que
         * suman 9, por lo que la suma de todas ellas será 5 * 9 = 45.
         * 
         * > Partiendo de esto, podemos calcular la 'e2' mínima que debe tener buhsan
         * para que la suma de su secuencia supere en 1 a la de danimania:
         */
        long e2_min = (danimania - 8 * 45) / 10 + 1;

        /*
         * Si 'e2_min' es múltiplo de 'n2', la aumentamos en 1 unidad.
         *
         * Un caso que no funciona si no pones esto es "3 37 7", pues 'e2_min' da 29489
         * antes de incrementarlo, que es múltiplo de 37:
         */
        if (e2_min % n2 == 0)
            e2_min++;

        // Con todo esto, ya podemos sacar nuestra 'k2':
        long k2 = e2_min - e2_min / n2;
        output.append(k2).append('\n');
    }

    public static long multiplosEnIntervalo(long base, long ini, long fin) {
        /*
         * No se pueden agrupar en (fin - ini + 1) / base porque estamos truncando el
         * resultado de cada división:
         */
        return fin / base - (ini - 1) / base;
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
            return p.a == this.a;
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b);
        }
    }
}