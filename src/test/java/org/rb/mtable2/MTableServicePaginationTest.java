package org.rb.mtable2;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rb.mtable2.model.MTable;
import org.rb.mtable2.model.TableData;
import org.rb.mtable2.repositories.IMtableCrudRepository;
import org.rb.mtable2.services.MTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= Mtable2SpringThymeleafApplication.class,webEnvironment = WebEnvironment.RANDOM_PORT)

public class MTableServicePaginationTest {

	@Autowired 
	IMtableCrudRepository crudRepo;
	
	
	@Before
    public void setUp() {
        crudRepo.deleteAll();
        /*List<MTable> tables = makeTables(-1,30);
        for (MTable mtable : tables) {
            crudRepo.save(mtable);
        }*/
    }

	@Autowired
	MTableService service;
	
	
	public static List<MTable> makeTables(int v,int maxRows){
        List<MTable> mtables = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            MTable table = new MTable();
            //table.setId(i);
            table.setName("Table_"+i);
            table.setModTime(new Date());
            if(i != v){
            for(int k=0; k<maxRows; k++){	
            table.getData().add(new TableData(new Date(117,1,10), "Table_" + i + "_Cat"+k, 10.0+k, "Note"+k));
            }
            }
            mtables.add(table);
        }
        return mtables;
}
	@Test
	public void testPagination() {
		 List<MTable> tables = makeTables(-1,30);
		 List<TableData> data= tables.get(0).getData();
		 PageRequest pageable = new PageRequest(0, 10);
		 int start = pageable.getOffset();
		 int end = (start + pageable.getPageSize()) > data.size() ? data.size() : (start + pageable.getPageSize());
		 Page<TableData> pages = new PageImpl<>(data.subList(start, end), pageable, data.size());
		 pageable = new PageRequest(1, 10);
		 start = pageable.getOffset();
		 end = (start + pageable.getPageSize()) > data.size() ? data.size() : (start + pageable.getPageSize());
		 pages = new PageImpl<>(data.subList(start, end), pageable, data.size());
		 pageable= (PageRequest) pageable.next();
		 start = pageable.getOffset();
		 end = (start + pageable.getPageSize()) > data.size() ? data.size() : (start + pageable.getPageSize());
		 pages = new PageImpl<>(data.subList(start, end), pageable, data.size());
		 
		 /****************
		 PagedListHolder<TableData> pl = new PagedListHolder<>(data);
		 pl.setPageSize(10);
		 pl.setPage(0);
		 List<TableData> p1 = pl.getPageList();
		 TableData el = p1.get(0);
		 pl.nextPage();
		 List<TableData> p2 = pl.getPageList();
		 int sz = p2.size();
		  el = p2.get(0);
		  
		 pl.setPage(2);
		 List<TableData> p3 = pl.getPageList();
		 sz = p3.size();
		  el = p3.get(0);
		  *****************/
	}
	@Test
	public void testfindAllRowsForTableByPage(){
		 List<MTable> tables = makeTables(-1,25);
		 
	        for (MTable mtable : tables) {
	            crudRepo.save(mtable);
	        }
		 List<MTable> rtables = service.findAllMTables();
		 List<TableData> data = rtables.get(0).getData();
		 long tid = rtables.get(0).getId();
		 PageRequest pageable = new PageRequest(0, 10);
		 
		 Page<TableData> page1 = service.findPageOfRowsForTable(tid, pageable);
		 Page<TableData> page2 = service.findPageOfRowsForTable(tid, (pageable=(PageRequest) pageable.next()));
		 Page<TableData> page3 = service.findPageOfRowsForTable(tid, (pageable=(PageRequest) pageable.next()));
		  assertTrue(page3.isLast());	
		
		
	}

}
