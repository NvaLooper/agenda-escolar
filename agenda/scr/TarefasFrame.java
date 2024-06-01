import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.ImageIcon;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TarefasFrame implements ActionListener {
    static final String DB_URL = "jdbc:mysql://localhost:3306/escola";
    static final String USER = "root";
    static final String PASS = "Admin";
    private MainFrame mainFrame;
    private SecondFrame secondFrame;
    private JButton buttonVoltar = new JButton("Voltar");
    JFrame tarefasFrame = new JFrame();
    JButton buttonSalvar = new JButton("Salvar");
    JButton buttonConcluido = new JButton("Concluir tarefa");
    JLabel labelData = new JLabel("Data");
    JLabel labelAssunto = new JLabel("Assunto");
    JTextArea textareaAssunto = new JTextArea();
    JList<String> listDatas;
    DefaultListModel<String> listModel;

    public TarefasFrame(MainFrame mainFrame, SecondFrame secondFrame) {
        this.mainFrame = mainFrame;
        this.secondFrame = secondFrame;

        // Configurar Tela
        tarefasFrame.setTitle("Tarefas");
        tarefasFrame.setSize(600, 500);
        tarefasFrame.setLayout(null);
        tarefasFrame.setLocationRelativeTo(null);
        tarefasFrame.setIconImage(new ImageIcon("Icon.png").getImage());
        tarefasFrame.getContentPane().setBackground(new Color(255, 240, 153));
        tarefasFrame.setResizable(false);
        tarefasFrame.setVisible(true);
        tarefasFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tarefasFrame.add(buttonConcluido);
        tarefasFrame.add(buttonVoltar);
        tarefasFrame.add(labelData);
        tarefasFrame.add(labelAssunto);
        tarefasFrame.add(textareaAssunto);
        tarefasFrame.add(buttonSalvar);

        // Botão Concluido
        buttonConcluido.setFont(new Font("Calibri", Font.PLAIN, 16));
        buttonConcluido.setBounds(58, 405, 120, 30);
        buttonConcluido.setFocusable(false);
        buttonConcluido.addActionListener(this);
        buttonConcluido.setBackground(new Color(0x6290d3));
        buttonConcluido.setBorder(BorderFactory.createEtchedBorder());
        buttonConcluido.setForeground(Color.WHITE);

        // Botão Salvar
        buttonSalvar.setFont(new Font("Calibri", Font.PLAIN, 16));
        buttonSalvar.setBounds(410, 405, 120, 30);
        buttonSalvar.setFocusable(false);
        buttonSalvar.addActionListener(this);
        buttonSalvar.setBackground(new Color(0x729B79));
        buttonSalvar.setBorder(BorderFactory.createEtchedBorder());
        buttonSalvar.setForeground(Color.WHITE);

        // Botão Voltar
        buttonVoltar.setFont(new Font("Calibri", Font.PLAIN, 16));
        buttonVoltar.setFocusable(false);
        buttonVoltar.setBackground(new Color(0xDE6B48));
        buttonVoltar.setForeground(Color.WHITE);
        buttonVoltar.setBounds(260, 405, 120, 30);
        buttonVoltar.setBorder(BorderFactory.createEtchedBorder());
        buttonVoltar.addActionListener(this);

        // Titulos
        labelData.setFont(new Font("Calibri", Font.BOLD, 20));
        labelData.setBounds(20, 25, 90, 30);
        labelAssunto.setFont(new Font("Calibri", Font.BOLD, 20));
        labelAssunto.setBounds(245, 25, 90, 30);

        // Campo de texto
        textareaAssunto.setBounds(240, 65, 310, 320);
        textareaAssunto.setFont(new Font("Calibri", Font.PLAIN, 18));
        textareaAssunto.setLineWrap(true);
        textareaAssunto.setWrapStyleWord(true);
        textareaAssunto.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Lista de datas
        listModel = new DefaultListModel<>();
        listDatas = new JList<>(listModel);
        listDatas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listDatas.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                mostrarAssunto();
            }
        });

        JScrollPane listScrollPane = new JScrollPane(listDatas);
        listScrollPane.setBounds(20, 65, 200, 320);
        tarefasFrame.add(listScrollPane);

        // Carregar datas do banco de dados
        carregarDatasTarefas();
    }

    private void carregarDatasTarefas() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String sql = "SELECT data FROM agenda WHERE login_id = ? ORDER BY data";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, mainFrame.getLoginId());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                listModel.addElement(rs.getString("data"));
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAssunto() {
        String selectedDate = listDatas.getSelectedValue();
        if (selectedDate != null) {
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                String sql = "SELECT assunto FROM agenda WHERE login_id = ? AND data = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, mainFrame.getLoginId());
                stmt.setString(2, selectedDate);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    textareaAssunto.setText(rs.getString("assunto"));
                }

                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void salvarAssunto() {
        String selectedDate = listDatas.getSelectedValue();
        String novoAssunto = textareaAssunto.getText();
        
        if (selectedDate != null && novoAssunto != null) {
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                String sql = "UPDATE agenda SET assunto = ? WHERE login_id = ? AND data = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, novoAssunto);
                stmt.setInt(2, mainFrame.getLoginId());
                stmt.setString(3, selectedDate);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(tarefasFrame, "Assunto atualizado", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(tarefasFrame, "Falha ao atualizar o assunto", "Falha", JOptionPane.ERROR_MESSAGE);
                }

                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    private void concluirTarefa() {
        String selectedDate = listDatas.getSelectedValue();
        
        if (selectedDate != null) {
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                String sql = "DELETE FROM agenda WHERE login_id = ? AND data = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, mainFrame.getLoginId());
                stmt.setString(2, selectedDate);
    
                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("Tarefa concluída e removida com sucesso.");
                    listModel.removeElement(selectedDate);
                    textareaAssunto.setText("");
                } else {
                    System.out.println("Erro ao remover a tarefa.");
                }
    
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonVoltar) {
            tarefasFrame.setVisible(false);
            secondFrame.setVisible(true);
        } else if (e.getSource() == buttonSalvar) {
            salvarAssunto();
        } else if (e.getSource() == buttonConcluido) {
            concluirTarefa();
        }
    }
}
