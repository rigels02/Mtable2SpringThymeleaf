package org.rb.mtable2.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


import org.rb.mtable2.controllers.helper.FilterInfo;
import org.rb.mtable2.controllers.helper.PageInfo;
import org.rb.mtable2.controllers.helper.StatusInfo;
import org.rb.mtable2.controllers.helper.StatusType;
import org.rb.mtable2.model.MTable;
import org.rb.mtable2.model.TableData;
import org.rb.mtable2.model.TableDataList;
import org.rb.mtable2.services.MTableService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;


//import org.springframework.data.domain.Pageable;
/**
 * {@link http://ankushs92.github.io/tutorial/2016/05/03/pagination-with-spring-boot.html}
 * 
 * {@link http://stackoverflow.com/questions/18490820/spring-thymeleaf-how-to-implement-pagination-for-a-list}
 * 
 * @author raitis
 *
 */
@Controller
@RequestMapping("/mtables")
public class MTableController {
 
	//TODO: for rows operations check table id consistency: should be the same on client side and on server side 
	
	private static final String NEWTABLE = "MODE_NEW_TABLE";
	private static final String TABLES = "MODE_TABLES";
	private static final String UPDATETABLE = "MODE_UPDATE_TABLE";
	private static final String ROWS = "MODE_ROWS";
	private static final String NEWROW = "MODE_NEW_ROW";
	private static final String UPDATEROW = "MODE_UPDATE_ROW";
	//------//
	private static final String SORTFILTERRESETMSG = "The sort and filter settings are been reset!";
	private static final String FILTERRESETMSG = "The filter settings are been reset!";
	
	
	
	@Autowired
	private MTableService mtableService;
	
	
	private LinkedList<StatusInfo> statusInfoList;
	
	
	
	
	@GetMapping(path ="/dummy")
    public String getDummy(Model model){
        MTable table = new MTable();
        table.setName("DummyTable");
        table.setModTime(new Date());
        table.getData().add(new TableData(new Date(),"DummyCat1",11.0,"DummyNote1"));
        table.getData().add(new TableData(new Date(),"DummyCat2",22.0,"DummyNote2"));
        table.getData().add(new TableData(new Date(),"DummyCat3",33.0,"DummyNote3"));
        model.addAttribute("result", table);
        return "dummy";
    }
	
	
	//mtables
	@RequestMapping(method=RequestMethod.GET)
	public String allTables( Model model){
	 Iterable<MTable> result = mtableService.findAllMTables();
	
	 updateTablesModel(model,TABLES,result);
	 return "index";
	}
	
	
	private void updateTablesModel(Model model, String mode, Iterable<MTable> result) {
		model.addAttribute("selectedTable",mtableService.getSelectedTableId());
		 model.addAttribute("tableName", mtableService.getSelectedTableName());
		 model.addAttribute("tables", result);
		 model.addAttribute("mode",mode);
		
	}


	@RequestMapping(path="{id}", method=RequestMethod.GET)
	public String oneTable(@PathVariable(name="id") int id, Model model){
		MTable result = mtableService.findMTableById(id);
		 model.addAttribute("table", result);
		 model.addAttribute("mode", UPDATETABLE);
		 return "index";
	}
	

