package fr.wilda.picocli;

import fr.wilda.picocli.sdk.OvhApi;
import fr.wilda.picocli.sdk.OvhApiException;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "greeting", mixinStandardHelpOptions = true)
public class GreetingCommand implements Runnable {

    @Parameters(paramLabel = "<name>", defaultValue = "picocli", description = "Your name.")
    String name;

    @Override
    public void run() {
        String endpoint = "ovh-eu";
        String appKey = "xxx";
        String appSecret = "xxx";
        String consumerKey = "xxxx";

        OvhApi api = new OvhApi(endpoint, appKey, appSecret, consumerKey);
        try {
            System.out.println("/me: " + api.get("/me"));
            System.out.println("/Kube: " + api.get("/cloud/project/xxx/kube"));
            System.out.println("/Kube: " + api.get("/cloud/project/xxxx/kube/xxx"));
        } catch (OvhApiException e) {
            e.printStackTrace();
        }
        System.out.printf("Hello %s, go go commando!\n", name);
    }

}
