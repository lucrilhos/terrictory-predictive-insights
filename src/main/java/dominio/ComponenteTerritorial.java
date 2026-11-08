package dominio;

/**
 * Qualquer "peça" do território que pode ser monitorada (um ponto de alagamento,
 * uma ponte, uma escola...). É abstrata: não existe um componente "genérico",
 * só tipos concretos que herdam dela.
 */
public abstract class ComponenteTerritorial {

    private final int id;
    private final String nome;
    private final String regiao;
    private String status;

    protected ComponenteTerritorial(int id, String nome, String regiao) {
        this.id = id;
        this.nome = nome;
        this.regiao = regiao;
        this.status = "NÃO VERIFICADO";
    }

    /**
     * Cada tipo de componente decide como verificar a própria situação
     * e atualiza o status. Por isso o método é abstrato.
     */
    public abstract String verificarStatus();

    // Só as filhas podem mudar o status (protected)
    protected void setStatus(String status) {
        this.status = status;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getRegiao() { return regiao; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return String.format("#%d %s (%s) - status: %s", id, nome, regiao, status);
    }
}
