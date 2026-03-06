import java.io.*;
import java.util.*;

public class P27_SalvarLaNavidad {
    static Lector sc;
    static StringBuilder output;

    public static void main(String[] args) throws IOException {
        // Inicializamos entrada y salida:
        inicializar(27);

        // Resolvemos el problema:
        solve();

        // Imprimimos la salida y cerramos el flujo de entrada:
        System.out.print(output);
        sc.cerrar();
    }

    public static void solve() throws IOException {
        // Leemos la palabra original:
        char[] palabra = sc.nextLine().toCharArray();

        // Creamos un array que recoja las ocurrencias de cada letra del alfabeto inglés
        // en 'palabra':
        int[] alfabeto = new int[26];

        for (char letra : palabra)
            alfabeto[letra - 'a']++;

        // Leemos cada palabra desordenada:
        while (sc.hasNext()) {
            String anagrama = sc.next();

            // > Si la palabra no tiene la misma longitud que la original, la descartamos
            // directamente:
            if (anagrama.length() != palabra.length)
                continue;

            // > Si miden lo mismo, vamos recorriendo los caracteres de 'anagrama'. En
            // cuanto el número de ocurrencias de alguna letra supere las ocurrencias de esa
            // misma letra en 'alfabeto', descartamos la palabra:
            int[] ocurrencias = new int[26];

            int i;
            for (i = 0; i < anagrama.length(); i++) {
                int letra = anagrama.charAt(i) - 'a';

                // >> Si ya eran iguales de antes, significa que ahora 'ocurrencias[letra]'
                // pasará a ser mayor:
                if (ocurrencias[letra] == alfabeto[letra])
                    break;

                ocurrencias[letra]++;
            }

            // > Si hemos salido del bucle a través del 'break', significa que el anagrama
            // no es válido:
            if (i < anagrama.length())
                continue;

            output.append(anagrama).append(" ");
        }
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