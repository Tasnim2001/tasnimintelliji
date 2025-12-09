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
            return main + " → " + sub + " | " + hit + " in line " + line;
        }
    }

    // Hauptscan-Methode
    public static List<Result> scan(PsiFile file, Map<String, Object> keywords) {

        Project project = file.getProject();
        Document document = PsiDocumentManager.getInstance(project).getDocument(file);
        List<Result> results = new ArrayList<>();

        file.accept(new JavaRecursiveElementWalkingVisitor() {

            void check(String text, int offset) {
                if (text == null) return;

                for (String main : keywords.keySet()) {
                    Map<String, List<String>> category = (Map<String, List<String>>) keywords.get(main);

                    for (String sub : category.keySet()) {

                        for (String pattern : category.get(sub)) {

                            if (text.toLowerCase().matches(".*" + pattern.toLowerCase() + ".*")) {
                                int line = document.getLineNumber(offset) + 1;
                                results.add(new Result(main, sub, pattern, line));
                            }
                        }
                    }
                }
            }

            @Override
            public void visitVariable(PsiVariable variable) {
                check(variable.getName(), variable.getTextOffset());
                super.visitVariable(variable);
            }

            @Override
            public void visitMethod(PsiMethod method) {
                check(method.getName(), method.getTextOffset());
                super.visitMethod(method);
            }

            @Override
            public void visitLiteralExpression(PsiLiteralExpression expression) {
                Object value = expression.getValue();
                if (value instanceof String) {
                    check((String) value, expression.getTextOffset());
                }
                super.visitLiteralExpression(expression);
            }

            @Override
            public void visitComment(PsiComment comment) {
                check(comment.getText(), comment.getTextOffset());
                super.visitComment(comment);
            }

            @Override
            public void visitAnnotation(PsiAnnotation annotation) {
                check(annotation.getText(), annotation.getTextOffset());
                super.visitAnnotation(annotation);
            }

            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression call) {
                PsiReferenceExpression ref = call.getMethodExpression();
                check(ref.getReferenceName(), call.getTextOffset());
                super.visitMethodCallExpression(call);
            }
        });
        return results;
    }
}
