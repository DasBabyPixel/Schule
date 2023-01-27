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
		return toString(ToStringStrategy.IN_ORDER);
	}

	public String toString(ToStringStrategy strategy) {
		return switch (strategy) {
			case IN_ORDER -> toStringInOrder(root, new StringBuilder(), 0).toString();
			case PRE_ORDER -> toStringPreOrder(root, new StringBuilder(), 0).toString();
			case POST_ORDER -> toStringPostOrder(root, new StringBuilder(), 0).toString();
		};
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
		builder.append(node.data).append('\n');
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
		builder.append(node.data).append('\n');
		return builder;
	}

	public enum ToStringStrategy {
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

	@Override
	public Iterator<T> iterator() {
		return new Itr();
	}

	private class Itr implements Iterator<T> {

		private ItrElement cur = null;
		private ToStringStrategy strategy;

		private void next(ToStringStrategy strategy) {
			if (cur == null) {
				if (root != null) {
					cur = new ItrElement(root);
					return;
				}
				throw new NoSuchElementException();
			}
			switch (strategy) {
				case IN_ORDER:
					if (cur.stage == 0) {
						cur.stage = 1;
						if (cur.node.left != null) {
							cur = new ItrElement(cur.node.left, cur);
							break;
						}
					}
					if (cur.stage == 1) {
						cur.stage = 2;
						break;
					}
					if (cur.stage == 2) {
						cur.stage = 3;
						if (cur.node.right != null) {
							cur = new ItrElement(cur.node.right, cur);break;
						}
					}
					break;
				case POST_ORDER:
					break;
				case PRE_ORDER:
					break;
			}
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
			if (cur == null) {
				return root != null;
			}
			return false;
		}

		/**
		 * Returns the next element in the iteration.
		 *
		 * @return the next element in the iteration
		 * @throws NoSuchElementException if the iteration has no more elements
		 */
		@Override
		public T next() {
			return null;
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
		 * this iterator
		 * @throws IllegalStateException if the {@code next} method has not yet been called, or the
		 * {@code remove} method has already been called after the last call to the {@code next}
		 * method
		 * @implSpec The default implementation throws an instance of
		 * {@link UnsupportedOperationException} and performs no other action.
		 */
		@Override
		public void remove() {
			Iterator.super.remove();
		}

		private class ItrElement {
			private Node node;
			private ItrElement parent;
			private int stage = 0;

			public ItrElement() {
			}

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
