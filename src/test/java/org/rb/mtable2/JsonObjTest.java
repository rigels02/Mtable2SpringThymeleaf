package org.rb.mtable2;

import static org.junit.Assert.*;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.rb.mtable2.model.MTable;
import org.rb.mtable2.model.TableData;

public class JsonObjTest {

	@Test
	public void test() {
		List<MTable> tables = Mtable2SpringThymeleafApplication.makeTables(-1);
		List<TableData> rows= tables.get(0).getData();
		JSONObject obj = new JSONObject();
	    try {
	    	JSONArray jarr= new JSONArray();
	    	for (TableData row : rows) {
	    		JSONObject jrow = new JSONObject();
	    		jrow.put("id", row.getId());
	    		jrow.put("Cdate", row.getCdate());
	    		jrow.put("Cat", row.getCat());
	    		jrow.put("Amount", row.getAmount());
	    		jrow.put("Note", row.getNote());
	    		
	    		jarr.put(jrow);
			}
			obj.put("data", jarr);
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
	    System.out.println("obj ="+obj.toString());
	}
	
	
}
