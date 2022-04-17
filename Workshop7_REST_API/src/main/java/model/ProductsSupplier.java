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
@Table(name = "products_suppliers")
public class ProductsSupplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProductSupplierId", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProductId")
    private Product product;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}