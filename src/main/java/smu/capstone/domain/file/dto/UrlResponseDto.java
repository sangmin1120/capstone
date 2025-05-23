package smu.capstone.domain.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrlResponseDto {
    private String presignedUrl;
    private String accessUrl;
    private String key;
}
