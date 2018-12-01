package org.rb.mtable2;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rb.mtable2.model.MTable;
import org.rb.mtable2.model.TableData;
import org.rb.mtable2.repositories.IMtableCrudRepository;
import org.rb.mtable2.services.MTableService;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)

public class MTableServiceTest {
	
	@Autowired 
	IMtableCrudRepository crudRepo;
	
	
	@Before
    public void setUp() {
        crudRepo.deleteAll();
        List<MTable> tables = Mtable2SpringThymeleafApplication.makeTables(-1);
        for (MTable mtable : tables) {
            crudRepo.save(mtable);
        }
    }

	@Autowired
	MTableService service;
	
	@Test
	public void testfindAllMTables() {
		List<MTable> tables = service.findAllMTables();
		System.out.println("Tables = "+tables.toString());
	}
	
	@Test
	public void testcreateMTable(){
		crudRepo.deleteAll();
		
		service.createMTable("TestTable", true);
		
		List<MTable> tables = service.findAllMTables();
		assertTrue(tables.size()==1);
		assertEquals("TestTable", tables.get(0).getName());
		
	}
	@Test
	public void testupdateMTable(){
		List<MTable> tables = service.findAllMTables();
		MTable result = service.updateMTable(tables.get(1).getId(), "ChangedName", false);
		assertEquals("ChangedName", result.getName());
	}

	@Test
	public void testdeleteMTable(){
		List<MTable> tables = service.findAllMTables();
		service.deleteMTable(tables.get(1).getId());
		service.deleteMTable(tables.get(2).getId());
		List<MTable> resultTables = service.findAllMTables();
		assertEquals(tables.get(0).toString(), resultTables.get(0).toString());
	}
	@Test
	public void testfindAllRowsForTable(){
		List<MTable> tables = service.findAllMTables();
		List<TableData> result = service.findAllRowsForTable(tables.get(0).getId());
		assertEquals(tables.get(0).getData().toString(), result.toString());
		
	}
	@Test
	public void testfindRowByIdForTable(){
		List<MTable> tables = service.findAllMTables();
		TableData expected = tables.get(1).getData().get(0);
		TableData result = service.findRowForTableById(tables.get(1).getId(),expected.getId());
		assertEquals(expected.toString(), result.toString());
		
	}
	@Test
	public void testaddRowForTable(){
		List<MTable> tables = service.findAllMTables();
		long tabId = tables.get(2).getId();
		int sz1 = tables.get(2).getData().size();
		TableData row = service.addRowForTable(tabId, new TableData(new Date(),"AddCat",11.0,"AddNote"));
		//assertTrue(table.getData().size()==(sz1+1));
		assertEquals("AddCat", row.getCat());
		assertEquals("AddNote", row.getNote());
	}
	@Test
	public void testupdateRowForTable(){
		List<MTable> tables = service.findAllMTables();
		long tabId = tables.get(2).getId();
		MTable table = tables.get(2);
		TableData row = table.getData().get(1);
		row.setCat("ChangedCat");
		row.setAmount(100.0);
		TableData rrow = service.updateRowForTable(tabId, row);
		//assertTrue(result.getData().size()==table.getData().size());
		assertEquals("ChangedCat", rrow.getCat());
		assertEquals(100.0,rrow.getAmount(),0.01);
	}
	@Test
	public void testdeleteRowForTable(){
		List<MTable> tables = service.findAllMTables();
		long tabId = tables.get(1).getId();
		MTable table = tables.get(1);
		TableData row = table.getData().get(1);
		MTable result = service.deleteRowForTable(tabId, row.getId());
		assertTrue(result.getData().size()==(table.getData().size()-1));
		
	}
		
	
}
