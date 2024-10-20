package tests;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import extentReports.Setup;
import frontend.GetTheCode;
import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import junit.framework.Assert;
import pojos.Playlist;
import rest.Context;
import rest.EContentType;
import rest.Methods;
import rest.RestUtil;
import utility.DataProviders;
import utility.MyKeys;
import utility.UtilClass;

public class TestClass extends Setup{

	
	
//Obtaining the bearer token with client credentials just for test not for use
	@Test(priority = 0)
	public void test_00_getToken() {

		Response resp = TestMethods.getAccessToken_Client_Credentials();
		int expire = RestUtil.parse(resp, "expires_in");

		Assert.assertEquals(resp.getStatusCode(), 200);
		Assert.assertEquals(RestUtil.parse(resp, "token_type"), "Bearer");
		Assert.assertEquals(expire, 3600);

	}

	@Test(priority = 1)
	public void test00_getAccessToken_auth_code_flow() throws IOException, InterruptedException {

		Response resp = TestMethods.getAccessToken_Auth_Code();
		MyKeys.token = RestUtil.parse(resp, "access_token");
		int expire = RestUtil.parse(resp, "expires_in");

		Assert.assertEquals(resp.getStatusCode(), 200);
		Assert.assertEquals(RestUtil.parse(resp, "token_type"), "Bearer");
		Assert.assertEquals(expire, 3600);
		System.out.println(MyKeys.token);
	}

//*** GET INFO OF CURRENT USER ***
	@Test(priority = 2)
	public void test_01_getUser() {

		Response resp = TestMethods.getUser();

		Assert.assertEquals(resp.getStatusCode(), 200);
		Assert.assertEquals(RestUtil.parse(resp, "display_name"), "budza");
		Assert.assertEquals(RestUtil.parse(resp, "country"), "RS");
	}

//*** GET ALL PLAYLIST OF CURRENT USER ***
	@Test(priority = 3)
	public void test_02_getPlaylists() {

		Response resp = TestMethods.getPlaylists();

		Assert.assertEquals(resp.getStatusCode(), 200);
		Assert.assertNotNull(RestUtil.parse(resp, "items[0].id"));
		Assert.assertEquals(RestUtil.parse(resp, "items[0].owner.display_name"), "budza");
		Assert.assertTrue(resp.jsonPath().getList("items.id").stream().allMatch(el -> el != null));

		MyKeys.myPlaylistID = RestUtil.parse(resp, "items[0].id");
	}

// *** GET SONGS FROM THE MY INITIAL PLAYLIST ***
	@Test(priority = 4)
	public void test_02_getTrack_In_Playlist() {

		Response resp = TestMethods.getTracks(MyKeys.myPlaylistID);

		String pathArtists = "items.track.artists.name";
		String pathSongs = "items.track.name";
		List<String> artists = UtilClass.flatMyList(resp.jsonPath().getList(pathArtists));
		List<String> songs = resp.jsonPath().getList(pathSongs);

		Assert.assertEquals(resp.getStatusCode(), 200);
		Assert.assertEquals(artists, Arrays.asList("Nino", "Nightwish", "Sia"));
		Assert.assertTrue(songs.stream().anyMatch(el -> el.equals("Sta cu mala s tobom")));

		MyKeys.myArtistsIDs = UtilClass.flatMyList(resp.jsonPath().getList("items.track.artists.id"));
	}

//GET THE ARTISTS FROM MY DEFAULT PLAYLISTS - DEPENDS ON METHOD BEFORE
	@Test(priority = 4, dependsOnMethods = { "test_02_getTrack_In_Playlist" })
	public void test_03_GetMy_FavoriteArtists() {

		List<String> names = new ArrayList<>();
		List<List<String>> genres = new ArrayList<>();

		for (int a = 0; a < MyKeys.myArtistsIDs.size(); a++) {
			Response resp = TestMethods.getArtist(MyKeys.myArtistsIDs.get(a));
			Assert.assertEquals(resp.getStatusCode(), 200);
			names.add(resp.jsonPath().get("name"));
			genres.add(resp.jsonPath().getList("genres"));

		}

		Assert.assertTrue(names.containsAll(Arrays.asList("Nino", "Nightwish", "Sia")));
		Assert.assertTrue(UtilClass.flatMyList(genres).size() > 8);
		Assert.assertTrue(UtilClass.flatMyList(genres).stream().anyMatch(el -> el.equals("bosnian pop")));

	}

// *** SEARCH FOR SOME RANDOM ARTISTS 
	@Test(priority = 5, dataProvider = "getArtists", dataProviderClass = DataProviders.class)
	public void test_04_searchFor_Artist(String artist) {

		Response resp = TestMethods.searchArtists(artist);

		resp.then().log().all();
		String name = RestUtil.parse(resp, "artists.items[0].name");
		String id = RestUtil.parse(resp, "artists.items[0].id");
		List<String> genres = resp.jsonPath().getList("artists.items[0].genres");

		Assert.assertEquals(resp.getStatusCode(), 200);
		Assert.assertTrue(name.equalsIgnoreCase(artist));
		Assert.assertTrue(genres.stream().allMatch(el -> el != null));

	}

