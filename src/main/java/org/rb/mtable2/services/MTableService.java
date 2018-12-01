package org.rb.mtable2.services;

import java.util.Date;
import java.util.List;

import org.rb.mtable2.model.MTable;
import org.rb.mtable2.model.TableData;
import org.rb.mtable2.repositories.IMTableJPARepository;
import org.rb.mtable2.repositories.IMtableCrudRepository;
import org.rb.mtable2.repositories.ITableDataCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class MTableService {

	@Autowired
	private IMtableCrudRepository crudMtableRepo;
	
	@Autowired
	private ITableDataCrudRepository crudTableDataRepo;

	//for pagination service
	@Autowired
	private IMTableJPARepository crudMtableJPA;
	
	private long selectedTableId;

	private String selectedTableName;

	
	
	public List<MTable> findAllMTables(){
		
		List<MTable> tables = (List<MTable>) crudMtableRepo.findAll();
		/**
		 * set selectedTableId from DB info.
		 * The idea is that only one table may be selected at the time.
		 */
		if(selectedTableId <= 0){
			for (MTable mTable : tables) {
				if(mTable.isSelected()){
				 selectedTableId = mTable.getId();
				 selectedTableName= mTable.getName();
				 break;
				}
			}
		}else {
			/**
			 * Table selection was changed by table create or table update operations.
			 * Make check and clean tables in such a way that only table with selectedTableId
			 * is selected table but others not selected.
			 */
		for (MTable mTable : tables) {
			if(mTable.isSelected() && (mTable.getId()!= selectedTableId)){
				
				mTable.setSelected(false);
				selectedTableName= mTable.getName();
			}
		}
		}
		return tables;
		
	}
	
	public long getSelectedTableId() {
		return selectedTableId;
	}

	public String getSelectedTableName(){
		return selectedTableName;
	}
	public MTable findMTableById(long id){
		
		return crudMtableRepo.findOne( id);
	}
	public MTable createMTable(String name, boolean selected){
		MTable table = new MTable();
		table.setModTime(new Date());
		table.setName(name);
		table.setSelected(selected);
		if(selected) {
			selectedTableId = table.getId();
			selectedTableName= table.getName();
		}
		return crudMtableRepo.save(table);
		
	}
	
	public MTable postMTable(MTable table){
		return crudMtableRepo.save(table);
	}
	public MTable putMTable(MTable table){
		return crudMtableRepo.save(table);
	}
	public void deleteAllTables(){
		crudMtableRepo.deleteAll();
	}
	
	public MTable updateMTable(long id,String name, boolean selected){
		MTable table = crudMtableRepo.findOne(id);
		
		table.setName(name);
		table.setSelected(selected);
		if(selected) {
			selectedTableId = table.getId();
			selectedTableName= table.getName();
		}
		table.setModTime(new Date());
		return crudMtableRepo.save(table);
	}
	public void deleteMTable(long id){
		crudMtableRepo.delete(id);
		
	}
	//-------------Rows operations ---------------------//
	//@Cacheable
	public List<TableData> findAllRowsForTable(long id){
		
		MTable table = crudMtableRepo.findOne(id);
		
		return table.getData();
	}
   
	public Page<TableData> findPageOfRowsForTable(long id,Pageable pageable){
		
		MTable table = crudMtableRepo.findOne(id);
		List<TableData> data = table.getData();
		 int start = pageable.getOffset();
		 int end = (start + pageable.getPageSize()) > data.size() ? data.size() : (start + pageable.getPageSize());
		 Page<TableData> pages = new PageImpl<>(data.subList(start, end), pageable, data.size());
		
		return pages;
	}
	
	public TableData findRowForTableById(long tableId,long rowId){
		MTable table = crudMtableRepo.findOne(tableId);
		if(table == null) return null;
		TableData containsRow=null;
		for(TableData row: table.getData()){
			if(row.getId()==rowId){
				containsRow=row;
				break;
			}
		}
		if(containsRow==null) return null;
		return crudTableDataRepo.findOne( rowId);
	}
	public TableData addRowForTable(long id, TableData data){
		MTable table = crudMtableRepo.findOne(id);
		table.getData().add(data);
		table.setModTime(new Date());
		MTable result = crudMtableRepo.save(table);
		//System.out.println("newRow = "+result.getData().get(result.getData().size()-1));
		return result.getData().get(result.getData().size()-1);
	}
	/**
	 * Update existing row (TableData) in table.
	 * row must have a valid row's id (must not be modified)
	 * @param id table id
	 * @param newData modified existing data row (id must not be changed)
	 * @return
	 */
	public TableData updateRowForTable(long id,TableData newData){
		
		MTable table = crudMtableRepo.findOne(id);
		//if(table==null) return null;
		int idx=-1;
		for(int i=0; i<table.getData().size(); i++){
			if(table.getData().get(i).getId()== newData.getId()){
				
				table.getData().remove(i);
				table.getData().add(i, newData);
				idx= i;
				break;
			}
		}
		MTable result = crudMtableRepo.save(table);
		return result.getData().get(idx);
	}
	public MTable deleteRowForTable(long id, long rowId){
		MTable table = crudMtableRepo.findOne(id);
		for(int i=0; i<table.getData().size(); i++){
			if(table.getData().get(i).getId()== rowId){
				
				table.getData().remove(i);
				crudTableDataRepo.delete(rowId);
				break;
			}
		}
		return crudMtableRepo.save(table);
	}
	
}
