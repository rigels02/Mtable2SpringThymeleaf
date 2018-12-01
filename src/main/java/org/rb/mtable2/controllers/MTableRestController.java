package org.rb.mtable2.controllers;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.rb.mtable2.model.MTable;
import org.rb.mtable2.model.TableData;
import org.rb.mtable2.services.MTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tables")
@CrossOrigin(origins = "*",allowedHeaders = "*")

public class MTableRestController {

	
	@Autowired
    private MTableService mtableService;

    @RequestMapping(path ="/dummy" ,method = RequestMethod.GET)
    public ResponseEntity<MTable> getDummy(){
    	MTable table = new MTable();
        table.setName("DummyTable");
        table.setModTime(new Date());
        table.getData().add(new TableData(new Date(),"DummyCat1",11.0,"DummyNote1"));
        table.getData().add(new TableData(new Date(),"DummyCat2",22.0,"DummyNote2"));
        table.getData().add(new TableData(new Date(),"DummyCat3",33.0,"DummyNote3"));
        
       return new ResponseEntity<>(table, HttpStatus.OK);
    }
    
    
    
    @RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<Iterable<MTable>> allTables(){
	 Iterable<MTable> result = mtableService.findAllMTables();
	
	return new ResponseEntity<>(result,HttpStatus.OK);
	}
	
	
	@RequestMapping(path="{id}", method=RequestMethod.GET)
	public ResponseEntity<MTable> oneTable(@PathVariable(name="id") int id){
		MTable result = mtableService.findMTableById(id);
		if(result==null){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(result,HttpStatus.OK);
	}
	
	@PostMapping()
	public ResponseEntity<MTable> newTable(@RequestBody @Valid MTable table, BindingResult binding){
		if(binding.hasErrors()){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		MTable result = mtableService.postMTable(table);
		
		return new ResponseEntity<>(result,HttpStatus.CREATED);
	}
	
	@PutMapping(path="{id}")
	public ResponseEntity<MTable> updateTable(
			@PathVariable(name="id") int id,
			@RequestBody @Valid MTable table, 
			BindingResult binding){
		if(binding.hasErrors()){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		MTable exist = mtableService.findMTableById(id);
		if(exist == null){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		MTable result = mtableService.putMTable(table);
		return new ResponseEntity<>(result,HttpStatus.OK);
	}
	
	@DeleteMapping(path="{id}")
	public ResponseEntity<HttpStatus> deleteTable(	@PathVariable(name="id") int id)
			{
		MTable exist = mtableService.findMTableById(id);
		if(exist == null){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		 mtableService.deleteMTable(id);;
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@DeleteMapping()
	public ResponseEntity<HttpStatus> deleteAllTables(){
		mtableService.deleteAllTables();
		return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
	}
	
	//----------------------ROWS operations -------------------------//
	
	@RequestMapping(path="{id}/rows", method=RequestMethod.GET)
	public ResponseEntity<List<TableData>> findRowsForTable(	@PathVariable(name="id") int id ){
		
		MTable table = mtableService.findMTableById(id);
		if(table == null){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(table.getData(),HttpStatus.OK);
		
	}
	
	@RequestMapping(path="{id}/rows/{rowId}", method=RequestMethod.GET)
	public ResponseEntity<TableData> oneRowForTable( 
			@PathVariable(name="id") int id,
			@PathVariable(name="rowId") int rowid
			){
		
		TableData row = mtableService.findRowForTableById( id, rowid);
		if(row == null){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(row,HttpStatus.OK);
		
	}
	@RequestMapping(path="{id}/rows/{rowId}", method=RequestMethod.PUT)
	public  ResponseEntity<TableData> oneRowForTableUpdate( 
			@PathVariable(name="id") int id,
			@PathVariable(name="rowId") int rowid,
			@RequestBody @Valid TableData data,
			BindingResult binding
			){
		
		if(binding.hasErrors()){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		MTable table = mtableService.findMTableById(id);
		if(table == null){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		TableData result = mtableService.updateRowForTable(id, data);
		return new ResponseEntity<>(result,HttpStatus.OK);
	}
	
	@RequestMapping(path="{id}/rows", method=RequestMethod.POST)
	public  ResponseEntity<TableData> oneRowForTableAdd( 
			@PathVariable(name="id") int id,
			@RequestBody @Valid TableData data,
			BindingResult binding
			){
		
		if(binding.hasErrors()){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		MTable table = mtableService.findMTableById(id);
		if(table == null){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		TableData result = mtableService.addRowForTable(id, data);
		return new ResponseEntity<>(result,HttpStatus.CREATED);
	}
	
	@DeleteMapping(path="{id}/rows/{rid}")
	public  ResponseEntity<HttpStatus> oneRowForTableDelete( 
			@PathVariable(name="id") int id,
			@PathVariable(name="rid") int rid
			){
		
		MTable table = mtableService.findMTableById(id);
		if(table == null){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		
		MTable result = mtableService.deleteRowForTable(id, rid);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
