package ru.hse.lyubortk.trie;

import java.util.HashMap;

public class Trie {
    public Trie() {
        root = new TrieNode();
        size = 0;
    }

    public boolean add(String element){
        var node = getTerminalNode(element);
        if (!node.isTerminal()) {
            node.makeTerminal();
            size++;
            return true;
        } else {
            return false;
        }
    }

    public boolean contains(String element){
        var node = getLastNodeOnPath(element);
        if (node.getDepth() == element.length() && node.isTerminal()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean remove(String element) {
        var node = getLastNodeOnPath(element);
        if (node.getDepth() == element.length() && node.isTerminal()) {
            node.makeNotTerminal();
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
        var node = getLastNodeOnPath(prefix);
        return node.getNumberOfTerminalsInSubtree();
    }


    private class TrieNode {
        TrieNode() {
            this(0, null, '\0');
        }

        TrieNode(int depthVal, TrieNode fatherRef, char symbolVal) {
            sons = new HashMap<>();
            terminal = false;
            depth = depthVal;
            terminalsInSubtree = 0;
            father = fatherRef;
            symbol = symbolVal;
        }

        boolean isTerminal() {
            return terminal;
        }

        void makeNotTerminal() {
            if (!terminal) {
                return;
            }
            terminal = false;
            terminalsInSubtree--;

            var curNode = this;
            var fatherNode = father;
            while (fatherNode != null) {
                fatherNode.terminalsInSubtree--;
                if (curNode.terminalsInSubtree == 0) {
                    fatherNode.sons.remove(curNode.symbol);
                }
                curNode = fatherNode;
                fatherNode = curNode.father;
            }
        }

        void makeTerminal() {
            if (terminal) {
                return;
            }
            terminal = true;

            var curNode = this;
            while (curNode != null) {
                curNode.terminalsInSubtree++;
                curNode = curNode.father;
            }
        }

        boolean hasNext(char c) {
            return sons.containsKey(c);
        }

        TrieNode getNext(char c) {
            if (sons.containsKey(c)) {
                return (TrieNode)sons.get(c);
            } else {
                var newSon = new TrieNode(depth+1, this, c);
                sons.put(c, newSon);
                return newSon;
            }
        }

        int getDepth() {
            return depth;
        }

        int getNumberOfTerminalsInSubtree() {
            return terminalsInSubtree;
        }

        HashMap<Character, TrieNode> sons;
        boolean terminal;
        int depth;
        int terminalsInSubtree;
        TrieNode father;
        char symbol;
    }

    private TrieNode getLastNodeOnPath(String element) {
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

    private TrieNode getTerminalNode(String element) {
        if (element == null) {
            throw new IllegalArgumentException();
        }

        var curNode = root;
        for (char c : element.toCharArray()) {
            curNode = curNode.getNext(c);
        }
        return curNode;
    }


    private TrieNode root;
    private int size;
}
