package org.rb.mtable2.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *Created on 27-Feb-17.
 * @author raitis
 */
@Entity
@Table(name = "MTable")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NamedQueries(
        {
    @NamedQuery(name = "TableInfo.findAll", query = "select p from TableInfo p"),
    @NamedQuery(name = "TableInfo.findAllById", query = "select p from TableInfo p where p.id = :id")
        
}
)
public class TableInfo implements Serializable{

    
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)        
    long Id;

@NotBlank
private String name;
@Temporal(TemporalType.TIMESTAMP)
@NotNull
@DateTimeFormat(pattern="dd/MM/yyyy HH:mm")
private Date modTime;
private boolean selected;

    public TableInfo() {
    }

    public TableInfo(String name, Date modTime) {
        this.name = name;
        this.modTime = modTime;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getModTime() {
        return modTime;
    }

    public void setModTime(Date modTime) {
        this.modTime = modTime;
    }

    public long getId() {
        return Id;
    }

    public void setId(long Id) {
        this.Id = Id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    
    @Override
    public String toString() {
        return "TableInfo{name=" + name + ", modTime=" + modTime + '}';
    }

    



}
