-- auto-generated definition
create table user
(
    id         bigint auto_increment
        primary key,
    username   varchar(50)  null comment '用户名',
    gmt_create bigint       null comment '用户创建时间',
    avatar_url varchar(100) null comment '用户头像url地址',
    password   varchar(128) null comment '密码',
    email      varchar(120) null comment '邮箱',
    phone      varchar(64)  null comment '电话号码',
    constraint user_email_uindex
        unique (email),
    constraint user_name_uindex
        unique (username)
)
    comment '用户';



-- auto-generated definition
create table question
(
    id            bigint auto_increment
        primary key,
    title         varchar(50)   null comment '问题的标题',
    description   text          null comment '问题的描述',
    gmt_create    bigint        null comment '问题的创建时间',
    gmt_modified  bigint        null comment '问题的上一次修改时间',
    creator       bigint        not null comment '问题的创建人id主键',
    comment_count int default 0 null comment '评论数',
    tag           varchar(256)  null comment '问题标签'
)
    comment '问题';

-- auto-generated definition
create table persistent_logins
(
    username  varchar(64)                         not null,
    series    varchar(64)                         not null
        primary key,
    token     varchar(64)                         not null,
    last_used timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP
)
    comment '用来存放持久化令牌，因为如果不把令牌存在数据库中，那么该程序重启就会失效。字段名不可随意改变，这是框架中内定的';



-- auto-generated definition
create table notification
(
    id            bigint auto_increment
        primary key,
    notifier      bigint        not null comment '通知人id',
    receiver      bigint        not null comment '被通知人id',
    outerid       bigint        not null comment '通知内容的id,可能是评论的id也可能是问题的id',
    type          int           not null comment '通知的类型，问题是1，评论是2',
    gmt_create    bigint        not null comment '通知的创建时间',
    status        int default 0 not null comment '通知是否已读，默认是0未读，1是已读',
    notifier_name varchar(100)  null comment '通知人的usernmae',
    outer_title   varchar(256)  null comment '通知问题或评论的标题，'
)
    comment '通知';

-- auto-generated definition
create table comment
(
    id            bigint auto_increment
        primary key,
    parent_id     bigint        not null comment '问题的id 或者评论的id',
    type          int           not null comment '1代表是评论了问题， 2代表是评论了某个评论',
    commentator   bigint        not null comment '评论创建人id',
    gmt_create    bigint        not null comment '评论创建时间',
    gmt_modified  bigint        not null comment '评论修改时间',
    content       varchar(1024) null comment '评论内容',
    comment_count int default 0 null comment '评论数'
)
    comment '评论和二级评论';



