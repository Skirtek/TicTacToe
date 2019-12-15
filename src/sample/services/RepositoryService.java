package sample.services;

import sample.models.Game;
import sample.models.Statistics;
import sample.repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RepositoryService implements IRepositoryService {

    @Override
    public int zapiszRozgrywke(Game rozgrywka) {
        try (Connection conn = Repository.getConnection()) {
            final String SQL_INSERT = "INSERT INTO rozgrywka(zwyciezca, gracz_o, gracz_x, dataczas_rozgrywki) VALUES(?,?,?,?)";
            PreparedStatement statement = conn.prepareStatement(SQL_INSERT);
            statement.setString(1, rozgrywka.getZwyciezca());
            statement.setString(2, rozgrywka.getGraczO());
            statement.setString(3, rozgrywka.getGraczX());
            statement.setTimestamp(4, Timestamp.valueOf(rozgrywka.getDataczasRozgrywki()));

            return statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public List<Game> pobierzRozgrywki(Integer firstId, Integer count) {
        try (Connection conn = Repository.getConnection()) {
            List<Game> lista = new ArrayList<>();

            final String SQL_SELECT = "SELECT * FROM rozgrywka WHERE rozgrywka_id BETWEEN ? AND ?";
            PreparedStatement statement = conn.prepareStatement(SQL_SELECT);

            statement.setInt(1, firstId);
            statement.setInt(2, count);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                lista.add(new Game(
                        rs.getInt("rozgrywka_id"),
                        rs.getString("zwyciezca"),
                        rs.getString("gracz_x"),
                        rs.getString("gracz_o"),
                        rs.getTimestamp("dataczas_rozgrywki").toLocalDateTime()));
            }

            return lista;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public int usunRozgrywki() {
        try (Connection conn = Repository.getConnection()) {
            return conn.createStatement().executeUpdate("TRUNCATE TABLE rozgrywka RESTART IDENTITY AND COMMIT NO CHECK;");
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int getLastId() {
        try (Connection conn = Repository.getConnection()) {
            ResultSet result = conn.createStatement().executeQuery("SELECT MAX(rozgrywka_id) AS max FROM rozgrywka;");
            if (result.next()) {
                return result.getInt("max");
            }

            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return 1;
        }
    }

    @Override
    public int updateGame(Game game) {
        try (Connection conn = Repository.getConnection()) {
            final String SQL_UPDATE = "UPDATE rozgrywka SET gracz_o = ?, gracz_x = ? WHERE rozgrywka_id = ?";
            PreparedStatement statement = conn.prepareStatement(SQL_UPDATE);

            statement.setString(1, game.getGraczO());
            statement.setString(2, game.getGraczX());
            statement.setInt(3, game.getRozgrywkaId());
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public List<Statistics> getStatistics(List<Game> games) {
        List<Statistics> result = new ArrayList<>();

        try {
            Map<String, Long> gamesAsX = games.stream().collect(Collectors.groupingBy(Game::getGraczX, Collectors.counting()));
            Map<String, Long> gamesAsO = games.stream().collect(Collectors.groupingBy(Game::getGraczO, Collectors.counting()));

            gamesAsO.forEach(
                    (key, value) -> gamesAsX.merge(key, value, Long::sum)
            );

            Map<String, Long> winsAsX = games.stream().filter(x -> x.getZwyciezca().equals("X")).collect(Collectors.groupingBy(Game::getGraczX, Collectors.counting()));
            Map<String, Long> winsAsO = games.stream().filter(x -> x.getZwyciezca().equals("O")).collect(Collectors.groupingBy(Game::getGraczO, Collectors.counting()));

            winsAsO.forEach(
                    (key, value) -> winsAsX.merge(key, value, Long::sum)
            );

            for (Map.Entry<String, Long> entry1 : gamesAsX.entrySet()) {
                String firstName = entry1.getKey();
                Long gamesPlayed = entry1.getValue();
                Long wins = winsAsX.get(firstName);

                result.add(new Statistics(firstName, wins == null ? 0 : wins, gamesPlayed));
            }

            Collections.sort(result);
            return result;
        }
        catch (Exception ex){
            return result;
        }
    }
}
