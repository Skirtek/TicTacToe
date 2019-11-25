package sample.services;

import com.sun.deploy.util.StringUtils;
import javafx.animation.RotateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import sample.enums.GameFields;
import sample.enums.OXElements;
import sample.interfaces.IOXGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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

    //region Buttons actions
    @FXML
    public void OnStartClick() {
        initialize();

        player_one = player_one_field.getText();
        player_two = player_two_field.getText();

        if (isNullOrWhitespace(player_one) || isNullOrWhitespace(player_two)) {
            info_label.setTextFill(Color.RED);
            info_label.setText("Proszę wybrać nazwę gracza");
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
    //endregion

    //region IOXGame Implementation
    @Override
    public void initialize() {
        FillButtonsList();
        FillCombinations();
        ResetButtons();
        ChangeButtonsResponsiveness(true);
        ResetPositions();
    }

    @Override
    public void setField(Button button) {
        if (isNullOrWhitespace(button.getText())) {

            button.setText(Turn.getVisualisation());
            AddMove(Turn, GameFields.valueOf(button.getId()).getPosition());

            if (WasWinnerMove(Turn)) {
                ChangeButtonsResponsiveness(true);
                turn_label.setVisible(false);
                info_label.setText(String.format("Wygrał gracz: %s", getWinner() == OXElements.O ? player_two : player_one));
                AnimateTiles();
                return;
            }

            if (IsBoardFull()) {
                info_label.setText("Remis");
                turn_label.setVisible(false);
                ChangeButtonsResponsiveness(true);
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

    private boolean isNullOrWhitespace(String s) {
        return s == null || StringUtils.trimWhitespace(s).length() == 0;
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
