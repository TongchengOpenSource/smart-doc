package com.power.doc.constants;

/**
 * JAX-RS Annotations
 *
 * @author Zxq
 *
 * @deprecated Java EE has been renamed to Jakarta EE, an upgrade is recommended.
 * @see JakartaJaxrsAnnotations
 */
@Deprecated
public final class JAXRSAnnotations {

    private JAXRSAnnotations() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * JAX-RS@DefaultValue
     */
    public static final String JAX_DEFAULT_VALUE_FULLY = "javax.ws.rs.DefaultValue";
    /**
     * JAX-RS@HeaderParam
     */
    public static final String JAX_HEADER_PARAM_FULLY = "javax.ws.rs.HeaderParam";
    /**
     * JAX-RS@PathParam
     */
    public static final String JAX_PATH_PARAM_FULLY = "javax.ws.rs.PathParam";
    /**
     * JAX-RS@PATH
     */
    public static final String JAX_PATH_FULLY = "javax.ws.rs.Path";
    /**
     * JAX-RS@Produces
     */
    public static final String JAX_PRODUCES_FULLY = "javax.ws.rs.Produces";
    /**
     * JAX-RS@Consumes
     */
    public static final String JAX_CONSUMES_FULLY = "javax.ws.rs.Consumes";
    /**
     * JAX-RS@GET
     */
    public static final String GET = "GET";
    /**
     * JAX-RS@POST
     */
    public static final String POST = "POST";
    /**
     * JAX-RS@PUT
     */
    public static final String PUT = "PUT";
    /**
     * JAX-RS@DELETE
     */
    public static final String DELETE = "DELETE";

    /**
     * JAX-RS@GET
     */
    public static final String JAX_GET_FULLY = "javax.ws.rs.GET";
    /**
     * JAX-RS@POST
     */
    public static final String JAX_POST_FULLY = "javax.ws.rs.POST";
    /**
     * JAX-RS@PUT
     */
    public static final String JAX_PUT_FULLY = "javax.ws.rs.PUT";
    /**
     * JAX-RS@DELETE
     */
    public static final String JAXB_DELETE_FULLY = "javax.ws.rs.DELETE";
    /**
     * JAX-RS@RestPath
     */
    public static final String JAXB_REST_PATH_FULLY = "org.jboss.resteasy.reactive.RestPath";

}