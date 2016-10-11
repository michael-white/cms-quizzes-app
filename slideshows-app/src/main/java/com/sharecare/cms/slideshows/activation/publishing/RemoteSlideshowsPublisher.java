package com.sharecare.cms.slideshows.activation.publishing;

import com.google.common.collect.Sets;
import com.sharecare.cms.publishing.commons.activation.RemoteDataPublisher;
import com.sharecare.cms.publishing.commons.configuration.CommonsModuleConfig;
import com.sharecare.cms.slideshows.configuration.SlideshowsModuleConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.inject.Inject;
import javax.jcr.*;
import java.util.Set;

import static com.sharecare.cms.publishing.commons.ui.taglib.activation.EnvironmentActivationField.ACTIVE_STATUS_FIELD;

@Slf4j
class RemoteSlideshowsPublisher implements RemoteDataPublisher {

	static final String NODE_TYPE = "mgnl:article";


	@Inject
	public RemoteSlideshowsPublisher(SlideshowsModuleConfig slideshowsModuleConfig,
                                     CommonsModuleConfig commonsModuleConfigr) {
	}

	@Override
	public boolean publish(Node node, String environment) {



		return true;
	}

	@Override
	public boolean unPublish(Node node, String environment) {


		return true;
	}


	private interface StatusUpdater<V, I, S> {

		boolean updateStatus(V valueFactory, I item, S environment);
	}

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


	private StatusUpdater<Node, String, StatusUpdater<ValueFactory, Node, String>> activeStatusUpdater = (item, environment, statusUpdater) -> {
		log.debug("Marking item {} as active on environment {}", item, environment);
		try {
			Session session = item.getSession();
			ValueFactory valueFactory = session.getValueFactory();
			boolean result = statusUpdater.updateStatus(valueFactory, item, environment);
			session.save();
			return result;
		} catch (RepositoryException e) {
			log.error("Failed to mark as activated {} ", ExceptionUtils.getFullStackTrace(e));
			return unPublish(item, environment);
		}
	};


	@Override
	public boolean canService(String nodeType) {
		return NODE_TYPE.equals(nodeType);
	}
}
