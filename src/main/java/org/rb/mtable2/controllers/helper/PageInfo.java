package org.rb.mtable2.controllers.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

public class PageInfo {
	
	private Logger log= Logger.getLogger(PageInfo.class.getName());
	
	private int number; //page number , offset
	private int size; //pageSize
	private boolean isFirst;
	private boolean isLast;
	private boolean hasNext;
	private boolean hasPrev;
	private int totalPages;
	//private Sort sort;
	//private Order order;
	//private Map<String,String> omap;
	private String orderString;
	private String filterString;
	
	public PageInfo() {
		
	}

	public PageInfo(int pageNumber, int pageSize) {
		
		this.number = pageNumber;
		this.size = pageSize;
	}

	public PageInfo(@SuppressWarnings("rawtypes") Page page) {
		number = page.getNumber();
		size = page.getSize();
		isFirst = page.isFirst();
		isLast = page.isLast();
		hasNext = page.hasNext();
		hasPrev = page.hasPrevious();
		totalPages = page.getTotalPages();
		
		
		 orderString= composeOrderString(page.getSort());	
		 
	}
	
	/**
	 * Build this PageInfo from parameters String :page=%s&size=%s&sort=%s 
	 * Fetch page, size, orderString values
	 * @param pageParms
	 */
	public void  parseFromPageParmsString(String pageParms){
		String[] parms = pageParms.trim().split("&");
		Map<String,String> parmsMap = new HashMap<>();
		for(String parm:parms){
			String[] pare = parm.trim().split("=");
			parmsMap.put(pare[0], pare[1]);
		}
	    for(Entry<String, String> entry: parmsMap.entrySet()){
	    	if(entry.getKey().equalsIgnoreCase("page")){
	    		try {
					this.number= (entry.getValue().isEmpty())? 0: Integer.parseInt(entry.getValue());
					
				} catch (NumberFormatException e) {
					log.log(Level.SEVERE, e.getMessage(), e);
					return;
				}
	    	}
	    	if(entry.getKey().equalsIgnoreCase("size")){
	    		try {
					this.size= (entry.getValue().isEmpty())? 0: Integer.parseInt(entry.getValue());
				} catch (NumberFormatException e) {
					log.log(Level.SEVERE, e.getMessage(), e);
					return;
				}
	    	}
	    	if(entry.getKey().equalsIgnoreCase("sort")){
	    		this.orderString=entry.getValue();
	    	}
	    }
	}
	public String composeOrderString(Sort sort){
		StringBuffer orderBuf= new StringBuffer();
		 if(sort!=null){
			 Order mo;
			 if((mo = sort.getOrderFor("cdate"))!=null){
				 orderBuf.append("cdate,").append(mo.getDirection().name());
				}
			 if((mo = sort.getOrderFor("cat"))!=null){
				 orderBuf.append("&cat,").append(mo.getDirection().name());
			 }
			 return orderBuf.toString();
		 }
		 return null;
		
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int pageNumber) {
		this.number = pageNumber;
	}
	

	public int getSize() {
		return size;
	}

	public void setSize(int pageSize) {
		this.size = pageSize;
	}

	public boolean isFirst() {
		return isFirst;
	}

	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}

	public boolean isLast() {
		return isLast;
	}

	public void setLast(boolean isLast) {
		this.isLast = isLast;
	}

	public boolean isHasNext() {
		return hasNext;
	}

	public void setHasNext(boolean hasNext) {
		this.hasNext = hasNext;
	}

	public boolean isHasPrev() {
		return hasPrev;
	}

	public void setHasPrev(boolean hasPrev) {
		this.hasPrev = hasPrev;
	}

	
	
	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	
	public String getOrderString() {
		return orderString;
	}

	public void setOrderString(String orderString) {
		this.orderString = orderString;
	}
	
	
	
	public String getFilterString() {
		return filterString;
	}

	public void setFilterString(String filterString) {
		this.filterString = filterString;
	}

	/**
	 * make page info as url parameters string
	 * @return
	 */
	public String pageAsParametersString(){
		
		return String.format("page=%s&size=%s&sort=%s", getNumber(),getSize(),getOrderString());
	}
	

}
