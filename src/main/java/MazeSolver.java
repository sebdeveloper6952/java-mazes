import java.util.List;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║                     IMazeSolver                             ║
 * ║         The contract between the maze and YOUR solver       ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * Implement this interface to create a maze-solving algorithm.
 *
 * RULES:
 *   - BFS group: you MUST use a java.util.Queue (LinkedList as Queue)
 *   - DFS group: you MUST use a java.util.Stack
 *   - Each call to step() should explore exactly ONE cell
 *   - Do NOT modify the maze array
 *
 * MAZE FORMAT:
 *   maze[row][col]  →  0 = wall, 1 = open path
 *   start = {row, col} of the starting cell (top-left area)
 *   end   = {row, col} of the goal cell (bottom-right area)
 *
 * COORDINATE CONVENTION:
 *   {row, col} — row 0 is the top, col 0 is the left
 */
public interface MazeSolver {

    /**
     * Called once before solving begins.
     * Use this to set up your data structures (Queue or Stack),
     * store the maze reference, and prepare your visited set.
     *
     * @param maze   2D grid: 0 = wall, 1 = path. Do NOT modify.
     * @param start  {row, col} of the start cell
     * @param end    {row, col} of the goal cell
     */
    void init(int[][] maze, int[] start, int[] end);

    /**
     * Perform ONE step of your search algorithm.
     *
     * One step means:
     *   1. Take the next cell from your Queue/Stack
     *   2. If it's the goal → you're done, return true
     *   3. Otherwise, explore its neighbors (up/down/left/right)
     *      and add valid, unvisited path cells to your Queue/Stack
     *   4. Return false (not done yet)
     *
     * If your Queue/Stack is empty and you haven't found the goal,
     * return true (done, but no path exists).
     *
     * @return true if solving is complete (goal found OR no solution),
     *         false if there are more steps to take
     */
    boolean step();

    /**
     * Return the cell(s) that were visited/explored during the
     * LAST call to step().
     *
     * This is used for visualization — we color these cells on screen.
     * Typically this is a list containing just the one cell you dequeued/popped.
     *
     * @return list of {row, col} arrays visited in the last step
     */
    List<int[]> getVisitedThisStep();

    /**
     * After solving is complete (step() returned true), return the
     * path from start to end.
     *
     * You'll need to track how you reached each cell (a "parent map")
     * and reconstruct the path by following parents from end to start.
     *
     * @return ordered list of {row, col} from start to end,
     *         or an empty list if no path was found
     */
    List<int[]> getPath();

    /**
     * Return the name of your solver for display purposes.
     * Example: "BFS (Queue)" or "DFS (Stack)"
     *
     * @return human-readable solver name
     */
    String getName();
}