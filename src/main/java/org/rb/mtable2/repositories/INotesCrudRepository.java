package org.rb.mtable2.repositories;

import java.util.List;

import org.rb.mtable2.model.Note;
import org.springframework.data.repository.CrudRepository;
/**
 * Proper keyWords in methods see in:
 * {@link http://docs.spring.io/spring-data/jpa/docs/1.5.1.RELEASE/reference/html/jpa.repositories.html#jpa.query-methods.query-creation}
 * @author raitis
 *
 */
public interface  INotesCrudRepository extends CrudRepository<Note, Integer>{

	Note findOneByTitle(String title); 
	List<Note> findByTitleContaining(String word);
	List<Note> findByContentContaining(String word);
	List<Note> findByTitleContainingAndContentContaining(String titleWord,String contentWord);
	List<Note> findByTitleContainingIgnoreCaseAndContentContainingIgnoreCase(String titleWord,String contentWord);
}
