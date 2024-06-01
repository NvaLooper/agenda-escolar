import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ConfigFrame implements ActionListener {
    MainFrame framelogin;
    SecondFrame framePrograma;
    JFrame configframe = new JFrame();
    JButton buttonLogoff = new JButton("Logoff");
    JButton buttonTrocar = new JButton("Trocar Senha");
    JLabel labelTitulo = new JLabel("Definições");

    public ConfigFrame(MainFrame framelogin, SecondFrame framePrograma) {
        this.framelogin = framelogin;
        this.framePrograma = framePrograma;

        // Titulo
        labelTitulo.setFont(new Font("Calibri", Font.BOLD, 20));
        labelTitulo.setBounds(50, 12, 90, 30);

        // Botão Logoff
        buttonLogoff.setFont(new Font("Calibri", Font.PLAIN, 16));
        buttonLogoff.setBounds(32, 90, 120, 30);
        buttonLogoff.setFocusable(false);
        buttonLogoff.addActionListener(this);
        buttonLogoff.setBackground(new Color(0xDE6B48));
        buttonLogoff.setForeground(Color.WHITE);

        // Botão Trocar Palavra-passe
        buttonTrocar.setFont(new Font("Calibri", Font.PLAIN, 16));
        buttonTrocar.setBounds(32, 50, 120, 30);
        buttonTrocar.setFocusable(false);
        buttonTrocar.addActionListener(this);
        buttonTrocar.setBackground(new Color(0x729B79));
        buttonTrocar.setForeground(Color.WHITE);

        // Configurar Tela
        configframe.setSize(200, 200);
        configframe.setLayout(null);
        configframe.setLocationRelativeTo(null);
        configframe.setIconImage(new ImageIcon("Icon.png").getImage());
        configframe.getContentPane().setBackground(new Color(255, 240, 153));
        configframe.setResizable(false);
        configframe.add(buttonLogoff);
        configframe.add(buttonTrocar);
        configframe.add(labelTitulo);
    }

    public void setVisible(boolean visible) {
        configframe.setVisible(visible);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonLogoff) {
            configframe.setVisible(false);
            framePrograma.setVisible(false);
            framelogin.frameLogin.setVisible(true);
            framelogin.LimparLogoff();
        } else if (e.getSource() == buttonTrocar) {
            // Abre um popup para digitar a nova senha
            String novaSenha = JOptionPane.showInputDialog(configframe, "Digite a nova senha:", "Trocar palavra-passe", JOptionPane.PLAIN_MESSAGE);
            if (novaSenha != null && !novaSenha.trim().isEmpty()) {
                trocarSenha(novaSenha);
            } else {
                JOptionPane.showMessageDialog(configframe, "Senha inválida. Tente novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    // Troca a senha na base de dados
    private void trocarSenha(String novaSenha) {
        try (Connection conn = DriverManager.getConnection(MainFrame.DB_URL, MainFrame.USER, MainFrame.PASS);
             PreparedStatement stmt = conn.prepareStatement("UPDATE login SET Senha = ? WHERE idLogin = ?")) {
            stmt.setString(1, novaSenha);
            stmt.setInt(2, framelogin.getLoginId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(configframe, "Senha alterada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(configframe, "Falha ao alterar a senha. Tente novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(configframe, "Erro ao conectar ao banco de dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}