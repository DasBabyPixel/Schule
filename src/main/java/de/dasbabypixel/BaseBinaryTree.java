package de.dasbabypixel;

public class BaseBinaryTree<T> {

	protected Node<T> root;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		appendNodeToStringRecursive(root, builder);
		return builder.toString();
	}

	private void appendNodeToStringRecursive(Node<T> node, StringBuilder builder) {
		appendNodeToString(node, builder);
		if (node.left != null) {
			builder.append(" L{");
			appendNodeToStringRecursive(node.left, builder);
			builder.append('}');
		}
		if (node.right != null) {
			builder.append(" R{");
			appendNodeToStringRecursive(node.right, builder);
			builder.append('}');
		}
	}

	protected void appendNodeToString(Node<T> node, StringBuilder builder) {
		builder.append(node.data);
	}

	protected static class Node<T> {
		// also called "value" in a binary tree
		// also called "key" in a binary search tree
		T data;

		Node<T> left;
		Node<T> right;

		int height;

		public Node(T data) {
			this.data = data;
		}
	}
}
