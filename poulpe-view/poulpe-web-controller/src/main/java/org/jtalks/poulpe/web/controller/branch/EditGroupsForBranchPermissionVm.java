/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.poulpe.web.controller.branch;

import org.apache.commons.collections.ListUtils;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.permissions.BranchPermission;
import org.jtalks.poulpe.logic.AnonymousGroup;
import org.jtalks.poulpe.model.dto.PermissionChanges;
import org.jtalks.poulpe.model.dto.PermissionForEntity;
import org.jtalks.poulpe.model.dto.PermissionsMap;
import org.jtalks.poulpe.model.entity.PoulpeBranch;
import org.jtalks.poulpe.service.BranchService;
import org.jtalks.poulpe.service.GroupService;
import org.jtalks.poulpe.web.controller.SelectedEntity;
import org.jtalks.poulpe.web.controller.TwoSideListWithFilterVm;
import org.jtalks.poulpe.web.controller.WindowManager;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Feeds the dialog for adding/removing groups for permissions. The page has 2 lists: available groups & those that are
 * already granted/restricted to the permission. So when the user selects some items from one list and moves them to
 * another, a command is triggered in {@link BranchPermissionManagementVm} which then delegates the actual changing of
 * the lists to this view model.
 *
 * @author Vyacheslav Zhivaev
 * @see BranchPermissionManagementVm
 */
public class EditGroupsForBranchPermissionVm extends TwoSideListWithFilterVm<Group> {
    public static final String BRANCH_PERMISSION_MANAGEMENT_ZUL = "WEB-INF/pages/forum/BranchPermissionManagement.zul";
    private final WindowManager windowManager;
    private final BranchService branchService;
    private final GroupService groupService;
    private final SelectedEntity<Object> selectedEntity;
    // Related to internal state
    private final PermissionForEntity permissionForEntity;
    private final PoulpeBranch branch;

    /**
     * Construct VM for editing group list for selected {@link BranchPermission}.
     *
     * @param windowManager  the window manager instance
     * @param branchService  the branch service instance
     * @param groupService   the group service instance
     * @param selectedEntity the SelectedEntity contains {@link PermissionForEntity} with data needed for construction
     *                       VM state
     */
    public EditGroupsForBranchPermissionVm(@Nonnull WindowManager windowManager, @Nonnull BranchService branchService,
                                           @Nonnull GroupService groupService,
                                           @Nonnull SelectedEntity<Object> selectedEntity) {
        permissionForEntity = (PermissionForEntity) selectedEntity.getEntity();

        this.windowManager = windowManager;
        this.branchService = branchService;
        this.groupService = groupService;
        this.selectedEntity = selectedEntity;

        branch = (PoulpeBranch) permissionForEntity.getTarget();
        getStateAfterEdit().addAll(getAlreadyAddedGroupsForMode(branch, permissionForEntity.isAllowed()));
    }

    // -- ZK Command bindings --------------------

    /**
     * Closes the dialog.
     */
    @Command
    public void cancel() {
        openBranchPermissionsWindow();
    }

    /**
     * Saves the state.
     */
    @Command
    public void save() {
        List<Group> alreadyAddedGroups = getAlreadyAddedGroupsForMode(branch, permissionForEntity.isAllowed());

        @SuppressWarnings("unchecked")
        PermissionChanges accessChanges = new PermissionChanges(permissionForEntity.getPermission(),
                ListUtils.subtract(getStateAfterEdit(), alreadyAddedGroups), ListUtils.subtract(alreadyAddedGroups,
                getStateAfterEdit()));

        if (!accessChanges.isEmpty()) {
            if (permissionForEntity.isAllowed()) {
                branchService.changeGrants(branch, accessChanges);
            }
            else {
                branchService.changeRestrictions(branch, accessChanges);
            }
        }

        openBranchPermissionsWindow();
    }

    // -- Utility methods ------------------------

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Init
    @Override
    public void updateVm() {
        getExist().clear();
        getExist().addAll(getStateAfterEdit());

        getAvail().clear();
        List<Group> groups = groupService.getAll();
        groups.add(AnonymousGroup.ANONYMOUS_GROUP);
        getAvail().addAll(ListUtils.subtract(groups, getStateAfterEdit()));
    }

    /**
     * Gets list of groups which already added in persistence for current {@link PoulpeBranch} with {@link AclMode}.
     *
     * @param branch  the branch to get for
     * @param allowed the permission mode (allowed or restricted)
     * @return list of groups already added for current {@link PoulpeBranch} with specified mode
     */
    private List<Group> getAlreadyAddedGroupsForMode(PoulpeBranch branch, boolean allowed) {
        PermissionsMap<BranchPermission> permissionsMap = branchService.getPermissionsFor(branch);
        return permissionsMap.get((BranchPermission) permissionForEntity.getPermission(), allowed);
    }

    /**
     * Opens window with BranchPermissions page.
     */
    private void openBranchPermissionsWindow() {
        selectedEntity.setEntity(branch);
        BranchPermissionManagementVm.showPage(windowManager);
    }
}
