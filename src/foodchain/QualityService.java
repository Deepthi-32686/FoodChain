package foodchain;

import foodchain.algorithms.StringAlgorithms;
import foodchain.algorithms.StringAlgorithms.AhoCorasickScanner;
import foodchain.algorithms.StringAlgorithms.QualityMatch;
import foodchain.algorithms.SuffixAlgorithms;
import foodchain.algorithms.SuffixAlgorithms.SuffixAutomaton;
import foodchain.algorithms.SuffixAlgorithms.SuffixTree;

import java.util.*;

/**
 * Handles Food Quality inspection, automated warning detection, and report text indexing.
 * Integrates M1 (Aho-Corasick, Z-Function) and M2 (Suffix Array, SA-IS, Kasai LCP, Suffix Tree, Suffix Automaton).
 */
public class QualityService {

    private final List<Product> products;
    private final AhoCorasickScanner warningScanner;

    // Warning keywords monitored by Aho-Corasick automaton
    private static final List<String> WARNING_KEYWORDS = Arrays.asList(
            "expired", "spoiled", "contaminated", "damaged", "unsafe",
            "mold", "leak", "infestation", "pest", "compromised"
    );

    public QualityService(List<Product> products) {
        this.products = products;
        this.warningScanner = new AhoCorasickScanner(WARNING_KEYWORDS);
    }

