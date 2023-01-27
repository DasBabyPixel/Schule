package de.dasbabypixel;

import java.util.Comparator;
import java.util.Objects;

public class BinarySearchTreeRecursive<T> extends BaseBinaryTree<T> {

	private final Comparator<T> comparator;

	public BinarySearchTreeRecursive(Comparator<T> comparator) {
		this.comparator = comparator;
	}

	protected Node<T> searchNode(T key) {
		return searchNode(key, root);
	}

	private Node<T> searchNode(T key, Node<T> node) {
		if (node == null) {
			return null;
		}

		if (Objects.equals(key, node.data)) {
			return node;
		} else if (comparator.compare(key, node.data) < 0) {
			return searchNode(key, node.left);
		} else {
			return searchNode(key, node.right);
		}
	}

	public void insertNode(T key) {
		root = insertNode(key, root);
	}

	Node<T> insertNode(T key, Node<T> node) {
		// No node at current position --> store new node at current position
		if (node == null) {
			node = new Node<>(key);
		}

		// Otherwise, traverse the tree to the left or right depending on the key
		else if (comparator.compare(key, node.data) < 0) {
			node.left = insertNode(key, node.left);
		} else if (comparator.compare(key, node.data) > 0) {
			node.right = insertNode(key, node.right);
		} else {
			throw new IllegalArgumentException("BST already contains a node with key " + key);
		}

		return node;
	}

	public void deleteNode(T key) {
		root = deleteNode(key, root);
	}

	Node<T> deleteNode(T key, Node<T> node) {
		// No node at current position --> go up the recursion
		if (node == null) {
			return null;
		}

		// Traverse the tree to the left or right depending on the key
		if (comparator.compare(key, node.data) < 0) {
			node.left = deleteNode(key, node.left);
		} else if (comparator.compare(key, node.data) > 0) {
			node.right = deleteNode(key, node.right);
		}

		// At this point, "node" is the node to be deleted

		// Node has no children --> just delete it
		else if (node.left == null && node.right == null) {
			node = null;
		}

		// Node has only one child --> replace node by its single child
		else if (node.left == null) {
			node = node.right;
		} else if (node.right == null) {
			node = node.left;
		}

		// Node has two children
		else {
			deleteNodeWithTwoChildren(node);
		}

		return node;
	}

	private void deleteNodeWithTwoChildren(Node<T> node) {
		// Find minimum node of right subtree ("inorder successor" of current node)
		Node<T> inOrderSuccessor = findMinimum(node.right);

		// Copy inorder successor's data to current node
		node.data = inOrderSuccessor.data;

		// Delete inorder successor recursively
		node.right = deleteNode(inOrderSuccessor.data, node.right);
	}

	private Node<T> findMinimum(Node<T> node) {
		while (node.left != null) {
			node = node.left;
		}
		return node;
	}
}
