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
package org.jtalks.poulpe.service.transactional;

import java.util.List;
import java.util.Set;

import org.jtalks.common.service.transactional.AbstractTransactionalEntityService;
import org.jtalks.poulpe.model.dao.ComponentDao;
import org.jtalks.poulpe.model.dao.DuplicatedField;
import org.jtalks.poulpe.model.entity.Component;
import org.jtalks.poulpe.model.entity.ComponentType;
import org.jtalks.poulpe.service.ComponentService;
import org.jtalks.poulpe.service.exceptions.NotUniqueFieldsException;

/**
 * Transactional implementation of {@link ComponentService}. Transactions are
 * provided by AOP.
 * 
 * @author Pavel Vervenko
 */
public class TransactionalComponentService extends AbstractTransactionalEntityService<Component, ComponentDao>
        implements ComponentService {

    /**
     * Creates new instance of the service
     * @param dao
     */
    public TransactionalComponentService(ComponentDao dao) {
        this.dao = dao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Component> getAll() {
        return dao.getAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteComponent(Component component) {
        dao.delete(component.getId());
    }

    /** {@inheritDoc} */
    @Override
    public void saveComponent(Component component) throws NotUniqueFieldsException {
        Set<DuplicatedField> set = dao.getDuplicateFieldsFor(component);

        if (!set.isEmpty()) {
            throw new NotUniqueFieldsException(set);
        }

        dao.saveOrUpdate(component);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<ComponentType> getAvailableTypes() {
        return dao.getAvailableTypes();
    }

    /** {@inheritDoc} */
    @Override
    public Set<DuplicatedField> getDuplicateFieldsFor(Component component) {
        return dao.getDuplicateFieldsFor(component);
    }
}
