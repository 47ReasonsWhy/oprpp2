package hr.fer.zemris.java.custom.scripting.demo;

import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.nodes.*;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Program that demonstrates the example implementation INodeVisitor.
 * The implementation writes the output of the parsed document tree to the standard output.
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public class TreeWriter {

    /**
     * Implementation of INodeVisitor that writes the output of the parsed document tree to the standard output.
     */
    private static class WriterVisitor implements INodeVisitor {
        @Override
        public void visitTextNode(TextNode node) {
            System.out.print(node.getText());
        }

        @Override
        public void visitForLoopNode(ForLoopNode node) {
            String variable = node.getVariable().asText(),
                    start = node.getStartExpression().asText(),
                    end = node.getEndExpression().asText(),
                    step = node.getStepExpression() == null ? null : node.getStepExpression().asText();
            System.out.print("{$ FOR " + variable + " " + start + " " + end + (step == null ? "" : ( " " + step)) + " $}");
            for (int i = 0; i < node.numberOfChildren(); i++) {
                node.getChild(i).accept(this);
            }
            System.out.print("{$ END $}");
        }

        @Override
        public void visitEchoNode(EchoNode node) {
            System.out.print("{$= ");
            for (Element element : node.getElements()) {
                System.out.print(element.asText() + " ");
            }
            System.out.print("$}");
        }

        @Override
        public void visitDocumentNode(DocumentNode node) {
            for (int i = 0; i < node.numberOfChildren(); i++) {
                node.getChild(i).accept(this);
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Expected 1 argument: path to file.");
            System.exit(1);
        }
        Path path = Path.of(args[0]);
        List<String> lines;
        try {
            lines = Files.readAllLines(path);
        } catch (Exception e) {
            System.out.println("Error reading file.");
            System.exit(1);
            return;
        }
        String docBody = lines.stream().reduce("", (a, b) -> a + b + "\n");
        SmartScriptParser p = new SmartScriptParser(docBody);
        WriterVisitor visitor = new WriterVisitor();
        p.getDocumentNode().accept(visitor);
    }
}
