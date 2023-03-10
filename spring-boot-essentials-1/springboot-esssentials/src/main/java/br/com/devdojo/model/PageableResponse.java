package br.com.devdojo.model;

import br.com.devdojo.util.CustomSortDeserializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PageableResponse<T> extends PageImpl<T> {

    private boolean last;
    private boolean first;
    private int totalPages;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public PageableResponse(@JsonProperty("content") List<T> content,
                            @JsonProperty("pageable") JsonNode pageable,
                            @JsonProperty("number") int page,
                            @JsonProperty("size") int size,
                            @JsonProperty("totalElements") long totalElements,
                            @JsonProperty("sort") JsonNode sort) {
        super(content, PageRequest.of(page, size), totalElements);
    }

    public PageableResponse(List<T> content) {
        super(content);
    }

    public PageableResponse(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public PageableResponse() {
        super(new ArrayList<>());
    }

    @Override
    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    @Override
    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    @Override
    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}