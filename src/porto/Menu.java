package porto;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu {
    private JPanel base;
    private JButton consumoButton;
    private JButton apartamentosButton;
    private JButton residentesButton;
    private JButton registrosButton;
    private JLabel title;

    JFrame frame = new JFrame();

    public Menu(){
        createMenu();
        consumoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new ConsumoGUI();
            }
        });
        registrosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new RegistroGUI();
            }
        });
        residentesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new ResidenteGUI();
            }
        });
        apartamentosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new ApartamentosGUI();
            }
        });
    }

    private void createMenu(){
        JPanel root = getRootPanel();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setTitle("Database Hidrometro");
        frame.setContentPane(root);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public JPanel getRootPanel(){
        return base;
    }
}
