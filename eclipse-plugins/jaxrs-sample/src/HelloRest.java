package fr.an.test.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.springframework.stereotype.Component;

import fr.an.refactor.POST_TupleBodyFragments;

@Component
@Path("/hello")
public class HelloRest {

	@GET
	public String message() {
		return "Hello ";
	}

	@POST_TupleBodyFragments
	public String meth1(String arg1, String arg2) {
		return "Hello " + arg1;
	}

	@POST_TupleBodyFragments
	public String meth2(String arg1, String arg2, int arg3) {
		return "Hello " + arg1;
	}

}
