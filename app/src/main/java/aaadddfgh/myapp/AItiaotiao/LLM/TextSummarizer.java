package aaadddfgh.myapp.AItiaotiao.LLM;

import com.openai.client.OpenAIClient;
import com.openai.client.OpenAIClientImpl;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.ClientOptions;
import com.openai.models.ChatCompletion;
import com.openai.models.ChatCompletionCreateParams;
import com.openai.models.ChatCompletionMessageParam;
import com.openai.models.ChatCompletionUserMessageParam;

import java.util.List;
import java.util.function.Consumer;
//
//



public class TextSummarizer {

    private OpenAIClient ollamaAPI;

    public TextSummarizer(String host) {
        this.ollamaAPI = OpenAIOkHttpClient.builder()
                .baseUrl(host)
                .maxRetries(2)
                .apiKey("ollama")
                .build();
    }

    /**
     * 对输入文本进行总结
     *
     * @param text 要总结的文本
     * @return 总结结果
     */
    public String summarize(String text) {
//        // 构建对话请求，包含系统提示和用户输入
//

            ChatCompletion chat = ollamaAPI.chat().completions().create(ChatCompletionCreateParams.builder()
                    .model("qwen2.5")
                    .messages(List.of(ChatCompletionMessageParam.ofChatCompletionUserMessageParam(ChatCompletionUserMessageParam.builder()
                            .role(ChatCompletionUserMessageParam.Role.USER)
                            .content(ChatCompletionUserMessageParam.Content.ofTextContent(text))
                            .build())))

                    .build()

            );
            return chat.choices().get(0).message().content().get();

    }

    public static void main(String[] args) {
        String host = "http://localhost:11434/";
        TextSummarizer summarizer = new TextSummarizer(host);

        String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.";

        String summary = summarizer.summarize(text);
        System.out.println("总结结果: " + summary);
    }
}
