package org.rb.mtable2.services;

public class SearchFilter {
 private	String titleWord;
 private	String contentWord;
	
	public SearchFilter() {
		
	}
	public SearchFilter(String titleWord, String contentWord) {
		
		this.titleWord = titleWord;
		this.contentWord = contentWord;
	}
	public String getTitleWord() {
		return titleWord;
	}
	public void setTitleWord(String titleWord) {
		this.titleWord = titleWord;
	}
	public String getContentWord() {
		return contentWord;
	}
	public void setContentWord(String contentWord) {
		this.contentWord = contentWord;
	}

}
