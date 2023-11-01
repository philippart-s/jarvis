package fr.wilda.picocli;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fr.wilda.picocli.sdk.OVHcloudAPIService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "greeting", mixinStandardHelpOptions = true)
public class GreetingCommand implements Runnable {
    private static final Logger _LOG = LoggerFactory.getLogger(GreetingCommand.class);
    private Long ovhTimestamp;

    @Parameters(paramLabel = "<name>", defaultValue = "picocli", description = "Your name.")
    String name;

    @ConfigProperty(name = "ovhcloud.projectId")
    String projectId;

    @RestClient
    OVHcloudAPIService apiService;

    @Override
    public void run() {
        _LOG.info("Hello, what can I do for you?");
        _LOG.info("Me:\n{}", apiService.getMe(signature("me"),
                Long.toString(ovhTimestamp)));
        _LOG.debug("My project:\n {}", projectId);
        String[] kubes = apiService.getKubernetes(projectId, signature("cloud/project/" + projectId + "/kube"),
                Long.toString(ovhTimestamp));
        _LOG.info("\nNumber of Kubernetes clusters: {}", kubes.length);

        for (String kubeId : kubes) {
            _LOG.info("\n{}", apiService.getKubernete(projectId, kubeId, signature("cloud/project/" + projectId + "/kube/" 
             + kubeId),
                Long.toString(ovhTimestamp)));
        }
    }

    private String signature(String endPoint) {
        ovhTimestamp = System.currentTimeMillis() / 1000;
        // build signature
        String toSign = new StringBuilder(System.getenv("OVH_APPLICATION_SECRET"))
                .append("+")
                .append(System.getenv("OVH_CONSUMER_KEY"))
                .append("+")
                .append("GET")
                .append("+")
                .append("https://eu.api.ovh.com/v1/" + endPoint)
                .append("+")
                .append("")
                .append("+")
                .append(ovhTimestamp)
                .toString();
        try {
            return new StringBuilder("$1$").append(hashSHA1(toSign)).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String hashSHA1(String text)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md;
        md = MessageDigest.getInstance("SHA-1");
        byte[] sha1hash = new byte[40];
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        sha1hash = md.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < sha1hash.length; i++) {
            sb.append(Integer.toString((sha1hash[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

}
