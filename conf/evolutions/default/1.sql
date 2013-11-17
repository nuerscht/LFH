# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table user (
  id                        integer auto_increment not null,
  type_id                   varchar(255),
  email                     varchar(255),
  salt                      varchar(255),
  password                  varchar(255),
  is_active                 tinyint(1) default 0,
  updated_at                datetime,
  created_at                datetime,
  constraint pk_user primary key (id))
;

create table user_type (
  id                        varchar(255) not null,
  description               varchar(255),
  constraint pk_user_type primary key (id))
;

alter table user add constraint fk_user_type_1 foreign key (type_id) references user_type (id) on delete restrict on update restrict;
create index ix_user_type_1 on user (type_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table user;

drop table user_type;

SET FOREIGN_KEY_CHECKS=1;

