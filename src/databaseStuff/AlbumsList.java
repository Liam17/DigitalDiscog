package databaseStuff;

public class AlbumsList {

    private String albumName;
    private String artistName;
    private String genre;
    private int yearDropped;
    private int rating;


    public AlbumsList(String albumName, String artistName, String genre, int yearDropped, int rating) {
        this.albumName = albumName;
        this.artistName = artistName;
        this.genre = orderGenresAlphabetically(genre);
        this.yearDropped = yearDropped;
        this.rating = rating;
    }

    public String getAlbumName() {
        return this.albumName;
    }

    public String getArtistname() {
        return this.artistName;
    }

    public String getGenre() {
        return this.genre;
    }

    public String appendToGenres(String genre){
        this.genre = this.genre + "&" + genre;
        this.genre = orderGenresAlphabetically(this.genre);
        return this.genre;
    }

    public String orderGenresAlphabetically(String genre){
        String[] genreDecomposed = genre.split("&");
        String saveGenre;
        boolean switches = false;
        do{
            switches = false;
            for (int i = 0; i<genreDecomposed.length-1; i++){
                if (genreDecomposed[i].compareTo(genreDecomposed[i+1])>0){
                    saveGenre = genreDecomposed[i];
                    genreDecomposed[i] = genreDecomposed[i+1];
                    genreDecomposed[i+1] = saveGenre;
                    switches = true;
                }
            }
        }while (switches);
        String genreAlphabetical = null;
        for(int i=0; i<genreDecomposed.length; i++){
            if (i==0){
                genreAlphabetical = genreDecomposed[i];
            }
            else{
                genreAlphabetical = genreAlphabetical + " & " + genreDecomposed[i];
            }
        }
        return genreAlphabetical;
    }

    public int getYearDropped() {
        return this.yearDropped;
    }

    public int getRating() {
        return this.rating;
    }

    public void printAlbumsList(){
        System.out.println(this.albumName + " by " + this.artistName + ", Genre: " + this.genre + ", Dropped: " +
                this.yearDropped + ", Rated: " + this.rating);
    }

}
