import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thedndpub.data.dte.monster.*;
import java.nio.file.*;
import java.util.*;

public class AnalyzeResolvedMonsterFields {
  static class Agg {
    long nodes, nulls;
    Set<String> entities = new HashSet<>();
    Map<String,Long> types = new TreeMap<>(), shapes = new TreeMap<>();
    List<Object> samples = new ArrayList<>();
  }
  static final ObjectMapper M = new ObjectMapper();
  static final Map<String,Agg> ALL = new TreeMap<>();
  public static void main(String[] args) throws Exception {
    Path dir=Path.of("src/main/resources/data/monsters");
    List<MonsterSourceRecord> records=new ArrayList<>();
    List<Path> sourceFiles;
    try(var files=Files.list(dir)) {
      sourceFiles=files.filter(x->x.getFileName().toString().startsWith("bestiary-")&&x.toString().endsWith(".json")).sorted().toList();
      for(Path f:sourceFiles) {
        JsonNode arr=M.readTree(f.toFile()).path("monster");
        for(int i=0;i<arr.size();i++) {
          ObjectNode raw=(ObjectNode)arr.get(i);
          var id=MonsterIdentity.from(raw.path("name").asText(),raw.path("source").asText());
          records.add(new MonsterSourceRecord(id,new MonsterSourceLocation(f.getFileName().toString(),i),M.treeToValue(raw,MonsterDte.class),raw));
        }
      }
    }
    var copy=new DefaultMonsterCopyResolver(M,MonsterTemplateRepository.fromResource(M));
    List<ResolvedMonster> resolved=new MonsterVersionExpander(M,copy).expandAll(copy.resolveAll(records));
    for(var monster:resolved) walk(monster.content(),"",monster);
    ObjectNode report=M.createObjectNode();report.put("dataset","resolved monster catalog");report.put("sourceFileCount",sourceFiles.size());report.put("originalCount",records.size());report.put("expandedCount",resolved.size());
    report.put("resolvedProperties",ALL.size());
    var props=report.putArray("properties");
    for(var e:ALL.entrySet()) {
      String path=e.getKey();Agg a=e.getValue();ObjectNode p=props.addObject();p.put("path",path);p.put("rootProperty",path.substring(0,path.indexOf('.')<0?path.length():path.indexOf('.')).replace("[]",""));p.put("monsterCount",a.entities.size());p.put("nodeCount",a.nodes);p.put("explicitNullCount",a.nulls);
      ObjectNode types=p.putObject("observedTypes");a.types.forEach(types::put);
      ObjectNode shapes=p.putObject("observedShapes");a.shapes.forEach(shapes::put);
      var samples=p.putArray("samples");for(Object sample:a.samples)samples.add((ObjectNode)sample);
    }
    M.writerWithDefaultPrettyPrinter().writeValue(Path.of("analysis/monsters/normalized-field-inventory.json").toFile(),report);
    System.out.println("monsters="+resolved.size()+" paths="+ALL.size());
  }
  static void walk(JsonNode n,String path,ResolvedMonster m) {
    if(!path.isEmpty()) {
      Agg a=ALL.computeIfAbsent(path,x->new Agg());a.nodes++;a.entities.add(m.identity().toString());
      a.types.merge(type(n),1L,Long::sum);a.shapes.merge(shape(n,0),1L,Long::sum);
      if(n.isNull())a.nulls++;
      if(a.samples.size()<3) {ObjectNode s=M.createObjectNode();s.put("monster",m.identity().toString());s.put("file",m.location().filename());s.put("index",m.location().arrayIndex());s.put("value",excerpt(n));a.samples.add(s);}
    }
    if(n.isObject()) {var it=n.fields();while(it.hasNext()){var e=it.next();walk(e.getValue(),path.isEmpty()?e.getKey():path+"."+e.getKey(),m);}}
    else if(n.isArray()) for(JsonNode x:n) walk(x,path+"[]",m);
  }
  static String type(JsonNode n) {if(n.isNull())return "null";if(n.isObject())return "object";if(n.isArray())return "array";if(n.isTextual())return "string";if(n.isBoolean())return "boolean";if(n.isNumber())return n.isIntegralNumber()?"integer":"number";return "other";}
  static String shape(JsonNode n,int d) {
    if(d>=2)return type(n);
    if(n.isObject()){List<String> keys=new ArrayList<>();n.fieldNames().forEachRemaining(keys::add);Collections.sort(keys);List<String> parts=new ArrayList<>();for(String k:keys)parts.add(k+":"+shape(n.get(k),d+1));return "object{"+String.join(",",parts)+"}";}
    if(n.isArray()){Set<String> members=new TreeSet<>();for(JsonNode x:n)members.add(shape(x,d+1));return "array["+String.join("|",members)+"]";}
    return type(n);
  }
  static String excerpt(JsonNode n){String s=n.toString();return s.length()>320?s.substring(0,317)+"...":s;}
}
