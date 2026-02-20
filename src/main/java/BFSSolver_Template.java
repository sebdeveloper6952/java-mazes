import java.util.*;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║         Solver de Laberintos BFS — SU TAREA (Grupo A)       ║
 * ╠══════════════════════════════════════════════════════════════╣
 * ║  Implementar Búsqueda en Amplitud usando una COLA (Queue).  ║
 * ║                                                             ║
 * ║  Conceptos clave:                                           ║
 * ║    - Queue = FIFO (First-In, First-Out / Primero en         ║
 * ║      entrar, primero en salir)                              ║
 * ║    - Explora celdas nivel por nivel (las más cercanas       ║
 * ║      primero)                                               ║
 * ║    - Garantiza encontrar el camino MÁS CORTO               ║
 * ║                                                             ║
 * ║  Clases útiles de Java:                                     ║
 * ║    - LinkedList<int[]> implementa Queue<int[]>              ║
 * ║    - queue.add(elemento)  → encolar al final                ║
 * ║    - queue.poll()         → desencolar del frente           ║
 * ║    - queue.isEmpty()      → verificar si está vacía         ║
 * ║    - HashMap<Long, int[]> para rastrear celdas padre        ║
 * ║    - HashSet<Long> para rastrear celdas visitadas           ║
 * ║                                                             ║
 * ║  Completen las secciones TODO. NO renombren la clase.       ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
public class BFSSolver implements MazeSolver {

    // La cuadrícula del laberinto (0 = pared, 1 = camino). NO modificar.
    private int[][] maze;
    private int[] start;
    private int[] end;

    // ──────────────────────────────────────────
    //  TODO 1: Declaren sus estructuras de datos aquí
    // ──────────────────────────────────────────
    // Van a necesitar:
    //   - Un Queue<int[]> para la frontera del BFS (usen LinkedList)
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
        // - Creen su Queue, conjunto de visitados y mapa de padres
        // - Agreguen la celda inicial a la queue
        // - Marquen la celda inicial como visitada


    }

    @Override
    public boolean step() {
        // ──────────────────────────────────────────
        //  TODO 3: Implementen UN paso de BFS
        // ──────────────────────────────────────────
        // Limpien la lista de últimos visitados
        //
        // Si la queue está vacía → retornar true (no hay camino)
        //
        // Saquen UNA celda del FRENTE de la queue (¡FIFO!)
        // Agréguela a la lista de últimos visitados
        //
        // Si esta celda es el final → reconstruir camino, retornar true
        //
        // Para cada dirección (arriba, abajo, izquierda, derecha):
        //   Calculen la posición del vecino
        //   Si el vecino está dentro de los límites Y es camino (==1) Y no fue visitado:
        //     Márquenlo como visitado
        //     Registren su padre (celda actual) en el mapa de padres
        //     Agreguen el vecino al FINAL de la queue
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
        return "BFS (Queue)";
    }
}