	@RequestMapping(path="new-table", method=RequestMethod.GET)
	public String newTable(
			Model model){
		 MTable table = new MTable();
		 table.setModTime(new Date());
		table.setName("Table Name");
		//model.addAttribute("data", result);
		 model.addAttribute("table", table);
		 model.addAttribute("mode", NEWTABLE);
		 return "index";
		
	}
	@PostMapping(path="save-table")
	public String saveTable(@ModelAttribute("table") @Valid MTable table, 
			BindingResult binding,
			Model model
			){
		//request.setAttribute("tasks", tasksService.findAll());
		if(binding.hasErrors()){
			model.addAttribute("mode",UPDATETABLE);
			return "index";
		}
		if(table.getId()==0){
		  mtableService.createMTable(table.getName(), table.isSelected());
		}else {
			mtableService.updateMTable(table.getId(), table.getName(), table.isSelected());
		}
		
		List<MTable> tables = mtableService.findAllMTables();
		
		 updateTablesModel(model, TABLES, tables);
		return "index";
	}
	@GetMapping(path="delete-table")
	public String deleteTable(
			@RequestParam(name="id") int id,
			Model model){
		
		mtableService.deleteMTable(id);
		
		List<MTable> tables = mtableService.findAllMTables();
		
		 updateTablesModel(model, TABLES, tables);
		return "index";
	}
	
	
	@PostMapping(path="manage-rows/filter",consumes="application/x-www-form-urlencoded")
	public String rowsFilter(
			/*
			@RequestParam(name="startDate",defaultValue="") @DateTimeFormat(pattern="dd/MM/yyyy") Date startDate,
			@RequestParam(name="endDate",defaultValue="") @DateTimeFormat(pattern="dd/MM/yyyy") Date endDate,
			@RequestParam(name="catWord",defaultValue="") String catWord,
			@RequestParam(name="noteWord",defaultValue="") String noteWord,
			*/
			@RequestBody String body,
			HttpServletRequest request,
			Model model
			){
		
      	
	  
	   Set<String> filterParsToBeAdded= new HashSet<>();
	   List<String> filterPars =   Arrays.asList("startDate","endDate","catWord","noteWord");
	   
	   /*
	   if(startDate !=null) filterParsToBeAdded.add("startDate");
	   if(endDate !=null) filterParsToBeAdded.add("endDate");
	   if(!catWord.isEmpty()) filterParsToBeAdded.add("catWord");
	   if(!noteWord.isEmpty()) filterParsToBeAdded.add("noteWord");
	   */
	   String path = request.getRequestURL().toString();
	   String baseUrl = path.substring(0,path.indexOf("filter")-1);
	   
	   Enumeration<String> pars = request.getParameterNames();
	   if(pars.hasMoreElements()){
		   baseUrl=baseUrl+"?";
	   }
	   //String query = request.getQueryString();
	   UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl);
	   while(pars.hasMoreElements()) {
		   String key = pars.nextElement();
		   /*
		   if(filterPars.contains(key) && (!filterParsToBeAdded.contains(key))){
			   continue; //skip it
		   }*/
			      
		   /**
		    * protection: it might happen key == 'null', skip it!
		    */
		 if(key.equals("null")) continue;  
		builder.queryParam(key,request.getParameter(key) );
	}
	   String npath = builder.build().encode().toUriString();
	   
