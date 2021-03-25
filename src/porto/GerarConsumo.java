package porto;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.SimpleTimeZone;

public class GerarConsumo {
    private JPanel panel1;
    private JButton gerarButton;
    private JComboBox comboBox1;
    private JTextField aaaaMm01TextField;

    private static String theurl = "jdbc:postgresql://localhost:5432/PortoBsB";
    private static String user = "daniel";
    private static String password ="password";

    JFrame frame = new JFrame();

    public GerarConsumo(){
        createGUI();
        gerarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String date = aaaaMm01TextField.getText();
                String condo = comboBox1.getSelectedItem().toString();
                Boolean checkvaliddate = checkdatevalid(date);
                if(checkvaliddate == false){
                    JOptionPane.showMessageDialog(null, "Error: Data inserido invalida");
                    aaaaMm01TextField.setText("aaaa-mm-01");
                }
                if(checkvaliddate== true){
                    Boolean checkConsumo = checkConsumoMes(getCondominioNum(condo), date);
                    if(checkConsumo == true){
                        frame.dispose();
                        Loading load = new Loading();
                        load.createRoot();
                        JOptionPane.showMessageDialog(null, "Adicionando dados");
                        generateConsumo(date, condo);
                        load.Dispose();
                        JOptionPane.showMessageDialog(null, "Consumo para o condomínio: " + condo + "\n Data: "+ date +"\n Lançando com sucesso");
                    }else if(checkConsumo == false){
                        String [] buttons= {"Não", "Sim"};
                        int i= JOptionPane.showOptionDialog(null, "Consumo ja lançado para \n Condomínio: " + condo + "\n na data: " + date+"\n Dexeija Mudar o lancamento",
                                "Atençâo", 0, JOptionPane.WARNING_MESSAGE, null, buttons, buttons[0]);
                        if(i == 1){
                            frame.dispose();
                            Loading load = new Loading();
                            load.createRoot();
                            deleteConsumoMonth(getCondominioNum(condo), date);
                            generateConsumo(date, condo);
                            load.Dispose();
                            JOptionPane.showMessageDialog(null, "Consumo para o condominio " + condo + " adicionados");
                        }else if(i == 0){
                            frame.dispose();
                            JOptionPane.showMessageDialog(null, "Consumo para o condominio " + condo + " não adicionados");
                        }
                    }
                }
            }
        });
    }

    private void createGUI(){
        JPanel theroot = getRootPanel();
        frame.setContentPane(theroot);
        frame.setSize(400, 500);
        frame.setTitle("Database Hidrometro");
        frame.setLocationRelativeTo(null);
        String [] p= getCondominios();
        comboBox1.setModel(new DefaultComboBoxModel(p));
        frame.setVisible(true);
    }
    public JPanel getRootPanel(){
        return panel1;
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
        if (Integer.parseInt(date.substring(5,7)) == 0 || Integer.parseInt(date.substring(5,7)) > 12 || Integer.parseInt(date.substring(5,7)) < 0 || Integer.parseInt(date.substring(0,4)) < 0){
            return false;
        }
        if(date.charAt(4) != '-'){
            return false;
        }else if(date.charAt(7) != '-'){
            return false;
        }
        return true;
    }

    public Boolean checkConsumoMes(int local, String date){
        try{
            Connection connection = DriverManager.getConnection(theurl, user, password);
            String query1 = "Select * FROM consumo where data_cobranca = '" + date + "' AND local =" + local +";";
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

    public void deleteConsumoMonth(int condominio, String date){
        try{
            Connection connection = DriverManager.getConnection(theurl, user, password);
            String query1 = "Delete from consumo where local = "+condominio+" and data_cobranca= '" + date+"';";
            Statement st = connection.createStatement();
            st.executeUpdate(query1);
            st.close();
            connection.close();
        }catch (SQLException e) {
            System.out.println("Error in connecting to data base");
            e.printStackTrace();
        }
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

    public void generateConsumo(String date, String condo) {
        //goes to the registro table a gets all points of a month
        ArrayList<Registro> registrosMes = registroListMonth(date);
        int p = getCondominioNum(condo);
        ArrayList<Consumo> ConsumoMes = new ArrayList<>();
        int x = 0;
        for (int i = 0; i < registrosMes.size(); i++) {
            ArrayList<Registro> registrospassado = registroListMonth(getMespassado(date), registrosMes.get(i).getaprt_id());
            x = registrosMes.get(i).getmedimento_hidrometro() - registrospassado.get(0).getmedimento_hidrometro();
            Consumo consumo;
            consumo = new Consumo(registrosMes.get(i).getaprt_id(), registrosMes.get(i).getdata_cobranca(), x, calculatePrice(x, registrosMes.get(i).getaprt_id()), p);
            ConsumoMes.add(consumo);
        }
        addConsumo(ConsumoMes);
    }

    public int findCondominio(int aprt_id){
        int p = 0;
        try{
            Connection connection= DriverManager.getConnection(theurl, user, password);
            String query1 = "Select * FROM apartamentos where id = "+ aprt_id +";";
            Statement st = connection.createStatement();
            ResultSet rs= st.executeQuery(query1);
            while(rs.next()){
                p=rs.getInt("local");
            }
            st.close();
            rs.close();
            connection.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }

    public static double Round(double value, int places) { //rounds a double to the desired decimal stick fig
        //so if 20.011 = 20.02
        //if 20.0009 = 20.00
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places +1, RoundingMode.DOWN);
        bd = bd.setScale(places, RoundingMode.UP);
        return bd.doubleValue();
    }

    public double calculatePrice(int consumo, int aprt_id){
        //checks which condominium the apartment is in
        double x;
        int place= findCondominio(aprt_id);
        if(place == 1){
            x= consumo*5.54;
            return Round(x, 2);
        }else if(place == 2){
            if(consumo <= 10.0){
                return 0.0;
            }
            x= (consumo-10.0)*5.8;
            return Round(x, 2);
        }
        return 0.0;
    }

    public String getMespassado(String data){ //pega o mes passado
        String previous ="n foi";
        if(data.substring(5,7).equals("12")){
            previous= data.substring(0,5) + "11" + "-01";
        }else if(data.substring(5,7).equals("11")){
            previous= data.substring(0,5) + "10" + "-01";
        }else if(data.substring(5,7).equals("10")){
            previous= data.substring(0,5) + "09" + "-01";
        }else if(data.substring(5,7).equals("09")){
            previous= data.substring(0,5) + "08" + "-01";
        }else if(data.substring(5,7).equals("08")){
            previous= data.substring(0,5) + "07" + "-01";
        }else if(data.substring(5,7).equals("07")){
            previous= data.substring(0,5) + "06" + "-01";
        }else if(data.substring(5,7).equals("06")){
            previous= data.substring(0,5) + "05" + "-01";
        }else if(data.substring(5,7).equals("05")){
            previous= data.substring(0,5) + "04" + "-01";
        }else if(data.substring(5,7).equals("04")){
            previous= data.substring(0,5) + "03" + "-01";
        }else if(data.substring(5,7).equals("03")){
            previous= data.substring(0,5) + "02" + "-01";
        }else if(data.substring(5,7).equals("02")){
            previous= data.substring(0,5) + "01" + "-01";
        }else if(data.substring(5,7).equals("01")){
            int year = Integer.parseInt(data.substring(0,4));
            year -= 1;
            previous= String.valueOf(year) + "-12-01";
        }
        return previous;
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

    public void addConsumo(ArrayList<Consumo> toadd){
        for(int i=0; i< toadd.size();i++ ) {
            try {
                Connection connection = DriverManager.getConnection(theurl, user, password);
                String query1 = "INSERT INTO consumo (aprt_id, data_cobranca, consumo, preco, \"local\") VALUES (" + toadd.get(i).getAprt_id() + ",'" + toadd.get(i).getMes_e_ano_da_cobranca() + "'," + toadd.get(i).getConsumo() + "," + toadd.get(i).getPreco() + ","+ toadd.get(i).getLocal()+");";
                Statement st = connection.createStatement();
                st.executeUpdate(query1);
                connection.close();
            } catch (SQLException e) {
                System.out.println("Error in connecting to data base");
                e.printStackTrace();
            }
        }
    }

    public ArrayList<Registro> registroListMonth(String monthyear){ //gets all registros of a month
        ArrayList<Registro> registroList = new ArrayList<>();
        try{
            Connection connection= DriverManager.getConnection(theurl, user, password);
            String query1 = "Select * FROM registro where data_cobranca = '"+ monthyear+ "';";
            Statement st = connection.createStatement();
            ResultSet rs= st.executeQuery(query1);
            Registro registro;
            while(rs.next()){
                registro=new Registro(rs.getString("data_cobranca"), rs.getString("data_lancamento"), rs.getInt("aprt_id"), rs.getInt("medimento_hidrometro"));
                registroList.add(registro);
            }
            st.close();
            rs.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println("Error in connecting to data base");
            e.printStackTrace();
        }
        return registroList;
    }
    public ArrayList<Registro> registroListMonth(String monthyear, int aprt_id){ //gets all registros of a month
        ArrayList<porto.Registro> registroList = new ArrayList<>();
        try{
            Connection connection= DriverManager.getConnection(theurl, user, password);
            String query1 = "Select * FROM registro where data_cobranca = '"+ monthyear+ "' AND aprt_id = " + aprt_id +";";
            Statement st = connection.createStatement();
            ResultSet rs= st.executeQuery(query1);
            porto.Registro registro;
            while(rs.next()){
                registro=new porto.Registro(rs.getString("data_cobranca"), rs.getString("data_lancamento"), rs.getInt("aprt_id"), rs.getInt("medimento_hidrometro"));
                registroList.add(registro);
            }
            st.close();
            rs.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println("Error in connecting to data base");
            e.printStackTrace();
        }
        return registroList;
    }
}
