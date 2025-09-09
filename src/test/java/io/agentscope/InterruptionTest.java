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
package io.agentscope;

import io.agentscope.action.AsyncNodeActionWithConfig;
import io.agentscope.checkpoint.config.SaverConfig;
import io.agentscope.checkpoint.constant.SaverEnum;
import io.agentscope.checkpoint.savers.MemorySaver;
import io.agentscope.utils.EdgeMappings;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.agentscope.StateGraph.END;
import static io.agentscope.StateGraph.START;
import static io.agentscope.action.AsyncEdgeAction.edge_async;
import static io.agentscope.action.AsyncNodeActionWithConfig.node_async;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class InterruptionTest {

	private AsyncNodeActionWithConfig _nodeAction(String id) {
		return node_async((state, config) -> Map.of("messages", id));
	}

	@Test
	public void interruptAfterEdgeEvaluation() throws Exception {
		var saver = new MemorySaver();
		KeyStrategyFactory keyStrategyFactory = new KeyStrategyFactoryBuilder().defaultStrategy(KeyStrategy.REPLACE)
			.addStrategy("messages")
			.build();
		var workflow = new StateGraph(keyStrategyFactory).addNode("A", _nodeAction("A"))
			.addNode("B", _nodeAction("B"))
			.addNode("C", _nodeAction("C"))
			.addNode("D", _nodeAction("D"))
			.addConditionalEdges("B", edge_async(state -> {
				var message = state.value("messages").orElse(END);
				return message.equals("B") ? "D" : message.toString();
			}), EdgeMappings.builder().to("A").to("C").to("D").toEND().build())
			.addEdge(START, "A")
			.addEdge("A", "B")
			.addEdge("C", END)
			.addEdge("D", END)
			.compile(CompileConfig.builder()
				.saverConfig(SaverConfig.builder().register(SaverEnum.MEMORY.getValue(), saver).build())
				.interruptAfter("B")
				.build());

		var runnableConfig = RunnableConfig.builder().build();

		var results = workflow.stream(Map.of(), runnableConfig)
			.stream()
			.peek(System.out::println)
			.map(NodeOutput::node)
			.toList();

		assertIterableEquals(List.of(START, "A", "B"), results);

		results = workflow.stream(null, runnableConfig)
			.stream()
			.peek(System.out::println)
			.map(NodeOutput::node)
			.toList();
		assertIterableEquals(List.of("D", END), results);

		var snapshotForNodeB = workflow.getStateHistory(runnableConfig)
			.stream()
			.filter(s -> s.node().equals("B"))
			.findFirst()
			.orElseThrow();

		runnableConfig = workflow.updateState(snapshotForNodeB.config(), Map.of("messages", "C"));

		results = workflow.stream(null, runnableConfig)
			.stream()
			.peek(System.out::println)
			.map(NodeOutput::node)
			.toList();
		assertIterableEquals(List.of("D", END), results);
	}

	@Test
	public void interruptBeforeEdgeEvaluation() throws Exception {

		var saver = new MemorySaver();
		KeyStrategyFactory keyStrategyFactory = new KeyStrategyFactoryBuilder().defaultStrategy(KeyStrategy.REPLACE)
			.addStrategy("messages")
			.build();
		var workflow = new StateGraph(keyStrategyFactory).addNode("A", _nodeAction("A"))
			.addNode("B", _nodeAction("B"))
			.addNode("C", _nodeAction("C"))
			.addConditionalEdges("B", edge_async(state -> state.value("messages").orElse(END).toString()),
					EdgeMappings.builder().to("A").to("C").toEND().build())
			.addEdge(START, "A")
			.addEdge("A", "B")
			.addEdge("C", END)
			.compile(CompileConfig.builder()
				.saverConfig(SaverConfig.builder().register(SaverEnum.MEMORY.getValue(), saver).build())
				.interruptAfter("B")
				.interruptBeforeEdge(true)
				.build());

		var runnableConfig = RunnableConfig.builder().build();

		var results = workflow.stream(Map.of(), runnableConfig)
			.stream()
			.peek(System.out::println)
			.map(NodeOutput::node)
			.toList();

		assertIterableEquals(List.of(START, "A", "B"), results);

		runnableConfig = workflow.updateState(runnableConfig, Map.of("messages", "C"));
		results = workflow.stream(null, runnableConfig)
			.stream()
			.peek(System.out::println)
			.map(NodeOutput::node)
			.toList();
		assertIterableEquals(List.of("C", END), results);
	}

}
