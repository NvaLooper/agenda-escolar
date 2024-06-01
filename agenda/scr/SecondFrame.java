import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import com.toedter.calendar.JCalendar;

public class SecondFrame implements ActionListener {
    ConfigFrame configframe;
    JFrame framePrograma = new JFrame();
    JButton buttontarefas = new JButton("Tarefas");
    JButton buttonAcessar = new JButton("Acessar");
    JButton buttonLogoff = new JButton("Logoff");
    JCalendar calendar = new JCalendar();
    JButton buttonConfig = new JButton("⚙");
    MainFrame framelogin;
    TarefasFrame tarefasframe;
    private Date ultimaDataSelecionada;
    Color CorPrincipal = new Color(255, 240, 153);

    SecondFrame(MainFrame frameLogin) {
        this.framelogin = frameLogin;
        // Calendário
        calendar.setBackground(Color.BLACK);
        calendar.setForeground(Color.BLACK);
        calendar.setFont(new Font("Calibri", Font.PLAIN, 15));
        calendar.setBounds(18, 80, 450, 210);
        // Definição de data máxima que pode ser anotado algo
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.YEAR, -1);
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, 2);
        calendar.setMinSelectableDate(minDate.getTime());
        calendar.setMaxSelectableDate(maxDate.getTime());

        // Título
        JLabel labelTitulo = new JLabel("Bem vindo a sua agenda, " + frameLogin.getLogin());
        labelTitulo.setForeground(Color.BLACK);
        labelTitulo.setFont(new Font("Calibri", Font.BOLD, 22));
        labelTitulo.setBounds(20, 33, 420, 30);

        // Botão Acessar
        buttonAcessar.setFont(new Font("Calibri", Font.PLAIN, 16));
        buttonAcessar.setBounds(195, 350, 90, 30);
        buttonAcessar.setFocusable(false);
        buttonAcessar.addActionListener(this);
        buttonAcessar.setBackground(new Color(0x729B79));
        buttonAcessar.setForeground(Color.WHITE);
        buttonAcessar.setBorder(BorderFactory.createEtchedBorder());
        buttonAcessar.setEnabled(false); // Começa desabilitado para evitar erros
        
        // Botão de tarefas
         buttontarefas.setFont(new Font("Calibri", Font.PLAIN, 16));
         buttontarefas.setBounds(379, 295, 90, 30);
         buttontarefas.setFocusable(false);
         buttontarefas.addActionListener(this);
         buttontarefas.setBackground(new Color(0x148370));
         buttontarefas.setForeground(Color.WHITE);
         buttontarefas.setBorder(BorderFactory.createEtchedBorder());
    
         // Botão Configurações
        buttonConfig.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        buttonConfig.setBounds(440, 1, 40, 35);
        buttonConfig.setFocusable(false);
        buttonConfig.addActionListener(this);
        buttonConfig.setBorder(BorderFactory.createEtchedBorder());
        buttonConfig.setBackground(new Color(0xA0A0A0));
        buttonConfig.setForeground(Color.WHITE);
        buttonConfig.setHorizontalAlignment(SwingConstants.CENTER);
        buttonConfig.setVerticalAlignment(SwingConstants.BOTTOM);

        // Configurando o frame
        framePrograma.setTitle("Agenda Escolar");
        framePrograma.getContentPane().setBackground(new Color(255, 240, 153));
        framePrograma.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        framePrograma.setSize(500, 470);
        framePrograma.setLayout(null);
        framePrograma.setVisible(true);
        framePrograma.add(buttonAcessar);
        framePrograma.add(labelTitulo);
        framePrograma.add(buttonConfig);
        framePrograma.setLocationRelativeTo(null);
        framePrograma.setResizable(false);
        framePrograma.setIconImage(new ImageIcon("Icon.png").getImage());
        framePrograma.add(calendar);
        framePrograma.add(buttontarefas);

        // Add property change listener for the calendar
        calendar.addPropertyChangeListener("calendar", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if ("calendar".equals(e.getPropertyName())) {
                    Date dataSelecionada = calendar.getDate();
                    if (dataSelecionada != null) {
                        ultimaDataSelecionada = dataSelecionada; // Armazena a última data selecionada
                        buttonAcessar.setEnabled(true); // Habilita o botão "Acessar" quando uma data é selecionada
                    } else {
                        buttonAcessar.setEnabled(false); // Desabilita o botão "Acessar" quando nenhuma data é selecionada
                    }
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonAcessar) {
            verificarAnotacao(ultimaDataSelecionada, framelogin.getLoginId());
        } else if (e.getSource() == buttonConfig) {
            if (configframe == null) {
                configframe = new ConfigFrame(framelogin, this); 
            }
            configframe.setVisible(true); 
        } else if (e.getSource() == buttontarefas) {
            tarefasframe = new TarefasFrame(framelogin, this); 
            framePrograma.setVisible(false);
        }

        // Atualizar o calendário após qualquer ação
        configurarCalendarioSemAnotacoes();
    }

    public void setVisible(boolean visible) {
        framePrograma.setVisible(visible);
    }


    private void verificarAnotacao(Date data, int idUsuario) {
        try (Connection conn = DriverManager.getConnection(MainFrame.DB_URL, MainFrame.USER, MainFrame.PASS);
             PreparedStatement stmt = conn.prepareStatement("SELECT assunto FROM agenda WHERE data = ? AND login_id = ?")) {
            stmt.setDate(1, new java.sql.Date(data.getTime()));
            stmt.setInt(2, idUsuario);
            ResultSet rs = stmt.executeQuery();
            excluirDatasSemAssuntos(conn); // Excluir datas sem assuntos
            if (rs.next()) {
                String assunto = rs.getString("assunto");
                Calendar cal = Calendar.getInstance();
                cal.setTime(data);
                int year = cal.get(Calendar.YEAR);
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                if (year >= currentYear - 2 && year <= currentYear + 2) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    String dataFormatada = sdf.format(data); // Formatando a data
                    new AgendaFrame(this, idUsuario, data, dataFormatada, assunto);
                    framePrograma.setVisible(false);
                } else {
                    apagarAnotacaoForaDoIntervalo(data, idUsuario);
                }
            } else {
                criarNovaAnotacao(data, idUsuario);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(framePrograma, "Erro ao verificar anotação: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void apagarAnotacaoForaDoIntervalo(Date data, int idUsuario) {
        try (Connection conn = DriverManager.getConnection(MainFrame.DB_URL, MainFrame.USER, MainFrame.PASS);
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM agenda WHERE data = ? AND login_id = ?")) {
            stmt.setDate(1, new java.sql.Date(data.getTime()));
            stmt.setInt(2, idUsuario);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Anotação removida do banco de dados: " + data);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(framePrograma, "Erro ao apagar anotação fora do intervalo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirDatasSemAssuntos(Connection conn) throws SQLException {
        String deleteQuery = "DELETE FROM agenda WHERE assunto IS NULL OR assunto = ''";
        try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
            stmt.executeUpdate();
        }
    }

    private void criarNovaAnotacao(Date data, int idUsuario) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int anoAtual = cal.get(Calendar.YEAR);
        cal.setTime(data);
        int anoSelecionado = cal.get(Calendar.YEAR);
        if (Math.abs(anoSelecionado - anoAtual) > 2) {
            JOptionPane.showMessageDialog(framePrograma, "Você só pode adicionar anotações até dois anos antes ou depois do ano atual.");
        } else {
            try (Connection conn = DriverManager.getConnection(MainFrame.DB_URL, MainFrame.USER, MainFrame.PASS);
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO agenda (data, login_id, assunto) VALUES (?, ?, ?)")) {
                stmt.setDate(1, new java.sql.Date(data.getTime()));
                stmt.setInt(2, idUsuario);
                stmt.setString(3, ""); // Anotação inicial vazia
                stmt.executeUpdate();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String dataFormatada = sdf.format(data); // Formatando a data
                new AgendaFrame(this, idUsuario, data, dataFormatada, "");
                framePrograma.setVisible(false);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(framePrograma, "Erro ao criar nova anotação: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void configurarCalendarioSemAnotacoes() {
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.YEAR, -1);
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, 2);
        calendar.setMinSelectableDate(minDate.getTime());
        calendar.setMaxSelectableDate(maxDate.getTime());
        calendar.repaint();
    }
}
