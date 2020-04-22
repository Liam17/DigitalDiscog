package databaseStuff;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import exceptions.NonexistantConnectionException;
import exceptions.SQLExceptionHandler;

public class AlbumsListTable {

    private Connection dbCon;
    private PreparedStatement preparedStatementSelectAlbumInfo;
    private PreparedStatement preparedStatementUpdateAlbumArtist;
    private PreparedStatement preparedStatementUpdateAlbumName;
    private PreparedStatement preparedStatementUpdateAlbumGenre;
    private PreparedStatement preparedStatementUpdateAlbumYearDropped;
    private PreparedStatement preparedStatementUpdateAlbumRating;
    private PreparedStatement preparedStatementInsertNewAlbum;
    private PreparedStatement artistBestAlbums;
    private PreparedStatement genreBestAlbums;
    private PreparedStatement yearBestAlbums;
    private PreparedStatement displayAlbumsForArtist;
    private PreparedStatement favouriteArtistsForYear;
    private PreparedStatement favouriteArtistsForDecade;
    private PreparedStatement favouriteArtistsForCustomYears;
    private PreparedStatement favouriteArtistsForGenre;
    private PreparedStatement favouriteAlbumsForDecade;
    private PreparedStatement favouriteAlbumsForCustomYears;

    public AlbumsListTable(Connection dbConnection) {
        dbCon = dbConnection;

        try {
            preparedStatementSelectAlbumInfo = dbCon.prepareStatement("Select * from AlbumsList where albumName = ?");
            displayAlbumsForArtist = dbCon.prepareStatement("Select * from AlbumsList where artistName = ? order by rating desc");
            favouriteArtistsForYear = dbCon.prepareStatement("SELECT artistName, round(avg(rating),2) as Average_Rating from AlbumsList where yearDropped = ? group by artistName order by round(avg(rating),2) desc ;");
            favouriteArtistsForDecade = dbCon.prepareStatement("SELECT artistName, round(avg(rating),2) as Average_Rating from AlbumsList where yearDropped >= ? and yearDropped < ?+10 group by artistName order by round(avg(rating),2) desc ;");
            favouriteArtistsForCustomYears = dbCon.prepareStatement("SELECT artistName, round(avg(rating),2) as Average_Rating from AlbumsList where yearDropped >= ? and yearDropped <= ? group by artistName order by round(avg(rating),2) desc ;");
            favouriteArtistsForGenre = dbCon.prepareStatement("SELECT artistName, round(avg(rating),2) as Average_Rating from AlbumsList where genre = ? group by artistName order by round(avg(rating),2) desc ;");
            preparedStatementUpdateAlbumArtist = dbCon.prepareStatement("UPDATE AlbumsList set artistName = ?  where albumName = ?");
            preparedStatementUpdateAlbumName = dbCon.prepareStatement("UPDATE AlbumsList set albumName = ?  where albumName = ?");
            preparedStatementUpdateAlbumGenre = dbCon.prepareStatement("UPDATE AlbumsList set genre = ?  where albumName = ?");
            preparedStatementUpdateAlbumYearDropped = dbCon.prepareStatement("UPDATE AlbumsList set yearDropped = ?  where albumName = ?");
            preparedStatementUpdateAlbumRating = dbCon.prepareStatement("UPDATE AlbumsList set rating = ?  where albumName = ?");
            preparedStatementInsertNewAlbum = dbCon.prepareStatement("Insert into AlbumsList (albumName, artistName, genre, yearDropped, rating) VALUES (?,?,?,?,?)");
            artistBestAlbums = dbCon.prepareStatement("Select * from AlbumsList where artistName = ? order by rating desc");
            genreBestAlbums = dbCon.prepareStatement("Select * from AlbumsList where genre = ? order by rating desc, artistName");
            yearBestAlbums = dbCon.prepareStatement("Select * from AlbumsList where yearDropped = ? order by rating desc, artistName");
            favouriteAlbumsForDecade = dbCon.prepareStatement("Select * from AlbumsList where yearDropped >= ? and yearDropped <?+10 order by rating desc, artistName;");
            favouriteAlbumsForCustomYears = dbCon.prepareStatement("Select * from AlbumsList where yearDropped >= ? and yearDropped <=? order by rating desc, artistName;");


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public AlbumsList retrieveAlbum(String albumName) throws NonexistantConnectionException {
        AlbumsList albumToReturn = null;

        if (dbCon == null)
            throw new NonexistantConnectionException();

        ResultSet resultSet = null;
        try {
            preparedStatementSelectAlbumInfo.setString(1, albumName);
            resultSet = preparedStatementSelectAlbumInfo.executeQuery();

            if (resultSet.next()){
                String retrievedAlbumName = resultSet.getString("albumName");
                String artistName = resultSet.getString("artistName");
                String genre = resultSet.getString("genre");
                int yearDropped = resultSet.getInt("yearDropped");
                int rating = resultSet.getInt("rating");

                albumToReturn = new AlbumsList(retrievedAlbumName, artistName, genre, yearDropped, rating);
            }

            resultSet.close();
        }catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }

        return albumToReturn;
    }

    public void displayAlbumsForArtist(String artistName) throws NonexistantConnectionException {
        if (dbCon == null)
            throw new NonexistantConnectionException();

        ResultSet resultSet = null;
        try {
            displayAlbumsForArtist.setString(1, artistName);
            resultSet = displayAlbumsForArtist.executeQuery();

            while (resultSet.next()){
                String retrievedAlbumName = resultSet.getString("albumName");
                String genre = resultSet.getString("genre");
                int yearDropped = resultSet.getInt("yearDropped");
                int rating = resultSet.getInt("rating");

                AlbumsList albumToReturn = new AlbumsList(retrievedAlbumName, artistName, genre, yearDropped, rating);
                albumToReturn.printAlbumsList();
            }

            resultSet.close();
        }catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }
    }

