package fr.an.test.jaxrs;

import javax.ws.rs.Path;

import org.springframework.stereotype.Component;

@Component
@Path("/hello")
public class HelloRest implements IHelloRest {

	@Override
	public String message() {
		return "Hello ";
	}

	@Override
	public String meth1(String arg1, String arg2) {
		return "Hello " + arg1;
	}

	@Override
	public String meth2(String arg1, String arg2, int arg3) {
		return "Hello " + arg1;
	}

}
