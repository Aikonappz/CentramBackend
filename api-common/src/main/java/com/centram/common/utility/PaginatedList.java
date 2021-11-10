package com.centram.common.utility;

import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

public class PaginatedList<T> implements Serializable {
    private static final long serialVersionUID = 1509919069915516041L;
    private long totalElements;
    private int numberOfElements;
    private int totalPages;
    private long offset;
    private int pageNumber;
    private int pageSize;
    private List<T> content;

    public PaginatedList() {
    }

    public PaginatedList(Page<T> page) {
        this.totalElements = page.getTotalElements();
        this.numberOfElements = page.getNumberOfElements();
        this.totalPages = page.getTotalPages();
        this.offset = page.getPageable().getOffset();
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