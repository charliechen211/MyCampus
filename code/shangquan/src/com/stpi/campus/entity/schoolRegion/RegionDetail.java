package com.stpi.campus.entity.schoolRegion;

/**
 * Created by cyc on 2014/11/22.
 */
public class RegionDetail {

    private String id;
    private String regionId;
    private String regionName;
    private String schoolId;

    public RegionDetail() {
    }

    public RegionDetail(String id, String regionId, String regionName, String schoolId) {
        this.id = id;
        this.regionId = regionId;
        this.regionName = regionName;
        this.schoolId = schoolId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }
}
