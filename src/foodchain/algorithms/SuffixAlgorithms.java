package foodchain.algorithms;

import java.util.*;

/**
 * Suffix-based data structures and algorithms used in FoodChain.
 * Implements M2 algorithms:
 * 1. Suffix Array (Standard and SA-IS Linear-Time Induced Sorting)
 * 2. Suffix Array Binary Search
 * 3. Kasai's Algorithm for Longest Common Prefix (LCP) Array
 * 4. Suffix Tree for substring searches
 * 5. Suffix Automaton (DAWG) for O(|P|) substring verification
 */
public class SuffixAlgorithms {

    // =========================================================================
    // 1. STANDARD SUFFIX ARRAY (BASELINE & VERIFICATION)
    // =========================================================================

    /**
     * Builds suffix array using standard suffix sorting.
     * Used as a robust baseline and for verifying SA-IS correctness.
     */
    public static int[] buildSuffixArrayStandard(String s) {
        if (s == null) return new int[0];
        int n = s.length();
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) {
            order[i] = i;
        }

        Arrays.sort(order, (a, b) -> {
            int len = n - Math.max(a, b);
            for (int k = 0; k < len; k++) {
                char c1 = s.charAt(a + k);
                char c2 = s.charAt(b + k);
                if (c1 != c2) return Character.compare(c1, c2);
            }
            return Integer.compare(n - a, n - b);
        });

