package filters;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
@TokenQueryValidator
public class TokenFilter implements ContainerRequestFilter{
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        MultivaluedMap<String, String> queryParameters = requestContext.getUriInfo().getQueryParameters();

        List<String> parameters =  queryParameters.keySet().stream().filter(param -> param.equals("token") ).collect(Collectors.toList());

        if ( parameters.size()==0 ) {
            requestContext.abortWith(Response
            .status(400)
            .entity("Token is missing")
            .build());
        }

    }

}
