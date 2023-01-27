package de.dasbabypixel;

import java.util.Comparator;

public class AVL<T> extends BinarySearchTreeRecursive<T> {

	public AVL(Comparator<T> comparator) {
		super(comparator);
	}

	@Override
	Node<T> insertNode(T key, Node<T> node) {
		node = super.insertNode(key, node);

		updateHeight(node);

		return balance(node);
	}

	@Override
	Node<T> deleteNode(T key, Node<T> node) {
		node = super.deleteNode(key, node);

		// Node is null if the tree doesn't contain the key
		if (node == null) {
			return null;
		}

		updateHeight(node);

		return balance(node);
	}

	private void updateHeight(Node<T> node) {
		int leftChildHeight = height(node.left);
		int rightChildHeight = height(node.right);
		node.height = Math.max(leftChildHeight, rightChildHeight) + 1;
	}

	private Node<T> balance(Node<T> node) {
		int balanceFactor = balanceFactor(node);
		if (balanceFactor < -1) {
			if (balanceFactor(node.left) > 0) {
				node.left = rotateLeft(node.left);
			}
			node = rotateRight(node);
		}
		if (balanceFactor > 1) {
			if (balanceFactor(node.right) < 0) {
				node.right = rotateRight(node.right);
			}
			node = rotateLeft(node);
		}

		return node;
	}

	private Node<T> rotateRight(Node<T> node) {
		Node<T> leftChild = node.left;

		node.left = leftChild.right;
		leftChild.right = node;

		updateHeight(node);
		updateHeight(leftChild);

		return leftChild;
	}

	private Node<T> rotateLeft(Node<T> node) {
		Node<T> rightChild = node.right;

		node.right = rightChild.left;
		rightChild.left = node;

		updateHeight(node);
		updateHeight(rightChild);

		return rightChild;
	}

	private int balanceFactor(Node<T> node) {
		return height(node.right) - height(node.left);
	}

	private int height(Node<T> node) {
		return node != null ? node.height : -1;
	}

	@Override
	protected void appendNodeToString(Node<T> node, StringBuilder builder) {
		builder.append(node.data).append("[H=").append(height(node)).append(", BF=")
				.append(balanceFactor(node)).append(']');
	}
}