        int[] sa = new int[n];
        for (int i = 0; i < n; i++) {
            sa[i] = order[i];
        }
        return sa;
    }

    // =========================================================================
    // 2. GENUINE SA-IS (SUFFIX ARRAY INDUCED SORTING) ALGORITHM
    // =========================================================================

    /**
     * Builds suffix array using the genuine SA-IS algorithm in linear time O(N).
     * Includes L/S-type classification, LMS substrings, induced sorting, and recursion.
     */
    public static int[] buildSuffixArraySAIS(String s) {
        if (s == null || s.isEmpty()) return new int[0];
        int n = s.length();

        // Convert string to integer array with sentinel 0 at the end
        int[] text = new int[n + 1];
        for (int i = 0; i < n; i++) {
            text[i] = s.charAt(i) + 1; // +1 to ensure characters > 0
        }
        text[n] = 0; // 0 is unique minimum sentinel ($)

        int maxChar = 256 + 1;
        for (int i = 0; i < n; i++) {
            if (text[i] > maxChar) maxChar = text[i];
        }

        int[] saWithSentinel = sais(text, maxChar + 1);

        // Remove the sentinel at saWithSentinel[0] (which is position n)
        int[] sa = new int[n];
        for (int i = 0; i < n; i++) {
            sa[i] = saWithSentinel[i + 1];
        }
        return sa;
    }

    private static int[] sais(int[] s, int alphabetSize) {
        int n = s.length;
        if (n == 1) {
            return new int[]{0};
        }

        // Step 1: Classify characters into S-type (true) and L-type (false)
        boolean[] isS = new boolean[n];
        isS[n - 1] = true; // Sentinel is S-type
        for (int i = n - 2; i >= 0; i--) {
            if (s[i] < s[i + 1]) {
                isS[i] = true;
            } else if (s[i] > s[i + 1]) {
                isS[i] = false;
            } else {
                isS[i] = isS[i + 1];
            }
        }

        // Step 2: Bucket counts
        int[] bucketCounts = new int[alphabetSize];
        for (int ch : s) {
            bucketCounts[ch]++;
        }

        // Step 3: Identify LMS (Leftmost S-type) characters
        int[] lmsMap = new int[n];
        Arrays.fill(lmsMap, -1);
        int lmsCount = 0;
        for (int i = 1; i < n; i++) {
            if (isS[i] && !isS[i - 1]) {
                lmsMap[i] = lmsCount++;
            }
        }

        int[] lms = new int[lmsCount];
        int ptr = 0;
        for (int i = 1; i < n; i++) {
            if (isS[i] && !isS[i - 1]) {
                lms[ptr++] = i;
            }
        }

        // Step 4: First induced sort to order LMS substrings
        int[] sa = new int[n];
        inducedSort(s, sa, isS, bucketCounts, alphabetSize, lms);

        // Step 5: Name LMS substrings
        if (lmsCount > 0) {
            int[] sortedLms = new int[lmsCount];
            int sortedPtr = 0;
            for (int val : sa) {
                if (val >= 0 && lmsMap[val] >= 0) {
                    sortedLms[sortedPtr++] = val;
                }
            }

            int[] summaryString = new int[lmsCount];
            int name = 0;
            summaryString[lmsMap[sortedLms[0]]] = 0;

            for (int i = 1; i < lmsCount; i++) {
                int prev = sortedLms[i - 1];
                int curr = sortedLms[i];

                if (!areLmsSubstringsEqual(s, isS, prev, curr)) {
                    name++;
                }
                summaryString[lmsMap[curr]] = name;
            }

            int summaryAlphabetSize = name + 1;
            int[] summarySA;

            // Step 6: Recurse if names are not unique; else direct sort
            if (summaryAlphabetSize < lmsCount) {
                summarySA = sais(summaryString, summaryAlphabetSize);
            } else {
                summarySA = new int[lmsCount];
                for (int i = 0; i < lmsCount; i++) {
                    summarySA[summaryString[i]] = i;
                }
            }

            // Order LMS positions according to summarySA
            int[] orderedLms = new int[lmsCount];
            for (int i = 0; i < lmsCount; i++) {
                orderedLms[i] = lms[summarySA[i]];
            }

            // Step 7: Second induced sort using exact LMS order
            inducedSort(s, sa, isS, bucketCounts, alphabetSize, orderedLms);
        }

        return sa;
    }

    private static boolean areLmsSubstringsEqual(int[] s, boolean[] isS, int p1, int p2) {
        int n = s.length;
        for (int offset = 0; ; offset++) {
            int idx1 = p1 + offset;
            int idx2 = p2 + offset;
            if (idx1 >= n || idx2 >= n) return false;
            if (s[idx1] != s[idx2] || isS[idx1] != isS[idx2]) return false;

            if (offset > 0) {
                boolean isLms1 = isS[idx1] && !isS[idx1 - 1];
                boolean isLms2 = isS[idx2] && !isS[idx2 - 1];
                if (isLms1 && isLms2) return true;
                if (isLms1 || isLms2) return false;
            }
        }
    }

    private static void inducedSort(int[] s, int[] sa, boolean[] isS, int[] bucketCounts,
                                    int alphabetSize, int[] lmsElements) {
        int n = s.length;
        Arrays.fill(sa, -1);

        int[] bucketTails = getBucketTails(bucketCounts, alphabetSize);
        // Put LMS elements at the tails of their buckets in reverse order
        for (int i = lmsElements.length - 1; i >= 0; i--) {
            int idx = lmsElements[i];
            int ch = s[idx];
            sa[bucketTails[ch]--] = idx;
        }

        // Induce L-type from left to right
        int[] bucketHeads = getBucketHeads(bucketCounts, alphabetSize);
        for (int i = 0; i < n; i++) {
            int j = sa[i] - 1;
            if (j >= 0 && !isS[j]) {
                int ch = s[j];
                sa[bucketHeads[ch]++] = j;
            }
        }

        // Induce S-type from right to left
        bucketTails = getBucketTails(bucketCounts, alphabetSize);
        for (int i = n - 1; i >= 0; i--) {
            int j = sa[i] - 1;
            if (j >= 0 && isS[j]) {
                int ch = s[j];
                sa[bucketTails[ch]--] = j;
            }
        }
    }

    private static int[] getBucketHeads(int[] bucketCounts, int alphabetSize) {
        int[] heads = new int[alphabetSize];
        int sum = 0;
        for (int i = 0; i < alphabetSize; i++) {
            heads[i] = sum;
            sum += bucketCounts[i];
        }
        return heads;
    }

    private static int[] getBucketTails(int[] bucketCounts, int alphabetSize) {
        int[] tails = new int[alphabetSize];
        int sum = 0;
        for (int i = 0; i < alphabetSize; i++) {
            sum += bucketCounts[i];
            tails[i] = sum - 1;
        }
        return tails;
    }

    /**
     * Validates that SA-IS output matches standard suffix array.
     */
    public static boolean verifySAIS(String text) {
        int[] saStandard = buildSuffixArrayStandard(text);
        int[] saSAIS = buildSuffixArraySAIS(text);
        return Arrays.equals(saStandard, saSAIS);
    }

    // =========================================================================
    // 3. SUFFIX ARRAY BINARY SEARCH
    // =========================================================================

    /**
     * Searches for all occurrences of pattern in text using binary search over suffix array.
     * Runs in O(|P| * log N) time.
     */
    public static List<Integer> searchSuffixArray(String text, int[] sa, String pattern) {
        List<Integer> occurrences = new ArrayList<>();
        if (text == null || sa == null || pattern == null || pattern.isEmpty()) {
            return occurrences;
        }

        int n = text.length();
        int m = pattern.length();

        // Binary search for lower bound
        int low = 0, high = n - 1;
        int first = -1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int suffixStart = sa[mid];
            String sub = text.substring(suffixStart, Math.min(n, suffixStart + m));
            int cmp = sub.compareTo(pattern);

            if (cmp >= 0) {
                if (cmp == 0) first = mid;
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        if (first == -1) return occurrences;

        // Binary search for upper bound
        low = first;
        high = n - 1;
        int last = first;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int suffixStart = sa[mid];
            String sub = text.substring(suffixStart, Math.min(n, suffixStart + m));
            int cmp = sub.compareTo(pattern);

            if (cmp <= 0) {
                if (cmp == 0) last = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        for (int i = first; i <= last; i++) {
            occurrences.add(sa[i]);
        }
        Collections.sort(occurrences);
        return occurrences;
    }

    // =========================================================================
    // 4. KASAI'S LCP (LONGEST COMMON PREFIX) ALGORITHM
    // =========================================================================

    /**
     * Constructs the LCP (Longest Common Prefix) array in O(N) time using Kasai's algorithm.
     * lcp[i] = length of common prefix between suffixes at sa[i] and sa[i - 1].
     */
    public static int[] buildKasaiLCP(String text, int[] sa) {
        if (text == null || sa == null) return new int[0];
        int n = text.length();
        if (n == 0) return new int[0];

        int[] rank = new int[n];
        for (int i = 0; i < n; i++) {
            rank[sa[i]] = i;
        }

        int[] lcp = new int[n];
        int h = 0;

        for (int i = 0; i < n; i++) {
            if (rank[i] > 0) {
                int k = sa[rank[i] - 1];
                while (i + h < n && k + h < n && text.charAt(i + h) == text.charAt(k + h)) {
                    h++;
                }
                lcp[rank[i]] = h;
                if (h > 0) {
                    h--;
                }
            }
        }
        return lcp;
    }

    /**
     * Finds the longest repeated substring in text using Suffix Array and Kasai LCP.
     */
    public static String findLongestRepeatedSubstring(String text, int[] sa, int[] lcp) {
        if (text == null || sa == null || lcp == null || text.isEmpty()) return "";

        int maxLen = 0;
        int bestIdx = -1;

        for (int i = 1; i < lcp.length; i++) {
            if (lcp[i] > maxLen) {
                maxLen = lcp[i];
                bestIdx = sa[i];
            }
        }

        if (maxLen > 0 && bestIdx != -1) {
            return text.substring(bestIdx, bestIdx + maxLen);
        }
        return "";
    }

    // =========================================================================
    // 5. SUFFIX TREE
    // =========================================================================

    public static class SuffixTreeNode {
        Map<Character, SuffixTreeNode> children = new TreeMap<>();
        int start;
        int end;
        List<Integer> suffixIndices = new ArrayList<>();

        public SuffixTreeNode(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    public static class SuffixTree {
        private final String text;
        private final String fullText;
        private final SuffixTreeNode root;

        public SuffixTree(String text) {
            this.text = (text != null) ? text : "";
            this.fullText = this.text + "\u0000";
            this.root = new SuffixTreeNode(-1, -1);
            buildTree();
        }

        private void buildTree() {
            int n = fullText.length();
            for (int i = 0; i < n; i++) {
                insertSuffix(i);
            }
        }

        private void insertSuffix(int suffixIndex) {
            SuffixTreeNode current = root;
            if (suffixIndex < text.length()) {
                current.suffixIndices.add(suffixIndex);
            }

            int i = suffixIndex;
            int n = fullText.length();

            while (i < n) {
                char ch = fullText.charAt(i);
                if (!current.children.containsKey(ch)) {
                    // Create new leaf node
                    SuffixTreeNode leaf = new SuffixTreeNode(i, n);
                    if (suffixIndex < text.length()) {
                        leaf.suffixIndices.add(suffixIndex);
                    }
                    current.children.put(ch, leaf);
                    return;
                }

                SuffixTreeNode child = current.children.get(ch);
                int edgeLen = child.end - child.start;
                int matchLen = 0;

                while (matchLen < edgeLen && i + matchLen < n &&
                        fullText.charAt(child.start + matchLen) == fullText.charAt(i + matchLen)) {
                    matchLen++;
                }

                if (matchLen == edgeLen) {
                    // Match consumed full edge, continue to child
                    current = child;
                    if (suffixIndex < text.length()) {
                        current.suffixIndices.add(suffixIndex);
                    }
                    i += edgeLen;
                } else {
                    // Split the edge
                    SuffixTreeNode split = new SuffixTreeNode(child.start, child.start + matchLen);
                    split.suffixIndices.addAll(child.suffixIndices);
                    if (suffixIndex < text.length() && !split.suffixIndices.contains(suffixIndex)) {
                        split.suffixIndices.add(suffixIndex);
                    }

                    current.children.put(ch, split);

                    // Reattach child with adjusted start
                    child.start += matchLen;
                    if (child.start < n) {
                        split.children.put(fullText.charAt(child.start), child);
                    }

                    // Add new leaf for remaining suffix
                    if (i + matchLen < n) {
                        SuffixTreeNode newLeaf = new SuffixTreeNode(i + matchLen, n);
                        if (suffixIndex < text.length()) {
                            newLeaf.suffixIndices.add(suffixIndex);
                        }
                        split.children.put(fullText.charAt(i + matchLen), newLeaf);
                    }
                    return;
                }
            }
        }

        /**
         * Checks if pattern exists as a substring and returns matching starting indices.
         */
        public List<Integer> searchSubstring(String pattern) {
            List<Integer> result = new ArrayList<>();
            if (pattern == null || pattern.isEmpty() || text.isEmpty()) return result;

            SuffixTreeNode current = root;
            int i = 0;
            int m = pattern.length();

            while (i < m) {
                char ch = pattern.charAt(i);
                if (!current.children.containsKey(ch)) {
                    return result; // Mismatch
                }

                SuffixTreeNode child = current.children.get(ch);
                int edgeLen = child.end - child.start;
                for (int k = 0; k < edgeLen && i < m; k++, i++) {
                    if (child.start + k >= fullText.length() || fullText.charAt(child.start + k) != pattern.charAt(i)) {
                        return result; // Mismatch on edge
                    }
                }
                current = child;
            }

            // Pattern fully matched, return all valid suffixes in this subtree
            Set<Integer> uniqueIndices = new TreeSet<>();
            for (int idx : current.suffixIndices) {
                if (idx < text.length()) {
                    uniqueIndices.add(idx);
                }
            }
            result.addAll(uniqueIndices);
            return result;
        }

        public boolean contains(String pattern) {
            return !searchSubstring(pattern).isEmpty();
        }
    }

    // =========================================================================
    // 6. SUFFIX AUTOMATON (DAWG)
    // =========================================================================

    public static class SuffixAutomaton {
        public static class State {
            int len;
            int link;
            Map<Character, Integer> next = new HashMap<>();

            State(int len, int link) {
                this.len = len;
                this.link = link;
            }
        }

        private final List<State> states = new ArrayList<>();
        private int last;

        public SuffixAutomaton(String s) {
            // Initial state 0
            states.add(new State(0, -1));
            last = 0;

            if (s != null) {
                for (char c : s.toCharArray()) {
                    extend(c);
                }
            }
        }

        public final void extend(char c) {
            int cur = states.size();
            states.add(new State(states.get(last).len + 1, 0));

            int p = last;
            while (p != -1 && !states.get(p).next.containsKey(c)) {
                states.get(p).next.put(c, cur);
                p = states.get(p).link;
            }

            if (p == -1) {
                states.get(cur).link = 0;
            } else {
                int q = states.get(p).next.get(c);
                if (states.get(p).len + 1 == states.get(q).len) {
                    states.get(cur).link = q;
                } else {
                    int clone = states.size();
                    State cloneState = new State(states.get(p).len + 1, states.get(q).link);
                    cloneState.next.putAll(states.get(q).next);
                    states.add(cloneState);

                    while (p != -1 && states.get(p).next.get(c) == q) {
                        states.get(p).next.put(c, clone);
                        p = states.get(p).link;
                    }

                    states.get(q).link = clone;
                    states.get(cur).link = clone;
                }
            }
            last = cur;
        }

        /**
         * Checks whether pattern exists as a substring in O(|pattern|) time.
         */
        public boolean contains(String pattern) {
            if (pattern == null) return false;
            if (pattern.isEmpty()) return true;

            int curr = 0;
            for (char c : pattern.toCharArray()) {
                if (!states.get(curr).next.containsKey(c)) {
                    return false;
                }
                curr = states.get(curr).next.get(c);
            }
            return true;
        }

        public int getStateCount() {
            return states.size();
        }
    }
}
