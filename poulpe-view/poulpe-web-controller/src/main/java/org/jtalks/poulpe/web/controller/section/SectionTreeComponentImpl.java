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
package org.jtalks.poulpe.web.controller.section;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jtalks.common.model.entity.Entity;
import org.jtalks.poulpe.model.entity.Branch;
import org.jtalks.poulpe.model.entity.Section;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Div;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;

/**
 * @author Konstantin Akimov
 * */
public class SectionTreeComponentImpl extends Div implements IdSpace {
    /**
     * 
     */
    private static final long serialVersionUID = -1083425488934932487L;
    /**
     * Reference to a corresponding ZUL file
     */
    private static final String ZUL_REF = "WEB-INF/pages/sectionTree.zul";
    private Tree sectionTree;
    private SectionPresenter presenter;

    /**
     * @param section
     *            for which will be build tree
     * @param presenter
     *            instance section presenter
     * */
    public SectionTreeComponentImpl(Section section, SectionPresenter presenter) {
        this.presenter = presenter;
        Executions.createComponents(ZUL_REF, this, null);
        Components.wireVariables(this, this);
        Components.addForwards(this, this);

        DefaultTreeNode<Section> child = TreeNodeFactory.getTreeNode(section);
        List<DefaultTreeNode<Section>> defaultTreeNodes = Collections.singletonList(child);

        DefaultTreeNode<Section> root = new DefaultTreeNode<Section>(null, defaultTreeNodes);
        DefaultTreeModel<Section> model = new DefaultTreeModel<Section>(root);
        sectionTree.setModel(model);
        sectionTree.setItemRenderer(new SectionBranchTreeitemRendere());
    }

    /**
     * {@inheritDoc}
     * */
    
    public Object getSelectedObject() {
        if (sectionTree.getSelectedCount() != 0) {
            Object data = sectionTree.getSelectedItem().getValue();
            return data;
        }
        return null;
    }

    /**
     * event which happen when click on add branch button
     * */
    public void onClick$addBranchButton() {
        presenter.openNewBranchDialog(this);
    }

    /**
     * event which happen when double click on section or branch
     * */
    public void onDoubleClick$sectionTree() {
        presenter.openEditDialog(this);
    }

    /**
     * {@inheritDoc}
     * */
    
    public void addBranchToView(Branch branch) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * */
    
    public void updateBranchInView(Branch branch) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * */
    
    public void removeBranchFromView(Branch branch) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * */
    
    public void updateSectionInView(Section section) {
        DefaultTreeNode<Section> root = (DefaultTreeNode<Section>) sectionTree.getModel().getRoot();
        ExtendedTreeNode<Section> sectionNode = (ExtendedTreeNode<Section>) root.getChildAt(0);
        sectionNode.setData(section);
    }

    /**
     * Click on '*' button
     */
    public void onClick$moderatorButton() {
        presenter.openModeratorDialog(getSelectedObject());
    }

    public void onClick$permissionsButton() throws IOException {
        Object selected = getSelectedObject();
        if(!(selected instanceof Branch)) {
            Messagebox.show("This action not provided for section, please select a branch");
            return;
        }
        Branch branch = (Branch) selected;
        Executions.sendRedirect("/sections/BranchPermissionManagement.zul?branchId=" + branch.getId());
    }

    /**
     * Event which happen when user click on '-' button after it selected
     * section is going to be deleted
     */
    public void onClick$delButton() {
        Object selectedObject = getSelectedObject();
        if (selectedObject == null) {
            // if the there no selected object
            // we should consider this event as a try to remove section
            TreeModel<DefaultTreeNode> model = sectionTree.getModel();
            DefaultTreeNode root = model.getRoot();
            ExtendedTreeNode child = (ExtendedTreeNode) root.getChildAt(0);
            selectedObject = child.getData();
        }

        presenter.openDeleteDialog(selectedObject);

    }

    public class SectionBranchTreeitemRendere implements TreeitemRenderer {
        
        @Override
        public void render(final Treeitem treeItem, Object node) throws Exception {
            ExtendedTreeNode curNode = (ExtendedTreeNode) node;

            final Entity data = (Entity) curNode.getData();
            treeItem.setOpen(curNode.isExpanded());// Whether open the node
            treeItem.setValue(data);
            Iterator iter = treeItem.getChildren().iterator();

            while (iter.hasNext()) {
                Object child = iter.next();
                if (child instanceof Treerow) {
                    for (Object oCell : ((Treerow) child).getChildren()) {
                        if (oCell instanceof Treecell) {
                            if (data instanceof Section) {
                                ((Treecell) oCell).setLabel(((Section) data).getName());
                            } else if (data instanceof Branch) {
                                ((Treecell) oCell).setLabel(((Branch) data).getName());
                            }
                        }
                    }
                    return;
                }
            }

            Treerow treeRow = new Treerow();
            treeItem.appendChild(treeRow);
            if (data instanceof Section) {
                treeRow.appendChild(new Treecell(((Section) data).getName()));
            } else if (data instanceof Branch) {
                treeRow.appendChild(new Treecell(((Branch) data).getName()));

            }

        }
    };

    /**
     * to make it injectable
     * 
     * @param tree
     */
    public void setSectionTree(Tree tree) {
        this.sectionTree = tree;
        sectionTree.addEventListener(Events.ON_SELECT, new EventListener<Event>() {

            @Override
            public void onEvent(Event event) throws Exception {
                Object selected = getSelectedObject();
                Button permissionsButton = getBranchPermissionsButton();
                if (selected instanceof Branch) {
                    permissionsButton.setDisabled(false);
                } else {
                    permissionsButton.setDisabled(true);
                }
            }

        });
    }
    
    private Button getBranchPermissionsButton() {
        return (Button) sectionTree.getFellow("permissionsButton");
    }
}
