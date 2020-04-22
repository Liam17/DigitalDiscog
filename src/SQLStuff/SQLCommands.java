package SQLStuff;

public class SQLCommands {
    private String createAlbumsListTable = "CREATE TABLE AlbumsList(\n" +
            "albumName varchar(100),\n" +
            "artistName varchar(100),\n" +
            "genre varchar(100),\n" +
            "yearDropped int,\n" +
            "rating int,\n" +
            "\n" +
            "PRIMARY KEY (artistName, albumName),\n" +
            "FOREIGN KEY (artistName) references ArtistTable (artistName),\n" +
            "FOREIGN KEY (genre) references GenreTable (genre)\n" +
            ");";

    private String createGenresTable = "CREATE TABLE GenreTable(\n" +
            "genre varchar(100),\n" +
            "\n" +
            "PRIMARY KEY (genre)\n" +
            ");";

    private String createArtistTable = "CREATE TABLE ArtistTable(\n" +
            "artistName varchar(100),\n" +
            "birthday DATE,\n" +
            "\n" +
            "PRIMARY KEY (artistName)\n" +
            ");";

}
