import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MovieAnalytics {
    private Stage stage;

    public MovieAnalytics(Stage stage) {
        this.stage = stage;
    }

    public void initializeComponents() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        TableView<ShowtimeMovieEntry> table = new TableView<>();
        ObservableList<ShowtimeMovieEntry> data = FXCollections.observableArrayList();

        TableColumn<ShowtimeMovieEntry, String> titleCol = new TableColumn<>("Movie Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<ShowtimeMovieEntry, String> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("room"));

        TableColumn<ShowtimeMovieEntry, String> dateCol = new TableColumn<>("Show Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<ShowtimeMovieEntry, String> timeCol = new TableColumn<>("Show Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn<ShowtimeMovieEntry, Integer> rankCol = new TableColumn<>("popular");
        rankCol.setCellValueFactory(new PropertyValueFactory<>("rank"));

        TableColumn<ShowtimeMovieEntry, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button plusBtn = new Button("+1");
            private final Button minusBtn = new Button("-1");
            private final HBox pane = new HBox(5, plusBtn, minusBtn);

            {
                plusBtn.setOnAction(e -> {
                    ShowtimeMovieEntry entry = getTableView().getItems().get(getIndex());
                    updatePeakRanking(entry, 1);
                });

                minusBtn.setOnAction(e -> {
                    ShowtimeMovieEntry entry = getTableView().getItems().get(getIndex());
                    updatePeakRanking(entry, -1);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });

        table.getColumns().addAll(titleCol, roomCol, dateCol, timeCol, rankCol, actionCol);

        try (Connection con = DBUtils.establishConnection()) {
            String query = "SELECT s.id, m.title, s.room_number, s.show_date, s.show_time, s.peak_rank " +
                    "FROM showtimes s JOIN movies m ON s.movie_id = m.id ORDER BY s.peak_rank DESC";
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int showtimeId = rs.getInt("id");
                String title = rs.getString("title");
                String room = String.valueOf(rs.getInt("room_number"));
                String date = rs.getDate("show_date").toString();
                String time = rs.getTime("show_time").toString();
                int rank = rs.getInt("peak_rank");
                data.add(new ShowtimeMovieEntry(showtimeId, title, room, date, time, rank));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        table.setItems(data);

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> new Dashboard(stage).initializeComponents());

        layout.getChildren().addAll(table, backBtn);

        Scene scene = new Scene(layout, 750, 400);
        stage.setScene(scene);
        stage.setTitle("Movie Analytics");
        stage.show();
    }

    private void updatePeakRanking(ShowtimeMovieEntry entry, int delta) {
        try (Connection con = DBUtils.establishConnection()) {
            String updateQuery = "UPDATE showtimes SET peak_rank = IFNULL(peak_rank, 0) + ? WHERE id = ?";
            PreparedStatement stmt = con.prepareStatement(updateQuery);
            stmt.setInt(1, delta);
            stmt.setInt(2, entry.getShowtimeId());
            stmt.executeUpdate();
            initializeComponents();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class ShowtimeMovieEntry {
        private final int showtimeId;
        private final SimpleStringProperty title;
        private final SimpleStringProperty room;
        private final SimpleStringProperty date;
        private final SimpleStringProperty time;
        private final SimpleIntegerProperty rank;

        public ShowtimeMovieEntry(int showtimeId, String title, String room, String date, String time, int rank) {
            this.showtimeId = showtimeId;
            this.title = new SimpleStringProperty(title);
            this.room = new SimpleStringProperty(room);
            this.date = new SimpleStringProperty(date);
            this.time = new SimpleStringProperty(time);
            this.rank = new SimpleIntegerProperty(rank);
        }

        public int getShowtimeId() { return showtimeId; }
        public String getTitle() { return title.get(); }
        public String getRoom() { return room.get(); }
        public String getDate() { return date.get(); }
        public String getTime() { return time.get(); }
        public int getRank() { return rank.get(); }
    }
}