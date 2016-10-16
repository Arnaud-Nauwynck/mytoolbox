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

	public static class Meth1Request {
		public String arg1;
		public String arg2;

		public Meth1Request(String arg1, String arg2) {
			this.arg1 = arg1;
			this.arg2 = arg2;
		}
	}

	@POST_TupleBodyFragments
	public String meth1(Meth1Request req) {
		String arg1 = req.arg1;
		String arg2 = req.arg2;
		return "Hello " + arg1;
	}

	public static class Meth2Request {
		public String arg1;
		public String arg2;
		public int arg3;

		public Meth2Request(String arg1, String arg2, int arg3) {
			this.arg1 = arg1;
			this.arg2 = arg2;
			this.arg3 = arg3;
		}
	}

	@POST_TupleBodyFragments
	public String meth2(Meth2Request req) {
		String arg1 = req.arg1;
		String arg2 = req.arg2;
		int arg3 = req.arg3;
		return "Hello " + arg1;
	}

}
