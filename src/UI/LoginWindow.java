package UI;

import Session.UserSession;
import User.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import DAO.UserDAO;
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
        panel.add(new JLabel("Nom :"));
        panel.add(emailField);
        panel.add(new JLabel("Mot de passe :"));
        panel.add(passwordField);
        panel.add(loginButton);

        add(panel);

        // Action du bouton de connexion
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = emailField.getText();
                String password = new String(passwordField.getPassword());
                UserDAO userDAO = new UserDAO();
                if (userDAO.authenticate(name, password)) {
                    JOptionPane.showMessageDialog(LoginWindow.this, "Votre mot de passe est valide");
                    LoginWindow.this.dispose();
                    new MainWindow();
                }
            }
        });
    }
}