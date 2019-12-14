package sample.services;

import sample.models.Game;

import java.util.List;

public interface IRepositoryService {
    int zapiszRozgrywke(Game rozgrywka);
    List<Game> pobierzRozgrywki(Integer odWiersza, Integer liczbaWierszy);
    int usunRozgrywki();
    int getLastId();
}
