package porto;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AdincionarResidente {
    private JTextField textField1;
    private JComboBox comboBox1;
    private JComboBox comboBox2;
    private JTextField textField2;
    private JButton adicionarButton;
    private JPanel root;

    private static String theurl = "jdbc:postgresql://localhost:5432/PortoBsB";
    private static String user = "daniel";
    private static String password ="password";

    JFrame frame= new JFrame();

    public AdincionarResidente(){
        createGUI();
        adicionarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nome = textField1.getText();
                String tipo = String.valueOf(comboBox1.getSelectedItem());
                String cpf_cnpj = String.valueOf(comboBox2.getSelectedItem());
                String numero = textField2.getText();
                frame.dispose();
                String [] buttons= {"Não", "Sim"};
                    int i = JOptionPane.showOptionDialog(null, "Tem certeza que quer adicionar: \n Nome:  " + nome+ "\n tipo:  "+ tipo +"\n cpf/cnpj:  "+cpf_cnpj+"\n #  "+ numero, "Confirmaçâo",
                            0, JOptionPane.WARNING_MESSAGE, null, buttons, buttons[0]);
                    if(i == 1){
                        Boolean check = checkresidentenovo(numero);
                        if(check == true){
                            addResidentenovo(nome, tipo, cpf_cnpj, numero);
                            JOptionPane.showMessageDialog(null, "Nome:  " + nome + "\n tipo:  "+ tipo +"\n cpf/cnpj:  "+ cpf_cnpj +"\n #  "+ numero +"\n Adicionado com sucesso!");
                        }else if(check== false){
                            JOptionPane.showMessageDialog(null, "Nome:" + nome + "\n tipo:"+ tipo +"\n cpf/cnpj:"+ cpf_cnpj +"\n # "+ numero +"\n Já esta no sistema");
                        }
                    }else if(i == 0){
                        JOptionPane.showMessageDialog(null, "Nome:  " + nome + "\n tipo:  "+ tipo +"\n cpf/cnpj:  "+ cpf_cnpj +"\n #  "+ numero +"\n não adicionado");
                    }
            }
        });
    }
    private void createGUI(){
        JPanel theroot = getRootPanel();
        //frame.setDefaultCloseOperation();
        frame.setContentPane(theroot);
        frame.setSize(500, 400);
        frame.setTitle("Adicionar Residente");
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public JPanel getRootPanel(){
        return root;
    }

    public void addResidentenovo(String nome, String tipo, String cpf_cnpj, String numero){
        try{
            Connection connection= DriverManager.getConnection(theurl, user, password);
            String query1= "INSERT INTO residentes (nome, tipo, cpf_cnpj, numero) VALUES ('"+ nome +"','" + tipo + "','"+ cpf_cnpj + "','"+ numero +"');";
            Statement st = connection.createStatement();
            st.executeQuery(query1);
            st.close();
            connection.close();
        }catch (SQLException e) {
            System.out.println("Error in connecting to data base");
            e.printStackTrace();
        }
    }

    public Boolean checkresidentenovo(String numero){
        Boolean works = false;
        int x=0;
        //checar se residente ja existe atraves do numero do cpf_cnpj
        try{
            Connection connection= DriverManager.getConnection(theurl, user, password);
            String query1= "select * from residentes where numero = '"+numero+"';";
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
}
