package com.github.tasnim2001.tasnimintelliji.toolwindow;

import com.github.tasnim2001.tasnimintelliji.keywords.PSISecurityScanner;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SecurityToolWindowFactory implements ToolWindowFactory {


    //public static List<PSISecurityScanner.Result> latestResults = List.of();


    public static List<PSISecurityScanner.Result> latestResults = List.of();


    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // Inhalte kommen NUR aus der Action
    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return true;
    }
}
