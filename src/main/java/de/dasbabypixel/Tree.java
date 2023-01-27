package de.dasbabypixel;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public interface Tree<E> extends Set<E> {

	interface TreeIterator<E> extends Iterator<E> {
		int height();

		int depth();
	}

	@Override
	String toString();

	String toString(IterationStrategy strategy);

	class AVLBaum<Entry> implements Tree<Entry> {

		private Node root;
		private final Comparator<? super Entry> comparator;

		public AVLBaum(Comparator<? super Entry> comparator) {
			this.comparator = comparator;
		}

		@Override
		public int size() {
			return root == null ? 0 : root.size;
		}

		@Override
		public boolean isEmpty() {
			return root == null;
		}

		@Override
		public boolean contains(Object o) {
			return searchNode(o) != null;
		}

		@SuppressWarnings("unchecked")
		private Node searchNode(Object data) {
			return searchNode((Entry) data, root);
		}

		public int height() {
			if (root == null)
				return 0;
			return root.height + 1;
		}

		private Node searchNode(Entry data, Node node) {
			if (node == null)
				return null;
			int comp = comparator.compare(data, node.data);
			if (comp == 0)
				return node;
			if (comp < 0)
				return searchNode(data, node.left);
			else
				return searchNode(data, node.right);
		}

		private Node insertNode(Entry data, Node node, Node parent) {
			if (node == null)
				node = new Node(data);
			else if (comparator.compare(data, node.data) < 0)
				node.left = insertNode(data, node.left, node);
			else if (comparator.compare(data, node.data) > 0)
				node.right = insertNode(data, node.right, node);
			else
				throw new IllegalArgumentException("Tree already has that element: " + data);
			node.parent = parent;
			update(node);
			return balance(node);
		}

		public boolean add(Entry data) {
			insertNode(data);
			return true;
		}

		private void insertNode(Entry data) {
			root = insertNode(data, root, null);
		}

		@Override
		@SuppressWarnings("unchecked")
		public boolean remove(Object o) {
			return deleteNode((Entry) o);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			for (Object o : c)
				if (!contains(o))
					return false;
			return true;
		}

		@Override
		public boolean addAll(Collection<? extends Entry> c) {
			boolean changed = false;
			for (Entry t : c)
				if (add(t))
					changed = true;
			return changed;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			return removeIf(entry -> !c.contains(entry));
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			Iterator<?> it = c.iterator();
			boolean changed = false;
			while (it.hasNext())
				if (remove(it.next()))
					changed = true;
			return changed;
		}

		@Override
		public void clear() {
			root = null;
		}

		private boolean deleteNode(Entry data) {
			AtomicBoolean changed = new AtomicBoolean(false);
			root = deleteNode(data, root, changed);
			return changed.get();
		}

		private Node deleteNode(Entry data, Node node, AtomicBoolean changed) {
			if (node == null)
				return null;
			Node parent = node.parent;
			int comp = comparator.compare(data, node.data);
			if (comp < 0)
				node.left = deleteNode(data, node.left, changed);
			else if (comp > 0)
				node.right = deleteNode(data, node.right, changed);
			else if (node.left == null && node.right == null) {
				changed.set(true);
				return null;
			} else if (node.left == null) {
				changed.set(true);
				node = node.right;
			} else if (node.right == null) {
				changed.set(true);
				node = node.left;
			} else {
				deleteNodeWithTwoChildren(node, changed);
			}
			node.parent = parent;
			update(node);
			return balance(node);
		}

		private void deleteNodeWithTwoChildren(Node node, AtomicBoolean changed) {
			changed.set(true);
			Node inOrderSuccessor = findMinimum(node.right);
			node.data = inOrderSuccessor.data;
			node.right = deleteNode(inOrderSuccessor.data, node.right, changed);
		}

		private int height(Node node) {
			return node != null ? node.height : -1;
		}

		private void update(Node node) {
			if (node == null)
				return;
			int leftChildHeight = height(node.left);
			int rightChildHeight = height(node.right);
			node.height = Math.max(leftChildHeight, rightChildHeight) + 1;
			node.size = (node.left != null ? node.left.size : 0) + (node.right != null
					? node.right.size
					: 0) + 1;
		}

		private Node rotateRight(Node node) {
			Node leftChild = node.left;
			leftChild.parent = node.parent;

			node.left = leftChild.right;
			if (node.left != null)
				node.left.parent = node;
			leftChild.right = node;
			leftChild.right.parent = leftChild;

			update(node);
			update(leftChild);
			update(node.left);

			return leftChild;
		}

		private Node rotateLeft(Node node) {
			Node rightChild = node.right;
			rightChild.parent = node.parent;

			node.right = rightChild.left;
			if (node.right != null)
				node.right.parent = node;
			rightChild.left = node;
			rightChild.left.parent = rightChild;

			update(node);
			update(rightChild);
			update(node.right);

			return rightChild;
		}

		private Node balance(Node node) {
			int balanceFactor = balanceFactor(node);
			if (balanceFactor < -1) {
				if (balanceFactor(node.left) > 0)
					node.left = rotateLeft(node.left);
				node = rotateRight(node);
			}

			if (balanceFactor > 1) {
				if (balanceFactor(node.right) < 0)
					node.right = rotateRight(node.right);
				node = rotateLeft(node);
			}
			return node;
		}

		private int balanceFactor(Node node) {
			return height(node.right) - height(node.left);
		}

		private Node findMinimum(Node node) {
			if (node == null)
				return null;
			while (node.left != null)
				node = node.left;
			return node;
		}

		@Override
		public String toString() {
			return toString(IterationStrategy.IN_ORDER);
		}

		public String toString(IterationStrategy strategy) {
			switch (strategy) {
				case IN_ORDER:
					return toStringInOrder(root, new StringBuilder(), 0).toString();
				case PRE_ORDER:
					return toStringPreOrder(root, new StringBuilder(), 0).toString();
				case POST_ORDER:
					return toStringPostOrder(root, new StringBuilder(), 0).toString();
			}
			throw new IllegalArgumentException();
		}

		private StringBuilder toStringInOrder(Node node, StringBuilder builder, int indent) {
			if (node == null)
				return builder;
			toStringInOrder(node.left, builder, indent + 1);
			builder.append(String.format("%-" + (indent * 7 + 1) + "s", ""));
			builder.append(node.data);
			builder.append('\n');
			toStringInOrder(node.right, builder, indent + 1);
			return builder;
		}

		private StringBuilder toStringPreOrder(Node node, StringBuilder builder, int indent) {
			if (node == null)
				return builder;
			builder.append(String.format("%-" + (indent * 7 + 1) + "s", ""));
			builder.append(node.data);
			builder.append('\n');
			toStringPreOrder(node.left, builder, indent + 1);
			toStringPreOrder(node.right, builder, indent + 1);
			return builder;
		}

		private StringBuilder toStringPostOrder(Node node, StringBuilder builder, int indent) {
			if (node == null)
				return builder;
			toStringPostOrder(node.left, builder, indent + 1);
			toStringPostOrder(node.right, builder, indent + 1);
			builder.append(String.format("%-" + (indent * 7 + 1) + "s", ""));
			builder.append(node.data);
			builder.append('\n');
			return builder;
		}

		private class Node {
			Node(Entry data) {
				this.data = data;
			}

			Node parent;
			Entry data;
			Node left;
			Node right;
			int height;
			int size;

		}

		@Override
		public TreeIterator<Entry> iterator() {
			return iterator(IterationStrategy.IN_ORDER);
		}

		@Override
		public Object[] toArray() {
			Object[] r = new Object[size()];
			Iterator<Entry> it = iterator();
			for (int i = 0; i < r.length; i++) {
				if (!it.hasNext()) // fewer elements than expected
					return Arrays.copyOf(r, i);
				r[i] = it.next();
			}
			return it.hasNext() ? finishToArray(r, it) : r;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> T[] toArray(T[] a) {
			// Estimate size of array; be prepared to see more or fewer elements
			int size = size();
			T[] r = a.length >= size
					? a
					: (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(),
							size);
			Iterator<Entry> it = iterator();

			for (int i = 0; i < r.length; i++) {
				if (!it.hasNext()) { // fewer elements than expected
					if (a == r) {
						r[i] = null; // null-terminate
					} else if (a.length < i) {
						return Arrays.copyOf(r, i);
					} else {
						System.arraycopy(r, 0, a, 0, i);
						if (a.length > i) {
							a[i] = null;
						}
					}
					return a;
				}
				r[i] = (T) it.next();
			}
			// more elements than expected
			return it.hasNext() ? finishToArray(r, it) : r;
		}

		@SuppressWarnings("unchecked")
		private static <T> T[] finishToArray(T[] r, Iterator<?> it) {
			int len = r.length;
			int i = len;
			while (it.hasNext()) {
				if (i == len) {
					len = len + (len >> 1) + 1;
					r = Arrays.copyOf(r, len);
				}
				r[i++] = (T) it.next();
			}
			// trim if overallocated
			return (i == len) ? r : Arrays.copyOf(r, i);
		}

		public TreeIterator<Entry> iterator(IterationStrategy strategy) {
			return new Itr(strategy);
		}

		private class Itr implements TreeIterator<Entry> {

			private Node cur = null;
			private boolean hasNextCache = false;
			private boolean removed = false;
			private int indent = 0;
			private Node nextCache = null;
			private final IterationStrategy strategy;

			public Itr(IterationStrategy strategy) {
				this.strategy = strategy;
			}

			private Node successor(Node element) {
				switch (strategy) {
					case IN_ORDER:
						return nextInOrder(element);
					case POST_ORDER:
						return nextPostOrder(element);
					case PRE_ORDER:
						return nextPreOrder(element);
				}
				throw new IllegalStateException();
			}

			private Node nextInOrder(Node element) {
				if (element == null) {
					indent = 0;
					element = root;
					while (element.left != null) {
						indent++;
						element = element.left;
					}
					return element;
				} else if (element.right != null) {
					indent++;
					element = element.right;
					while (element.left != null) {
						indent++;
						element = element.left;
					}
					return element;
				}
				if (element.parent == null) {
					return null;
				}
				if (element.parent.right == element) {
					while (element.parent != null && element.parent.right == element) {
						element = element.parent;
						indent--;
					}
				}
				indent--;
				return element.parent;
			}

			private Node nextPreOrder(Node element) {
				if (element == null) {
					indent = 0;
					return root;
				} else if (element.left != null) {
					indent++;
					return element.left;
				} else if (element.right != null) {
					indent++;
					return element.right;
				}
				do {
					if (element.parent == null) {
						return null;
					} else if (element.parent.left == element && element.parent.right != null) {
						return element.parent.right;
					} else {
						indent--;
						element = element.parent;
					}
				} while (true);
			}

			private Node nextPostOrder(Node element) {
				if (element == null) {
					indent = 0;
					return postOrderLoop(root);
				} else if (element.parent == null) {
					return null;
				} else if (element.parent.right != null && element.parent.right != element) {
					return postOrderLoop(element.parent.right);
				} else {
					indent--;
					return element.parent;
				}
			}

			private Node postOrderLoop(Node element) {
				if (element == null)
					return null;
				while (true) {
					if (element.left != null) {
						indent++;
						element = element.left;
					} else if (element.right != null) {
						indent++;
						element = element.right;
					} else {
						return element;
					}
				}
			}

			@Override
			public int depth() {
				if (cur == null)
					throw new NoSuchElementException();
				return indent;
			}

			@Override
			public int height() {
				if (cur == null)
					throw new NoSuchElementException();
				return cur.height + 1;
			}

			/**
			 * Returns {@code true} if the iteration has more elements. (In other words, returns
			 * {@code true} if {@link #next} would return an element rather than throwing an
			 * exception.)
			 *
			 * @return {@code true} if the iteration has more elements
			 */
			@Override
			public boolean hasNext() {
				if (!hasNextCache) {
					hasNextCache = true;
					nextCache = successor(cur);
				}
				return nextCache != null;
			}

			/**
			 * Returns the next element in the iteration.
			 *
			 * @return the next element in the iteration
			 *
			 * @throws NoSuchElementException if the iteration has no more elements
			 */
			@Override
			public Entry next() {
				if (hasNextCache) {
					hasNextCache = false;
					cur = nextCache;
				} else {
					cur = successor(cur);
				}
				removed = false;
				if (cur == null)
					throw new NoSuchElementException();
				return cur.data;
			}

			/**
			 * Removes from the underlying collection the last element returned by this iterator
			 * (optional operation).  This method can be called only once per call to
			 * {@link #next}.
			 * <p>
			 * The behavior of an iterator is unspecified if the underlying collection is modified
			 * while the iteration is in progress in any way other than by calling this method,
			 * unless an overriding class has specified a concurrent modification policy.
			 * <p>
			 * The behavior of an iterator is unspecified if this method is called after a call to
			 * the {@link #forEachRemaining forEachRemaining} method.
			 *
			 * @throws UnsupportedOperationException if the {@code remove} operation is not
			 *                                       supported by this iterator
			 * @throws IllegalStateException         if the {@code next} method has not yet been
			 *                                       called, or the {@code remove} method has
			 *                                       already been called after the last call to the
			 *                                       {@code next} method
			 * @implSpec The default implementation throws an instance of
			 * {@link UnsupportedOperationException} and performs no other action.
			 */
			@Override
			public void remove() {
				if (strategy != IterationStrategy.IN_ORDER)
					throw new UnsupportedOperationException(
							"Remove only supported for in-order iterators");
				if (removed)
					throw new IllegalStateException("Can't remove an element twice");
				if (cur == null)
					throw new IllegalStateException("Must call #next() before calling remove");
				removed = true;
				Node nextNode = successor(cur);
				Entry nextData = nextNode == null ? null : nextNode.data;
				if (!AVLBaum.this.remove(cur.data)) {
					throw new UnsupportedOperationException("Couldn't remove");
				}
				if (nextData != null) {
					nextCache = searchNode(nextData);
					hasNextCache = true;
				}
			}
		}
	}


	enum IterationStrategy {
		IN_ORDER, PRE_ORDER, POST_ORDER
	}

}