	    /*model.addAttribute("result", 
				String.format("startDate=%s, endDate=%s, catWord=%s, noteWord=%s, queryString=%s",
						startDate,endDate,catWord,noteWord,query));
		return "dummy";*/
	   return "redirect:"+npath;
	}
	
	@GetMapping("filter-reset")
	public String resetFilters(
			HttpServletRequest request,
			Model model){
		
		
		String queryString = request.getQueryString();
		System.out.println("Query string= "+queryString);
		model.addAttribute("startDate", null);
		model.addAttribute("endDate", null);
		 model.addAttribute("catWord", "");
		model.addAttribute("noteWord", "");
		model.addAttribute("infoText", "Filters and orders keys are reset..."); 
		//System.out.println(String.format("dateOrder= %s , catOrder= %s", dateOrder,catOrder));
		//keepRequestsOrderParameters(dateOrder, catOrder);
		String par1=null,par2=null; 
		String qstring = "";
		if(queryString.contains("dateOrder")){
			par1= request.getParameter("dateOrder");
			qstring+="?dateOrder="+par1;
		}
		if(queryString.contains("catOrder")){
			par2= request.getParameter("catOrder");
			qstring+="&catOrder="+par2;
		}
		String nquery = (qstring==null)?"":qstring;
		return "redirect:manage-rows"+nquery;
	}
	
	
	
	@RequestMapping(path="manage-rows", method=RequestMethod.GET)
	public String allRowsForTable(	
			@RequestParam(name="startDate",defaultValue="") @DateTimeFormat(pattern="dd/MM/yyyy") Date startDate,
			@RequestParam(name="endDate",defaultValue="") @DateTimeFormat(pattern="dd/MM/yyyy") Date endDate,
			@RequestParam(name="catWord",defaultValue="") String catWord,
			@RequestParam(name="noteWord",defaultValue="") String noteWord,
			@RequestParam(name="dateOrder",defaultValue="asc") String dateOrder,
			@RequestParam(name="catOrder",defaultValue="") String catOrder,
			
			Model model ){
		
		
		
		long id = mtableService.getSelectedTableId();
		if(id <= 0){
			model.addAttribute("errMsg","Table is not selected! Please, select a table.");
			/*Iterable<MTable> result = mtableService.findAllMTables();
			 
			 model.addAttribute("selectedTable",mtableService.getSelectedTableId());
			 model.addAttribute("tables", result);
			 model.addAttribute("mode",TABLES);*/
			allTables(model);
			return "index";
		}
		
		 List<TableData> rows = mtableService.findAllRowsForTable(id);
		  
		 List<TableData> rowsf=null;
		try {
			rowsf = makeFilter(FilterInfo.create(startDate, endDate, catWord, noteWord),rows);
		} catch (ParseException e) {
			Logger.getLogger(MTableController.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			rowsf= rows;
		}
		 
		 Map<String,String> orders= new HashMap<>();
		 if(dateOrder!=null){
			 orders.put("dateOrder", dateOrder);
			 
			 rowsf.sort(comparatorForCDate((dateOrder.equals("asc")?Sort.Direction.ASC:Sort.Direction.DESC)));
		 }
		 if(!catOrder.isEmpty()){
			 orders.put("catOrder", catOrder);
			 
			 rowsf.sort(comparatorForCat((catOrder.equals("asc")?Sort.Direction.ASC:Sort.Direction.DESC)));
		 }
		 //bind orders map to model
		 
		 for (String key : orders.keySet()) {
			model.addAttribute(key, orders.get(key));
		}
		 //bind filter params to model
		 SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
		 //Strange, but I have to put date as formated text String into model.
		 //If i pass date as  Date type it will been shown in unformatted style 
		 //even I have data-date-format="dd/mm/yyyy" for datePicker
		 //In case of th:field in form it works properly as expected.
		 
		 if(startDate!=null) model.addAttribute("startDate", sf.format(startDate));
		 if(endDate!=null) model.addAttribute("endDate", sf.format(endDate));
		 if(!catWord.isEmpty()) model.addAttribute("catWord", catWord);
		 if(!noteWord.isEmpty()) model.addAttribute("noteWord", noteWord);
		 updateRowsModel(model, ROWS, rowsf);
		 return "rows";
		
	}
	
	private List<TableData> makeFilter(FilterInfo filter,
			List<TableData> rows) throws ParseException {
		
		Date startDate = filter.startDateAsDate(); Date endDate=filter.endDateAsDate(); 
		String catWord= filter.getCatWord(); String noteWord= filter.getNoteWord();
		
		List<TableData> filteredRows= new ArrayList<>();
		for(TableData row: rows){
			if(likeDate(startDate,endDate,row) && 
					likeCat(catWord,row)&&
					likeNote(noteWord,row)){
				filteredRows.add(row);
			}
		}
		return filteredRows;
	}


	private boolean likeNote(String noteWord, TableData row) {
		if(noteWord.isEmpty()) return true;
		return row.getNote().toUpperCase().contains(noteWord.toUpperCase());
	}


	private boolean likeCat(String catWord, TableData row) {
		if(catWord.isEmpty()) return true;
		return row.getCat().toUpperCase().contains(catWord.toUpperCase());
	}


	private boolean likeDate(Date startDate, Date endDate, TableData row) {
		if(startDate==null && endDate==null) return true;
		if(startDate==null){
			if(row.getCdate().compareTo(endDate)<=0) return true;
		}
		if(endDate==null){
			if(row.getCdate().compareTo(startDate)>=0) return true;
		}
		if(startDate==null || endDate == null) return false;
		return (row.getCdate().compareTo(startDate)>=0) && (row.getCdate().compareTo(endDate)<=0);
	}


	//----------------------ROWS operations -------------------------//
	
	@RequestMapping(path="{id}/rows", method=RequestMethod.POST)
	public String addRowForTable(
			@PathVariable(name="id") int id,
			@RequestBody TableData data,
			Model model){
		TableData rrow = mtableService.addRowForTable(id, data);
		List<TableData> result = mtableService.findAllRowsForTable(id);
		//List<TableData> result = table.getData();
		//model.addAttribute("data", result);
		model.addAttribute("resultLst", result);
		 return "dummy";
		
	}
	
	@RequestMapping(path="{id}/rows/{rowId}", method=RequestMethod.GET)
	public String oneRowForTable( 
			@PathVariable(name="id") int id,
			@PathVariable(name="rowId") int rowid,
			Model model){
		
		TableData result = mtableService.findRowForTableById( id, rowid);
		//model.addAttribute("data", result);
		model.addAttribute("result", result);
		 return "dummy";
		
	}
	@RequestMapping(path="{id}/rows/{rowId}", method=RequestMethod.PUT)
	public String oneRowForTableUpdate( 
			@PathVariable(name="id") int id,
			@PathVariable(name="rowId") int rowid,
			@RequestBody  TableData data,
			Model model){
		
		TableData result = mtableService.updateRowForTable(id, data);
		
		
		//model.addAttribute("data", result);
		model.addAttribute("result", result);
		 return "dummy";
		
	}

	
	//------rows update operations -------//
	@GetMapping(path="{id}/rows/delete")
	public String oneRowForTableDelete( 
			@PathVariable(name="id") int id,
			@RequestParam(name="rowId") int rowid,
			Model model){
		
		MTable result = mtableService.deleteRowForTable(id, rowid);
		String txt= "Row deleted. "+SORTFILTERRESETMSG;
		model.addAttribute("infoText", txt);
		 updateRowsModel(model, ROWS,result.getData());
		 return "rows";
		
	}
	
	private double calcSum(List<TableData> rows){
		double sum= 0.0;
		 for (TableData tableData : rows) {
			sum= sum+tableData.getAmount();
		}
		 return sum;
	}
	
	private void updateRowsModel(Model model, String mode, List<TableData> result) {
		model.addAttribute("selectedTable",mtableService.getSelectedTableId());
		model.addAttribute("tableName", mtableService.getSelectedTableName());
		model.addAttribute("mode", mode);
		//model.addAttribute("dateOrder","desc");
		 model.addAttribute("rows", result);
		 double sum= calcSum(result);
		 
		 model.addAttribute("Sum", sum);
			
	}
	
	private void updateRowsModelPaginationWithOrderingFiltering(
			Model model, String mode, Page<TableData> page,FilterInfo filter, double sum
			) {
		model.addAttribute("selectedTable",mtableService.getSelectedTableId());
		model.addAttribute("tableName", mtableService.getSelectedTableName());
		model.addAttribute("mode", mode);
		 model.addAttribute("rows", page.getContent());
		 PageInfo pageInfo = new PageInfo(page);
		 pageInfo.setFilterString(filter.filterAsParametersString());
		 model.addAttribute("page", pageInfo);
		 model.addAttribute("pageTest", page); //for test
		 //Add filter
		 model.addAttribute("filter", filter);
		 model.addAttribute("Sum", sum);
		 Sort sort= page.getSort();
		 String dateOrder= null,catOrder=null;
		 if(sort!=null){
			 if(sort.getOrderFor("cdate")!=null)
				 dateOrder = sort.getOrderFor("cdate").getDirection().toString();
			 if(sort.getOrderFor("cat")!=null)
				 catOrder = sort.getOrderFor("cat").getDirection().toString();
		 }
		 model.addAttribute("dateOrder", ((dateOrder!=null) ? dateOrder:null));
		 model.addAttribute("catOrder", ((catOrder!=null) ? catOrder:null));
		
	}
	private void updateRowsModelPagination(Model model, String mode, Page<TableData> page,double sum) {
		model.addAttribute("selectedTable",mtableService.getSelectedTableId());
		model.addAttribute("tableName", mtableService.getSelectedTableName());
		model.addAttribute("mode", mode);
		 model.addAttribute("rows", page.getContent());
		 model.addAttribute("page", page);
		 model.addAttribute("Sum", sum);
		 
	}

	@GetMapping(path="{id}/rows/update")
	public String oneRowForTableUpdate( 
			@PathVariable(name="id") int id,
			@RequestParam(name="rowId") int rowid,
			Model model){
		
		TableData result = mtableService.findRowForTableById(id, rowid);
		
		model.addAttribute("mode", UPDATEROW);
		 model.addAttribute("row", result);
		 return "rows";
		
	}
	@GetMapping(path="{id}/rows/new")
	public String newRowForTable( 
			@PathVariable(name="id") int id,
			Model model){
		
		TableData result = new TableData(new Date(),"",0.0,"");
		
		model.addAttribute("mode", NEWROW);
		 model.addAttribute("row", result);
		 return "rows";
		
	}
	
	@PostMapping(path="{id}/rows/save-row")
	public String oneRowForTableSave(
			@PathVariable(name="id") int id,
			@ModelAttribute("row") @Valid TableData row,
			 BindingResult binding,
			 Model model
			){
		if(binding.hasErrors()){
			/*Map<String, Object> mmap = model.asMap();
			System.out.println("mode= "+mmap.get("mode"));
			System.out.println("row= "+mmap.get("row"));*/
			model.addAttribute("mode", UPDATEROW);
			return "rows";
		}
		long tableId = mtableService.getSelectedTableId();
		
		String txt;
		if(row.getId()<=0){
			mtableService.addRowForTable(tableId, row);
			txt= "Row Added. ";
		}else{
			mtableService.updateRowForTable(tableId, row);
			txt="Row updated. ";
		}
		 
		List<TableData> result = mtableService.findAllRowsForTable(tableId);
		 
		txt= txt+SORTFILTERRESETMSG;
		model.addAttribute("infoText", txt);
		 updateRowsModel(model, ROWS, result);
		 return "rows";
	}
	//----------------------END of ROWS operations -------------------------//

	
	//------------JS dataTable lib usage ------/
	private static String DT_NEW= "/mtables/tabledata/new";
	private static String DT_JROWS= "/mtables/tabledata/jrows";
	private static String DT_UPDATE= "/mtables/tabledata/update/";
	
	  private boolean isTableSelected(Model model){
		  long id = mtableService.getSelectedTableId();
			if(id <= 0){
				model.addAttribute("errMsg","Table is not selected! Please, select a table.");
				allTables(model);
				return false;
			}
			return true;
	  }
	  
		@GetMapping("tabledata")
		public String getJsonTableData(Model model){
			
			if(!isTableSelected(model)) return "index";
			long id = mtableService.getSelectedTableId();
			 List<TableData> rows = mtableService.findAllRowsForTable(id);
			
			 updateRowsModelTableData(model,ROWS,rows);
			 return "tabledata";
			
		}

		
		private void updateRowsModelTableData(Model model, String mode, List<TableData> rows) {
			model.addAttribute("DTNEW", DT_NEW);
			model.addAttribute("DTJROWS", DT_JROWS);
			model.addAttribute("DTUPDATE", DT_UPDATE);
			 updateRowsModel(model, ROWS, rows);
			
		}


		@RequestMapping(path="tabledata/jrows", method=RequestMethod.GET)
		public ResponseEntity<TableDataList> allRowsForTableJson(Model model){
			long id = mtableService.getSelectedTableId();
			 List<TableData> rows = mtableService.findAllRowsForTable(id);
			
			 TableDataList data = new TableDataList(rows);
			 
			return new ResponseEntity<>(data,HttpStatus.OK);
			
		}
		
		@GetMapping("tabledata/jrows/{rid}")
	    public String deleteRowForTable(
	    		@PathVariable(name="rid") int rowid,
	    		Model model
	    		){
			long id = mtableService.getSelectedTableId();
			MTable table = mtableService.deleteRowForTable(id, rowid);
			if(table==null) {
				model.addAttribute("errText", "Error to delete!");
			}else {
			model.addAttribute("infoText", "Row deleted!");
			}
			
			 List<TableData> rows = mtableService.findAllRowsForTable(id);
			 
			 updateRowsModelTableData(model,ROWS,rows);
			 return "tabledata";
			
		}
		@GetMapping("tabledata/update/{rid}")
	    public String updateRowForTable(
	    		@PathVariable(name="rid") int rowid,
	    		Model model
	    		){
			long id = mtableService.getSelectedTableId();
			TableData result = mtableService.findRowForTableById(id, rowid);
			model.addAttribute("row", result);
			model.addAttribute("mode", UPDATEROW);
			model.addAttribute("saverow", "/mtables/tabledata/save");
			return "tabledata";
			
		}
		@GetMapping("tabledata/new")
		public String newRowForTable(Model model){
			long id = mtableService.getSelectedTableId();
			model.addAttribute("row", new TableData(new Date(), "", 0.0, ""));
			model.addAttribute("mode", NEWROW);
			model.addAttribute("saverow", "save");
			return "tabledata";
		}
		@PostMapping("tabledata/save")
		public String saveRowForTable(
				@ModelAttribute("row") TableData row,
				Model model
				){
			long id = mtableService.getSelectedTableId();
			TableData result;
			String text;
			if(row.getId()<=0){
			  result= mtableService.addRowForTable(id, row);
			  if(result==null) { 
				    text = "Error to add new row!";
					model.addAttribute("errText", text);
					}else{
						model.addAttribute("infoText", "Row added!");
					}
			}else{
				result= mtableService.updateRowForTable(id, row);
				if(result==null) { 
				    text = "Error to update row!";
					model.addAttribute("errText", text);
					}else{
						model.addAttribute("infoText", "Row updated!");
					}
				}
			
			 List<TableData> rows = mtableService.findAllRowsForTable(id);
			 
			 updateRowsModelTableData(model,ROWS,rows);
			 return "tabledata";
			
		}
		//------------End dataTable lib usage------/

	
	private Comparator<TableData> comparatorForCDate(final Direction order) {
		return new Comparator<TableData>() {
			@Override
			public int compare(TableData o1, TableData o2) {
				return order == Direction.ASC ? 
						o1.getCdate().compareTo(o2.getCdate()) : - o1.getCdate().compareTo(o2.getCdate());
			}
		};
	}
	
	private Comparator<TableData> comparatorForCat(Direction order) {
		
		return new Comparator<TableData>() {

			@Override
			public int compare(TableData o1, TableData o2) {
				return order == Direction.ASC ? 
						o1.getCat().compareTo(o2.getCat()) : - o1.getCat().compareTo(o2.getCat());
			}
		};
	}
	
    private Comparator<TableData> comparatorForNote(Direction order) {
		
		return new Comparator<TableData>() {

			@Override
			public int compare(TableData o1, TableData o2) {
				return order == Direction.ASC ? 
						o1.getNote().compareTo(o2.getNote()) : - o1.getNote().compareTo(o2.getNote());
			}
		};
	}

    //----------------------------Pagination and filtering -------------------------//
    

	@PostMapping(path="pagerows/filter",consumes="application/x-www-form-urlencoded")
	public String pagerowsFilter(
			
			@ModelAttribute FilterInfo filter,
			@RequestParam(name="startDate",defaultValue="") @DateTimeFormat(pattern="dd/MM/yyyy") Date startDate,
			@RequestParam(name="endDate",defaultValue="") @DateTimeFormat(pattern="dd/MM/yyyy") Date endDate,
			@RequestParam(name="catWord",defaultValue="") String catWord,
			@RequestParam(name="noteWord",defaultValue="") String noteWord,
			@RequestParam(name="pageInfo",defaultValue="") String pageInfo,
			HttpServletRequest request,
			Model model
			){
		
        String pageStr = pageInfo;
        
        
		//String query = request.getQueryString();  	
	   Map<String,String> filterParsToBeAdded= new HashMap<>();
	   
	   SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
	   if(startDate !=null) filterParsToBeAdded.put("startDate",sf.format(startDate));
	   if(endDate !=null) filterParsToBeAdded.put("endDate",sf.format(endDate));
	   if(!catWord.isEmpty()) filterParsToBeAdded.put("catWord",catWord);
	   if(!noteWord.isEmpty()) filterParsToBeAdded.put("noteWord",noteWord);
	   
	    StringBuffer path = request.getRequestURL();
	    
	    path = new StringBuffer("/mtables/pagerows");
	    if(pageStr!=null && !pageStr.isEmpty() ) path.append('?');
	    if(pageStr!=null) path.append(pageStr);
	    if((pageStr==null || pageStr.isEmpty()) && !filterParsToBeAdded.isEmpty()) path.append('?');
	   
	    for(String key: filterParsToBeAdded.keySet()){
	    		path.append('&').append(key).append('=').append(filterParsToBeAdded.get(key));
	    }
	    
	   
	   
	    return "redirect:"+path;
	}
	
	
	@GetMapping("pagerows/filter-reset")
	public String pageRowsResetFilters(
			HttpServletRequest request,
			Model model
			){
		
		FilterInfo nfilter = FilterInfo.create();
		model.addAttribute("filter", nfilter);
		 String query = request.getQueryString();
		
		 StringBuffer path = new StringBuffer("/mtables/pagerows");
		    if(query!=null && !query.isEmpty() ) path.append('?').append(query);
		    
		     
		
		return "redirect:"+path.toString();
	}
	
	
	//For page request like '/pagerows?page=0&size=10&sort=cdate,desc&sort=cat,asc&sort=note,desc'
		//Request parameters we do not need if we use Pageable
		//Spring will bind it automatically
		
		@RequestMapping(path="pagerows", method=RequestMethod.GET)
		public String pageOfRowsForTableWithFilter(
				//@RequestParam("page") int pageOffset,
				//@RequestParam("size") int pageSize,
				Pageable pageable,
				@RequestParam(name="startDate",defaultValue="") @DateTimeFormat(pattern="dd/MM/yyyy") Date startDate,
				@RequestParam(name="endDate",defaultValue="") @DateTimeFormat(pattern="dd/MM/yyyy") Date endDate,
				@RequestParam(name="catWord",defaultValue="") String catWord,
				@RequestParam(name="noteWord",defaultValue="") String noteWord,
				
				Model model ){
			
			long id = mtableService.getSelectedTableId();
			if(id <= 0){
				model.addAttribute("errMsg","Table is not selected! Please, select a table.");
				
				allTables(model);
				return "index";
			}
			 
			 //Page<TableData> prows = mtableService.findPageOfRowsForTable(id, pageable);
			MTable table = mtableService.findMTableById(id);
			
			List<TableData> data = table.getData();
			FilterInfo filter = FilterInfo.create(startDate, endDate, catWord, noteWord);
			 try {
				data= makeFilter(filter,data);
			} catch (ParseException e) {
				Logger.getLogger(MTableController.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			}
			//stuff with sorting
			if(pageable.getSort()!=null){
			Iterator<Order> iterator = pageable.getSort().iterator();
			while(iterator.hasNext()){
				Order sortProp = iterator.next();
				String sprop = sortProp.getProperty();
				Direction sdir = sortProp.getDirection();
				if(sprop.equalsIgnoreCase("cdate")){
					data.sort(comparatorForCDate(sdir));
				}
				if(sprop.equalsIgnoreCase("cat")){
					data.sort(comparatorForCat(sdir));
				}
				if(sprop.equalsIgnoreCase("note")){
					data.sort(comparatorForNote(sdir));
				}
			}
			}
			//stuff with sorting
			//page number 0...n
			//protect against wrong page offset by PREV NEXT request from client side
			int lastPageNumber= data.size()/pageable.getPageSize()+((data.size()%pageable.getPageSize()>0)?1:0)-1;
			if(lastPageNumber<0) lastPageNumber=0; //lower border
			int pn= pageable.getPageNumber(); //current page number
			pn= (pn < 0)? 0 : pn; //lower border
			pn= (pn>lastPageNumber)? lastPageNumber : pn; //upper boder
			
			//if(filter.isFilterActive()) pn=0;
            int po= pn*pageable.getPageSize(); //get item's offset in page number pn		
			//calculate page start and end offsets
			int start = po; //start offset
			 int end = (start + pageable.getPageSize()) > data.size() ? data.size() : (start + pageable.getPageSize());
			//To make new pageable use PageRequest class, we do it if we need to change some pageable paramaters 
			PageRequest pageReq = new PageRequest(pn,pageable.getPageSize(),pageable.getSort());
			//To make new page use PageImpl, we create page with items sublist and page requested.
			 Page<TableData> pages = new PageImpl<>(data.subList(start, end), pageReq, data.size());
			 
			 //calcSum for all data rows
			 
			 updateStatusModel(model);
			 updateRowsModelPaginationWithOrderingFiltering(model, ROWS, pages,filter, calcSum(data));
			 return "pagerows";
			
		}
	
		//-----Experimental---------------Rows update operations with pagination (Filtering reset)---/
		
		@GetMapping(path="pagerows/{id}/rows/delete")
		public String oneRowForTableDeletePaging( 
				@PathVariable(name="id") int id,
				@RequestParam(name="rowId") int rowid,
				Pageable page,
				@RequestParam(name="startDate",defaultValue="") @DateTimeFormat(pattern="dd/MM/yyyy") Date startDate,
				@RequestParam(name="endDate",defaultValue="") @DateTimeFormat(pattern="dd/MM/yyyy") Date endDate,
				@RequestParam(name="catWord",defaultValue="") String catWord,
				@RequestParam(name="noteWord",defaultValue="") String noteWord,
				
				Model model){
			
			MTable result = mtableService.deleteRowForTable(id, rowid);
			String txt= "Row deleted. "; //+FILTERRESETMSG;
		
			//model.addAttribute("infoText", txt);
			// updateRowsModel(model, ROWS,result.getData());
			// return "pagerows";
			FilterInfo filter = FilterInfo.create(startDate, endDate, catWord, noteWord);
			PageInfo pageInfo = new PageInfo(page.getPageNumber(),page.getPageSize());
			pageInfo.setOrderString(pageInfo.composeOrderString(page.getSort()));
			pageInfo.setFilterString(filter.filterAsParametersString());
			String pageParms = pageInfo.pageAsParametersString()+pageInfo.getFilterString();
			addStatusInfo(new StatusInfo(StatusType.INFO, txt));
			return "redirect:/mtables/pagerows?"+pageParms;
		
		}

		@GetMapping(path="pagerows/{id}/rows/new")
		public String newRowForTablePaging( 
				@PathVariable(name="id") int id,
				Pageable page,
				Model model){
			
			TableData result = new TableData(new Date(),"",0.0,"");
			PageInfo pageInfo = new PageInfo(page.getPageNumber(),page.getPageSize());
			pageInfo.setOrderString(pageInfo.composeOrderString(page.getSort()));
			model.addAttribute("pageInfo", pageInfo);
			model.addAttribute("page", pageInfo);
			model.addAttribute("selectedTable", id);
			
			model.addAttribute("mode", NEWROW);
			 model.addAttribute("row", result);
			 return "pagerows";
			
		}

		
		@GetMapping(path="pagerows/{id}/rows/update")
		public String oneRowForTableUpdatePaging( 
				@PathVariable(name="id") int id,
				@RequestParam(name="rowId") int rowid,
				Pageable page,
				@RequestParam(name="startDate",defaultValue="") @DateTimeFormat(pattern="dd/MM/yyyy") Date startDate,
				@RequestParam(name="endDate",defaultValue="") @DateTimeFormat(pattern="dd/MM/yyyy") Date endDate,
				@RequestParam(name="catWord",defaultValue="") String catWord,
				@RequestParam(name="noteWord",defaultValue="") String noteWord,
			
				Model model){
			
			
			TableData result = mtableService.findRowForTableById(id, rowid);
			PageInfo pageInfo = new PageInfo(page.getPageNumber(),page.getPageSize());
			pageInfo.setOrderString(pageInfo.composeOrderString(page.getSort()));
			FilterInfo filter = FilterInfo.create(startDate, endDate, catWord, noteWord);
			pageInfo.setFilterString(filter.filterAsParametersString());
			model.addAttribute("pageInfo", pageInfo);
			model.addAttribute("page", pageInfo);
			model.addAttribute("selectedTable", id);
			model.addAttribute("mode", UPDATEROW);
			 model.addAttribute("row", result);
			 return "pagerows";
			
		}
		

		
		@PostMapping(path="pagerows/{id}/rows/save-row")
		public String oneRowForTableSavePaging(
				@PathVariable("id") int tableid,
				@RequestParam("pageParms") String pageParms,
				@ModelAttribute("row") @Valid TableData row,
				 BindingResult binding,
				 Model model
				){
			if(binding.hasErrors()){
				/*Map<String, Object> mmap = model.asMap();
				System.out.println("mode= "+mmap.get("mode"));
				System.out.println("row= "+mmap.get("row"));*/
				model.addAttribute("mode", UPDATEROW);
				return "pagerows";
			}
			long tableId = mtableService.getSelectedTableId();
			
			String txt;
			if(row.getId()<=0){
				mtableService.addRowForTable(tableId, row);
				txt= "Row Added. "+FILTERRESETMSG;
			}else{
				mtableService.updateRowForTable(tableId, row);
				txt="Row updated. ";
			}
			 
			//List<TableData> result = mtableService.findAllRowsForTable(tableId);
			 
			/***
			txt= txt+SORTFILTERRESETMSG;
			model.addAttribute("infoText", txt);
			 updateRowsModel(model, ROWS, result);
			 return "pagerows";
			 ***/
			
			addStatusInfo(new StatusInfo(StatusType.INFO, txt));
			return "redirect:/mtables/pagerows?"+pageParms;
		}
		
		//----- status information ----/
		private void addStatusInfo(StatusInfo info){
			if(statusInfoList==null){
				statusInfoList = new LinkedList<StatusInfo>();
			}
			statusInfoList.add(info);
		}
		private StatusInfo takeStatusInfo(){
			if(statusInfoList!=null){
				
				return statusInfoList.pollFirst();
			}
			return null;
		}
		private void updateStatusModel(Model model){
			if(statusInfoList==null || statusInfoList.isEmpty()) return;
			StatusInfo statusInfo= takeStatusInfo();
			if(statusInfo.getType()==StatusType.INFO){
			model.addAttribute("infoText", statusInfo.getMessage());
			return;
			}
			if(statusInfo.getType()==StatusType.ERROR){
				model.addAttribute("errText", statusInfo.getMessage());
			return;
			}
		}
			
		//----------------------------END of Pagination and filtering -------------------------//
	    
	

}
