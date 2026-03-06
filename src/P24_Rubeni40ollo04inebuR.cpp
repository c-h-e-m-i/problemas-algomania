#include <bits/stdc++.h>

using namespace std;
using ll = long long;
using vi = vector<int>;
using vvi = vector<vector<int>>;
using vll = vector<ll>;
using vvll = vector<vector<ll>>;
using vb = vector<bool>;
using vvb = vector<vector<bool>>;
using pii = pair<int, int>;
using pll = pair<ll, ll>;

// clang-format off
#define nl '\n'
#define pb push_back
#define FOR(i, a, b) for (int i = a; i < b; i++)
#define PRINTLN(a) cout << (a) << nl
#define PRINT(a) cout << (a) << " "
#define ARR(a, n) int a[n]; FOR(i, 0, n) cin >> a[i];
#define DBG(x) cerr << #x << " = " << x << nl
// clang-format on

const int PROB = 24;
const bool
    ARCHIVO = 1,
    CASOS = 0;

void solve()
{
    // Leemos cada nombre:
    string nombre;

    while (cin >> nombre)
    {
        int len = nombre.length();

        // Inicializamos la matriz 'dp' a 0s:
        vvi dp(len, vi(len, 0));

        // PROBLEMA DE CONTORNO: Palíndromos de longitud 1:
        FOR(i, 0, len)
        {
            dp[i][i] = 0;
        }

        // Vamos diagonal por diagonal de la matriz (solo rellenaremos el triángulo superior derecho)
        // de resultados intermedios:
        FOR(diag, 1, len)
        FOR(i, 0, len - diag)
        {
            int j = diag + i;

            if (nombre[i] == nombre[j])
            {
                dp[i][j] = dp[i + 1][j - 1];
            }
            else if (dp[i + 1][j] <= dp[i][j - 1])
            {
                dp[i][j] = dp[i + 1][j] + 1;
            }
            else
            {
                dp[i][j] = dp[i][j - 1] + 1;
            }
        }

        int p1 = 0, p2 = len - 1, inserciones_izq = 0;

        while (p1 <= p2)
        {
            if (nombre[p1] == nombre[p2])
            {
                p1++;
                p2--;
            }
            else
            {
                if (dp[p1 + 1 - inserciones_izq][p2 - inserciones_izq] <= dp[p1 - inserciones_izq][p2 - 1 - inserciones_izq])
                {
                    nombre.insert(p2 + 1, 1, nombre[p1]);
                    p1++;
                }
                else
                {
                    nombre.insert(p1, 1, nombre[p2]);
                    p1++;
                    inserciones_izq++;
                }
            }
        }

        PRINT(nombre.length() - len);
        PRINTLN(nombre);
    }
}

int main()
{
    // Selección de entrada estándar:
    if (ARCHIVO)
    {
        string ruta = "C:/Users/usuario/Desktop/cp-upv/AlgoMania/inputs/24.txt";
        freopen(ruta.c_str(), "r", stdin);
    }

    // Optimizaciones de I/O:
    ios::sync_with_stdio(0);
    cin.tie(0);

    // Manejo de casos de prueba:
    int t = 1;
    if (CASOS)
        cin >> t;

    // Resolución del problema:
    while (t--)
    {
        solve();
    }

    return 0;
}