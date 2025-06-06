import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class InterfaceGrafica extends JFrame {

    private JTextField qtdProcessosField;
    private JComboBox<String> algoritmoBox;
    private JTable tabelaProcessos;
    private JTextArea resultadoArea;
    private JPanel graficoPanel;

    private List<Processo> processos = new ArrayList<>();
    private List<GanttBloco> ganttBlocos = new ArrayList<>();

    public InterfaceGrafica() {
        setTitle("Simulador de Processos");
        setSize(1000, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));

        // Painel Superior - Controle
        JPanel topo = new JPanel(new GridBagLayout());
        topo.setBackground(new Color(60, 63, 65));
        topo.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        JLabel qtdLabel = new JLabel("Número de Processos:");
        qtdLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        topo.add(qtdLabel, gbc);

        qtdProcessosField = new JTextField(5);
        gbc.gridx = 1;
        topo.add(qtdProcessosField, gbc);

        JLabel algoritmoLabel = new JLabel("Algoritmo:");
        algoritmoLabel.setForeground(Color.WHITE);
        gbc.gridx = 2;
        topo.add(algoritmoLabel, gbc);

        String[] algoritmos = {"FIFO", "SJF", "Prioridade", "Round Robin", "Híbrido"};
        algoritmoBox = new JComboBox<>(algoritmos);
        gbc.gridx = 3;
        topo.add(algoritmoBox, gbc);

        JButton gerarButton = new JButton("Gerar Processos");
        estilizaBotao(gerarButton);
        gerarButton.addActionListener(e -> gerarProcessos());
        gbc.gridx = 4;
        topo.add(gerarButton, gbc);

        JButton iniciarButton = new JButton("Executar Simulação");
        estilizaBotao(iniciarButton);
        iniciarButton.addActionListener(e -> iniciarSimulacao());
        gbc.gridx = 5;
        topo.add(iniciarButton, gbc);

        add(topo, BorderLayout.NORTH);


        String[] colunas = {"PID", "Tempo de Chegada", "Tempo de Burst", "Prioridade"};
        DefaultTableModel modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaProcessos = new JTable(modeloTabela);
        JScrollPane tabelaScroll = new JScrollPane(tabelaProcessos);
        tabelaScroll.setBorder(BorderFactory.createTitledBorder("Processos"));
        add(tabelaScroll, BorderLayout.WEST);

        JPanel painelCentro = new JPanel(new BorderLayout(10, 10));
        resultadoArea = new JTextArea();
        resultadoArea.setEditable(false);
        resultadoArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultadoArea.setBackground(new Color(30, 30, 30));
        resultadoArea.setForeground(new Color(50, 205, 50));
        resultadoArea.setBorder(BorderFactory.createTitledBorder("Resultados"));

        JScrollPane resultadoScroll = new JScrollPane(resultadoArea);
        painelCentro.add(resultadoScroll, BorderLayout.CENTER);

        add(painelCentro, BorderLayout.CENTER);

        graficoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                desenharGraficoGantt(g);
            }
        };
        graficoPanel.setPreferredSize(new Dimension(900, 250));
        graficoPanel.setBackground(Color.WHITE);
        graficoPanel.setBorder(BorderFactory.createTitledBorder("Gráfico de Gantt"));
        add(graficoPanel, BorderLayout.SOUTH);
    }

    private void estilizaBotao(JButton botao) {
        botao.setBackground(new Color(70, 130, 180));
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setFont(new Font("Arial", Font.BOLD, 12));
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void gerarProcessos() {
        try {
            processos.clear();
            resultadoArea.setText("");
            ganttBlocos.clear();

            DefaultTableModel modelo = (DefaultTableModel) tabelaProcessos.getModel();
            modelo.setRowCount(0);

            int qtd = Integer.parseInt(qtdProcessosField.getText());
            for (int i = 0; i < qtd; i++) {
                int tempoChegada = (int) (Math.random() * 10);  
                int tempoBurst = 1 + (int) (Math.random() * 10);  
                int prioridade = 1 + (int) (Math.random() * 5);  

                modelo.addRow(new Object[]{i + 1, tempoChegada, tempoBurst, prioridade});
            }
            graficoPanel.repaint();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um número válido de processos.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void iniciarSimulacao() {
        try {
            resultadoArea.setText("");
            ganttBlocos.clear();
            processos.clear();

            DefaultTableModel modelo = (DefaultTableModel) tabelaProcessos.getModel();
            int rowCount = modelo.getRowCount();

            for (int i = 0; i < rowCount; i++) {
                try {
                    int pid = Integer.parseInt(modelo.getValueAt(i, 0).toString());
                    int tempoChegada = Integer.parseInt(modelo.getValueAt(i, 1).toString());
                    int tempoBurst = Integer.parseInt(modelo.getValueAt(i, 2).toString());
                    int prioridade = Integer.parseInt(modelo.getValueAt(i, 3).toString());

                    Processo p = new Processo("P" + pid, tempoBurst, prioridade, tempoChegada);
                    processos.add(p);
                } catch (NumberFormatException | NullPointerException e) {
                    JOptionPane.showMessageDialog(this, "Erro: Preencha corretamente todas as células da tabela com números.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            String algoritmo = (String) algoritmoBox.getSelectedItem();
            List<String> resultados = new ArrayList<>();

            switch (algoritmo) {
                case "FIFO":
                    resultados = TipoEscalonamentos.execucaoFCFS(processos, ganttBlocos);
                    break;
                case "SJF":
                    resultados = TipoEscalonamentos.execucaoSJF(processos, ganttBlocos);
                    break;
                case "Prioridade":
                    resultados = TipoEscalonamentos.execucaoPrioridade(processos, ganttBlocos);
                    break;
                case "Round Robin":
                    String input = JOptionPane.showInputDialog(this, "Informe o quantum:");
                    int quantum = Integer.parseInt(input);
                    resultados = TipoEscalonamentos.execucaoRR(processos, quantum, ganttBlocos);
                    break;
                case "Híbrido":
                    String inputHibrido = JOptionPane.showInputDialog(this, "Informe o quantum para Fila Alta:");
                    int quantumHibrido = Integer.parseInt(inputHibrido);
                    resultados = TipoEscalonamentos.execucaoHibridoMultiplasFilas(processos, quantumHibrido, ganttBlocos);
                    break;
            }

            for (String res : resultados) {
                resultadoArea.append(res + "\n");
            }

            graficoPanel.repaint();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Erro ao executar a simulação.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void desenharGraficoGantt(Graphics g) {
        if (ganttBlocos.isEmpty()) return;

        int x = 20;
        int y = 50;
        int altura = 40;
        int larguraUnit = 30;

        for (GanttBloco bloco : ganttBlocos) {
            int largura = bloco.duracao * larguraUnit;

            g.setColor(bloco.cor);
            g.fillRoundRect(x, y, largura, altura, 15, 15);
            g.setColor(Color.BLACK);
            g.drawRoundRect(x, y, largura, altura, 15, 15);

            g.drawString(bloco.nome, x + 5, y + 25);
            g.drawString("" + bloco.inicio, x, y + altura + 15);

            x += largura;
        }
        g.drawString("" + (ganttBlocos.get(ganttBlocos.size() - 1).fim), x, y + altura + 15);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InterfaceGrafica gui = new InterfaceGrafica();
            gui.setVisible(true);
        });
    }
}
