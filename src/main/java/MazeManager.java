import java.util.*;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║                     MazeManager                             ║
 * ║       Maze generation, visualization, and solver runner     ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * USAGE:
 *   javac *.java
 *
 *   # Run a single solver:
 *   java MazeManager --solver BFSSolver
 *   java MazeManager --solver DFSSolver --size 31 --delay 50
 *
 *   # Compare two solvers side by side:
 *   java MazeManager --compare BFSSolver DFSSolver
 *   java MazeManager --compare BFSSolver DFSSolver --size 25 --delay 80
 *
 *   # Options:
 *   --size  N     Maze size (odd number, 11-51, default 21)
 *   --delay N     Milliseconds between steps (10-2000, default 100)
 *   --seed  N     Random seed for reproducible mazes
 *   --nocolor     Disable ANSI colors (for terminals that don't support them)
 */
public class MazeManager {

    // ─────────────────────────────────────────────
    //  ANSI COLOR CODES
    // ─────────────────────────────────────────────
    private static boolean useColor = true;

    private static final String RESET   = "\033[0m";
    private static final String BOLD    = "\033[1m";

    // Foreground
    private static final String BLACK   = "\033[30m";
    private static final String WHITE   = "\033[97m";

    // Background colors
    private static final String BG_WALL      = "\033[48;5;235m";   // Dark gray
    private static final String BG_PATH      = "\033[48;5;255m";   // Light
    private static final String BG_START     = "\033[42m";         // Green
    private static final String BG_END       = "\033[41m";         // Red
    private static final String BG_VISITED_1 = "\033[48;5;33m";   // Blue (BFS)
    private static final String BG_VISITED_2 = "\033[48;5;208m";  // Orange (DFS)
    private static final String BG_SOLVED_1  = "\033[48;5;21m";   // Deep blue (BFS path)
    private static final String BG_SOLVED_2  = "\033[48;5;202m";  // Deep orange (DFS path)
    private static final String BG_FINAL     = "\033[48;5;46m";   // Bright green (final solved path)

    // Cell display states
    private static final int CELL_WALL    = 0;
    private static final int CELL_PATH    = 1;
    private static final int CELL_START   = 2;
    private static final int CELL_END     = 3;
    private static final int CELL_VISITED = 4;
    private static final int CELL_SOLVED  = 5;
    private static final int CELL_FINAL   = 6;

    // ─────────────────────────────────────────────
    //  MAZE GENERATION — Recursive Backtracker
    // ─────────────────────────────────────────────

    /**
     * Generates a maze using iterative recursive backtracker, then removes
     * extra interior walls to create branches, loops, and a more open feel.
     *
     * @param size       side length (must be odd)
     * @param rng        Random instance for reproducibility
     * @return 2D array: 0=wall, 1=path
     */
    public static int[][] generateMaze(int size, Random rng) {
        if (size % 2 == 0) size++;
        int[][] maze = new int[size][size];

        // ── Phase 1: Carve a perfect maze (recursive backtracker) ──
        int[][] carveDirs = {{0, 2}, {0, -2}, {2, 0}, {-2, 0}};

        Stack<int[]> stack = new Stack<>();
        maze[1][1] = 1;
        stack.push(new int[]{1, 1});

        while (!stack.isEmpty()) {
            int[] current = stack.peek();
            int cr = current[0], cc = current[1];

            List<int[]> unvisited = new ArrayList<>();
            for (int[] dir : carveDirs) {
                int nr = cr + dir[0], nc = cc + dir[1];
                if (nr > 0 && nr < size - 1 && nc > 0 && nc < size - 1
                        && maze[nr][nc] == 0) {
                    unvisited.add(dir);
                }
            }

            if (!unvisited.isEmpty()) {
                int[] dir = unvisited.get(rng.nextInt(unvisited.size()));
                int br = cr + dir[0] / 2, bc = cc + dir[1] / 2;
                int nr = cr + dir[0],     nc = cc + dir[1];
                maze[br][bc] = 1;
                maze[nr][nc] = 1;
                stack.push(new int[]{nr, nc});
            } else {
                stack.pop();
            }
        }

        // ── Phase 2: Remove extra walls to create branches and loops ──
        // Collect all interior walls that separate two path cells.
        // Removing these creates alternative routes and a more open maze.
        List<int[]> removableWalls = new ArrayList<>();

        for (int r = 1; r < size - 1; r++) {
            for (int c = 1; c < size - 1; c++) {
                if (maze[r][c] == 0) {
                    // Horizontal wall: path on left and right
                    if (c - 1 >= 0 && c + 1 < size &&
                            maze[r][c - 1] == 1 && maze[r][c + 1] == 1) {
                        removableWalls.add(new int[]{r, c});
                    }
                    // Vertical wall: path above and below
                    else if (r - 1 >= 0 && r + 1 < size &&
                            maze[r - 1][c] == 1 && maze[r + 1][c] == 1) {
                        removableWalls.add(new int[]{r, c});
                    }
                }
            }
        }

        // Shuffle and remove ~30% of removable walls
        Collections.shuffle(removableWalls, rng);
        int toRemove = (int)(removableWalls.size() * 0.30);
        for (int i = 0; i < toRemove; i++) {
            int[] wall = removableWalls.get(i);
            maze[wall[0]][wall[1]] = 1;
        }

        return maze;
    }

    // ─────────────────────────────────────────────
    //  DISPLAY ENGINE
    // ─────────────────────────────────────────────

    /**
     * Renders one or two maze display grids side by side.
     * Uses ANSI escape codes for color and cursor repositioning.
     */
    private static void renderMazes(int[][] display1, int[][] display2,
                                    String name1, String name2,
                                    int steps1, int steps2,
                                    int pathLen1, int pathLen2,
                                    boolean done1, boolean done2) {
        int size = display1.length;
        StringBuilder sb = new StringBuilder();

        // Move cursor to top-left
        sb.append("\033[H");

        boolean sideBySide = (display2 != null);

        // Header
        if (sideBySide) {
            String header1 = centerPad(name1, size * 2);
            String header2 = centerPad(name2, size * 2);
            sb.append(col(BOLD + "\033[34m")).append(header1).append(col(RESET));
            sb.append("    ");
            sb.append(col(BOLD + "\033[38;5;208m")).append(header2).append(col(RESET));
            sb.append("\n");
        } else {
            String header = centerPad(name1, size * 2);
            sb.append(col(BOLD + "\033[34m")).append(header).append(col(RESET)).append("\n");
        }

        // Maze rows
        for (int r = 0; r < size; r++) {
            // First maze
            for (int c = 0; c < size; c++) {
                sb.append(cellToString(display1[r][c], true));
            }
            sb.append(col(RESET));

            if (sideBySide) {
                sb.append("    "); // gap
                for (int c = 0; c < size; c++) {
                    sb.append(cellToString(display2[r][c], false));
                }
                sb.append(col(RESET));
            }
            sb.append("\n");
        }

        // Stats line
        sb.append("\n");
        if (sideBySide) {
            String stat1 = String.format(" Steps: %-6d Path: %-6s",
                    steps1, done1 ? (pathLen1 > 0 ? "" + pathLen1 : "none") : "...");
            String stat2 = String.format(" Steps: %-6d Path: %-6s",
                    steps2, done2 ? (pathLen2 > 0 ? "" + pathLen2 : "none") : "...");
            stat1 = padRight(stat1, size * 2);
            stat2 = padRight(stat2, size * 2);
            sb.append(col(BOLD + "\033[34m")).append(stat1).append(col(RESET));
            sb.append("    ");
            sb.append(col(BOLD + "\033[38;5;208m")).append(stat2).append(col(RESET));
        } else {
            sb.append(col(BOLD)).append(String.format(" Steps: %d  |  Path: %s",
                            steps1, done1 ? (pathLen1 > 0 ? "" + pathLen1 : "none") : "..."))
                    .append(col(RESET));
        }
        sb.append("\n");

        System.out.print(sb);
        System.out.flush();
    }

    private static String cellToString(int state, boolean isFirst) {
        String bg;
        String ch = "  "; // two spaces per cell for square-ish look
        switch (state) {
            case CELL_WALL:    bg = BG_WALL; break;
            case CELL_PATH:    bg = BG_PATH; break;
            case CELL_START:   bg = BG_START; ch = col(BOLD + WHITE) + "S "; break;
            case CELL_END:     bg = BG_END;   ch = col(BOLD + WHITE) + "E "; break;
            case CELL_VISITED: bg = isFirst ? BG_VISITED_1 : BG_VISITED_2; break;
            case CELL_SOLVED:  bg = isFirst ? BG_SOLVED_1  : BG_SOLVED_2;
                ch = col(BOLD + WHITE) + "██"; break;
            case CELL_FINAL:   bg = "";
                ch = "\033[32;1m" + "██"; break;
            default:           bg = BG_PATH; break;
        }
        return col(bg) + ch;
    }

    /** Wraps string with color code, or returns plain if colors disabled */
    private static String col(String code) {
        return useColor ? code : "";
    }

    private static String centerPad(String s, int width) {
        if (s.length() >= width) return s;
        int pad = (width - s.length()) / 2;
        return " ".repeat(pad) + s + " ".repeat(width - s.length() - pad);
    }

    private static String padRight(String s, int width) {
        if (s.length() >= width) return s;
        return s + " ".repeat(width - s.length());
    }

    // ─────────────────────────────────────────────
    //  BUILD DISPLAY GRID FROM MAZE + SOLVE STATE
    // ─────────────────────────────────────────────

    private static int[][] buildDisplay(int[][] maze, int[] start, int[] end,
                                        Set<Long> visited, List<int[]> path) {
        int size = maze.length;
        int[][] display = new int[size][size];

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (r == start[0] && c == start[1]) {
                    display[r][c] = CELL_START;
                } else if (r == end[0] && c == end[1]) {
                    display[r][c] = CELL_END;
                } else if (maze[r][c] == 0) {
                    display[r][c] = CELL_WALL;
                } else if (visited.contains(coordKey(r, c))) {
                    display[r][c] = CELL_VISITED;
                } else {
                    display[r][c] = CELL_PATH;
                }
            }
        }

        // Overlay final path
        if (path != null) {
            for (int[] cell : path) {
                if ((cell[0] != start[0] || cell[1] != start[1]) &&
                        (cell[0] != end[0]   || cell[1] != end[1])) {
                    display[cell[0]][cell[1]] = CELL_SOLVED;
                }
            }
        }

        return display;
    }

    /** Pack row,col into a single long for fast set lookups */
    private static long coordKey(int r, int c) {
        return ((long) r << 32) | (c & 0xFFFFFFFFL);
    }

    /**
     * Builds a clean display showing ONLY the maze and the solved path in green.
     * Visited cells are reset to normal path color so the final path stands out.
     */
    private static int[][] buildFinalDisplay(int[][] maze, int[] start, int[] end,
                                             List<int[]> path) {
        int size = maze.length;
        int[][] display = new int[size][size];

        // Base maze: just walls and paths
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                display[r][c] = maze[r][c] == 0 ? CELL_WALL : CELL_PATH;
            }
        }

        // Overlay final path in green
        if (path != null) {
            for (int[] cell : path) {
                display[cell[0]][cell[1]] = CELL_FINAL;
            }
        }

        // Start and end on top
        display[start[0]][start[1]] = CELL_START;
        display[end[0]][end[1]] = CELL_END;

        return display;
    }

    // ─────────────────────────────────────────────
    //  SOLVER RUNNER
    // ─────────────────────────────────────────────

    /**
     * Runs one or two solvers with animated output.
     */
    private static void runSolvers(MazeSolver solver1, MazeSolver solver2,
                                   int[][] maze, int[] start, int[] end,
                                   int delay) throws Exception {
        int size = maze.length;

        // Init solvers
        solver1.init(maze, start.clone(), end.clone());
        if (solver2 != null) {
            solver2.init(maze, start.clone(), end.clone());
        }

        // Tracking sets (for display — we read from solver's getVisitedThisStep)
        Set<Long> visited1 = new HashSet<>();
        Set<Long> visited2 = new HashSet<>();
        visited1.add(coordKey(start[0], start[1]));
        if (solver2 != null) visited2.add(coordKey(start[0], start[1]));

        boolean done1 = false, done2 = false;
        int steps1 = 0, steps2 = 0;
        List<int[]> path1 = null, path2 = null;

        // Clear screen
        System.out.print("\033[2J");

        while (!done1 || (solver2 != null && !done2)) {
            // Step solver 1
            if (!done1) {
                try {
                    done1 = solver1.step();
                    steps1++;
                    for (int[] cell : solver1.getVisitedThisStep()) {
                        visited1.add(coordKey(cell[0], cell[1]));
                    }
                    if (done1) {
                        path1 = solver1.getPath();
                    }
                } catch (Exception e) {
                    System.err.println("\n" + col(BOLD + "\033[31m") +
                            "ERROR in " + solver1.getName() + ": " + e.getMessage() +
                            col(RESET));
                    e.printStackTrace();
                    done1 = true;
                }
            }

            // Step solver 2
            if (solver2 != null && !done2) {
                try {
                    done2 = solver2.step();
                    steps2++;
                    for (int[] cell : solver2.getVisitedThisStep()) {
                        visited2.add(coordKey(cell[0], cell[1]));
                    }
                    if (done2) {
                        path2 = solver2.getPath();
                    }
                } catch (Exception e) {
                    System.err.println("\n" + col(BOLD + "\033[31m") +
                            "ERROR in " + solver2.getName() + ": " + e.getMessage() +
                            col(RESET));
                    e.printStackTrace();
                    done2 = true;
                }
            }

            // Render
            int[][] display1 = buildDisplay(maze, start, end, visited1, path1);
            int[][] display2 = solver2 != null ?
                    buildDisplay(maze, start, end, visited2, path2) : null;

            int pathLen1 = path1 != null ? path1.size() : 0;
            int pathLen2 = path2 != null ? path2.size() : 0;

            renderMazes(display1, display2,
                    solver1.getName(),
                    solver2 != null ? solver2.getName() : "",
                    steps1, steps2, pathLen1, pathLen2, done1, done2);

            Thread.sleep(delay);
        }

        // Final text summary (printed first, will be overwritten by final render)
        // We store it and print AFTER the final maze render below the maze area

        // ── Final render: clean maze with green solved path ──
        Thread.sleep(500); // Brief pause before the reveal

        int[][] finalDisplay1 = buildFinalDisplay(maze, start, end, path1);
        int[][] finalDisplay2 = solver2 != null ?
                buildFinalDisplay(maze, start, end, path2) : null;

        int pathLen1Final = path1 != null ? path1.size() : 0;
        int pathLen2Final = path2 != null ? path2.size() : 0;

        // Clear screen fully and render the green maze
        System.out.print("\033[2J");
        renderMazes(finalDisplay1, finalDisplay2,
                solver1.getName() + " ✓",
                solver2 != null ? solver2.getName() + " ✓" : "",
                steps1, steps2, pathLen1Final, pathLen2Final, true, true);

        // Now print results BELOW the maze (no cursor-home, just append)
        System.out.println();
        System.out.println(col(BOLD) + "═══════════════════════════════════════" + col(RESET));
        System.out.println(col(BOLD) + "  RESULTS" + col(RESET));
        System.out.println(col(BOLD) + "═══════════════════════════════════════" + col(RESET));

        printResult(solver1.getName(), steps1, path1);
        if (solver2 != null) {
            printResult(solver2.getName(), steps2, path2);

            // Comparison verdict
            System.out.println();
            if (path1 != null && path2 != null && !path1.isEmpty() && !path2.isEmpty()) {
                if (path1.size() < path2.size()) {
                    System.out.println(col(BOLD + "\033[32m") +
                            "  → " + solver1.getName() + " found a shorter path!" + col(RESET));
                } else if (path2.size() < path1.size()) {
                    System.out.println(col(BOLD + "\033[32m") +
                            "  → " + solver2.getName() + " found a shorter path!" + col(RESET));
                } else {
                    System.out.println(col(BOLD) +
                            "  → Both found paths of equal length." + col(RESET));
                }
            }
        }
        System.out.println();
    }

    private static void printResult(String name, int steps, List<int[]> path) {
        System.out.printf("  %-20s  Steps: %-6d  Path length: %s%n",
                name, steps,
                (path != null && !path.isEmpty()) ? path.size() : "no path found");
    }

    // ─────────────────────────────────────────────
    //  SOLVER INSTANTIATION (by class name)
    // ─────────────────────────────────────────────

    private static MazeSolver createSolver(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            if (!MazeSolver.class.isAssignableFrom(clazz)) {
                System.err.println("Error: " + className + " does not implement MazeSolver.");
                System.exit(1);
            }
            return (MazeSolver) clazz.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Could not find class '" + className + "'.");
            System.err.println("Make sure " + className + ".java is compiled (javac *.java)");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error creating solver '" + className + "': " + e.getMessage());
            System.exit(1);
        }
        return null; // unreachable
    }

    // ─────────────────────────────────────────────
    //  MAIN
    // ─────────────────────────────────────────────

    public static void main(String[] args) throws Exception {
        // Defaults
        int size = 21;
        int delay = 100;
        long seed = System.currentTimeMillis();
        String solverName = null;
        String compareName1 = null;
        String compareName2 = null;

        // Parse args
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--size":
                    size = Integer.parseInt(args[++i]);
                    if (size % 2 == 0) size++;
                    size = Math.max(11, Math.min(51, size));
                    break;
                case "--delay":
                    delay = Integer.parseInt(args[++i]);
                    delay = Math.max(10, Math.min(2000, delay));
                    break;
                case "--seed":
                    seed = Long.parseLong(args[++i]);
                    break;
                case "--solver":
                    solverName = args[++i];
                    break;
                case "--compare":
                    compareName1 = args[++i];
                    compareName2 = args[++i];
                    break;
                case "--nocolor":
                    useColor = false;
                    break;
                case "--help":
                    printUsage();
                    return;
                default:
                    System.err.println("Unknown option: " + args[i]);
                    printUsage();
                    return;
            }
        }

        if (solverName == null && compareName1 == null) {
            printUsage();
            return;
        }

        // Generate maze
        Random rng = new Random(seed);
        int[][] maze = generateMaze(size, rng);
        int[] start = {1, 1};
        int[] end   = {size - 2, size - 2};

        System.out.println(col(BOLD) + "Maze size: " + size + "x" + size +
                "  |  Seed: " + seed +
                "  |  Delay: " + delay + "ms" + col(RESET));
        Thread.sleep(1000);

        if (compareName1 != null && compareName2 != null) {
            // Compare mode
            MazeSolver s1 = createSolver(compareName1);
            MazeSolver s2 = createSolver(compareName2);
            runSolvers(s1, s2, maze, start, end, delay);
        } else {
            // Single solver mode
            MazeSolver s1 = createSolver(solverName);
            runSolvers(s1, null, maze, start, end, delay);
        }
    }

    private static void printUsage() {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║          Maze Solver Lab — MazeManager          ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║ Usage:                                          ║");
        System.out.println("║   java MazeManager --solver <ClassName>         ║");
        System.out.println("║   java MazeManager --compare <Class1> <Class2>  ║");
        System.out.println("║                                                 ║");
        System.out.println("║ Options:                                        ║");
        System.out.println("║   --size  N    Maze size (odd, 11-51)           ║");
        System.out.println("║   --delay N    Ms between steps (10-2000)       ║");
        System.out.println("║   --seed  N    Random seed for maze             ║");
        System.out.println("║   --nocolor    Disable ANSI colors              ║");
        System.out.println("║                                                 ║");
        System.out.println("║ Examples:                                       ║");
        System.out.println("║   java MazeManager --solver BFSSolver           ║");
        System.out.println("║   java MazeManager --compare BFSSolver DFSSolver║");
        System.out.println("║   java MazeManager --solver DFSSolver --size 31 ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
    }
}