package com.github.tasnim2001.tasnimintelliji.actions;

import com.github.tasnim2001.tasnimintelliji.keywords.*;
import com.github.tasnim2001.tasnimintelliji.toolwindow.SecurityToolWindow;
import com.github.tasnim2001.tasnimintelliji.toolwindow.SecurityToolWindowFactory;

import com.github.tasnim2001.tasnimintelliji.keywords.KeywordLoader;
import com.github.tasnim2001.tasnimintelliji.keywords.KeywordScanner;
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

import java.util.Map;
import java.util.List;

public class SearchSecurityFeaturesAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        Project project = e.getProject();
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        if (project == null || file == null) return;

        // Scan results
        var keywords = KeywordLoader.loadKeywords();
        var matches = KeywordScanner.scanFileWithOffsets(file, keywords);

        // Save results
        SecurityToolWindowFactory.latestResults = matches.stream()
                .map(r -> r.main + " → " + r.sub + " | Found: " + r.hit)
                .toList();

        // Load tool window
        ToolWindow tw = ToolWindowManager.getInstance(project).getToolWindow("SecurityScan");
        if (tw == null) return;

        tw.getContentManager().removeAllContents(true);

        // Build new panel
        SecurityToolWindow panel = new SecurityToolWindow(SecurityToolWindowFactory.latestResults);

        Content content = ContentFactory.getInstance()
                .createContent(panel.getContent(), "", false);

        tw.getContentManager().addContent(content);
        tw.show();
    }

}
