package com.github.tasnim2001.tasnimintelliji.keywords;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

import java.util.*;

public class PSISecurityScanner {

    public static class Result {
        public final String main;
        public final String sub;
        public final String hit;
        public final int line;

        public Result(String main, String sub, String hit, int line) {
            this.main = main;
            this.sub = sub;
            this.hit = hit;
            this.line = line;
        }

        @Override
        public String toString() {
            return main + " → " + sub + " | Found: " + hit + " | Line: " + line;
        }
    }


    public static List<Result> scan(PsiFile file, Map<String, Object> keywords) {

        Project project = file.getProject();
        Document document = PsiDocumentManager.getInstance(project).getDocument(file);
        List<Result> results = new ArrayList<>();


        file.accept(new JavaRecursiveElementWalkingVisitor() {

            void scanText(String text, int offset) {
                if (text == null || text.isBlank()) return;

                String lower = text.toLowerCase();

                for (String main : keywords.keySet()) {

                    Object value = keywords.get(main);

                    // FALL 1 — einfache Liste
                    if (value instanceof List<?> list) {
                        for (Object o : list) {
                            String pattern = o.toString().toLowerCase();

                            if (lower.contains(pattern)) {
                                int line = document.getLineNumber(offset) + 1;
                                results.add(new Result(main, "", pattern, line));
                            }
                        }
                    }

                    // FALL 2 — verschachtelte Maps
                    else if (value instanceof Map<?, ?> subMap) {
                        for (Object subKey : subMap.keySet()) {
                            Object items = subMap.get(subKey);

                            if (items instanceof List<?> list2) {
                                for (Object o : list2) {
                                    String pattern = o.toString().toLowerCase();

                                    if (lower.contains(pattern)) {
                                        int line = document.getLineNumber(offset) + 1;
                                        results.add(new Result(main, subKey.toString(), pattern, line));
                                    }
                                }
                            }
                        }
                    }
                }
            }


            @Override
            public void visitLiteralExpression(PsiLiteralExpression expression) {
                Object value = expression.getValue();
                if (value instanceof String str) {
                    scanText(str, expression.getTextOffset());
                }
                super.visitLiteralExpression(expression);
            }

            @Override
            public void visitIdentifier(PsiIdentifier identifier) {
                scanText(identifier.getText(), identifier.getTextOffset());
                super.visitIdentifier(identifier);
            }

            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression call) {
                PsiReferenceExpression ref = call.getMethodExpression();
                scanText(ref.getReferenceName(), call.getTextOffset());
                super.visitMethodCallExpression(call);
            }

            @Override
            public void visitVariable(PsiVariable variable) {
                scanText(variable.getName(), variable.getTextOffset());
                super.visitVariable(variable);
            }

            @Override
            public void visitAnnotation(PsiAnnotation annotation) {
                scanText(annotation.getText(), annotation.getTextOffset());
                super.visitAnnotation(annotation);
            }
        });

        return results;
    }
}
