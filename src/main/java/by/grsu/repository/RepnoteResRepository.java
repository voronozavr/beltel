package by.grsu.repository;

import by.grsu.entity.RepnoteRes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RepnoteResRepository extends JpaRepository<RepnoteRes, Long> {
    @Query("SELECT r FROM RepnoteRes r WHERE r.reportingNotice_id.id = :id")
    List<RepnoteRes> getByNoticeId(@Param("id") long id);

    @Query("DELETE FROM RepnoteRes r WHERE r.reportingNotice_id.id = :id")
    @Modifying
    @Transactional
    void deleteByNoticeId(@Param("id") long id);
}
