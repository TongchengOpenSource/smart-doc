package com.power.doc.constants;

/**
 * Java EE has been renamed Jakarta EE, this class is an upgraded replacement for {@link JAXRSAnnotations}
 *
 * JAX-RS Annotations
 *
 * @author youngledo
 */
public final class JakartaJaxrsAnnotations {

    private JakartaJaxrsAnnotations() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * JAX-RS@DefaultValue
     */
    public static final String JAX_DEFAULT_VALUE_FULLY = "jakarta.ws.rs.DefaultValue";
    /**
     * JAX-RS@HeaderParam
     */
    public static final String JAX_HEADER_PARAM_FULLY = "jakarta.ws.rs.HeaderParam";
    /**
     * JAX-RS@PathParam
     */
    public static final String JAX_PATH_PARAM_FULLY = "jakarta.ws.rs.PathParam";
    /**
     * JAX-RS@PATH
     */
    public static final String JAX_PATH_FULLY = "jakarta.ws.rs.Path";
    /**
     * JAX-RS@Produces
     */
    public static final String JAX_PRODUCES_FULLY = "jakarta.ws.rs.Produces";
    /**
     * JAX-RS@Consumes
     */
    public static final String JAX_CONSUMES_FULLY = "jakarta.ws.rs.Consumes";
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
    public static final String JAX_GET_FULLY = "jakarta.ws.rs.GET";
    /**
     * JAX-RS@POST
     */
    public static final String JAX_POST_FULLY = "jakarta.ws.rs.POST";
    /**
     * JAX-RS@PUT
     */
    public static final String JAX_PUT_FULLY = "jakarta.ws.rs.PUT";
    /**
     * JAX-RS@DELETE
     */
    public static final String JAXB_DELETE_FULLY = "jakarta.ws.rs.DELETE";
    /**
     * JAX-RS@RestPath
     */
    public static final String JAXB_REST_PATH_FULLY = "org.jboss.resteasy.reactive.RestPath";
    /**
     * JAX-RS@PATCH
     */
    public static final String JAX_PATCH_FULLY = "jakarta.ws.rs.PATCH";
    /**
     * JAX-RS@HEAD
     */
    public static final String JAX_HEAD_FULLY = "jakarta.ws.rs.HEAD";

}