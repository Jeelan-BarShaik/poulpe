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
package org.jtalks.poulpe.util.databasebackup.domain;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.jtalks.poulpe.util.databasebackup.persistence.SqlTypes;
import org.testng.annotations.Test;

/**
 * ColumnMetaData is mainly used in Collections so first of all we need to test it's comparison abilities.
 * 
 * @author Evgeny Surovtsev
 * 
 */
public class ColumnMetaDataTest {
    /**
     * Two different instances of ColumnMetaData should not be equal.
     */
    @Test
    public void twoNotEqualTableColumnAreNotEquals() {
        ColumnMetaData testObject1 = createColumnTypeA();
        ColumnMetaData differentTestObject = createColumnTypeB();
        assertFalse(testObject1.equals(differentTestObject));
    }

    /**
     * Two the same instances of ColumnMetaData should be equal.
     */
    @Test
    public void twoEqualTableColumnAreEquals() {
        ColumnMetaData testObject1 = createColumnTypeA();
        ColumnMetaData testObject2 = createColumnTypeA();
        assertTrue(testObject1.equals(testObject2));
    }

    /**
     * There should be no possibility to construct object without providing Name and Type values.
     */
    @Test
    @SuppressWarnings("unused")
    public void tableColumnShouldBeInitializedInConstructor() {
        try {
            ColumnMetaData tableColumn = new ColumnMetaData(null, null);
            fail("Exception expected");
        } catch (NullPointerException e) {
            // do nothing - the exception is expected.
        }
        try {
            ColumnMetaData tableColumn = new ColumnMetaData("columnTypeA", null);
            fail("Exception expected");
        } catch (NullPointerException e) {
            // do nothing - the exception is expected.
        }
        try {
            ColumnMetaData tableColumn = new ColumnMetaData(null, SqlTypes.INT);
            fail("Exception expected");
        } catch (NullPointerException e) {
            // do nothing - the exception is expected.
        }
    }

    /**
     * Tests Java Equals Contract.
     */
    @Test
    public void equalsContractTest() {
        ColumnMetaData testObject1, testObject2, testObject3, differentTestObject;

        testObject1 = createColumnTypeA();
        assertTrue(testObject1.equals(testObject1), "Reflexive");

        testObject1 = createColumnTypeA();
        testObject2 = createColumnTypeA();
        assertTrue(testObject1.equals(testObject2), "Equal Symmetric");
        assertTrue(testObject2.equals(testObject1), "Equal Symmetric");

        testObject1 = createColumnTypeA();
        differentTestObject = createColumnTypeB();
        assertFalse(testObject1.equals(differentTestObject), "Not Equal Symmetric");
        assertFalse(differentTestObject.equals(testObject1), "Not Equal Symmetric");

        testObject1 = createColumnTypeA();
        testObject2 = createColumnTypeA();
        testObject3 = createColumnTypeA();
        assertTrue(testObject1.equals(testObject2), "Transitive");
        assertTrue(testObject1.equals(testObject3), "Transitive");
        assertTrue(testObject2.equals(testObject3), "Transitive");

        assertFalse(testObject1.equals(null), "Null value should return false");
    }

    /**
     * Creates and returns an "ColumnTypeA" instance of ColumnmetaData. Method creates a new instance every time it is
     * called.
     * 
     * @return a newly created "ColumnTypeA"
     */
    private ColumnMetaData createColumnTypeA() {
        return new ColumnMetaData("columnTypeA", SqlTypes.INT)
                .setSize(32)
                .setAutoincrement(true)
                .setDefaultValue("5")
                .setNullable(false)
                .setComment("true - sid is user, false - sid is a granted authority");
    }

    /**
     * Creates and returns an "ColumnTypeB" instance of ColumnmetaData. Method creates a new instance every time it is
     * called.
     * 
     * @return a newly created "ColumnTypeB"
     */
    private ColumnMetaData createColumnTypeB() {
        return new ColumnMetaData("columnTypeB", SqlTypes.VARCHAR)
                .setSize(8)
                .setAutoincrement(false)
                .setDefaultValue("")
                .setNullable(true);
    }
}
