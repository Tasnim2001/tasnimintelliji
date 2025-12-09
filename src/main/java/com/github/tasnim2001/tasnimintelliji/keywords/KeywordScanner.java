package com.github.tasnim2001.tasnimintelliji.keywords;

import com.intellij.psi.PsiFile;
import java.util.*;
import java.util.regex.*;

public class KeywordScanner {

    private static String removeComments(String code) {
        // Remove block comments /* ... */
        code = code.replaceAll("/\\*.*?\\*/", "");

        // Remove line comments // ...
        code = code.replaceAll("//.*", "");

        return code;
    }

    // MAIN METHOD — returns KeywordMatch objects with offsets
    public static List<KeywordMatch> scanFileWithOffsets(PsiFile file, Map<String, Object> keywords) {
        List<KeywordMatch> found = new ArrayList<>();

        String code = file.getText();
        code = removeComments(code);

        for (Map.Entry<String, Object> entry : keywords.entrySet()) {
            String mainCategory = entry.getKey();
            Object value = entry.getValue();

            // CASE 1 — direct list
            if (value instanceof List<?>) {
                scanList(mainCategory, "", (List<?>) value, code, found);
            }

            // CASE 2 — nested subcategories
            else if (value instanceof Map<?, ?> nested) {
                for (Object sub : nested.keySet()) {
                    Object subList = nested.get(sub);
                    if (subList instanceof List<?>) {
                        scanList(mainCategory, sub.toString(), (List<?>) subList, code, found);
                    }
                }
            }
        }

        return found;
    }

    // NUR KeywordMatch Version!
    private static void scanList(String main, String sub, List<?> list, String code, List<KeywordMatch> found) {

        Set<String> seen = new HashSet<>();

        for (Object o : list) {
            String patternText = o.toString();
            Pattern p = Pattern.compile(patternText, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(code);

            while (m.find()) {
                String hit = m.group().trim();

                // FILTER — prevent false positives
                if (hit.isEmpty()) continue;
                if (hit.length() > 25) continue;                     // whole line skip
                if (hit.matches("^[A-Za-z0-9_]+\\s*\\(")) continue;  // method name skip
                if (hit.matches("^[A-Za-z0-9_]+\\s*=")) continue;    // assignment skip

                String unique = (main + "|" + sub + "|" + hit).toLowerCase();
                if (seen.contains(unique)) continue;
                seen.add(unique);

                found.add(
                        new KeywordMatch(
                                main,
                                sub,
                                patternText,
                                hit,
                                m.start(),
                                m.end()
                        )
                );
            }
        }
    }
}
