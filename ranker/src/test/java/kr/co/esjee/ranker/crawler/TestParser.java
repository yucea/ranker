package kr.co.esjee.ranker.crawler;

import kr.co.esjee.ranker.hwp.HwpTextExtractor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

@Slf4j
public class TestParser {

    @Test
    public void testParser () throws IOException {
        File hwp = new File("D://hangul.hwp"); // 텍스트를 추출할 HWP 파일
        Writer writer = new StringWriter(); // 추출된 텍스트를 출력할 버퍼
        HwpTextExtractor.extract(hwp, writer); // 파일로부터 텍스트 추출
        String text = writer.toString(); // 추출된 텍스트

        System.out.println(text);
    }
}
