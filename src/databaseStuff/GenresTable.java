package databaseStuff;

import java.sql.*;

import exceptions.NonexistantConnectionException;
import exceptions.SQLExceptionHandler;
import extras.Dates;

public class GenresTable {

    private Connection dbCon;
    private PreparedStatement preparedStatementUpdateGenre;
    private PreparedStatement preparedStatementInsertNewGenre;
    private PreparedStatement preparedStatementDoesItExist;

    public GenresTable(Connection dbConn){
        dbCon=dbConn;

        try {
            preparedStatementUpdateGenre = dbCon.prepareStatement("Update Genres set genre = ? where genre = ?");
            preparedStatementInsertNewGenre = dbCon.prepareStatement("Insert Into Genres (genre) VALUES (?)");
            preparedStatementDoesItExist = dbCon.prepareStatement("Select * from Genres where genre = ?");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public boolean exists(String genre) throws NonexistantConnectionException {
        boolean exists = false;

        if (dbCon == null)
            throw new NonexistantConnectionException();

        ResultSet resultSet = null;
        try {
            preparedStatementDoesItExist.setString(1, genre);
            resultSet = preparedStatementDoesItExist.executeQuery();

            if (resultSet.next()){
                exists = true;
            }

            resultSet.close();
        }catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }

        return exists;
    }

    public void updateGenre(String oldName, String newName) throws NonexistantConnectionException {
        if (dbCon == null)
            throw new NonexistantConnectionException();

        try {
            preparedStatementUpdateGenre.setString(1, newName);
            preparedStatementUpdateGenre.setString(2, oldName);
            preparedStatementUpdateGenre.executeUpdate();
        }catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }
    }

    public void addNewGenre(String name) throws NonexistantConnectionException {
        try {
            preparedStatementInsertNewGenre.setString(1, name);
            preparedStatementInsertNewGenre.execute();
        } catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }
    }

    public void displayAllGenres() throws NonexistantConnectionException {
        if (dbCon == null)
            throw new NonexistantConnectionException();
        ResultSet genresList = null;
        try{
            Statement errthang = dbCon.createStatement();
            genresList = errthang.executeQuery("Select * from Genres order by genre");

            while(genresList.next()){
                System.out.println(genresList.getString("genre"));
            }

        } catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }
    }


}
