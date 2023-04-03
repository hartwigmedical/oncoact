package com.hartwig.oncoact.graph;

import org.jgrapht.graph.DefaultEdge;

class NamedEdge extends DefaultEdge {
    private final String name;

    /**
     * Constructs a relationship edge
     *
     * @param label the label of the new edge.
     */
    public NamedEdge(String label) {
        this.name = label;
    }

    /**
     * Gets the label associated with this edge.
     *
     * @return edge label
     */
    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return "(" + getSource() + " : " + getTarget() + " : " + name + ")";
    }
}
