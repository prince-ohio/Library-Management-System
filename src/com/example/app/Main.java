package com.example.app;

import com.example.library.DatabaseHelper;
import com.example.ui.LibrarySwingUI;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // initialize DB (creates library.db)
        DatabaseHelper.init();

        // show Swing UI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new LibrarySwingUI().show());
    }
}
