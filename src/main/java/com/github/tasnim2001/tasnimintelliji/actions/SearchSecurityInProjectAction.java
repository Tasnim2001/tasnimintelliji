package com.github.tasnim2001.tasnimintelliji.actions;

import com.github.tasnim2001.tasnimintelliji.keywords.KeywordLoader;
import com.github.tasnim2001.tasnimintelliji.keywords.PSISecurityScanner;
import com.github.tasnim2001.tasnimintelliji.toolwindow.SecurityToolWindow;
import com.github.tasnim2001.tasnimintelliji.toolwindow.SecurityToolWindowFactory;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SearchSecurityInProjectAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        Project project = e.getProject();
        if (project == null) return;

        var keywords = KeywordLoader.loadKeywords();
        List<PSISecurityScanner.Result> all = new ArrayList<>();

        // Scan ALL files in project
        VirtualFile root = project.getBaseDir();

        scanFolder(root, project, keywords, all);

        // Remove duplicates
        var unique = all.stream()
                .collect(java.util.stream.Collectors.toMap(
                        r -> r.main + "|" + r.sub + "|" + r.line,
                        r -> r,
                        (a,b) -> a
                ))
                .values()
                .stream()
                .sorted(java.util.Comparator.comparingInt(r->r.line))
                .toList();

        // Save and show
        SecurityToolWindowFactory.latestResults = unique;

        ToolWindow tw = ToolWindowManager.getInstance(project).getToolWindow("SecurityScan");
        if (tw == null) return;

        tw.getContentManager().removeAllContents(true);

        SecurityToolWindow panel = new SecurityToolWindow(unique);
        Content content = ContentFactory.getInstance().createContent(panel.getContent(), "", false);

        tw.getContentManager().addContent(content);
        tw.show();
    }

    private void scanFolder(VirtualFile folder, Project project, Object keywords, List<PSISecurityScanner.Result> all) {
        for (VirtualFile child : folder.getChildren()) {
            if (child.isDirectory()) {
                scanFolder(child, project, keywords, all);
            } else if (child.getName().endsWith(".java")) {
                PsiFile psi = PsiManager.getInstance(project).findFile(child);
                if (psi != null) {
                    all.addAll(PSISecurityScanner.scan(psi, (java.util.Map<String, Object>) keywords));
                }
            }
        }
    }
}
