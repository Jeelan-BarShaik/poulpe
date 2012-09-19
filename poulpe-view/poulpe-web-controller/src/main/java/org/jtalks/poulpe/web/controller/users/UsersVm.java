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
package org.jtalks.poulpe.web.controller.users;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.StringUtils;
import org.jtalks.poulpe.model.entity.PoulpeUser;
import org.jtalks.poulpe.service.UserService;
import org.jtalks.poulpe.validator.EmailValidator;
import org.jtalks.poulpe.web.controller.ZkHelper;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Textbox;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author dim42
 * @author Alexey Grigorev
 */
public class UsersVm {

    private static final int ITEMS_PER_PAGE = 50;

    private static final String SELECTED_ITEM_PROP = "selectedUser";
    private static final String VIEW_DATA_PROP = "viewData";
    private static final String ACTIVE_PAGE = "activePage";
    private static final String USERS = "users";
    private static final String TOTAL_SIZE = "totalSize";

    static final String NO_FILTER_SEARCH_STRING = "";
    static final String EDIT_USER_URL = "/WEB-INF/pages/users/edit_user.zul";
    static final String CHANGE_PASSWORD_URL = "/WEB-INF/pages/users/change_password.zul";
    static final String EDIT_USER_DIALOG = "#editUserDialog";
    static final String CHANGE_PASSWORD_DIALOG = "#changePasswordDialog";

    private final UserService userService;
    private final Validator emailValidator;
    private ZkHelper zkHelper;

    private List<PoulpeUser> users;
    private String searchString = NO_FILTER_SEARCH_STRING;
    private int activePage = 0;
    private PoulpeUser selectedUser;

    /**
     * @param userService the service to get access to users and to store changes to the database
     */
    public UsersVm(@Nonnull UserService userService) {
        this.userService = userService;
        emailValidator = new EmailValidator(userService);
    }

    /**
     * Wires users window to this ViewModel.
     *
     * @param component users window
     */
    @Init
    public void init(@ContextParam(ContextType.VIEW) Component component) {
        init(component, new ZkHelper(component));
    }

    /**
     * @param component {@link org.jtalks.poulpe.model.entity.Jcommune} instance
     * @param zkHelper  instance of {@link ZkHelper}
     */
    @VisibleForTesting
    void init(Component component, ZkHelper zkHelper) {
        this.zkHelper = zkHelper;
        zkHelper.wireComponents(component, this);
        prepareForListing();
    }

    // === listing and search ===

    /**
     * Prepares for listing
     */
    private void prepareForListing() {
        displayFirstPage(NO_FILTER_SEARCH_STRING);
    }

    /**
     * Sets first page as active page.
     *
     * @param searchString expression to filter users
     */
    private void displayFirstPage(String searchString) {
        this.searchString = searchString;
        setActivePage(0);
    }

    /**
     * Returns list containing users on page with number given as param
     * @param page number of page used to users look up
     * @return list containing users on page with number given as param
     */
    private List<PoulpeUser> usersOf(int page) {
        return userService.findUsersPaginated(searchString, page, ITEMS_PER_PAGE);
    }

    /**
     * Returns currently bounded list of users
     * @return currently bounded list of users
     */
    public List<PoulpeUser> getUsers() {
        return users;
    }

    /**
     * Returns total amount of users matched the searchString
     * @return total amount of users matched the searchString
     */
    public int getTotalSize() {
        return userService.countUsernameMatches(searchString);
    }

    /**
     * Updates the active page value with the current page of pagination. Updates the list of users so it displays the
     * needed page.
     *
     * @param activePage current page of pagination
     */
    @NotifyChange({USERS})
    public void setActivePage(int activePage) {
        this.activePage = activePage;
        this.users = usersOf(activePage);
    }

    /**
     * Returns currently active page
     * @return currently active page
     */
    public int getActivePage() {
        return activePage;
    }

    /**
     * Filters all the users using the given string.
     *
     * @param searchString string for filtering
     */
    @Command
    @NotifyChange({USERS, TOTAL_SIZE, ACTIVE_PAGE})
    public void searchUsers(@BindingParam(value = "searchString") String searchString) {
        displayFirstPage(searchString);
    }

