package fr.an.ruby2java.ruby2java;

import java.io.PrintStream;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

import org.jcodings.Encoding;
import org.jruby.Ruby;
import org.jruby.ast.*;
import org.jruby.ast.visitor.NodeVisitor;
import org.jruby.internal.runtime.methods.ProcMethod;
import org.jruby.runtime.Helpers;
import org.jruby.util.ByteList;
import org.jruby.util.KeyValuePair;
import org.jruby.util.RegexpOptions;

import fr.an.ruby2java.ruby2java.CountMap.Count;

public class Ruby2JavaVisitor implements NodeVisitor<Object> {

	private PrintStream out;
	private String inputFilePath;
	private R2JContext currR2JContext;
	private R2JContextBuilder builderContext;
	private Ruby ruby;

	
	private Node currentClassDecl;
	private boolean currentClassDeclIsStatic;
	
	// ------------------------------------------------------------------------
	
	public Ruby2JavaVisitor(PrintStream out, String inputFilePath, R2JContext currR2JContext, R2JContextBuilder builderContext) {
		this.out = out;
		this.inputFilePath = inputFilePath;
		this.currR2JContext = currR2JContext;
		this.builderContext = builderContext;
		this.ruby = builderContext.getRuby();
	}
	
	// ------------------------------------------------------------------------

	private void unsupported(Node node) {
		Count c = builderContext.getUnsupportedCountMap().getCount(node.getClass().getName());
		c.value++;
		print("/* ERROR unsupported " + node.getClass() + " */");
	}
	

	private void print(String text) {
		out.print(text);
	}
	private void println(String line) {
		out.print(line);
		println();
	}
	private void println() {
		out.print("\n");
	}
	
	private void visitNodes(List<Node> nodes) {
		if (nodes != null && !nodes.isEmpty()) {
			for(Node node : nodes) {
				visitNode(node);
			}
		}
	}

	private void visitNodes(Node[] nodes, String sep) {
		final int len = nodes.length;
		for(int i = 0; i < len; i++) {
			visitNode(nodes[i]);
			if (sep != null && i + 1 < len) {
				print(sep);
			}
		}
	}

	private void visitNodes(List<Node> nodes, String sep) {
		int len = nodes.size();
		for(int i = 0; i < len; i++) {
			Node node = nodes.get(i);
			visitNode(node);
			if (sep != null && i + 1 < len) {
				print(sep);
			}
		}
	}
	
	private Object visitNode(Node node) {
		Object res = null;
		if (node != null) {
			res = node.accept(this);
		}
		return res;
	}
	
	private String ruby2javaIdent(String name) {
		if (name == null) return null;
		String res = name.replace("?",  "_question");
		if (res.equals("private") || res.equals("public") || res.equals("protected")
				) {
			res = "$" + res;
		}
		return res;
	}
	
	// ------------------------------------------------------------------------

	@Override
	public Object visitRootNode(RootNode node) {
		String packageName = inputFilePath;
		if (packageName.startsWith("/")) {
			packageName = packageName.substring(1);
		}
		// remove file name... (keep base dir name)
		int lastSlash = packageName.lastIndexOf("/");
		if (lastSlash != -1) {
			packageName = packageName.substring(0, lastSlash);
	//		if (packageName.endsWith(".rb")) {
	//			packageName = packageName.substring(0, packageName.length() - 3);
	//		}
			
			packageName = packageName.replaceAll("/", ".");
			if (packageName.startsWith(".")) {
				packageName = packageName.substring(1);
			}
			
			println("package " + packageName + ";");
		}
		visitNode(node.getBodyNode());
		return null;
	}

	@Override
	public Object visitModuleNode(ModuleNode node) {
	    Colon3Node cpath = node.getCPath();
	    String name = cpath.getName();
	    assert name != null;
	    println("public class " + name + "{");

	    Node bodyNode = node.getBodyNode();
	    visitNode(bodyNode);
	    
	    println("}");
	    return null;
	}


	@Override
	public Object visitClassNode(ClassNode node) {
	    Colon3Node cpath = node.getCPath();
	    String name = cpath.getName();
	    // StaticScope scope;
	    Node superNode = node.getSuperNode();

	    assert name != null;
	    print("public class " + name);
	    if (superNode != null) {
	    	println(" extends ");
	    	visitNode(superNode);
	    }
	    print(" ");

	    Node bodyNode = node.getBodyNode();
	    if (bodyNode != null && !(bodyNode instanceof NilNode)) {
	    	// print("{");
	    	visitNode(bodyNode);
	    	// println("}");
	    } else {
	    	println("{ }");
	    }
		
	    return null;
	}

