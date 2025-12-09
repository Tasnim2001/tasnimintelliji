package com.github.tasnim2001.tasnimintelliji.actions;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.command.WriteCommandAction;

public class AddAnnotationAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;
        PsiElement element = e.getData(CommonDataKeys.PSI_ELEMENT);
        if (!(element instanceof PsiMethod method)) {
            Messages.showErrorDialog(project, "Please select a method!", "Error");
            return;
        }
        String annotationText = Messages.showInputDialog(
                project,
                "Enter annotation text (e.g. @MyAnnotation):",
                "Add Annotation",
                Messages.getQuestionIcon()
        );

        if (annotationText == null || annotationText.isBlank()) {
            return;
        }
        PsiModifierList modifiers = method.getModifierList();

        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiAnnotation annotation = JavaPsiFacade.getElementFactory(project)
                    .createAnnotationFromText(annotationText, method);
            modifiers.addBefore(annotation, modifiers.getFirstChild());
        });
    }
}