package neo4j.example;

public class Relation {
	public static final String OUT = "out";
    public static final String IN = "in";
    public static final String BOTH = "both";
    private String type;
    private String direction;

    public String toJsonCollection()
    {
        StringBuilder sb = new StringBuilder();
        sb.append( "{ " );
        sb.append( " \"type\" : \"" + type + "\"" );
        if ( direction != null )
        {
            sb.append( ", \"direction\" : \"" + direction + "\"" );
        }
        sb.append( " }" );
        return sb.toString();
    }

    public Relation( String type, String direction )
    {
        setType( type );
        setDirection( direction );
    }

    public Relation( String type )
    {
        this( type, null );
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public void setDirection( String direction )
    {
        this.direction = direction;
    }
}
