package users;

public class UserInfo {

    private int userID;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;

    public UserInfo (int userID, String firstName, String lastName, String username, String password, String email){
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public int getUserID() {
        return userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void printUserInfo(String password){
        if (this.password.compareTo(password) == 0){
            System.out.println("User ID: " + this.userID + "\nFirst Name: " + this.firstName + "\nLast Name: " + this.lastName
            + "\nUsername: " + this.username + "\nEmail: " + this.email);
        } else {
            System.out.println("Wrong password");
        }
    }
}
