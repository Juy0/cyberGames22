package ConnReg;

import DAO.UserDAO;
import User.User;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.SQLException;

public class LoginController {
    private UserDAO userDAO = new UserDAO();

    public User login(String email, String inputPassword) throws SQLException {
        User user = userDAO.getByEmail(email);
        if (user != null && BCrypt.checkpw(inputPassword, user.getPassword())) {
            return user; // Connexion réussie
        } else {
            return null; // Échec de la connexion
        }
    }
}

//public class LoginController {
//    private UserDAO userDAO; // Une classe pour accéder aux données des utilisateurs
//
//    public LoginController() {
//        this.userDAO = new UserDAO(); // Initialisation de l'accès à la base de données
//    }
//
//
//    public User getByEmail(String email) {
//        return null;
//    }
//    public User login(String email, String inputPassword) throws SQLException {
//        // Récupérer l'utilisateur depuis la base de données avec son email
//        User user = userDAO.getByEmail(email);
//
//        // Vérifier si l'utilisateur existe et si le mot de passe est correct
//        if (user != null && BCrypt.checkpw(inputPassword, user.getPassword())) {
//            // Connexion réussie
//            return user; // Retourne l'utilisateur connecté
//        } else {
//            // Échec de la connexion
//            return null; // Indique que la connexion a échoué
//        }
//    }
//}