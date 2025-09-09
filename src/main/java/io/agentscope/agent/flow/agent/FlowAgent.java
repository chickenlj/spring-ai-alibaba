/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.agentscope.agent.flow.agent;

import java.util.List;

import io.agentscope.CompileConfig;
import io.agentscope.CompiledGraph;
import io.agentscope.KeyStrategyFactory;
import io.agentscope.StateGraph;
import io.agentscope.action.AsyncNodeAction;
import io.agentscope.agent.BaseAgent;
import io.agentscope.agent.ReactAgent;
import io.agentscope.agent.flow.builder.FlowGraphBuilder;
import io.agentscope.exception.GraphStateException;
import io.agentscope.scheduling.ScheduleConfig;
import io.agentscope.scheduling.ScheduledAgentTask;

import static io.agentscope.action.AsyncNodeAction.node_async;

public abstract class FlowAgent extends BaseAgent {

	protected CompileConfig compileConfig;

	protected String inputKey;

	protected KeyStrategyFactory keyStrategyFactory;

	protected List<BaseAgent> subAgents;

	protected StateGraph graph;

	protected CompiledGraph compiledGraph;

	protected FlowAgent(String name, String description, String outputKey, String inputKey,
			KeyStrategyFactory keyStrategyFactory, CompileConfig compileConfig, List<BaseAgent> subAgents)
			throws GraphStateException {
		super(name, description, outputKey);
		this.compileConfig = compileConfig;
		this.inputKey = inputKey;
		this.keyStrategyFactory = keyStrategyFactory;
		this.subAgents = subAgents;
	}

	protected StateGraph initGraph() throws GraphStateException {
		// Use FlowGraphBuilder to construct the graph
		FlowGraphBuilder.FlowGraphConfig config = FlowGraphBuilder.FlowGraphConfig.builder()
			.name(this.name())
			.keyStrategyFactory(keyStrategyFactory)
			.rootAgent(this)
			.subAgents(this.subAgents());

		// Delegate to specific graph builder based on agent type
		return buildSpecificGraph(config);
	}

	/**
	 * Abstract method for subclasses to specify their graph building strategy. This
	 * method should be implemented by concrete FlowAgent subclasses to define how their
	 * specific graph structure should be built.
	 * @param config the graph configuration
	 * @return the constructed StateGraph
	 * @throws GraphStateException if graph construction fails
	 */
	protected abstract StateGraph buildSpecificGraph(FlowGraphBuilder.FlowGraphConfig config)
			throws GraphStateException;

	@Override
	public AsyncNodeAction asAsyncNodeAction(String inputKeyFromParent, String outputKeyToParent)
			throws GraphStateException {
		if (this.compiledGraph == null) {
			this.compiledGraph = getAndCompileGraph();
		}
		return node_async(
				new ReactAgent.SubGraphStreamingNodeAdapter(inputKeyFromParent, outputKeyToParent, this.compiledGraph));
	}

	@Override
	public ScheduledAgentTask schedule(ScheduleConfig scheduleConfig) throws GraphStateException {
		CompiledGraph compiledGraph = getAndCompileGraph();
		return compiledGraph.schedule(scheduleConfig);
	}

	public CompiledGraph getAndCompileGraph() throws GraphStateException {
		if (this.compileConfig == null) {
			this.compiledGraph = graph.compile();
		}
		else {
			this.compiledGraph = graph.compile(this.compileConfig);
		}
		return this.compiledGraph;
	}

	public CompileConfig compileConfig() {
		return compileConfig;
	}

	public String inputKey() {
		return inputKey;
	}

	public KeyStrategyFactory keyStrategyFactory() {
		return keyStrategyFactory;
	}

	public List<BaseAgent> subAgents() {
		return this.subAgents;
	}

}
