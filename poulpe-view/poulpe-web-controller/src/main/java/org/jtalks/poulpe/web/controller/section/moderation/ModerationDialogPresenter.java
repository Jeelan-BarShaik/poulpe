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
package org.jtalks.poulpe.web.controller.section.moderation;

import java.util.List;

import org.jtalks.common.validation.EntityValidator;
import org.jtalks.common.validation.ValidationResult;
import org.jtalks.poulpe.model.entity.User;
import org.jtalks.poulpe.model.entity.PoulpeBranch;
import org.jtalks.poulpe.service.BranchService;
import org.jtalks.poulpe.service.UserService;
import org.jtalks.poulpe.web.controller.DialogManager;

import com.google.common.base.Optional;

/**
 * This class provides implementation for the moderation dialog presenter in pattern Model-View-Presenter.
 */
public class ModerationDialogPresenter {

    public static final String USER_ALREADY_MODERATOR = "moderatedialog.validation.user_already_in_list";
    
    private ModerationDialogView view;
    private PoulpeBranch branch;
    private BranchService branchService;
    private UserService userService;
    private DialogManager dialogManager;
    private EntityValidator entityValidator;

    /**
     * Mutator to set the {@link DialogManager} for this presenter.
     */
    public void setDialogManager(DialogManager dialogManager) {
        this.dialogManager = dialogManager;
    }

    /**
     * Mutator to set the {@link ModerationDialogView} for this presenter.
     */
    public void initView(ModerationDialogView view) {
        this.view = view;
        refreshView();
    }
    
    /**
     * Updates view using users from the branch and userService of this presenter.
     */
    public void refreshView() {
        updateView(branch.getModeratorsList(), userService.getAll());
    }

    /**
     * Updates view using specified users.
     */
    public void updateView(List<User> users, List<User> usersInCombo) {
        view.updateView(users, usersInCombo);
    }

    /**
     * Validate the specified user, on success sets him as a moderator for the branch, on failure shows error message.
     * 
     * @param user - candidate for the moderator
     */
    public void addUser(User user) {
        Optional<String> error = validateUser(user);

        if (error.isPresent()) {
            view.showComboboxErrorMessage(error.get());
        } else {
            branch.addModerator(user);
            refreshView();
        }
    }
    
    /**
     * Validate branch, on success saves it, on failure shows message.
     * */
    public void confirm() {
        ValidationResult result = entityValidator.validate(branch);

        // FIXME: what if the error has nothing to do with item's existence? 
        if (result.hasErrors()) {
            dialogManager.notify("item.already.exist");
        } else {
            branchService.saveBranch(branch);
        }

        view.hideDialog();
    }
    
    /**
     * Creates new user validator and validates the specified user.
     * 
     *  @param user - user to validate
     *  @return validator - new validator for the user
     */
    public Optional<String> validateUser(User user) {
        if (branch.isModeratedBy(user)) {
            return Optional.of(ModerationDialogPresenter.USER_ALREADY_MODERATOR);
        } else {
            return Optional.absent();
        }
    }
    
    /**
     * Removes the specified user from the moderator's list of the branch.
     * 
     * @param user the moderator to remove
     */
    public void deleteUser(User user) {
        branch.removeModerator(user);
        refreshView();
    }
    
    /**
     * Hides the dialog.
     */
    public void reject() {
        view.hideDialog();
    }
    
    @Deprecated
    public void onAdd(User user) {
        addUser(user);
    }

    @Deprecated
    public void onDelete(User user) {
        deleteUser(user);
    }
    
    @Deprecated
    public void onConfirm() {
        confirm();
    }

    @Deprecated
    public void onReject() {
        reject();
    }

    /**
     * Mutator to set the {@link BranchService} for this presenter.
     * 
     * @param service the service to set
     */
    public void setBranchService(BranchService service) {
        this.branchService = service;
    }

    /**
     * Mutator to set the {@link UserService} for this presenter.
     * 
     * @param service the service to set
     */
    public void setUserService(UserService service) {
        this.userService = service;
    }

    /**
     * Mutator to set the {@link PoulpeBranch} for this presenter.
     * 
     * @param branch the branch to set
     */
    public void setBranch(PoulpeBranch branch) {
        this.branch = branch;
    }

    /**
     * Mutator to set the {@link EntityValidator} for this presenter.
     * 
     * @param entityValidator the entityValidator to set
     */
    public void setEntityValidator(EntityValidator entityValidator) {
        this.entityValidator = entityValidator;
    }

}