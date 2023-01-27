package de.dasbabypixel;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class AVLBaum<T> implements Iterable<T> {

	private Node root;
	private final Comparator<? super T> comparator;

	public AVLBaum(Comparator<? super T> comparator) {
		this.comparator = comparator;
	}

	public boolean contains(T data) {
		return searchNode(data) != null;
	}

	private Node searchNode(T data) {
		return searchNode(data, root);
	}

	public int height() {
		if (root == null)
			return 0;
		return root.height + 1;
	}

	private Node searchNode(T data, Node node) {
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

	private Node insertNode(T data, Node node) {
		if (node == null)
			node = new Node(data);
		else if (comparator.compare(data, node.data) < 0)
			node.left = insertNode(data, node.left);
		else if (comparator.compare(data, node.data) > 0)
			node.right = insertNode(data, node.right);
		else
			throw new IllegalArgumentException("Baum enthält bereits ein solches Element: " + data);
		updateHeight(node);
		return balance(node);
	}

	public void add(T data) {
		insertNode(data);
	}

	private void insertNode(T data) {
		root = insertNode(data, root);
	}

	public boolean remove(T data) {
		return null != deleteNode(data);
	}

	private Node deleteNode(T data) {
		return root = deleteNode(data, root);
	}

	private Node deleteNode(T data, Node node) {
		if (node == null)
			return null;
		if (comparator.compare(data, node.data) < 0)
			node.left = deleteNode(data, node.left);
		else if (comparator.compare(data, node.data) > 0)
			node.right = deleteNode(data, node.right);
		else if (node.left == null && node.right == null)
			node = null;
		else if (node.left == null)
			node = node.right;
		else if (node.right == null)
			node = node.left;
		else
			deleteNodeWithTwoChildren(node);
		if (node == null)
			return null;
		updateHeight(node);
		return balance(node);
	}

	private void deleteNodeWithTwoChildren(Node node) {
		Node inOrderSuccessor = findMinimum(node);
		node.data = inOrderSuccessor.data;
		node.right = deleteNode(inOrderSuccessor.data, node.right);
	}

	private int height(Node node) {
		return node != null ? node.height : -1;
	}

	private void updateHeight(Node node) {
		int leftChildHeight = height(node.left);
		int rightChildHeight = height(node.right);
		node.height = Math.max(leftChildHeight, rightChildHeight) + 1;
	}

	private Node rotateRight(Node node) {
		Node leftChild = node.left;

		node.left = leftChild.right;
		leftChild.right = node;

		updateHeight(node);
		updateHeight(leftChild);

		return leftChild;
	}

	private Node rotateLeft(Node node) {
		Node rightChild = node.right;

		node.right = rightChild.left;
		rightChild.left = node;

		updateHeight(node);
		updateHeight(rightChild);

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

	public enum IterationStrategy {
		IN_ORDER, PRE_ORDER, POST_ORDER
	}


	private class Node {
		public Node(T data) {
			this.data = data;
		}

		T data;
		Node left;
		Node right;
		int height;

	}


	public interface TreeIterator<E> extends Iterator<E> {
		int height();
	}

	@Override
	public TreeIterator<T> iterator() {
		return iterator(IterationStrategy.IN_ORDER);
	}

	public TreeIterator<T> iterator(IterationStrategy strategy) {
		return new Itr(strategy);
	}

	private class Itr implements TreeIterator<T> {

		private ItrElement cur = null;
		private final IterationStrategy strategy;

		public Itr(IterationStrategy strategy) {
			this.strategy = strategy;
		}

		private ItrElement successor(ItrElement element) {
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

		private ItrElement nextInOrder(ItrElement element) {
			if (element == null) {
				if (root == null)
					return null;
				element = leftmost(new ItrElement(root));
			} else {
				if (element.node.right != null) {
					element = leftmost(new ItrElement(element.node.right, element.parent));
				} else {
					element = element.parent;
				}
			}
			return element;
		}

		private ItrElement nextPreOrder(ItrElement element) {
			if (element == null) {
				if (root == null)
					return null;
				element = new ItrElement(root);
			} else {
				if (element.node.left != null) {
					ItrElement parent = element.node.right == null
							? element.parent
							: new ItrElement(element.node.right, element.parent);
					element = new ItrElement(element.node.left, parent);
				} else if (element.node.right != null) {
					element = new ItrElement(element.node.right, element.parent);
				} else {
					element = element.parent;
				}
			}
			return element;
		}

		private ItrElement nextPostOrder(ItrElement element) {
			if (element == null) {
				if (root == null)
					return null;
				element = new ItrElement(root);
				element = postOrderLoop1(element);
			} else {
				if (element.parent == null)
					return null;
				while (element.parent != null && element.parent.node.right == element.node) {
					element = element.parent.parent;
					if (element == null)
						return null;
				}
				if (element.node.right != null) {
					element = new ItrElement(element.node.right, element);
					element = postOrderLoop1(element);
				} else {
					element = element.parent;
				}
			}
			return element;
		}

		private ItrElement postOrderLoop1(ItrElement element) {
			while (true) {
				if (element.node.left != null) {
					ItrElement parent = element.node.right == null
							? element
							: new ItrElement(element.node.right, element);
					element = new ItrElement(element.node.left, parent);
				} else if (element.node.right != null) {
					element = new ItrElement(element.node.right, element);
				} else {
					break;
				}
			}
			return element;
		}

		private ItrElement leftmost(ItrElement from) {
			while (from.node.left != null) {
				from = new ItrElement(from.node.left, from);
			}
			return from;
		}

		@Override
		public int height() {
			if (cur == null)
				throw new NoSuchElementException();
			return cur.node.height;
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
			return successor(cur) != null;
		}

		/**
		 * Returns the next element in the iteration.
		 *
		 * @return the next element in the iteration
		 *
		 * @throws NoSuchElementException if the iteration has no more elements
		 */
		@Override
		public T next() {
			cur = successor(cur);
			if (cur == null)
				throw new NoSuchElementException();
			return cur.node.data;
		}

		/**
		 * Removes from the underlying collection the last element returned by this iterator
		 * (optional operation).  This method can be called only once per call to {@link #next}.
		 * <p>
		 * The behavior of an iterator is unspecified if the underlying collection is modified while
		 * the iteration is in progress in any way other than by calling this method, unless an
		 * overriding class has specified a concurrent modification policy.
		 * <p>
		 * The behavior of an iterator is unspecified if this method is called after a call to the
		 * {@link #forEachRemaining forEachRemaining} method.
		 *
		 * @throws UnsupportedOperationException if the {@code remove} operation is not supported by
		 *                                       this iterator
		 * @throws IllegalStateException         if the {@code next} method has not yet been called,
		 *                                       or the {@code remove} method has already been
		 *                                       called after the last call to the {@code next}
		 *                                       method
		 * @implSpec The default implementation throws an instance of
		 * {@link UnsupportedOperationException} and performs no other action.
		 */
		@Override
		public void remove() {
			TreeIterator.super.remove();
		}

		private class ItrElement {
			private Node node;
			private ItrElement parent;

			public ItrElement(Node node) {
				this.node = node;
			}

			public ItrElement(Node node, ItrElement parent) {
				this.node = node;
				this.parent = parent;
			}
		}
	}
}
