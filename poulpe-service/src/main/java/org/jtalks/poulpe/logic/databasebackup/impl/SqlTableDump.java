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
package org.jtalks.poulpe.logic.databasebackup.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.jtalks.poulpe.logic.databasebackup.impl.dto.TableColumn;
import org.jtalks.poulpe.logic.databasebackup.impl.dto.TableForeignKey;
import org.jtalks.poulpe.logic.databasebackup.impl.dto.TablePrimaryKey;
import org.jtalks.poulpe.logic.databasebackup.impl.dto.TableRow;
import org.jtalks.poulpe.logic.databasebackup.impl.jdbc.DbTable;
import org.jtalks.poulpe.logic.databasebackup.impl.jdbc.TableDataUtil;

import com.google.common.base.Joiner;

/**
 * The class represents a full table dump and is used to export all information (structure, data) about concrete table
 * from the database into text based SQL commands shape.
 * 
 * @author Evgeny Surovtsev
 * 
 */
class SqlTableDump {
    private final String tableName;
    private final String toStringRepresentation;
    private final DbTable tableInfo;

    /**
     * The constructor creates a text based representation of the table with the same name as the name given in the
     * constructor's parameters. For that constructor successively calls Create Table Structure (see
     * {@link #getTableStructure()}) and Export data (see {@link #getDumpedData()}) methods.
     * 
     * @param dataSource
     *            A Data Source object which will be used to access the database.
     * @param tableName
     *            the name of the table in the database which will be exported into text based form.
     * @throws SQLException
     *             is thrown when there is an SQL error during preparing the text based form of the table.
     */
    public SqlTableDump(final DataSource dataSource, final String tableName) throws SQLException {
        this.tableInfo = new DbTable(dataSource, tableName);
        this.tableName = tableName;

        this.toStringRepresentation = (getTableStructure().append(getDumpedData())).toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return toStringRepresentation;
    }

    /**
     * Formats and return the table structure in the shape of SQL statements which can be run lately on SQL console for
     * the table creation.
     * 
     * @return A text based SQL statement for creating the table.
     * @throws SQLException
     *             Is thrown in case any errors during work with database occur.
     */
    private StringBuffer getTableStructure() throws SQLException {
        Joiner joiner = Joiner.on("," + NEWLINE_CHAR).skipNulls();
        StringBuffer tableStructure = new StringBuffer(formatStructureHeader());
        tableStructure.append("CREATE TABLE IF NOT EXISTS " + TableDataUtil.getSqlColumnQuotedString(tableName)
                + " (" + NEWLINE_CHAR + joiner.join(getTableColumnDescriptionList()));

        List<String> tablePrimaryKeyList = getTablePrimaryKeyList();
        if (tablePrimaryKeyList.size() > 0) {
            tableStructure.append("," + NEWLINE_CHAR + joiner.join(tablePrimaryKeyList));
        }

        List<String> tableForeignKeyList = getTableForeignKeyList();
        if (tableForeignKeyList.size() > 0) {
            tableStructure.append("," + NEWLINE_CHAR + joiner.join(tableForeignKeyList));
        }

        tableStructure.append(NEWLINE_CHAR + ") " + getOtherTableParameters() + ";" + NEWLINE_CHAR + NEWLINE_CHAR);

        return tableStructure;
    }

    /**
     * Formats and returns additional parameters which are needed for the table creation (see
     * {@link #getTableStructure()}). Currently the method returns ENGINE, COLLATE and AUTO_INCREMENT keys for the
     * CREATE TABLE statement. The method returns only those key which defined for the table.
     * 
     * @return A string which contains additional parameters for the table creating and their values.
     * @throws SQLException
     *             Is thrown in case any errors during work with database occur.
     */
    private String getOtherTableParameters() throws SQLException {
        StringBuffer otherTableParameters = new StringBuffer();

        Map<String, String> parameters = tableInfo.getCommonParameterMap();
        for (String key : parameters.keySet()) {
            otherTableParameters.append(key + "=" + parameters.get(key) + " ");
        }

        return otherTableParameters.toString();
    }

    /**
     * Returns the list of SQL-shape formatted primary keys for the table.
     * 
     * @return List of the strings where each string contains a primary key's definition in the SQL terms and could be
     *         attached to the CREATE TABLE statement lately.
     * @throws SQLException
     *             Is thrown in case any errors during work with database occur.
     */
    private List<String> getTablePrimaryKeyList() throws SQLException {
        List<String> tablePrimaryKeyList = new ArrayList<String>();

        List<TablePrimaryKey> primaryKeyList = tableInfo.getPrimaryKeyList();
        for (TablePrimaryKey primaryKey : primaryKeyList) {
            tablePrimaryKeyList.add("    PRIMARY KEY ("
                    + TableDataUtil.getSqlColumnQuotedString(primaryKey.getPkColumnName()) + ")");
        }

        return tablePrimaryKeyList;
    }

