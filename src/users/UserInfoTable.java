package users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import exceptions.NonexistantConnectionException;
import exceptions.SQLExceptionHandler;

import javax.xml.transform.Result;

public class UserInfoTable {

    private Connection dbCon;

    private PreparedStatement addNewUserInfo;
    private PreparedStatement getUserInfo;
    private PreparedStatement checkForLogin;

    public UserInfoTable(Connection connection){
        dbCon = connection;

        try{
            addNewUserInfo = dbCon.prepareStatement("Insert into UserInfo (UserID, First_Name, Last_Name, Username, Password, Email) VALUES (?,?,?,?,?,?);");
            getUserInfo = dbCon.prepareStatement("Select * from UserInfo where UserID = ?;");
            checkForLogin = dbCon.prepareStatement("Select * from UserInfo where Username = ? AND Password = ?;");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void addNewUserInfo(UserInfo userInfo) throws NonexistantConnectionException {
        if (dbCon == null){
            throw new NonexistantConnectionException();
        }

        try{

            addNewUserInfo.setInt(1, userInfo.getUserID());
            addNewUserInfo.setString(2, userInfo.getFirstName());
            addNewUserInfo.setString(3, userInfo.getLastName());
            addNewUserInfo.setString(4, userInfo.getUsername());
            addNewUserInfo.setString(5, userInfo.getPassword());
            addNewUserInfo.setString(6, userInfo.getEmail());

            addNewUserInfo.execute();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public UserInfo getUserInfo (int userID) throws NonexistantConnectionException {
        UserInfo userInfoToReturn = null;

        if (dbCon == null){
            throw new NonexistantConnectionException();
        }

        ResultSet resultSet = null;
        try{

            getUserInfo.setInt(1, userID);
            resultSet = getUserInfo.executeQuery();

            if(resultSet.next()){
                String firstName = resultSet.getString("First_Name");
                String lastName = resultSet.getString("Last_Name");
                String username = resultSet.getString("Username");
                String password = resultSet.getString("Password");
                String email = resultSet.getString("Email");

                userInfoToReturn = new UserInfo(userID, firstName, lastName, username, password, email);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return userInfoToReturn;
    }

    public UserInfo login(String username, String password) throws NonexistantConnectionException {
        UserInfo user = null;

        if (dbCon == null){
            throw new NonexistantConnectionException();
        }

        ResultSet rs = null;

        try{
            checkForLogin.setString(1, username);
            checkForLogin.setString(2, password);
            rs = checkForLogin.executeQuery();

            if(rs.next()){
                int userID = rs.getInt("UserID");
                String firstName = rs.getString("First_Name");
                String lastName = rs.getString("Last_Name");
                String email = rs.getString("Email");

                user = new UserInfo(userID, firstName, lastName, username, password, email);

            }else{
                System.out.println("Wrong login");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return user;
    }

}
