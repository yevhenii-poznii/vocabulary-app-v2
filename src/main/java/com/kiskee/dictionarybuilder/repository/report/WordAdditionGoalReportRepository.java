package com.kiskee.dictionarybuilder.repository.report;

import com.kiskee.dictionarybuilder.model.entity.report.goal.word.WordAdditionGoalReport;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordAdditionGoalReportRepository extends JpaRepository<WordAdditionGoalReport, Long> {

    @EntityGraph(attributePaths = {"reportRows.dictionaryReports"})
    Optional<WordAdditionGoalReport> findByUserId(UUID userId);
}
