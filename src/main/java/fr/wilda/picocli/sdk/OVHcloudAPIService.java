package fr.wilda.picocli.sdk;


import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/v1")
@RegisterRestClient
@ClientHeaderParam(name = "X-Ovh-Consumer", value = "${ovhcloud.consumer}")
@ClientHeaderParam(name = "X-Ovh-Application", value = "${ovhcloud.application}")
@ClientHeaderParam(name = "Content-Type", value = "application/json")
public interface OVHcloudAPIService {

	@GET
	@Path("/me")
	OVHcloudUser getMe(@HeaderParam("X-Ovh-Signature") String signature,
			@HeaderParam("X-Ovh-Timestamp") String ovhTimestamp);

	@GET
	@Path("/cloud/project/{projectId}/kube")
	String[] getKubernetes(@PathParam("projectId") String projectId,
			@HeaderParam("X-Ovh-Signature") String signature,
			@HeaderParam("X-Ovh-Timestamp") String ovhTimestamp);

	@GET
	@Path("/cloud/project")
	String[] getProjects(
			@HeaderParam("X-Ovh-Signature") String signature,
			@HeaderParam("X-Ovh-Timestamp") String ovhTimestamp);

	@GET
	@Path("/cloud/project/{projectId}/kube/{kubeId}")
	OVHcloudKube getKubernete(@PathParam("projectId") String projectId, @PathParam("kubeId") String kubeId,
			@HeaderParam("X-Ovh-Signature") String signature,
			@HeaderParam("X-Ovh-Timestamp") String ovhTimestamp);
}
