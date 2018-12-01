package org.rb.mtable2.repositories;

import java.util.List;

import org.rb.mtable2.model.MTable;
import org.rb.mtable2.model.TableData;
import org.springframework.data.repository.CrudRepository;

public interface ITableDataCrudRepository extends CrudRepository<TableData, Long>{

	//List<TableData> findAllByTable(MTable table);
	//TableData findOneByTableAndId(MTable table,Long id);
}
