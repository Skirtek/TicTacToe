package sample.services;

import sample.models.Game;
import sample.repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositoryService implements IRepositoryService {

    @Override
    public int zapiszRozgrywke(Game rozgrywka) {
        try (Connection conn = Repository.getConnection()) {
            String SQL_INSERT = "INSERT INTO rozgrywka(zwyciezca, gracz_o, gracz_x, dataczas_rozgrywki) VALUES(?,?,?,?)";
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
    public List<Game> pobierzRozgrywki(Integer odWiersza, Integer liczbaWierszy) {
        try (Connection conn = Repository.getConnection()) {
            List<Game> lista = new ArrayList<>();

            String SQL_SELECT = "SELECT * FROM rozgrywka WHERE rozgrywka_id BETWEEN ? AND ?";
            PreparedStatement statement = conn.prepareStatement(SQL_SELECT);

            statement.setInt(1, odWiersza);
            statement.setInt(2, liczbaWierszy);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                lista.add(new Game(
                        rs.getInt("rozgrywka_id"),
                        rs.getString("zwyciezca"),
                        rs.getString("gracz_o"),
                        rs.getString("gracz_x"),
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
            return conn.createStatement().executeUpdate("TRUNCATE TABLE rozgrywka;");
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int getLastId() {
        try (Connection conn = Repository.getConnection()) {
            ResultSet result = conn.createStatement().executeQuery("SELECT MAX(rozgrywka_id) AS max FROM rozgrywka;");
            if(result.next()){
                return result.getInt("max");
            }

            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return 1;
        }
    }
}
