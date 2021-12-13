package com.centram.common.vo;

import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class WorkingDay implements Comparable<WorkingDay> {
    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public WorkingDay(LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.startTime = LocalDateTime.of(date, startTime);
        this.endTime = LocalDateTime.of(date, endTime);
        this.date = date;
    }

    @Override
    public int compareTo(@NotNull WorkingDay workingDay) {
        return this.getDate().compareTo(workingDay.getDate());
    }
}
