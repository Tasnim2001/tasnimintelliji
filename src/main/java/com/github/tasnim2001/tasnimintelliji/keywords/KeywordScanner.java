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

    public static List<KeywordMatch> scanFileWithOffsets(PsiFile file, Map<String, Object> keywords) {
        List<KeywordMatch> found = new ArrayList<>();

        String code = file.getText();
        code = removeComments(code);

        for (Map.Entry<String, Object> entry : keywords.entrySet()) {
            String mainCategory = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof List<?>) {
                scanList(mainCategory, "", (List<?>) value, code, found);
            } else if (value instanceof Map<?, ?> nested) {
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

    private static void scanList(String main, String sub, List<?> list, String code, List<KeywordMatch> found) {

        Set<String> seen = new HashSet<>();
        String[] lines = code.split("\n"); // <-- nötig für die Zeilenberechnung

        for (Object o : list) {
            String patternText = o.toString();
            Pattern p = Pattern.compile(patternText, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(code);

            while (m.find()) {
                String hit = m.group().trim();

                // FILTER
                if (hit.isEmpty()) continue;
                if (hit.length() > 25) continue;
                if (hit.matches("^[A-Za-z0-9_]+\\s*\\(")) continue;
                if (hit.matches("^[A-Za-z0-9_]+\\s*=")) continue;

                String unique = (main + "|" + sub + "|" + hit).toLowerCase();
                if (seen.contains(unique)) continue;
                seen.add(unique);

                // 🔥 Zeilennummer berechnen
                int offset = m.start();
                int lineNumber = calcLineNumber(code, offset);

                found.add(
                        new KeywordMatch(
                                main,
                                sub,
                                patternText,
                                hit,
                                m.start(),
                                m.end(),
                                lineNumber  // <-- 🔥 JETZT WIRD line ÜBERGEBEN
                        )
                );
            }
        }
    }

    private static int calcLineNumber(String fullText, int offset) {
        int line = 1;
        for (int i = 0; i < offset; i++) {
            if (fullText.charAt(i) == '\n') {
                line++;
            }
        }
        return line;
    }
}
