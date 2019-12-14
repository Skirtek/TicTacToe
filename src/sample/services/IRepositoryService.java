package sample.services;

import sample.models.Game;

import java.util.List;

public interface IRepositoryService {
    int zapiszRozgrywke(Game rozgrywka);
    List<Game> pobierzRozgrywki(Integer firstId, Integer count);
    int usunRozgrywki();
    int getLastId();
    int updateGame(Game game);
}
