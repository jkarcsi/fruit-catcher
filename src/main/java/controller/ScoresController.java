//package controller;
//
//import javafx.fxml.FXML;
//import javafx.scene.control.Label;
//import javafx.scene.control.ListView;
//import model.UserDAO;
//import model.Score;
//import utils.UserSession;
//
//import java.sql.SQLException;
//import java.util.List;
//
//public class ScoresController {
//
//    @FXML
//    private ListView<String> scoresListView;
//
//    private String username = UserSession.getInstance().getUsername();
//
//    public void setUsername(String username) {
//        this.username = username;
//        loadScores();
//    }
//
//    private void loadScores() {
//        try {
//            UserDAO userDAO = new UserDAO();
//            List<Score> scores = userDAO.getScores(username);
//            for (Score score : scores) {
//                scoresListView.getItems().add(score.toString());
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//}
