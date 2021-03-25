package porto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class ConsumoGUI extends JFrame {

    private JPanel root;
    private JPanel List;
    public JTable sqltable;
    private JPanel menu;
    private JButton analiseDeAlmentoNoButton;
    private JButton gerarPdfSDoButton;
    private JTextField aaaaMmDdTextField;
    private JButton extrairDadosButton;
    private JButton voltarAoMenuButton;
    private JComboBox comboBox2;
    private JButton pesquisarButton;

    JFrame frame = new JFrame();

    public ConsumoGUI(){
        createGUI();
        showConsumo();
        voltarAoMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                new Menu();
            }
        });
    }

    private void createGUI(){
        JPanel theroot = getRootPanel();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(theroot);
        frame.pack();
        frame.setTitle("Database Hidrometro");
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        createTable();
    }

    public void createTable(){
        sqltable.setModel(new DefaultTableModel(null, new String [] {"Aprt id", "Data", "Consumo", "Valor (R$)"}));
        sqltable.setEnabled(false);
    }

    public JPanel getRootPanel(){
        return root;
    }

    public void showConsumo(){
        ArrayList<Consumo> list = Consumolist();
        DefaultTableModel model = (DefaultTableModel) sqltable.getModel();
        Object [] row = new Object[4];
        for(int i = 0; i < list.size();i++){
            row[0]= list.get(i).getAprt_id();
            row[1]= list.get(i).getMes_e_ano_da_cobranca();
            row[2]= list.get(i).getConsumo();
            row[3]= list.get(i).getPreco();
            model.addRow(row);
        }
    }
    private static String theurl = "jdbc:postgresql://localhost:5432/PortoBsB";
    private static String user = "daniel";
    private static String password ="password";

    public ArrayList<Consumo> Consumolist(){
        ArrayList<Consumo> registroList = new ArrayList<>();
        try{
            Connection connection= DriverManager.getConnection(theurl, user, password);
            String query1 = "Select * FROM consumo";
            Statement st = connection.createStatement();
            ResultSet rs= st.executeQuery(query1);
            Consumo registro;
            while(rs.next()){
                registro=new Consumo(rs.getInt("aprt_id"), rs.getString("data_cobranca"), rs.getInt("consumo"), rs.getDouble("preco"), rs.getInt("local"));
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
