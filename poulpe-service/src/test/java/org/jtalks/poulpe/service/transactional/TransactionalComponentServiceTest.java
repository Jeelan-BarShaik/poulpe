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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.Set;

import org.jtalks.poulpe.model.dao.ComponentDao;
import org.jtalks.poulpe.model.entity.Component;
import org.jtalks.poulpe.validation.EntityValidator;
import org.jtalks.poulpe.validation.ValidationError;
import org.jtalks.poulpe.validation.ValidationException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Pavel Vervenko
 * @author Alexey Grigorev
 */
public class TransactionalComponentServiceTest {

    private ComponentDao componentDao;
    private TransactionalComponentService componentService;
    private EntityValidator validator;

    Component component = new Component();

    @BeforeMethod
    public void setUp() throws Exception {
        componentDao = mock(ComponentDao.class);
        validator = mock(EntityValidator.class);
        componentService = new TransactionalComponentService(componentDao, validator);
    }

    @Test
    public void testSaveComponent() {
        Component component = new Component();
        
        componentService.saveComponent(component);
        
        verify(validator).throwOnValidationFailure(component);
        verify(componentDao).saveOrUpdate(component);
    }

    @Test
    public void testGetAll() {
        componentService.getAll();
        verify(componentDao).getAll();
    }

    @Test(expectedExceptions = ValidationException.class)
    public void testSaveComponentException() {
        givenConstraintsViolations();
        componentService.saveComponent(component);
    }

    private void givenConstraintsViolations() {
        Set<ValidationError> empty = Collections.<ValidationError> emptySet();
        doThrow(new ValidationException(empty)).when(validator).throwOnValidationFailure(any(Component.class));
    }

    @Test
    public void testDeleteComponent() {
        componentService.deleteComponent(component);
        verify(componentDao).delete(component.getId());
    }

    @Test
    void testGetAvailableTypes() {
        componentService.getAvailableTypes();
        verify(componentDao).getAvailableTypes();
    }
}
