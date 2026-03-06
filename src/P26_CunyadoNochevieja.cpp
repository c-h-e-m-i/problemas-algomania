#include <bits/stdc++.h>

using namespace std;
using ll = long long;
using vi = vector<int>;
using vll = vector<ll>;
using vb = vector<bool>;
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

const int PROB = 26;
const bool
    ARCHIVO = true,
    CASOS = true;

void solve()
{
    // Leemos las variables:
    int n1, n2;
    ll m;
    cin >> n1 >> n2 >> m;

    /* Vamos a usar la siguiente lógica:
        - Si nuestra configuración inicial tenía, por ejemplo, 5 caras y 3 cruces (C C C C C X X X)
          y la final es "C C X X X X X C" -es decir, 3 caras y 5 cruces, ignorando el orden-, significa
          que, en la práctica, hemos realizado 2 cambios "útiles", siendo el resto "redundantes" (entendiendo
          "redundante" como girar 2 veces una misma moneda, obteniendo su posición inicial).
        - Partiendo de esto, para que este caso se haya dado, hemos necesitado 2 + 2*k cambios, siendo 'k' el
          número de cambios redundantes.
        - Si contamos el número de caras (o cruces) que han cambiado, podemos sacar el primer sumando. El cual,
          teniendo en cuenta la ecuación anterior, deberá tener la misma PARIDAD que 'm'.
        - Esto quiere decir que, si nos ocultan una de las monedas en el resultado final, podemos deducir su posición
          tomando aquella que haga que dicha paridad coincida con la de 'm'.
        - Por ejemplo, para el caso anterior, si nos hubieran dado la entrada "C C X - X X X C" y nos hubieran dicho
          que se han realizado 8 cambios, habríamos empezado contando el número de caras de la línea: 3.
          Luego habríamos calculado la diferencia entre el número de caras original y este: 5 - 3 = 2.
          Como 2 tiene la misma paridad que 8, significa que no debemos añadir ninguna cara más (pues si no la diferencia
          pasaría a ser 5 - 4 = 1, y ya no coincidiría en paridad). Por tanto, la moneda oculta es una CRUZ.
    */
    int caras = 0;
    FOR(i, 0, n1 + n2)
    {
        char moneda;
        cin >> moneda;

        if (moneda == 'C')
            caras++;
    }

    int dif = n1 - caras;
    PRINTLN((dif ^ m) & 1 ? 'C' : 'X');
}

int main()
{
    // Selección de entrada estándar:
    if (ARCHIVO)
    {
        string ruta = "../inputs/" + to_string(PROB) + ".txt";
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