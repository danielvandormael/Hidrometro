package porto;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LogIn {
    private JTextField username;
    private JButton logInButton;
    private JPanel base;
    private JLabel logo;
    private JPasswordField password;

    private static String theurl = "jdbc:postgresql://localhost:5432/PortoBsB";
    private static String user = "daniel";
    private static String pass ="password";

    JFrame frame = new JFrame();

    public LogIn(){
        createLogIn();
        logInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    Connection connection= DriverManager.getConnection(theurl, user, pass);
                    String sql = "SELECT * FROM login WHERE username=? AND password = ?";
                    PreparedStatement pst = connection.prepareStatement(sql);
                    pst.setString(1, username.getText());
                    pst.setString(2, password.getText());
                    ResultSet rs = pst.executeQuery();
                    if(rs.next()){
                        new Menu();
                        frame.setVisible(false);
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "Usuario e senha incorreta");
                        username.setText("");
                        password.setText("");
                    }
                    connection.close();
                }
                catch(Exception ex){
                    JOptionPane.showMessageDialog(null, e);
                }

            }
        });
    }

    private void createLogIn(){
        JPanel root = getRootPanel();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setTitle("Database Hidrometro");
        frame.setContentPane(root);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public JPanel getRootPanel(){
        return base;
    }


}
