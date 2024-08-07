package com.kiskee.dictionarybuilder.model.entity.report.goal.word;

import com.kiskee.dictionarybuilder.model.entity.report.DictionaryGoalReport;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DictionaryWordAdditionGoalReport implements DictionaryGoalReport<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long dictionaryId;

    @Column(nullable = false)
    private String dictionaryName;

    @Column(nullable = false)
    private Double goalCompletionPercentage;

    @Column(nullable = false)
    private int newWordsGoal;

    @Column(nullable = false)
    private int newWordsActual;

    public DictionaryWordAdditionGoalReport(Long dictionaryId) {
        this.dictionaryId = dictionaryId;
        this.goalCompletionPercentage = 0.0;
    }

    public DictionaryWordAdditionGoalReport(
            Long dictionaryId,
            String dictionaryName,
            Double goalCompletionPercentage,
            int newWordsGoal,
            int newWordsActual) {
        this.dictionaryId = dictionaryId;
        this.dictionaryName = dictionaryName;
        this.goalCompletionPercentage = goalCompletionPercentage;
        this.newWordsGoal = newWordsGoal;
        this.newWordsActual = newWordsActual;
    }

    @Override
    public Integer getGoalForPeriod() {
        return this.getNewWordsGoal();
    }

    @Override
    public DictionaryWordAdditionGoalReport buildFrom(
            String dictionaryName, Double goalCompletionPercentage, Integer goalForPeriod, Integer value) {
        return new DictionaryWordAdditionGoalReport(
                this.id,
                this.dictionaryId,
                dictionaryName,
                goalCompletionPercentage,
                goalForPeriod,
                this.newWordsActual + value);
    }

    @Override
    public DictionaryWordAdditionGoalReport buildFrom(Double goalCompletionPercentage, Integer goalForPeriod) {
        return new DictionaryWordAdditionGoalReport(
                this.id,
                this.dictionaryId,
                this.dictionaryName,
                goalCompletionPercentage,
                goalForPeriod,
                this.newWordsActual);
    }
}
