package pl.edu.ur.pz.clinicapp.utils.views;

import javafx.scene.Node;

public class ViewDefinition {
    final public Node node;
    final public ViewController controller;

    ViewDefinition(Node node, ViewController controller) {
        this.node = node;
        this.controller = controller;
    }
}
