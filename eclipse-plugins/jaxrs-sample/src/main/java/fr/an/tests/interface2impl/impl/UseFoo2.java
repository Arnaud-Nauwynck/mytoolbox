package fr.an.tests.interface2impl.impl;

import javax.inject.Inject;

import fr.an.tests.interface2impl.IBar;
import fr.an.tests.interface2impl.IFoo;

public class UseFoo2 {

	@Inject protected IFoo foo;
	@Inject protected IFoo foo2;
	@Inject protected IBar bar;
	@Inject protected IBar bar2;
	
	public static class InnerUseFoo1 {

		@Inject protected IFoo foo;
		@Inject protected IFoo foo2;
		@Inject protected IBar bar;
		@Inject protected IBar bar2;
		
	}
}
