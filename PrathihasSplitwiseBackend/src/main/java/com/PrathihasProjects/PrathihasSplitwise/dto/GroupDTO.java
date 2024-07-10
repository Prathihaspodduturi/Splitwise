package com.PrathihasProjects.PrathihasSplitwise.dto;

public class GroupDTO {

    private final String groupName;
    private final String groupDescription;

//    public GroupDTO(){}

    public GroupDTO(String groupName, String groupDescription){
        this.groupName = groupName;
        this.groupDescription = groupDescription;
    }
    public String getGroupName() {
        return groupName;
    }

//    public void setGroupName(String groupName) {
//        this.groupName = groupName;
//    }

    public String getGroupDescription() {
        return groupDescription;
    }

//    public void setGroupDescription(String groupDescription) {
//        this.groupDescription = groupDescription;
//    }
}
