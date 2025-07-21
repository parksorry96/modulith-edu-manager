package com.edumanager.shared.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 페이징 응답을 위한 공통 클래스
 * Spring Data JPA의 Page 객체를 래핑하여 클라이언트 친화적인 형태로 변환
 * 
 * @param <T> 페이징 데이터의 타입
 */
@Getter
@Builder
@RequiredArgsConstructor
public class PageResponse<T> {
    
    private final List<T> content;
    private final int pageNumber;
    private final int pageSize;
    private final long totalElements;
    private final int totalPages;
    private final boolean first;
    private final boolean last;
    private final boolean hasNext;
    private final boolean hasPrevious;
    
    /**
     * Spring Data JPA Page 객체로부터 PageResponse 생성
     * Java 21의 최신 기능 활용
     */
    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
    
    /**
     * 커스텀 페이징 정보로 PageResponse 생성
     */
    public static <T> PageResponse<T> of(List<T> content, int pageNumber, int pageSize, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        
        return PageResponse.<T>builder()
                .content(content)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(pageNumber == 0)
                .last(pageNumber == totalPages - 1)
                .hasNext(pageNumber < totalPages - 1)
                .hasPrevious(pageNumber > 0)
                .build();
    }
    
    /**
     * 빈 페이징 응답 생성
     */
    public static <T> PageResponse<T> empty() {
        return PageResponse.<T>builder()
                .content(List.of())
                .pageNumber(0)
                .pageSize(0)
                .totalElements(0)
                .totalPages(0)
                .first(true)
                .last(true)
                .hasNext(false)
                .hasPrevious(false)
                .build();
    }
}