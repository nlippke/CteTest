package org.example.model;

import com.blazebit.persistence.CTE;

import javax.persistence.Entity;
import javax.persistence.Id;

@CTE
@Entity
public class CatCte {
    @Id
    private Long id;
    private Integer rank;

    public Long getId() {
        return id;
    }

    public Integer getRank() {
        return rank;
    }

}
