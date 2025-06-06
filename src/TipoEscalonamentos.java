import java.util.*;

public class TipoEscalonamentos {

    public static List<String> execucaoFCFS(List<Processo> processos, List<GanttBloco> ganttBlocos) {
        List<String> resultados = new ArrayList<>();
        List<Processo> lista = new ArrayList<>(processos);
        lista.sort(Comparator.comparingInt(p -> p.tempoChegada)); 

        int tempoAtual = 0;

        for (Processo p : lista) {
            if (tempoAtual < p.tempoChegada) tempoAtual = p.tempoChegada; 
            int espera = tempoAtual - p.tempoChegada;
            int turnaround = espera + p.tempoBurst;

            ganttBlocos.add(new GanttBloco(p.nome, tempoAtual, tempoAtual + p.tempoBurst));

            resultados.add(p.nome + ": Espera = " + espera + ", Turnaround = " + turnaround);
            tempoAtual += p.tempoBurst;
        }
        return resultados;
    }


    public static List<String> execucaoSJF(List<Processo> processos, List<GanttBloco> ganttBlocos) {
        List<String> resultados = new ArrayList<>();
        List<Processo> lista = new ArrayList<>(processos);

        int tempoAtual = 0;

        while (!lista.isEmpty()) {
            List<Processo> disponiveis = new ArrayList<>();
            for (Processo p : lista) {
                if (p.tempoChegada <= tempoAtual) {
                    disponiveis.add(p);
                }
            }

            if (disponiveis.isEmpty()) {
                tempoAtual++;
                continue;
            }

            Processo proximo = Collections.min(disponiveis, Comparator.comparingInt(p -> p.tempoBurst));

            int espera = tempoAtual - proximo.tempoChegada;
            int turnaround = espera + proximo.tempoBurst;

            ganttBlocos.add(new GanttBloco(proximo.nome, tempoAtual, tempoAtual + proximo.tempoBurst));

            resultados.add(proximo.nome + ": Espera = " + espera + ", Turnaround = " + turnaround);
            tempoAtual += proximo.tempoBurst;

            lista.remove(proximo);
        }
        return resultados;
    }


    public static List<String> execucaoPrioridade(List<Processo> processos, List<GanttBloco> ganttBlocos) {
        List<String> resultados = new ArrayList<>();
        List<Processo> lista = new ArrayList<>(processos);

        int tempoAtual = 0;

        while (!lista.isEmpty()) {
            List<Processo> disponiveis = new ArrayList<>();
            for (Processo p : lista) {
                if (p.tempoChegada <= tempoAtual) {
                    disponiveis.add(p);
                }
            }

            if (disponiveis.isEmpty()) {
                tempoAtual++;
                continue;
            }

            Processo proximo = Collections.min(disponiveis, Comparator.comparingInt(p -> p.prioridade));

            int espera = tempoAtual - proximo.tempoChegada;
            int turnaround = espera + proximo.tempoBurst;

            ganttBlocos.add(new GanttBloco(proximo.nome, tempoAtual, tempoAtual + proximo.tempoBurst));

            resultados.add(proximo.nome + ": Espera = " + espera + ", Turnaround = " + turnaround);
            tempoAtual += proximo.tempoBurst;

            lista.remove(proximo);
        }
        return resultados;
    }


    public static List<String> execucaoRR(List<Processo> processosOriginais, int quantum, List<GanttBloco> ganttBlocos) {
        List<String> resultados = new ArrayList<>();
        List<Processo> processos = new ArrayList<>();

        for (Processo p : processosOriginais) {
            processos.add(new Processo(p.nome, p.tempoBurst, p.prioridade, p.tempoChegada));
        }

        Queue<Processo> fila = new LinkedList<>(processos);
        Map<String, Integer> temposConclusao = new HashMap<>();
        int tempoAtual = 0;

        while (!fila.isEmpty()) {
            Processo p = fila.poll();

            int tempoExecutado = Math.min(quantum, p.tempoRestante);

            ganttBlocos.add(new GanttBloco(p.nome, tempoAtual, tempoAtual + tempoExecutado));

            tempoAtual += tempoExecutado;
            p.tempoRestante -= tempoExecutado;

            if (p.tempoRestante > 0) {
                fila.add(p);
            } else {
                temposConclusao.put(p.nome, tempoAtual);
            }
        }

        for (Processo p : processos) {
            int turnaround = temposConclusao.get(p.nome);
            int espera = turnaround - p.tempoBurst;
            resultados.add(p.nome + ": Espera = " + espera + ", Turnaround = " + turnaround);
        }
        return resultados;
    }

    public static List<String> execucaoHibridoMultiplasFilas(List<Processo> processos, int quantum, List<GanttBloco> ganttBlocos) {
        List<String> resultados = new ArrayList<>();

        List<Processo> filaAlta = new ArrayList<>();
        List<Processo> filaBaixa = new ArrayList<>();

        for (Processo p : processos) {
            if (p.prioridade <= 3) {
                filaAlta.add(p);
            } else {
                filaBaixa.add(p);
            }
        }

        resultados.add("=== Fila Alta (RR) ===");
        resultados.addAll(execucaoRR(filaAlta, quantum, ganttBlocos));

        resultados.add("=== Fila Baixa (FIFO) ===");
        resultados.addAll(execucaoFCFS(filaBaixa, ganttBlocos));

        return resultados;
    }
}
