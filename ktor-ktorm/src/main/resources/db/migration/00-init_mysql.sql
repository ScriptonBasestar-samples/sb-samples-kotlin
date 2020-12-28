CREATE TABLE t_article
(
    id            binary(16) not null primary key default UUID_TO_BIN(UUID()),
    gallery_uid   binary(16) not null,
    user_uid      binary(16) not null,
    user_nickname varchar(100) not null,

    title         varchar(100),
    content       varchar(1000),
    format_type   varchar(10),

    created_at    timestamp default current_timestamp,
    updated_at    timestamp default current_timestamp on update current_timestamp,

    UNIQUE KEY idx_article_uniq (gallery_uid, user_uid)
)

CREATE TABLE t_comment
(
    id            binary(16) not null primary key default UUID_TO_BIN(UUID()),
    gallery_uid   binary(16) not null,
    user_uid      binary(16) not null,
    user_nickname varchar(100)  not null,

    article       binary(16),
    FOREIGN KEY t_article(id) REFERENCES t_article(id)
        ON UPDATE CASCADE,
    parent_uid    binary(16),
    FOREIGN KEY (parent_uid) REFERENCES t_comment (id),

    content       varchar(1000) not null,
    format_type   varchar(10)   not null,

    created_at    timestamp default current_timestamp,
    updated_at    timestamp default current_timestamp on update current_timestamp,

    UNIQUE KEY idx_comment_uniq (gallery_uid, user_uid, article)
)
