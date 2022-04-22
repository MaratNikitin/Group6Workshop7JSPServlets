/*
Author: Marat Nikitin;
Co-Author: , contribution: ;
PROJ-207 Threaded Project, Stage 3, Group 6, Workshop #8 (Android),
OOSD program, SAIT, March-May 2022;
This Android app allows doing a full range of CRUD operations with the 'packages' table
    of the 'travelexperts' MySQL database.
This class provides objects and methods for working with the ListView in the "activity_main.xml" window .
*/

package group6.workshop8android;

import java.io.Serializable;

public class ListViewPackage implements Serializable {
    private static final long serialVersionUID = 1L; // needed for deserialization

    private int id; // represents a packageId
    private String pkgName;

    public ListViewPackage() {
    }

    // constructor method:
    public ListViewPackage(int id, String pkgName) {
        this.id = id;
        this.pkgName = pkgName;
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

    // overriding to string to show only package names in the ListView of the main window:
    @Override
    public String toString(){
        return pkgName;
    }
}
