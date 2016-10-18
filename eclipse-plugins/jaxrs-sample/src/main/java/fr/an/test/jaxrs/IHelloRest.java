package fr.an.test.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import fr.an.refactor.POST_TupleBodyFragments;

@Path("/hello")
public interface IHelloRest {

	@GET
	public String message();

	@POST_TupleBodyFragments
	public String meth1(String arg1, String arg2);

	@POST_TupleBodyFragments
	public String meth2(String arg1, String arg2, int arg3);

}
