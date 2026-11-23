-- Subtasks: one level under tasks (no parent_subtask_id: depth 1 is structural).
-- task.id is UUID in the existing schema, so subtask ids/task_id are UUID too.
CREATE TABLE subtask (
    id UUID NOT NULL,
    task_id UUID NOT NULL,
    title VARCHAR(200) NOT NULL,
    done BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_subtask PRIMARY KEY (id),
    CONSTRAINT fk_subtask_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);

CREATE INDEX idx_subtask_task_id ON subtask (task_id);
