package sample.services;

import javafx.animation.RotateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sample.enums.GameFields;
import sample.enums.OXElements;
import sample.interfaces.IOXGame;
import sample.models.Game;
import sample.models.Statistics;
import sample.utilities.Utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OXGame implements IOXGame {

    private static final String String_Empty = "";

    private OXElements Turn;
    private ArrayList<Integer> X_Positions = new ArrayList<>();
    private ArrayList<Integer> O_Positions = new ArrayList<>();
    private ArrayList<Button> buttons = new ArrayList<>();

    private ArrayList<List<Integer>> combinations = new ArrayList<>();
    private List<Integer> winnerCombination = new ArrayList<>();

    private String player_one;
    private String player_two;

    //region Buttons
    public Button reset_history;

    public Button start_button;

    public Button top_left_button;
    public Button top_center_button;
    public Button top_right_button;

    public Button middle_left_button;
    public Button middle_center_button;
    public Button middle_right_button;

    public Button bottom_left_button;
    public Button bottom_center_button;
    public Button bottom_right_button;
    //endregion

    //region TextFields
    public TextField player_two_field;
    public TextField player_one_field;
    //endregion

    //region Labels
    public Label turn_label;
    public Label info_label;
    //endregion

    //region Tables properties
    public TableView<Game> score_table;
    public TableColumn<Game, Integer> idColumn;
    public TableColumn<Game, OXElements> winner;
    public TableColumn<Game, String> playerO;
    public TableColumn<Game, String> playerX;
    public TableColumn<Game, String> dateTimeColumn;

    public TableView<Statistics> statistics_table;
    public TableColumn<Statistics, String> firstName;
    public TableColumn<Statistics, Long> totalGames;
    public TableColumn<Statistics, Long> wins;
    public TableColumn<Statistics, String> effectiveness;
    //endregion

    private RepositoryService repositoryInstance;
    private ObservableList<Game> games;
    private ObservableList<Statistics> statistics;

    private static final Logger logger = LoggerFactory.getLogger(OXGame.class);

    //region Buttons actions
    @FXML
    public void OnStartClick() {
        initialize();

        player_one = player_one_field.getText();
        player_two = player_two_field.getText();

        if (!Utils.isNickNameValid(player_one) || !Utils.isNickNameValid(player_two)) {
            info_label.setTextFill(Color.RED);
            info_label.setText("Nazwa gracza jest niepoprawna");
            return;
        }

        ChangeButtonsResponsiveness(false);
        info_label.setTextFill(Color.BLACK);
        info_label.setText(String.format("Trwa pojedynek: %s vs %s", player_one_field.getText(), player_two_field.getText()));
        Random random = new Random();
        setTurn(random.nextInt(100) % 2 == 0 ? OXElements.O : OXElements.X);
        turn_label.setVisible(true);
        player_one_field.setText(String_Empty);
        player_two_field.setText(String_Empty);
    }

    @FXML
    public void OnFieldClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        setField(clickedButton);
    }

    @FXML
    public void onResetClick() {
        int result = repositoryInstance.usunRozgrywki();

        if(result < 0){
            logger.error("Nie udało się wyczyścić rozgrywek!");
            return;
        }

        info_label.setText("Rozgrywki zostały usunięte");
        games.clear();
        statistics.clear();
    }

    @FXML
    public void onColumnValueChanged(TableColumn.CellEditEvent<Game, String> editEvent){
        Game editedGame = score_table.getSelectionModel().getSelectedItem();

        if(!Utils.isNickNameValid(editEvent.getNewValue())){
            info_label.setText("Nowa nazwa użytkownika jest nieprawidłowa");
            score_table.refresh();
            return;
        }

        if(editEvent.getTableColumn().getId().equals("playerX")){
            editedGame.setGraczX(editEvent.getNewValue());
        }
        else {
            editedGame.setGraczO(editEvent.getNewValue());
        }

        int result = repositoryInstance.updateGame(editedGame);

        if(result < 1){
            logger.error(String.format("Rozgrywka o ID %d nie została zaktualizowana!", editedGame.getRozgrywkaId()));
            return;
        }

        statistics.clear();
        statistics.addAll(repositoryInstance.getStatistics(games));
    }
    //endregion

    //region IOXGame Implementation
    @Override
    public void initialize() {
        FillButtonsList();
        FillCombinations();
        ResetButtons();
        ChangeButtonsResponsiveness(true);
        ResetPositions();

        if (repositoryInstance == null) {
            repositoryInstance = new RepositoryService();
        }

        idColumn.setCellValueFactory(new PropertyValueFactory<>("rozgrywkaId"));
        winner.setCellValueFactory(new PropertyValueFactory<>("zwyciezca"));
        playerO.setCellValueFactory(new PropertyValueFactory<>("graczO"));
        playerX.setCellValueFactory(new PropertyValueFactory<>("graczX"));
        dateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("formattedDateTime"));
        games = FXCollections.observableArrayList();
        score_table.setItems(games);

        firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        wins.setCellValueFactory(new PropertyValueFactory<>("wins"));
        totalGames.setCellValueFactory(new PropertyValueFactory<>("totalGames"));
        effectiveness.setCellValueFactory(new PropertyValueFactory<>("effectiveness"));

        statistics = FXCollections.observableArrayList();
        statistics_table.setItems(statistics);

        score_table.setEditable(true);
        playerX.setCellFactory(TextFieldTableCell.forTableColumn());
        playerO.setCellFactory(TextFieldTableCell.forTableColumn());

        ExecutorService threadWorker = Executors.newFixedThreadPool(1);
        threadWorker.execute(() -> {
            List<Game> rows = repositoryInstance.pobierzRozgrywki(1, repositoryInstance.getLastId());

            if (rows != null && rows.size() != 0) {
                games.addAll(rows);
                statistics.addAll(repositoryInstance.getStatistics(rows));
            }
        });
    }

    @Override
    public void setField(Button button) {
        if (Utils.isNullOrWhitespace(button.getText())) {

            button.setText(Turn.getVisualisation());
            AddMove(Turn, GameFields.valueOf(button.getId()).getPosition());

            if (WasWinnerMove(Turn)) {
                ChangeButtonsResponsiveness(true);
                turn_label.setVisible(false);
                info_label.setText(String.format("Wygrał gracz: %s", getWinner() == OXElements.O ? player_two : player_one));
                AnimateTiles();
                Game gameToSave = new Game(getWinner().getVisualisation(), player_one, player_two, LocalDateTime.now());
                int result = repositoryInstance.zapiszRozgrywke(gameToSave);

                if(result < 1){
                    logger.error(String.format("Rozgrywka między %s a %s o godzinie %s nie została zapisana!", gameToSave.getGraczO(), gameToSave.getGraczX(), gameToSave.getFormattedDateTime()));
                }

                gameToSave.setRozgrywkaId(repositoryInstance.getLastId());
                games.add(gameToSave);

                statistics.clear();
                statistics.addAll(repositoryInstance.getStatistics(games));
                return;
            }

            if (IsBoardFull()) {
                info_label.setText("Remis");
                turn_label.setVisible(false);
                ChangeButtonsResponsiveness(true);
                Game gameToSave = new Game(OXElements.Tie.getVisualisation(), player_one, player_two, LocalDateTime.now());
                int result = repositoryInstance.zapiszRozgrywke(gameToSave);

                if(result < 1){
                    logger.error(String.format("Rozgrywka między %s a %s o godzinie %s nie została zapisana!", gameToSave.getGraczO(), gameToSave.getGraczX(), gameToSave.getFormattedDateTime()));
                }

                gameToSave.setRozgrywkaId(repositoryInstance.getLastId());
                games.add(gameToSave);

                statistics.clear();
                statistics.addAll(repositoryInstance.getStatistics(games));
                return;
            }

            setTurn();
        }
    }

    @Override
    public void setTurn(OXElements turn) {
        Turn = turn;
        turn_label.setText(String.format("Ruch gracza %s", Turn.getVisualisation()));
    }

    @Override
    public void setTurn() {
        if (Turn == OXElements.O) {
            Turn = OXElements.X;
        } else {
            Turn = OXElements.O;
        }

        turn_label.setText(String.format("Ruch gracza %s", Turn.getVisualisation()));
    }

    @Override
    public OXElements getWinner() {
        return Turn;
    }

    @Override
    public List<Integer> getWinnerIndexes() {
        return winnerCombination;
    }
    //endregion

    //region Private methods
    private void ResetButtons() {
        for (Button button : buttons) {
            button.setText(String_Empty);
        }
    }

    private void ChangeButtonsResponsiveness(boolean disableState) {
        for (Button button : buttons) {
            button.setDisable(disableState);
        }
    }

    private void FillButtonsList() {
        buttons.clear();

        buttons.add(top_left_button);
        buttons.add(top_center_button);
        buttons.add(top_right_button);

        buttons.add(middle_left_button);
        buttons.add(middle_center_button);
        buttons.add(middle_right_button);

        buttons.add(bottom_left_button);
        buttons.add(bottom_center_button);
        buttons.add(bottom_right_button);
    }

    private boolean IsBoardFull() {
        return X_Positions.size() + O_Positions.size() == 9;
    }

    private void AddMove(OXElements elements, int position) {
        if (elements == OXElements.O) {
            O_Positions.add(position);
            return;
        }

        X_Positions.add(position);
    }

    private boolean WasWinnerMove(OXElements elements) {
        if (elements == OXElements.O) {
            for (List<Integer> combination : combinations) {
                if (O_Positions.containsAll(combination)) {
                    winnerCombination = combination;
                    return true;
                }
            }

            return false;
        }

        for (List<Integer> combination : combinations) {
            if (X_Positions.containsAll(combination)) {
                winnerCombination = combination;
                return true;
            }
        }

        return false;
    }

    private void ResetPositions() {
        O_Positions.clear();
        X_Positions.clear();
    }

    private void FillCombinations() {
        combinations.clear();

        combinations.add(Arrays.asList(0, 1, 2));
        combinations.add(Arrays.asList(3, 4, 5));
        combinations.add(Arrays.asList(6, 7, 8));
        combinations.add(Arrays.asList(0, 3, 6));
        combinations.add(Arrays.asList(1, 4, 7));
        combinations.add(Arrays.asList(2, 5, 8));
        combinations.add(Arrays.asList(0, 4, 8));
        combinations.add(Arrays.asList(2, 4, 6));
    }

    private RotateTransition GetTransition(Button buttonToAnimate) {
        RotateTransition rotation = new RotateTransition(Duration.seconds(0.5), buttonToAnimate);
        rotation.setCycleCount(1);
        rotation.setByAngle(360);

        return rotation;
    }

    private void AnimateTiles() {
        for (int position : getWinnerIndexes()) {
            Button buttonToAnimate = buttons.get(position);
            GetTransition(buttonToAnimate).play();
        }
    }
    //endregion
}