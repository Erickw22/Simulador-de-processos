public class Processo {
    String nome;
    int tempoBurst;
    int prioridade;
    int tempoChegada;
    int tempoRestante;

    public Processo(String nome, int tempoBurst, int prioridade, int tempoChegada) {
        this.nome = nome;
        this.tempoBurst = tempoBurst;
        this.prioridade = prioridade;
        this.tempoChegada = tempoChegada;
        this.tempoRestante = tempoBurst;
    }
}
