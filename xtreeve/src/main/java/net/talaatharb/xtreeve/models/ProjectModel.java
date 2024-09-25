package net.talaatharb.xtreeve.models;

import lombok.Data;

@Data
public class ProjectModel {
    private String projectName;


    public ProjectModel(String projectName) {
        this.projectName = projectName;
    }
}

