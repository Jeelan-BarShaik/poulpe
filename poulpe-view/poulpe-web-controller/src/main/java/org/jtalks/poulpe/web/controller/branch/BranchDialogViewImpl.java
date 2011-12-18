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

import java.util.List;

import org.jtalks.poulpe.model.entity.Branch;
import org.jtalks.poulpe.model.entity.Section;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 * This class is implementation view for single branch
 * 
 * @author Bekrenev Dmitry
 * */
public class BranchDialogViewImpl extends Window implements BranchDialogView,
        AfterCompose {

    private static final long serialVersionUID = 7388638074018815713L;

    private BranchPresenter presenter;
    private Textbox branchName;
    private Textbox branchDescription;
    private Button confirmButton;
    private Button rejectButton;
    private Branch branch;
    private Combobox sectionList;

    /**
     * set default section in combobox for select section when branch will be
     * stored
     * 
     * @param section
     *            default section
     * */
    private void setDefaultSection(Section section) {
        @SuppressWarnings("unchecked")
        ListModelList<Section> model = (ListModelList<Section>) sectionList.getModel();
        model.clearSelection();
        if (section != null) {
            model.addSelection(section);
        } else {
            model.addSelection(model.get(0));
        }
        sectionList.setModel(model);

    }

    /**
     * This class render items Combobox list
     * */
    private static ComboitemRenderer<Section> sectionItemRenderer = new ComboitemRenderer<Section>() {

        @Override
        public void render(Comboitem item, Section section) {
            item.setLabel(section.getName());
            item.setDescription(section.getDescription());
        }
    };

    /**
     * Set presenter
     * 
     * @see BranchEditorPresenter
     * @param presenter
     *            instance presenter for view
     * */
    public void setPresenter(BranchPresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void afterCompose() {
        Components.addForwards(this, this);
        Components.wireVariables(this, this);
        presenter.setView(this);
        presenter.initView();

        branchName.setConstraint("no empty");
        sectionList.setItemRenderer(sectionItemRenderer);
    }

    /**
     * Handle event when user click on confirm button
     * */
    public void onClick$confirmButton() {
        presenter.saveBranch();
        presenter.updateView();
    }

    /**
     * Handle event when user click on reject button
     * */
    public void onClick$rejectButton() {
        hide();
    }

    /**
     * Handle event when branch name field in focus
     * */
    public void onFocus$branchName() {
        branchName.clearErrorMessage();
    }

    /**
     * Handle event from main window for open add new branch dialog
     * */
    public void onOpenAddDialog() {
        show();
    }

    /**
     * Handle event from main window for open edit branch dialog
     * 
     * @param event
     *            information about event
     * */
    public void onOpenEditDialog(Event event) {
        show((Branch) event.getData());
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public Section getSection() {
        Section section = (Section) sectionList.getModel().getElementAt(
                sectionList.getSelectedIndex());
        return section;
    }

    public Branch getBranch(Section section) {
        branch.setName(branchName.getText().trim());
        branch.setDescription(branchDescription.getText().trim());
        branch.setSection(section);
        return branch;
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void hide() {
        setVisible(false);
        BranchEditorView view = (BranchEditorView) getAttribute("backWin");
        if (view == null) {
            return;
        }
        BranchEditorPresenter presenter = (BranchEditorPresenter) getAttribute("presenter");
        presenter.updateView();
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void show() {
        presenter.initView();
        setTitle(Labels.getLabel("branches.newbranchedialog.title"));
        confirmButton.setLabel(Labels.getLabel("branches.button.add"));
        rejectButton.setLabel(Labels.getLabel("branches.button.cancel"));
        branchName.setRawValue("");
        branchDescription.setText("");
        branch = new Branch();
        setDefaultSection(null);
        setVisible(true);
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void show(Branch branch) {
        presenter.initView();
        setTitle(Labels.getLabel("branches.editdialog.title"));
        confirmButton.setLabel(Labels.getLabel("branches.button.edit"));
        rejectButton.setLabel(Labels.getLabel("branches.button.cancel"));
        Section section = branch.getSection();
        setDefaultSection(section);
        branchName.setText(branch.getName());
        branchDescription.setText(branch.getDescription());
        this.branch = branch;
        setVisible(true);
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void notUniqueBranchName() {
        throw new WrongValueException(branchName,
                Labels.getLabel("branches.error.branch_name_already_exists"));
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void initSectionList(List<Section> sections) {
        sectionList.setModel(new ListModelList<Section>(sections));
    }

    Branch createBranch() {
        return branch = new Branch();
    }
    
    @Override
    public void openErrorPopupInNewSectionDialog(String label) {
    	final String message = Labels.getLabel(label);
    	branchName.setErrorMessage(message);
    	
    }

}
