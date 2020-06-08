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
package de.hhu.ba.yoshikoWrapper.core;

import java.awt.*;
import java.util.concurrent.CountDownLatch;

import de.hhu.ba.yoshikoWrapper.swing.components.MainPanel;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.presentation.RenderingEngineFactory;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.*;
import org.cytoscape.work.swing.DialogTaskManager;


/**Simple link collection to enable access to Cytoscape instances
 *
 */
public class CyCore {
	//SYMBOLIC LINKS
	public static CyApplicationManager cy;
	public static DialogTaskManager dialogTaskManager;
	public static ConfigurationManager cm;
	//public static BundleContext context;
	public static CyServiceRegistrar registrar;
	public static CyNetworkViewFactory networkViewFactory;
	public static CyNetworkFactory networkFactory;
	public static CySwingApplication swing;
	public static CyNetworkViewManager networkViewManager;
	public static CyNetworkManager networkManager;
	public static CyLayoutAlgorithmManager layoutAlgorithmManager;
	public static VisualMappingManager visualMappingManager;
	public static VisualStyleFactory visualStyleFactory;
	public static VisualMappingFunctionFactory continuousMappingFactory;
	public static CyRootNetworkManager rootNetworkManager;
	public static RenderingEngineFactory<CyNetwork> renderingEngineFactory;
	//public static CloneNetworkTaskFactory cloneNetworkTaskFactory;
	public static CommandExecutorTaskFactory commandExecutorTaskFactory;
	//

	//ugly passing of variables
	public static MainPanel mainPanel;
	public static TaskMonitor taskMonitor;
	public static Window statusWindow;

	//Convenience
//	public static String getConfig(String key) {
//		return cm.getProperties().getProperty(key);
//	}

	/**
	 * Convenience function that runs all tasks in a task iterator but blocks until all tasks are finished
	 * @param taskIterator
	 * @throws InterruptedException
	 */
	public static synchronized void runAndWait(TaskIterator taskIterator) throws InterruptedException {
		dialogTaskManager.execute(taskIterator);
	}
}
