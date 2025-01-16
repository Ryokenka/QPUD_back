CREATE TABLE IF NOT EXISTS questions (
                                         id              SERIAL PRIMARY KEY,
                                         question_text   TEXT NOT NULL,
                                         choice_a        TEXT NOT NULL,
                                         choice_b        TEXT NOT NULL,
                                         choice_c        TEXT NOT NULL,
                                         choice_d        TEXT NOT NULL,
                                         correct_answer  INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS game_history (
                                            id              SERIAL PRIMARY KEY,
                                            lobby_id        VARCHAR(50) NOT NULL,
                                            finished_at     TIMESTAMP   NOT NULL,
                                            final_scores_json TEXT      NOT NULL
);
