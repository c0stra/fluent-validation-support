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

import fluent.validation.result.Result;
import fluent.validation.result.ResultFactory;
import fluent.validation.result.TableAggregator;

import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.*;
import static java.util.stream.IntStream.range;

final class AnyOrderCheck<D> extends Check<Iterator<D>> {

    private final String elementName;
    private final ArrayList<Check<? super D>> checks;
    private final boolean prefix;
    private final boolean contains;
    private final boolean all;

    AnyOrderCheck(String elementName, Collection<Check<? super D>> checks, boolean full, boolean exact, boolean all) {
        this.elementName = elementName;
        this.checks = new ArrayList<>(checks);
        this.prefix = !full;
        this.contains = !exact;
        this.all = all;
    }

    @Override
    public String toString() {
        return elementName + "s matching in any order " + checks;
    }

    @Override
    public Result evaluate(Iterator<D> data, ResultFactory factory) {
        if(data == null) {
            return factory.expectation(this, false);
        }
        Set<D> free = new HashSet<>();
        TableAggregator<D> resultBuilder = factory.table(this, checks);
        Map<D, List<Check<? super D>>> graph = new HashMap<>();
        Map<Check<? super D>, D> pairs = new HashMap<>();
        while (data.hasNext()) {
            D item = data.next();
            free.add(item);
            int c = resultBuilder.column(item);
            List<Check<? super D>> matches = range(0, checks.size()).filter(r -> resultBuilder.cell(r, c, checks.get(r).evaluate(item, factory)).passed()).mapToObj(checks::get).collect(toList());
            graph.put(item, matches);
            updatePairs(free, pairs, graph);
            if(!all && pairs.size() == checks.size()) {
                return contains || free.isEmpty() && (prefix || !data.hasNext())
                        ? resultBuilder.build("All checks satisfied", true)
                        : resultBuilder.build("Unexpected " + elementName, false);
            }
        }
        int unsatisfied = checks.size() - pairs.size();
        return (unsatisfied == 0 && graph.values().stream().noneMatch(List::isEmpty))
                ? resultBuilder.build("All checks satisfied", true)
                : resultBuilder.build(unsatisfied + (unsatisfied == 1 ? " check" : " checks") + " not satisfied", false);
    }

    private static  <A, B> void updatePairs(Set<A> free, Map<B, A> pairs, Map<A, List<B>> graph) {
        for(Edge<B, A> path = findImprovedPath(free, pairs, graph); path != null; path = findImprovedPath(free, pairs, graph)) {
            for(; path.prev.prev != path; path = path.prev.prev) {
                pairs.put(path.value, path.prev.value);
            }
            free.remove(path.prev.value);
        }
    }

    private static <A, B> Edge<B, A> findImprovedPath(Set<A> free, Map<B, A> pairs, Map<A, List<B>> graph) {
        Queue<Edge<A, B>> queue = free.stream().map(Edge<A, B>::new).collect(toCollection(LinkedList::new));
        Set<A> visited = new HashSet<>();
        while (!queue.isEmpty()) {
            Edge<A, B> edge = queue.poll();
            if(visited.add(edge.value)) for(B b : graph.get(edge.value)) {
                if(!pairs.containsKey(b)) {
                    return new Edge<>(edge, b);
                } else {
                    queue.add(new Edge<>(new Edge<>(edge, b), pairs.get(b)));
                }
            }
        }
        return null;
    }

    private final static class Edge<A, B> {
        private final Edge<B, A> prev;
        private final A value;

        private Edge(Edge<B, A> prev, A value) {
            this.prev = prev;
            this.value = value;
        }

        private Edge(A value) {
            this.prev = new Edge<>(this, null);
            this.value = value;
        }
    }

}
