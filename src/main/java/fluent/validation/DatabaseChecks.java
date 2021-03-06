/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2021, Ondrej Fischer
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package fluent.validation;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Collection;

import static fluent.validation.BasicChecks.*;

public final class DatabaseChecks {
    private DatabaseChecks() {}

    private static final Check<ResultSet> HAS_MORE_RECORDS = sqlCheck("has more records", ResultSet::next, equalTo(true));
    private static final Check<ResultSet> HAS_NO_MORE_RECORDS = sqlCheck("has no more records", ResultSet::next, equalTo(false));

    public static Check<ResultSet> hasMoreRecords() {
        return HAS_MORE_RECORDS;
    }

    public static Check<ResultSet> hasNoMoreRecords() {
        return HAS_NO_MORE_RECORDS;
    }

    public static Check<ResultSet> resultSetEqualTo(Collection<Check<? super ResultSet>> checks) {
        return new ResultSetCheckInOrder(checks, true, true);
    }

    public static Check<ResultSet> resultSetEqualInAnyOrderTo(Collection<Check<? super ResultSet>> checks) {
        return new ResultSetCheckInAnyOrder(checks, true, true);
    }

    public static Check<ResultSet> resultSetStartsWith(Collection<Check<? super ResultSet>> checks) {
        return new ResultSetCheckInOrder(checks, false, true);
    }

    public static Check<ResultSet> resultSetStartsInAnyOrderWith(Collection<Check<? super ResultSet>> checks) {
        return new ResultSetCheckInAnyOrder(checks, false, true);
    }

    public static Check<ResultSet> resultSetContains(Collection<Check<? super ResultSet>> checks) {
        return new ResultSetCheckInOrder(checks, false, false);
    }

    public static Check<ResultSet> resultSetContainsInAnyOrder(Collection<Check<? super ResultSet>> checks) {
        return new ResultSetCheckInAnyOrder(checks, false, false);
    }

    public static <R> Check<ResultSet> sqlCheck(String name, Transformation<ResultSet, R> function, Check<? super R> check) {
        return compose(name, function, check);
    }

    public static <R> Check<ResultSet> sqlCheck(int position, Transformation<ResultSet, R> function, Check<? super R> check) {
        return sqlCheck("column [" + position + "]", function, check);
    }



    public static <T> Check<ResultSet> column(String label, Class<T> type, Check<? super T> check) {
        return sqlCheck(label, resultSet -> resultSet.getObject(label, type), check);
    }

    public static <T> Check<ResultSet> column(int position, Class<T> type, Check<? super T> check) {
        return sqlCheck(position, resultSet -> resultSet.getObject(position, type), check);
    }

    public static Check<ResultSet> intColumn(String label, Check<? super Integer> check) {
        return sqlCheck(label, resultSet -> resultSet.getInt(label), check);
    }

    public static Check<ResultSet> intColumn(int position, Check<? super Integer> check) {
        return sqlCheck(position, resultSet -> resultSet.getInt(position), check);
    }

    public static Check<ResultSet> booleanColumn(String label, Check<? super Boolean> check) {
        return sqlCheck(label, resultSet -> resultSet.getBoolean(label), check);
    }

    public static Check<ResultSet> booleanColumn(int position, Check<? super Boolean> check) {
        return sqlCheck(position, resultSet -> resultSet.getBoolean(position), check);
    }

    public static Check<ResultSet> stringColumn(String label, Check<? super String> check) {
        return sqlCheck(label, resultSet -> resultSet.getString(label), check);
    }

    public static Check<ResultSet> stringColumn(int position, Check<? super String> check) {
        return sqlCheck(position, resultSet -> resultSet.getString(position), check);
    }

    public static Check<ResultSet> doubleColumn(String label, Check<? super Double> check) {
        return sqlCheck(label, resultSet -> resultSet.getDouble(label), check);
    }

    public static Check<ResultSet> doubleColumn(int position, Check<? super Double> check) {
        return sqlCheck(position, resultSet -> resultSet.getDouble(position), check);
    }

    public static Check<ResultSet> byteColumn(String label, Check<? super Byte> check) {
        return sqlCheck(label, resultSet -> resultSet.getByte(label), check);
    }

    public static Check<ResultSet> byteColumn(int position, Check<? super Byte> check) {
        return sqlCheck(position, resultSet -> resultSet.getByte(position), check);
    }

    public static Check<ResultSet> bytesColumn(String label, Check<? super byte[]> check) {
        return sqlCheck(label, resultSet -> resultSet.getBytes(label), check);
    }

    public static Check<ResultSet> bytesColumn(int position, Check<? super byte[]> check) {
        return sqlCheck(position, resultSet -> resultSet.getBytes(position), check);
    }



    public static Check<ResultSet> metadata(Check<? super ResultSetMetaData> check) {
        return sqlCheck("metadata", ResultSet::getMetaData, check);
    }

    public static <V> Check<ResultSetMetaData> metadataCheck(String name, Transformation<ResultSetMetaData, V> function, Check<? super V> check) {
        return compose(name, function, check);
    }

    public static Check<ResultSetMetaData> columnCount(int expectedValue) {
        return metadataCheck("column count", ResultSetMetaData::getColumnCount, equalTo(expectedValue));
    }

    public static Check<ResultSetMetaData> columnName(int position, String expectedName) {
        return metadataCheck("column " + position + " name", r -> r.getColumnName(position), equalTo(expectedName));
    }


    public static <T> Check<CallableStatement> outParameter(int position, Class<T> type, Check<? super T> check) {
        return compose("", (CallableStatement c) -> c.getObject(position, type), check);
    }

    public static Check<PreparedStatement> executeQuery(Check<? super ResultSet> check) {
        return has("", (Transformation<PreparedStatement, ResultSet>) PreparedStatement::executeQuery).matching(check);
    }
}
