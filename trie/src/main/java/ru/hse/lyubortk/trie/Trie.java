package ru.hse.lyubortk.trie;

import java.util.HashMap;

public class Trie {
    
    private TrieNode root;
    private int size;

    public Trie() {
        root = new TrieNode();
        size = 0;
    }

    public boolean add(String element){
        var node = getNode(element);
        if (!node.isTerminal) {
            makeNodeTerminal(node);
            size++;
            return true;
        } else {
            return false;
        }
    }

    public boolean contains(String element){
        var node = getLastExistingNodeOnPath(element);
        if (node.depth == element.length() && node.isTerminal) {
            return true;
        } else {
            return false;
        }
    }

    public boolean remove(String element) {
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

    public int howManyStartWithPrefix(String prefix) {
        var node = getLastExistingNodeOnPath(prefix);
        if (node.depth == prefix.length()) {
            return node.terminalsInSubtree;
        } else {
            return 0;
        }
    }


    private class TrieNode {
        TrieNode() {
            this(0, null, '\0');
        }

        TrieNode(int depthVal, TrieNode fatherRef, char symbolVal) {
            sons = new HashMap<>();
            isTerminal = false;
            depth = depthVal;
            terminalsInSubtree = 0;
            father = fatherRef;
            symbol = symbolVal;
        }

        boolean hasNext(char c) {
            return sons.containsKey(c);
        }

        TrieNode getNext(char c) {
            if (sons.containsKey(c)) {
                return sons.get(c);
            } else {
                var newSon = new TrieNode(depth+1, this, c);
                sons.put(c, newSon);
                return newSon;
            }
        }

        HashMap<Character, TrieNode> sons;
        boolean isTerminal;
        int depth;
        int terminalsInSubtree;
        TrieNode father;
        char symbol;
    }

    private TrieNode getLastExistingNodeOnPath(String element) {
        if (element == null) {
            throw new IllegalArgumentException();
        }

        var curNode = root;
        int i = 0;
        while (element.length() > i && curNode.hasNext(element.charAt(i))) {
            curNode = curNode.getNext(element.charAt(i));
            i++;
        }
        return curNode;
    }

    private TrieNode getNode(String element) {
        if (element == null) {
            throw new IllegalArgumentException();
        }

        var curNode = root;
        for (char c : element.toCharArray()) {
            curNode = curNode.getNext(c);
        }
        return curNode;
    }

    private void makeNodeNotTerminal(TrieNode node) {
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

    private void makeNodeTerminal(TrieNode node) {
        if (node.isTerminal) {
            return;
        }
        node.isTerminal = true;

        while (node != null) {
            node.terminalsInSubtree++;
            node = node.father;
        }
    }
}
