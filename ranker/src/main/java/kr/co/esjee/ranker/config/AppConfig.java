package kr.co.esjee.ranker.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

@Configuration
public class AppConfig {

	private static List<String> stopwords;

	@Bean
	public static List<String> getStopwords() throws IOException {
		if (stopwords == null) {
			Resource resource = new ClassPathResource("stopwords.properties");

			byte[] data = FileCopyUtils.copyToByteArray(resource.getInputStream());
			String source = new String(data, StandardCharsets.UTF_8);

			stopwords = Arrays.asList(source.split("\r\n"))
					.stream()
					.filter(x -> !x.isEmpty())
					.collect(Collectors.toList());
		}

		return stopwords;
	}

}
