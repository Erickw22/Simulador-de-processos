import java.awt.*;

public class GanttBloco {
    String nome;
    int inicio;
    int fim;
    int duracao;
    Color cor;

    public GanttBloco(String nome, int inicio, int fim) {
        this.nome = nome;
        this.inicio = inicio;
        this.fim = fim;
        this.duracao = fim - inicio;
        this.cor = new Color((int)(Math.random() * 0x1000000));
    }
}
