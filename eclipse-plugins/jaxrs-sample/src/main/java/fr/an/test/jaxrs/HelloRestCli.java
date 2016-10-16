package fr.an.test.jaxrs;

public class HelloRestCli {

	protected HelloRest target;
	
	public void meth1(String arg1, String arg2) {
		target.meth1(arg1, arg2);
	}

	public void meth2(String arg1, String arg2, int arg3) {
		target.meth2(arg1, arg2, arg3);
	}

}
