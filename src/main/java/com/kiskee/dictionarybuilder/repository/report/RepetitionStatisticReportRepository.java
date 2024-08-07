package com.kiskee.dictionarybuilder.repository.report;

import com.kiskee.dictionarybuilder.model.entity.report.progress.repetition.RepetitionStatisticReport;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepetitionStatisticReportRepository extends JpaRepository<RepetitionStatisticReport, Long> {

    @EntityGraph(attributePaths = {"reportRows.dictionaryReports"})
    Optional<RepetitionStatisticReport> findByUserId(UUID userId);
}