    /**
     * Resets the search - clears searchbox, rewinds to the first page.
     *
     * @param searchBox component with search string
     */
    @Command
    @NotifyChange({USERS, TOTAL_SIZE, ACTIVE_PAGE})
    public void clearSearch(@BindingParam(value = "searchBox") @Nonnull Textbox searchBox) {
        if (StringUtils.isNotEmpty(searchBox.getValue())) {
            searchBox.setValue("");
            prepareForListing();
        }
    }

    // === editing & saving ===

    /**
     * Opens edit user dialog.
     */
    @Command
    public void editUser() {
        zkHelper.wireToZul(EDIT_USER_URL);
    }

    /**
     * Validates editing user, on success saves him, on failure shows the error message.
     *
     * @param user editing user
     */
    @Command
    @NotifyChange({VIEW_DATA_PROP, SELECTED_ITEM_PROP})
    public void saveUser(@BindingParam(value = "user") PoulpeUser user) {
        userService.updateUser(user);
        closeEditDialog();
    }

    /**
     * Cancel current edit operation by closing dialog.
     */
    @Command
    @NotifyChange({VIEW_DATA_PROP, SELECTED_ITEM_PROP})
    public void cancelEdit() {
        closeEditDialog();
    }

    /**
     * Close currently opened edit dialog.
     */
    private void closeEditDialog() {
        zkHelper.findComponent(EDIT_USER_DIALOG).detach();
    }

    /**
     * Gets the user selected on the UI.
     *
     * @return the user selected on the UI
     */
    public PoulpeUser getSelectedUser() {
        return selectedUser;
    }

    /**
     * Sets the user as currently selected
     * @param selectedUser {@link PoulpeUser} to set as currently selected
     */
    public void setSelectedUser(PoulpeUser selectedUser) {
        this.selectedUser = selectedUser;
    }

    /**
     * Returns expression from search string
     * @return expression from search string
     */
    public String getSearchString() {
        return searchString;
    }

    /**
     * Returns users count, shown on a single page
     * @return users count, shown on a single page
     */
    public int getItemsPerPage() {
        return ITEMS_PER_PAGE;
    }

    /**
     * Returns instance used for e-mail validation
     * @return {@link Validator} instance used for e-mail validation
     */
    public Validator getEmailValidator() {
        return emailValidator;
    }

    /**
     * Sets zkHelper
     * @param zkHelper instance to set
     */
    @VisibleForTesting
    void setZkHelper(ZkHelper zkHelper) {
        this.zkHelper = zkHelper;
    }

    /**
     * Shows the form for password changing
     */
    @Command
    public void showChangePasswordWindow() {
        zkHelper.wireToZul(CHANGE_PASSWORD_URL);
    }

    /**
     * Cancel current changing password operation by closing dialog.
     */
    @Command
    public void cancelChangePassword() {
        closeChangePasswordDialog();
    }

    /**
     * Closes the dialog for password changing
     */
    public void closeChangePasswordDialog() {
        zkHelper.findComponent(CHANGE_PASSWORD_DIALOG).detach();
    }

    /**
     * Changes the password of selected user
     * @param newPassword - new user's password
     */
    @Command
    @NotifyChange("confirmPasswordBox")
    public void changePassword(@BindingParam("newPassword") String newPassword, @BindingParam("confirmedPassword") String confirmedPassword) {
        if (newPassword.equals(confirmedPassword)) {
            String hash = getMD5Hash(newPassword);
            selectedUser.setPassword(hash);
            userService.updateUser(selectedUser);
            closeChangePasswordDialog();
        } else {
            Textbox confirmPasswordBox = (Textbox) zkHelper.getComponentByPath("/adminWindow/usersWindow/changePasswordDialog/confirmPasswordBox");
            throw new WrongValueException(confirmPasswordBox, "Passwords do not match");
        }
    }

    /**
     * Encodes the password with md5 algorithm and returns hash code
     * @param password - password to encode
     * @return md5 hash of the password
     */
    public String getMD5Hash(String password) {
        PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
        return passwordEncoder.encodePassword(password, null);
    }

}