package hr.fer.zemris.java.custom.scripting.exec;

import hr.fer.zemris.java.custom.collections.ObjectStack;
import hr.fer.zemris.java.custom.scripting.elems.*;
import hr.fer.zemris.java.custom.scripting.nodes.*;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;
import hr.fer.zemris.java.webserver.RequestContext;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Engine that executes a document node parsed with {@link SmartScriptParser}.
 */
public class SmartScriptEngine {
    /**
     * Document node to execute.
     */
    private final DocumentNode documentNode;

    /**
     * Request context to write output to.
     */
    private final RequestContext requestContext;

    /**
     * Multistack to store variables while executing for loops.
     */
    private final ObjectMultistack multistack = new ObjectMultistack();

    /**
     * Visitor that visits all nodes in the document node and executes them.
     */
    private final INodeVisitor visitor = new INodeVisitor() {

        @Override
        public void visitDocumentNode(DocumentNode node) {
            for (int i = 0; i < node.numberOfChildren(); i++) {
                node.getChild(i).accept(this);
            }
            //System.out.println("\n");
        }

        @Override
        public void visitTextNode(TextNode node) {
            try {
                requestContext.write(node.getText());
            } catch (IOException e) {
                System.err.println("Error while visiting text node \"" + node.getText() + "\" and trying to write it.");
                System.exit(1);
            }
        }

        @Override
        public void visitForLoopNode(ForLoopNode node) {
            String variable = node.getVariable().asText();
            String start = node.getStartExpression().asText();
            String end = node.getEndExpression().asText();
            String step = node.getStepExpression().asText();

            multistack.push(variable, new ValueWrapper(Integer.parseInt(start)));
            while (multistack.peek(variable).numCompare(Integer.parseInt(end)) <= 0) {
                for (int i = 0; i < node.numberOfChildren(); i++) {
                    node.getChild(i).accept(this);
                }
                multistack.peek(variable).add(Integer.parseInt(step));
            }
            multistack.pop(variable);
        }

        @Override
        public void visitEchoNode(EchoNode node) {
            ObjectStack stack = new ObjectStack();
            // Visit all elements
            for (Element element : node.getElements()) {
                switch (element) {
                    case ElementConstantInteger elementConstantInteger ->
                            stack.push(new ValueWrapper(elementConstantInteger.getValue()));
                    case ElementConstantDouble elementConstantDouble ->
                            stack.push(new ValueWrapper(elementConstantDouble.getValue()));
                    case ElementString elementString ->
                            stack.push(new ValueWrapper(elementString.getValue()));
                    case ElementVariable elementVariable ->
                            stack.push(new ValueWrapper(multistack.peek(elementVariable.getName()).getValue()));
                    case ElementOperator elementOperator ->
                            doOperation(elementOperator, stack);
                    case ElementFunction elementFunction ->
                            callFunction(elementFunction, stack);
                    default -> {
                        System.err.println("Unknown element type: " + element.getClass());
                        System.exit(1);
                    }
                }
            }
            // Write all remaining elements out in appropriate order
            ObjectStack stackInOrder = new ObjectStack();
            while (!stack.isEmpty()) {
                stackInOrder.push(stack.pop());
            }
            while (!stackInOrder.isEmpty()) {
                try {
                    requestContext.write(((ValueWrapper) stackInOrder.pop()).getValue().toString());
                } catch (IOException e) {
                    System.err.println("Error while visiting echo node and trying to write it.");
                    System.exit(1);
                }
            }
        }
    };

    /**
     * Constructs a new {@link SmartScriptEngine} with the given document node and request context.
     *
     * @param documentNode document node to execute
     * @param requestContext request context to write output to
     */
    public SmartScriptEngine(DocumentNode documentNode, RequestContext requestContext) {
        this.documentNode = documentNode;
        this.requestContext = requestContext;
    }

    /**
     * Starts executing the engine by starting with the document node.
     */
    public void execute() {
        documentNode.accept(visitor);
    }

    /**
     * Executes the given operation on the top two elements of the stack.
     *
     * @param elementOperator element operator to execute
     * @param stack stack to execute the operation on
     */
    private void doOperation(ElementOperator elementOperator, ObjectStack stack) {
        ValueWrapper second = (ValueWrapper) stack.pop();
        ValueWrapper first = (ValueWrapper) stack.pop();
        String operator = elementOperator.getSymbol();
        switch (operator) {
            case "+" -> first.add(second.getValue());
            case "-" -> first.subtract(second.getValue());
            case "*" -> first.multiply(second.getValue());
            case "/" -> first.divide(second.getValue());
        }
        stack.push(first);
    }

