package porto;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        Commands c = new Commands();
        System.out.println();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run () {
                new LogIn();
            }
        });
    }
}