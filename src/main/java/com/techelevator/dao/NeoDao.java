package com.techelevator.dao;

import com.techelevator.model.Neo;

import java.time.LocalDate;

public interface NeoDao {

    Neo saveNeo(Neo neo, int userId, LocalDate date);

    Neo getNeoById(int id);



}
