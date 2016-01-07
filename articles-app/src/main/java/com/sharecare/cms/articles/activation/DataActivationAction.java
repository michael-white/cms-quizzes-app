package com.sharecare.cms.articles.activation;

import javax.inject.Named;
import javax.jcr.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import info.magnolia.commands.CommandsManager;
import info.magnolia.context.LifeTimeJCRSessionUtil;
import info.magnolia.event.EventBus;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.framework.action.ActivationAction;
import info.magnolia.ui.vaadin.integration.jcr.JcrItemAdapter;
import info.magnolia.ui.vaadin.overlay.MessageStyleTypeEnum;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataActivationAction<D extends DataActivationActionDefinition> extends ActivationAction<D> {

	private static final Logger log = LoggerFactory.getLogger(DataActivationAction.class);

	private Session session;

	public DataActivationAction(D definition, JcrItemAdapter item, CommandsManager commandsManager, @Named("admincentral") EventBus admincentralEventBus, SubAppContext uiContext, SimpleTranslator i18n) {
		super(definition, item, commandsManager, admincentralEventBus, uiContext, i18n);
	}

	public DataActivationAction(D definition, List<JcrItemAdapter> items, CommandsManager commandsManager, @Named("admincentral") EventBus admincentralEventBus, SubAppContext uiContext, SimpleTranslator i18n) {
		super(definition, items, commandsManager, admincentralEventBus, uiContext, i18n);
	}


	//		List<ArticleRequestFactory.ArticleRequest> requests = buildArticleActivationRequests();
//
//		ContentContentActivator activator = new RestApiContentDataActivator();
//		ContentContentActivator.ActivationResult result = activator.activate(requests);
//
//		if (result.isSuccess()) {
//			log.debug("Successfull Activation of {} articles", requests.size());
//			getUiContext().openNotification(MessageStyleTypeEnum.INFO, true, getSuccessMessage());
//			markItemsAsActivated();
//		} else {
//			log.error("Failed Activation of {} articles", requests.size());
//			getUiContext().openNotification(MessageStyleTypeEnum.ERROR, false, getErrorMessage());
//		}
	}

//	private List<ArticleRequestFactory.ArticleRequest> buildArticleActivationRequests() throws ActionExecutionException {
//		List<ArticleRequestFactory.ArticleRequest> requests = new ArrayList<>();
//
//		for (JcrItemAdapter item : getSortedItems(getItemComparator())) {
//			try {
//				if (item.isNode()) {
////					Node node = item.getJcrItem().getSession().getNodeByIdentifier(item.getItemId().getUuid());
//					requests.add(ArticleRequestFactory.instance((Node)item.getJcrItem()));
//				}
//			} catch (Exception ex) {
//				log.error(ExceptionUtils.getStackTrace(ex));
//				throw new ActionExecutionException(ex.getMessage());
//			}
//		}
//		return requests;
//	}

//
//	private void markItemsAsActivated() {
//
//		String activationEnvironment = getDefinition().getEnvironment();
//
//		for (JcrItemAdapter item : getItems()) {
//
//			if (item.isNode()) {
//				log.debug("Marking item {} as ective on environment {}", item.getItemId(), activationEnvironment);
//				try {
//					Session session = item.getJcrItem().getSession();
//					ValueFactory valueFactory = session.getValueFactory();
//					Node node = (Node)item.getJcrItem();
//
//					if (node.hasProperty("active-status")) {
//						Property p = node.getProperty("active-status");
//						Value[] values = Arrays.copyOf(p.getValues(), p.getValues().length + 1);
//						values[p.getValues().length] = valueFactory.createValue(activationEnvironment);
//						p.setValue(values);
//
//					} else {
//						Value[] values = new Value[] { valueFactory.createValue(activationEnvironment) };
//						node.setProperty("active-status", values);
//					}
//
//					session.save();
//
//				} catch (RepositoryException e) {
//					// TODO revert the sync
//					log.error("Failed Activation of article  {} ", item.getItemId().getUuid());
//					getUiContext().openNotification(MessageStyleTypeEnum.ERROR, false, getErrorMessage());
//				}
//			}
//		}
//	}
//
//	public Session getSession(String workspaceName) throws RepositoryException {
//		return LifeTimeJCRSessionUtil.getSession(workspaceName);
//	}
//
//	@Override
//	protected String getSuccessMessage() {
//		return "Successfully activated " + getItems().size();
//	}
//
//	@Override
//	protected String getErrorMessage() {
//		return "Activation failed";
//	}