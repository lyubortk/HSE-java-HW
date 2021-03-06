package ru.hse.lyubortk.trie;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A data structure to store set of strings.
 * Basic operations have time complexity of O(length).
 */
public class Trie implements Serializable {
    /** A root node which represents empty string. */
    private TrieNode root = new TrieNode();

    /** Total number of strings added to trie. */
    private int size = 0;

    /**
     * Adds string to the trie. If the string is already added to the trie,
     * then the trie remains unchanged (each string can be contained only once).
     * Time complexity: O(|element|).
     * @param element non-null {@link String} to add
     * @return true if trie did not contain the string before addition
     */
    public boolean add(@NotNull String element){
        var node = getNode(element);
        if (!node.isTerminal) {
            makeNodeTerminal(node);
            size++;
            return true;
        }
        return false;
    }

    /**
     * Checks whether the trie contains specific string.
     * Time complexity: O(|element|).
     * @param element non-null {@link String} to check
     * @return whether the trie contains argument string
     */
    public boolean contains(@NotNull String element){
        var node = getLastExistingNodeOnPath(element);
        return node.depth == element.length() && node.isTerminal;
    }

    /**
     * Deletes string from the trie. Returns false if the trie does not contain the string.
     *  Time complexity: O(|element|).
     * @param element non-null {@link String} to delete
     * @return true if the actual deletion has happened
     */
    public boolean remove(@NotNull String element) {
        var node = getLastExistingNodeOnPath(element);
        if (node.depth == element.length() && node.isTerminal) {
            makeNodeNotTerminal(node);
            size--;
            return true;
        }
        return false;
    }

    /** Returns number of strings which trie contains */
    public int size() {
        return size;
    }

    /**
     * Calculates the number of strings which are contained in the tree
     * and which start with specific prefix.Time complexity: O(|prefix|).
     * @param prefix a non-null {@link String} which represents prefix
     * @return number of contained strings which satisfy the requirement.
     */
    public int howManyStartWithPrefix(@NotNull String prefix) {
        var node = getLastExistingNodeOnPath(prefix);
        return node.depth == prefix.length() ? node.terminalsInSubtree : 0;
    }

    /** {@inheritDoc} */
    @Override
    public void serialize(@NotNull OutputStream out) throws IOException {
        var dataOut = new DataOutputStream(out);
        serializeSubtree(root, dataOut);
        dataOut.flush();
    }

    /** {@inheritDoc} */
    @Override
    public void deserialize(@NotNull InputStream in) throws IOException {
        var dataIn = new DataInputStream(in);
        root = new TrieNode();
        deserializeSubtree(root, dataIn);
        size = root.terminalsInSubtree;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Trie trie = (Trie) o;
        return size == trie.size &&
                root.equals(trie.root);
    }

    @Override
    public int hashCode() {
        return Objects.hash(root, size);
    }

    /** Inner class which represents nodes of trie. */
    private static class TrieNode {
        /** Map of char and child-node. */
        private HashMap<Character, TrieNode> children;

        /** Whether the node represents full string. */
        private boolean isTerminal;

        /** Number of edges/characters on the path to root. */
        private int depth;

        /** Number of terminal nodes in the subtree. */
        private int terminalsInSubtree;

        /** Reference to parent node. */
        private TrieNode parent;

        /** Character on the edge to parent node. */
        private char symbol;

        /** Default constructor for nodes without parent. */
        TrieNode() {
            this(0, null, '\0');
        }

        /**
         * Constructor for nodes with parent node.
         * @param depthVal depth of node
         * @param parent reference to parent node
         * @param symbol character on the edge to parent node
         */
        TrieNode(int depthVal, @Nullable TrieNode parent, char symbol) {
            children = new HashMap<>();
            isTerminal = false;
            depth = depthVal;
            terminalsInSubtree = 0;
            this.parent = parent;
            this.symbol = symbol;
        }

        /**
         * Whether the node has a child representing specific character.
         * @param c char to search for a child.
         */
        private boolean hasNext(char c) {
            return children.containsKey(c);
        }

