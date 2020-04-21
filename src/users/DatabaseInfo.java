package users;

public class DatabaseInfo {
    private int userID;
    private String version;
    private String location;
    private String dateModified;

    public DatabaseInfo(int userID, String version, String location, String dateModified){
        this.userID = userID;
        this.version = version;
        this.location = location;
        this.dateModified = dateModified;
    }

    public int getUserID() {
        return this.userID;
    }

    public String getVersion() {
        return this.version;
    }

    public String getLocation() {
        return this.location;
    }

    public String getDateModified() {
        return this.dateModified;
    }

    public void printDatabaseInfo(){
        System.out.println("User ID: " + this.userID + "\nVersion: " + this.version + "\nDatabase Location: " + this.location + "\nDate Last Modified: " + this.dateModified);
    }

}