	@Override
	public Object visitBlockNode(BlockNode node) {
		println("{");
		Node[] ls = node.children();
		if (ls != null && ls.length != 0) {
			for(Node child : ls) {
				visitNode(child);
			}
		}
		println();
		println("}");
	    return null;
	}

	@Override
	public Object visitNewlineNode(NewlineNode node) {
		println();
		Node nextNode = node.getNextNode();
		if (nextNode != null) {
			visitNode(nextNode);
		}
		return null;
	}
	
	@Override
	public Object visitDefnNode(DefnNode node) {
		String name = node.getName();
		ArgsNode argsNode = node.getArgsNode();
		// StaticScope scope;
		Node bodyNode = node.getBodyNode();
		boolean isStatic = false; // TODO...  "class << self"
		
		doVisitDefineMethod(isStatic, name, argsNode, bodyNode);
		return null;
	}

	private void doVisitDefineMethod(boolean isStatic, String name, ArgsNode argsNode, Node bodyNode) {
		String javaName = ruby2javaIdent(name);
		print("public Object " + javaName + "(");
		argsNode.accept(this);
		print(")");
		if (bodyNode != null) {
			if (bodyNode instanceof NewlineNode) {
				bodyNode = (((NewlineNode) bodyNode).getNextNode());
			}
			boolean isBlock = isBlock(bodyNode);
			if (! isBlock) {
				println("{");
			}
			visitNode(bodyNode);
			if (! isBlock) {
				println("}");
			}
		} else {
			print(";");
		}
		println();
	}

	private boolean isBlock(Node node) {
		if (node == null) return false;
		else if (node instanceof BlockNode) return true;
		else if (node instanceof NewlineNode) return isBlock(((NewlineNode) node).getNextNode());
		else return false;
	}
	
	@Override
	public Object visitDefsNode(DefsNode node) {
		// ruby singleton <-> java static  (for self...) 
		Node receiverNode = node.getReceiverNode();
		String name = node.getName();
		ArgsNode argsNode = node.getArgsNode();
		// StaticScope scope;
		Node bodyNode = node.getBodyNode();
		boolean isSelf = false;
		if (receiverNode instanceof SelfNode) {
			isSelf = true;
		} else if (receiverNode == null
				// && ....
				) {
			
		}
		
		doVisitDefineMethod(true, name, argsNode, bodyNode);
		return null;
	}


	
	@Override
	public Object visitFCallNode(FCallNode node) {
	    String name = ruby2javaIdent(node.getName());
	    Node argsNode = node.getArgsNode();
	    Node iterNode = node.getIterNode();

	    print(name);
	    print("(");
	    visitNode(argsNode);
	    visitNode(iterNode);
	    print(")");
	    
		return null;
	}
	
	@Override
	public Object visitVCallNode(VCallNode node) {
		String name = ruby2javaIdent(node.getName());
		print(name);
	    print("()");
	    return null;
	}

	@Override
	public Object visitCallNode(CallNode node) {
	    Node receiverNode = node.getReceiverNode();
		String name = ruby2javaIdent(node.getName());
	    Node argsNode = node.getArgsNode();
	    Node iterNode = node.getIterNode();

	    if (receiverNode != null) {
	    	visitNode(receiverNode);
	    	print(".");
	    }
	    print(name);
	    print("(");
	    visitNode(argsNode);
	    visitNode(iterNode);
	    print(")");
	    
		return null;
	}


	
	@Override
	public Object visitAliasNode(AliasNode node) {
		Node oldName = node.getOldName();
		Node newName = node.getNewName();
		print("alias(");
		visitNode(newName);
		print(", ");
		visitNode(oldName);
		print(")");
		return null;
	}

	@Override
	public Object visitVAliasNode(VAliasNode node) {
		String oldName = ruby2javaIdent(node.getOldName());
		String newName = ruby2javaIdent(node.getNewName());
		print("alias(");
		print("\"" + newName + "\"");
		print(", ");
		print("\"" + oldName + "\"");
		print(")");
		return null;
	}

	
	@Override
	public Object visitAndNode(AndNode node) {
		// TOADD... parenthesis precedence
		visitNode(node.getFirstNode());
		print(" && ");
		visitNode(node.getSecondNode());
		return null;
	}

