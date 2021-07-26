select * from auto_trade_order where id=24;
select * from trade where auto_trade_order_id=392;

select * from trade where auto_trade_order_id =(
SELECT max(ato.id) FROM trade t, auto_trade_order ato, client c 
where t.auto_trade_order_id=ato.id and c.id=ato.client_id and c.id=23);

update   auto_trade_order set status = 4 ;