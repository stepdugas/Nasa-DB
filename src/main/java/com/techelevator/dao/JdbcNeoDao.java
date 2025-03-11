package com.techelevator.dao;

import com.techelevator.model.Neo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDate;

public class JdbcNeoDao implements NeoDao{

    JdbcTemplate jdbcTemplate;
    public JdbcNeoDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public Neo saveNeo(Neo neo, int userId, LocalDate date) {
        String sql = "Insert into neo (nasa_id, user_id, neo_date, neo_name, hazardous, min_diameter, max_diameter) values (?, ?, ?, ?, ?, ?, ?) returning db_id";
        Integer dbId = jdbcTemplate.queryForObject(sql, int.class,
                neo.getId(), userId, date, neo.getName(),
                neo.isPotentiallyHazardousAsteroid(),
                neo.getEstimatedDiameter().getMiles().getEstimatedDiameterMin(),
                neo.getEstimatedDiameter().getMiles().getGetEstimatedDiameterMax());
        Neo newNeo = getNeoById(dbId);
        return newNeo;
    }

    @Override
    public Neo getNeoById(int id) {
        String sql = "Select db_id, nasa_id, neo_name from neo where db_id=?";

        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
        Neo neo = new Neo();
        if (result.next() ) {
            neo.setId(result.getString("nasa_id"));
            neo.setName(result.getString("neo_name"));
        }
        return neo;
    }
}
