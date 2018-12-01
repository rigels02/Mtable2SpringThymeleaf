package org.rb.mtable2;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rb.mtable2.model.MTable;
import org.rb.mtable2.model.Note;
import org.rb.mtable2.model.TableData;
import org.rb.mtable2.repositories.IMtableCrudRepository;
import org.rb.mtable2.repositories.INotesCrudRepository;
import org.rb.mtable2.services.NotesService;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)

public class NotesServiceTest {
	
	@Autowired 
	INotesCrudRepository crudRepo;
	
	
	@Before
    public void setUp() {
        crudRepo.deleteAll();
        List<Note> notes = Mtable2SpringThymeleafApplication.makeNotes();
        for (Note note : notes) {
            crudRepo.save(note);
        }
    }

	@Autowired
	NotesService service;
	
	@Test
	public void testfindAllNotes() {
		Iterable<Note> notes = service.findAll();
		System.out.println("Notes = "+notes.toString());
		assertTrue(((List<Note>) notes).size()==3);
	}
	
	@Test
	public void testfindByWordInTitle(){
		List<Note> result = service.findByWordInTitle("Note_2");
		System.out.println("Notes = "+result.toString());
		
		assertTrue(result.get(0).getTitle().contains("Note_2"));
	}
	@Test
	public void testfindByWordInContent(){
		List<Note> result = service.findByWordInContent("Content_2");
		System.out.println("Notes = "+result.toString());
		
		assertTrue( result.get(0).getContent().contains("Content_2"));
	}
	
	@Test
	public void testfindByWordInTitleAndInContent(){
		List<Note> result = service.findByWordInTitleAndWordInContent("Note_2", "content_2");
		System.out.println("Notes = "+result.get(0).getTitle()+":"+result.get(0).getContent());
		//ignore case
		assertTrue(result.size()==1);
		assertTrue(result.get(0).getTitle().contains("Note_2"));
		assertTrue( result.get(0).getContent().contains("Content_2"));
		result = service.findByWordInTitleAndWordInContent("Note_2", "content");
		assertTrue(result.size()==1);
		assertTrue(result.get(0).getTitle().contains("Note_2"));
		assertTrue( result.get(0).getContent().contains("Content_2"));
		result = service.findByWordInTitleAndWordInContent("Note_2", "");
		assertTrue(result.size()==1);
		assertTrue(result.get(0).getTitle().contains("Note_2"));
		assertTrue( result.get(0).getContent().contains("Content_2"));
		result = service.findByWordInTitleAndWordInContent("", "");
		assertTrue(result.size()==3);
	}
	
	//@Test
	public void testcreateNote(){
		crudRepo.deleteAll();
		Note note = new Note("Note_1", new Date(), "Conten_1");
		service.createNote(note);
		
		Iterable<Note> notes = service.findAll();
		assertTrue(((List<Note>) notes).size()==1);
		assertEquals("TestTable", ((List<Note>) notes).get(0).getTitle());
		
	}
	//@Test
	public void testupdateMTable(){
		Iterable<Note> notes = service.findAll();
		((List<Note>) notes).get(1).setTitle("ChangedTitle");
		Note result = service.updateById(((List<Note>) notes).get(1).getId(), ((List<Note>) notes).get(1));
		assertEquals("ChangedTitle", result.getTitle());
	}

	//@Test
	/*public void testdeleteMTable(){
		List<MTable> tables = service.findAllMTables();
		service.deleteMTable(tables.get(1).getId());
		service.deleteMTable(tables.get(2).getId());
		List<MTable> resultTables = service.findAllMTables();
		assertEquals(tables.get(0).toString(), resultTables.get(0).toString());
	}*/
		
	
}
