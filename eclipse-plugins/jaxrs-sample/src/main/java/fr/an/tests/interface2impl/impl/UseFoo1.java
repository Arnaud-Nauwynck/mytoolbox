package fr.an.tests.interface2impl.impl;

import javax.inject.Inject;

import fr.an.tests.interface2impl.Bar;
import fr.an.tests.interface2impl.Foo;

public class UseFoo1 {

	@Inject protected Foo foo;
	@Inject protected Bar bar;

}
