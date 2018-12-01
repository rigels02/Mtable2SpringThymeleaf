package org.rb.mtable2.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.rb.mtable2.model.Note;
import org.rb.mtable2.services.NotesService;
import org.rb.mtable2.services.SearchFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/mynotes")
public class NotesController {

	
	private static final String NOTES = "MODE_NOTES";
	private static final String UPDATENOTE = "MODE_UPDATE";
	private static final String VIEWNOTE = "MODE_VIEW";
	//---------------//
	private static final String PAGEINDEX="notes/index";
	private static final String PAGEUPDATE="notes/index"; //"notes/update";
	private static final String PAGEVIEW="notes/view";
	
	
	
	@Autowired
	private NotesService noteService;
	
	
	
	@RequestMapping(method=RequestMethod.GET)
	public String allNotes( Model model){
	 Iterable<Note> notes = noteService.findAll();
	
	 updateNotesModel(model,NOTES,notes);
	 return PAGEINDEX; //"notes/index";
	}

	private void updateNotesModel(Model model, String mode, Iterable<Note> notes) {
		 model.addAttribute("notes", notes);
		 model.addAttribute("mode",mode);
		//model.addAttribute("searchFilter", new SearchFilter("",""));
		
	}
	private void updateOneNoteModel(Model model, String mode, Note note) {
		 model.addAttribute("note", note);
		 model.addAttribute("mode",mode);
		
	}
	
	@RequestMapping(path="{id}",method=RequestMethod.GET)
	public String oneNoteById(
			@PathVariable("id") int id, 
			@RequestParam(name="mode",defaultValue="view") String mode, 
			Model model){
	 Note note = noteService.findOneById(id);
	 if(mode.equals("view")){
	 updateOneNoteModel(model,VIEWNOTE,note);
	 return PAGEVIEW; //"notes/view";
	 }else{
		 updateOneNoteModel(model, UPDATENOTE, note);
	 }
	 return PAGEUPDATE; //"notes/update";
	}
	@PostMapping(path="save-note")
	public String updateNoteById(
			@ModelAttribute @Valid Note note,
			BindingResult bind,
			Model model ){
		
	 if(bind.hasErrors()){
		 return PAGEUPDATE; //"notes/update";
	 }
	 
	 if(note.getId()<=0){
	    noteService.createNote(note);	 
	 }else{
	    noteService.updateById(note.getId(), note);
	 }
	 updateNotesModel(model, NOTES, noteService.findAll());
	 return  PAGEINDEX; //"notes/index";
	}
	
	@GetMapping(path="create-note")
	public String createNote(Model model){
		Note note = new Note("",new Date(),"");
		updateOneNoteModel(model, UPDATENOTE, note);
		return PAGEUPDATE; //"notes/update";
	}
	@GetMapping(path="delete-note")
	public String deleteNoteById(@RequestParam("id") int id,Model model){
		boolean ok = noteService.deleteNoteById(id);
		if(!ok) {
			model.addAttribute("errMsg", "Delete unsuccessful!");
		}
		updateNotesModel(model, NOTES, noteService.findAll());
		return PAGEINDEX; //"notes/index";
	}
	@PostMapping(value="filter")
	public String findByTitleWordAndContentWord(
			//@ModelAttribute("searchFilter") SearchFilter searchFilter,
			@RequestParam("titleWord") String titleWord,
			@RequestParam("contentWord") String contentWord,
			HttpServletRequest request,
			Model model
			){
		
		
	//System.out.println("titleWord: "+titleWord+" contentWord: "+contentWord);
		List<Note> result = noteService.findByWordInTitleAndWordInContent(
				titleWord, contentWord);
		updateNotesModel(model, NOTES, result);
		request.setAttribute("titleWord", titleWord);
		request.setAttribute("contentWord", contentWord);
		
		return PAGEINDEX;
	}
	@GetMapping(value="filter-reset")
	public String resetFilter(HttpServletRequest request, Model model){
		List<Note> result = noteService.findByWordInTitleAndWordInContent("", "");
		updateNotesModel(model, NOTES, result);
		request.setAttribute("titleWord", "");
		request.setAttribute("contentWord", "");
		//System.out.println("Reset called...");
		return PAGEINDEX;
	}
	
}
