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
package org.jtalks.poulpe.model.entity;

import java.util.List;

import org.jtalks.common.model.entity.Property;

/**
 * Type of the {@link Component}. It is used as a primary key in {@link ComponentBase} and as a discriminator in
 * {@link Component}.<br>
 * <br>
 * 
 * If adding a new value to {@link ComponentType} enum, make sure that corresponding {@link ComponentBase} entity is
 * created.
 * 
 * @author Pavel Vervenko
 * @see Component
 */
public enum ComponentType {
    /**
     * Specifies that the component is Forum
     */
    FORUM {
        @Override
        public Component newComponent(String name, String description, List<Property> properties) {
            return new Jcommune(name, description, properties);
        }
    },

    /**
     * Specifies that the component is Article
     */
    ARTICLE {
        @Override
        public Component newComponent(String name, String description, List<Property> properties) {
            return new Component(name, description, ARTICLE, properties);
        }
    },

    /**
     * Specifies that the component is Administrator Panel.
     */
    ADMIN_PANEL {
        @Override
        public Component newComponent(String name, String description, List<Property> properties) {
            return new Poulpe(name, description, properties);
        }
    };

    /**
     * Creates a component of needed {@link ComponentType}. Should be used from {@link ComponentBase}
     * 
     * @param name of the component
     * @param description its description
     * @param properties of the component
     * @return {@link Component} ready for using
     */
    public abstract Component newComponent(String name, String description, List<Property> properties);

}