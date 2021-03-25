package porto;

import java.util.Objects;

public class Consumo {
    private int consumo, aprt_id, local;
    private double preco;
    private String mes_e_ano_da_cobranca;

    public Consumo(int aprt_id, String mes_e_ano_da_cobranca, int consumo, double preco, int local){ //constructer
        this.aprt_id= aprt_id;
        this.mes_e_ano_da_cobranca=mes_e_ano_da_cobranca;
        this.consumo =consumo;
        this.preco=preco;
        this.local=local;
    }

    public int getLocal() {
        return local;
    }
    public int getConsumo() {
        return consumo;
    }

    public double getPreco() {
        return preco;
    }

    public int getAprt_id() {
        return aprt_id;
    }

    public String getMes_e_ano_da_cobranca() {
        return mes_e_ano_da_cobranca;
    }
}
