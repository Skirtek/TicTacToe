package sample.enums;

public enum OXElements {
    O("O"),
    X("X");

    String elementVisualisation;
    OXElements(String element) {
        elementVisualisation = element;
    }

    public String getVisualisation() {
        return elementVisualisation;
    }
}
