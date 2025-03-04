package UI;

import ConnReg.LoginController;
import User.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginWindow extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginWindow() {
        setTitle("Connexion");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centre la fenêtre à l’écran

        // Création des composants
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Se connecter");

        // Mise en page avec un panneau
        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Email :"));
        panel.add(emailField);
        panel.add(new JLabel("Mot de passe :"));
        panel.add(passwordField);
        panel.add(loginButton);

        add(panel);

        // Action du bouton de connexion
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                LoginController controller = new LoginController();
                try {
                    User user = controller.login(email, password);
                    if (user != null) {
                        // Connexion réussie, ouvrir la fenêtre principale
                        new MainWindow(user).setVisible(true);
                        dispose(); // Ferme la fenêtre de connexion
                    } else {
                        JOptionPane.showMessageDialog(LoginWindow.this, "Email ou mot de passe incorrect");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(LoginWindow.this, "Erreur de connexion à la base de données");
                }
            }
        });
    }
}