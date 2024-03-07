package ru.clevertec.comment.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Getter
@Setter
public class ResponsePage<T> {

    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;

    public ResponsePage(Page<T> page) {
        this.content = page.getContent();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
    }
}