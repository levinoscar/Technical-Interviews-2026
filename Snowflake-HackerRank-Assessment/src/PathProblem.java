import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

class Result {
    /* 
     * Complete the 'findOptimalPair' function below.
     *
     * The function is expected to return an INTEGER_ARRAY.
     * The function accepts following parameters:
     * 1. INTEGER n
     * 2. INTEGER m
     * 3. 2D_INTEGER_ARRAY blockedPositions
     */

    public static List<Integer> findOptimalPair(int n, int m, List<List<Integer>> blockedPositions) {
        // Initialize distance grid with a large value
        int[][] dist = new int[n + 1][m + 1];
        for (int i = 1; i <= n; i++) { Arrays.fill(dist[i], Integer.MAX_VALUE); }
        
        // Multi-source BFS Queue
        Queue<int[]> queue = new LinkedList<>();
        boolean[][] isBlocked = new boolean[n + 1][m + 1];

        // Convert 1-based coordinates from blockedPositions and mark them as distance 0
        for (List<Integer> pos : blockedPositions) {
            int r = pos.get(0);
            int c = pos.get(1);
            dist[r][c] = 0;
            isBlocked[r][c] = true;
            queue.offer(new int[]{r, c});
        }

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        while(!pq.isEmpty()) {
            int[] curr = pq.poll();
            int r = curr[0];
            int c = curr[1];

            for (int i = 0; i < 4; i++) {
                int r = curr[0];
                int c = curr[1];

                if (nr >= 1 && nr <= n && nc >= 1 && nc <= m) {
                    if (dist[nr][nc] == Integer.MAX_VALUE) {
                        dist[nr][nc] = dist[r][c] + 1;
                        queue.offer(new int[]{nr, nc});
                    }
                }
            }
        }

        // If start/end position is explicitly blocked, no valid path can exist
        if (isBlocked[1][1] || isBlocked[n][m]) { return Arrays.asList(-1, -1); }

        // Use Dijkstra's algorithm to find the path to minimize distance and minimize steps
        int[][] maxStrength = new int[n + 1][m + 1];

        // minCells[r][c] stores the minimum cells visited to reach (r, c) with max strength
        int[][] minCells = new int[n + 1][m + 1];

        for (int i = 1; i <= n; i++) {
            Arrays.fill(maxStrength[i], -1);
            Arrays.fill(minCells[i], Integer.MAX_VALUE);
        }

        // Priority Queue elements" {bottleneck_strength, cells_visited, r, c}
        // Max-heap: sort by strength descending. If tie, sort by cells_visited ascending
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> {
            if (b[2] != a[2]) { return Integer.compare(b[2], a[2]); }
            return Integer.compare(a[3], b[3]);
        });
        
        maxStrength[1][1] = dist[1][1];
        minCells[1][1] = 1;
        pq.offer(new int[]{1, 1, dist[1][1], 1});

        while(!pq.isEmpty()) {
            int[] curr = pq.poll();
            int r = curr[0];
            int c = curr[1];
            int strength = curr[2];
            int cells = curr[3];

            // If we reached bottom-right destination, we found our optimal path
            if (r == n && c == m) { return Arrays.asList(strength, cells); }
            if (strength < maxStrength[r][c]) { continue; }

            for (int i = 0; i < 4; i++) {
                int nr = r + dr[i];
                int nc = c + dc[i];

                // Ensure neighbor is within boundaries and NOT a blocked cell
                if (nr >= 1 && nr <= n && nc >= 1 && nc <= m && !isBlocked[nr][nc]) {
                    int nextStrength = Math.min(strength, dist[nr][nc]);
                    int nextCells = cells + 1;

                    // Relaxation condition
                    if (nextStrength > maxStrength[nr][nc]) {
                        maxStrength[nr][nc] = nextStrength;
                        minCells[nr][nc] = nextCells;
                        pq.offer(new int[]{nr, nc, nextStrength, nextCells});
                    } else if (nextStrength == maxStrength[nr][nc] && nextCells < minCells[nr][nc]) {
                        minCells[nr][nc] = nextCells;
                        pq.offer(new int[]{nr, nc, nextStrength, nextCells});
                    }
                }
            }
        }
        // If (n, m) is unreachable
        return Arrays.asList(-1, -1); 
    }
}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int n = Integer.parseInt(bufferedReader.readLine().trim());
        
        int m = Integer.parseInt(bufferedReader.readLine().trim());
        
        int blockedPositionsRows = Integer.parseInt(bufferedReader.readLine().trim());
        int blockedPositionsColumns = Integer.parseInt(bufferedReader.readLine().trim());

        List<List<Integer>> blockedPositions = new ArrayList<>();

        IntStream.range(0, blockedPositionsRows).forEach(i -> {
            try {
                blockedPositions.add(
                    Stream.of(bufferedReader.readLine().replaceAll("\\s+$", "").split(" "))
                        .map(Integer::parseInt)
                        .collect(toList())
                );
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        List<Integer> result = Result.findOptimalPair(n, m, blockedPositions);

        bufferedWriter.write(
            result.stream()
                .map(Object::toString)
                .collect(joining(" "))
            + "\n"
        );

        bufferedReader.close();
        bufferedWriter.close();
    }
}