package ru.hse.lyubortk.mytreeset;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class Tree<E> extends AbstractSet<E> implements MyTreeSet<E>  {

    private int size = 0;
    private TreeNode<E> root = null;
    private final Comparator<? super E> comparator;
    private long treeRevision = 0;

    public Tree() {
        comparator = null;
    }

    public Tree(Comparator<? super E> comparator) {
        this.comparator = comparator;
    }


    @NotNull
    @Override
    public Iterator<E> iterator() {
        return new TreeIterator(root != null ? getLeftmostNodeInSubtree(root) : null,
                                this::getNextNode);
    }

    @NotNull
    @Override
    public Iterator<E> descendingIterator() {
        return new TreeIterator(root != null ? getRightmostNodeInSubtree(root) : null,
                                this::getPrevNode);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean add(@NotNull E e) {
        if (root == null) {
            root = new TreeNode<>(e);
            size++;
            treeRevision++;
            return true;
        }

        var lessOrEqualNode = getLessOrEqualNode(root, e);
        if (lessOrEqualNode == null) {
            var leftmostNode = getLeftmostNodeInSubtree(root);
            leftmostNode.left = new TreeNode<>(e);
            leftmostNode.left.father = leftmostNode;

            size++;
            treeRevision++;
            return true;
        } else if (compare(lessOrEqualNode.data, e) < 0) {
            var newNode = new TreeNode<>(e);
            newNode.right = lessOrEqualNode.right;
            if (newNode.right != null ) {
                newNode.right.father = newNode;
            }
            lessOrEqualNode.right = newNode;
            newNode.father = lessOrEqualNode;

            size++;
            treeRevision++;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public MyTreeSet<E> descendingSet() {
        return new DescendingTree();
    }

    @NotNull
    @Override
    public E first() {
        if (root == null) {
            throw new NoSuchElementException();
        }

        var leftmostNode = getLeftmostNodeInSubtree(root);
        return leftmostNode.data;
    }

    @NotNull
    @Override
    public E last() {
        if (root == null) {
            throw new NoSuchElementException();
        }

        var rightmostNode = getRightmostNodeInSubtree(root);
        return rightmostNode.data;
    }

    @Override
    public E lower(@NotNull E e) {
        var lessOrEqualNode = getLessOrEqualNode(root, e);
        if (lessOrEqualNode == null) {
            return null;
        }

        var lessNode = getPrevNode(lessOrEqualNode);
        return lessNode != null ? lessNode.data : null;
    }

    @Override
    public E floor(@NotNull E e) {
        var lessOrEqualNode = getLessOrEqualNode(root, e);
        return lessOrEqualNode != null ? lessOrEqualNode.data : null;
    }

    @Override
    public E ceiling(@NotNull E e) {
        var greaterOrEqualNode = getGreaterOrEqualNode(root, e);
        return greaterOrEqualNode != null ? greaterOrEqualNode.data : null;
    }

    @Override
    public E higher(@NotNull E e) {
        var greaterOrEqualNode = getGreaterOrEqualNode(root, e);
        if (greaterOrEqualNode == null) {
            return null;
        }
        var greaterNode = getNextNode(greaterOrEqualNode);
        return greaterNode != null ? greaterNode.data : null;
    }

    private static class TreeNode<E> {
        private TreeNode<E> left = null;
        private TreeNode<E> right = null;
        private TreeNode<E> father = null;
        private E data;

        private TreeNode(@NotNull E data) {
            this.data = data;
        }
    }

    private class TreeIterator implements Iterator<E> {
        private TreeNode<E> nextNode;
        private Function<TreeNode<E>, TreeNode<E>> stepForward;
        private long iteratorRevision;

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

        @NotNull
        @Override
        public E next() {
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

    @Nullable
    private TreeNode<E> getNextNode(@NotNull TreeNode<E> node) {
        if (node.right != null) {
            return getLeftmostNodeInSubtree(node.right);
        } else {
            while (node.father != null && node.father.right == node) {
                node = node.father;
            }
            return node.father;
        }
    }

    @Nullable
    private TreeNode<E> getPrevNode(@NotNull TreeNode<E> node) {
        if (node.left != null) {
            return getRightmostNodeInSubtree(node.left);
        } else {
            while (node.father != null && node.father.left == node) {
                node = node.father;
            }
            return node.father;
        }
    }

    @NotNull
    private TreeNode<E> getLeftmostNodeInSubtree(@NotNull TreeNode<E> node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    @NotNull
    private TreeNode<E> getRightmostNodeInSubtree(@NotNull TreeNode<E> node) {
        while (node.right != null) {
            node = node.right;
        }
        return node;
    }

    @Nullable
    private TreeNode<E> getLessOrEqualNode(@Nullable TreeNode<E> node, @NotNull E e) {
        if (node == null) {
            return null;
        }

        if (compare(node.data, e) <= 0) {
            var answerFromRightSubtree = getLessOrEqualNode(node.right, e);
            return answerFromRightSubtree == null ? node : answerFromRightSubtree;
        } else {
            return getLessOrEqualNode(node.left, e);
        }
    }

    @Nullable
    private TreeNode<E> getGreaterOrEqualNode(@Nullable TreeNode<E> node, @NotNull E e) {
        if (node == null) {
            return null;
        }

        if (compare(node.data, e) >= 0) {
            var answerFromLeftSubtree = getGreaterOrEqualNode(node.left, e);
            return answerFromLeftSubtree == null ? node : answerFromLeftSubtree;
        } else {
            return getGreaterOrEqualNode(node.right, e);
        }
    }

    private int compare(@NotNull E first, @NotNull E second) {
        if (comparator != null) {
            return comparator.compare(first, second);
        } else {
            @SuppressWarnings("unchecked")
            var comparableFirst = (Comparable<? super E>)first;
            return comparableFirst.compareTo(second);
        }
    }

    private class DescendingTree extends AbstractSet<E> implements MyTreeSet<E> {

        @NotNull
        @Override
        public Iterator<E> iterator() {
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

        @NotNull
        @Override
        public Iterator<E> descendingIterator() {
            return Tree.this.iterator();
        }

        @NotNull
        @Override
        public MyTreeSet<E> descendingSet() {
            return Tree.this;
        }

        @NotNull
        @Override
        public E first() {
            return Tree.this.last();
        }

        @NotNull
        @Override
        public E last() {
            return Tree.this.first();
        }

        @Nullable
        @Override
        public E lower(@NotNull E e) {
            return Tree.this.higher(e);
        }

        @Nullable
        @Override
        public E floor(@NotNull E e) {
            return Tree.this.ceiling(e);
        }

        @Nullable
        @Override
        public E ceiling(@NotNull E e) {
            return Tree.this.floor(e);
        }

        @Nullable
        @Override
        public E higher(@NotNull E e) {
            return Tree.this.lower(e);
        }
    }
}
