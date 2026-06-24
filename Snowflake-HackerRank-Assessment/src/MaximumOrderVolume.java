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
     * Complete the 'phoneCalls' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts following parameters:
     * 1. INTEGER_ARRAY start
     * 2. INTEGER_ARRAY duration
     * 3. INTEGER_ARRAY volume
     */

    // Very interesting weighted interval scheduling problem. Not something I've done previously, but I'll do my best here with my strategy ideas, Thinking of a dynamic programming approach to combine with binary search to handle the large constrains.

    // Helper Class to group call details
    static class Call {
        int start;
        int end;
        int volume;

        Call(int start, int duration, int volume) {
            this.start = start;
            this.end = end;
            this.volume = volume;
        }
    }

    public static int phoneCalls(List<Integer> start, List<Integer> duration, List<Integer> volume) {
        int n = start.size();
        Call[] calls = new Call[n];

        // Combine inputs into Call structures
        for (int i = 0; i < n; i++) { calls[i] = new Call(start.get(i), start.get(i) + duration.get(i), volume.get(i)); }
        
        int n = start.size();
        Call[] calls = new Call[n];

        // Combine inputs into Call structures
        for (int i = 0; i < n; i++) { calls[i] = new Call(start.get(i), start.get(i) + duration.get(i), volume.get(i)); }
        
        // Sort calls based on their end times
        Arrays.sort(calls, (a, b) -> Integer.compare(a.end, b.end));

        // dp[i] scores max volume using options up to index i - 1
        int[] dp = new int[n + 1];
        dp[0] = 0; // Base case: no calls processed

        for (int i = 1; i < n; i++) {
            // first option is to skip the current call
            int excludeCall = dp[i - 1];

            // second option is to include the current call
            int includeCall = calls[i - 1].volume;

            // Next, we find the latest non-overlapping call index, using binary search
            int latestCompatibleIndex = findLatestCompatible(calls, i - 1);
            if (latestCompatibleIndex != -1) {
                includeCall += dp[latestCompatibleIndex + 1];
            }

            // Maximize choices
            dp[i] = Math.max(excludeCall, includeCall);
        }
        return dp[n];
    }

    // Binary search helper to find the last call that ends before or at current call's start time
    private static int findLatestCompatible(Call[] calls, int index) {
        int low = 0;
        int high = index - 1;
        int result = -1;
        int targetTime = calls[index].start;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (calls[mid].end <= targetTime) {
                result = mid; // Found a compatible call
                low = mid + 1; // Search for a later compatible call
            } else { high = mid - 1; } // Search earlier calls
        }
        return result;
    }
}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int startCount = Integer.parseInt(bufferedReader.readLine().trim());

        List<Integer> start = IntStream.range(0, startCount).mapToObj(i -> {
            try {
                return bufferedReader.readLine().replaceAll("\\s+$", "");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        })
            .map(String::trim)
            .map(Integer::parseInt)
            .collect(toList());

        int volumeCount = Integer.parseInt(bufferedReader.readLine().trim());

        List<Integer> volume = IntStream.range(0, columeCount).mapToObj(i -> {
            try {
                return bufferedReader.readLine().replaceAll("\\s+$", "");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        })
            .map(String::trim)
            .map(Integer::parseInt)
            .collect(toList());
        
        int result = Result.phoneCalls(start, duration, volume);

        bufferedWriter.write(String.valueOf(result));
        bufferedWriter.newLine();

        bufferedReader.close();
        bufferedWriter.close();
    }
}