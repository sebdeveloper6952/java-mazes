# Laboratorio: Resolver Laberintos con BFS vs DFS

## Descripción General

En este laboratorio van a implementar un algoritmo para resolver laberintos usando **Búsqueda en Amplitud (BFS)** o **Búsqueda en Profundidad (DFS)**. Van a ver su solver ejecutarse en tiempo real, observando cómo explora el laberinto celda por celda en la terminal. Al final, vamos a comparar ambos enfoques sobre el mismo laberinto para ver cómo la elección de estructura de datos lo cambia todo.

```
   Grupo A → BFS usando una Cola / Queue (FIFO)
   Grupo B → DFS usando una Pila / Stack (LIFO)
```

---

## Configuración

### Requisitos
- **Java 11** o superior (JDK, no solo JRE — necesitan `javac`)

### Archivos que Reciben

| Archivo | Descripción |
|---|---|
| `MazeSolver.java` | La interfaz que deben implementar. **No modificar.** |
| `MazeManager.java` | El generador de laberintos y visualizador. **No modificar.** |
| `BFSSolver_Template.java` | Plantilla para el **Grupo A**. Renombrar a `BFSSolver.java` antes de empezar. |
| `DFSSolver_Template.java` | Plantilla para el **Grupo B**. Renombrar a `DFSSolver.java` antes de empezar. |

### Primeros Pasos

```bash
# 1. mover su plantilla BFS
mv BFSSolver_Template.java BFSSolver.java

# 1. mover su plantilla  DFS
mv templates/DFSSolver_Template.java DFSSolver.java

# 2. Abrir su archivo (BFSSolver.java o DFSSolver.java) en su editor
#    y completar las 5 secciones TODO

# 3. Compilar todo
javac *.java

# 4. Ejecutar su solver
java MazeManager --solver BFSSolver
java MazeManager --solver DFSSolver
```

---

## Su Tarea

Abran su archivo de plantilla. Necesitan completar **5 secciones TODO**:

### TODO 1 — Declarar Estructuras de Datos
Declaren los campos que van a necesitar:
- **BFS**: `Queue<int[]>` (usen `LinkedList` como implementación)
- **DFS:** un `Stack<int[]>`
- Ambos: un `Set<Long>` para celdas visitadas, un `Map<Long, int[]>` para rastrear padres, y campos `List<int[]>` para las celdas visitadas en el paso actual y el camino final

### TODO 2 — Inicializar (método `init`)
Creen instancias de sus estructuras de datos. Agreguen la celda inicial a su Queue/Stack y márquenla como visitada.

### TODO 3 — Un Paso del Algoritmo (método `step`)
Este es el corazón de su solver. Cada llamada a `step()` debe:
1. Tomar **una** celda de su Queue/Stack
   - **BFS:** `queue.poll()` — toma del **frente** (FIFO)
   - **DFS:** `stack.pop()` — toma del **tope** (LIFO)
2. Verificar si es la meta
3. Si no, agregar los vecinos no visitados que sean camino a su Queue/Stack
   - **BFS:** `queue.add(vecino)` — agrega al **final**
   - **DFS:** `stack.push(vecino)` — agrega al **tope**
4. Retornar `false` (todavía buscando) o `true` (terminó)

### TODO 4 — Reportar Celdas Visitadas (método `getVisitedThisStep`)
Retornar la lista de celdas exploradas en el último paso (para la visualización).

### TODO 5 — Reconstruir el Camino (método `getPath`)
Una vez que encontraron la meta, recorran su mapa de padres desde el final hasta el inicio, y luego inviertan la lista.

---

## Formato del Laberinto

El laberinto es un arreglo 2D `int[][]`:
```
0 = pared  (no pueden caminar aquí)
1 = camino (pueden caminar aquí)
```

Las coordenadas son `{fila, columna}` donde `fila 0` es la parte superior y `columna 0` es la izquierda. El inicio siempre está en `{1, 1}` (esquina superior izquierda) y el final en `{size-2, size-2}` (esquina inferior derecha).

### Exploración de Vecinos

