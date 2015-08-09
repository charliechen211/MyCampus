package com.stpi.campus.entity.schoolRegion;

/**
 * Created by cyc on 2014/11/22.
 */
public class SchoolDetail {

    private String id;
    private String schoolId;
    private String schoolName;

    public SchoolDetail() {

    }

    public SchoolDetail(String schoolName, String id, String schoolId) {
        this.schoolName = schoolName;
        this.id = id;
        this.schoolId = schoolId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }
}
