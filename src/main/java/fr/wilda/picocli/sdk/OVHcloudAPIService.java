package fr.wilda.picocli.sdk;


import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/v1")
@RegisterRestClient
@ClientHeaderParam(name = "X-Ovh-Application", value = "{autorizationHeader}")
public interface OVHcloudAPIService {

    @GET
    @Path("/me")
    OVHcloudUser getMe();
}