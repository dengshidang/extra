drop table  if exists `user`;


create table `user`
(
    id   INTEGER  PRIMARY KEY ,
    `name` VARCHAR(32) DEFAULT 'DEFAULT',
    update_count  INTEGER default 0
);

insert into `user`(id, `name`)
values (1, '张无忌'),
       (2, '赵敏'),
       (3, '周芷若'),
       (4, '小昭'),
       (5, '殷离');

