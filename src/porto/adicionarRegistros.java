package porto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class adicionarRegistros {
    private JPanel panel1;
    private JTable sqltable;
    private JComboBox comboBox1;
    private JTextField aaaaMm01TextField;
    private JButton adicionarButton;
    private JScrollPane oi;

    private static String theurl = "jdbc:postgresql://localhost:5432/PortoBsB";
    private static String user = "daniel";
    private static String password ="password";

    JFrame frame = new JFrame();

    public adicionarRegistros(){
        createGUI();
        comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRegistro(getCondominioNum(comboBox1.getSelectedItem().toString()));
            }
        });
        adicionarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String date = aaaaMm01TextField.getText();
                String condoName =  comboBox1.getSelectedItem().toString();
                Boolean checkDatevalid = checkdatevalid(date);
                if(checkDatevalid == false){
                    JOptionPane.showMessageDialog(null, "Error: Data inserido invalida");
                    aaaaMm01TextField.setText("aaaa-mm-01");
                }else{
                    Boolean checkTable = checkTable();
                    if(checkTable == false){
                        JOptionPane.showMessageDialog(null, "Error: Datos inseridos invalidos");
                    }else{
                        Boolean checkDateInUse = checkdateInUse(getCondominioNum(condoName), date);
                        if(checkDatevalid == true && checkTable == true){
                            if(checkDateInUse == false){
                                String [] buttons= {"Não", "Sim"};
                                int i= JOptionPane.showOptionDialog(null, "Voce ira deletar o registros já lançados para o condominio " + condoName + ", na data: " + date,
                                        "oi", 0, JOptionPane.WARNING_MESSAGE, null, buttons, buttons[0]);
                                if(i == 1){
                                    frame.dispose();
                                    deleteRegistros(getCondominioNum(condoName), date);
                                    addRegistros(getCondominioNum(condoName), date);
                                }else if(i == 0){
                                    JOptionPane.showMessageDialog(null, "Registros para o condominio " + condoName + " não adicionados");
                                }
                            }else if(checkDateInUse == true){
                                String [] buttons= {"Não", "Sim"};
                                int i= JOptionPane.showOptionDialog(null, "Está pronto para adicionar os Registros do " + condoName + ", na data: " + date,
                                        "oi", 0, JOptionPane.WARNING_MESSAGE, null, buttons, buttons[0]);
                                if(i == 1){
                                    frame.dispose();

                                    addRegistros(getCondominioNum(condoName), date);

                                }else if(i == 0){
                                    JOptionPane.showMessageDialog(null, "Registros para o condominio " + condoName + " não adicionados");
                                }
                            }
                        }
                    }
                }


            }
        });
    }


    private void createGUI(){
        JPanel theroot = getRootPanel();
        frame.setContentPane(theroot);
        frame.setSize(800, 500);
        frame.setTitle("Database Hidrometro");
        frame.setLocationRelativeTo(null);
        String [] p= getCondominios();
        comboBox1.setModel(new DefaultComboBoxModel(p));
        frame.setVisible(true);
        createTable();
    }

    public void createTable(){
        sqltable.setModel(new adi(null, new String [] {"Aprt id", "Local", "Bloco", "Apartamento", "Medimento do Hidrometro"}));
        showRegistro(1);
    }

    public void deleteRegistros(int condo, String date){
        try{
            Connection connection = DriverManager.getConnection(theurl, user, password);
            String query1 = "DELETE FROM registro WHERE local ="+ condo +" AND data_cobranca = '"+ date +"';";
            Statement st = connection.createStatement();
            st.executeUpdate(query1);
            st.close();
            connection.close();
        }catch (SQLException e) {
            System.out.println("Error in connecting to data base");
            e.printStackTrace();
        }
    }

    public void addRegistros(int condo, String date){
        ArrayList<Integer> ids = getElementsOfColumn(0);
        ArrayList<Integer> medimento = getElementsOfColumn(4);
        String currentdate = getCurrentdate();
        for(int i = 0; i < sqltable.getRowCount(); i++){
            try{
                Connection connection = DriverManager.getConnection(theurl, user, password);
                String query1 = "INSERT INTO registro (aprt_id,\"local\",data_cobranca,data_lancamento,medimento_hidrometro) values (" + ids.get(i) + "," +  condo + ",'" + date + "','"+ currentdate+"', "+medimento.get(i)+");";
                Statement st = connection.createStatement();
                st.executeUpdate(query1);
                st.close();
                connection.close();
            }catch (SQLException e) {
                System.out.println("Error in connecting to data base");
                e.printStackTrace();
            }
        }

    }

    public int getCondominioNum(String condominio){
        int x= 0;
        try{
            Connection connection= DriverManager.getConnection(theurl, user, password);
            String query1= "select * from condominio where condominio = '"+condominio+"';";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query1);
            while(rs.next()){
                x=rs.getInt("id");
            }
            rs.close();
            st.close();
            connection.close();
        }catch (SQLException e) {
            System.out.println("Error in connecting to data base");
            e.printStackTrace();
        }
        return x;
    }

    public String[] getCondominios(){
        ArrayList<String> condominios= new ArrayList<>();
        try{
            Connection connection= DriverManager.getConnection(theurl, user, password);
            String query1= "select * from condominio;";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query1);
            String x;
            while(rs.next()){
                x=rs.getString("condominio");
                condominios.add(x);
            }
            rs.close();
            st.close();
            connection.close();
        }catch (SQLException e) {
            System.out.println("Error in connecting to data base");
            e.printStackTrace();
        }
        String [] sample = condominios.toArray(new String[0]);
        return sample;
    }

    public JPanel getRootPanel(){
        return panel1;
    }

    public void showRegistro(int condominio) {
        int p =sqltable.getRowCount();
        adi model = (adi) sqltable.getModel();
        if(p >0){
            for(int j = p - 1; j >= 0; j--){
                model.removeRow(j);
            }
        }
        ArrayList<Apartamentos> list = Apartamentoslist(condominio);
        Object[] row = new Object[5];
        String  x = getCondominioName(list.get(0).getLocal());
        for (int i = 0; i < list.size(); i++) {
            row[0] = list.get(i).getId();
            row[1] = x; //condominio
            row[2] = list.get(i).getBloco();  //
            row[3] = list.get(i).getApartamento(); //casa
            row[4] = "0";
            model.addRow(row);
        }
    }

    public ArrayList<Apartamentos> Apartamentoslist(int condominio){
        ArrayList<Apartamentos> registroList = new ArrayList<>();
        try{
            Connection connection= DriverManager.getConnection(theurl, user, password);
            String query1 = "Select * FROM apartamentos where local = " + condominio + ";";
            Statement st = connection.createStatement();
            ResultSet rs= st.executeQuery(query1);
            Apartamentos registro;
            while(rs.next()){
                registro=new Apartamentos(rs.getInt("id"), rs.getInt("local"), rs.getString("bloco"), rs.getInt("apartamento"));
                registroList.add(registro);
            }
            connection.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("Error in connecting to data base");
            e.printStackTrace();
        }
        return registroList;
    }
    public String getCondominioName(int x) {
        String i = "";
        try {
            Connection connection = DriverManager.getConnection(theurl, user, password);
            String query1 = "Select * FROM condominio where \"id\" = " + x + ";";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query1);
            Apartamentos registro;
            while (rs.next()) {
                i = rs.getString("condominio");
            }
            connection.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("Error in connecting to data base");
            e.printStackTrace();
        }
        return i;
    }

    public Boolean checkdatevalid(String date){
        if(date.length() != 10){
            return false;
        }
        try{
            int d = Integer.parseInt(date.substring(0,4));
        }catch (NumberFormatException e){
            return false;
        }
        try{
            int d = Integer.parseInt(date.substring(5,7));
        }catch (NumberFormatException e){
            return false;
        }
        if (Integer.parseInt(date.substring(5,7)) == 0 || Integer.parseInt(date.substring(5,7)) > 12){
            return false;
        }
        if(date.charAt(4) != '-'){
            return false;
        }else if(date.charAt(7) != '-'){
            return false;
        }
        return true;
    }

    public Boolean checkdateInUse(int condo, String date){
        try{
            Connection connection = DriverManager.getConnection(theurl, user, password);
            String query1 = "Select * FROM registro where data_cobranca = '" + date + "' AND local =" + condo +";";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query1);
            while (rs.next()){
                return false;
            }
            connection.close();
            st.close();
            rs.close();
        }catch (SQLException e) {
            System.out.println("Error in connecting to data base");
            e.printStackTrace();
        }
        return true;
    }

    public Boolean checkTable(){ // needs to be fixed
        for(int i = 0;i<sqltable.getModel().getRowCount();i++){
            String test= "";
            test = sqltable.getModel().getValueAt(i,4).toString();
            for(int x = 0; x < test.length(); x++) {
                if(Character.isDigit(test.charAt(x)) == false){
                    return false;
                }
            }
        }
        return true;
    }

    public ArrayList<Integer> getElementsOfColumn(int column){
        ArrayList<Integer> elements =new ArrayList<>();

        for(int i = 0; i< sqltable.getRowCount();  i++){
            int x = Integer.parseInt(sqltable.getModel().getValueAt(i, column).toString());
            elements.add(x);
        }
        return elements;
    }

    public String getCurrentdate(){
        String p = "";
        try{
            Connection connection = DriverManager.getConnection(theurl, user, password);
            String query1 = "Select CURRENT_DATE;";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query1);
            while (rs.next()){
                p=rs.getString("current_date");
            }
            connection.close();
            st.close();
            rs.close();
        }catch (SQLException e) {
            System.out.println("Error in connecting to data base");
            e.printStackTrace();
        }
        return p;
    }
}



class adi extends DefaultTableModel {


    public adi(String [][] p, String[] strings) {
        super(p, strings);
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if (col == 4) {
            return true;
        } else {
            return false;
        }
    }
}

