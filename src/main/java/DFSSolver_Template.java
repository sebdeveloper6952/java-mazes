import java.util.*;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║        Solver de Laberintos DFS — SU TAREA (Grupo B)        ║
 * ╠══════════════════════════════════════════════════════════════╣
 * ║  Implementar Búsqueda en Profundidad usando una PILA        ║
 * ║  (Stack).                                                   ║
 * ║                                                             ║
 * ║  Conceptos clave:                                           ║
 * ║    - Stack = LIFO (Last-In, First-Out / Último en           ║
 * ║      entrar, primero en salir)                              ║
 * ║    - Se mete lo más profundo posible antes de retroceder    ║
 * ║    - Encuentra UN camino, pero NO necesariamente el más     ║
 * ║      corto                                                  ║
 * ║                                                             ║
 * ║  Clases útiles de Java:                                     ║
 * ║    - Stack<int[]>                                           ║
 * ║    - stack.push(elemento)  → empujar al tope                ║
 * ║    - stack.pop()           → sacar del tope                 ║
 * ║    - stack.isEmpty()       → verificar si está vacía        ║
 * ║    - HashMap<Long, int[]> para rastrear celdas padre        ║
 * ║    - HashSet<Long> para rastrear celdas visitadas           ║
 * ║                                                             ║
 * ║  Completen las secciones TODO. NO renombren la clase.       ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
public class DFSSolver implements MazeSolver {

    // La cuadrícula del laberinto (0 = pared, 1 = camino). NO modificar.
    private int[][] maze;
    private int[] start;
    private int[] end;

    // ──────────────────────────────────────────
    //  TODO 1: Declaren sus estructuras de datos aquí
    // ──────────────────────────────────────────
    // Van a necesitar:
    //   - Un Stack<int[]> para la frontera del DFS
    //   - Un Set<Long> para rastrear qué celdas ya visitaron
    //   - Un Map<Long, int[]> para recordar cómo llegaron a cada celda (para reconstruir el camino)
    //   - Un List<int[]> para almacenar las celdas visitadas en el paso actual
    //   - Un List<int[]> para el camino final una vez encontrado



    // Direcciones: arriba, abajo, izquierda, derecha (desplazamientos en fila, columna)
    private final int[][] DIRS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    /**
     * Helper: convierte (fila, columna) a una clave long única para usar en Sets/Maps.
     * Uso: long key = toKey(fila, columna);
     */
    private long toKey(int r, int c) {
        return ((long) r << 32) | (c & 0xFFFFFFFFL);
    }

    @Override
    public void init(int[][] maze, int[] start, int[] end) {
        this.maze = maze;
        this.start = start;
        this.end = end;

        // ──────────────────────────────────────────
        //  TODO 2: Inicialicen sus estructuras de datos
        // ──────────────────────────────────────────
        // - Creen su Stack, conjunto de visitados y mapa de padres
        // - Empujen la celda inicial al stack
        // - Marquen la celda inicial como visitada


    }

    @Override
    public boolean step() {
        // ──────────────────────────────────────────
        //  TODO 3: Implementen UN paso de DFS
        // ──────────────────────────────────────────
        // Limpien la lista de últimos visitados
        //
        // Si el stack está vacío → retornar true (no hay camino)
        //
        // Saquen UNA celda del TOPE del stack (¡LIFO!)
        // Agréguela a la lista de últimos visitados
        //
        // Si esta celda es el final → reconstruir camino, retornar true
        //
        // Para cada dirección (arriba, abajo, izquierda, derecha):
        //   Calculen la posición del vecino
        //   Si el vecino está dentro de los límites Y es camino (==1) Y no fue visitado:
        //     Márquenlo como visitado
        //     Registren su padre (celda actual) en el mapa de padres
        //     Empujen el vecino al TOPE del stack
        //
        // Retornar false (todavía no terminamos)

        return true; // ← Reemplacen esto con su implementación
    }

    @Override
    public List<int[]> getVisitedThisStep() {
        // ──────────────────────────────────────────
        //  TODO 4: Retornen las celdas visitadas en el último paso
        // ──────────────────────────────────────────
        return Collections.emptyList(); // ← Reemplacen esto
    }

    @Override
    public List<int[]> getPath() {
        // ──────────────────────────────────────────
        //  TODO 5: Retornen el camino final del inicio al final
        // ──────────────────────────────────────────
        // Usen su mapa de padres para recorrer desde el final hasta el inicio:
        //   Empiecen en la celda final
        //   Mientras la celda actual tenga un padre:
        //     Agreguen la celda actual al camino
        //     Avancen al padre
        //   Agreguen la celda inicial
        //   Inviertan la lista (para que vaya de inicio → final)
        //
        // Si no se encontró camino, retornen una lista vacía.

        return Collections.emptyList(); // ← Reemplacen esto
    }

    @Override
    public String getName() {
        return "DFS (Stack)";
    }
}