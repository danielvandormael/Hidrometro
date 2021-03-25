package porto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class RegistroGUI {
    private JPanel root;
    private JPanel List;
    private JTable sqltable;
    private JPanel menu;
    private JButton adicionarRegistrosDoMêsButton;
    private JButton lançarConsumoDoMêsButton;
    private JTextField aaaaMmDdTextField;
    private JComboBox comboBox2;
    private JButton pesquisarButton;
    private JButton voltarAoMenuButton;

    JFrame frame = new JFrame();

    public RegistroGUI(){
        createGUI();
        showRegistro();
        voltarAoMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                new Menu();
            }
        });
        adicionarRegistrosDoMêsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new adicionarRegistros();
            }
        });
        lançarConsumoDoMêsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GerarConsumo();
            }
        });
    }

    private void createGUI(){
        JPanel theroot = getRootPanel();
        frame.setContentPane(theroot);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setTitle("Database Hidrometro");
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        createTable();
    }

    private static String theurl = "jdbc:postgresql://localhost:5432/PortoBsB";
    private static String user = "daniel";
    private static String password ="password";

    public void showRegistro() {
        ArrayList<Registro> list = registroList();
        DefaultTableModel model = (DefaultTableModel) sqltable.getModel();
        Object[] row = new Object[4];
        for (int i = 0; i < list.size(); i++) {
            row[0] = list.get(i).getaprt_id();
            row[1] = list.get(i).getdata_cobranca();
            row[2] = list.get(i).getdata_lancamento();
            row[3] = list.get(i).getmedimento_hidrometro();
            model.addRow(row);
        }
    }

    public void createTable(){
        sqltable.setModel(new DefaultTableModel(null, new String [] {"Aprt id", "Data da cobrança", "Data do lancamento", "Medimento do hidrometro"}));
        sqltable.setEnabled(false);
    }

    public JPanel getRootPanel(){
        return root;
    }

    public ArrayList<Registro> registroList(){
        ArrayList<Registro> registroList = new ArrayList<>();
        try{
                Connection connection= DriverManager.getConnection(theurl, user, password);
                String query1 = "Select * FROM registro";
                Statement st = connection.createStatement();
                ResultSet rs= st.executeQuery(query1);
                Registro registro;
                while(rs.next()){
                    registro=new Registro(rs.getString("data_cobranca"), rs.getString("data_lancamento"), rs.getInt("aprt_id"), rs.getInt("medimento_hidrometro"));
                    registroList.add(registro);
                }
            connection.close();
        } catch (SQLException e) {
                System.out.println("Error in connecting to data base");
                e.printStackTrace();
        }
            return registroList;
    }
}
