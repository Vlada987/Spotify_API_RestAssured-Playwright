package utility;

import org.testng.annotations.DataProvider;

public class DataProviders {

	@DataProvider(name = "getArtists")
	public static String[] getArtists() {
		String[] artists = { "sabaton", "Lepa Lukić", "the beatles", "mile kitic", "prljavo kazaliste" };
		return artists;
	};

	@DataProvider(name = "getSongsAndArtists")
	public static String[] getSongsAndArtists() {
		String[] songs = { "sabaton#primo victoria", "lepa lukic#srce je moje violina", "dj krmak#popusili smo",
				"mile kitic#krcma", "prljavo kazaliste#na badnje vece" };
		return songs;
	};

	@DataProvider(name = "albumIDS")
	public static String[] getAlbumIDS() {

		return MyKeys.albumsIDs.stream().toArray(String[]::new);

	}

}
