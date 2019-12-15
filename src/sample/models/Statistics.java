package sample.models;

public class Statistics implements Comparable<Statistics>  {
    private String firstName;
    private Long wins;
    private Long totalGames;
    private String effectiveness;

    public Statistics(String firstName, Long wins, Long totalGames) {
        this.firstName = firstName;
        this.wins = wins;
        this.totalGames = totalGames;
        this.effectiveness = ((double)wins / totalGames) * 100 + "%";
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

    public String getEffectiveness() {
        return effectiveness;
    }

    @Override
    public String toString() {
        return String.format("Imie: %s Zagrane gry: %d Wygrane: %d Skuteczność: %s", this.firstName, this.totalGames, this.wins, this.effectiveness);
    }

    @Override
    public int compareTo(Statistics compare) {
        return compare.getWins().compareTo(getWins());
    }
}