    public void executeQualityMenu(Scanner scanner) {
        boolean active = true;
        while (active) {
            System.out.println("\n--- Food Quality & Inspection Management ---");
            System.out.println("1. Scan Food Quality Reports for Warnings (Aho-Corasick)");
            System.out.println("2. Substring Search in Quality Logs (Suffix Array & Automaton)");
            System.out.println("3. Detect Text Repetition & Boilerplate (Z-Function & Kasai LCP)");
            System.out.println("4. Verify Suffix Index Integrity (SA-IS vs Standard SA Validation)");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select quality operation: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    scanProductQualityWorkflow(scanner);
                    break;
                case "2":
                    substringSearchWorkflow(scanner);
                    break;
                case "3":
                    detectRepetitionWorkflow(scanner);
                    break;
                case "4":
                    verifySuffixIntegrityWorkflow();
                    break;
                case "0":
                    active = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1, 2, 3, 4, or 0.");
            }
        }
    }

    // =========================================================================
    // 1. AHO-CORASICK WARNING SCAN
    // =========================================================================

    public void scanProductQualityWorkflow(Scanner scanner) {
        System.out.println("\n--- Quality Report Scanner (Aho-Corasick Multi-Pattern Automaton) ---");
        System.out.println("1. Scan Single Product Report");
        System.out.println("2. Batch Scan Entire Inventory Catalog");
        System.out.print("Choose mode: ");
        String sub = scanner.nextLine().trim();

        if (sub.equals("1")) {
            System.out.print("Enter Product ID or Name (e.g., Milk, P106): ");
            String input = scanner.nextLine().trim();
            Product p = findProduct(input);
            if (p != null) {
                inspectSingleProduct(p);
            } else {
                System.out.println("Product not found.");
            }
        } else {
            System.out.println("\n=================================================================================");
            System.out.println("                    COMPREHENSIVE BATCH QUALITY AUDIT REPORT                     ");
            System.out.println("=================================================================================");
            int flaggedCount = 0;
            for (Product p : products) {
                boolean flagged = inspectSingleProduct(p);
                if (flagged) flaggedCount++;
            }
            System.out.println("=================================================================================");
            System.out.printf("Total Products Inspected: %d | Flagged for Inspection: %d | Clean: %d%n",
                    products.size(), flaggedCount, products.size() - flaggedCount);
            System.out.println("Note: Absence of warning keywords does not certify complete biological safety.");
            System.out.println("      Standard laboratory physical testing protocol still applies.");
        }
    }

    public boolean inspectSingleProduct(Product p) {
        String report = p.getQualityReport();
        List<QualityMatch> matches = warningScanner.scan(report);

        System.out.println("\nProduct       : " + p.getProductName() + " (" + p.getProductId() + ")");
        System.out.println("Quality Report: " + report);

        if (!matches.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            Set<String> uniqueKws = new LinkedHashSet<>();
            for (QualityMatch m : matches) {
                uniqueKws.add(m.getKeyword());
            }
            for (String kw : uniqueKws) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(kw);
            }
            System.out.println("Warning Found : " + sb.toString());
            System.out.println("Quality Status: Requires Inspection (Hazardous/Defective keyword detected)");
            return true;
        } else {
            System.out.println("Warning Found : None");
            System.out.println("Quality Status: No Keyword Warnings Found");
            return false;
        }
    }

    // =========================================================================
    // 2. SUFFIX ARRAY & SUFFIX AUTOMATON SUBSTRING SEARCH
    // =========================================================================

    public void substringSearchWorkflow(Scanner scanner) {
        System.out.print("\nEnter substring or phrase to search across quality records (e.g. 'intact', 'moisture', 'damaged'): ");
        String query = scanner.nextLine().trim();
        if (query.isEmpty()) return;

        System.out.println("\n[Building SA-IS Suffix Array, Suffix Tree, and Suffix Automaton on Quality Logs...]");

        int matchCount = 0;
        for (Product p : products) {
            String text = p.getQualityReport();

            // Build SA-IS Suffix Array
            int[] sa = SuffixAlgorithms.buildSuffixArraySAIS(text);
            List<Integer> saMatches = SuffixAlgorithms.searchSuffixArray(text, sa, query);

            // Cross-verify with Suffix Tree & Suffix Automaton
            SuffixTree suffixTree = new SuffixTree(text);
            boolean treeFound = suffixTree.contains(query);

            SuffixAutomaton automaton = new SuffixAutomaton(text);
            boolean automatonFound = automaton.contains(query);

            if (!saMatches.isEmpty() || treeFound || automatonFound) {
                matchCount++;
                System.out.println("\nFound in Product: " + p.getProductName() + " [" + p.getProductId() + "]");
                System.out.println("Quality Record  : " + text);
                System.out.println("Indexed Matches : " + saMatches.size() + " occurrence(s) at positions " + saMatches);
                System.out.println("Automaton State : Verified in O(|P|) transitions (" + automaton.getStateCount() + " states)");
            }
        }

        if (matchCount == 0) {
            System.out.println("No quality reports contained the substring '" + query + "'.");
        } else {
            System.out.println("\nQuery matched across " + matchCount + " product record(s).");
        }
    }

    // =========================================================================
    // 3. REPETITION & BOILERPLATE ANALYSIS (Z-FUNCTION & KASAI LCP)
    // =========================================================================

    public void detectRepetitionWorkflow(Scanner scanner) {
        System.out.println("\n--- Quality Record Repetition & Duplicate Text Detector ---");
        System.out.print("Enter Product ID to inspect (or press Enter to analyze combined catalog text): ");
        String input = scanner.nextLine().trim();

        String targetText;
        if (!input.isEmpty()) {
            Product p = findProduct(input);
            if (p == null) {
                System.out.println("Product not found.");
                return;
            }
            targetText = p.getDescription() + " " + p.getQualityReport();
            System.out.println("Analyzing Product: " + p.getProductName());
        } else {
            StringBuilder sb = new StringBuilder();
            for (Product p : products) {
                sb.append(p.getQualityReport()).append(" ");
            }
            targetText = sb.toString();
            System.out.println("Analyzing combined quality log corpus (" + targetText.length() + " characters)...");
        }

        System.out.println("\nText Content: " + (targetText.length() > 140 ? targetText.substring(0, 140) + "..." : targetText));

        // 1. Z-Algorithm Analysis
        int[] z = StringAlgorithms.computeZArray(targetText);
        int maxZ = StringAlgorithms.findLongestRepeatedPrefix(targetText);
        System.out.println("\n[Z-Algorithm Analysis]");
        System.out.println("Z-Array Computed Offsets         : " + z.length + " positions evaluated");
        System.out.println("Longest Prefix Repetition Length : " + maxZ + " character(s)");

        // 2. Suffix Array & Kasai LCP Analysis
        int[] sa = SuffixAlgorithms.buildSuffixArraySAIS(targetText);
        int[] lcp = SuffixAlgorithms.buildKasaiLCP(targetText, sa);
        String longestRepeated = SuffixAlgorithms.findLongestRepeatedSubstring(targetText, sa, lcp);

        System.out.println("\n[Kasai LCP Array Analysis]");
        if (!longestRepeated.trim().isEmpty()) {
            System.out.println("Longest Repeated Substring       : \"" + longestRepeated.trim() + "\"");
            System.out.println("Length of Repeated Substring     : " + longestRepeated.trim().length() + " chars");
        } else {
            System.out.println("No substantial repeated substring found.");
        }
    }

    // =========================================================================
    // 4. SUFFIX INDEX INTEGRITY (SA-IS vs STANDARD SA)
    // =========================================================================

    public void verifySuffixIntegrityWorkflow() {
        System.out.println("\n--- Suffix Index Construction Integrity Test ---");
        System.out.println("Comparing Linear-Time SA-IS against Standard O(N^2 log N) Suffix Array...");

        int testCount = 0;
        int passCount = 0;

        for (Product p : products) {
            String text = p.getQualityReport();
            boolean match = SuffixAlgorithms.verifySAIS(text);
            testCount++;
            if (match) passCount++;
        }

        System.out.println("Evaluated " + testCount + " product quality report documents.");
        System.out.println("SA-IS output exact matches with Standard Suffix Array: " + passCount + " / " + testCount);
        if (passCount == testCount) {
            System.out.println("Status: 100% Index Verification Succeeded. SA-IS is operating correctly.");
        } else {
            System.out.println("Warning: Discrepancy detected between SA-IS and standard suffix array.");
        }
    }

    private Product findProduct(String query) {
        for (Product p : products) {
            if (p.getProductId().equalsIgnoreCase(query) || p.getProductName().equalsIgnoreCase(query)) {
                return p;
            }
        }
        return null;
    }
}
