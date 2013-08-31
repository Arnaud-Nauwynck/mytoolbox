package fr.an.testastannotation.hello;

public class HelloAstAnnotationMain {

    public static void main(String[] args) {
        foo();
        fooWithASTAnnotation();
    }

    private static void foo() {
        /* not an annotation */
        System.out.println("Hello AST Annotations");
    }

    private static void fooWithASTAnnotation() {
        /*@@TestASTAnnotation1*/
        System.out.println("Hello AST Annotations");
    }
}
