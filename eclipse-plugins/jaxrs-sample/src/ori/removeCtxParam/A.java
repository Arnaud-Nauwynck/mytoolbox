package fr.an.test.removeCtxParam;

public class A {

	static A a = new A();
	
	public void foo(int i, String j, RemoveCtx ctx) {
		bar(i, ctx);
	}

	private void bar(int i, RemoveCtx ctx) {
		a.baz(ctx);
	}

	private void baz(RemoveCtx ctx) {		
	}
	
}
