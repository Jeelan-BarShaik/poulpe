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

import org.jtalks.poulpe.model.entity.Branch;
import org.jtalks.poulpe.model.entity.Section;
import org.jtalks.poulpe.service.BranchService;
import org.jtalks.poulpe.service.SectionService;
import org.jtalks.poulpe.validation.EntityValidator;
import org.jtalks.poulpe.validation.ValidationResult;
import org.jtalks.poulpe.web.controller.section.SectionPresenter;

/**
 * This class is implementation the branch presenter in pattern
 * Model-View-Presenter
 * 
 * @author Bekrenev Dmitry
 */
public class BranchPresenter {

    private SectionService sectionService;
    private EntityValidator entityValidator;
    private BranchDialogView view;
    private BranchService branchService;
    private SectionPresenter sectionPresenter;

    public SectionPresenter getSectionPresenter() {
        return sectionPresenter;
    }

    public void setSectionPresenter(SectionPresenter sectionPresenter) {
        this.sectionPresenter = sectionPresenter;
    }

    /**
     * Sets the Branch instance
     */
    public void setBranchService(BranchService service) {
        branchService = service;
    }

    /**
     * Sets the Section instance
     */
    public void setSectionService(SectionService service) {
        sectionService = service;
    }

    public void updateView() {
        sectionPresenter.updateView();
    }

    /**
     * Sets the view instance which represent User interface
     */
    public void setView(BranchDialogView view) {
        this.view = view;
    }

    /**
     * Init view initial data
     */
    public void initView() {
        view.initSectionList(sectionService.getAll());
    }


    /**
     * Save new or edited branch in db In case when branch with equal name
     * exists, cause open error popup in view.
     */
    public boolean saveBranch() {
        Section section = view.getSection();
        Branch branch = view.getBranch(section);
		return saveBranch(branch);
    }

    //TODO: This method should be reworked or removed. It was added for testing purposes
	protected boolean saveBranch(Branch branch) {
		if (validate(branch)) {
		    Section section = branch.getSection();
		    section.addOrUpdateBranch(branch);
		    sectionService.saveSection(section);
			view.hide();
			return true;
		} else {
			return false;
		}
	}
    
    /**
     * @param entityValidator the entityValidator to set
     */
    public void setEntityValidator(EntityValidator entityValidator) {
        this.entityValidator = entityValidator;
    }
    
    private boolean validate(Branch branch) {
        ValidationResult result = entityValidator.validate(branch);

        if (result.hasErrors()) {
            view.validationFailure(result);
            return false;
        } else {
            return true;
        }
    }

}
