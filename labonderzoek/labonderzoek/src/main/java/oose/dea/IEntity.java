package oose.dea;

import java.util.HashMap;

public abstract class IEntity {
    protected HashMap<String, String> properties = new HashMap<>();
    protected HashMap<String, IEntity> relations = new HashMap<>();

    public abstract String entity_name();

    public String build() {
        String build ="(" + entity_name().toLowerCase() + ":" + entity_name() + " ";
        build += "{";
        int index = 0;
        for ( String prop : properties.keySet() ) {
            String val = properties.get(prop);

            build += prop;
            build += ": ";
            build += "'" + val + "'";

            if ( index < properties.size()-1 )
                build += ", ";

            index++;
        }
        build += "}";
        build += ")";

        return build;
    }
}
