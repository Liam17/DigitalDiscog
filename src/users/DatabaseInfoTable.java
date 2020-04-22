package users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import exceptions.NonexistantConnectionException;
import exceptions.SQLExceptionHandler;
import extras.Dates;

import java.util.HashMap;

import javax.xml.transform.Result;

public class DatabaseInfoTable {

    private Connection dbCon;

    private PreparedStatement addNewDatabaseInfo;
    private PreparedStatement updateLocation;
    private PreparedStatement getDatabaseInfo;
    private PreparedStatement displayUsersDatabases;
    private PreparedStatement setDateToday;

    private HashMap<Integer , DatabaseInfo> storeInfo;

    public DatabaseInfoTable(Connection connection){
        dbCon = connection;
        try{
            addNewDatabaseInfo = dbCon.prepareStatement("Insert into DatabaseInfo (UserID, Version, DBMS_Location, DateModified) VALUES (?,?,?,?);");
            updateLocation = dbCon.prepareStatement("Update DatabaseInfo set DBMS_Location = ? where UserID = ? AND Version = ?;");
            getDatabaseInfo = dbCon.prepareStatement("Select * from DatabaseInfo where UserID = ? AND Version = ?;");
            displayUsersDatabases = dbCon.prepareStatement("Select * from DatabaseInfo where UserID = ?;");
            setDateToday = dbCon.prepareStatement("Update DatabaseInfo set DateModified = ? where UserID = ? AND Version = ?;");

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void addNewDatabaseInfo(DatabaseInfo databaseInfo) throws NonexistantConnectionException {
        if (dbCon == null){
            throw new NonexistantConnectionException();
        }

        try{

            addNewDatabaseInfo.setInt(1, databaseInfo.getUserID());
            addNewDatabaseInfo.setString(2, databaseInfo.getVersion());
            addNewDatabaseInfo.setString(3, databaseInfo.getLocation());
            addNewDatabaseInfo.setString(4, databaseInfo.getDateModified());

            addNewDatabaseInfo.execute();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void updateLocation (DatabaseInfo databaseInfo, String newLocation) throws NonexistantConnectionException {
        if (dbCon == null){
            throw new NonexistantConnectionException();
        }

        try{
            updateLocation.setString(1, newLocation);
            updateLocation.setInt(2, databaseInfo.getUserID());
            updateLocation.setString(3, databaseInfo.getVersion());

            updateLocation.execute();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public DatabaseInfo getDatabaseInfo(int userID, String version) throws NonexistantConnectionException {
        DatabaseInfo infoToReturn = null;
        if (dbCon == null){
            throw new NonexistantConnectionException();
        }

        ResultSet resultSet = null;

        try{
            getDatabaseInfo.setInt(1, userID);
            getDatabaseInfo.setString(2, version);
            resultSet = getDatabaseInfo.executeQuery();

            if (resultSet.next()){
                String retrievedLocation = resultSet.getString("DBMS_Location");
                String retrievedDateModified = resultSet.getString("DateModified");

                infoToReturn = new DatabaseInfo(userID, version, retrievedLocation, retrievedDateModified);
            }
            resultSet.close();

        }catch (SQLException e){
            e.printStackTrace();
        }

        return infoToReturn;
    }

    public void displayDatabaseInfo(int userID) throws NonexistantConnectionException {
        DatabaseInfo infoToReturn = null;
        if (dbCon == null){
            throw new NonexistantConnectionException();
        }

        ResultSet resultSet = null;

        try{
            displayUsersDatabases.setInt(1, userID);
            resultSet = displayUsersDatabases.executeQuery();

            while (resultSet.next()){
                String retrievedVersion = resultSet.getString("Version");
                String retrievedLocation = resultSet.getString("DBMS_Location");
                String retrievedDateModified = resultSet.getString("DateModified");

                infoToReturn = new DatabaseInfo(userID, retrievedVersion, retrievedLocation, retrievedDateModified);
                infoToReturn.printDatabaseInfo();
                System.out.println();
            }
            resultSet.close();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void setDateToToday (DatabaseInfo user) throws NonexistantConnectionException, SQLException {
        if (dbCon == null){
            throw new NonexistantConnectionException();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String dateToday = formatter.format(date);

        try{
            setDateToday.setString(1, dateToday);
            setDateToday.setInt(2, user.getUserID());
            setDateToday.setString(3, user.getVersion());

            setDateToday.execute();

        } catch(SQLException e){
            e.printStackTrace();
        }

    }

    public DatabaseInfo chooseMostRecent (UserInfo userInfo) throws NonexistantConnectionException {
        DatabaseInfo databaseInfoToReturn = null;
        storeInfo = new HashMap<Integer,DatabaseInfo>();

        if (dbCon == null){
            throw new NonexistantConnectionException();
        }

        ResultSet rs = null;

        try{
            displayUsersDatabases.setInt(1, userInfo.getUserID());
            rs = displayUsersDatabases.executeQuery();

            for(int i=1; rs.next(); i++){
                String version = rs.getString("Version");
                String location = rs.getString("DBMS_Location");
                String dateModified = rs.getString("DateModified");

                storeInfo.put(i, new DatabaseInfo(userInfo.getUserID(),version,location,dateModified));
            }

            boolean change = false;

            do{// Sort query based on which date is the most recent
                for(int i=1; i<storeInfo.size(); i++){
                    Dates first = new Dates(storeInfo.get(i).getDateModified());
                    Dates second = new Dates(storeInfo.get(i+1).getDateModified());

                    int recent = first.whichCameLast(first,second);

                    if(recent == 1){
                        // It's in order
                    }else if (recent == 2){
                        DatabaseInfo dI = storeInfo.get(i);
                        storeInfo.put(i,storeInfo.get(i+1));
                        storeInfo.put(i+1,dI);
                    }
                    else{
                        // They're the same data so do nothing
                    }

                }
            }while(change);

            databaseInfoToReturn = storeInfo.get(1);


        }catch(SQLException e) {
            e.printStackTrace();
        }

        return databaseInfoToReturn;
    }

}
