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

package fluent.validation.data;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

import static fluent.validation.data.Data.combineWith;
import static java.util.stream.Collectors.toList;

public class DataTest {

    @Test
    public void testCombineArrays() {
        Collection<Object[]> A = Stream.of(new Object[]{1,2}, new Object[]{3,4}).collect(toList());
        Collection<Object[]> B = Stream.of(new Object[]{"A","B"}, new Object[]{"C","D"}).collect(toList());
        Collection<Object[]> C = Stream.of(new Object[]{5,6}, new Object[]{7,8}).collect(toList());

        Iterator<Object[]> iterator = A.stream().flatMap(combineWith(B)).flatMap(combineWith(C)).iterator();

        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(iterator.next(), new Object[] {1,2,"A","B",5,6});
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(iterator.next(), new Object[] {1,2,"A","B",7,8});
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(iterator.next(), new Object[] {1,2,"C","D",5,6});
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(iterator.next(), new Object[] {1,2,"C","D",7,8});
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(iterator.next(), new Object[] {3,4,"A","B",5,6});
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(iterator.next(), new Object[] {3,4,"A","B",7,8});
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(iterator.next(), new Object[] {3,4,"C","D",5,6});
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(iterator.next(), new Object[] {3,4,"C","D",7,8});
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testExpandArrays() {
        Iterator<Object[]> iterator = Data.expand(Stream.of(new Object[][]{
                {1, 1, 1, 1},
                {         2},
                {      2, 1},
                {         2},
                {2, 1, 1, 1},
                {   2, 1, 1},
                {   3, 1, 1},
        })).iterator();

        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(iterator.next(), new Object[] {1, 1, 1, 1});
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(iterator.next(), new Object[] {1, 1, 1, 2});
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(iterator.next(), new Object[] {1, 1, 2, 1});
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(iterator.next(), new Object[] {1, 1, 2, 2});
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(iterator.next(), new Object[] {2, 1, 1, 1});
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(iterator.next(), new Object[] {2, 2, 1, 1});
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(iterator.next(), new Object[] {2, 3, 1, 1});
        Assert.assertFalse(iterator.hasNext());

    }

}
