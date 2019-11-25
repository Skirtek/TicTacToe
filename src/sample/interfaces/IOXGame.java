package sample.interfaces;

import javafx.scene.control.Button;
import sample.enums.OXElements;

import java.util.List;

public interface IOXGame {
    void initialize();
    void setField(Button button);
    void setTurn(OXElements turn);
    void setTurn();
    OXElements getWinner();
    List<Integer> getWinnerIndexes();
}