	@Override
	public Object visitArgsNode(ArgsNode node) {
		// represents ars: foo(p1, ..., pn, o1 = v1, ..., on = v2, *r, q1, ..., qn, k1:, ..., kn:, **K, &b)
		List<Node> childNodes = node.childNodes();
		visitNodes(childNodes, ", ");
		return null;
	}

	@Override
	public Object visitArgsCatNode(ArgsCatNode node) {
		Node firstNode = node.getFirstNode();
	    Node secondNode = node.getSecondNode();
	    visitNode(firstNode);
	    print(".");
	    visitNode(secondNode);
	    return null;
	}

	@Override
	public Object visitArgsPushNode(ArgsPushNode node) {
		// TODO Auto-generated method stub
		unsupported(node); return null;
	}

	@Override
	public Object visitArgumentNode(ArgumentNode node) {
		String name = ruby2javaIdent(node.getName());
		print(name);
		return null;
	}

	@Override
	public Object visitArrayNode(ArrayNode node) {
		print("[ ");
		Node[] childList = node.children();
		visitNodes(childList, ", ");
		print(" ]");
		return null;
	}

	@Override
	public Object visitZArrayNode(ZArrayNode node) {
		print("[]");
		return null;
	}

	@Override
	public Object visitAttrAssignNode(AttrAssignNode node) {
		Node receiverNode = node.getReceiverNode();
	    String name = ruby2javaIdent(node.getName());
	    Node argsNode = node.getArgsNode();
	    
	    visitNode(receiverNode);
	    print(".");
	    print(name);
	    print(" = ");
	    visitNode(argsNode);
	    println(";");
	    return null;
	}

	@Override
	public Object visitBackRefNode(BackRefNode node) {
		char type = node.getType();
		print("$backref_" + type + "()");
		return null;
	}

	@Override
	public Object visitBeginNode(BeginNode node) {
		Node bodyNode = node.getBodyNode();
		boolean printCurly = true;
		if (bodyNode instanceof BlockNode) {
			printCurly = false;
		}
		if (printCurly) {
			println("{");
		}
		visitNode(bodyNode);
		if (printCurly) {
			println("}");
		}
		return null;
	}

	@Override
	public Object visitBignumNode(BignumNode node) {
		BigInteger value = node.getValue();
		print("new BigInteger(\"");
		print(value.toString());
		print("\")");
		return null;
	}

	@Override
	public Object visitBlockArgNode(BlockArgNode node) {
		String name = ruby2javaIdent(node.getName());
		print(name);
		return null;
	}


	@Override
	public Object visitBlockPassNode(BlockPassNode node) {
		Node bodyNode = node.getBodyNode();
		boolean isCallable = (bodyNode instanceof IterNode)
				// || (bodyNode instanceof ProcMethod)
				;
		if (! isCallable) {
			print("() -> ");
		}
		visitNode(bodyNode);
		return null;
	}

	@Override
	public Object visitBreakNode(BreakNode node) {
		print("break");
		Node value = node.getValueNode();
		if (value != null) {
			print(" ");
			visitNode(value);
			println(";");
		} else {
			println(";");
		}
		return null;
	}

	@Override
	public Object visitConstDeclNode(ConstDeclNode node) {
		String name = node.getName();
		Node constNode = node.getConstNode();
		Node valueNode = node.getValueNode();
		print("public static final ");
		print(name);
		print("_");
		visitNode(constNode);
		print(" = ");
		visitNode(valueNode);
		println(";");
		return null;
	}

	@Override
	public Object visitClassVarAsgnNode(ClassVarAsgnNode node) {
		String name = rubyClassVar2javaIdent(node.getName());
		print(name);
		print(" = ");
		visitNode(node.getValueNode());
		println(";");
		return null;
	}

	@Override
	public Object visitClassVarDeclNode(ClassVarDeclNode node) {
		String name = rubyClassVar2javaIdent(node.getName());
		print("public static Object ");
		print(name);
		print(" = ");
		visitNode(node.getValueNode());
		println(";");
		return null;
	}

	private String rubyClassVar2javaIdent(String name) {
		String res = name;
		if (res.startsWith("@@")) {
			res = "$s_" + res.substring(2);
		}
		res = ruby2javaIdent(res);
		return res;
	}
	
