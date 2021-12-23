package com.centram.common.utility;

import com.centram.common.view.Views;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PaginatedList<T> implements Serializable {
    private static final long serialVersionUID = 1509919069915516041L;
    @JsonView({Views.BasicView.class, Views.ListView.class, Views.DetailView.class, Views.InternalView.class})
    private long totalElements;
    @JsonView({Views.BasicView.class, Views.ListView.class, Views.DetailView.class, Views.InternalView.class})
    private int numberOfElements;
    @JsonView({Views.BasicView.class, Views.ListView.class, Views.DetailView.class, Views.InternalView.class})
    private int totalPages;
    @JsonView({Views.BasicView.class, Views.ListView.class, Views.DetailView.class, Views.InternalView.class})
    private long offset;
    @JsonView({Views.BasicView.class, Views.ListView.class, Views.DetailView.class, Views.InternalView.class})
    private int pageNumber;
    @JsonView({Views.BasicView.class, Views.ListView.class, Views.DetailView.class, Views.InternalView.class})
    private int pageSize;
    @JsonView({Views.BasicView.class, Views.ListView.class, Views.DetailView.class, Views.InternalView.class})
    private List<T> content;

    public PaginatedList() {
    }

    public PaginatedList(Page<T> page) {
        this.totalElements = page.getTotalElements();
        this.numberOfElements = page.getNumberOfElements();
        this.totalPages = page.getTotalPages();
        this.offset = page.getPageable().isPaged() ? page.getPageable().getOffset() : 0;
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.content = page.getContent();
    }

    /**
     * @param totalElements
     * @param numberOfElements
     * @param totalPages
     * @param offset
     * @param pageNumber
     * @param pageSize
     * @param content
     */
    public PaginatedList(long totalElements, int numberOfElements, int totalPages, long offset, int pageNumber,
                         int pageSize, List<T> content) {
        this.totalElements = totalElements;
        this.numberOfElements = numberOfElements;
        this.totalPages = totalPages;
        this.offset = offset;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.content = content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

}