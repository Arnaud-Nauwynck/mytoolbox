package fr.an.tests.interface2impl.impl;

import javax.inject.Inject;

import fr.an.tests.interface2impl.Bar;
import fr.an.tests.interface2impl.Foo;

public class UseFoo2 {

	@Inject protected Foo foo;
	@Inject protected Foo foo2;
	@Inject protected Bar bar;
	@Inject protected Bar bar2;
	
	public static class InnerUseFoo1 {

		@Inject protected Foo foo;
		@Inject protected Foo foo2;
		@Inject protected Bar bar;
		@Inject protected Bar bar2;
		
	}
}
