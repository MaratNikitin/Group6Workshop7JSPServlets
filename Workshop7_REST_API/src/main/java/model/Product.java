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
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProductId", nullable = false)
    private Integer id;

    @Column(name = "ProdName", nullable = false, length = 50)
    private String prodName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

}