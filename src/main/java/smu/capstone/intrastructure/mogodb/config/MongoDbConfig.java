package smu.capstone.intrastructure.mogodb.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import smu.capstone.intrastructure.mogodb.util.DateToLocalDateTimeConverter;
import smu.capstone.intrastructure.mogodb.util.LocalDateTimeToDateConverter;

import java.util.List;

//해당 ""에 MongoDB 쓰는 repository 디렉터리 추가
@RequiredArgsConstructor
@Configuration
@EnableMongoRepositories(basePackages = "smu.capstone.domain.chat.repository")
public class MongoDbConfig {
    //순환 문제를 위해 삭제
    //private final MongoMappingContext mongoMappingContext;
    private final MongoProperties mongoProperties;

    //JSON 직렬화/역직렬화 및 기본 설정
    @Bean
    public MappingMongoConverter mongoConverter(MongoDatabaseFactory mongoDbFactory,
                                                MongoMappingContext mongoMappingContext,
                                                MongoCustomConversions conversions) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext); //MongoDB와의 Java 데이터 (역)직렬화
        converter.setCustomConversions(conversions);        //명시적 등록
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));      //class 값 저장 안하도록 설정

        return converter;
    }

    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(
                List.of(new DateToLocalDateTimeConverter(),
                        new LocalDateTimeToDateConverter())
        );
    }

    //설정 값 가져옴
    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(mongoProperties.getUri());
    }

    //Mongo Templete 설정 - 쿼리 커스텀
    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), mongoProperties.getDatabase());
    }
}
