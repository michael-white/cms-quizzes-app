package com.sharecare.cms.guides.activation.publishing;

import com.google.common.collect.Sets;
import com.sharecare.cms.guides.activation.remote.HealthGuideRequestBuilder;
import com.sharecare.cms.guides.configuration.HealthGuideModuleConfig;
import com.sharecare.cms.publishing.commons.activation.RemoteDataPublisher;
import com.sharecare.cms.publishing.commons.configuration.CommonsModuleConfig;
import com.sharecare.cms.publishing.commons.configuration.RemoteServerResourceConfig;
import com.sharecare.core.sdk.BasicResponse;
import com.sharecare.core.sdk.configuration.BasicAuthCredentials;
import com.sharecare.core.sdk.configuration.ServerInfo;
import com.sharecare.healthguides.sdk.HealthGuidesApiClient;
import com.sharecare.healthguides.sdk.model.HealthGuideRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import javax.inject.Inject;
import javax.jcr.*;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sharecare.cms.publishing.commons.ui.taglib.activation.EnvironmentActivationField.ACTIVE_STATUS_FIELD;

/**
 * Created by robert.davis on 12/20/2016.
 */
@Slf4j
public class RemoteHealthGuidePublisher implements RemoteDataPublisher {

    static final String NODE_TYPE = "mgnl:healthguide";

    private Map<String,HealthGuidesApiClient> clientMap;
    private HealthGuideRequestBuilder requestBuilder;
    //private HealthGuideAssetProcessor assetProcessor;

    private interface StatusUpdater<V, I, S> {
        boolean updateStatus(V valueFactory, I item, S environment);
    }


    @Inject
    public RemoteHealthGuidePublisher(HealthGuideModuleConfig moduleConfig,
                                  CommonsModuleConfig commonsModuleConfig,
                                  HealthGuideRequestBuilder requestBuilder)
    {
        this.clientMap = buildApiClients(moduleConfig.getPublishing().get(commonsModuleConfig.getEnvironment()));
        this.requestBuilder = requestBuilder;
    }


    @Override
    public boolean publish(Node node, String environment) {

        try {
            log.info("Publishing {}:{} content to {} ", node.getName(), node.getIdentifier(), environment);
            HealthGuidesApiClient client = clientMap.get(environment);

         //   Optional<HealthGuideUploadResult> uploadResult = assetProcessor.uploadAssetFrom(node);
              HealthGuideRequest request = requestBuilder.forNode(node);

            BasicResponse response = client.saveRequest().withData(request).execute();
            if (String.valueOf(response.getStatusCode()).startsWith("20")) {
                log.info("Successfully published content item {}:{} to {}", node.getPrimaryNodeType().getName(), node.getIdentifier(), environment);
                if (!activeStatusUpdater.updateStatus(node, environment, addEnvironmentCallback))
                    log.error("Failed to update node status: {}", node);
            } else {
                log.error("Failed Activation on  {} . Response from service {}", environment, response.getStatusCode());
                return false;
            }
        }
        catch (Exception e){
            log.error("Failed Activation of article  {} ", ExceptionUtils.getFullStackTrace(e));
            return false;
        }

        return true;
    }

    @Override
    public boolean unPublish(Node node, String environment) {
        try {

            log.warn("Deleting {}:{} content from {} ", node.getName(), node.getIdentifier(), environment);
            HealthGuidesApiClient client = clientMap.get(environment);
            log.debug("Executing DELETE rest call {}", node.getName());
            BasicResponse response = client.deleteRequest().withUri(node.getName()).execute();
            if (response.getStatusCode() == 200) {
                log.info("Successfully deleted content item {} from remote", node.getName());
                if (!activeStatusUpdater.updateStatus(node, environment, removeEnvironmentCallback))
                    log.error("Failed to update node status: {}", node);
            }
        } catch (Exception e) {
            log.error("Failed De-Activation of article  {} ", ExceptionUtils.getFullStackTrace(e));
            return false;
        }

        return true;
    }

    @Override
    public boolean canService(String nodeType) {
        return NODE_TYPE.equals(nodeType);
    }

    private StatusUpdater<Node, String, StatusUpdater<ValueFactory, Node, String>> activeStatusUpdater = (item, environment, statusUpdater) -> {
        log.debug("Marking item {} as active on environment {}", item, environment);
        try {
            Session session = item.getSession();
            ValueFactory valueFactory = session.getValueFactory();
            boolean result = statusUpdater.updateStatus(valueFactory, item, environment);
            session.save();
            return result;
        } catch (javax.jcr.RepositoryException e) {
            log.error("Failed to mark as activated {} ", ExceptionUtils.getFullStackTrace(e));
            return unPublish(item, environment);
        }
    };

    private StatusUpdater<ValueFactory, Node, String> addEnvironmentCallback = (vf, item, environment) -> {
        try {
            if (item.hasProperty(ACTIVE_STATUS_FIELD)) {
                Property p = item.getProperty(ACTIVE_STATUS_FIELD);
                Set<Value> values = Sets.newHashSet(p.getValues());
                values.add(vf.createValue(environment));
                p.setValue(values.toArray(new Value[values.size()]));
            } else {
                Value[] values = new Value[]{vf.createValue(environment)};
                item.setProperty(ACTIVE_STATUS_FIELD, values);
            }
        } catch (RepositoryException e) {
            log.error("Failed to update JCR {} ", ExceptionUtils.getFullStackTrace(e));
        }

        return true;
    };

    private StatusUpdater<ValueFactory, Node, String> removeEnvironmentCallback = (vf, item, environment) -> {
        try {
            if (item.hasProperty(ACTIVE_STATUS_FIELD)) {
                Property p = item.getProperty(ACTIVE_STATUS_FIELD);
                Set<Value> values = Sets.newHashSet(p.getValues());
                values.remove(vf.createValue(environment));
                p.setValue(values.toArray(new Value[values.size()]));
            }
        } catch (RepositoryException e) {
            log.error("Failed to update JCR {} ", ExceptionUtils.getFullStackTrace(e));
            return false;
        }

        return true;
    };



    private Map<String, HealthGuidesApiClient> buildApiClients(Map<String, RemoteServerResourceConfig> environmentsMap) {

        return environmentsMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> {
                            RemoteServerResourceConfig config = entry.getValue();
                            BasicAuthCredentials basicAuthCredentials = new BasicAuthCredentials(entry.getValue().getUsername(), config.getPassword());
                            ServerInfo serverInfo = ServerInfo.builder().protocol(config.getHostProtocol())
                                    .hostName(config.getHostAddress())
                                    .port(config.getHostPort())
                                    .basePath("/health-guides")
                                    .build();
                            return new HealthGuidesApiClient(basicAuthCredentials, serverInfo);
                        }));
    }
}
