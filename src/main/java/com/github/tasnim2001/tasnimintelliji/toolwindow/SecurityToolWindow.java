package com.github.tasnim2001.tasnimintelliji.toolwindow;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SecurityToolWindow {

    private final JPanel root;

    public SecurityToolWindow(List<String> results) {

        root = new JPanel(new BorderLayout());

        // --- Text Area ---
        JBTextArea area = new JBTextArea();
        area.setEditable(false);

        StringBuilder sb = new StringBuilder();
        for (String r : results) {
            sb.append(r).append("\n\n");
        }
        area.setText(sb.toString());

        // ADD SCROLLPANE
        root.add(new JBScrollPane(area), BorderLayout.CENTER);

        // --- Buttons ---
        JButton refresh = new JButton("Refresh");
        JButton close = new JButton("Close");

        JPanel buttons = new JPanel();
        buttons.add(refresh);
        buttons.add(close);

        root.add(buttons, BorderLayout.SOUTH);
    }

    public JPanel getContent() {
        return root;
    }
}