Para revisar las 4 direcciones desde la celda `{r, c}`:
```java
int[][] DIRS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // arriba, abajo, izquierda, derecha

for (int[] dir : DIRS) {
    int nr = r + dir[0];  // fila del vecino
    int nc = c + dir[1];  // columna del vecino

    // Verificar: dentro de límites, es camino, no visitado
    if (nr >= 0 && nr < maze.length &&
        nc >= 0 && nc < maze[0].length &&
        maze[nr][nc] == 1 &&
        !visited.contains(toKey(nr, nc))) {
        // Este es un vecino válido — agréguenlo a su Queue/Stack
    }
}
```

### El Helper `toKey`

Como Java no puede usar `int[]` directamente en un `HashSet` (los arreglos no implementan igualdad correctamente), les proveemos un helper que empaqueta `(fila, columna)` en un solo `long`:
```java
long key = toKey(row, col);
visited.add(key);                          // marcar como visitado
parentMap.put(key, new int[]{prevR, prevC}); // registrar padre
```

---

## Ejecución y Pruebas

### Solver Individual
```bash
java MazeManager --solver BFSSolver
java MazeManager --solver DFSSolver
```

### Cambiar Tamaño del Laberinto
```bash
java MazeManager --solver BFSSolver --size 11    # Pequeño (fácil de seguir paso a paso)
java MazeManager --solver BFSSolver --size 31    # Mediano
java MazeManager --solver BFSSolver --size 41    # Grande
```

### Cambiar Velocidad
```bash
java MazeManager --solver BFSSolver --delay 500  # Lento (500ms por paso)
java MazeManager --solver BFSSolver --delay 50   # Rápido
```

### Usar una Semilla Específica (laberinto reproducible)
```bash
java MazeManager --solver BFSSolver --seed 42
```

### Modo Comparación (después de que ambos grupos terminen)
```bash
java MazeManager --compare BFSSolver DFSSolver
java MazeManager --compare BFSSolver DFSSolver --size 31 --delay 80
```

---

## Qué Observar

Cuando su solver se ejecute, van a ver cómo las celdas se iluminan conforme son exploradas, y el camino final se resalta en verde cuando termina.

### Comportamiento de BFS (Grupo A)
- La exploración se expande hacia afuera como una **ola** desde el inicio
- Todas las celdas a distancia N se exploran antes que cualquier celda a distancia N+1
- El camino encontrado es **siempre el más corto**

### Comportamiento de DFS (Grupo B)
- La exploración **se mete a fondo** por un pasillo antes de retroceder
- Puede explorar celdas lejanas antes que las cercanas
- El camino encontrado **no necesariamente es el más corto**

### La Gran Revelación
Cuando ejecutemos `--compare`, ambos solvers atacan el mismo laberinto simultáneamente. Observen:
- Qué tan diferente explora cada uno
- Cuántos pasos necesita cada uno
- Cuál encuentra un camino más corto
- Que la **única diferencia en el código** es Queue vs Stack

---

## Errores Comunes

| Síntoma | Causa Probable |
|---|---|
| `ClassNotFoundException` | El archivo no está nombrado correctamente o no fue compilado |
| El solver nunca termina | Se les olvidó marcar celdas como visitadas → ciclo infinito |
| El camino está vacío | Se les olvidó registrar padres en el mapa de padres |
| El camino pasa por paredes | Se les olvidó verificar `maze[nr][nc] == 1` |
| `NullPointerException` | No inicializaron las estructuras de datos en `init()` |
| El solver termina de inmediato | `step()` siempre retorna `true` — revisen su lógica |

---

## Retos Extra

- **Bonus 1:** Agreguen un contador de pasos dentro de su solver e imprímanlo al final
- **Bonus 2:** Después del lab, implementen el OTRO algoritmo (el grupo BFS intenta DFS y viceversa) — les debería tomar unos 2 minutos porque solo cambian 2 líneas
- **Bonus 3:** Implementen búsqueda A* — creen `AStarSolver.java` implementando `MazeSolver`, usando un `PriorityQueue` ordenado por `distancia + heurística`

---