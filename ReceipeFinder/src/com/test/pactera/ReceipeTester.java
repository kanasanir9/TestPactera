package com.test.pactera;

import static org.junit.Assert.*;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.dev.pactera.CookBookException;
import com.dev.pactera.ReceipeFinder;

public class ReceipeTester {
	ReceipeFinder finder;
	String filePath;
	String jsonReceipeString;
	JSONArray receipeBook;

	private static String NAME = ReceipeFinder.NAME;
	private static String ITEM = ReceipeFinder.ITEM;
	private static String AMOUNT = ReceipeFinder.AMOUNT;
	private static String UNIT = ReceipeFinder.UNIT;
	private static String INGREDIENTS = ReceipeFinder.INGREDIENTS;

	private static String FILENAME = "Items.csv";

	@Before
	public void setUp() throws Exception {
		finder = new ReceipeFinder();
		filePath = ReceipeTester.class.getProtectionDomain().getCodeSource()
				.getLocation().getFile();
		JSONObject bread = new JSONObject();
		bread.put(ITEM, "bread");
		bread.put(AMOUNT, "2");
		bread.put(UNIT, "slices");
		JSONObject cheese = new JSONObject();
		cheese.put(ITEM, "cheese");
		cheese.put(AMOUNT, "2");
		cheese.put(UNIT, "slices");

		JSONObject grilledCheese = new JSONObject();
		grilledCheese.put(NAME, "grilled cheese on toast");
		JSONArray ingre = new JSONArray();
		ingre.add(bread);
		ingre.add(cheese);
		grilledCheese.put(INGREDIENTS, ingre);

		JSONObject saladSandWich = new JSONObject();
		saladSandWich.put(NAME, "salad sandwich");
		JSONObject mixedSalad = new JSONObject();
		mixedSalad.put(ITEM, "mixed salad");
		mixedSalad.put(AMOUNT, "100");
		mixedSalad.put(UNIT, "grams");

		ingre = new JSONArray();
		ingre.add(bread);
		ingre.add(mixedSalad);
		saladSandWich.put(INGREDIENTS, ingre);

		receipeBook = new JSONArray();
		receipeBook.add(grilledCheese);
		receipeBook.add(saladSandWich);
		jsonReceipeString = receipeBook.toJSONString();

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDateParser() throws Exception {
		Date actualDate = finder.getParsedDate("25/12/2014");
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
		Date expectedDate = dateFormat.parse("25/12/2014");

		assertEquals(actualDate, expectedDate);

	}

	@Test
	public void testLoadCSV() {

		File file = new File("Items.csv");
		try {
			finder.loadCSV(file.getAbsolutePath());
			assertTrue(finder.isExistsInFridge("eggs"));
		} catch (CookBookException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void defaulttest() {
		String result = finder.receipeFinder("Items.csv", jsonReceipeString);
		assertEquals(result, "salad sandwich");
	}

	@Test
	public void testForFewerItemsThanRequired() {

		String json = " [{\"name\":\"omlet\",\"ingredients\":[{\"item\":\"eggs\",\"amount\":\"2\",\"unit\":\"of\"}]}]";
		String result = finder.receipeFinder(FILENAME, json);
		assertEquals("omlet", result);
		// increase eggs to 4
		String json2 = " [{\"name\":\"omlet\",\"ingredients\":[{\"item\":\"eggs\",\"amount\":\"4\",\"unit\":\"of\"}]}]";
		result = finder.receipeFinder(FILENAME, json2);
		assertNotEquals("omlet", result);

	}

	@Test
	public void testForNonExistentItem() {

	}

	@Test
	public void testForMultiplePossibleItem() {

	}

	

	@Test
	public void testMalformedReceipeJson() {
		String json = "[{\"name\":}";
		try {
			finder.receipeFinder(FILENAME, json);
		} catch (RuntimeException e) {
			assert (true);
		}

	}

	@Test
	public void testEmptyFridge() {
		String json = " [{\"name\":\"omlet\",\"ingredients\":[{\"item\":\"eggs\",\"amount\":\"2\",\"unit\":\"of\"}]}]";
		String result = finder.receipeFinder(null, json);
		assertEquals(ReceipeFinder.ORDER_TAKEOUT, result);

	}

	@Test
	public void testEmptyReceipeBook() {
		String json = "[{}]";
		String result = finder.receipeFinder(FILENAME, json);
		assertEquals(ReceipeFinder.ORDER_TAKEOUT, result);
	}

	@Test
	public void testPartialMatchinReceipe() {

	}

	@Test
	public void testForMatchInReceipeButPastExpiryDate() {

	}

	@Test
	public void testForValidJSONButInvalidReceipe() {
		String json = "[{\"ram\":\"test\"}]";
		String result = finder.receipeFinder(FILENAME, json);
		// invalid json prints a message to sysout or traces
		assertEquals(finder.ORDER_TAKEOUT, result);
	}

}
