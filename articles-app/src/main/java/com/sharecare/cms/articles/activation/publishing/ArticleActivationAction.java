package com.sharecare.cms.articles.activation.publishing;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.List;

import com.sharecare.cms.articles.activation.remote.RemoteArticleRequestBuilder;
import com.sharecare.cms.articles.configuration.ArticlesModuleConfig;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Link;
import info.magnolia.commands.CommandsManager;
import info.magnolia.context.Context;
import info.magnolia.context.MgnlContext;
import info.magnolia.event.EventBus;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.api.event.ContentChangedEvent;
import info.magnolia.ui.framework.action.ActivationAction;
import info.magnolia.ui.framework.overlay.ViewAdapter;
import info.magnolia.ui.vaadin.integration.jcr.JcrItemAdapter;
import info.magnolia.ui.vaadin.integration.jcr.JcrItemId;
import info.magnolia.ui.vaadin.overlay.MessageStyleTypeEnum;

public class ArticleActivationAction extends ActivationAction<ArticleActivationActionDefinition> {

	private final Item jcrItem;
	private final EventBus admincentralEventBus;
	private final SubAppContext uiContext;
	private final JcrItemId changedItemId;

	@Inject
	private ArticlesModuleConfig articlesModuleConfig;

	public ArticleActivationAction(ArticleActivationActionDefinition definition, List<JcrItemAdapter> items, CommandsManager commandsManager, @Named("admincentral") EventBus admincentralEventBus, SubAppContext uiContext, SimpleTranslator i18n) {
		super(definition, items, commandsManager, admincentralEventBus, uiContext, i18n);
		this.admincentralEventBus = admincentralEventBus;
		this.uiContext = uiContext;
		this.changedItemId = items.isEmpty() ? null : items.get(0).getItemId();
		this.jcrItem = items.isEmpty() ? null : items.get(0).getJcrItem();
	}

	@Override
	protected void onPostExecute() throws Exception {
		admincentralEventBus.fireEvent(new ContentChangedEvent(changedItemId));

		Context context = MgnlContext.getInstance();
		// yes, this is inverted, because a chain returns false when it is finished.
		boolean success = !(Boolean) context.getAttribute(COMMAND_RESULT);
		String articleLink = getMessage(success);

		String host = articlesModuleConfig.getWebHost().get(articlesModuleConfig.getEnvironment());

		if (success) {
			Link link = new Link(articleLink, new ExternalResource(host + articleLink));
			link.setTargetName("_blank");
			uiContext.openNotification(MessageStyleTypeEnum.INFO, false, new ViewAdapter(link));
		} else {
			uiContext.openNotification(MessageStyleTypeEnum.ERROR, true, getFailureMessage());
		}
	}

	@Override
	protected String getSuccessMessage() {
		if (jcrItem != null && jcrItem.isNode()) {
			Node node = (Node) jcrItem;
			try {
				return RemoteArticleRequestBuilder.buildArticleUri(node);
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}

		return super.getSuccessMessage();
	}

}