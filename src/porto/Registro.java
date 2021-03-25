package porto;

public class Registro {
    private int aprt_id, medimento_hidrometro;
    private String data_lancamento, data_cobranca;

    public Registro(String data_cobranca, String data_lancamento, int aprt_id, int medimento_hidrometro){
        this.aprt_id=aprt_id;
        this.medimento_hidrometro=medimento_hidrometro;
        this.data_lancamento=data_lancamento;
        this.data_cobranca=data_cobranca;
    }

    public int getaprt_id() {
        return aprt_id;
    }

    public int getmedimento_hidrometro() {
        return medimento_hidrometro;
    }

    public String getdata_lancamento() {
        return data_lancamento;
    }

    public String getdata_cobranca() {
        return data_cobranca;
    }
}
