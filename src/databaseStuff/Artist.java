package databaseStuff;

import extras.*;

public class Artist {
    private String artistName;
    private Dates birthday;

    public Artist (String artistName, Dates birthday) {
        this.artistName = artistName;
        this.birthday = birthday;
    }

    public String getArtistName(){
        return artistName;
    }

    public int getArtistAge() {
        return this.birthday.getYearsPassed();
    }

    public void printArtist(){
        System.out.println(this.artistName + ", " + this.getArtistAge() + " years old");
    }

}
