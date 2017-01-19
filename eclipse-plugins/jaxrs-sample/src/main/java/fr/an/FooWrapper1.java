package fr.an;

public class FooWrapper1 {

	Foo delegate;

	public void foo() {
		delegate.foo();
	}

	public void foo(int param1) {
		delegate.foo(param1);
	}

	public String foo(int param1, String param2) {
		return delegate.foo(param1, param2);
	}

	
	
	
	public void fooEx() {
		try {
			delegate.foo();
		} catch(Exception ex) {
			throw new RuntimeException("Failed", ex);
		}
	}

	public void fooEx(int param1) {
		delegate.foo(param1);
	}

	public String fooEx(int param1, String param2) {
		return delegate.foo(param1, param2);
	}


	
}