    /**
     * Calls the given function with the given arguments on the stack.
     * Supported functions are:
     * <ul>
     *     <li>sin(x) - calculates the sine of the given value</li>
     *     <li>decfmt(x, format) - formats the given value with the given format</li>
     *     <li>dup(x) - duplicates the given value (not the reference)</li>
     *     <li>swap(x, y) - swaps the two given values</li>
     *     <li>setMimeType(mimeType) - sets the mime type of the request context</li>
     *     <li>paramGet(name, defValue) - gets the parameter with the given name or the default value</li>
     *     <li>pparamGet(name, defValue) - gets the persistent parameter with the given name or the default value</li>
     *     <li>pparamSet(name, value) - sets the persistent parameter with the given name to the given value</li>
     *     <li>pparamDel(name) - deletes the persistent parameter with the given name</li>
     *     <li>tparamGet(name, defValue) - gets the temporary parameter with the given name or the default value</li>
     *     <li>tparamSet(name, value) - sets the temporary parameter with the given name to the given value</li>
     *     <li>tparamDel(name) - deletes the temporary parameter with the given name</li>
     * </ul>
     *
     * @param elementFunction element function to call
     * @param stack stack to call the function on
     */
    private void callFunction(ElementFunction elementFunction, ObjectStack stack) {
        String functionName = elementFunction.getName();
        switch (functionName) {
            case "sin" -> {
                ValueWrapper value = (ValueWrapper) stack.pop();
                value.add(0.0);
                value.setValue(Math.sin(Math.toRadians((double) value.getValue())));
                stack.push(value);
            }
            case "decfmt" -> {
                DecimalFormat format = new DecimalFormat((String) ((ValueWrapper) stack.pop()).getValue());
                ValueWrapper value = (ValueWrapper) stack.pop();
                stack.push(new ValueWrapper(format.format(value.getValue())));
            }
            case "dup" -> {
                ValueWrapper value = (ValueWrapper) stack.pop();
                stack.push(value);
                stack.push(new ValueWrapper(value.getValue()));
            }
            case "swap" -> {
                ValueWrapper first = (ValueWrapper) stack.pop();
                ValueWrapper second = (ValueWrapper) stack.pop();
                stack.push(first);
                stack.push(second);
            }
            case "setMimeType" -> {
                String mimeType = ((ValueWrapper) stack.pop()).getValue().toString();
                requestContext.setMimeType(mimeType);
            }
            case "paramGet" -> {
                Object defValue = ((ValueWrapper) stack.pop()).getValue();
                String name = ((ValueWrapper) stack.pop()).getValue().toString();
                String value = requestContext.getParameter(name);
                stack.push(new ValueWrapper(value != null ? value : defValue));
            }
            case "pparamGet" -> {
                Object defValue = ((ValueWrapper) stack.pop()).getValue();
                String name = ((ValueWrapper) stack.pop()).getValue().toString();
                String value = requestContext.getPersistentParameter(name);
                stack.push(new ValueWrapper(value != null ? value : defValue));
            }
            case "pparamSet" -> {
                String name = ((ValueWrapper) stack.pop()).getValue().toString();
                String value = ((ValueWrapper) stack.pop()).getValue().toString();
                requestContext.setPersistentParameter(name, value);
            }
            case "pparamDel" -> {
                String name = ((ValueWrapper) stack.pop()).getValue().toString();
                requestContext.removePersistentParameter(name);
            }
            case "tparamGet" -> {
                Object defValue = ((ValueWrapper) stack.pop()).getValue();
                String name = ((ValueWrapper) stack.pop()).getValue().toString();
                String value = requestContext.getTemporaryParameter(name);
                stack.push(new ValueWrapper(value != null ? value : defValue));
            }
            case "tparamSet" -> {
                String name = ((ValueWrapper) stack.pop()).getValue().toString();
                String value = ((ValueWrapper) stack.pop()).getValue().toString();
                requestContext.setTemporaryParameter(name, value);
            }
            case "tparamDel" -> {
                String name = ((ValueWrapper) stack.pop()).getValue().toString();
                requestContext.removeTemporaryParameter(name);
            }
            default -> {
                System.err.println("Unknown function: " + functionName);
                System.exit(1);
            }
        }
    }
}
