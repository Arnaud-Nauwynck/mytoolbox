package fr.an.tests.interface2impl.impl;

import javax.inject.Inject;

import fr.an.tests.interface2impl.IBar;
import fr.an.tests.interface2impl.IFoo;

public class UseFoo1 {

	@Inject protected IFoo foo;
	@Inject protected IBar bar;

}
