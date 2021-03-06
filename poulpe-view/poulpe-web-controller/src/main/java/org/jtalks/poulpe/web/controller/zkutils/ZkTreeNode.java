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
package org.jtalks.poulpe.web.controller.zkutils;

import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeNode;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * An enhanced implementation of the {@link TreeNode} that adds more operations to the original classes, like {@link
 * #moveTo(TreeNode)}.
 *
 * @author stanislav bashkirtsev
 */
public class ZkTreeNode<E> extends DefaultTreeNode<E> {
    private static final long serialVersionUID = 20110038094143L;

    /**
     * Creates a branch (non-leaf) node.
     *
     * @param children a collection of children (they must be {@link DefaultTreeNode} too). If null or empty, it means
     *                 no children at all. However, it still allows to add children. If it is not allowed, please use
     *                 {@link #ZkTreeNode(Object)} instead.
     */
    public ZkTreeNode(E data, TreeNode<E>... children) {
        super(data, children);
    }

    /**
     * Creates a branch (non-leaf) node.
     *
     * @param children a collection of children (they must be {@link DefaultTreeNode} too). If null or empty, it means
     *                 no children at all. However, it still allows to add children. If it is not allowed, please use
     *                 {@link #ZkTreeNode(Object)} instead.
     */
    public ZkTreeNode(E data, Collection<? extends TreeNode<E>> children) {
        super(data, children);
    }

    /**
     * Creates a leaf node, i.e., it won't allow any children.
     */
    public ZkTreeNode(E data) {
        super(data);
    }

    /**
     * Moves the node from its parent to the {@code destinationNode}. If current node doesn't have parent, then it will
     * be simply added to the destination node. Note, that the node will be added to the end of the destination node
     * children list.
     *
     * @param destinationNode the node to add the {@code toMove} node to
     * @return this
     */
    public ZkTreeNode<E> moveTo(@Nonnull TreeNode<E> destinationNode) {
        TreeNode<E> moveFrom = getParent();
        if (moveFrom == destinationNode) {
            return this;
        } else if (moveFrom != null) {
            moveFrom.remove(this);
        }
        destinationNode.add(this);
        return this;
    }

    /**
     * Adds the node to selection in its tree model.
     *
     * @return this
     */
    public ZkTreeNode<E> select() {
        getModel().addToSelection(this);
        return this;
    }
}
