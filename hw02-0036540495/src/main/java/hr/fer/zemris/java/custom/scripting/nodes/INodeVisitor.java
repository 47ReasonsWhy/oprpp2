package hr.fer.zemris.java.custom.scripting.nodes;

/**
 * A visitor that visits nodes of the document tree.
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public interface INodeVisitor {
    void visitTextNode(TextNode node);
    void visitForLoopNode(ForLoopNode node);
    void visitEchoNode(EchoNode node);
    void visitDocumentNode(DocumentNode node);
}
