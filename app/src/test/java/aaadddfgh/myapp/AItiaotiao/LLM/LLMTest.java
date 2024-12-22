package aaadddfgh.myapp.AItiaotiao.LLM;

import org.junit.Test;

public class LLMTest {
    @Test
    public void test(){

        TextSummarizer text = new TextSummarizer("http://192.168.1.62:11434/v1");
        System.err.printf(text.summarize("Say it is a test"));
    }
}
