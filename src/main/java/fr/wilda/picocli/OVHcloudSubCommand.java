package fr.wilda.picocli;

import java.util.concurrent.Callable;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fr.wilda.picocli.sdk.OVHcloudAPIService;
import fr.wilda.picocli.sdk.OVHcloudSignatureHelper;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "ovhcloud", mixinStandardHelpOptions = true)
public class OVHcloudSubCommand implements Callable<Integer> {

  // Logger
  private static final Logger _LOG = LoggerFactory.getLogger(OVHcloudSubCommand.class);

  // Option to activate the /me OVHcloud REST API
  @Option(names = {"-m", "--me"}, description = "Display the OVHcloud account details.")
  private boolean me;

  @Option(names = {"-k", "--kube"}, description = "Display your Managed Kubernetes Service created.")
  private boolean kube;

  // Service to call the OVHcloud REST API
  @RestClient
  OVHcloudAPIService apiService;

  // OVHcloud public cloud project ID injected by environment variables in the
  // application.properties file
  @ConfigProperty(name = "ovhcloud.projectId")
  String projectId;

  // Timestamp to add to each OVHcloud API call
  private Long ovhTimestamp;

  @Override
  public Integer call() throws Exception {
    ovhTimestamp = System.currentTimeMillis() / 1000;
    if (me) {
      _LOG.info("Me:\n{}", apiService.getMe(OVHcloudSignatureHelper.signature("me", ovhTimestamp),
          Long.toString(ovhTimestamp)));
    }

    if (kube) {
      String[] kubes = apiService.getKubernetes(projectId,
          OVHcloudSignatureHelper.signature("cloud/project/" + projectId + "/kube", ovhTimestamp),
          Long.toString(ovhTimestamp));
      _LOG.info("\nNumber of Kubernetes clusters: {}", kubes.length);

      for (String kubeId : kubes) {
        _LOG.info("\n{}",
            apiService.getKubernete(
                projectId, kubeId, OVHcloudSignatureHelper
                    .signature("cloud/project/" + projectId + "/kube/" + kubeId, ovhTimestamp),
                Long.toString(ovhTimestamp)));
      }
    }

    return 0;
  }
}
