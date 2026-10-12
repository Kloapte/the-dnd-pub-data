import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const here = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(here, '../..');
const cmp = (a,b) => a < b ? -1 : a > b ? 1 : 0;
const canonical = x => Array.isArray(x) ? x.map(canonical) : (x && typeof x === 'object')
  ? Object.fromEntries(Object.keys(x).sort().map(k=>[k,canonical(x[k])])) : x;
const manifest = JSON.parse(fs.readFileSync(path.join(root, 'analysis/coverage-manifest.json'), 'utf8'));
const collection = manifest.collections.find(c => c.domain === 'monsters' && c.collection === 'monster');
const files = [...collection.sourceFiles].sort();
const records = [];
for (const file of files) {
  const doc = JSON.parse(fs.readFileSync(path.join(root, 'src/main/resources/data', file), 'utf8'));
  doc.monster.forEach((monster, index) => records.push({ file, index, monster }));
}
const ident = r => ({name:r.monster.name ?? null, source:r.monster.source ?? null,
  edition:r.monster.edition ?? null, file:r.file, index:r.index});
const copyRecords = records.filter(r => r.monster._copy && typeof r.monster._copy === 'object');
const versionRecords = records.filter(r => Array.isArray(r.monster._versions) && r.monster._versions.length);
const both = records.filter(r => copyRecords.includes(r) && versionRecords.includes(r));
const missing = [], ambiguous = [], edges = [];
for (const r of copyRecords) {
  const ref = r.monster._copy;
  const candidates = records.filter(x => String(x.monster.name).toLowerCase() === String(ref.name).toLowerCase()
    && (!ref.source || String(x.monster.source).toLowerCase() === String(ref.source).toLowerCase()));
  const edge = {child:ident(r), requestedParent:{name:ref.name ?? null,source:ref.source ?? null},
    candidateCount:candidates.length,candidates:candidates.map(ident)};
  edges.push(edge); if (!candidates.length) missing.push(edge); if (candidates.length > 1) ambiguous.push(edge);
}
const modeCounts = new Map(), modeShapes = new Map(), examples = new Map(), fieldCounts = new Map(), modeFields = new Map();
for (const r of records) {
  const m = r.monster, mods = [];
  if (m._copy?._mod && typeof m._copy._mod === 'object') mods.push(['_copy._mod',m._copy._mod]);
  for (const [i,v] of (m._versions ?? []).entries()) {
    if (v?._mod && typeof v._mod === 'object') mods.push([`_versions[${i}]._mod`,v._mod]);
    if (v?._abstract?._mod && typeof v._abstract._mod === 'object') mods.push([`_versions[${i}]._abstract._mod`,v._abstract._mod]);
  }
  for (const [at,mod] of mods) for (const [field,value] of Object.entries(mod)) {
    fieldCounts.set(field,(fieldCounts.get(field)??0)+1);
    let ops;
    if (value && !Array.isArray(value) && typeof value === 'object' && 'mode' in value) ops=[[value.mode,value]];
    else if (Array.isArray(value)) ops=value.filter(x=>x && typeof x==='object' && 'mode' in x).map(x=>[x.mode,x]);
    else if (['trait','action','bonus','reaction','legendary','mythic','spellcasting'].includes(field) && value && typeof value === 'object')
      ops = Object.entries(value).map(([mode,instruction])=>[mode,instruction]);
    else if(value==='remove' && ['action','variant'].includes(field)) ops=[['removeProperty',value]];
    else ops=[[`field:${field}`,value]];
    for (const [mode,instruction] of ops) {
      modeCounts.set(mode,(modeCounts.get(mode)??0)+1);
      if(!modeFields.has(mode))modeFields.set(mode,new Map());modeFields.get(mode).set(field,(modeFields.get(mode).get(field)??0)+1);
      if (!modeShapes.has(mode)) modeShapes.set(mode,new Map());
      const serialized=JSON.stringify(canonical(instruction)), sm=modeShapes.get(mode);sm.set(serialized,(sm.get(serialized)??0)+1);
      if (!examples.has(mode)) examples.set(mode,[]);
      if (examples.get(mode).length < 8) examples.get(mode).push({monster:ident(r),path:at,field,instruction});
    }
  }
}
const versionDefs=versionRecords.flatMap(r=>r.monster._versions.map((version,index)=>({parent:ident(r),index,version})));
const versionKeys=new Map(), versionShapes=new Map(), namePresence=new Map(), sourcePresence=new Map();
for (const {version} of versionDefs) {
  for (const k of Object.keys(version)) versionKeys.set(k,(versionKeys.get(k)??0)+1);
  const key=Object.hasOwn(version,'_abstract')?'abstract':'direct';versionShapes.set(key,(versionShapes.get(key)??0)+1);
  const nk=Object.hasOwn(version,'name')?'name':'no-name';namePresence.set(nk,(namePresence.get(nk)??0)+1);
  const sk=Object.hasOwn(version,'source')?'source':'no-source';sourcePresence.set(sk,(sourcePresence.get(sk)??0)+1);
}
const versionModLocations=new Map();
for(const {version} of versionDefs){const k=version._abstract? (version._abstract._mod?'abstract-with-mod':'abstract-no-mod') : (version._mod?'direct-with-mod':'direct-no-mod');versionModLocations.set(k,(versionModLocations.get(k)??0)+1)}
const templateRefs=[];
for(const r of copyRecords)for(const t of r.monster._copy._templates??[])templateRefs.push({child:ident(r),template:t});
const versionExpansionCount=versionDefs.reduce((n,{version})=>n+(version._abstract ? (version._implementations?.length??0) : 1),0);
const nameGroups=new Map();
for(const r of records){const k=String(r.monster.name??'').toLowerCase();if(!nameGroups.has(k))nameGroups.set(k,[]);nameGroups.get(k).push(r)}
const collisions=[...nameGroups].filter(([,rs])=>rs.length>1).sort(([a],[b])=>cmp(a,b))
  .map(([name,rs])=>({name,count:rs.length,records:rs.map(ident)}));
