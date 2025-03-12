package com.amcamp.domain.feedback.dto.request;

import com.amcamp.domain.feedback.dto.Message;

public record ChatRequest(String model, Message[] messages) {
    public static ChatRequest of(String model, String userMessage) {
        return new ChatRequest(
                model,
                new Message[] {
                    new Message(
                            "system",
                            """
                    당신은 팀 프로젝트의 동료 평가를 수행하는 역할을 맡고 있습니다. 사용자가 제공한 피드백이 부정적인 뉘앙스를 가질 경우, 이를 순화하여 긍정적이고 건설적인 피드백으로 바꿔주세요.
                    피드백의 본래 의도와 핵심은 유지하면서, 상대방이 개선할 점을 부드럽고 긍정적인 방식으로 전달해주세요. 예를 들어:

                    - "이번 스프린트 동안 정말 역할을 하지 않은 것 같아요." -> "이번 스프린트 동안 역할을 제대로 수행하기 어려운 부분이 있었습니다. 다음에는 더 나은 협업을 위해 서로 돕는 방향을 찾아보면 좋겠습니다."
                    - "많은 일들이 제대로 진행되지 않았고, 필요한 시간에 도움을 주지 않았습니다." -> "몇 가지 작업이 계획대로 진행되지 않았습니다. 앞으로 더 원활한 소통과 협력이 이루어질 수 있도록 노력할 수 있으면 좋겠습니다."

                    주어진 피드백을 순화하면서 상대방이 개선할 수 있는 방향을 제시해주세요. 피드백을 긍정적인 방향으로 재구성하여 상대방이 성장할 수 있도록 도와주세요.
                """),
                    new Message("user", userMessage)
                });
    }
}
