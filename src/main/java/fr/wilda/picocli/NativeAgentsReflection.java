package fr.wilda.picocli;

import fr.wilda.picocli.sdk.ai.agent.AutonomousAgent;
import fr.wilda.picocli.sdk.ai.agent.common.ClassifierAgent;
import fr.wilda.picocli.sdk.ai.agent.common.JarvisAgent;
import fr.wilda.picocli.sdk.ai.agent.common.OVHcloudAgent;
import fr.wilda.picocli.sdk.ai.agent.common.RagAgent;
import fr.wilda.picocli.sdk.ai.agent.workflow.AvailableAgents;
import fr.wilda.picocli.sdk.ai.agent.workflow.ChatAgent;
import fr.wilda.picocli.sdk.ai.agent.workflow.JarvisWorkflow;
import io.quarkus.runtime.annotations.RegisterForReflection;

/// Keeps the agent interfaces reflectively visible in native mode.
///
/// LangChain4j resolves an agent by walking the interface's methods and looking
/// for `@Agent` (`AgentUtil.validateAgentClass`). In a native image, methods are
/// only visible when the class is registered for reflection, otherwise the scan
/// finds nothing and the agent bean fails with
/// `No agent method found in class: …`.
///
/// `quarkus-langchain4j-agentic` used to register this itself: up to 1.6.0 its
/// `nativeSupport` build step registered every detected agent with
/// `methods(true)`; 1.7.5 narrowed it to proxies only, and 1.12.0 dropped the
/// step altogether. The `ReflectiveClassBuildItem` still produced by
/// `generateAgentImplementations` is built without `methods()`, so it does not
/// cover this. Hence the explicit registration here.
///
/// This class holds no code — only the annotation. Every interface carrying
/// `@Agent`, `@SequenceAgent`, `@ConditionalAgent` or `@SupervisorAgent` must be
/// listed, otherwise that agent breaks in native mode only.
@RegisterForReflection(targets = {
    ClassifierAgent.class,
    JarvisAgent.class,
    OVHcloudAgent.class,
    RagAgent.class,
    ChatAgent.class,
    AvailableAgents.class,
    JarvisWorkflow.class,
    AutonomousAgent.class
})
public class NativeAgentsReflection {
}