const identityGroups=new Map();
for(const r of records){const k=`${String(r.monster.name??'').toLowerCase()}\u0000${String(r.monster.source??'').toLowerCase()}`;if(!identityGroups.has(k))identityGroups.set(k,[]);identityGroups.get(k).push(r)}
const exactIdentityCollisions=[...identityGroups].filter(([,rs])=>rs.length>1).sort(([a],[b])=>cmp(a,b))
  .map(([,rs])=>({name:rs[0].monster.name,source:rs[0].monster.source,count:rs.length,records:rs.map(ident)}));
const expandedVersionNames=[];
for(const {parent,index,version} of versionDefs){const parentRec=records.find(r=>r.file===parent.file&&r.index===parent.index);
  if(version._abstract){for(const [implementationIndex,implementation] of (version._implementations??[]).entries()){
    let displayName=version._abstract.name??parentRec.monster.name;
    for(const [key,value] of Object.entries(implementation._variables??{}))displayName=displayName.replaceAll(`{{${key}}}`,String(value));
    expandedVersionNames.push({parent,index,implementationIndex,displayName,source:version._abstract.source??parentRec.monster.source,variables:implementation._variables??{}});
  }} else expandedVersionNames.push({parent,index,implementationIndex:null,displayName:version.name??parentRec.monster.name,
    source:version.source??parentRec.monster.source,variables:null});
}
const versionDisplayNameCollisions=[];
for(const generated of expandedVersionNames){const matching=records.filter(r=>r.monster.name===generated.displayName&&r.monster.source===generated.source);if(matching.length)versionDisplayNameCollisions.push({...generated,matchingCanonicalRecords:matching.map(ident)})}
const byKey=new Map(records.map(r=>[r.file+'#'+r.index,r]));
const parentByChild=new Map();
for(const r of copyRecords){const e=edges.find(x=>x.child.file===r.file&&x.child.index===r.index);if(e?.candidateCount===1)parentByChild.set(r.file+'#'+r.index,e.candidates[0].file+'#'+e.candidates[0].index)}
const depths=new Map(),cycles=[];
function depth(k,trail=[]){if(depths.has(k))return depths.get(k);if(trail.includes(k)){cycles.push([...trail.slice(trail.indexOf(k)),k]);return null}const p=parentByChild.get(k);if(!p){depths.set(k,0);return 0}const d=depth(p,[...trail,k]);if(d!==null)depths.set(k,d+1);return d===null?null:d+1}
for(const r of records)depth(r.file+'#'+r.index);
const depthCounts=new Map();for(const d of depths.values())depthCounts.set(d,(depthCounts.get(d)??0)+1);
const multiLevel=[...depths].filter(([k,d])=>parentByChild.has(k)&&d>=2).map(([k,d])=>({record:ident(byKey.get(k)),depth:d}));
const copiedParents=[...parentByChild].filter(([,p])=>parentByChild.has(p));
const directOverrideCounts=new Map();
for(const r of copyRecords)for(const k of Object.keys(r.monster))if(!['_copy','name','source','edition'].includes(k))directOverrideCounts.set(k,(directOverrideCounts.get(k)??0)+1);
const copyKeyCounts=new Map(),preserveKeyCounts=new Map();let copyWithMod=0,copyWithTemplates=0,directOverrideRecordCount=0;
for(const r of copyRecords){for(const k of Object.keys(r.monster._copy))copyKeyCounts.set(k,(copyKeyCounts.get(k)??0)+1);if(r.monster._copy._mod)copyWithMod++;if(r.monster._copy._templates)copyWithTemplates++;if(Object.keys(r.monster).some(k=>!['_copy','name','source','edition'].includes(k)))directOverrideRecordCount++;for(const k of Object.keys(r.monster._copy._preserve??{}))preserveKeyCounts.set(k,(preserveKeyCounts.get(k)??0)+1)}
const sortedObject = map => Object.fromEntries([...map].sort(([a],[b])=>cmp(String(a),String(b))));
const sortedShapes = map => Object.fromEntries([...map].sort(([a],[b])=>cmp(a,b)).map(([k,v])=>[k,
  [...v].sort(([a],[b])=>cmp(a,b)).map(([json,count])=>({shape:JSON.parse(json),count}))]));
