package sample.enums;

public enum GameFields {
    top_left_button(0),
    top_center_button(1),
    top_right_button(2),

    middle_left_button(3),
    middle_center_button(4),
    middle_right_button(5),

    bottom_left_button(6),
    bottom_center_button(7),
    bottom_right_button(8);

    int position;
    GameFields(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