	@Override
	public Object visitClassVarNode(ClassVarNode node) {
		String name = rubyClassVar2javaIdent(node.getName());
		print(name);
		return null;
	}

	@Override
	public Object visitCaseNode(CaseNode node) {
	    Node caseNode = node.getCaseNode();
	    Node[] caseNodes = node.getCases().children();
	    Node elseNode = node.getElseNode();
	    
	    print("Object switchVal = ");
	    visitNode(caseNode);
	    println(";");
	    
		final int len = caseNodes.length;
		for(int i = 0; i < len; i++) {
			Node caseEltNode = caseNodes[i];
			if (caseEltNode instanceof WhenNode) {
				WhenNode caseElt = (WhenNode) caseEltNode;
				Node caseExpr = caseElt.getExpressionNodes();
				Node caseBody = caseElt.getBodyNode();
				// redundant with list? caseElt.getNextCase()
				print("if (switchVal.equals(");
				visitNode(caseExpr);
				print(")) {");
				visitNode(caseBody);

				if (i + 1 < len) {
					println("} else ");
				}
			} else {
				print("/*SHOULD NOT OCCUR: unexpected case*/");
				visitNode(caseEltNode);
			}
		}
		if (elseNode != null) {
			println("} else {");
			visitNode(elseNode);
		}
		println("}");

		return null;
	}

	@Override
	public Object visitColon2Node(Colon2Node node) {
		Node lhs = node.getLeftNode();
		String name = ruby2javaIdent(node.getName());
		
		visitNode(lhs);
		print(".");
		print(name);
		return null;
	}

	@Override
	public Object visitColon3Node(Colon3Node node) {
		// TODO Auto-generated method stub
		unsupported(node); return null;
	}

	@Override
	public Object visitComplexNode(ComplexNode node) {
		// TODO Auto-generated method stub
		unsupported(node); return null;
	}

	@Override
	public Object visitConstNode(ConstNode node) {
		String name = ruby2javaIdent(node.getName());
		print(name);
		return null;
	}

	@Override
	public Object visitDAsgnNode(DAsgnNode node) {
		String name = ruby2javaIdent(node.getName());
		print(name);
		print(" = ");
		visitNode(node.getValueNode());
		println(";");
		return null;
	}

	@Override
	public Object visitDRegxNode(DRegexpNode node) {
		// Encoding encoding = node.getEncoding();
		RegexpOptions options = node.getOptions();
		Node[] children = node.children();
		visitNodes(children, ",");
		
		return null;
	}

	@Override
	public Object visitDStrNode(DStrNode node) {
		// TOADD.. node.getEncoding();
		Node[] children = node.children();
		visitNodes(children, " + ");
		return null;
	}

	@Override
	public Object visitDSymbolNode(DSymbolNode node) {
		// TOADD.. node.getEncoding();
		Node[] children = node.children();
		visitNodes(children, " + ");
		return null;
	}

	@Override
	public Object visitDVarNode(DVarNode node) {
		String name = ruby2javaIdent(node.getName());
		print(name);
		return null;
	}

	@Override
	public Object visitDXStrNode(DXStrNode node) {
		print("$exec(");
		Node[] children = node.children();
		visitNodes(children, " + ");
		print(")");
		return null;
	}

	@Override
	public Object visitDefinedNode(DefinedNode node) {
		// TODO Auto-generated method stub
		unsupported(node); return null;
	}

	@Override
	public Object visitEncodingNode(EncodingNode node) {
		print("/*encoding(*/ \"");
		Encoding encoding = node.getEncoding();
		print(new String(encoding.getName()));
		print("\" /*)encoding*/");
		return null;
	}

	@Override
	public Object visitEnsureNode(EnsureNode node) {
		Node bodyNode = node.getBodyNode();
	    Node ensureNode = node.getEnsureNode();
	    println("try {");
	    visitNode(bodyNode);
	    println("} catch(Exception ex) {");
	    visitNode(ensureNode);
	    println("}");
	    return null;
	}

	@Override
	public Object visitEvStrNode(EvStrNode node) {
		// an #{} expression in a string
		Node body = node.getBody();
		visitNode(body);
		return null;
	}

	@Override
	public Object visitFalseNode(FalseNode node) {
		print("false");
		return null;
	}