const write=(name,data)=>fs.writeFileSync(path.join(here,name),JSON.stringify(data,null,2)+'\n');
write('copy-mod-inventory.json',{totalMonsters:records.length,files:files.length,monstersWithCopy:copyRecords.length,
  monstersWithVersions:versionRecords.length,monstersWithBoth:both.length,versionDefinitions:versionDefs.length,
  modOccurrenceCounts:sortedObject(modeCounts),modFieldsByMode:Object.fromEntries([...modeFields].sort(([a],[b])=>cmp(a,b)).map(([k,v])=>[k,sortedObject(v)])),
  modFieldOccurrenceCounts:sortedObject(fieldCounts),modShapeCounts:sortedShapes(modeShapes),
  examples:Object.fromEntries([...examples].sort(([a],[b])=>a.localeCompare(b))),
  copyKeyCounts:sortedObject(copyKeyCounts),copyWithMod,copyWithTemplates,preserveKeyCounts:sortedObject(preserveKeyCounts),
  directOverrideRecordCount,directOverrideCounts:sortedObject(directOverrideCounts),copies:copyRecords.map(r=>({monster:ident(r),copy:r.monster._copy}))});
write('version-inventory.json',{monstersWithVersions:versionRecords.length,versionDefinitions:versionDefs.length,
  versionKeyCounts:sortedObject(versionKeys),versionShapeCounts:sortedObject(versionShapes),
  namePresence:sortedObject(namePresence),sourcePresence:sortedObject(sourcePresence),modLocations:sortedObject(versionModLocations),
  additionalExpansionCount:versionExpansionCount,expandedVersionNames,versionDisplayNameCollisions,versions:versionDefs});
write('inheritance-graph.json',{totalMonsters:records.length,rootMonsters:records.length-copyRecords.length,copiedMonsters:copyRecords.length,
  copyEdges:edges,missingParents:missing,ambiguousParents:ambiguous,
  sameNameCollisions:collisions,exactNameSourceCollisions:exactIdentityCollisions,depthCounts:sortedObject(depthCounts),
  maxCopyDepth:Math.max(0,...depths.values()),multiLevelCopyChainRecords:multiLevel,
  copiedParentCount:copiedParents.length,copiedParentEdges:copiedParents.map(([child,parent])=>({child:ident(byKey.get(child)),parent:ident(byKey.get(parent))})),cycles,
  templateReferenceCount:templateRefs.length,templateReferences:templateRefs,
  versions:versionRecords.map(r=>({parent:ident(r),versionCount:r.monster._versions.length,
  versions:r.monster._versions.map((version,index)=>({index,name:version.name??null,source:version.source??null,
  abstract:version._abstract??null,implementations:version._implementations??null}))}))});
console.log(JSON.stringify({files:files.length,entities:records.length,copy:copyRecords.length,versions:versionRecords.length,
  both:both.length,versionDefinitions:versionDefs.length,modeCounts:sortedObject(modeCounts),missing:missing.length,
  ambiguous:ambiguous.length,collisionBuckets:collisions.length,exactIdentityCollisionBuckets:exactIdentityCollisions.length,
  copiedParents:copiedParents.length,
  maxCopyDepth:Math.max(0,...depths.values()),multiLevelChains:multiLevel.length,cycles:cycles.length},null,2));
