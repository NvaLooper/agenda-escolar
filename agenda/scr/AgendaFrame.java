import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class AgendaFrame implements ActionListener {
    // Conexão com a base de dados
    static final String DB_URL = "jdbc:mysql://localhost:3306/escola";
    static final String USER = "root";
    static final String PASS = "Admin";

    private JFrame agendaFrame = new JFrame();
    private JButton buttonLimpar = new JButton("Limpar");
    private JButton buttonConfirmar = new JButton("Confirmar");
    private JButton buttonVoltar = new JButton("Voltar");
    private JTextArea textareaAssunto = new JTextArea();
    private SecondFrame framePrograma;
    private int loginId;
    private Date dataSelecionada;
    private String dataSelecionadaFormatada;

    AgendaFrame(SecondFrame framePrograma, int loginId, Date dataSelecionada, String dataSelecionadaFormatada, String assunto) {
        this.framePrograma = framePrograma;
        this.loginId = loginId;
        this.dataSelecionada = dataSelecionada;
        this.dataSelecionadaFormatada = dataSelecionadaFormatada; // Armazenar a data formatada

        // Configuração dos botões
        buttonLimpar.setFont(new Font("Calibri", Font.PLAIN, 16));
        buttonLimpar.setFocusable(false);
        buttonLimpar.setBackground(new Color(0xDE6B48));
        buttonLimpar.setForeground(Color.WHITE);
        buttonLimpar.setBounds(210, 390, 120, 30);
        buttonLimpar.setVerticalAlignment(SwingConstants.BOTTOM);
        buttonLimpar.setHorizontalAlignment(SwingConstants.CENTER);
        buttonLimpar.setBorder(BorderFactory.createEtchedBorder());
        buttonLimpar.addActionListener(this);
        
        buttonConfirmar.setFont(new Font("Calibri", Font.PLAIN, 16));
        buttonConfirmar.setFocusable(false);
        buttonConfirmar.setBackground(new Color(0x729B79));
        buttonConfirmar.setForeground(Color.WHITE);
        buttonConfirmar.setBounds(349, 390, 120, 30);
        buttonConfirmar.setBorder(BorderFactory.createEtchedBorder());
        buttonConfirmar.addActionListener(this);
        
        buttonVoltar.setFont(new Font("Calibri", Font.PLAIN, 16));
        buttonVoltar.setFocusable(false);
        buttonVoltar.setBackground(new Color(0x2e2c2f));
        buttonVoltar.setForeground(Color.WHITE);
        buttonVoltar.setBounds(70, 390, 120, 30);
        buttonVoltar.setBorder(BorderFactory.createEtchedBorder());
        buttonVoltar.addActionListener(this);


        JLabel labelTitulo = new JLabel("A data selecionada foi: ");
        labelTitulo.setBounds(25, 35, 400, 30);
        labelTitulo.setFont(new Font("Calibri", Font.BOLD, 22));

        JLabel labeldata = new JLabel(dataSelecionadaFormatada);
        labeldata.setBounds(240, 35, 120, 30);
        labeldata.setFont(new Font("Calibri", Font.BOLD, 20));
        
        // Campo de texto
        textareaAssunto.setBounds(70, 105, 400, 220);
        textareaAssunto.setFont(new Font("Calibri", Font.PLAIN, 18));
        textareaAssunto.setToolTipText("Coloque alguma informação que queira relembrar");
        textareaAssunto.setLineWrap(true);
        textareaAssunto.setWrapStyleWord(true);
        textareaAssunto.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        textareaAssunto.setText(assunto);

        // Configuração do frame
        agendaFrame.setTitle("Agenda Escolar");
        agendaFrame.getContentPane().setBackground(new Color(255, 240, 153));
        agendaFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        agendaFrame.setSize(555, 520);
        agendaFrame.setLayout(null);
        agendaFrame.setVisible(true);
        agendaFrame.add(labelTitulo);
        agendaFrame.add(labeldata);
        agendaFrame.add(buttonConfirmar);
        agendaFrame.add(buttonLimpar);
        agendaFrame.add(buttonVoltar);
        agendaFrame.add(textareaAssunto);
        agendaFrame.setLocationRelativeTo(null);
        agendaFrame.setResizable(false);
        agendaFrame.setIconImage(new ImageIcon("Icon.png").getImage());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonVoltar) {
            agendaFrame.setVisible(false);
            framePrograma.framePrograma.setVisible(true);
        }
        if (e.getSource() == buttonLimpar) {
            textareaAssunto.setText("");
        }
        if (e.getSource() == buttonConfirmar) {
            String assunto = textareaAssunto.getText();
            try {
                String updateQuery = "UPDATE agenda SET assunto = ? WHERE data = ? AND login_id = ?";
                try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                     PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                    stmt.setString(1, assunto);
                    stmt.setDate(2, new java.sql.Date(dataSelecionada.getTime()));
                    stmt.setInt(3, loginId);
                    int affectedRows = stmt.executeUpdate();
                    if (affectedRows == 0) {
                        // Se nenhum registro foi atualizado, insira um novo
                        String insertQuery = "INSERT INTO agenda (data, login_id, assunto) VALUES (?, ?, ?)";
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                            insertStmt.setDate(1, new java.sql.Date(dataSelecionada.getTime()));
                            insertStmt.setInt(2, loginId);
                            insertStmt.setString(3, assunto);
                            insertStmt.executeUpdate();
                        }
                    }
                    // Excluir datas sem assuntos
                    excluirDatasSemAssuntos(conn);
                }
                agendaFrame.setVisible(false);
                framePrograma.framePrograma.setVisible(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void excluirDatasSemAssuntos(Connection conn) throws SQLException {
        String deleteQuery = "DELETE FROM agenda WHERE assunto IS NULL OR assunto = ''";
        try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
            stmt.executeUpdate();
        }
    }
}
