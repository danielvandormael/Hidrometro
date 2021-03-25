package porto;

public class Apartamentos {
    private int id, apartamento,local;

    private String bloco;

    public Apartamentos(int id, int local, String bloco, int apartamento){
        this.id=id;
        this.local=local;
        this.bloco=bloco;
        this.apartamento=apartamento;
    }

    public int getId() {
        return id;
    }

    public int getApartamento() {
        return apartamento;
    }

    public int getLocal() {
        return local;
    }

    public String getBloco() {
        return bloco;
    }
}
