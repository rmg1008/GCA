package com.gca.repositories;

import com.gca.domain.Group;
import com.gca.repository.GroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class GroupRepositoryTest {

    @Autowired
    private GroupRepository groupRepository;

    @Test
    @DisplayName("Should find device by name")
    void testFindByParentIsNull() {
        // Given
        Group group = new Group();
        group.setName("DeviceTest");
        group.setParent(null);

        groupRepository.save(group);

        // When
        Optional<Group> found = groupRepository.findByParentIsNull();

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("DeviceTest");
    }

    @Test
    @DisplayName("Should find only group with parent null and ignore groups with parent set")
    void testFindByParentIsNull_IgnoreGroupsWithParent() {
        // Given
        Group rootGroup = new Group();
        rootGroup.setName("RootGroup");
        rootGroup.setParent(null);
        groupRepository.save(rootGroup);

        Group childGroup1 = new Group();
        childGroup1.setName("ChildGroup1");
        childGroup1.setParent(rootGroup);

        Group childGroup2 = new Group();
        childGroup2.setName("ChildGroup2");
        childGroup2.setParent(rootGroup);

        groupRepository.save(childGroup1);
        groupRepository.save(childGroup2);

        // When
        Optional<Group> found = groupRepository.findByParentIsNull();

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("RootGroup");
        assertThat(found.get().getParent()).isNull();
    }

}
