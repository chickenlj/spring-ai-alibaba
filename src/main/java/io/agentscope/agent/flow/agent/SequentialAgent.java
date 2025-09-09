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

import io.agentscope.CompiledGraph;
import io.agentscope.NodeOutput;
import io.agentscope.OverAllState;
import io.agentscope.StateGraph;
import io.agentscope.agent.flow.builder.FlowAgentBuilder;
import io.agentscope.agent.flow.builder.FlowGraphBuilder;
import io.agentscope.agent.flow.enums.FlowAgentEnum;
import io.agentscope.async.AsyncGenerator;
import io.agentscope.exception.GraphRunnerException;
import io.agentscope.exception.GraphStateException;

import java.util.Map;
import java.util.Optional;

public class SequentialAgent extends FlowAgent {

	protected SequentialAgent(SequentialAgentBuilder builder) throws GraphStateException {
		super(builder.name, builder.description, builder.outputKey, builder.inputKey, builder.keyStrategyFactory,
				builder.compileConfig, builder.subAgents);
		this.graph = initGraph();
	}

	@Override
	public Optional<OverAllState> invoke(Map<String, Object> input) throws GraphStateException, GraphRunnerException {
		CompiledGraph compiledGraph = getAndCompileGraph();
		return compiledGraph.invoke(input);
	}

	@Override
	public AsyncGenerator<NodeOutput> stream(Map<String, Object> input)
			throws GraphStateException, GraphRunnerException {
		if (this.compiledGraph == null) {
			this.compiledGraph = getAndCompileGraph();
		}
		return this.compiledGraph.stream(input);
	}

	@Override
	protected StateGraph buildSpecificGraph(FlowGraphBuilder.FlowGraphConfig config) throws GraphStateException {
		return FlowGraphBuilder.buildGraph(FlowAgentEnum.SEQUENTIAL.getType(), config);
	}

	public static SequentialAgentBuilder builder() {
		return new SequentialAgentBuilder();
	}

	/**
	 * Builder for creating SequentialAgent instances. Extends the common FlowAgentBuilder
	 * to provide type-safe building.
	 */
	public static class SequentialAgentBuilder extends FlowAgentBuilder<SequentialAgent, SequentialAgentBuilder> {

		@Override
		protected SequentialAgentBuilder self() {
			return this;
		}

		@Override
		protected void validate() {
			super.validate();
			// Add any SequentialAgent-specific validation here if needed
		}

		@Override
		public SequentialAgent build() throws GraphStateException {
			validate();
			return new SequentialAgent(this);
		}

	}

}
