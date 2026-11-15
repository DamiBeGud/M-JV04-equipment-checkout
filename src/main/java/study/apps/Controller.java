package study.apps;
import java.util.*;
import org.springframework.web.bind.annotation.*;
record ItemInput(String id,String label){}
record HolderInput(String holder){}
record Item(String id,String label,String holder){}
interface Store {void ready();Item create(String id,String label);Item get(String id);Item change(String id,String holder,boolean returning);}
class Domain {
 final Store store;Domain(Store store){this.store=store;}
 Item create(ItemInput input){return store.create(Rules.id(input.id()),Rules.text(input.label(),80));}
 Item change(String id,HolderInput input,boolean returning){return store.change(Rules.id(id),Rules.id(input.holder()),returning);}
 Item get(String id){Item row=store.get(Rules.id(id));if(row==null)throw new Problem(404,"not_found");return row;}
}
@RestController
class Controller {
 final Domain domain;final String token;
 @org.springframework.beans.factory.annotation.Autowired Controller(Domain domain){this(domain,System.getenv("API_TOKEN"));}
 Controller(Domain domain,String token){this.domain=domain;this.token=token;}
 @GetMapping("/health/live")Map<String,String> live(){return Map.of("status","ok");}
 @GetMapping("/ready")Map<String,String> ready(){domain.store.ready();return Map.of("status","ready");}
 @PostMapping("/items")@ResponseStatus(org.springframework.http.HttpStatus.CREATED)Item create(@RequestHeader(value="Authorization",required=false)String auth,@RequestBody ItemInput input){Rules.authorize(auth,token);return domain.create(input);}
 @GetMapping("/items/{id}")Item get(@RequestHeader(value="Authorization",required=false)String auth,@PathVariable String id){Rules.authorize(auth,token);return domain.get(id);}
 @PostMapping("/items/{id}/checkout")Item checkout(@RequestHeader(value="Authorization",required=false)String auth,@PathVariable String id,@RequestBody HolderInput input){Rules.authorize(auth,token);return domain.change(id,input,false);}
 @PostMapping("/items/{id}/return")Item returning(@RequestHeader(value="Authorization",required=false)String auth,@PathVariable String id,@RequestBody HolderInput input){Rules.authorize(auth,token);return domain.change(id,input,true);}
}
