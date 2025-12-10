package com.github.tasnim2001.tasnimintelliji.actions;

import com.github.tasnim2001.tasnimintelliji.keywords.KeywordLoader;
import com.github.tasnim2001.tasnimintelliji.keywords.PSISecurityScanner;
import com.github.tasnim2001.tasnimintelliji.toolwindow.SecurityToolWindow;
import com.github.tasnim2001.tasnimintelliji.toolwindow.SecurityToolWindowFactory;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiFile;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import org.jetbrains.annotations.NotNull;

public class SearchSecurityFeaturesAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        Project project = e.getProject();
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        if (project == null || file == null) return;

        // Load keywords
        var keywords = KeywordLoader.loadKeywords();

        // PSI scan
        var matches = PSISecurityScanner.scan(file, keywords);

// Remove duplicates per (main, sub, line)
        var uniqueMatches = matches.stream()
                .collect(
                        java.util.stream.Collectors.toMap(
                                r -> r.main + "|" + r.sub + "|" + r.line,
                                r -> r,
                                (a, b) -> a
                        )
                )
                .values()
                .stream()
                .toList();

// Sort by line number
        var sortedMatches = uniqueMatches.stream()
                .sorted(java.util.Comparator.comparingInt(r -> r.line))
                .toList();

// Save filtered + sorted results
        SecurityToolWindowFactory.latestResults = sortedMatches;

// Load tool window
        ToolWindow tw = ToolWindowManager.getInstance(project).getToolWindow("SecurityScan");
        if (tw == null) return;

        tw.getContentManager().removeAllContents(true);

// Build UI with sorted results
        SecurityToolWindow panel = new SecurityToolWindow(sortedMatches);

        Content content = ContentFactory.getInstance()
                .createContent(panel.getContent(), "", false);

        tw.getContentManager().addContent(content);
        tw.show();

    }
}
