package com.stpi.campus.entity.schoolRegion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyc on 2014/11/22.
 */
public class SchoolInfo {

    private List<RegionDetail> regions;
    private SchoolDetail school;

    public SchoolInfo() {
        regions = new ArrayList<RegionDetail>();
        school = new SchoolDetail();
    }

    public SchoolInfo(SchoolDetail school, List<RegionDetail> regions) {
        this.school = school;
        this.regions = regions;
    }

    public List<RegionDetail> getRegions() {
        return regions;
    }

    public void setRegions(List<RegionDetail> regions) {
        this.regions = regions;
    }

    public SchoolDetail getSchool() {
        return school;
    }

    public void setSchool(SchoolDetail school) {
        this.school = school;
    }
}
