package org.rb.mtable2.repositories;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.rb.mtable2.Mtable2SpringThymeleafApplication;
import org.rb.mtable2.model.MTable;
import org.rb.mtable2.model.TableData;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import static org.junit.Assert.*;


public class MTableRestControllerTest {

	private static final String REST_SERVICE_URI =  "http://localhost:8080/api";
	private static MTable tableWithRow;

	/**
	 * Before run this Rest client test the server Mtable2SpringThymeleafApplication.java
	 * class must be started 
	 * 
	 */
	public static void main(String[] args) {
		testFindAllMTables();
		testFindOneTable();
		testPostTable();
		testPostPutTable();
		testPostDeleteTable();
		testPostRowForTable();
		testPutRowForTable();
		testDeleteRowForTable();
		//------------------//
		testDeleteAll();
	}

private static void checkTables(List<MTable> excpected,MTable[] actual){
	 SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
	for (int i=0;i< excpected.size();i++) {
    	MTable mTable = excpected.get(i);
    	assertEquals(mTable.getName(),actual[i].getName());
    	assertEquals(mTable.isSelected(),actual[i].isSelected());
    	for(int k=0; k<mTable.getData().size();k++){
    	assertEquals(sf.format(mTable.getData().get(k).getCdate()), sf.format(actual[i].getData().get(k).getCdate()));
    	assertEquals( mTable.getData().get(k).getCat(), actual[i].getData().get(k).getCat());
    	assertEquals( mTable.getData().get(k).getAmount(), actual[i].getData().get(k).getAmount());
    	assertEquals( mTable.getData().get(k).getNote(), actual[i].getData().get(k).getNote());
    	}
    	
	}
}
private static void checkTables(MTable excpected,MTable actual){
	 SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
	
   	assertEquals(excpected.getName(),actual.getName());
   	assertEquals(excpected.isSelected(),actual.isSelected());
   	for(int k=0; k<excpected.getData().size();k++){
   	assertEquals(sf.format(excpected.getData().get(k).getCdate()), sf.format(actual.getData().get(k).getCdate()));
   	assertEquals( excpected.getData().get(k).getCat(), actual.getData().get(k).getCat());
   	assertEquals( excpected.getData().get(k).getAmount(), actual.getData().get(k).getAmount());
   	assertEquals( excpected.getData().get(k).getNote(), actual.getData().get(k).getNote());
   	}
   	
	
}
public static void testFindAllMTables() {
	   
		List<MTable> tables = Mtable2SpringThymeleafApplication.makeTables(-1);
		RestTemplate restTemplate = new RestTemplate();
        @SuppressWarnings("unchecked")
		ResponseEntity<MTable[]> response = restTemplate.getForEntity(REST_SERVICE_URI+"/tables/", MTable[].class);
         MTable[] rtables = response.getBody();
        System.out.println("Result: "+tables.toString());
        checkTables(tables, rtables);
        System.out.println("=============== testFindAllMTables OK ========");
       
	}
public static void testFindOneTable(){
	List<MTable> tables = Mtable2SpringThymeleafApplication.makeTables(-1);
	RestTemplate restTemplate = new RestTemplate();
    @SuppressWarnings("unchecked")
	ResponseEntity<MTable> response = restTemplate.getForEntity(REST_SERVICE_URI+"/tables/2", MTable.class);
    checkTables(tables.get(1), response.getBody());
    System.out.println("=============== testFindOneTable OK ========");
}
public static void testPostTable(){

	RestTemplate restTemplate = new RestTemplate();
    MTable table = new MTable();
    table.setName("CreatedTable");
    table.setModTime(new Date());;
    table.setSelected(true);
    HttpEntity<MTable> request = new HttpEntity<>(table);
    ResponseEntity<MTable> result = restTemplate.postForEntity(REST_SERVICE_URI+"/tables", request,MTable.class);
    assertTrue(result.getStatusCode()==HttpStatus.CREATED);
    checkTables(table, (MTable)result.getBody());
	System.out.println("=============== testPostTable OK ========");
	
}
public static void testPostPutTable(){

	RestTemplate restTemplate = new RestTemplate();
    MTable table = new MTable();
    table.setName("CreatedTable1");
    table.setModTime(new Date());;
    table.setSelected(true);
    HttpEntity<MTable> request = new HttpEntity<>(table);
    ResponseEntity<MTable> result = restTemplate.postForEntity(REST_SERVICE_URI+"/tables", request,MTable.class);
    assertTrue(result.getStatusCode()==HttpStatus.CREATED);
    checkTables(table, (MTable)result.getBody());
    table.getData().add(new TableData(new Date(), "putCat", 11.23, "putNote"));
    request = new HttpEntity<>(table);
    result = restTemplate.exchange(REST_SERVICE_URI+"/tables/"+result.getBody().getId(), HttpMethod.PUT, request, MTable.class);
    assertTrue(result.getStatusCode()==HttpStatus.OK);
    checkTables(table, (MTable)result.getBody());
	System.out.println("=============== testPostPutTable OK ========");
	
}
public static void testPostDeleteTable(){

	RestTemplate restTemplate = new RestTemplate();
    MTable table = new MTable();
    table.setName("CreatedTableForDelete");
    table.setModTime(new Date());;
    table.setSelected(true);
    HttpEntity<MTable> request = new HttpEntity<>(table);
    ResponseEntity<MTable> result = restTemplate.postForEntity(REST_SERVICE_URI+"/tables", request,MTable.class);
    assertTrue(result.getStatusCode()==HttpStatus.CREATED);
    checkTables(table, (MTable)result.getBody());
    table.getData().add(new TableData(new Date(), "putCat", 11.23, "putNote"));
    request = new HttpEntity<>(table);
    result = restTemplate.exchange(REST_SERVICE_URI+"/tables/"+result.getBody().getId(), HttpMethod.PUT, request, MTable.class);
    assertTrue(result.getStatusCode()==HttpStatus.OK);
    checkTables(table, (MTable)result.getBody());
    long tableId = result.getBody().getId();
    ResponseEntity<MTable[]> rtables = restTemplate.getForEntity(REST_SERVICE_URI+"/tables", MTable[].class);
    int sz = rtables.getBody().length;
    //delete table
    ResponseEntity<HttpStatus> response = restTemplate.exchange(REST_SERVICE_URI+"/tables/"+tableId, HttpMethod.DELETE, null, HttpStatus.class);
    assertTrue(response.getStatusCode()==HttpStatus.NO_CONTENT);
    
     rtables = restTemplate.getForEntity(REST_SERVICE_URI+"/tables", MTable[].class);
     assertTrue(rtables.getBody().length== (sz-1));

     //ResponseEntity<HttpStatus> tresponse = restTemplate.getForEntity(REST_SERVICE_URI+"/tables/"+tableId,HttpStatus.class);
     //assertTrue(tresponse.getStatusCode()==HttpStatus.NOT_FOUND);
    
    System.out.println("=============== testPostDeleteTable OK ========");
	
}
public static void testPostRowForTable(){
	RestTemplate restTemplate = new RestTemplate();
    tableWithRow = new MTable();
    tableWithRow.setName("TableWithRow");
    tableWithRow.setModTime(new Date());;
    tableWithRow.setSelected(true);
    HttpEntity<MTable> request = new HttpEntity<>(tableWithRow);
    ResponseEntity<MTable> result = restTemplate.postForEntity(REST_SERVICE_URI+"/tables", request,MTable.class);
    assertTrue(result.getStatusCode()==HttpStatus.CREATED);
    long tableId = result.getBody().getId();
    //---
    TableData row = new TableData(new Date(), "rowCat", 12.34, "rowNote");
   
    HttpEntity<TableData> requestRow= new HttpEntity<>(row);
    ResponseEntity<TableData> rowResult = restTemplate.postForEntity(REST_SERVICE_URI+"/tables/"+tableId+"/rows", requestRow,TableData.class);
    assertTrue(result.getStatusCode()==HttpStatus.CREATED);
    checkDataRow(row, rowResult.getBody());
    System.out.println("=============== testPostRowForTbale OK ========");
}
public static  void testPutRowForTable(){
	RestTemplate restTemplate = new RestTemplate();
    tableWithRow = new MTable();
    tableWithRow.setName("testPutRowForTable");
    tableWithRow.setModTime(new Date());;
    tableWithRow.setSelected(true);
    tableWithRow.getData().add(new TableData(new Date(),"Cat1",11.23,"Note1"));
    
    HttpEntity<MTable> request = new HttpEntity<>(tableWithRow);
    ResponseEntity<MTable> result = restTemplate.postForEntity(REST_SERVICE_URI+"/tables", request,MTable.class);
    assertTrue(result.getStatusCode()==HttpStatus.CREATED);
    long tableId = result.getBody().getId();
    long rowId = result.getBody().getData().get(0).getId();
    
    ResponseEntity<TableData> rowResult = restTemplate.getForEntity(REST_SERVICE_URI+"/tables/"+tableId+"/rows/"+rowId, TableData.class);
	assertTrue(rowResult.getStatusCode()==HttpStatus.OK);
	TableData row = rowResult.getBody();
	row.setAmount(100.0);
	row.setCat("changed");
	HttpEntity<TableData> rowRequest = new HttpEntity<>(row);
	ResponseEntity<TableData> row1Result = restTemplate.exchange(REST_SERVICE_URI+"/tables/"+tableId+"/rows/"+rowId, HttpMethod.PUT, rowRequest, TableData.class);
	 assertTrue(row1Result.getStatusCode()==HttpStatus.OK);
	 assertEquals(row1Result.getBody().getCat(), "changed");
	 assertEquals(row1Result.getBody().getAmount(), 100.0,0.01);
	 
	System.out.println("=============== testPutRowForTable OK ========");
}

public static void testDeleteRowForTable(){
	RestTemplate restTemplate = new RestTemplate();
    tableWithRow = new MTable();
    tableWithRow.setName("testDeleteRowForTable");
    tableWithRow.setModTime(new Date());;
    tableWithRow.setSelected(true);
    tableWithRow.getData().add(new TableData(new Date(),"Cat1",11.23,"Note1"));
    
    HttpEntity<MTable> request = new HttpEntity<>(tableWithRow);
    ResponseEntity<MTable> result = restTemplate.postForEntity(REST_SERVICE_URI+"/tables", request,MTable.class);
    assertTrue(result.getStatusCode()==HttpStatus.CREATED);
    long tableId = result.getBody().getId();
    long rowId = result.getBody().getData().get(0).getId();
  
	
    ResponseEntity<TableData> rowsaved = restTemplate.getForEntity(
    		REST_SERVICE_URI+"/tables/"+tableId+"/rows/"+rowId, TableData.class);
    assertTrue(rowsaved.getStatusCode()==HttpStatus.OK);
    
    ResponseEntity<HttpStatus> row1result = restTemplate.exchange(
    		REST_SERVICE_URI+"/tables/"+tableId+"/rows/"+rowId, 
    		HttpMethod.DELETE, null, 
    		HttpStatus.class);
    assertTrue(row1result.getStatusCode()==HttpStatus.NO_CONTENT);
    
    ResponseEntity<TableData> rowNo1=null;
    
		
      try {
    	  /**
    	   * restTemplate for httpstatus = 4** throws HttpClientErrorException exception
    	   */
		rowNo1 = restTemplate.getForEntity(
				   REST_SERVICE_URI+"/tables/"+tableId+"/rows/"+rowId, TableData.class);
	} catch (HttpClientErrorException  e) {
		System.out.println(e.getStatusCode());
		System.out.println(e.getResponseBodyAsString());
		assertTrue(e.getStatusCode()==HttpStatus.NOT_FOUND);
		assertTrue(e.getResponseBodyAsString().isEmpty());
	}
	
    
	System.out.println("=============== testDeleteRowForTable OK ========");
}

private static void checkDataRow(TableData row, TableData actual) {
	SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
	assertEquals(sf.format(row.getCdate()), sf.format(actual.getCdate()));
	assertEquals(row.getCat(), actual.getCat());
	assertEquals(row.getAmount(), actual.getAmount());
	assertEquals(row.getNote(), actual.getNote());
}

public static void testDeleteAll(){
	RestTemplate restTemplate = new RestTemplate();
	ResponseEntity<HttpStatus> result = restTemplate.exchange(REST_SERVICE_URI+"/tables", HttpMethod.DELETE, null, HttpStatus.class);
	
	assertTrue(result.getStatusCode()==HttpStatus.NO_CONTENT);
	ResponseEntity<MTable[]> response = restTemplate.getForEntity(REST_SERVICE_URI+"/tables", MTable[].class);
	assertTrue(response.getBody().length==0);
	System.out.println("=============== testDeleteAll OK ========");
}
}
