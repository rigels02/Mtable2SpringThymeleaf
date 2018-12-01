package org.rb.mtable2.controllers.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;


public class FilterInfo {
	private static SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
	
	public static FilterInfo create(String startDate, String endDate, String catWord, String noteWord) {
		
		return new FilterInfo(startDate, endDate, catWord, noteWord);
	}
	public static FilterInfo create(){
		return new FilterInfo("", "", "", "");
	}
public static FilterInfo create(Date startDate, Date endDate, String catWord, String noteWord) {
		
		String sdate= (startDate!=null)? sf.format(startDate): null;
		String edate= (endDate!=null)? sf.format(endDate): null;
		return new FilterInfo(sdate, edate, catWord, noteWord);
	}

	//@DateTimeFormat(pattern="dd/MM/yyyy")
	private String startDate;
	//@DateTimeFormat(pattern="dd/MM/yyyy")
	private String endDate;
	private String catWord;
	private String noteWord;
	
	private FilterInfo() {
		
	}

	private FilterInfo(String startDate, String endDate, String catWord, String noteWord) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
		this.catWord = catWord;
		this.noteWord = noteWord;
	}

	public boolean isFilterActive(){
		if(startDate!=null && !startDate.isEmpty()) return true;
		if(endDate!=null && !endDate.isEmpty()) return true;
		if(catWord!=null && !catWord.isEmpty()) return true;
		if(noteWord!=null && !noteWord.isEmpty()) return true;
		return false;
		}
	
	public String getStartDate() {
		return startDate;
	}
	public Date startDateAsDate() throws ParseException{
		return (startDate!=null)?sf.parse(startDate) : null;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}
	public Date endDateAsDate() throws ParseException{
		return (endDate!=null)? sf.parse(endDate): null;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getCatWord() {
		return catWord;
	}

	public void setCatWord(String catWord) {
		this.catWord = catWord;
	}

	public String getNoteWord() {
		return noteWord;
	}

	public void setNoteWord(String noteWord) {
		this.noteWord = noteWord;
	}

	public boolean isEmpty(){
		if(startDate==null && endDate==null && catWord.isEmpty() && noteWord.isEmpty()){
			return true;
		}
		return false;
		
	}
	
/**
 * Make filter info as url parameters string	
 * @return
 */
public String filterAsParametersString(){
	
	return String.format("&startDate=%s&endDate=%s&catWord=%s&noteWord=%s", 
			(startDate==null)? "" : startDate,(endDate==null) ? "":endDate,catWord,noteWord);
	
	}

}
