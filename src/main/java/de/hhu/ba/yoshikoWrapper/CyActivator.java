/*******************************************************************************
 * Copyright (C) 2017 Philipp Spohr
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package de.hhu.ba.yoshikoWrapper;

import java.util.Properties;

import de.hhu.ba.yoshikoWrapper.tasks.AlgorithmTask;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.presentation.RenderingEngineFactory;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.swing.DialogTaskManager;
import org.osgi.framework.BundleContext;


import static org.cytoscape.work.ServiceProperties.COMMAND;
import static org.cytoscape.work.ServiceProperties.COMMAND_DESCRIPTION;
import static org.cytoscape.work.ServiceProperties.COMMAND_NAMESPACE;

import de.hhu.ba.yoshikoWrapper.core.ConfigurationManager;
import de.hhu.ba.yoshikoWrapper.core.CyCore;
import de.hhu.ba.yoshikoWrapper.core.LocalizationManager;
import de.hhu.ba.yoshikoWrapper.swing.components.MainPanel;
import de.hhu.ba.yoshikoWrapper.taskFactories.CommandTaskFactory;
import de.hhu.ba.yoshikoWrapper.taskFactories.YoshikoCommand;

/**
 * Entry point for the application as required by the Cytoscape API
 * Registers services and sets up resources for Yoshiko
 */
public class CyActivator extends AbstractCyActivator {

	public CyActivator() {
		super();
	}

	@Override
	public void start(BundleContext context) throws Exception {
		//Initialize Cytoscape configuration system
		//This is responsible for saving/loading configuration
		ConfigurationManager cm = new ConfigurationManager("yoshikoWrapper", "yoshiko.props");
		Properties propsReaderServiceProps = new Properties();
		propsReaderServiceProps.setProperty("cyPropertyName", "yoshiko.props");
		registerAllServices(context,cm,propsReaderServiceProps);

		//Create symbolic links to give other classes access to Cytoscape Services
		CyCore.cy = getService(context, CyApplicationManager.class);
		CyCore.dialogTaskManager = getService(context, DialogTaskManager.class);
		CyCore.registrar = getService(context, CyServiceRegistrar.class);
		CyCore.cm = cm;
		CyCore.networkViewFactory = getService(context, CyNetworkViewFactory.class);
		CyCore.networkFactory = getService(context, CyNetworkFactory.class);
		CyCore.swing = getService(context, CySwingApplication.class);
		CyCore.networkViewManager  = getService(context,CyNetworkViewManager.class);
		CyCore.networkManager = getService(context,CyNetworkManager.class);
		CyCore.layoutAlgorithmManager = getService(context,CyLayoutAlgorithmManager.class);
		CyCore.visualMappingManager = getService(context,VisualMappingManager.class);
		CyCore.visualStyleFactory = getService(context,VisualStyleFactory.class);
		CyCore.continuousMappingFactory = getService(context,VisualMappingFunctionFactory.class, "(mapping.type=continuous)");
		CyCore.rootNetworkManager = getService(context,CyRootNetworkManager.class);
		//TODO: Not sure how to correctly infer arguments here
		CyCore.renderingEngineFactory = getService(context,RenderingEngineFactory.class);
		//CyCore.cloneNetworkTaskFactory = getService(context,CloneNetworkTaskFactory.class);
		CyCore.commandExecutorTaskFactory = getService(context,CommandExecutorTaskFactory.class);

		//Store a reference to the Version for easier access
		//YoshUtil.version = context.getBundle().getVersion();

		//Set language according to settings -> Default to enUS
		LocalizationManager.switchLanguage(cm.getProperties().getProperty("locale", "enUS"));


		//Register commands / CyRest //TODO: Group shared calls for better code readability / less lines of code
		TaskFactory commandTaskFactory_PERFORM_ALGORITHM = new CommandTaskFactory(YoshikoCommand.PERFORM_ALGORITHM);
		Properties props_PERFORM_ALGORITHM = new Properties();
		props_PERFORM_ALGORITHM.setProperty(COMMAND_NAMESPACE, "yoshiko");
		props_PERFORM_ALGORITHM.setProperty(COMMAND, YoshikoCommand.PERFORM_ALGORITHM.toString());
		props_PERFORM_ALGORITHM.setProperty(COMMAND_DESCRIPTION,"Cluster a network with the Yoshiko algorithm");
		registerService(context, commandTaskFactory_PERFORM_ALGORITHM, TaskFactory.class, props_PERFORM_ALGORITHM);

		TaskFactory commandTaskFactory_GET_SOLUTIONS = new CommandTaskFactory(YoshikoCommand.GET_SOLUTIONS);
		Properties props_GET_SOLUTIONS = new Properties();
		props_GET_SOLUTIONS.setProperty(COMMAND_NAMESPACE, "yoshiko");
		props_GET_SOLUTIONS.setProperty(COMMAND, YoshikoCommand.GET_SOLUTIONS.toString());
		props_GET_SOLUTIONS.setProperty(COMMAND_DESCRIPTION,"Retrieve solutions associated with a result");
		registerService(context, commandTaskFactory_GET_SOLUTIONS, TaskFactory.class, props_GET_SOLUTIONS);

		TaskFactory commandTaskFactory_GET_CLUSTERS = new CommandTaskFactory(YoshikoCommand.GET_CLUSTERS);
		Properties props_GET_CLUSTERS = new Properties();
		props_GET_CLUSTERS.setProperty(COMMAND_NAMESPACE, "yoshiko");
		props_GET_CLUSTERS.setProperty(COMMAND, YoshikoCommand.GET_CLUSTERS.toString());
		props_GET_CLUSTERS.setProperty(COMMAND_DESCRIPTION,"Retrieve clusters associated with a solution");
		registerService(context, commandTaskFactory_GET_CLUSTERS, TaskFactory.class, props_GET_CLUSTERS);

		//Initialize and register main panel
		MainPanel mainPanel = new MainPanel();
		registerService(context,mainPanel,CytoPanelComponent.class, new Properties());

		CyCore.mainPanel = mainPanel;

	}
}