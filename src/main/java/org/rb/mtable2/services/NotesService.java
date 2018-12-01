package org.rb.mtable2.services;

import java.util.List;

import org.rb.mtable2.model.Note;
import org.rb.mtable2.repositories.INotesCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class NotesService {

	@Autowired
	INotesCrudRepository notesRepo;
	
	public Iterable<Note> findAll(){
		
		return notesRepo.findAll();
	}
	public Note findOneById(int id){
		
		return notesRepo.findOne(id);
	}

	public Note findOneByTitle(String title){
		
		return notesRepo.findOneByTitle(title);
	}
	public Note updateById(int id, Note unote){
		Note note = notesRepo.findOne(id);
		if(note==null) return null;
		notesRepo.delete(id);
		return notesRepo.save(unote);
		}
	
	public Note createNote(Note note){
		return notesRepo.save(note);
	}
	
	public boolean deleteNoteById(int id){
		Note note = notesRepo.findOne(id);
		if(note==null) return false;
		notesRepo.delete(id);
		return true;
	}
	public List<Note> findByWordInTitle(String word){
		return notesRepo.findByTitleContaining(word);
	}
	public List<Note> findByWordInContent(String word){
		return notesRepo.findByContentContaining(word);
	}
	public List<Note> findByWordInTitleAndWordInContent(String titleWord, String contentWord){
		return notesRepo.findByTitleContainingIgnoreCaseAndContentContainingIgnoreCase(titleWord, contentWord);
	}
}
