package ru.hse.lyubortk.mytreeset;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/** Unbalanced binary search tree which implements {@link MyTreeSet} interface */
public class Tree<E> extends AbstractSet<E> implements MyTreeSet<E>  {
    private int size = 0;
    private TreeNode<E> root = null;
    private final Comparator<? super E> comparator;
    private long treeRevision = 0;

    /**
     * Default constructor without comparator.
     * Elements in the tree will be sorted according to their compareTo method.
     */
    public Tree() {
        comparator = null;
    }

    /**
     * Constructor with comparator
     * @param comparator used to maintain order of elements in the tree
     */
    public Tree(Comparator<? super E> comparator) {
        this.comparator = comparator;
    }

    /** {@inheritDoc} */
    @Override
    public @NotNull Iterator<E> iterator() {
        return new TreeIterator(root != null ? getLeftmostNodeInSubtree(root) : null,
                                this::getNextNode);
    }

    /** {@inheritDoc} */
    @Override
    public @NotNull Iterator<E> descendingIterator() {
        return new TreeIterator(root != null ? getRightmostNodeInSubtree(root) : null,
                                this::getPreviousNode);
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        return size;
    }

    /** {@link TreeSet#add} */
    @Override
    public boolean add(@NotNull E e) {
        var lessOrEqualNode = getLessOrEqualNode(root, e);
        if (lessOrEqualNode != null && compare(lessOrEqualNode.data, e) == 0) {
            return false;
        }

        if (root == null) {
            root = new TreeNode<>(e);
        } else if (lessOrEqualNode == null) {
            var leftmostNode = getLeftmostNodeInSubtree(root);
            leftmostNode.left = new TreeNode<>(e);
            leftmostNode.left.parent = leftmostNode;
        } else if (compare(lessOrEqualNode.data, e) < 0) {
            var newNode = new TreeNode<>(e);
            newNode.right = lessOrEqualNode.right;
            if (newNode.right != null ) {
                newNode.right.parent = newNode;
            }
            lessOrEqualNode.right = newNode;
            newNode.parent = lessOrEqualNode;
        }
        size++;
        treeRevision++;
        return true;
    }

