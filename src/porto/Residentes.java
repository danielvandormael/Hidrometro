package porto;

public class Residentes {
    private int id;
    private String nome, tipo, cpf_cnpj, numero;

    public Residentes(int id, String nome, String tipo, String cpf_cnpj, String numero){
        this.id=id;
        this.nome=nome;
        this.tipo=tipo;
        this.cpf_cnpj=cpf_cnpj;
        this.numero=numero;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getTipo() {
        return tipo;
    }

    public String getCpf_cnpj() {
        return cpf_cnpj;
    }

    public String getNumero() {
        return numero;
    }
}
