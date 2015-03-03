package com.dev.pactera;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;



public class ReceipeFinder {

	private static String COMMA_DELIMITER = ",";
	public static String NAME = "name";
	public static String INGREDIENTS = "ingredients";
	public static String AMOUNT = "amount";
	public static String UNIT = "unit";
	public static String ITEM = "item";
	public static String ORDER_TAKEOUT = "ORDER TAKEOUT";
	
	private HashMap<String,Item> items = new HashMap<String,Item>();
	
	public Date getParsedDate(String dateString) throws ParseException {
		DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
		return format.parse(dateString);
		
	}
	
	public boolean isExistsInFridge(String item)
	{
		return items.containsKey(item);
	}
	public boolean loadCSV(String fileName) throws CookBookException
	{
		FileReader reader = null;
		BufferedReader bReader = null;
		try {
			 reader = new FileReader(fileName);
			 bReader = new BufferedReader(reader);
			while(bReader.ready())
			{
				String line = bReader.readLine();
				StringTokenizer st = new StringTokenizer(line,COMMA_DELIMITER);
				while(st.hasMoreTokens())
				{
					String name = st.nextToken();
					int amount = Integer.parseInt(st.nextToken());
					Item.UNITS unit = Item.UNITS.valueOf(st.nextToken());
					Date date = this.getParsedDate(st.nextToken()) ;
					Item item = new Item(name,amount,unit,date);
					if(!item.isExpired())
						items.put(name, item);
				}
				
			}
			
		} catch (FileNotFoundException e) {
			// TODO add tracing
			new CookBookException(e.getMessage(),e.getCause());
		} catch (IOException e) {
			// TODO add tracing
			new CookBookException(e.getMessage(),e.getCause());
		} catch (ParseException e) {
			// TODO add tracing
			new CookBookException(e.getMessage(),e.getCause());
		}
		
		finally {
			if(bReader!=null)
				try {
					bReader.close();
					if(reader!=null)
						reader.close();
						
				} catch (IOException e) {
					//TODO  Trivial issue show exception in debug
					e.printStackTrace();
				}
				
		}
		
		return true;
	}
	private String selectReceipe(String json) throws CookBookException 
	{
		//TODO validate passed json
		JSONArray receipeArray = (JSONArray) JSONValue.parse(json);
		if(receipeArray==null)
		{
			throw new RuntimeException("Malformed JSON!");
		}
		JSONObject receipe;
		Date minDate = null;
		String selectedReceipe = ORDER_TAKEOUT;
		String receipeName;
		Date minItemUseBy = null;
		for(int i=0;i<receipeArray.size();i++)
			
		{
			receipe = (JSONObject) receipeArray.get(i);
			receipeName = (String) receipe.get(NAME);
			JSONArray ingredients = (JSONArray) receipe.get(INGREDIENTS);
			boolean foundReceipe = true;
			minItemUseBy = null;
			if(ingredients == null)
				{//TODO change to debug level
				System.out.println("Empty receipe book or invalid receipe");
				return this.ORDER_TAKEOUT;
				}
			for(int j=0;j<ingredients.size();j++)
			{
				JSONObject itemObject = (JSONObject) ingredients.get(j);
				String itemName = (String) itemObject.get(ITEM);
				int amount = Integer.parseInt((String) itemObject.get(AMOUNT));
				//TODO check if units match
				Item.UNITS unit  = Item.UNITS.valueOf((String)itemObject.get(UNIT));
				Item item = this.items.get(itemName);
				if(item!=null&&item.isSufficient(amount))
				{  
					Date useBy = item.getUseBy();
					if(minItemUseBy==null)
						{
						minItemUseBy=useBy;
						
						}
					else
						if(minItemUseBy.after(useBy))
							{
							minItemUseBy=useBy;
							}
				}
				else {foundReceipe = false;
				      receipeName = null;
					break;
				}
				
			}
			if(foundReceipe)
			{
				if(minDate==null)
				{
					minDate = minItemUseBy;
					selectedReceipe = receipeName;
					
				} else
				{
					if(minDate.after(minItemUseBy))
					{
						selectedReceipe = receipeName;
						minDate = minItemUseBy;
					}
				}
			}
			
		}
	return selectedReceipe;	
	}
	
	
	public String receipeFinder(String csv,String json) 
	{
		try {
			if(csv==null)
			{   
				//TODO change it to debug level tracing
				System.out.println("CSV file path not passed");
				
				return this.ORDER_TAKEOUT;
				
				
			}
			boolean isSuccess = this.loadCSV(csv);
			if(isSuccess)
			{
				return this.selectReceipe(json);
			}
		} catch (CookBookException e) {
			
			//TODO add debug trace
			e.printStackTrace();
			return "Error";
			
		}
		
		return null;
	}
	
	public static void main(String args[])
	{
		if(args.length<2)
		{
			System.out.println("Invalid method Invocation! Please pass a csv file path and a receipe json");
		}
		
		ReceipeFinder finder = new ReceipeFinder();
		System.out.println(finder.receipeFinder(args[0], args[1]));
	}
}
