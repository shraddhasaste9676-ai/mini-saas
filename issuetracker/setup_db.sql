USE mydb;

DROP TRIGGER IF EXISTS after_issue_update;

DELIMITER //

CREATE TRIGGER after_issue_update
AFTER UPDATE ON issues
FOR EACH ROW
BEGIN
    IF OLD.status != NEW.status THEN
        INSERT INTO issue_history (issue_id, old_status, new_status, changed_at)
        VALUES (NEW.id, OLD.status, NEW.status, NOW());
    END IF;
END //

DROP PROCEDURE IF EXISTS RollbackIssueStatus //

CREATE PROCEDURE RollbackIssueStatus(IN p_issue_id BIGINT)
BEGIN
    DECLARE v_old_status VARCHAR(255);
    DECLARE v_history_id BIGINT;

    SELECT id, old_status INTO v_history_id, v_old_status
    FROM issue_history
    WHERE issue_id = p_issue_id
    ORDER BY changed_at DESC
    LIMIT 1;

    IF v_history_id IS NOT NULL THEN
        UPDATE issues SET status = v_old_status WHERE id = p_issue_id;
        DELETE FROM issue_history WHERE id = v_history_id;
    END IF;
END //

DELIMITER ;
