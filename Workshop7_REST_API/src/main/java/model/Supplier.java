/*
Author: Java Persistence Architecture (JPA);
Co-Author: , contribution: ;
PROJ-207 Threaded Project, Stage 3, Group 6, Workshop #7 (JSP/Servlets),
OOSD program, SAIT, March-May 2022;
This app creates a RESTful API serving select tables of the 'travelexperts' MySQL database.
This entity class was created by JPA
*/

package model;

import javax.persistence.*;

@Entity
@Table(name = "suppliers")
public class Supplier {
    @Id
    @Column(name = "SupplierId", nullable = false)
    private Integer id;

    @Lob
    @Column(name = "SupName")
    private String supName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSupName() {
        return supName;
    }

    public void setSupName(String supName) {
        this.supName = supName;
    }

}