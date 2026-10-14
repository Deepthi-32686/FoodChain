package foodchain.algorithms;

import java.util.*;

/**
 * String matching and pattern processing algorithms used throughout FoodChain.
 * Implements M1 algorithms:
 * 1. Knuth-Morris-Pratt (KMP) with LPS array
 * 2. Rabin-Karp with rolling hash and character verification
 * 3. Z-Algorithm (Z-Function) for prefix matching
 * 4. Aho-Corasick automaton for multi-pattern keyword scanning
 */
public class StringAlgorithms {

    // =========================================================================
    // 1. KNUTH-MORRIS-PRATT (KMP) ALGORITHM
    // =========================================================================

    /**
     * Computes the Longest Proper Prefix which is also a Suffix (LPS) array.
     * lps[i] stores the length of the longest proper prefix of pattern[0..i]
     * that is also a suffix of pattern[0..i].
     */
    public static int[] computeLPSArray(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return new int[0];
        }
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0; // length of previous longest prefix suffix
        int i = 1;

        lps[0] = 0; // lps[0] is always 0

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1]; // fallback to previous known prefix length
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    /**
     * Searches for occurrences of pattern in text using the KMP algorithm.
     * Returns a list of starting indices where pattern occurs in text.
     */
    public static List<Integer> searchKMP(String text, String pattern, boolean caseSensitive) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return matches;
        }

        String t = caseSensitive ? text : text.toLowerCase();
        String p = caseSensitive ? pattern : pattern.toLowerCase();

        int[] lps = computeLPSArray(p);
        int i = 0; // index for text
        int j = 0; // index for pattern
        int n = t.length();
        int m = p.length();

        while (i < n) {
            if (t.charAt(i) == p.charAt(j)) {
                i++;
                j++;
            }
            if (j == m) {
                matches.add(i - j); // Match found at index (i - j)
                j = lps[j - 1];
            } else if (i < n && t.charAt(i) != p.charAt(j)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return matches;
    }

    /**
     * Checks if text contains pattern using KMP.
     */
    public static boolean containsKMP(String text, String pattern, boolean caseSensitive) {
        return !searchKMP(text, pattern, caseSensitive).isEmpty();
    }

    // =========================================================================
    // 2. RABIN-KARP ALGORITHM (Rolling Hash with verification)
    // =========================================================================

    private static final int RK_BASE = 256;
    private static final long RK_PRIME = 1000000007L;

    /**
     * Searches for pattern in text using Rabin-Karp rolling hash.
     * Accurately verifies actual characters after hash matches to avoid collision errors.
     */
    public static List<Integer> searchRabinKarp(String text, String pattern, boolean caseSensitive) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return matches;
        }

        String t = caseSensitive ? text : text.toLowerCase();
        String p = caseSensitive ? pattern : pattern.toLowerCase();

        int n = t.length();
        int m = p.length();

        // Calculate h = (RK_BASE ^ (m - 1)) % RK_PRIME
        long h = 1;
        for (int i = 0; i < m - 1; i++) {
            h = (h * RK_BASE) % RK_PRIME;
        }

        long pHash = 0;
        long tHash = 0;

        // Calculate initial hash values for pattern and first window of text
        for (int i = 0; i < m; i++) {
            pHash = (RK_BASE * pHash + p.charAt(i)) % RK_PRIME;
            tHash = (RK_BASE * tHash + t.charAt(i)) % RK_PRIME;
        }

        // Slide the window over the text
        for (int i = 0; i <= n - m; i++) {
            // Check hash match
            if (pHash == tHash) {
                // Character by character verification to prevent false positives
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (t.charAt(i + j) != p.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    matches.add(i);
                }
            }

            // Calculate rolling hash for next window
            if (i < n - m) {
                tHash = (RK_BASE * (tHash - t.charAt(i) * h) + t.charAt(i + m)) % RK_PRIME;
                if (tHash < 0) {
                    tHash += RK_PRIME;
                }
            }
        }
        return matches;
    }

    // =========================================================================
    // 3. Z-ALGORITHM (Z-Function)
    // =========================================================================

    /**
     * Computes the Z-array for string s.
     * Z[i] is the length of the longest substring starting from s[i] that is also a prefix of s.
     */
    public static int[] computeZArray(String s) {
        if (s == null || s.isEmpty()) {
            return new int[0];
        }
        int n = s.length();
        int[] z = new int[n];
        z[0] = n; // By definition, prefix of length n matches itself

        int l = 0;
        int r = 0;

        for (int i = 1; i < n; i++) {
            if (i > r) {
                l = r = i;
                while (r < n && s.charAt(r) == s.charAt(r - l)) {
                    r++;
                }
                z[i] = r - l;
                r--;
            } else {
                int k = i - l;
                if (z[k] < r - i + 1) {
                    z[i] = z[k];
                } else {
                    l = i;
                    while (r < n && s.charAt(r) == s.charAt(r - l)) {
                        r++;
                    }
                    z[i] = r - l;
                    r--;
                }
            }
        }
        return z;
    }

    /**
     * Finds occurrences of pattern in text using Z-algorithm by forming (pattern + "$" + text).
     */
    public static List<Integer> searchZ(String text, String pattern, boolean caseSensitive) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return matches;
        }

        String t = caseSensitive ? text : text.toLowerCase();
        String p = caseSensitive ? pattern : pattern.toLowerCase();

        String concat = p + "$" + t;
        int[] z = computeZArray(concat);
        int m = p.length();

        for (int i = m + 1; i < concat.length(); i++) {
            if (z[i] == m) {
                matches.add(i - (m + 1));
            }
        }
        return matches;
    }

    /**
     * Finds repeated prefix segments within a single text using Z-function.
     */
    public static int findLongestRepeatedPrefix(String text) {
        if (text == null || text.length() < 2) return 0;
        int[] z = computeZArray(text);
        int maxLen = 0;
        for (int i = 1; i < z.length; i++) {
            if (z[i] > maxLen) {
                maxLen = z[i];
            }
        }
        return maxLen;
    }

    // =========================================================================
    // 4. AHO-CORASICK MULTI-PATTERN SEARCH ALGORITHM
    // =========================================================================

    public static class QualityMatch {
        private final String keyword;
        private final int startIndex;
        private final int endIndex;

        public QualityMatch(String keyword, int startIndex, int endIndex) {
            this.keyword = keyword;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        public String getKeyword() { return keyword; }
        public int getStartIndex() { return startIndex; }
        public int getEndIndex() { return endIndex; }

        @Override
        public String toString() {
            return keyword + " (pos: " + startIndex + "-" + endIndex + ")";
        }
    }

    public static class AhoCorasickScanner {
        private static class Node {
            Map<Character, Node> children = new HashMap<>();
            Node fail = null;
            List<String> outputPatterns = new ArrayList<>();
        }

        private final Node root;

        public AhoCorasickScanner(List<String> keywords) {
            this.root = new Node();
            buildTrie(keywords);
            buildFailureLinks();
        }

        private void buildTrie(List<String> keywords) {
            for (String kw : keywords) {
                if (kw == null || kw.trim().isEmpty()) continue;
                String normalized = kw.trim().toLowerCase();
                Node current = root;
                for (char ch : normalized.toCharArray()) {
                    current = current.children.computeIfAbsent(ch, c -> new Node());
                }
                current.outputPatterns.add(normalized);
            }
        }

        private void buildFailureLinks() {
            Queue<Node> queue = new LinkedList<>();

            // Root's direct children fail to root
            for (Node child : root.children.values()) {
                child.fail = root;
                queue.add(child);
            }

            while (!queue.isEmpty()) {
                Node current = queue.poll();

                for (Map.Entry<Character, Node> entry : current.children.entrySet()) {
                    char ch = entry.getKey();
                    Node child = entry.getValue();

                    // Find failure transition
                    Node fallback = current.fail;
                    while (fallback != null && !fallback.children.containsKey(ch)) {
                        fallback = fallback.fail;
                    }

                    if (fallback == null) {
                        child.fail = root;
                    } else {
                        child.fail = fallback.children.get(ch);
                    }

                    // Inherit output patterns from failure node
                    if (child.fail != null) {
                        child.outputPatterns.addAll(child.fail.outputPatterns);
                    }

                    queue.add(child);
                }
            }
        }

        /**
         * Scans text and detects all occurrences of any registered keyword.
         */
        public List<QualityMatch> scan(String text) {
            List<QualityMatch> results = new ArrayList<>();
            if (text == null || text.isEmpty()) return results;

            String normalized = text.toLowerCase();
            Node current = root;

            for (int i = 0; i < normalized.length(); i++) {
                char ch = normalized.charAt(i);

                // Follow fail link if child doesn't exist
                while (current != root && !current.children.containsKey(ch)) {
                    current = current.fail;
                }

                current = current.children.getOrDefault(ch, root);

                // Collect matched keywords at current position
                for (String matchedWord : current.outputPatterns) {
                    int start = i - matchedWord.length() + 1;
                    results.add(new QualityMatch(matchedWord, start, i));
                }
            }
            return results;
        }
    }
}
