package com.gca.service;

import com.gca.dto.GroupDTO;
import com.gca.dto.TreeNodeDTO;

import java.util.List;

public interface GroupService {

    TreeNodeDTO getTree();

    List<TreeNodeDTO> getAllGroups();

    void deleteGroup(Long id);

    Long createGroup(GroupDTO group);

    Long updateGroup(GroupDTO group) throws Exception;

    void assignTemplateToGroup(Long groupId, Long templateId);

    void unassignTemplateToGroup(Long groupId);
}