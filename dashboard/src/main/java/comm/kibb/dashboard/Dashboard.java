package comm.kibb.dashboard;

import comm.kibb.dashboard.mood.HappinessChartData;
import comm.kibb.dashboard.mood.MoodChartData;
import comm.kibb.dashboard.mood.MoodsParser;
import comm.kibb.dashboard.mood.TweetMood;
import comm.kibb.dashboard.user.LeaderBoardData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Dashboard extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // all models created in advance
        LeaderBoardData leaderBoardData = new LeaderBoardData();
        MoodChartData moodChartData = new MoodChartData();
        HappinessChartData happinessChartData = new HappinessChartData();

        // wire up the models to the services they're getting the data from
        ClientEndpoint<String> userEndpoint = ClientEndpoint.createPassthroughEndpoint("ws://localhost:8083/users/");
        userEndpoint.addListener(leaderboardData);
        userEndpoint.connect();
        ClientEndpoint<TweetMood> moodEndpoint = new ClientEndpoint<>("ws://localhost:8082/moods/",

                MoodsParser::parse);
        moodEndpoint.addListener(moodChartData);
        moodEndpoint.addListener(happinessChartData);
        moodEndpoint.connect();


        // initialise the UI
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
        primaryStage.setTitle("Twitter Dashboard");
        Scene scene = new Scene(loader.load(), 1024, 1024);
        scene.getStylesheets().add("dashboard.css");

        // wire up the models to the controllers
        DashboardController dashboardController = loader.getController();
        dashboardController.getLeaderBoardController().setData(leaderBoardData);
        dashboardController.getMoodController().setData(moodChartData);
        dashboardController.getHappinessController().setData(happinessChartData);

        // let's go!
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args){
        launch(args);
    }
}
