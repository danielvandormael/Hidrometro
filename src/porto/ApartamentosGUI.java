package porto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class ApartamentosGUI {
    private JPanel root;
    private JPanel List;
    private JTable sqltable;
    private JPanel menu;
    private JButton adicionarApartamentosButton;
    private JButton removerApartamentoButton;
    private JComboBox comboBox2;
    private JButton voltarAoMenuButton;
    private JButton adicionarCondominioButton;

    JFrame frame = new JFrame();

    public ApartamentosGUI(){
        createGUI();
        showApartamentos();
        voltarAoMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new Menu();
            }
        });

        adicionarCondominioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String condominio;
                boolean want = false;
                String [] buttons= {"Não", "Sim"};
                condominio= JOptionPane.showInputDialog("Nome do Condominio");
                if(condominio.length() > 0){
                    int i = JOptionPane.showOptionDialog(null, "Tem certeza que quer adicionar " + condominio + " como um novo condomínio?", "Confirmaçâo",
                            0, JOptionPane.WARNING_MESSAGE, null, buttons, buttons[0]);
                    if(i == 1){
                        Boolean check = checkCondominionovo(condominio);
                        if(check == true){
                            addCondominionovo(condominio);
                            JOptionPane.showMessageDialog(null, condominio+" adicionado com sucesso");
                        }else if(check== false){
                            JOptionPane.showMessageDialog(null, condominio+" já esta no sistema");
                        }
                    }else if(i == 0){
                        JOptionPane.showMessageDialog(null, condominio+" não adicionado");
                    }
                }
            }
        });

        adicionarApartamentosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AdicionarApartamento();
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
        sqltable.setModel(new DefaultTableModel(null, new String [] {"Aprt id", "Local", "Bloco", "Apartamento"}));
        sqltable.setEnabled(false);
    }

    public JPanel getRootPanel(){
        return root;
    }

    public void showApartamentos(){
        ArrayList<Apartamentos> list = Apartamentoslist();
        DefaultTableModel model = (DefaultTableModel) sqltable.getModel();
        Object [] row = new Object[4];
        for(int i = 0; i < list.size();i++){
            row[0]= list.get(i).getId();
            row[1]= getCondominio(list.get(i).getLocal());
            row[2]= list.get(i).getBloco();
            row[3]= list.get(i).getApartamento();
            model.addRow(row);
        }
    }
    private static String theurl = "jdbc:postgresql://localhost:5432/PortoBsB";
    private static String user = "daniel";
    private static String password ="password";

    public ArrayList<Apartamentos> Apartamentoslist(){
        ArrayList<Apartamentos> registroList = new ArrayList<>();
        try{
            Connection connection= DriverManager.getConnection(theurl, user, password);
            String query1 = "Select * FROM apartamentos";
            Statement st = connection.createStatement();
            ResultSet rs= st.executeQuery(query1);
            Apartamentos registro;
            while(rs.next()){
                registro=new Apartamentos(rs.getInt("id"), rs.getInt("local"), rs.getString("bloco"), rs.getInt("apartamento"));
                registroList.add(registro);
            }
            connection.close();
        } catch (SQLException e) {
            System.out.println("Error in connecting to data base");
            e.printStackTrace();
        }
        return registroList;
    }

    public String getCondominio(int x){
        String i="";
        if(x== 1){
            i= "Solar do Cerrado";
        }else if(x==2){
            i="Cosmopolitan";
        }
        return i;
    }


    public void addCondominionovo(String name){
        try{
            Connection connection= DriverManager.getConnection(theurl, user, password);
            String query1= "INSERT INTO condominio (condominio) VALUES ('"+ name +"');";
            Statement st = connection.createStatement();
            st.executeQuery(query1);
            st.close();
            connection.close();
        }catch (SQLException e) {
            System.out.println("Error in connecting to data base");
            e.printStackTrace();
        }
    }

    public Boolean checkCondominionovo(String numero){
        Boolean works = false;
        int x=0;
        //checar se residente ja existe atraves do numero do cpf_cnpj
        try{
            Connection connection= DriverManager.getConnection(theurl, user, password);
            String query1= "select * from condominio where condominio = '"+numero+"';";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query1);
            while(rs.next()){
                x=1;
            }
            st.close();
            connection.close();
        }catch (SQLException e) {
            System.out.println("Error in connecting to data base");
            e.printStackTrace();
        }
        if(x== 0) works= true;
        return works;
    }

}
