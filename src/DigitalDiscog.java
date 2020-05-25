

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.*;

import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import configuration.DBUserDatabaseInfoConstants;
import configuration.DBConstants;
import exceptions.NonexistantConnectionException;
import exceptions.SQLExceptionHandler;

import databaseStuff.*;
import extras.*;
import users.DatabaseInfo;
import users.DatabaseInfoTable;
import users.UserInfo;
import users.UserInfoTable;

import javax.xml.transform.Result;

import java.util.Scanner;

public class DigitalDiscog {

    public static void main(String[] args) {

        Connection connUserDatabaseInfo = null; // Connection to user database
        Connection conn = null; // Connection to music database

        InputStreamReader r=new InputStreamReader(System.in);
        BufferedReader br=new BufferedReader(r);
        Scanner scan = new Scanner(System.in);

        boolean playAgain = true;
        boolean runItBack = false;

        UserInfo userInfo = null;
        DatabaseInfo databaseInfo = null;

        try {

            // Establish connection to user database
            connUserDatabaseInfo = DriverManager.getConnection(DBUserDatabaseInfoConstants.DBName);

            UserInfoTable userInfoTable = new UserInfoTable(connUserDatabaseInfo);
            DatabaseInfoTable databaseInfoTable = new DatabaseInfoTable(connUserDatabaseInfo);

            // Log in
            boolean login = false;

            do{
                System.out.println("Identify Yourself\nUsername:");
                String username = br.readLine();
                System.out.println("Password:");
                String password = br.readLine();

                userInfo = userInfoTable.login(username, password);

                if (userInfo == null){
                    System.out.println("Try logging in again?\n1 - Yes\n2 - No");
                    int loginAgain = scan.nextInt();
                    if (loginAgain == 1){
                        login = true;
                    }
                    else {
                        playAgain = false;
                    }
                }

            }while (login);

            // Establish what database user wants to connect to
            System.out.println("Connect to most recently used database?\n1 - Yes\n2 - No");
            int whatToConnect = scan.nextInt();

            if(whatToConnect == 1){
                // get most recent database? display which one that is first
                databaseInfo = databaseInfoTable.chooseMostRecent(userInfo);
                System.out.println("Version: " + databaseInfo.getVersion());

            }
            else if(whatToConnect == 2){
                databaseInfoTable.displayDatabaseInfo(userInfo.getUserID());
                System.out.println("What database version would you like to connect to?");
                String version = br.readLine();

                databaseInfo = databaseInfoTable.getDatabaseInfo(userInfo.getUserID(), version);

                databaseInfoTable.setDateToToday(databaseInfo);
            }
            else {
                System.out.println("Error");
                playAgain = false;
            }

            // Establish database connection
            conn = DriverManager.getConnection(databaseInfo.getLocation());

            while(playAgain) {

                System.out.println("\nWhatchu waant?:\n1 - Add new album\n2 - Add new artist\n3 - Add new genre\n" +
                        "4 - Update database info\n5 - Find your favourite albums\n6 - Find your favourite artists");
                int input = scan.nextInt();

                /*TODO
                - Go back if you fucked up
                - Make it into a website
                - Only submit things if there's no errors
                - What if there's more than 1 artist on an album?
                */

                AlbumsListTable albumsListTable = new AlbumsListTable(conn);
                AlbumsList albumsList = null;
                ArtistTable artistTable = new ArtistTable(conn);
                Artist artist = null;
                GenresTable genresTable = new GenresTable(conn);

                if (input == 1) { // Add new album
                    do {
                        playAgain = false;
                        runItBack = false;
                        try {
                            // Take user input
                            System.out.println("Name of the album?");
                            String albumName = br.readLine();
                            // Check if album is in database
                            if (albumsListTable.exists(albumName)){
                                System.out.println("Hold your horses, that album already exists in the database!");
                            } else {

                                System.out.println("By who?");
                                String artistName = br.readLine();
                                // Check if artist or genre are already in database and add them if not
                                if(!artistTable.exists(artistName)){
                                    System.out.println("Looks like you're adding a new artist! What's their birthday? (DD/MM/YYYY)");
                                    String birthdayString = br.readLine();
                                    Dates birthday = new Dates(birthdayString);
                                    artistTable.addNewArtist(artistName,birthday);
                                }

                                System.out.println("\nCurrent Genres in the Database:");
                                genresTable.displayAllGenres();
                                System.out.println();

                                System.out.println("What genre(s) is this album? (If more than 1, separate them by a (,) and no spaces)");
                                String genre = br.readLine();

                                String[] genreDecomposed = genre.split(",");
                                for (int i = 0; i<genreDecomposed.length; i++){
                                    // Check if genre exists is in database
                                    if(!genresTable.exists(genreDecomposed[i])){
                                        genresTable.addNewGenre(genreDecomposed[i]);
                                    }
                                }

                                System.out.println("What year did it drop?");
                                int yearDropped = scan.nextInt();
                                System.out.println("How hard is it (Outta 10)?");
                                int rating = scan.nextInt();

                                // Add album to database
                                albumsListTable.addNewAlbum(albumName, artistName, genre, yearDropped, rating);

                                System.out.println("\nSubmitted, New Album:");
                                albumsList = albumsListTable.retrieveAlbum(albumName);
                                albumsList.printAlbumsList();
                            }

                            // More user options, what to do now?
                            System.out.println("\nCongrats! You got more choices now:\n1 - Add another album?\n2 - Go back to the main menu?\n3 - Quit :(");
                            int answer = scan.nextInt();

                            if (answer == 1) {
                                runItBack = true;
                            }
                            if (answer == 2) {
                                playAgain = true;
                            }
                            if (answer == 3) {
                                System.exit(0);
                            }
                        } catch (Exception e) {
                            System.out.println("Error, run it back");
                            runItBack = true;
                        }
                    } while (runItBack);
                }// Main menu input statement 1 ends here - New Album

                else if (input == 2) { // Add new artist
                    do {
                        playAgain = false;
                        runItBack = false;
                        try {
                            // Take user input
                            System.out.println("Name of the artist?");
                            String artistName = br.readLine();

                            if(!artistTable.exists(artistName)){

                                System.out.println("What's their birthday? (DD/MM/YYYY)");
                                String birthdayString = br.readLine();
                                Dates birthday = new Dates(birthdayString);

                                // Add artist to database
                                artistTable.addNewArtist(artistName, birthday);

                                System.out.println("\nSubmitted, New Artist:");
                                artist = artistTable.retrieveArtist(artistName);
                                artist.printArtist();
                            }
                            else{
                                System.out.println("\nArtist exists already");
                            }

                            // More user options, what to do now?
                            System.out.println("Congrats! You got more choices now:\n1 - Add another artist?\n2 - Go back to the main menu?\n3 - Quit :(");
                            int answer = scan.nextInt();

                            if (answer == 1) {
                                runItBack = true;
                            }
                            if (answer == 2) {
                                playAgain = true;
                            }
                            if (answer == 3) {
                                System.exit(0);
                            }
                        } catch (Exception e) {
                            System.out.println("Error, run it back");
                            runItBack = true;                     }
                    } while (runItBack);
                }// Main menu input statement 2 ends here - New Artist

                else if (input == 3) { // Add new genre
                    do {
                        playAgain = false;
                        runItBack = false;
                        try {
                            // Take user input
                            System.out.println("Name of the genre?");
                            String genre = br.readLine();

                            if(!genresTable.exists(genre)){
                                // Add genre to database
                                genresTable.addNewGenre(genre);

                                System.out.println("\nSubmitted, New Genre:\n" + genre);
                            }
                            else{
                                System.out.println("\nGenre exists already");
                            }

                            // More user options, what to do now?
                            System.out.println("\nCongrats! You got more choices now:\n1 - Add another genre?\n2 - Go back to the main menu?\n3 - Quit :(");
                            int answer = scan.nextInt();

                            if (answer == 1) {
                                runItBack = true;
                            }
                            if (answer == 2) {
                                playAgain = true;
                            }
                            if (answer == 3) {
                                System.exit(0);
                            }
                        } catch (Exception e) {
                            System.out.println("Error, run it back");
                            runItBack = true;                       }
                    } while (runItBack);
                }// Main menu input statement 3 ends here - New Genre

                else if (input == 4) { // Update album info
                    do {
                        playAgain = false;
                        runItBack = false;

                        try {
                            System.out.println("What do you want to update:\n1 - Album Info\n2 - Artist Info\n3 - Genre Info");
                            int whatToUpdate = scan.nextInt();
                            if (whatToUpdate == 1){ // Update album info
                                // Take user input
                                System.out.println("What album's info do you want to tweak?");
                                String albumName = br.readLine();
                                if (!albumsListTable.exists(albumName)){
                                    System.out.println("Sorry you can't tweak an album that's not in the database");
                                }else{
                                    System.out.println("What do you want to change?\n1 - Change album name\n2 - Change artist name\n" +
                                            "3 - Change genre\n4 - Change year it dropped\n5 - Change rating");
                                    int change = scan.nextInt();
                                    if (change == 1){
                                        // Change album name
                                        System.out.println("What's the album's new name?");
                                        String newName = br.readLine();
                                        albumsListTable.updateAlbumName(newName, albumName);

                                        albumName = newName;
                                        System.out.println("\nSubmitted, Album Updated:");
                                        albumsList = albumsListTable.retrieveAlbum(albumName);
                                        albumsList.printAlbumsList();
                                    }
                                    else if (change == 2){
                                        // Change album artist
                                        System.out.println("What's the artist's new name?");
                                        String newName = br.readLine();
                                        albumsListTable.updateAlbumArtist(albumName, newName);

                                        System.out.println("\nSubmitted, Album Updated:");
                                        albumsList = albumsListTable.retrieveAlbum(albumName);
                                        albumsList.printAlbumsList();
                                    }
                                    else if (change == 3){
                                        System.out.println("More options:\n1 - Change album's genre?\n2 - Add another genre?");
                                        int answer = scan.nextInt();

                                        if (answer == 1){
                                            // Change album genre
                                            System.out.println("What's the album's new genre?");
                                            String newName = br.readLine();
                                            albumsListTable.updateAlbumGenre(newName, albumName);

                                            System.out.println("\nSubmitted, Album Updated:");
                                            albumsList = albumsListTable.retrieveAlbum(albumName);
                                            albumsList.printAlbumsList();
                                        }
                                        if (answer == 2){
                                            System.out.println("What genre(s) would you like to add? (If more than 1, separate them by a (,) and no spaces)");
                                            String genre = br.readLine();

                                            String[] genreDecomposed = genre.split(",");
                                            for (int i = 0; i<genreDecomposed.length; i++){
                                                // Check if genre exists is in database
                                                if(!genresTable.exists(genreDecomposed[i])){
                                                    genresTable.addNewGenre(genreDecomposed[i]);
                                                }
                                            }
                                            albumsListTable.updateAlbumGenre(albumsListTable.retrieveAlbum(albumName).appendToGenres(genre), albumName);

                                            System.out.println("\nSubmitted, Album Updated:");
                                            albumsList = albumsListTable.retrieveAlbum(albumName);
                                            albumsList.printAlbumsList();
                                        }
                                        else {
                                            System.out.println("Wrong answer");
                                        }

                                    }
                                    else if (change == 4){
                                        // Change album year dropped
                                        System.out.println("What's the album's new release date?");
                                        int newYear = scan.nextInt();
                                        albumsListTable.updateAlbumYearDropped(newYear, albumName);

                                        System.out.println("\nSubmitted, Album Updated:");
                                        albumsList = albumsListTable.retrieveAlbum(albumName);
                                        albumsList.printAlbumsList();
                                    }
                                    else if (change == 5){
                                        // Change album rating
                                        System.out.println("How you feeling about the album now (Outta 10)?");
                                        int newRating = scan.nextInt();
                                        albumsListTable.updateAlbumRating(newRating, albumName);

                                        System.out.println("\nSubmitted, Album Updated:");
                                        albumsList = albumsListTable.retrieveAlbum(albumName);
                                        albumsList.printAlbumsList();
                                    }
                                    else{
                                        System.out.println("Wrong answer");
                                    }
                                }
                            }
                            else if (whatToUpdate == 2){ // Update artist info
                                System.out.println("What artist info do you want to change?\n1 - Name\n2 - Birthday");
                                int artistUpdate = scan.nextInt();

                                if (artistUpdate == 1){
                                    System.out.println("Here are all the artists:\n");
                                    artistTable.displayAllArtists();
                                    System.out.println("Write the current name of the artist whose name you would you like to change");
                                    String oldName = br.readLine();
                                    System.out.println("What's the new name gonna be?");
                                    String newName = br.readLine();
                                    artistTable.updateArtistName(newName,oldName);

                                    System.out.println("Submitted, New Artist Info:");
                                    artistTable.retrieveArtist(newName).printArtist();
                                }
                                else if (artistUpdate == 2){
                                    System.out.println("Here are all the artists:");
                                    artistTable.displayAllArtists();
                                    System.out.println("Write the name of the artist whose birthday would you like to change");
                                    String artistName = br.readLine();
                                    System.out.println("What did their birthday change to? (DD/MM/YYYY)");
                                    String newBirthday = br.readLine();
                                    artistTable.updateArtistBirthday(artistName,new Dates(newBirthday));

                                    System.out.println("Submitted, New Artist Info:");
                                    artistTable.retrieveArtist(artistName).printArtist();
                                }
                                else{
                                    System.out.println("Wrong answer");
                                }
                            }
                            else if (whatToUpdate == 3){ // Update genre info
                                System.out.println("Here are the current genres in database:\n");
                                genresTable.displayAllGenres();

                                System.out.println("\nWrite the current name of the genre whose name you would like to change");
                                String oldName = br.readLine();

                                System.out.println("What's its new name?");
                                String newName = br.readLine();

                                genresTable.updateGenre(oldName, newName);

                                System.out.println("Submitted, New Genres List:");
                                genresTable.displayAllGenres();
                            }
                            else{
                                System.out.println("Wrong answer");
                            }

                            // More user options, what to do now?
                            System.out.println("\nCongrats! You got more choices now:\n1 - Tweak something else?\n2 - Go back to the main menu?\n3 - Quit :(");
                            int answer = scan.nextInt();

                            if (answer == 1) {
                                runItBack = true;
                            }
                            if (answer == 2) {
                                playAgain = true;
                            }
                            if (answer == 3) {
                                System.exit(0);
                            }
                        } catch (Exception e) {
                            System.out.println("Error, run it back\n");
                            runItBack = true;                        }
                    } while (runItBack);
                }// Main menu input statement 4 ends here - Update album info

                else if (input == 5) { // Find favourite albums
                    do {
                        playAgain = false;
                        runItBack = false;
                        try {
                            // Take user input
                            System.out.println("Find your favourite albums based on what?\n1 - Artist\n2 - Years\n3 - Genre");
                            int search = scan.nextInt();

                            if (search == 1){
                                System.out.println("Display all artists in your database to search from?\n1 - Yes\n2 - No");
                                int answer = scan.nextInt();
                                if(answer == 1){
                                    artistTable.displayAllArtists();
                                    System.out.println();
                                }
                                System.out.println("What Artist would you like to search for?");
                                String artistName = br.readLine();
                                System.out.println("Favourite albums by " + artistName + ":\n");
                                albumsListTable.artistSearch(artistName);
                            }
                            else if (search == 2){
                                System.out.println("Refine your search, Favourite Albums:\n1 - Ever?\n2 - A Decade?\n3 - A Year?\n4 - Custom year interval");
                                int yearsSearch = scan.nextInt();

                                if (yearsSearch == 1){
                                    // Search for top albums ever
                                    System.out.println("Up to how many albums would you like to display?");
                                    int amount = scan.nextInt();
                                    albumsListTable.findFavouriteAlbumsEver(albumsListTable,amount);
                                }
                                else if (yearsSearch == 2){
                                    // Search for top albums for a decade
                                    System.out.println("What decade would you like to search for? (Ex. 1990 for 90's)");
                                    int decade = scan.nextInt();
                                    System.out.println("Up to how many albums would you like to display?");
                                    int amount = scan.nextInt();
                                    albumsListTable.findFavouriteAlbumsForDecade(albumsListTable,amount,decade);
                                }
                                else if (yearsSearch == 3){
                                    // Search for top artists for a year
                                    System.out.println("What year would you like to search for?");
                                    int year = scan.nextInt();
                                    System.out.println("Up to how many albums would you like to display?");
                                    int amount = scan.nextInt();
                                    albumsListTable.yearSearch(year,amount);
                                }
                                else if (yearsSearch == 4){
                                    // Search for top artists for a custom interval
                                    System.out.println("What range of years would you search for? (The range will be inclusive)\nStarting year:");
                                    int yearStart = scan.nextInt();
                                    System.out.println("Ending year:");
                                    int yearEnd = scan.nextInt();
                                    System.out.println("Up to how many albums would you like to display?");
                                    int amount = scan.nextInt();
                                    albumsListTable.findFavouriteAlbumsForCustomYears(albumsListTable,amount,yearStart,yearEnd);
                                }
                                else{
                                    System.out.println("You goofed.\n");
                                }

                            }
                            else if (search == 3){
                                System.out.println("Display all genres in your database to search from?\n1 - Yes\n2 - No");
                                int answer = scan.nextInt();
                                if(answer == 1){
                                    genresTable.displayAllGenres();
                                    System.out.println();
                                }
                                System.out.println("What genre would you like to search for?");
                                String genre = br.readLine();
                                albumsListTable.genreSearch(genre);
                            }
                            else {
                                System.out.println("You goofed");
                            }

                            // More user options, what to do now?
                            System.out.println("\nCongrats! You got more choices now:\n1 - Search again?\n2 - Go back to the main menu?\n3 - Quit :(");
                            int answer = scan.nextInt();

                            if (answer == 1) {
                                runItBack = true;
                            }
                            if (answer == 2) {
                                playAgain = true;
                            }
                            if (answer == 3) {
                                System.exit(0);
                            }
                        } catch (Exception e) {
                            System.out.println("Error, run it back");
                        }
                    } while (runItBack);
                }// Main menu input statement 5 ends here - Find favourite albums

                else if (input == 6) { // Find favourite artists
                    do {
                        playAgain = false;
                        runItBack = false;
                        try {
                            // Take user input
                            System.out.println("Find your favourite artist based on what?\n1 - Years\n2 - Genre");
                            int search = scan.nextInt();

                            if (search == 1){ // Top artists years
                                System.out.println("Refine your search, Favourite Artists:\n1 - Ever?\n2 - A Decade?\n3 - A Year?\n4 - Custom year interval");
                                int yearsSearch = scan.nextInt();

                                if (yearsSearch == 1){
                                    // Search for top artists ever
                                    System.out.println("Up to how many artists would you like to display?");
                                    int amount = scan.nextInt();
                                    albumsListTable.findFavouriteArtistsEver(albumsListTable,amount);
                                }
                                else if (yearsSearch == 2){
                                    // Search for top artists for a decade
                                    System.out.println("What decade would you like to search for? (Ex. 1990 for 90's)");
                                    int decade = scan.nextInt();
                                    System.out.println("Up to how many artists would you like to display?");
                                    int amount = scan.nextInt();
                                    albumsListTable.findFavouriteArtistsForDecade(albumsListTable,amount,decade);
                                }
                                else if (yearsSearch == 3){
                                    // Search for top artists for a year
                                    System.out.println("What year would you like to search for?");
                                    int year = scan.nextInt();
                                    System.out.println("Up to how many artists would you like to display?");
                                    int amount = scan.nextInt();
                                    albumsListTable.findFavouriteArtistsForYear(albumsListTable,amount,year);
                                }
                                else if (yearsSearch == 4){
                                    // Search for top artists for a custom interval
                                    System.out.println("What range of years would you search for? (The range will be inclusive)\nStarting year:");
                                    int yearStart = scan.nextInt();
                                    System.out.println("Ending year:");
                                    int yearEnd = scan.nextInt();
                                    System.out.println("Up to how many artists would you like to display?");
                                    int amount = scan.nextInt();
                                    albumsListTable.findFavouriteArtistsForCustomYears(albumsListTable,amount,yearStart,yearEnd);
                                }
                            }
                            else if (search == 2){ // Top artists for a genre
                                System.out.println("Display all genres in your database to search from?\n1 - Yes\n2 - No");
                                int answer = scan.nextInt();
                                if(answer == 1){
                                    genresTable.displayAllGenres();
                                    System.out.println();
                                }
                                System.out.println("What genre would you like to search for?");
                                String genre = br.readLine();
                                System.out.println("Up to how many artists would you like to display?");
                                int amount = scan.nextInt();
                                albumsListTable.findFavouriteArtistsForGenres(albumsListTable,amount,genre);
                            }
                            else {
                                System.out.println("You goofed\n");
                            }

                            // More user options, what to do now?
                            System.out.println("Congrats! You got more choices now:\n1 - Search again?\n2 - Go back to the main menu?\n3 - Quit :(");
                            int answer = scan.nextInt();

                            if (answer == 1) {
                                runItBack = true;
                            }
                            if (answer == 2) {
                                playAgain = true;
                            }
                            if (answer == 3) {
                                System.exit(0);
                            }
                        } catch (Exception e) {
                            System.out.println("Error, run it back");
                            runItBack = true;
                        }
                    } while (runItBack);
                }// Main menu input statement 5 ends here - Find favourite artists

                else{
                    System.out.println("You messed up");
                    playAgain = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}







