package study.apps;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
class PostgresStore implements Store {
 final JdbcTemplate jdbc;final TransactionTemplate tx;
 PostgresStore(JdbcTemplate jdbc){this.jdbc=jdbc;tx=new TransactionTemplate(new DataSourceTransactionManager(jdbc.getDataSource()));}
 public void ready(){jdbc.queryForList("SELECT id FROM items LIMIT 1");}
 public Item create(String id,String label){try{jdbc.update("INSERT INTO items(id,label) VALUES(?,?)",id,label);}catch(DuplicateKeyException e){throw new Problem(409,"duplicate_item");}return new Item(id,label,null);}
 Item query(String id,boolean lock){var rows=jdbc.query("SELECT id,label,holder FROM items WHERE id=?"+(lock?" FOR UPDATE":""),(r,n)->new Item(r.getString(1),r.getString(2),r.getString(3)),id);return rows.isEmpty()?null:rows.get(0);}
 public Item get(String id){return query(id,false);}
 public Item change(String id,String holder,boolean returning){return tx.execute(status->{Item item=query(id,true);if(item==null)throw new Problem(404,"not_found");
  if(returning){if(!holder.equals(item.holder()))throw new Problem(409,"wrong_or_missing_holder");jdbc.update("UPDATE items SET holder=NULL WHERE id=?",id);return new Item(id,item.label(),null);}
  if(item.holder()!=null)throw new Problem(409,"already_checked_out");jdbc.update("UPDATE items SET holder=? WHERE id=?",holder,id);return new Item(id,item.label(),holder);
 });}
}
