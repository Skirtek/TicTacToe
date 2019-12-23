package sample.services;

import sample.models.Game;
import sample.models.Statistics;

import java.util.List;

public interface IRepositoryService {
    //TODO Wniosek - separacja repo od silnika gry
    int zapiszRozgrywke(Game rozgrywka);
    List<Game> pobierzRozgrywki(Integer firstId, Integer count);
    int usunRozgrywki();
    int getLastId();
    int updateGame(Game game);
    List<Statistics> getStatistics(List<Game> games);
}
