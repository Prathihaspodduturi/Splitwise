package com.PrathihasProjects.PrathihasSplitwise.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GroupDTOTest {

    @Test
    void testConstructorAndAccessors() {

        String initialGroupName = "Friends";
        String initialGroupDescription = "Group of close friends";

        GroupDTO group = new GroupDTO(initialGroupName, initialGroupDescription);

        assertEquals(initialGroupName, group.getGroupName(), "Constructor should set group name");
        assertEquals(initialGroupDescription, group.getGroupDescription(), "Constructor should set group description");

        String newGroupName = "Family";
        String newGroupDescription = "Family members only";

        group.setGroupName(newGroupName);
        group.setGroupDescription(newGroupDescription);

        // Test updated values
        assertEquals(newGroupName, group.getGroupName(), "Setter should update group name");
        assertEquals(newGroupDescription, group.getGroupDescription(), "Setter should update group description");
    }

}
