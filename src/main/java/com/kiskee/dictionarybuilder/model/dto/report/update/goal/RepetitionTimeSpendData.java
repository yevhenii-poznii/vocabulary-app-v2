package com.kiskee.dictionarybuilder.model.dto.report.update.goal;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RepetitionTimeSpendData implements GoalReportData<Duration> {

    private UUID userId;
    private Long dictionaryId;
    private String dictionaryName;
    private Duration repetitionDuration;
    private Duration repetitionDurationGoal;
    private LocalDate userCreatedAt;
    private LocalDate currentDate;

    @Override
    public Duration getValue() {
        return this.repetitionDuration;
    }

    @Override
    public Duration getGoal() {
        return this.repetitionDurationGoal;
    }
}