	@Test(priority = 6, dataProvider = "getSongsAndArtists", dataProviderClass = DataProviders.class)
	public void test_05_searchFor_song(String song) {

		String[] songAndArtist = song.split("#");

		Response resp = TestMethods.searchSongs(songAndArtist[0], songAndArtist[1]);

		resp.then().log().all();
		String songName = RestUtil.parse(resp, "tracks.items[0].name");
		String id = RestUtil.parse(resp, "tracks.items[0].id");
		String name = RestUtil.parse(resp, "tracks.items[0].artists[0].name");

		Assert.assertEquals(resp.getStatusCode(), 200);
		Assert.assertTrue(name.equalsIgnoreCase(songAndArtist[0]));
		Assert.assertTrue(songName.equalsIgnoreCase(songAndArtist[1]));
		Assert.assertNotNull(id);

	}

//*** GET ALL ALBUMS OF ARTIST
	@Test(priority = 7, dataProvider = "getArtists", dataProviderClass = DataProviders.class)
	public void test_06_GetAlbums(String artist) {

		Response resp = TestMethods.getAlbums(artist);

		List<String> namesOfAlbums = resp.jsonPath().getList("tracks.items.album.name");
		MyKeys.albumsIDs = resp.jsonPath().getList("tracks.items.album.id");

		Assert.assertEquals(resp.statusCode(), 200);
		Assert.assertTrue(namesOfAlbums.stream().allMatch(el -> el != null));
		;
		Assert.assertTrue(MyKeys.albumsIDs.stream().allMatch(el -> el != null));
	}

//*** GET SPECIFIC ALBUM BY ID , DEPEND ON METHOD BEFORE
	@Test(priority = 8, dependsOnMethods = {
			"test_06_GetAlbums" }, dataProvider = "albumIDS", dataProviderClass = DataProviders.class)
	public void test_07_Get_Album(String id) {

		Response resp = TestMethods.getAlbum(id);

		List<String> songs = resp.jsonPath().getList("tracks.items.name");
		List<String> IDs = resp.jsonPath().getList("tracks.items.id");

		Assert.assertEquals(resp.statusCode(), 200);
		Assert.assertTrue(songs.stream().allMatch(el -> el != null));
		Assert.assertTrue(IDs.stream().allMatch(el -> el != null));
		Assert.assertEquals(resp.contentType(), "application/json; charset=utf-8");
		Assert.assertEquals(resp.getHeader("Via"), "HTTP/2 edgeproxy, 1.1 google");
		Assert.assertEquals(resp.getHeader("x-content-type-options"), "nosniff");
	}

// *** GET LIST OF SONG OF ARTIST
	@Test(priority = 9, dataProvider = "getArtists", dataProviderClass = DataProviders.class)
	public void test_08_getAllSongOfArtist(String artist) {

		List<String> songs = TestMethods.Get_All_Songsof_artist(artist);
		System.out.println(songs.size());
		System.out.println(songs.get(0));
		System.out.println(songs);

		Assert.assertEquals(songs.size() > 10, true);
		Assert.assertTrue(songs.stream().allMatch(el -> el != null));
		Assert.assertTrue(songs.stream().allMatch(el -> !el.isBlank()));

	}

//**** CREATE A NEW PLAYLIST
	@Test(priority = 10)
	public void test_09_CreatePlaylist() throws JsonProcessingException {

		Response resp = TestMethods.createPlaylist("subota" + UtilClass.milisTime(), "oktobar");
		MyKeys.createPlayistID = RestUtil.parse(resp, "id");

		Assert.assertEquals(resp.getStatusCode(), 201);
		Assert.assertEquals(RestUtil.parse(resp, "description"), "oktobar");
		Assert.assertTrue(RestUtil.parse(resp, "name").toString().contains("subota"));
		Assert.assertNotNull(MyKeys.createPlayistID);
	}
	//ADD SONGS TO NEW PLAYLIST
	@Test(priority = 11, dataProvider = "getSongsAndArtists", dataProviderClass = DataProviders.class)
	public void test_10_Add_SongsToPlaylist(String artistAndSong) throws JsonProcessingException {

		String[] data = artistAndSong.split("#");
		Response resp = TestMethods.addSongs(data[0], data[1], "subota", 0);

		Assert.assertEquals(resp.getStatusCode(), 201);
		Assert.assertNotNull(resp.jsonPath().get("snapshot_id").toString());
		Assert.assertNotNull(resp.jsonPath().get("snapshot_id").toString());
		Assert.assertTrue(resp.getStatusLine().contains("Created"));
	}

	//INSPECT NEWLY CREATED PLAYLIST
	@Test(priority = 12)
	public void test_11_getCreatedPlaylist(String artistAndSong) {

		String artist = artistAndSong.split("#")[0];
		String pathArtists = "items.track.artists.name";
		Response resp = TestMethods.getTracks(MyKeys.createPlayistID);

		List<String> artists = UtilClass.flatMyList(resp.jsonPath().getList(pathArtists));
		List<String> ExpectedArtists = Arrays.asList(DataProviders.getSongsAndArtists());

		Assert.assertEquals(resp.getStatusCode(), 200);
		Assert.assertEquals(artists.size() == 5, true);
		Assert.assertEquals(artists, ExpectedArtists);

	}
}
