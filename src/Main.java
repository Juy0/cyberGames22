import UI.LoginWindow;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Lance l'interface utilisateur sur le thread d'événements Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginWindow().setVisible(true); // Ouvre la fenêtre de connexion
            }
        });
    }
}