        /**
         * Returns child node corresponding to specific character.
         * If such child was not presented in the trie, then a new node created.
         * @param c char to search for a child
         * @return corresponding child node
         */
        private TrieNode getNext(char c) {
            if (children.containsKey(c)) {
                return children.get(c);
            } else {
                var newSon = new TrieNode(depth + 1, this, c);
                children.put(c, newSon);
                return newSon;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            TrieNode trieNode = (TrieNode) o;
            return isTerminal == trieNode.isTerminal &&
                    depth == trieNode.depth &&
                    terminalsInSubtree == trieNode.terminalsInSubtree &&
                    symbol == trieNode.symbol &&
                    children.equals(trieNode.children);
        }

        @Override
        public int hashCode() {
            return Objects.hash(children, isTerminal, depth, terminalsInSubtree, symbol);
        }
    }

    /**
     * Walks on the path corresponding to string without creating non-existent nodes
     * and returns last existing node on this path.
     * @param element a non-null {@link String} which specifies path
     * @return last existing node on path
     */
    private TrieNode getLastExistingNodeOnPath(@NotNull String element) {
        var currentNode = root;
        int i = 0;
        while (element.length() > i && currentNode.hasNext(element.charAt(i))) {
            currentNode = currentNode.getNext(element.charAt(i));
            i++;
        }
        return currentNode;
    }

    /**
     * Walks on the path corresponding to string and creates non-existent nodes if needed.
     * @param element a non-null {@link String} which specifies path
     * @return node which represents argument string
     */
    private TrieNode getNode(@NotNull String element) {
        var currentNode = root;
        for (char c : element.toCharArray()) {
            currentNode = currentNode.getNext(c);
        }
        return currentNode;
    }

    /**
     * Makes node not terminal (i.e. the node no longer represents some string in the trie).
     * Updates all information on the path to the root so the trie remains valid.
     * Deletes nodes if they are no longer needed for the trie.
     * @param node a node to become not terminal
     */
    private void makeNodeNotTerminal(@NotNull TrieNode node) {
        if (!node.isTerminal) {
            return;
        }
        node.isTerminal = false;
        node.terminalsInSubtree--;

        var parentNode= node.parent;
        while (parentNode != null) {
            parentNode.terminalsInSubtree--;
            if (node.terminalsInSubtree == 0) {
                parentNode.children.remove(node.symbol);
            }
            node = parentNode;
            parentNode = node.parent;
        }
    }

    /**
     * Makes node terminal (i.e. the node from now on represents some string in the trie).
     * Updates all information on the path to the root so the trie remains valid.
     * @param node a node to become terminal
     */
    private void makeNodeTerminal(@NotNull TrieNode node) {
        if (node.isTerminal) {
            return;
        }
        node.isTerminal = true;

        while (node != null) {
            node.terminalsInSubtree++;
            node = node.parent;
        }
    }

    /** Write specific subtree as byte-sequence to the {@link DataOutputStream}. */
    private void serializeSubtree(@NotNull TrieNode node,
                                  @NotNull DataOutputStream out) throws IOException {
        out.writeBoolean(node.isTerminal);
        out.writeInt(node.depth);
        out.writeInt(node.terminalsInSubtree);
        out.writeChar(node.symbol);
        out.writeInt(node.children.size());

        for (Map.Entry<Character, TrieNode> o : node.children.entrySet()) {
            out.writeChar(o.getKey());
            serializeSubtree(o.getValue(), out);
        }
    }

    /** Read specific subtree as byte-sequence from {@link DataInputStream} */
    private void deserializeSubtree(@NotNull TrieNode node,
                                    @NotNull DataInputStream in) throws IOException {
        node.isTerminal = in.readBoolean();
        node.depth = in.readInt();
        node.terminalsInSubtree = in.readInt();
        node.symbol = in.readChar();

        int numberOfSons = in.readInt();
        for (; numberOfSons > 0; numberOfSons--) {
            char symbolOnEdge = in.readChar();
            TrieNode son = new TrieNode();
            deserializeSubtree(son, in);
            node.children.put(symbolOnEdge, son);
            son.parent = node;
        }
    }

}
