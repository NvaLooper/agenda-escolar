import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.sql.*;

public class MainFrame implements ActionListener {

    // Conexão com a base de dados
    static final String DB_URL = "jdbc:mysql://localhost:3306/escola";
    static final String USER = "root";
    static final String PASS = "Admin";

    JFrame frameLogin = new JFrame();
    JButton buttonEntrar = new JButton("Entrar");
    JTextField textFieldLogin = new JTextField();
    JPasswordField passwordFieldSenha = new JPasswordField();

    // Adicionando o campo loginId
    private int loginId;
    private String Login;

    // Limpar os campos quando o botão logoff for clicado
    public void LimparLogoff() {
        textFieldLogin.setText("");
        passwordFieldSenha.setText("");
    }
    
    MainFrame() { // Configuração da tela de login

        // Título
        JLabel labelTitulo = new JLabel("Bem vindo à agenda");
        labelTitulo.setForeground(Color.BLACK);
        labelTitulo.setFont(new Font("Calibri", Font.BOLD, 22));
        labelTitulo.setBounds(90, 20, 250, 30);

        // Login
        JLabel labelLogin = new JLabel("Login");
        labelLogin.setForeground(Color.BLACK);
        labelLogin.setFont(new Font("Calibri", Font.PLAIN, 22));
        labelLogin.setBounds(50, 70, 100, 30);

        textFieldLogin.setBounds(50, 100, 300, 30);
        textFieldLogin.setToolTipText("Coloque seu Login");

        // Senha
        JLabel labelSenha = new JLabel("Senha");
        labelSenha.setForeground(Color.BLACK);
        labelSenha.setFont(new Font("Calibri", Font.PLAIN, 22));
        labelSenha.setBounds(50, 150, 100, 30);

        passwordFieldSenha.setBounds(50, 180, 300, 30);
        passwordFieldSenha.setToolTipText("Coloque sua senha");

        // Botão
        buttonEntrar.setFocusable(false);
        buttonEntrar.setFont(new Font("Calibri", Font.PLAIN, 16));
        buttonEntrar.setBackground(new Color(0x729B79));
        buttonEntrar.setForeground(Color.WHITE);
        buttonEntrar.setBorder(BorderFactory.createEtchedBorder());
        buttonEntrar.setBounds(143, 250, 100, 30);
        buttonEntrar.addActionListener(this);

        // Imagem
        JLabel labelIcon = new JLabel();
        ImageIcon icon = new ImageIcon("IconReduzido.png");
        labelIcon.setIcon(icon);
        labelIcon.setBounds(280, 5, 50, 50);

        // Configurando o frame
        frameLogin.getContentPane().setBackground(new Color(255, 240, 153));
        frameLogin.setTitle("Agenda Escolar");
        frameLogin.setIconImage(new ImageIcon("Icon.png").getImage());
        frameLogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameLogin.setResizable(false);
        frameLogin.setLayout(null);
        frameLogin.add(labelTitulo);
        frameLogin.add(labelLogin);
        frameLogin.add(textFieldLogin);
        frameLogin.add(labelSenha);
        frameLogin.add(passwordFieldSenha);
        frameLogin.add(buttonEntrar);
        frameLogin.add(labelIcon);
        frameLogin.setSize(400, 370);
        frameLogin.setVisible(true);
        frameLogin.setLocationRelativeTo(null);
    }

    // Sistema de login
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonEntrar) {
            String login = textFieldLogin.getText();
            String senha = new String(passwordFieldSenha.getPassword());
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM escola.login WHERE Login = ? AND Senha = ?")) {
                stmt.setString(1, login); // Definindo o primeiro parâmetro como uma string
                stmt.setString(2, senha); // Definindo o segundo parâmetro como uma string
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // Se as credenciais estiverem corretas, definir o loginId
                        loginId = rs.getInt("idLogin");
                        Login = rs.getString("Login");
                        // Abrir a próxima janela
                        SecondFrame secondFrame = new SecondFrame(this);
                        frameLogin.setVisible(false);
                    } else {
                        // Se as credenciais estiverem incorretas, exibir uma mensagem de erro
                        JOptionPane.showMessageDialog(frameLogin, "Login ou senha incorretos.", "Erro de Login", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                // Trate a exceção adequadamente, por exemplo, exibindo uma mensagem de erro para o usuário
                JOptionPane.showMessageDialog(frameLogin, "Erro de conexão: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Método getter para loginId
    public int getLoginId() {
        return loginId;
    }

    public String getLogin() {
        return Login;
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}
