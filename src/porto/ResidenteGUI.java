package porto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class ResidenteGUI {
    private JPanel root;
    private JPanel List;
    private JTable sqltable;
    private JPanel menu;
    private JButton adicionarResidenteButton;
    private JButton editarResidenteButton;
    private JButton voltarAoMenuButton;
    private JButton pesquisarButton;
    private JTextField textField1;

    JFrame frame = new JFrame();

    public ResidenteGUI(){
        createGUI();
        showResidentes();
        voltarAoMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new Menu();
            }
        });
        adicionarResidenteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AdincionarResidente();
            }
        });
        textField1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(sqltable.getModel());
                sorter.setRowFilter(RowFilter.regexFilter(textField1.getText()));
                sqltable.setRowSorter(sorter);
            }
        });
        pesquisarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(sqltable.getModel());
                sorter.setRowFilter(RowFilter.regexFilter(textField1.getText()));
                sqltable.setRowSorter(sorter);
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
        sqltable.setModel(new DefaultTableModel(null, new String [] {"ID", "nome", "tipo", "CPF/CNPJ","#"}));
        sqltable.setEnabled(false);
    }

    public JPanel getRootPanel(){
        return root;
    }

    public void showResidentes(){
        ArrayList<Residentes> list = ResidentesList();
        DefaultTableModel model = (DefaultTableModel) sqltable.getModel();
        Object [] row = new Object[5];
        for(int i = 0; i < list.size();i++){
            row[0]= list.get(i).getId();
            row[1]= list.get(i).getNome();
            row[2]= list.get(i).getTipo();
            row[3]= list.get(i).getCpf_cnpj();
            row[4]= list.get(i).getNumero();
            model.addRow(row);
        }
    }
    private static String theurl = "jdbc:postgresql://localhost:5432/PortoBsB";
    private static String user = "daniel";
    private static String password ="password";

    public ArrayList<Residentes> ResidentesList(){
        ArrayList<Residentes> registroList = new ArrayList<>();
        try{
            Connection connection= DriverManager.getConnection(theurl, user, password);
            String query1 = "Select * FROM residentes";
            Statement st = connection.createStatement();
            ResultSet rs= st.executeQuery(query1);
            Residentes registro;
            while(rs.next()){
                registro=new Residentes(rs.getInt("id"), rs.getString("nome"), rs.getString("tipo"), rs.getString("cpf_cnpj"), rs.getString("numero"));
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
