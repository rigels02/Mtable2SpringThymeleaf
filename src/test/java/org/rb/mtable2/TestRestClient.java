package org.rb.mtable2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONObject;
import org.rb.mtable2.model.MTable;
import org.rb.mtable2.model.TableInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.minidev.json.JSONArray;

public class TestRestClient {

	private static final Logger log= LoggerFactory.getLogger(TestRestClient.class);
	protected static final String REST_SERVICE_URI = "http://localhost:8080/rest";
		
	/**
	 * The rest service Mtable2SpringThymeleafApplication.java must be running!
	 * before to run this class
	 */
	public static void main(String[] args) throws RestClientException, JsonProcessingException, IOException {
	
		getAll();
	}
	
	public static void getAll() throws JsonProcessingException, IOException, RestClientException {
		RestTemplate restTemplate = new RestTemplate();
		@SuppressWarnings("unchecked")
		ResponseEntity<String> response = restTemplate.getForEntity(REST_SERVICE_URI + "/mytables/", String.class);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(response.getBody());
		JsonNode name = root.path("_embedded");
			JsonNode tables = name.path("mytables");
			Iterator<JsonNode> elem = tables.elements();
			while(elem.hasNext()){
				JsonNode ttable = elem.next();
				Iterator<JsonNode> objs = ttable.elements();
				   while(objs.hasNext()){
					   JsonNode obj = objs.next();
					   if(obj.isTextual()){
					   String v1 = obj.asText();
					   System.out.println(v1);
					   }else if(obj.isLong()){
						   System.out.println(obj);
					   }else if(obj.isBoolean()){
						   System.out.println(obj);
					   }
				   }
				System.out.println("node= "+ttable.toString());
			}
		// ResponseEntity<TableInfo[]> val =
		// restTemplate.getForEntity(REST_SERVICE_URI+"/mytables/",
		// TableInfo[].class);
		System.out.println("Result: " + name.asText());
	}

}
