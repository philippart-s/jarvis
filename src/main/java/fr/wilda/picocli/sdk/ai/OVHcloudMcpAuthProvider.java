package fr.wilda.picocli.sdk.ai;


import io.quarkiverse.langchain4j.mcp.auth.McpClientAuthProvider;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class OVHcloudMcpAuthProvider implements McpClientAuthProvider {

  @ConfigProperty(name = "ovhcloud.pat")
  String ovhcloudPAT;

  @Override
  public String getAuthorization(Input input) {
    return "Bearer " + ovhcloudPAT;
  }
}
