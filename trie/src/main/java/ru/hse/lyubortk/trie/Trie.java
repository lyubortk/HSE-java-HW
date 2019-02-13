package ru.hse.lyubortk.trie;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/** A data structure to store set of strings.
 *  Basic operations have time complexity of O(length).*/
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
        } else {
            return false;
        }
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
        } else {
            return false;
        }
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
    public void serialize(@NotNull OutputStream out) throws IOException {
        var dataOut = new DataOutputStream(out);
        serializeSubtree(root, dataOut);
        dataOut.flush();

    }

    /** {@inheritDoc} */
    public void deserialize(@NotNull InputStream in) throws IOException {
        var dataIn = new DataInputStream(in);
        root = new TrieNode();
        deserializeSubtree(root, dataIn);
        size = root.terminalsInSubtree;

    }

    /** Checks whether another trie contains the same set of string */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Trie)) {
            return false;
        }
        var anotherTrie = (Trie) o;
        return (size == anotherTrie.size) && equalsSubtrees(root, anotherTrie.root);
    }

    /** returns size of the trie */
    @Override
    public int hashCode() {
        return size;
    }

    /** Inner class which represents nodes of trie. */
    private static class TrieNode {
        /** Map of char and child-node. */
        private HashMap<Character, TrieNode> sons;

        /** Whether the node represents full string. */
        private boolean isTerminal;

        /** Number of edges/characters on the path to root. */
        private int depth;

        /** Number of terminal nodes in the subtree. */
        private int terminalsInSubtree;

        /** Reference to parent node. */
        private TrieNode father;

        /** Character on the edge to parent node. */
        private char symbol;

        /** Default constructor for nodes without parent. */
        TrieNode() {
            this(0, null, '\0');
        }

        /**
         * Constructor for nodes with parent node.
         * @param depthVal depth of node
         * @param father reference to parent node
         * @param symbol character on the edge to parent node
         */
        TrieNode(int depthVal, @Nullable TrieNode father, char symbol) {
            sons = new HashMap<>();
            isTerminal = false;
            depth = depthVal;
            terminalsInSubtree = 0;
            this.father = father;
            this.symbol = symbol;
        }

        /**
         * Whether the node has a child representing specific character.
         * @param c char to search for a child.
         */
        private boolean hasNext(char c) {
            return sons.containsKey(c);
        }

        /**
         * Returns child node corresponding to specific character.
         * If such child was not presented in the trie, then a new node created.
         * @param c char to search for a child
         * @return corresponding child node
         */
        private TrieNode getNext(char c) {
            if (sons.containsKey(c)) {
                return sons.get(c);
            } else {
                var newSon = new TrieNode(depth + 1, this, c);
                sons.put(c, newSon);
                return newSon;
            }
        }
    }

    /**
     * Walks on the path corresponding to string without creating non-existent nodes
     * and returns last existing node on this path.
     * @param element a non-null {@link String} which specifies path
     * @return last existing node on path
     */
    private TrieNode getLastExistingNodeOnPath(@NotNull String element) {
        var curNode = root;
        int i = 0;
        while (element.length() > i && curNode.hasNext(element.charAt(i))) {
            curNode = curNode.getNext(element.charAt(i));
            i++;
        }
        return curNode;
    }

    /**
     * Walks on the path corresponding to string and creates non-existent nodes if needed.
     * @param element a non-null {@link String} which specifies path
     * @return node which represents argument string
     */
    private TrieNode getNode(@NotNull String element) {
        var curNode = root;
        for (char c : element.toCharArray()) {
            curNode = curNode.getNext(c);
        }
        return curNode;
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

        var fatherNode= node.father;
        while (fatherNode != null) {
            fatherNode.terminalsInSubtree--;
            if (node.terminalsInSubtree == 0) {
                fatherNode.sons.remove(node.symbol);
            }
            node = fatherNode;
            fatherNode = node.father;
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
            node = node.father;
        }
    }

    /** Write specific subtree as byte-sequence to the {@link DataOutputStream}. */
    private void serializeSubtree(@NotNull TrieNode node,
                                  @NotNull DataOutputStream out) throws IOException {
        out.writeBoolean(node.isTerminal);
        out.writeInt(node.depth);
        out.writeInt(node.terminalsInSubtree);
        out.writeChar(node.symbol);
        out.writeInt(node.sons.size());

        for (Map.Entry<Character, TrieNode> o : node.sons.entrySet()) {
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
            node.sons.put(symbolOnEdge, son);
            son.father = node;
        }
    }

    private boolean equalsSubtrees(@Nullable TrieNode first, @Nullable TrieNode second) {
        if (first == null && second == null) {
            return true;
        } else if (first != null && second != null) {
            boolean answer = true;
            answer &= first.isTerminal == second.isTerminal;
            answer &= first.depth == second.depth;
            answer &= first.symbol == second.symbol;
            answer &= first.terminalsInSubtree == second.terminalsInSubtree;
            answer &= first.sons.size() == second.sons.size();

            for (Map.Entry<Character, TrieNode> o : first.sons.entrySet()) {
                var key = o.getKey();
                if (!second.sons.containsKey(key)) {
                    answer = false;
                    break;
                }
                answer &= equalsSubtrees(first.sons.get(key), second.sons.get(key));
            }
            return answer;
        } else {
            return false;
        }
    }
}