    public boolean exists(String albumName) throws NonexistantConnectionException{
        boolean exists = false;

        if (dbCon == null)
            throw new NonexistantConnectionException();

        ResultSet resultSet = null;
        try {
            preparedStatementSelectAlbumInfo.setString(1, albumName);
            resultSet = preparedStatementSelectAlbumInfo.executeQuery();

            if (resultSet.next()){
                String retrievedName = resultSet.getString("albumName");
                if (retrievedName.equals(albumName)){
                    exists = true;
                }
            }

            resultSet.close();
        }catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }

        return exists;
    }

    public void updateAlbumArtist (String albumName, String artistName) throws NonexistantConnectionException {
        if (dbCon == null)
            throw new NonexistantConnectionException();

        try {
            preparedStatementUpdateAlbumArtist.setString(1, artistName);
            preparedStatementUpdateAlbumArtist.setString(2, albumName);
            preparedStatementUpdateAlbumArtist.executeUpdate();
        }catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }

    }

    public void updateAlbumName (String newName, String oldName) throws NonexistantConnectionException {
        if (dbCon == null)
            throw new NonexistantConnectionException();

        try {
            preparedStatementUpdateAlbumName.setString(1, newName);
            preparedStatementUpdateAlbumName.setString(2, oldName);
            preparedStatementUpdateAlbumName.executeUpdate();
        }catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }

    }

    public void updateAlbumGenre (String genre, String albumName) throws NonexistantConnectionException {
        if (dbCon == null)
            throw new NonexistantConnectionException();

        try {
            preparedStatementUpdateAlbumGenre.setString(1, genre);
            preparedStatementUpdateAlbumGenre.setString(2, albumName);
            preparedStatementUpdateAlbumGenre.executeUpdate();
        }catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }

    }

    public void updateAlbumYearDropped (int year, String albumName) throws NonexistantConnectionException {
        if (dbCon == null)
            throw new NonexistantConnectionException();

        try {
            preparedStatementUpdateAlbumYearDropped.setInt(1, year);
            preparedStatementUpdateAlbumYearDropped.setString(2, albumName);
            preparedStatementUpdateAlbumYearDropped.executeUpdate();
        }catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }

    }

    public void updateAlbumRating (int rating, String albumName) throws NonexistantConnectionException {
        if (dbCon == null)
            throw new NonexistantConnectionException();

        try {
            preparedStatementUpdateAlbumRating.setInt(1, rating);
            preparedStatementUpdateAlbumRating.setString(2, albumName);
            preparedStatementUpdateAlbumRating.executeUpdate();
        }catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }

    }

    public void addNewAlbum(String albumName, String artistName, String genre, int yearDropped, int rating) throws NonexistantConnectionException {
        try {
            preparedStatementInsertNewAlbum.setString(1, albumName);
            preparedStatementInsertNewAlbum.setString(2, artistName);
            preparedStatementInsertNewAlbum.setString(3, genre);
            preparedStatementInsertNewAlbum.setInt(4, yearDropped);
            preparedStatementInsertNewAlbum.setFloat(5, rating);
            preparedStatementInsertNewAlbum.execute();
        } catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }
    }

    public void artistSearch(String artistName) throws NonexistantConnectionException {
        AlbumsList albumsSearched;
        if (dbCon == null)
            throw new NonexistantConnectionException();

        ResultSet resultSet = null;
        try {
            artistBestAlbums.setString(1, artistName);
            resultSet = artistBestAlbums.executeQuery();
            int i = 1;
            while (resultSet.next()){
                String retrievedAlbumName = resultSet.getString("albumName");
                String genre = resultSet.getString("genre");
                int yearDropped = resultSet.getInt("yearDropped");
                int rating = resultSet.getInt("rating");

                albumsSearched = new AlbumsList(retrievedAlbumName, artistName, genre, yearDropped, rating);
                System.out.print(i + ": ");
                albumsSearched.printAlbumsList();
                i++;
            }

            resultSet.close();
        }catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }
    }

    public void genreSearch(String genre) throws NonexistantConnectionException {
        AlbumsList albumsSearched;
        if (dbCon == null)
            throw new NonexistantConnectionException();

        ResultSet resultSet = null;
        try {
            genreBestAlbums.setString(1, genre);
            resultSet = genreBestAlbums.executeQuery();
            int i = 1;
            while (resultSet.next()){
                String retrievedAlbumName = resultSet.getString("albumName");
                String artistName = resultSet.getString("artistName");
                int yearDropped = resultSet.getInt("yearDropped");
                int rating = resultSet.getInt("rating");

                albumsSearched = new AlbumsList(retrievedAlbumName, artistName, genre, yearDropped, rating);
                System.out.print(i + ": ");
                albumsSearched.printAlbumsList();
                i++;
            }

            resultSet.close();
        }catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }
    }

    public void yearSearch(int yearDropped, int amount) throws NonexistantConnectionException {
        AlbumsList albumsSearched;
        if (dbCon == null)
            throw new NonexistantConnectionException();

        ResultSet resultSet = null;
        try {
            yearBestAlbums.setInt(1, yearDropped);
            resultSet = yearBestAlbums.executeQuery();

            for(int i=1; resultSet.next() && i<= amount; i++ ){
                String retrievedAlbumName = resultSet.getString("albumName");
                String artistName = resultSet.getString("artistName");
                String genre = resultSet.getString("genre");
                int rating = resultSet.getInt("rating");

                albumsSearched = new AlbumsList(retrievedAlbumName, artistName, genre, yearDropped, rating);
                System.out.print(i + ": ");
                albumsSearched.printAlbumsList();
            }

            resultSet.close();
        }catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }
    }

    public void findFavouriteAlbumsEver(AlbumsListTable albumsListTable, int amount) throws NonexistantConnectionException, SQLException {
        if (dbCon == null)
            throw new NonexistantConnectionException();
        ResultSet albumsNRatings = null;
        try{
            Statement bestArtistsEver = dbCon.createStatement();
            albumsNRatings = bestArtistsEver.executeQuery("SELECT * from AlbumsList order by rating desc ;");

            for(int i=1; albumsNRatings.next() && i <= amount; i++){
                AlbumsList albumsList = new AlbumsList(albumsNRatings.getString("albumName"), albumsNRatings.getString("artistName"),
                        albumsNRatings.getString("genre"), albumsNRatings.getInt("yearDropped"), albumsNRatings.getInt("rating"));
                System.out.print(i + ": ");
                albumsList.printAlbumsList();
            }
            albumsNRatings.close();
        } catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }
    }

    public void findFavouriteAlbumsForDecade(AlbumsListTable albumsListTable, int amount, int decade) throws NonexistantConnectionException, SQLException {
        if (dbCon == null)
            throw new NonexistantConnectionException();
        ResultSet albumsNRatings = null;
        try{
            favouriteAlbumsForDecade.setInt(1, decade);
            favouriteAlbumsForDecade.setInt(2, decade);
            albumsNRatings = favouriteAlbumsForDecade.executeQuery();
            for(int i=1; albumsNRatings.next() && i <= amount; i++){
                AlbumsList albumsList = new AlbumsList(albumsNRatings.getString("albumName"), albumsNRatings.getString("artistName"),
                        albumsNRatings.getString("genre"), albumsNRatings.getInt("yearDropped"), albumsNRatings.getInt("rating"));
                System.out.print(i + ": ");

                albumsList.printAlbumsList();
            }
            albumsNRatings.close();

        } catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }
    }

    public void findFavouriteAlbumsForCustomYears(AlbumsListTable albumsListTable, int amount, int startingYear, int endingYear) throws NonexistantConnectionException, SQLException {
        if (dbCon == null)
            throw new NonexistantConnectionException();
        ResultSet albumsNRatings = null;
        try{
            favouriteAlbumsForCustomYears.setInt(1, startingYear);
            favouriteAlbumsForCustomYears.setInt(2, endingYear);
            albumsNRatings = favouriteAlbumsForCustomYears.executeQuery();

            for(int i=1; albumsNRatings.next() && i <= amount; i++){
                AlbumsList albumsList = new AlbumsList(albumsNRatings.getString("albumName"), albumsNRatings.getString("artistName"),
                        albumsNRatings.getString("genre"), albumsNRatings.getInt("yearDropped"), albumsNRatings.getInt("rating"));
                System.out.print(i + ": ");
                albumsList.printAlbumsList();
            }
            albumsNRatings.close();

        } catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }
    }

    public void findFavouriteArtistsEver(AlbumsListTable albumsListTable, int amount) throws NonexistantConnectionException, SQLException {
        if (dbCon == null)
            throw new NonexistantConnectionException();
        ResultSet artistsNRatings = null;
        try{
            Statement bestArtistsEver = dbCon.createStatement();
            artistsNRatings = bestArtistsEver.executeQuery("SELECT artistName, round(avg(rating),2) as Average_Rating from AlbumsList group by artistName order by round(avg(rating),2) desc ;");

            for(int i=1; artistsNRatings.next() && i <= amount; i++){
                System.out.println(i + ": " + artistsNRatings.getString("artistName") + ", Average Rating: " + artistsNRatings.getFloat(2));
                System.out.println();
            }
            artistsNRatings.close();

        } catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }
    }

    public void findFavouriteArtistsForYear(AlbumsListTable albumsListTable, int amount, int year) throws NonexistantConnectionException, SQLException {
        if (dbCon == null)
            throw new NonexistantConnectionException();
        ResultSet artistsNRatings = null;
        try{
            favouriteArtistsForYear.setInt(1,year);
            artistsNRatings = favouriteArtistsForYear.executeQuery();

            for(int i=1; artistsNRatings.next() && i <= amount; i++){
                System.out.println(i + ": " + artistsNRatings.getString("artistName") + ", Average Rating: " + artistsNRatings.getFloat(2));
                System.out.println();
            }
            artistsNRatings.close();

        } catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }
    }

    public void findFavouriteArtistsForDecade(AlbumsListTable albumsListTable, int amount, int decade) throws NonexistantConnectionException, SQLException {
        if (dbCon == null)
            throw new NonexistantConnectionException();
        ResultSet artistsNRatings = null;
        try{
            favouriteArtistsForDecade.setInt(1,decade);
            favouriteArtistsForDecade.setInt(2,decade);
            artistsNRatings = favouriteArtistsForDecade.executeQuery();

            for(int i=1; artistsNRatings.next() && i <= amount; i++){
                System.out.println(i + ": " + artistsNRatings.getString("artistName") + ", Average Rating: " + artistsNRatings.getFloat(2));
                System.out.println();
            }
            artistsNRatings.close();

        } catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }
    }

    public void findFavouriteArtistsForCustomYears(AlbumsListTable albumsListTable, int amount, int startingYear, int endingYear) throws NonexistantConnectionException, SQLException {
        if (dbCon == null)
            throw new NonexistantConnectionException();
        ResultSet artistsNRatings = null;
        try{
            favouriteArtistsForCustomYears.setInt(1,startingYear);
            favouriteArtistsForCustomYears.setInt(2,endingYear);
            artistsNRatings = favouriteArtistsForCustomYears.executeQuery();

            for(int i=1; artistsNRatings.next() && i <= amount; i++){
                System.out.println(i + ": " + artistsNRatings.getString("artistName") + ", Average Rating: " + artistsNRatings.getFloat(2));
                System.out.println();
            }
            artistsNRatings.close();

        } catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }
    }

    public void findFavouriteArtistsForGenres(AlbumsListTable albumsListTable, int amount, String genre) throws NonexistantConnectionException, SQLException {
        if (dbCon == null)
            throw new NonexistantConnectionException();
        ResultSet artistsNRatings = null;
        try{
            favouriteArtistsForGenre.setString(1,genre);
            artistsNRatings = favouriteArtistsForGenre.executeQuery();

            for(int i=1; artistsNRatings.next() && i <= amount; i++){
                System.out.println(i + ": " + artistsNRatings.getString("artistName") + ", Average Rating: " + artistsNRatings.getFloat(2));
                System.out.println();
            }
            artistsNRatings.close();

        } catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }
    }

    public void displayAllAlbums() throws NonexistantConnectionException {
        if (dbCon == null)
            throw new NonexistantConnectionException();
        ResultSet albumsList = null;
        try{
            Statement errthang = dbCon.createStatement();
            albumsList = errthang.executeQuery("Select * from AlbumsList group by artistName, genre order by rating desc");

            while(albumsList.next()){
                String albumName = albumsList.getString("albumName");
                String artistName = albumsList.getString("artistName");
                String genre = albumsList.getString("genre");
                int yearDropped = albumsList.getInt("yearDropped");
                int rating = albumsList.getInt("rating");
                AlbumsList foundList = new AlbumsList(albumName,artistName,genre,yearDropped,rating);
                foundList.printAlbumsList();
            }

        } catch (SQLException se) {
            SQLExceptionHandler.handleException(se);
        }
    }

}
