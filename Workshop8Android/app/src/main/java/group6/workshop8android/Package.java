/*
Author: Marat Nikitin;
Co-Author: , contribution: ;
PROJ-207 Threaded Project, Stage 3, Group 6, Workshop #8 (Android),
OOSD program, SAIT, March-May 2022;
This Android app allows doing a full range of CRUD operations with the 'packages' table
    of the 'travelexperts' MySQL database.
This class represents the 'packages' table entity of the database.
*/

package group6.workshop8android;

import java.io.Serializable;
import java.util.Date;

public class Package implements Serializable {
    private static final long serialVersionUID = 1L; // needed for deserialization

    private int id; // represent packageId column of the 'travelexperts' database
    private String pkgName;
    private String pkgStartDate; // String works great for dates as MySQL recognizes 'YYYY-MM-DD' strings as dates
    private String pkgEndDate; // String works great for dates as MySQL recognizes 'YYYY-MM-DD' strings as dates
    private String pkgDesc;
    private Double pkgBasePrice;
    private Double pkgAgencyCommission;

    // emptry constructor method (for convenience):
    public Package() {
    }

    // complete constructor method:
    public Package(int id, String pkgName, String pkgStartDate, String pkgEndDate, String pkgDesc,
                   Double pkgBasePrice, Double pkgAgencyCommission) {
        this.id = id;
        this.pkgName = pkgName;
        this.pkgStartDate = pkgStartDate;
        this.pkgEndDate = pkgEndDate;
        this.pkgDesc = pkgDesc;
        this.pkgBasePrice = pkgBasePrice;
        this.pkgAgencyCommission = pkgAgencyCommission;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    // getters and setters:
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getPkgStartDate() {
        return pkgStartDate;
    }

    public void setPkgStartDate(String pkgStartDate) {
        this.pkgStartDate = pkgStartDate;
    }

    public String getPkgEndDate() {
        return pkgEndDate;
    }

    public void setPkgEndDate(String pkgEndDate) {
        this.pkgEndDate = pkgEndDate;
    }

    public String getPkgDesc() {
        return pkgDesc;
    }

    public void setPkgDesc(String pkgDesc) {
        this.pkgDesc = pkgDesc;
    }

    public Double getPkgBasePrice() {
        return pkgBasePrice;
    }

    public void setPkgBasePrice(Double pkgBasePrice) {
        this.pkgBasePrice = pkgBasePrice;
    }

    public Double getPkgAgencyCommission() {
        return pkgAgencyCommission;
    }

    public void setPkgAgencyCommission(Double pkgAgencyCommission) {
        this.pkgAgencyCommission = pkgAgencyCommission;
    }

    // to string method allowing display of meaningful package names:
    @Override
    public String toString(){
        return pkgName;
    }
}
