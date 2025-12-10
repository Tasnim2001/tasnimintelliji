package com.github.tasnim2001.tasnimintelliji.toolwindow;

import com.github.tasnim2001.tasnimintelliji.keywords.PSISecurityScanner;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SecurityToolWindow {

    private final JPanel root;

    public SecurityToolWindow(List<PSISecurityScanner.Result> results) {

        root = new JPanel(new BorderLayout());

        String[] columns = {"Category", "Subcategory", "Found", "Line"};

        String[][] data = new String[results.size()][4];

        for (int i = 0; i < results.size(); i++) {
            PSISecurityScanner.Result r = results.get(i);
            data[i][0] = r.main;
            data[i][1] = r.sub;
            data[i][2] = r.hit;
            data[i][3] = String.valueOf(r.line);
        }

        JTable table = new JTable(data, columns);
        table.setFillsViewportHeight(true);
        table.setRowHeight(24);

        JScrollPane scrollPane = new JBScrollPane(table);
        root.add(scrollPane, BorderLayout.CENTER);
    }

    public JPanel getContent() {
        return root;
    }
}
