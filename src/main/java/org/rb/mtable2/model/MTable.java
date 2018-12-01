
package org.rb.mtable2.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.Valid;


/**
 * Created on 27-Feb-17.
 * @author raitis
 */
@Entity
@NamedQueries(
        {
    @NamedQuery(name = "MTable.findAll", query = "select p from MTable p"),
    @NamedQuery(name = "MTable.findAllById", query = "select p from MTable p where p.Id = :mid")
        
}
)
public class MTable extends TableInfo implements Serializable{
    
  
	private static final long serialVersionUID = 1L;
	//@ElementCollection
    @OneToMany(cascade = CascadeType.ALL /* , fetch=FetchType.EAGER*/ )
    @Valid
    private List<TableData> data= new ArrayList<>();

    public MTable() {
    }

   

    public List<TableData> getData() {
        return data;
    }

    public void setData(List<TableData> data) {
        this.data = data;
    }

    public long getId() {
        return Id;
    }

    public void setId(long Id) {
        this.Id = Id;
    }

    @Override
    public String toString() {
        return "MTable{"+super.toString()+", data=" + data + '}';
    }

    
   
    
}
