package porto;

import java.math.*;
import java.sql.*;
import java.util.ArrayList;

public class Commands {

    private static String theurl = "jdbc:postgresql://localhost:5432/PortoBsB";
    private static String user = "daniel";
    private static String password ="password";


    //falta improve velocidade na comparacao de apartamentos
    public void generateConsumo(String date){
        //goes to the registro table a gets all points of a month
        ArrayList<Registro> registrosMes= registroListMonth(date);
        ArrayList<Registro> registrospassado= registroListMonth(getMespassado(date));
        ArrayList<Consumo> ConsumoMes= new ArrayList<>();
        int x = 0;
        for(int i = 0; i < registrosMes.size(); i++){
            for(int j= 0; j< registrospassado.size(); j++){
                if(registrosMes.get(i).getaprt_id() == registrospassado.get(j).getaprt_id()){
                    x = registrosMes.get(i).getmedimento_hidrometro()-registrospassado.get(j).getmedimento_hidrometro();
                }
            }
            Consumo consumo;
            consumo = new Consumo(registrosMes.get(i).getaprt_id(), registrosMes.get(i).getdata_cobranca(), x, calculatePrice(x, registrosMes.get(i).getaprt_id()), 9);
            ConsumoMes.add(consumo);
        }



        addConsumo(ConsumoMes);

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
        } catch (SQLException e) {
            System.out.println("Error in connecting to data base");
            e.printStackTrace();
        }
        return registroList;
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

    public ArrayList<Consumo> getConsumoMonth(String monthyear){
        // will make a list will all the readings of a month
        //uses the month do define the search in the postgresql database
        ArrayList<Consumo> monthlist = new ArrayList<>();
        try{
            Connection connection= DriverManager.getConnection(theurl, user, password);
            String query = "SELECT * FROM consumo where data_cobranca = '" + monthyear +"';";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            Consumo registro;
            while(rs.next()){
                registro=new Consumo(rs.getInt("aprt_id"), rs.getString("data_cobranca"), rs.getInt("consumo"), rs.getInt("preco"), rs.getInt("local"));
                monthlist.add(registro);
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return monthlist;
    }

    public ArrayList<Integer> getApartmentswithXgrowth(int percentage, String currentmonthyear, String previmonthyear){ //should work
        ArrayList<Integer> list = new ArrayList<>();
        ArrayList<Consumo> previmonth = getConsumoMonth(currentmonthyear);
        ArrayList<Consumo> currentmonth = getConsumoMonth(previmonthyear);
        for(int x = 0; x < currentmonth.size(); x++) {
            for (int y = 0; y < previmonth.size(); y++) {
                if (currentmonth.get(x).getAprt_id() == previmonth.get(y).getAprt_id()) {
                    if ((currentmonth.get(x).getConsumo()) * 100 >= ((previmonth.get(x).getConsumo()) * percentage) + ((previmonth.get(x).getConsumo()) * 100)) {
                        list.add(currentmonth.get(x).getAprt_id());
                    }
                }
            }
        }
        return list;
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

    public void addConsumo(ArrayList<Consumo> toadd){
        for(int i=0; i< toadd.size();i++ ) {
            try {
                Connection connection = DriverManager.getConnection(theurl, user, password);
                String query1 = "INSERT INTO consumo (aprt_id, data_cobranca, consumo, preco) VALUES (" + toadd.get(i).getAprt_id() + ",'" + toadd.get(i).getMes_e_ano_da_cobranca() + "'," + toadd.get(i).getConsumo() + "," + toadd.get(i).getPreco() + ");";
                Statement st = connection.createStatement();
                st.executeUpdate(query1);
                connection.close();
            } catch (SQLException e) {
                System.out.println("Error in connecting to data base");
                e.printStackTrace();
            }
        }
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

    public Boolean checkConusmoalreadyadded(String date){
        Boolean works = false;
        int x=0;
        //checar se residente ja existe atraves do numero do cpf_cnpj
        try{
            Connection connection= DriverManager.getConnection(theurl, user, password);
            String query1= "select * from consumo where numero = '"+date+"';";
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

    public void deleteConsumoOfMonth(String monthyear){
        try{
            Connection connection= DriverManager.getConnection(theurl, user, password);
            String query1= "DELETE FROM consumo WHERE data_cobranca = '" + monthyear + "';";
            Statement st = connection.createStatement();
            st.executeQuery(query1);
            st.close();
            connection.close();
        }catch (SQLException e) {
            System.out.println("Error in connecting to data base");
            e.printStackTrace();
        }
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

    public void addApartamento(int local, String bloco, int apartamento){
        try{
            Connection connection= DriverManager.getConnection(theurl, user, password);
            String query1= "INSERT INTO apartamentos (local, bloco, apartamento) VALUES ('"+ local + "'," + bloco + "," + apartamento +");";
            Statement st = connection.createStatement();
            st.executeQuery(query1);
            st.close();
            connection.close();
        }catch (SQLException e) {
            System.out.println("Error in connecting to data base");
            e.printStackTrace();
        }
    }

    public Boolean checkResidentenovo(String numero){
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
            st.close();
            connection.close();
        }catch (SQLException e) {
            System.out.println("Error in connecting to data base");
            e.printStackTrace();
        }
        if(x== 0) works= true;
        return works;
    }

    public void addResidente(String nome, String tipo, String cpf_cnpj, String numero ){
        try{
            Connection connection= DriverManager.getConnection(theurl, user, password);
            String query1= "INSERT INTO residentes (nome, tipo, cpf_cnpj, numero) VALUES ('"+ nome + "','" + tipo + "','" + cpf_cnpj +"','"+ numero + "');";
            Statement st = connection.createStatement();
            st.executeQuery(query1);
            st.close();
            connection.close();
        }catch (SQLException e) {
            System.out.println("Error in connecting to data base");
            e.printStackTrace();
        }
    }

}
