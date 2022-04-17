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
@Table(name = "packages_products_suppliers")
public class PackagesProductsSupplier {
    @EmbeddedId
    private PackagesProductsSupplierId id;

    @MapsId("packageId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PackageId", nullable = false)
    private Package _package;

    public PackagesProductsSupplierId getId() {
        return id;
    }

    public void setId(PackagesProductsSupplierId id) {
        this.id = id;
    }

    public Package get_package() {
        return _package;
    }

    public void set_package(Package _package) {
        this._package = _package;
    }

}