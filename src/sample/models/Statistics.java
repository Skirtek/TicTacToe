package sample.models;

public class Statistics implements Comparable<Statistics>  {
    private String firstName;
    private Long wins;
    private Long totalGames;
    private Double effectiveness;
    private String effectivenessPercentage;

    public Statistics(String firstName, Long wins, Long totalGames) {
        this.firstName = firstName;
        this.wins = wins;
        this.totalGames = totalGames;
        this.effectiveness = ((double)wins / totalGames) * 100;
        this.effectivenessPercentage = this.effectiveness + "%";
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Long getWins() {
        return wins;
    }

    public void setWins(Long wins) {
        this.wins = wins;
    }

    public Long getTotalGames() {
        return totalGames;
    }

    public void setTotalGames(Long totalGames) {
        this.totalGames = totalGames;
    }

    public Double getEffectiveness() {
        return effectiveness;
    }

    public String getEffectivenessPercentage() {
        return effectivenessPercentage;
    }

    @Override
    public String toString() {
        return String.format("Imie: %s Zagrane gry: %d Wygrane: %d Skuteczność: %s", this.firstName, this.totalGames, this.wins, this.effectiveness);
    }

    @Override
    public int compareTo(Statistics comparableObject) {
        return comparableObject.getEffectiveness().compareTo(getEffectiveness());
    }
}