    /**
     * Returns the list of SQL-shape formatted foreign keys for the table.
     * 
     * @return List of strings where each string represents a foreign's key definition in the SQL terms and could be
     *         attached to the CREATE TABLE statement lately.
     * @throws SQLException
     *             Is thrown in case any errors during work with database occur.
     */
    private List<String> getTableForeignKeyList() throws SQLException {
        List<String> tableForeignKeyList = new ArrayList<String>();

        List<TableForeignKey> foreignKeyList = tableInfo.getForeignKeyList();
        for (TableForeignKey foreignKey : foreignKeyList) {
            String foreignKeyDescription = "    KEY "
                    + TableDataUtil.getSqlColumnQuotedString(foreignKey.getFkTableName()) + " ("
                    + TableDataUtil.getSqlColumnQuotedString(foreignKey.getFkColumnName()) + ")," + NEWLINE_CHAR
                    + "    CONSTRAINT "
                    + TableDataUtil.getSqlColumnQuotedString(foreignKey.getFkTableName()) + " FOREIGN KEY ("
                    + TableDataUtil.getSqlColumnQuotedString(foreignKey.getFkColumnName()) + ") REFERENCES "
                    + TableDataUtil.getSqlColumnQuotedString(foreignKey.getPkTableName()) + " ("
                    + TableDataUtil.getSqlColumnQuotedString(foreignKey.getPkColumnName()) + ")";
            tableForeignKeyList.add(foreignKeyDescription);
        }

        return tableForeignKeyList;
    }

    /**
     * Returns the list of table's columns description. For each column information about column's name, type and other
     * parameters returns. The information is provided in the form ready to be inserted into SQL CREATE TABLE statement.
     * 
     * @return A list of strings where each string represents description of one column.
     * @throws SQLException
     *             Is thrown in case any errors during work with database occur.
     */
    private List<String> getTableColumnDescriptionList() throws SQLException {
        List<String> tableColumnDescriptionList = new ArrayList<String>();

        List<TableColumn> columnDescriptionList = tableInfo.getStructure();
        for (TableColumn column : columnDescriptionList) {
            StringBuffer columnDescription = new StringBuffer("    ");
            columnDescription.append(TableDataUtil.getSqlColumnQuotedString(column.getName()) + " " + column.getType());

            if (column.isHasSize()) {
                columnDescription.append("(" + column.getSize() + ")");
            }

            if (column.isNullable()) {
                columnDescription.append(" NULL");
            } else {
                columnDescription.append(" NOT NULL");
            }

            if (column.isHasDefaultValue()) {
                columnDescription.append(" DEFAULT " + column.getDefaultValue());
            }

            if (column.isAutoincrement()) {
                columnDescription.append(" AUTO_INCREMENT");
            }

            tableColumnDescriptionList.add(columnDescription.toString());
        }

        return tableColumnDescriptionList;
    }

    /**
     * Formats and return the table's actual data in the shape of SQL statements which can be run lately on SQL console
     * for inserting exported data into the table.
     * 
     * @return A text based SQL statements for inserting exported data into the table.
     * @throws SQLException
     *             Is thrown in case any errors during work with database occur.
     */
    private StringBuffer getDumpedData() throws SQLException {
        StringBuffer result = new StringBuffer(formatDumpedDataHeader());

        for (TableRow dataDump : tableInfo.getData()) {
            List<String> nameColumns = new ArrayList<String>();
            List<String> valueColumns = new ArrayList<String>();

            Map<String, String> params = dataDump.getColumnsValueMap();
            for (String column : params.keySet()) {
                nameColumns.add(TableDataUtil.getSqlColumnQuotedString(column));
                valueColumns.add(params.get(column));
            }
            Joiner joiner = Joiner.on(",").skipNulls();
            result.append("INSERT INTO " + TableDataUtil.getSqlColumnQuotedString(tableName) + " ("
                    + joiner.join(nameColumns) + ") VALUES (" + joiner.join(valueColumns) + ");"
                    + NEWLINE_CHAR);
        }

        result.append(NEWLINE_CHAR + NEWLINE_CHAR);
        return result;
    }

    /**
     * The method prints a header for the table's exporting data.
     * 
     * @return A text formated header for the dump file.
     */
    private StringBuffer formatDumpedDataHeader() {
        StringBuffer dumpedDataHeader = new StringBuffer();
        dumpedDataHeader.append("--" + NEWLINE_CHAR);
        dumpedDataHeader.append("-- Dumping data for table '" + tableName + "'" + NEWLINE_CHAR);
        dumpedDataHeader.append("--" + NEWLINE_CHAR + NEWLINE_CHAR);
        return dumpedDataHeader;
    }

    /**
     * The method prints a header for the table's exporting structure.
     * 
     * @return A text formated header for the dump file.
     */
    private StringBuffer formatStructureHeader() {
        StringBuffer structureHeader = new StringBuffer();
        structureHeader.append("--" + NEWLINE_CHAR);
        structureHeader.append("-- Table structure for table '" + tableName + "'" + NEWLINE_CHAR);
        structureHeader.append("--" + NEWLINE_CHAR + NEWLINE_CHAR);
        return structureHeader;
    }

    private static String NEWLINE_CHAR = "\n";
}