package org.jtalks.poulpe.validation.unique;

import static org.testng.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jtalks.poulpe.model.dao.hibernate.ObjectsFactory;
import org.jtalks.poulpe.model.entity.Component;
import org.jtalks.poulpe.model.entity.ComponentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Alexey Grigorev
 */
public class EntityWrapperTest {

    private long id = 10;
    private EntityWrapper wrapper;
    private Component component;

    @BeforeClass
    public void beforeClass() {
        component = ObjectsFactory.createComponent(ComponentType.ARTICLE);
        component.setId(id);

        wrapper = new EntityWrapper(component);
    }

    @Test
    public void getEntityI1d() {
        assertEquals(wrapper.getEntityId(), id);
    }

    @Test
    public void getEntityClass() {
        assertEquals(wrapper.getEntityClass(), Component.class);
    }

    @Test
    public void getErrorMessage() {
        String errorMessage = wrapper.getErrorMessage("name");
        assertEquals(errorMessage, Component.NOT_UNIQUE_NAME);
    }

    @Test
    public void getNameValue() {
        Object actual = wrapper.getValue("name");
        String expected = component.getName();
        assertEquals(actual, expected);
    }

    @Test
    public void getComponentTypeValue() {
        Object actual = wrapper.getValue("componentType");
        ComponentType expected = component.getComponentType();
        assertEquals(actual, expected);
    }

    @Test
    public void getProperties() {
        Set<Entry<String, Object>> properties = wrapper.getProperties();

        Map<String, Object> result = toMap(properties);

        assertEquals(result.size(), 2);
        assertEquals(component.getName(), result.get("name"));
        assertEquals(component.getComponentType(), result.get("componentType"));
    }

    @Test
    public void nullsFiltered() {
        component.setComponentType(null);
        wrapper = new EntityWrapper(component);

        Set<Entry<String, Object>> properties = wrapper.getProperties();
        Map<String, Object> result = toMap(properties);

        assertEquals(result.size(), 1);
        assertEquals(component.getName(), result.get("name"));
        assertNull(component.getComponentType());
    }

    public static <K, V> Map<K, V> toMap(Set<Entry<K, V>> properties) {
        Map<K, V> m = new HashMap<K, V>();

        for (Entry<K, V> e : properties) {
            m.put(e.getKey(), e.getValue());
        }

        return m;
    }

}
