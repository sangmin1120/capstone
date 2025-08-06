package smu.capstone.intrastructure.mogodb.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

//WriteConverter

@Component
@WritingConverter
public class LocalDateTimeToDateConverter implements Converter<LocalDateTime, Date> {

    public Date convert(LocalDateTime source) {
        return Date.from(source.atZone(ZoneId.of("Asia/Seoul")).toInstant());
    }
}
