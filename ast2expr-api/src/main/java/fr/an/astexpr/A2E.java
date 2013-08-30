package fr.an.astexpr;

/**
 * Facade API class for "Annotation AST 2 Expression"
 * <p/>
 * 
 * Conceptually, it is a java API, that compile as pure Java (7 ... waiting jdk8 for closures),
 * and to be used as "AST Level" pseudo-annotation "@A2E*(..)".<br/>
 * Then, at the meta-langage level, theses "pseudo-annotations" will be used to interpret the java enriched syntax as an AST Expression object<br/>  
 * <p/>
 *
 * Your AST-Pattern processing tool will be able to understand java code fragment with this wrapper annation API as instances of AstPattern objects.
 * <p/>
 * 
 * The magic appears when using closures... because "{..}" java code block can be replaced by "A2E( ()-> {...} );"
 * <p/>
 * While still waiting with jdk<=7, you can annotate "{}" by "new A2EstmtClosure() { public void c() { ... } };"
 * 
 * <p/>
 * The magic of the magic is that you can continue using your preferred java IDE (eclipse...) to edit your pattern code<BR/>
 * You can copy&paste code from java to pattern, you can edit code, compile type-check your pattern, templatized some fragments, etc <BR/>
 * <p>
 * But don't try to execute pattern code, because it is only for interpreting from processing tool, not by runtime jvm.
 *    
 * <p/>
 * 
 *  
 * This api facade class contains 
 * <ul>
 * <li>plain old java annotations "@A2E_*" <br/>
 * to be used when java syntax authorize adding annotation of AST elements : class, field, method, ... 
 *  </li>
 * <li>plain old java methods declarations "public static .. A2E_*(..)", with A2Eclosure parameters <BR/>
 * to be used as annotation replacement on finer grain AST elements: AstStmt (IfStmt, ForStmt, ...), AstExpr (LiterraExpr, Name, BinaryOpExpr, ..)<br/>
 * </ul>
 * 
 * <HR/>
 * Example API usage of A2E_* method from your java code for pattern matching an if-then-else AST 
 *
 * <PRE>  
 *   A2E_if(() -> ( testVarName > 10 ), () -> { 
 *        System.out.println("got " + testVarName + " elts"); 
 *   }, &#047;*else*&#047;() -> { 
 *       System.out.println("only " + testVarName + " elts"); 
 *   });
 * </PRE>
 * 
 * This will match the following if-then-else AST codes
 * <PRE>
 *  public void fooMethod() {
 *      if ( testVarName > 10 ) { 
 *         System.out.println("got " + testVarName + " elts"); 
 *      } else { 
 *         System.out.println("only " + testVarName + " elts"); 
 *      }
 *  }
 * </PRE>
 * To transform the initial if-then-else java code into an if-then-else annoted pattern, perform these actions:
 * <ol>
 * <li> copy&paste this code into your pattern class
 * <li> add "import static fr.an.astexpr.A2E.*;" in your pattern class header
 * </li>
 * </li>
 * <li> then transform the "if" statement into a method call "A2E_if(expression, thenStatement,elseStatement);" 
 * </li>
 * <li> replace expression "()" by function closures "()-> (..)"  or  "()-> return .." or "() -> { return ..; }" 
 * </li>
 * <li> replace statement blocks "{}" by closures "()-> {..}"
 * </li>
 * <li> remove "else" keyword, possibly replacing by comment &#047;*else*&#047;
 * </li>
 * </ol>
 * 
 *
 * 
 * 
 * <HR/>
 * <h2>a Step-by-step explanation for if-then-else pattern</h2>
 * <p/>
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
 *      System.out.println("only " + intVar + " elts");
 *  }
 *  </PRE>
 *
 * <p/>
 *  AST class declaration:
 *  <PRE>
 *  public class IfStmt extends AbstractStmt {
 *      public AstExpr expression;
 *      public AstStmt thenStatement;
 *      public AstStmt optionalElseStatement;
 *      
 *      // .. ctor, getter, setter, visitor
 *      public void accept(AstNodeVisisor visitor) { visitor.caseIfStmt(this); }
 *  }
 *  </PRE>
 *  
 * <p/>
 *  Corresponding AST Expression Pattern class declaration:
 *  <PRE>
 *  public class IfStatementPattern extends AbstractStmtPattern {
 *      public IPattern&lt;AstExpr> expression;
 *      public IPattern&lt;AstStmt> thenStatement;
 *      public IPattern&lt;AstStmt> optionalElseStatement;
 *      
 *      // .. ctor, getter, setter, visitor
 *      public void accept(AstNodePatternVisisor visitor) { visitor.caseIfStmt(this); }
 *  }
 *  </PRE>
 *  
 *  
 * <p/>
 * ... So now, the A2E defines method(s) "pseudo-annotation wrapper" 
 * for interpreting real annoted java code with if-exp-then-else, into an if-pattern object    
 * 
 * class A2E {
 *     .. 
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
 * <p/>
 * ... This is an API method, so you can use it in your real java-compiled code
 * <PRE>
 * import fr.an.ast2expr.A2E.*;
 * 
 * public class FooPatternClass {
 *    public void fooMethodPattern() {
 *       A2E_if( 
 *          A2E_binaryOp( A2E_simpleName( "testVarName" ), ">", A2E_literalExpr(10) ), 
 *          A2E_block( A2E_any() ), 
 *          A2E_any() 
 *       );
 *    }
 * }
 * </PRE>
 *  
 * <p/>
 * Then your AST-Pattern processing tool will be able to understand this java code fragment as an instance of an AstPattern object:
 * it will run the "Ast -> to -> AstPattern Expression", so internally call "new IfStatementPattern(...)" 
 * <PRE>
 * IfStatementPattern myIfPattern = new IfStatementPattern(
 *    new BinaryOpExprPattern(
 *        new SimpleNamePattern("testVarName"),
 *        new BlockPattern( new AnyPattern() ),
 *        new AnyPattern()
 *        );
 * );
 * </PRE> 
 * 
 * more advanced if-then-else pattern code with using jdk8 closures:
 * <PRE>
 *     public void fooMethodPattern() {
 *         A2E_if( 
 *            () -> ( A2E_simpleName( "testVarName" ) > 10 ), 
 *            () -> { System.out.println("got " + A2E_simpleName( "testVarName" ) + " elts"); },
 *            &#047;*else*&#047; () -> { System.out.println("only " + A2E_simpleName( "testVarName" ) + " elts"); }
 *      );
 *  }
 * </PRE>
 * 
 * <p/>
 * by re-factoring the common variable "testVarName" out of the pattern, it really looks like java code fragments:
 * <PRE>
 * 
 * private SimpleNamePattern testVarName = new SimpleNamePattern("testVarName");
 * 
 * public void fooMethodPattern() {
 *    A2E_if( 
 *          () -> ( testVarName > 10 ), 
 *          () -> { System.out.println("got " + testVarName + " elts"); },
 *          &#047;*else*&#047; () -> { System.out.println("only " + testVarName + " elts"); }
 *    );
 *  }
 * </PRE>
 * 
 * <p/>
 * look how similar it look like the target java code to transform as a pattern:
 * <PRE>
 *  public void fooMethod() {
 *      if   ( testVarName > 10 ) 
 *           { System.out.println("got " + testVarName + " elts"); } 
 *      else { System.out.println("only " + testVarName + " elts"); }
 *  }
 * </PRE>
 * 
 * 
 * With more java compliant code indentation style, it still look like readable java/pattern code
 * 
 * <PRE>
 * 
 * private SimpleNamePattern testVarName = new SimpleNamePattern("testVarName");
 * 
 * public void fooMethodPattern() {
 *    A2E_if(() -> ( testVarName > 10 ), () -> { 
 *    	System.out.println("got " + testVarName + " elts"); 
 *    }, &#047;*else*&#047;() -> { 
 *    	System.out.println("only " + testVarName + " elts"); 
 *    });
 *  }
 * </PRE>
 * 
 * <p/>
 * look how similar it look like the target java code to transform as a pattern:
 * <PRE>
 *  public void fooMethod() {
 *      if ( testVarName > 10 ) { 
 *         System.out.println("got " + testVarName + " elts"); 
 *      } else { 
 *         System.out.println("only " + testVarName + " elts"); 
 *      }
 *  }
 * </PRE>
 * 
 * 
 * 
 * 
 * <p/>
 * less readable if-then-else pattern code with using poor old <=jdk7 annonyous inner class:
 * <PRE>
 *     private SimpleNamePattern testVarName = new SimpleNamePattern("testVarName");
 * 
 *     public void fooMethodPattern() {
 *    A2E_if( 
 *       new A2EexprClosure<boolean>() { 
 *          public bool c() { 
 *              return A2E_simpleName( "testVarName" ) > 10; 
 *          }
 *       }.toPattern(), 
 *       new A2EstmtClosure() { 
 *          public void c() { 
 *              System.out.println("got " + testVarName + " elts"); }
 *          }
 *       }.toPattern(), 
 *       new A2EstmtClosure() { 
 *          public void c() { 
 *              System.out.println("only " + testVarName + " elts"); }
 *          }
 *       }.toPattern()
 *    );
 *  }
 * </PRE>
 * 
 * 
 * 
 */
public class A2E {

    // TODO
    
}
