package org.rb.mtable2.repositories;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rb.mtable2.model.MTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)

public class IMTableJPARepositoryTest {

	private static final String REST_SERVICE_URI = "/api";

	@Autowired
	private TestRestTemplate restTemplate;
	
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testFindAllMTables() {
		
		//RestTemplate restTemplate = new RestTemplate();
        @SuppressWarnings("unchecked")
		ResponseEntity<MTable[]> response = restTemplate.getForEntity(REST_SERVICE_URI+"/tables/", MTable[].class);
         MTable[] tables = response.getBody();
        System.out.println("Result: "+tables.toString());
	}

	

}