    /** {@link TreeSet#remove} */
    @Override
    public boolean remove(@NotNull Object o) {
        var node = getLessOrEqualNode(root, o);
        if (node == null || !o.equals(node.data)) {
            return false;
        }

        remove(node);

        size--;
        treeRevision++;
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public MyTreeSet<E> descendingSet() {
        return new DescendingTree();
    }

    /** {@inheritDoc} */
    @Override
    public @NotNull E first() {
        if (root == null) {
            throw new NoSuchElementException();
        }

        var leftmostNode = getLeftmostNodeInSubtree(root);
        return leftmostNode.data;
    }

    /** {@inheritDoc} */
    @Override
    public @NotNull E last() {
        if (root == null) {
            throw new NoSuchElementException();
        }

        var rightmostNode = getRightmostNodeInSubtree(root);
        return rightmostNode.data;
    }

    /** {@inheritDoc} */
    @Override
    public E lower(@NotNull E e) {
        var lessOrEqualNode = getLessOrEqualNode(root, e);
        if (lessOrEqualNode == null) {
            return null;
        }

        var lessNode = lessOrEqualNode;
        if (compare(lessOrEqualNode.data, e) == 0) {
            lessNode = getPreviousNode(lessOrEqualNode);
        }
        return lessNode != null ? lessNode.data : null;
    }

    /** {@inheritDoc} */
    @Override
    public E floor(@NotNull E e) {
        var lessOrEqualNode = getLessOrEqualNode(root, e);
        return lessOrEqualNode != null ? lessOrEqualNode.data : null;
    }

    /** {@inheritDoc} */
    @Override
    public E ceiling(@NotNull E e) {
        var greaterOrEqualNode = getGreaterOrEqualNode(root, e);
        return greaterOrEqualNode != null ? greaterOrEqualNode.data : null;
    }

    /** {@inheritDoc} */
    @Override
    public E higher(@NotNull E e) {
        var greaterOrEqualNode = getGreaterOrEqualNode(root, e);
        if (greaterOrEqualNode == null) {
            return null;
        }

        var greaterNode = greaterOrEqualNode;
        if (compare(greaterOrEqualNode.data, e) == 0) {
            greaterNode = getNextNode(greaterOrEqualNode);
        }
        return greaterNode != null ? greaterNode.data : null;
    }

    private static class TreeNode<E> {
        private TreeNode<E> left = null;
        private TreeNode<E> right = null;
        private TreeNode<E> parent = null;
        private E data;

        private TreeNode(@NotNull E data) {
            this.data = data;
        }
    }

    private class TreeIterator implements Iterator<E> {
        private TreeNode<E> nextNode;
        private final Function<TreeNode<E>, TreeNode<E>> stepForward;
        private final long iteratorRevision;

        TreeIterator(@Nullable TreeNode<E> nextNode,
                     @NotNull Function<TreeNode<E>, TreeNode<E>> stepForward) {
            this.nextNode = nextNode;
            this.stepForward = stepForward;
            iteratorRevision = treeRevision;
        }

        @Override
        public boolean hasNext() {
            if (iteratorRevision != treeRevision) {
                throw new IllegalStateException();
            }
            return nextNode != null;
        }

        @Override
        public @NotNull E next() {
            if (iteratorRevision != treeRevision) {
                throw new IllegalStateException();
            }
            if (nextNode == null) {
                throw new NoSuchElementException();
            }

            E data = nextNode.data;
            nextNode = stepForward.apply(nextNode);
            return data;
        }
    }

    private void remove(@NotNull TreeNode<E> node) {
        if (node.left == null && node.right == null) {
            changeChild(node.parent, node, null);
            if (root == node) {
                root = null;
            }
        } else if (node.left == null) {
            changeChild(node.parent, node, node.right);
            node.right.parent = node.parent;
            if (root == node) {
                root = node.right;
            }
        } else if (node.right == null) {
            changeChild(node.parent, node, node.left);
            node.left.parent = node.parent;
            if (root == node) {
                root = node.left;
            }
        } else {
            var nextNode = getNextNode(node);
            assert nextNode != null;
            node.data = nextNode.data;
            remove(nextNode);
        }
    }

    private void changeChild(@Nullable TreeNode<E> parent, @NotNull TreeNode<E> child,
                             @Nullable TreeNode<E> replacement) {
        if (parent == null) return;
        if (parent.left == child) {
            parent.left = replacement;
        }
        if (parent.right == child) {
            parent.right = replacement;
        }
    }

    private @Nullable TreeNode<E> getNextNode(@NotNull TreeNode<E> node) {
        if (node.right != null) {
            return getLeftmostNodeInSubtree(node.right);
        } else {
            while (node.parent != null && node.parent.right == node) {
                node = node.parent;
            }
            return node.parent;
        }
    }

    private @Nullable TreeNode<E> getPreviousNode(@NotNull TreeNode<E> node) {
        if (node.left != null) {
            return getRightmostNodeInSubtree(node.left);
        } else {
            while (node.parent != null && node.parent.left == node) {
                node = node.parent;
            }
            return node.parent;
        }
    }

    private @NotNull TreeNode<E> getLeftmostNodeInSubtree(@NotNull TreeNode<E> node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    private @NotNull TreeNode<E> getRightmostNodeInSubtree(@NotNull TreeNode<E> node) {
        while (node.right != null) {
            node = node.right;
        }
        return node;
    }

    private @Nullable TreeNode<E> getLessOrEqualNode(@Nullable TreeNode<E> node, @NotNull Object o) {
        if (node == null) {
            return null;
        }

        if (compare(node.data, o) <= 0) {
            var answerFromRightSubtree = getLessOrEqualNode(node.right, o);
            return answerFromRightSubtree == null ? node : answerFromRightSubtree;
        } else {
            return getLessOrEqualNode(node.left, o);
        }
    }

    private @Nullable TreeNode<E> getGreaterOrEqualNode(@Nullable TreeNode<E> node, @NotNull Object o) {
        if (node == null) {
            return null;
        }

        if (compare(node.data, o) >= 0) {
            var answerFromLeftSubtree = getGreaterOrEqualNode(node.left, o);
            return answerFromLeftSubtree == null ? node : answerFromLeftSubtree;
        } else {
            return getGreaterOrEqualNode(node.right, o);
        }
    }

    private int compare(@NotNull E first, @NotNull Object second) {
        if (comparator != null) {
            @SuppressWarnings("unchecked")
            E secondCasted = (E)second;
            return comparator.compare(first, secondCasted);
        } else {
            @SuppressWarnings("unchecked")
            var comparableSecond = (Comparable<? super E>)second;
            return (-1) * comparableSecond.compareTo(first);
        }
    }

    private class DescendingTree extends AbstractSet<E> implements MyTreeSet<E> {
        @Override
        public @NotNull Iterator<E> iterator() {
            return Tree.this.descendingIterator();
        }

        @Override
        public int size() {
            return Tree.this.size();
        }

        @Override
        public boolean add(@NotNull E e) {
            return Tree.this.add(e);
        }

        @Override
        public boolean remove(@NotNull Object o) {
            return Tree.this.remove(o);
        }

        @Override
        public @NotNull Iterator<E> descendingIterator() {
            return Tree.this.iterator();
        }

        @Override
        public @NotNull MyTreeSet<E> descendingSet() {
            return Tree.this;
        }

        @Override
        public @NotNull E first() {
            return Tree.this.last();
        }

        @Override
        public @NotNull E last() {
            return Tree.this.first();
        }

        @Override
        public @Nullable E lower(@NotNull E e) {
            return Tree.this.higher(e);
        }

        @Override
        public @Nullable E floor(@NotNull E e) {
            return Tree.this.ceiling(e);
        }

        @Override
        public @Nullable E ceiling(@NotNull E e) {
            return Tree.this.floor(e);
        }

        @Override
        public @Nullable E higher(@NotNull E e) {
            return Tree.this.lower(e);
        }
    }
}
