package databaseStuff;

import java.sql.*;

import exceptions.NonexistantConnectionException;
import exceptions.SQLExceptionHandler;

import extras.*;

public class ArtistTable {

    private Connection dbCon;
    private PreparedStatement preparedStatementSelectArtistInfo;
    private PreparedStatement preparedStatementUpdateArtistName;
    private PreparedStatement preparedStatementUpdateArtistBirthday;
    private PreparedStatement preparedStatementInsertNewArtist;

    public ArtistTable(Connection dbConnection) {
        dbCon = dbConnection;

        try {
            preparedStatementSelectArtistInfo = dbCon.prepareStatement("Select * from Artists where artistName = ?");
            preparedStatementUpdateArtistName = dbCon.prepareStatement("Update Artists set artistName = ? where artistName = ?");
            preparedStatementUpdateArtistBirthday = dbCon.prepareStatement("Update Artists set birthday = ? where artistName = ?");
            preparedStatementInsertNewArtist = dbCon.prepareStatement("Insert Into Artists (artistName, birthday) VALUES (?,?)");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Artist retrieveArtist(String artistName) throws NonexistantConnectionException {
        Artist artistToReturn = null;

        if (dbCon == null)
            throw new NonexistantConnectionException();

        ResultSet resultSet = null;
        try {
            preparedStatementSelectArtistInfo.setString(1, artistName);
            resultSet = preparedStatementSelectArtistInfo.executeQuery();

            if (resultSet.next()){
                String retrievedArtistName = resultSet.getString("artistName");
                String birthdayString = resultSet.getString("birthday");
                Dates birthday = new Dates(birthdayString);
                artistToReturn = new Artist(retrievedArtistName, birthday);
            }

            resultSet.close();
        }catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }

        return artistToReturn;
    }

    public boolean exists(String artistName) throws NonexistantConnectionException {
        boolean exists = false;

        if (dbCon == null)
            throw new NonexistantConnectionException();

        ResultSet resultSet = null;
        try {
            preparedStatementSelectArtistInfo.setString(1, artistName);
            resultSet = preparedStatementSelectArtistInfo.executeQuery();

            if (resultSet.next()){
                String retrievedName = resultSet.getString("artistName");
                if (retrievedName.equals(artistName)){
                    exists = true;
                }
            }

            resultSet.close();
        }catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }
        return exists;
    }

    public void updateArtistName (String newName, String oldName) throws NonexistantConnectionException {
        if (dbCon == null)
            throw new NonexistantConnectionException();

        try {
            preparedStatementUpdateArtistName.setString(1, newName);
            preparedStatementUpdateArtistName.setString(2, oldName);
            preparedStatementUpdateArtistName.executeUpdate();
        }catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }

    }

    public void updateArtistBirthday (String Name, Dates birthday) throws NonexistantConnectionException {
        if (dbCon == null)
            throw new NonexistantConnectionException();

        try {
            preparedStatementUpdateArtistBirthday.setString(1, birthday.toString());
            preparedStatementUpdateArtistBirthday.setString(2, Name);
            preparedStatementUpdateArtistBirthday.executeUpdate();
        }catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }

    }

    public void addNewArtist(String name, Dates birthday) throws NonexistantConnectionException {
        try {
            preparedStatementInsertNewArtist.setString(1, name);
            preparedStatementInsertNewArtist.setString(2, birthday.toString());
            preparedStatementInsertNewArtist.execute();
        } catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }
    }

    public void displayAllArtists() throws NonexistantConnectionException {
        if (dbCon == null)
            throw new NonexistantConnectionException();
        ResultSet artistList = null;
        try{
            Statement errthang = dbCon.createStatement();
            artistList = errthang.executeQuery("Select * from Artists order by artistName");

            while(artistList.next()){
                String artistName = artistList.getString("artistName");
                String birthdayString = artistList.getString("birthday");
                Dates birthday = new Dates(birthdayString);
                Artist foundArtist = new Artist(artistName, birthday);
                foundArtist.printArtist();
            }

        } catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }
    }

}
