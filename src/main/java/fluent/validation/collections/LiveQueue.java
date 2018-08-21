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
    private final Node<E> head;
    private final Node<E> tail;
    private final Size size;
    private final Object lock;

    public LiveQueue(Duration timeout, Mode mode) {
        this(new Node<>(null), new Node<>(null), new Size(), timeout, mode == Mode.REMOVE_ON_NEXT);
        clear();
    }

    public LiveQueue(Duration timeout) {
        this(timeout, Mode.KEEP_ALL);
    }

    private LiveQueue(Node<E> head, Node<E> tail, Size size, Duration timeout, boolean removeOnNext) {
        this.head = head;
        this.tail = tail;
        this.size = size;
        this.timeout = timeout.toMillis();
        this.removeOnNext = removeOnNext;
        this.lock = head;
    }

    static Object lockOf(LiveQueue<?> queue) {
        return queue.lock;
    }

    public static <E> Queue<E> withTimeout(Queue<E> queue, Duration timeout) {
        return queue instanceof LiveQueue ? ((LiveQueue<E>) queue).withTimeout(timeout) : queue;
    }

    public LiveQueue<E> withTimeout(Duration timeout) {
        return new LiveQueue<>(head, tail, size, timeout, removeOnNext);
    }

    private boolean end(Node<E> node) {
        return node == tail;
    }

    private Node<E> getNext(Node<E> node) {
        synchronized (lock) {
            for(long r = timeout, d = currentTimeMillis() + r; end(node.next) && r > 0; r = d - currentTimeMillis()) {
                try {
                    lock.wait(r);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return node.next;
        }
    }

    private void to(Node<E> prev, Node<E> next) {
        prev.next = next;
        next.prev = prev;
    }

    private void removeNode(Node<E> node) {
        size.size--;
        to(node.prev, node.next);
    }

    @Override
    public int size() {
        return size.size;
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
                synchronized (lock) {
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
                synchronized (lock) {
                    if(node == head) {
                        throw new IllegalStateException("next() not called yet");
                    }
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
    public boolean add(E e) {
        synchronized (lock) {
            Node<E> node = new Node<>(e);
            to(tail.prev, node);
            to(node, tail);
            size.size++;
            lock.notifyAll();
            return false;
        }
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
    public void clear() {
        synchronized (lock) {
            to(head, tail);
            size.size = 0;
        }
    }

    @Override
    public boolean offer(E e) {
        return add(e);
    }

    private final E checkElement(E element) {
        if(element == null) {
            throw new NoSuchElementException();
        }
        return element;
    }

    @Override
    public E remove() {
        return checkElement(poll());
    }

    @Override
    public E poll() {
        synchronized (lock) {
            Node<E> node = getNext(head);
            if(end(node)) {
                return null;
            }
            removeNode(node);
            return node.value;
        }
    }

    @Override
    public E element() {
        return checkElement(peek());
    }

    @Override
    public E peek() {
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

    private static final class Size {
        private int size = 0;
    }

    public enum Mode {
        KEEP_ALL,
        REMOVE_ON_NEXT
    }

}
