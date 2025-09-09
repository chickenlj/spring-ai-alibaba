# AgentScope for Java

Introduction for AgentScope Java.

## ✨ Why AgentScope?

Easy for beginners, powerful for experts.

- **Transparent to Developers**: Transparent is our **FIRST principle**. Prompt engineering, API invocation, agent building, workflow orchestration, all are visible and controllable for developers. No deep encapsulation or implicit magic.
- **Model Agnostic**: Programming once, run with all models. More than **17+** LLM API providers are supported.
- **LEGO-style Agent Building**: All components are **modular** and **independent**. Use them or not, your choice.
- **Multi-Agent Oriented**: Designed for **multi-agent**, **explicit** message passing and workflow orchestration, NO deep encapsulation.
- **Native Distribution/Parallelization**: Centralized programming for distributed application, and **automatic parallelization**.
- **Highly Customizable**: Tools, prompt, agent, workflow, third-party libs & visualization, customization is encouraged everywhere.
- **Developer-friendly**: Low-code development, visual tracing & monitoring. From developing to deployment, all in one place.

## 📢 News
- **[2025-07-01]** A new version AgentScope is under development. In this new version, AgentScope will be more powerful and flexible, with a new architecture and more features. Refer to our [Roadmap](https://github.com/modelscope/agentscope/blob/main/docs/ROADMAP.md) for more details!
- **[2025-04-27]** A new 💻 AgentScope Studio is online now. Refer [here](https://doc.agentscope.io/build_tutorial/visual.html) for more details.
- **[2025-03-21]** AgentScope supports hooks functions now. Refer to our [tutorial](https://doc.agentscope.io/build_tutorial/hook.html) for more details.
- **[2025-03-19]** AgentScope supports 🔧 tools API now. Refer to our [tutorial](https://doc.agentscope.io/build_tutorial/tool.html).
- **[2025-03-20]** Agentscope now supports [MCP Server](https://github.com/modelcontextprotocol/servers)! You can learn how to use it by following this [tutorial](https://doc.agentscope.io/build_tutorial/MCP.html).
- **[2025-03-05]** Our [🎓 AgentScope Copilot](applications/multisource_rag_app/README.md), a multi-source RAG application is open-source now!
- **[2025-02-24]** [🇨🇳 Chinese version tutorial](https://doc.agentscope.io/zh_CN) is online now!
- **[2025-02-13]** We have released the [📁 technical report](https://doc.agentscope.io/tutorial/swe.html) of our solution in [SWE-Bench(Verified)](https://www.swebench.com/)!
- **[2025-02-07]** 🎉🎉 AgentScope has achieved a **63.4% resolve rate** in [SWE-Bench(Verified)](https://www.swebench.com/).
- **[2025-01-04]** AgentScope supports Anthropic API now.

👉👉 [**Older News**](https://github.com/modelscope/agentscope/blob/main/docs/news_en.md)

## 💬 Contact

Welcome to join our community on

| [Discord](https://discord.gg/eYMpfnkG8h)                                                                                         | DingTalk                                                                                                                          |
|----------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| <img src="https://gw.alicdn.com/imgextra/i1/O1CN01hhD1mu1Dd3BWVUvxN_!!6000000000238-2-tps-400-400.png" width="100" height="100"> | <img src="https://img.alicdn.com/imgextra/i1/O1CN01LxzZha1thpIN2cc2E_!!6000000005934-2-tps-497-477.png" width="100" height="100"> |


<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
## 📑 Table of Contents

- [🚀 Quickstart](#-quickstart)
  - [💻 Installation](#-installation)
    - [🛠️ From source](#-from-source)
    - [📦 From PyPi](#-from-pypi)
- [📝 Example](#-example)
  - [👋 Hello AgentScope](#-hello-agentscope)
  - [🧑‍🤝‍🧑 Multi-Agent Conversation](#-multi-agent-conversation)
  - [💡 Reasoning with Tools & MCP](#-reasoning-with-tools--mcp)
  - [🔠 Structured Output](#-structured-output)
  - [✏️ Workflow Orchestration](#-workflow-orchestration)
  - [⚡️ Distribution and Parallelization](#%EF%B8%8F-distribution-and-parallelization)
  - [👀 Tracing & Monitoring](#-tracing--monitoring)
- [⚖️ License](#-license)
- [📚 Publications](#-publications)
- [✨ Contributors](#-contributors)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## 🚀 Quickstart

Creating a basic conversation **explicitly** between **a user** and **an assistant** with AgentScope:

```java
ReactAgent writerAgent = ReactAgent.builder()
	.name("writer_agent")
	.model(chatModel)
	.description("可以写文章。")
	.instruction("你是一个知名的作家，擅长写作和创作。请根据用户的提问进行回答。")
	.outputKey("article")
	.build();
```

### 🧑‍🤝‍🧑 Multi-Agent Conversation

AgentScope is designed for **multi-agent** applications, offering flexible control over information flow and communication between agents.

![](https://img.shields.io/badge/✨_Feature-Transparent-green)
![](https://img.shields.io/badge/✨_Feature-Multi--Agent-purple)

```java
ReactAgent writerAgent = ReactAgent.builder()
	.name("writer_agent")
	.model(chatModel)
	.description("可以写文章。")
	.instruction("你是一个知名的作家，擅长写作和创作。请根据用户的提问进行回答。")
	.outputKey("article")
	.build();

ReactAgent reviewerAgent = ReactAgent.builder()
	.name("reviewer_agent")
	.model(chatModel)
	.description("可以对文章进行评论和修改。")
	.instruction("你是一个知名的评论家，擅长对文章进行评论和修改。对于散文类文章，请确保文章中必须包含对于西湖风景的描述。")
	.outputKey("reviewed_article")
	.build();

SequentialAgent blogAgent = SequentialAgent.builder()
	.name("blog_agent")
	.state(stateFactory)
	.description("可以根据用户给定的主题写一篇文章，然后将文章交给评论员进行评论，必要时做出修改。")
	.inputKey("input")
	.outputKey("reviewed_article")
	.subAgents(List.of(writerAgent, reviewerAgent))
	.build();
```

### 💡 Reasoning with Tools & MCP


### 🔠 Structured Output


### ✏️ Workflow Orchestration



### ⚡️ Distribution and Parallelization


### 👀 Tracing & Monitoring


## ⚖️ License

AgentScope is released under Apache License 2.0.
