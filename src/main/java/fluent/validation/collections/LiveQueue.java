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

import java.time.Duration;
import java.util.*;

import static java.lang.System.currentTimeMillis;

/**
 * Live queue - blocking queue with open-ended iterator and configured timeout.
 *
 * @param <E> Type of the elements in the queue.
 */
public final class LiveQueue<E> implements Queue<E> {

    private final long timeout;
    private final boolean removeOnNext;
    private final Node<E> head = new Node<>(null);
    private final Node<E> tail = new Node<>(null);
    private int size;

    public LiveQueue(Duration timeout, Mode mode) {
        this.timeout = timeout.toMillis();
        this.removeOnNext = mode == Mode.REMOVE_ON_NEXT;
        clear();
    }

    public LiveQueue(Duration timeout) {
        this(timeout, Mode.KEEP_ALL);
    }

    private boolean end(Node<E> node) {
        return node == tail;
    }

    private synchronized Node<E> getNext(Node<E> node) {
        for(long r = timeout, d = currentTimeMillis() + r; end(node.next) && r > 0; r = d - currentTimeMillis()) {
            try {
                wait(r);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return node.next;
    }

    private void to(Node<E> prev, Node<E> next) {
        prev.next = next;
        next.prev = prev;
    }

    private void removeNode(Node<E> node) {
        size--;
        to(node.prev, node.next);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return end(head.next);
    }

    @Override
    public boolean contains(Object o) {
        for (E e : this) {
            if(e.equals(o)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private Node<E> node = head;
            @Override
            public boolean hasNext() {
                return !end(getNext(node));
            }

            @Override
            public E next() {
                synchronized (LiveQueue.this) {
                    Node<E> next = getNext(node);
                    if (end(next)) {
                        throw new NoSuchElementException();
                    }
                    if (removeOnNext && node != head) {
                        removeNode(node);
                    }
                    node = next;
                    return next.value;
                }
            }

            @Override
            public void remove() {
                if(node == head) {
                    throw new IllegalStateException("next() not called yet");
                }
                synchronized (LiveQueue.this) {
                    removeNode(node);
                }
            }
        };
    }

    @Override
    public Object[] toArray() {
        List<E> list = new LinkedList<>();
        for(Node<E> node = head.next; !end(node); node = node.next) {
            list.add(node.value);
        }
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        List<E> list = new LinkedList<>();
        for(Node<E> node = head.next; !end(node); node = node.next) {
            list.add(node.value);
        }
        return list.toArray(a);
    }

    @Override
    public synchronized boolean add(E e) {
        Node<E> node = new Node<>(e);
        to(tail.prev, node);
        to(node, tail);
        size++;
        notifyAll();
        return false;
    }

    @Override
    public boolean remove(Object o) {
        for(Node<E> node = head.next; !end(node); node = node.next) {
            if(node.value.equals(o)) {
                removeNode(node);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        Set<?> set = new HashSet<>(c);
        for (E e : this) {
            set.remove(e);
        }
        return set.isEmpty();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        c.forEach(this::add);
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        Set<?> set = new HashSet<>(c);
        boolean result = false;
        for(Node<E> node = head.next; !end(node); node = node.next) {
            if(set.contains(node.value)) {
                removeNode(node);
                result = true;
            }
        }
        return result;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Set<?> set = new HashSet<>(c);
        boolean result = false;
        for(Node<E> node = head.next; !end(node); node = node.next) {
            if(!set.contains(node.value)) {
                removeNode(node);
                result = true;
            }
        }
        return result;
    }

    @Override
    public synchronized void clear() {
        to(head, tail);
        size = 0;
    }

    @Override
    public boolean offer(E e) {
        return add(e);
    }

    @Override
    public synchronized E remove() {
        Node<E> node = getNext(head);
        if(end(node)) {
            throw new NoSuchElementException();
        }
        removeNode(node);
        return node.value;
    }

    @Override
    public synchronized E poll() {
        Node<E> node = getNext(head);
        if(end(node)) {
            return null;
        }
        removeNode(node);
        return node.value;
    }

    @Override
    public synchronized E element() {
        Node<E> node = getNext(head);
        if(end(node)) {
            throw new NoSuchElementException();
        }
        return node.value;
    }

    @Override
    public synchronized E peek() {
        Node<E> node = getNext(head);
        return end(node) ? null : node.value;
    }

    private static final class Node<E> {
        private final E value;
        private Node<E> next;
        private Node<E> prev;
        private Node(E value) {
            this.value = value;
        }
    }

    public enum Mode {
        KEEP_ALL,
        REMOVE_ON_NEXT
    }

}
