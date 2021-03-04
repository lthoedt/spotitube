import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("test")
public class Spotitube {
    @GET
    public String test() {
        return "kaas";
    }
}
