package bootcamp.kakao.server.common.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.TreeMap;

@Configuration
public class SwaggerConfig {

    @Value("${cors.back.host}")
    private String backHost;

    @Bean
    public OpenAPI openAPI() {

        /// 개발 환경 추가
        io.swagger.v3.oas.models.servers.Server server = new io.swagger.v3.oas.models.servers.Server()
                .url(backHost)
                .description("ZeusAI 백엔드 스웨거");

        return new OpenAPI()
                .info(apiInfo())
                .addServersItem(server);
    }

    private Info apiInfo() {
        return new Info()
                .title("KTB 3기 해커톤 ZeusAI Swagger")
                .description("ZeusAI 백엔드 스웨거")
                .version("1.0.0");
    }

    /// 스키마 이름 기준 오름차순
    @Bean
    public OpenApiCustomizer sortSchemasAlphabetically() {
        return openApi -> {
            Map<String, Schema> schemas = openApi.getComponents().getSchemas();
            openApi.getComponents().setSchemas(new TreeMap<>(schemas));
        };
    }
}
