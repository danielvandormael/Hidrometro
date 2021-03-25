package porto;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class AdicionarApartamento {

    private static String theurl = "jdbc:postgresql://localhost:5432/PortoBsB";
    private static String user = "daniel";
    private static String password ="password";

    JFrame frame= new JFrame();
    private JPanel root;
    private JTextField textField1;
    private JTextField textField2;
    private JButton adicionarButton;
    private JComboBox combo;

    public AdicionarApartamento(){
        createGUI();
        adicionarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String condominio = String.valueOf(combo.getSelectedItem());
                String bloco = (textField1.getText()).toUpperCase();
                int numero = Integer. valueOf(textField2.getText());
                int condint=getCondominioNum(condominio);
                frame.dispose();
                String [] buttons= {"Não", "Sim"};
                int i = JOptionPane.showOptionDialog(null, "Tem certeza que quer adicionar: \n Condomínio:  " + condominio+ "\n Bloco:  "+bloco+"\n #  "+ numero, "Confirmaçâo",
                        0, JOptionPane.WARNING_MESSAGE, null, buttons, buttons[0]);
                if(i == 1){
                    Boolean check = checkApartamentonovo(condint, bloco, numero);
                    if(check == true){
                        addApartamentonovo(condint,bloco,numero);
                        JOptionPane.showMessageDialog(null, "Condomínio:  " + condominio+ "\n Bloco:  "+bloco+"\n #  "+ numero +"\n Adicionado com sucesso!");
                    }else if(check== false){
                        JOptionPane.showMessageDialog(null, "Condomínio:  " + condominio+ "\n Bloco:  "+bloco+"\n #  "+ numero +"\n Já esta no sistema");
                    }
                }else if(i == 0){
                    JOptionPane.showMessageDialog(null, "Condomínio:  " + condominio+ "\n Bloco:  "+bloco+"\n #  "+ numero +"\n não adicionado");
                }
            }
        });
    }
    private void createGUI(){
        JPanel theroot = getRootPanel();
        //frame.setDefaultCloseOperation();
        frame.setContentPane(theroot);
        frame.setSize(500, 400);
        frame.setTitle("Adicionar Apartamento");
        frame.setLocationRelativeTo(null);
        String [] p= getCondominios();
        combo.setModel(new DefaultComboBoxModel(p));
        frame.setVisible(true);
    }

    public JPanel getRootPanel(){
        return root;
    }

    public void addApartamentonovo(int condominio, String bloco, int numero){
        try{
            Connection connection= DriverManager.getConnection(theurl, user, password);
            String query1= "INSERT INTO residentes (condominio, bloco, numero) VALUES ("+ condominio +",'" + bloco + "',"+ numero +");";
            Statement st = connection.createStatement();
            st.executeQuery(query1);
            st.close();
            connection.close();
        }catch (SQLException e) {
            System.out.println("Error in connecting to data base");
            e.printStackTrace();
        }
    }

    public Boolean checkApartamentonovo(int condominio, String bloco, int numero){
        Boolean works = false;
        int x=0;
        //checar se residente ja existe atraves do numero do cpf_cnpj
        try{
            Connection connection= DriverManager.getConnection(theurl, user, password);
            String query1= "select * from condominio where local = "+condominio+" and bloco = '"+bloco+"' and apartamento =" + numero;
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query1);
            while(rs.next()){
                x=1;
            }
            rs.close();
            st.close();
            connection.close();
        }catch (SQLException e) {
            System.out.println("Error in connecting to data base");
            e.printStackTrace();
        }
        if(x== 0) works= true;
        return works;
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
}
