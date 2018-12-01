package org.rb.mtable2.model;

import java.util.List;

public class TableDataList {

	private List<TableData>data;

	public TableDataList() {
		
	}

	public TableDataList(List<TableData> data) {
		super();
		this.data = data;
	}

	public List<TableData> getData() {
		return data;
	}

	public void setData(List<TableData> data) {
		this.data = data;
	}
	
	
	
}
