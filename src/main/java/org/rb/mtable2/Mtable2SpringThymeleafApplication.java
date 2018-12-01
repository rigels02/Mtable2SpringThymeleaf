package org.rb.mtable2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.rb.mtable2.model.MTable;
import org.rb.mtable2.model.Note;
import org.rb.mtable2.model.TableData;
import org.rb.mtable2.repositories.IMtableCrudRepository;
import org.rb.mtable2.repositories.INotesCrudRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Mtable2SpringThymeleafApplication {

	 private static final Logger log= LoggerFactory.getLogger(Mtable2SpringThymeleafApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(Mtable2SpringThymeleafApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner demo(IMtableCrudRepository crudRepo,INotesCrudRepository crudNotes){

		return args -> {
			log.info("Save some MTable tables.....");
                    List<MTable> tables = makeTables(-1);
                    /*for (MTable table : tables) {
                    crudRepo.save(table);
                    }*/
                    crudRepo.save(tables);
                    List<Note> notes = makeNotes();
                    crudNotes.save(notes);
		};
	}
	/**/
        @SuppressWarnings("deprecation")
		public static List<MTable> makeTables(int v){
        List<MTable> mtables = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            MTable table = new MTable();
            //table.setId(i);
            table.setName("Table_"+i);
            table.setModTime(new Date());
            if(i != v){
            	for(int r=1;r<=10; r++){
            table.getData().add(new TableData(new Date(117,1,10+r), "Table_" + i + "_Cat"+r, 10.0, "Note"+r));
            	}
            }
            mtables.add(table);
        }
        return mtables;
}
        public static List<Note> makeNotes(){
        	List<Note> notes = new ArrayList<>();
            for (int i = 1; i <= 3; i++) {
                Note note = new Note("Note_"+i+" Title",new Date(),"Content_"+i);
                notes.add(note);
            }
            
             return notes;  
            }
          
        	
        
}
