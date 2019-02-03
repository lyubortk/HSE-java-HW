package ru.hse.lyubortk.trie;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Trie {

    private TrieNode root;
    private int size;

    public Trie() {
        root = new TrieNode();
        size = 0;
    }

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

    public boolean contains(@NotNull String element){
        var node = getLastExistingNodeOnPath(element);
        return node.depth == element.length() && node.isTerminal;
    }

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

    public int size() {
        return size;
    }

    public int howManyStartWithPrefix(@NotNull String prefix) {
        var node = getLastExistingNodeOnPath(prefix);
        if (node.depth == prefix.length()) {
            return node.terminalsInSubtree;
        } else {
            return 0;
        }
    }

    public void serialize(@NotNull OutputStream out) throws IOException {
        var dataOut = new DataOutputStream(out);
        serializeSubtree(root, dataOut);
        dataOut.flush();
        dataOut.close();
    }

    public void deserialize(@NotNull InputStream in) throws IOException {
        var dataIn = new DataInputStream(in);
        root = new TrieNode();
        deserializeSubtree(root, dataIn);
        dataIn.close();
    }

    private class TrieNode {
        private HashMap<Character, TrieNode> sons;
        private boolean isTerminal;
        private int depth;
        private int terminalsInSubtree;
        private TrieNode father;
        private char symbol;

        TrieNode() {
            this(0, null, '\0');
        }

        TrieNode(int depthVal, @Nullable TrieNode fatherRef, char symbolVal) {
            sons = new HashMap<>();
            isTerminal = false;
            depth = depthVal;
            terminalsInSubtree = 0;
            father = fatherRef;
            symbol = symbolVal;
        }

        private boolean hasNext(char c) {
            return sons.containsKey(c);
        }

        private TrieNode getNext(char c) {
            if (sons.containsKey(c)) {
                return sons.get(c);
            } else {
                var newSon = new TrieNode(depth+1, this, c);
                sons.put(c, newSon);
                return newSon;
            }
        }

    }

    private TrieNode getLastExistingNodeOnPath(@NotNull String element) {
        var curNode = root;
        int i = 0;
        while (element.length() > i && curNode.hasNext(element.charAt(i))) {
            curNode = curNode.getNext(element.charAt(i));
            i++;
        }
        return curNode;
    }

    private TrieNode getNode(@NotNull String element) {
        var curNode = root;
        for (char c : element.toCharArray()) {
            curNode = curNode.getNext(c);
        }
        return curNode;
    }

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

    private void deserializeSubtree(@NotNull TrieNode node,
                                    @NotNull DataInputStream in) throws IOException {
        node.isTerminal = in.readBoolean();
        node.depth = in.readInt();
        node.terminalsInSubtree = in.readInt();
        node.symbol = in.readChar();

        int numberOfSons = in.readInt();
        while (numberOfSons-- > 0) {
            char symbolOnEdge = in.readChar();
            TrieNode son = new TrieNode();
            deserializeSubtree(son, in);
            node.sons.put(symbolOnEdge, son);
            son.father = node;
        }
    }

}
