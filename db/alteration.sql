SET @@session.time_zone = "+00:00";
SET @@global.time_zone = "+00:00";
commit;

alter table user_aud add column vendor_id BIGINT;
alter table user add constraint FKqgrourq3uxvrs7bbxbhbi458y foreign key (vendor_id) references vendor (id);
alter table user add column vendor_id BIGINT;
alter table vendor add column ticket_allocation_type integer
alter table vendor_aud add column ticket_allocation_type integer
