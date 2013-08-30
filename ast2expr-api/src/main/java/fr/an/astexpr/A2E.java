package fr.an.astexpr;

/**
 * Facade API class for "Annotation AST 2 Expression"
 * 
 * Conceptually, it is a java API, that compile as pure Java (7 ... waiting jdk8 for closures),
 * and to be used as "AST Level" pseudo-annotation "@A2E*(..)" 
 * Then, at the meta-langage level, theses "pseudo-annotations" will be used to interpret the java enriched syntax as an AST Expression object  
 *
 * Your AST-Pattern processing tool will be able to understand java code fragment with this wrapper annation API as instances of AstPattern objects.
 * 
 * The magic appears when using closures... because "{..}" java code block can be replaced by "A2E( ()-> {...} );"
 * 
 *  
 * <P/>
 * This api facade class contains 
 * <ul>
 * <li>annotations "@A2E_*" <br/>
 * to be used when java syntax authorize adding annotation of AST elements : class, field, method, ... 
 *  </li>
 * <li>methods declarations "public static .. A2E_*(..)", with closures parameters <BR/>
 * to be used as annotation replacement on finer grain AST elements: AstStmt (IfStmt, ForStmt, ...), AstExpr (LiterraExpr, Name, BinaryOpExpr, ..)<br/>
 * </ul>
 * 
 *  example: for an "if-stmt"
 *  Java grammar syntax:  
 *  <PRE>
 *  if '(' testExpr ')' thenStmt (else elseStmt)? 
 *  </PRE>
 *  Java code sample:
 *  <PRE>
 *  if (intVar > 10) System.out.println("got " + intVar + " elts");
 *   
 *  if (intVar > 10) {
 *     System.out.println("got " + intVar + " elts");
 *  }
 *  
 *  if (intVar > 10) {
 *     System.out.println("got " + intVar + " elts");
 *  } else {
 *  	System.out.println("only " + intVar + " elts");
 *  }
 *  </PRE>
 *
 *  AST class declaration:
 *  <PRE>
 *  public class IfStmt extends AbstractStmt {
 *  	public AstExpr expression;
 *  	public AstStmt thenStatement;
 *  	public AstStmt optionalElseStatement;
 *      
 *      // .. ctor, getter, setter, visitor
 *  	public void accept(AstNodeVisisor visitor) { visitor.caseIfStmt(this); }
 *  }
 *  </PRE>
 *  
 *  Corresponding AST Pattern class declaration:
 *  <PRE>
 *  public class IfStatementPattern extends AbstractStmtPattern {
 *  	public IPattern<AstExpr> expression;
 *  	public IPattern<AstStmt> thenStatement;
 *  	public IPattern<AstStmt> optionalElseStatement;
 *      
 *      // .. ctor, getter, setter, visitor
 *  	public void accept(AstNodePatternVisisor visitor) { visitor.caseIfStmt(this); }
 *  }
 *  </PRE>
 *  
 *  
 * ... So now, the A2E defines method(s) "pseudo-annotation wrapper" 
 * for interpreting real annoted java code with if-exp-then-else, into an if-pattern object    
 * 
 * class A2E {
 * 	.. 
 *  public <T> static void A2E_if(A2EExprClosure<T> expressionClosure, A2EStmtClosure thenStatementClosure, A2EStmtClosure optionalElseStatementClosure) {}
 *  
 *  // overload method signature, with no else-statement
 *  public <T> static void A2E_if(A2EExprClosure<T> expressionClosure, A2EStmtClosure thenStatementClosure) {}
 *  
 *  // overload method signature with literals
 *  public static void A2E_if(boolean expression, A2EStmtClosure thenStatementClosure, A2EStmtClosure optionalElseStatementClosure) {}
 *  public static void A2E_if(boolean expression, A2EStmtClosure thenStatementClosure) {}
 *  ..
 * }
 * 
 * ... This is an API method, so you can use it in your real java-compiled code
 * <PRE>
 * import fr.an.ast2expr.A2E.*;
 * 
 * public class FooClass {
 * 	public void fooMethod() {
 *    A2E_if( 
 *    	A2E_binaryOp( A2E_simpleName( "testVarName" ), ">", A2E_literalExpr(10) ), 
 *    	A2E_block( A2E_any() ), 
 *    	A2E_any() 
 *    );
 * 	}
 * </PRE>
 *  
 * Then your AST-Pattern processing tool will be able to understand this java code fragment as an instance of an AstPattern object:
 * it will run the "Ast -> to -> AstPattern Expression", so internally call "new IfStatementPattern(...)" 
 * <PRE>
 * IfStatementPattern myIfPattern = new IfStatementPattern(
 * 	  	new BinaryOpExprPattern( new SimpleNamePattern("testVarName"),
 * 		new BlockPattern( new AnyPattern() ),
 * 		new AnyPattern()
 * 		);
 * );
 * </PRE> 
 * 
 * more advanced if-then-else pattern code with using jdk8 closures:
 * <PRE>
 * 	public void fooMethod() {
 * 		A2E_if( 
 *    		() -> ( A2E_simpleName( "testVarName" ) > 10 ), 
 *    		() -> { System.out.println("got " + A2E_simpleName( "testVarName" ) + " elts"); } 
 *    		() -> { System.out.println("only " + A2E_simpleName( "testVarName" ) + " elts"); }
 *      );
 *  }
 * </PRE>
 * 
 * less readable if-then-else pattern code with using poo old <=jdk7 annonyous inner class:
 * <PRE>
 * 	public void fooMethod() {
 * 		A2E_if( 
 *    		new A2EexprClosure<boolean>() { 
 *    			public bool c() { 
 *    				return A2E_simpleName( "testVarName" ) > 10; 
 *    			}
 *    		}.toPattern(), 
 *    		new A2EstmtClosure() { 
 *    			public void c() { 
 *    				System.out.println("got " + A2E_simpleName( "testVarName" ) + " elts"); }
 *    			}
 *    		}.toPattern(), 
 *    		new A2EstmtClosure() { 
 *    			public void c() { 
 *					System.out.println("only " + A2E_simpleName( "testVarName" ) + " elts"); }
 *				}
 *			}.toPattern()
 *      );
 *  }
 * </PRE>
 * 
 * 
 * 
 */
public class A2E {

	// TODO
	
}