	@Override
	public Object visitFixnumNode(FixnumNode node) {
		long value = node.getValue();
		print(Long.toString(value));
		if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
			print("l");
		}
		return null;
	}

	@Override
	public Object visitFlipNode(FlipNode node) {
		print("new Range(");
	    visitNode(node.getBeginNode());
	    print(", ");
	    visitNode(node.getEndNode());
	    boolean exclusive = node.isExclusive();
	    if (! exclusive) {
	    	print(", false");
	    }
	    print(")");
		return null;
	}
	
	@Override
	public Object visitDotNode(DotNode node) {
		// TOADD boolean isLiteral = node.isLiteral();
		print("new Range(");
	    visitNode(node.getBeginNode());
	    print(", ");
	    visitNode(node.getEndNode());
	    boolean exclusive = node.isExclusive();
	    if (! exclusive) {
	    	print(", false");
	    }
	    print(")");
		return null;
	}


	
	@Override
	public Object visitFloatNode(FloatNode node) {
		double value = node.getValue();
		print(Double.toString(value));
		return null;
	}

	@Override
	public Object visitForNode(ForNode node) {
		// TODO Auto-generated method stub
		unsupported(node); 
		return null;
	}

	@Override
	public Object visitGlobalAsgnNode(GlobalAsgnNode node) {
		String name = node.getName();
		print("$setGlobal(\"" + name + "\", ");
		visitNode(node.getValueNode());
		println(");");
		return null;
	}

	@Override
	public Object visitGlobalVarNode(GlobalVarNode node) {
		String name = node.getName();
		print("$getGlobal(\"" + name + "\")");
		return null;
	}

	@Override
	public Object visitHashNode(HashNode node) {
		List<KeyValuePair<Node, Node>> pairs = node.getPairs();
        print("{ ");
		for (Iterator<KeyValuePair<Node,Node>> iter = pairs.iterator(); iter.hasNext(); ) {
        	KeyValuePair<Node,Node> pair = iter.next();
            visitNode(pair.getKey());
            print("= ");
            visitNode(pair.getValue());
            if (iter.hasNext()) {
            	print(", ");
            }
        }
		print(" }");
		return null;
	}

	@Override
	public Object visitInstAsgnNode(InstAsgnNode node) {
		String name = rubyVar2javaIdent(node.getName());
		print("this.");
		print(name);
		print(" = ");
		visitNode(node.getValueNode());
		println(";");
		return null;
	}

	@Override
	public Object visitInstVarNode(InstVarNode node) {
		String name = rubyVar2javaIdent(node.getName());
		print("this.");
		print(name);
		return null;
	}

	private String rubyVar2javaIdent(String name) {
		String res = name;
		if (res.startsWith("@")) {
			res = res.substring(1);
		}// else should not occurs
		return ruby2javaIdent(res);
	}
	
	@Override
	public Object visitIfNode(IfNode node) {
		print("if (");
		visitNode(node.getCondition());
		print(") ");
		visitNode(node.getThenBody());
		Node elseBody = node.getElseBody();
		if (elseBody != null) {
			print(" else ");
			visitNode(elseBody);
		}
		return null;
	}

	@Override
	public Object visitIterNode(IterNode node) {
	    Node varNode = node.getVarNode();
	    Node bodyNode = node.getBodyNode();
	    print("(");
	    visitNode(varNode);
	    print(") -> ");
	    visitNode(bodyNode);
		return null;
	}

	@Override
	public Object visitKeywordArgNode(KeywordArgNode node) {
		// TODO Auto-generated method stub
		unsupported(node); return null;
	}

	@Override
	public Object visitKeywordRestArgNode(KeywordRestArgNode node) {
		// TODO Auto-generated method stub
		unsupported(node); return null;
	}

	@Override
	public Object visitLambdaNode(LambdaNode node) {
	    Node varNode = node.getVarNode();
	    Node bodyNode = node.getBodyNode();
	    print("(");
	    visitNode(varNode);
	    print(") -> ");
	    visitNode(bodyNode);
		return null;
	}

	@Override
	public Object visitListNode(ListNode node) {
		Node[] children = node.children();
		visitNodes(children, ", ");
		return null;
	}

	@Override
	public Object visitLiteralNode(LiteralNode node) {
		String name = node.getName();
		print(name);
		return null;
	}

	@Override
	public Object visitLocalAsgnNode(LocalAsgnNode node) {
		String name = ruby2javaIdent(node.getName());
		Node value = node.getValueNode();
		print(name);
		print(" = ");
		visitNode(value);
		return null;
	}

	@Override
	public Object visitLocalVarNode(LocalVarNode node) {
		String name = ruby2javaIdent(node.getName());
		print(name);
		return null;
	}

	@Override
	public Object visitMultipleAsgnNode(MultipleAsgnNode node) {
		// TODO Auto-generated method stub
		unsupported(node); return null;
	}

	@Override
	public Object visitMatch2Node(Match2Node node) {
		// TODO Auto-generated method stub
		unsupported(node); return null;
	}

	@Override
	public Object visitMatch3Node(Match3Node node) {
		// TODO Auto-generated method stub
		unsupported(node); return null;
	}

	@Override
	public Object visitMatchNode(MatchNode node) {
		// TODO Auto-generated method stub
		unsupported(node); return null;
	}

	@Override
	public Object visitNextNode(NextNode node) {
		// TODO Auto-generated method stub
		unsupported(node); return null;
	}

	@Override
	public Object visitNilNode(NilNode node) {
		print("null");
		return null;
	}

	@Override
	public Object visitNthRefNode(NthRefNode node) {
		// TODO Auto-generated method stub
		unsupported(node); return null;
	}

	@Override
	public Object visitOpElementAsgnNode(OpElementAsgnNode node) {
		// TODO Auto-generated method stub
		unsupported(node); return null;
	}

	@Override
	public Object visitOpAsgnNode(OpAsgnNode node) {
	    Node receiverNode = node.getReceiverNode();
	    String opAssign = node.getOperatorName() + "=";
	    Node valueNode = node.getValueNode();
	    // String variableName;
	    // String variableNameAsgn;
	    doVisitAssign(receiverNode, opAssign, valueNode);
		return null;
	}

	private void doVisitAssign(Node lhs, String opAssign, Node rhs) {
		visitNode(lhs);
		print(" " + opAssign + " ");
	    visitNode(rhs);
	    println(";");
	}

	@Override
	public Object visitOpAsgnAndNode(OpAsgnAndNode node) {
		Node lhs = node.getFirstNode();
		Node rhs = node.getSecondNode();
	    doVisitAssign(lhs, "&&=", rhs);
		return null;
	}

	@Override
	public Object visitOpAsgnOrNode(OpAsgnOrNode node) {
		Node lhs = node.getFirstNode();
		Node rhs = node.getSecondNode();
	    doVisitAssign(lhs, "||=", rhs);
		return null;
	}

	@Override
	public Object visitOptArgNode(OptArgNode node) {
		Node value = node.getValue();
		if (value instanceof LocalAsgnNode) {
			LocalAsgnNode value2 = (LocalAsgnNode) value;
			visitNode(value2);
		} else {
			print("/*SHOULD NOT OCCUR: OptArgNode(..)*/");
			visitNode(value);
		}
		return null;
	}

	@Override
	public Object visitOrNode(OrNode node) {
		// TOADD... parenthesis precedence
		visitNode(node.getFirstNode());
		print(" || ");
		visitNode(node.getSecondNode());
		return null;
	}

	@Override
	public Object visitPreExeNode(PreExeNode node) {
		// TODO Auto-generated method stub
		unsupported(node); return null;
	}

	@Override
	public Object visitPostExeNode(PostExeNode node) {
		// TODO Auto-generated method stub
		unsupported(node); return null;
	}

	@Override
	public Object visitRationalNode(RationalNode node) {
		// TODO Auto-generated method stub
		unsupported(node); return null;
	}

	@Override
	public Object visitRedoNode(RedoNode node) {
		print("/*UNSUPPORTED redo (deprecated)*/");
		return null;
	}

	@Override
	public Object visitRegexpNode(RegexpNode node) {
		String value = ruby2javaText(node.getValue());
	    // TOADD ? RegexpOptions options = node.getOptions();
	    print("Pattern.compile(\"");
        print(value); // TODO escape java chars
	    print("\")");
		return null;
	}

	@Override
	public Object visitRequiredKeywordArgumentValueNode(RequiredKeywordArgumentValueNode node) {
		// TODO Auto-generated method stub
		unsupported(node); return null;
	}

	@Override
	public Object visitRescueBodyNode(RescueBodyNode node) {
		// TODO Auto-generated method stub
		unsupported(node); return null;
	}

	@Override
	public Object visitRescueNode(RescueNode node) {
	    Node bodyNode = node.getBodyNode();
	    RescueBodyNode rescueNode = node.getRescueNode();
	    Node elseNode = node.getElseNode();
	    
	    println("try {");
	    visitNode(bodyNode);
	    println("} ");
	    rescueNode.accept(this);
	    println("catch(Throwable else) {");
	    visitNode(elseNode);
	    println("}");
	    return null;
	}

	@Override
	public Object visitRestArgNode(RestArgNode node) {
		String name = ruby2javaIdent(node.getName());
		print(name);
		return null;
	}

	@Override
	public Object visitRetryNode(RetryNode node) {
		print("/* UNSUPPORTED retry */");
		visitNode(node.getValueNode());
		return null;
	}

	@Override
	public Object visitReturnNode(ReturnNode node) {
		print("return ");
		visitNode(node.getValueNode());
		print(";"); // expr->stmt ??
		return null;
	}

	@Override
	public Object visitSClassNode(SClassNode node) {
		// represents "class << anObject ...end"
	    Node receiverNode = node.getReceiverNode();
	    Node bodyNode = node.getBodyNode();
	    
	    Node prevClassDecl = this.currentClassDecl;
	    this.currentClassDecl = receiverNode;
	    this.currentClassDeclIsStatic = (currentClassDecl instanceof SelfNode); // TODO...
	    try {
	    	if (currentClassDeclIsStatic) {
	    		print("/* static (singleton self) */");
	    	} else {
	    		print("/* singleton class */");
	    		visitNode(receiverNode);
	    	}
	    	visitNode(bodyNode);
	    } finally {
	    	this.currentClassDecl = prevClassDecl;
			this.currentClassDeclIsStatic = (currentClassDecl instanceof SelfNode); // TODO...
	    }
		return null;
	}

	@Override
	public Object visitSelfNode(SelfNode node) {
		print("this"); // TOCHECK when singleton self => current class 
		return null;
	}

	@Override
	public Object visitSplatNode(SplatNode node) {
		// TODO Auto-generated method stub
		unsupported(node); return null;
	}

	@Override
	public Object visitStrNode(StrNode node) {
		String str = ruby2javaText(node.getValue());
        // ignored? int codeRange = node.getCodeRange();
        print("\"" + str + "\"");
        return null;
	}

	private String ruby2javaText(ByteList bytes) {
		return Helpers.decodeByteList(ruby, bytes);
	}
	
	@Override
	public Object visitSuperNode(SuperNode node) {
		Node argsNode = node.getArgsNode();
		Node iterNode = node.getIterNode();
		
		print("super(");
		visitNode(argsNode);
		if (iterNode != null) {
			print(", ");
			visitNode(iterNode);
		}
		print(")");
		return null;
	}

	@Override
	public Object visitZSuperNode(ZSuperNode node) {
		Node iterNode = node.getIterNode();
		print("super(");
		visitNode(iterNode);
		print(")");
		return null;
	}

	@Override
	public Object visitSValueNode(SValueNode node) {
		Node value = node.getValue();
		unsupported(node);
		visitNode(value);
		return null;
	}

	@Override
	public Object visitSymbolNode(SymbolNode node) {
		String str = node.getName();
		print("\"" + str + "\"");
		return null;
	}

	@Override
	public Object visitTrueNode(TrueNode node) {
		print("true");
		return null;
	}

	@Override
	public Object visitUndefNode(UndefNode node) {
		Node name = node.getName();
		// String name = ruby2javaIdent();
		print("$undef(");
		visitNode(name);
		println(");");
		return null;
	}

	@Override
	public Object visitUntilNode(UntilNode node) {
		// TODO Auto-generated method stub
		unsupported(node); return null;
	}

	@Override
	public Object visitWhenNode(WhenNode node) {
		// TODO Auto-generated method stub
		unsupported(node); return null;
	}

	@Override
	public Object visitWhileNode(WhileNode node) {
		// TODO Auto-generated method stub
		unsupported(node); return null;
	}

	@Override
	public Object visitXStrNode(XStrNode node) {
		// TODO Auto-generated method stub
		unsupported(node); return null;
	}

	@Override
	public Object visitYieldNode(YieldNode node) {
		// TODO Auto-generated method stub
		unsupported(node); return null;
	}

	@Override
	public Object visitOther(Node node) {
		// TODO Auto-generated method stub
		unsupported(node); 
		return null;
	}

}
