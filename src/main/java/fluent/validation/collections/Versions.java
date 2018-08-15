/*
 * Copyright © 2018 Ondrej Fischer. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that
 * the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. The name of the author may not be used to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY [LICENSOR] "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

package fluent.validation.collections;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import static java.util.Objects.isNull;

public final class Versions<D> implements Queue<D> {

    private final Queue<D> queue;
    private final Lock lock;
    private final Condition condition;
    private final String name;
    private final long timeout;

    Versions(Queue<D> queue, Lock lock, Condition condition, String name, long timeout) {
        this.lock = lock;
        this.condition = condition;
        this.timeout = timeout;
        this.queue = queue;
        this.name = name;
    }

    public static <D> Versions<D> versions(String name, long timeout) {
        Lock lock = new ReentrantLock();
        return new Versions<>(new LinkedList<>(), lock, lock.newCondition(), name, timeout);
    }

    public Versions<D> view(long timeout) {
        return new Versions<>(queue, lock, condition, name, timeout);
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return queue.contains(o);
    }

    @Override
    public Iterator<D> iterator() {
        return new Iterator<D>() {
            @Override public boolean hasNext() {
                return block(() -> true, false);
            }
            @Override public D next() {
                return queue.poll();
            }
        };
    }

    @Override
    public Object[] toArray() {
        return queue.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return queue.toArray(a);
    }

    @Override
    public synchronized D peek() {
        return block(queue::peek, null);
    }

    @Override
    public boolean add(D data) {
        return signal(() -> queue.add(data));
    }

    @Override
    public boolean remove(Object o) {
        return queue.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return queue.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends D> c) {
        return signal(() -> queue.addAll(c));
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return queue.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return queue.retainAll(c);
    }

    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public boolean offer(D d) {
        return signal(() -> queue.offer(d));
    }

    @Override
    public D remove() {
        return queue.remove();
    }

    @Override
    public D poll() {
        return block(queue::poll, null);
    }

    @Override
    public D element() {
        D d = block(queue::element, null);
        if(isNull(d)) throw new NoSuchElementException("No " + name);
        return d;
    }

    private <E> E block(Supplier<E> supplier, E onTimeoutValue) {
        Date deadline = new Date(System.currentTimeMillis() + timeout);
        lock.lock();
        try {
            while (isEmpty()) {
                if (!condition.awaitUntil(deadline)) {
                    return onTimeoutValue;
                }
            }
            return supplier.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    private <E> E signal(Supplier<E> supplier) {
        lock.lock();
        try {
            E e = supplier.get();
            condition.signalAll();
            return e;
        } finally {
            lock.unlock();
        }
    }

}
