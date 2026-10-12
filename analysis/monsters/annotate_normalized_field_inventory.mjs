import fs from 'node:fs';
const file='analysis/monsters/normalized-field-inventory.json';
const data=JSON.parse(fs.readFileSync(file,'utf8'));
const roots={
  name:['identity.name','String','presentation and canonical identity'], source:['identity.source','String','source identity'], page:['identity.sourcePage','Integer','source citation'], alias:['identity.aliases','List<String>','alternate search names'],
  otherSources:['identity.otherSources','List<SourceReferenceDto>','source citations'], referenceSources:['identity.referenceSources','List<SourceReferenceDto>','edition references'], reprintedAs:['identity.reprintedAs','List<String>','cross-source identity references'], group:['identity.groups','List<String>','catalog grouping'], legendaryGroup:['identity.legendaryGroup','LegendaryGroupReferenceDto','external rules reference'],
  size:['statistics.sizes','List<SizeCode>','retain all alternatives and unusual source codes'], type:['identity.creatureType','CreatureTypeDto','type string/object and subtype choices'], alignment:['identity.alignment','List<AlignmentDto>','alternative alignments and chances'], alignmentPrefix:['identity.alignmentPrefix','String','display qualification'], dragonAge:['identity.dragonAge','String','edition/source-specific identity detail'], dragonCastingColor:['identity.dragonCastingColor','String','edition/source-specific identity detail'], shortName:['identity.shortName','ShortNameDto','string or boolean behavior'], isNamedCreature:['identity.namedCreature','Boolean','identity/display'], isNpc:['identity.npc','Boolean','identity/display'], level:['statistics.level','Integer','retain source-defined level'], summonedByClass:['identity.summoning.class','String','summon reference'], summonedBySpell:['identity.summoning.spell','String','summon reference'], summonedBySpellLevel:['identity.summoning.spellLevel','Integer','summon scaling'],
  str:['statistics.abilities.strength','Integer','ability score'], dex:['statistics.abilities.dexterity','Integer','ability score'], con:['statistics.abilities.constitution','Integer','ability score'], int:['statistics.abilities.intelligence','Integer','ability score'], wis:['statistics.abilities.wisdom','Integer','ability score'], cha:['statistics.abilities.charisma','Integer','ability score'], initiative:['statistics.initiative','InitiativeDto','integer or proficiency/advantage object'], hp:['statistics.hitPoints','HitPointsDto','average, formula, or special'], cr:['statistics.challengeRating','ChallengeRatingDto','fraction/string or structured coven/lair/XP'], speed:['statistics.movement','MovementDto','conditional, alternate, hover and choice forms'], ac:['statistics.armorClasses','List<ArmorClassDto>','multiple conditional armor classes'], save:['statistics.savingThrows','Map<Ability,String>','source bonus strings retained'], skill:['statistics.skills','Map<Skill,SkillValueDto>','textual bonuses and alternative choices'], passive:['statistics.passivePerception','IntegerOrText','two nonnumeric cases retained as text'], senses:['statistics.senses','List<String>','preserve source clauses'], languages:['statistics.languages','Nullable<List<String>>','explicit null is distinct from absent/empty'], savingThrowForced:['statistics.forcedSaves','List<String>','preserve source-specific labels'], savingThrowForcedLegendary:['statistics.legendaryForcedSaves','List<String>','preserve source-specific labels'], savingThrowForcedSpell:['statistics.spellForcedSaves','List<String>','preserve source-specific labels'],
  resist:['defenses.resistances','List<DefenseDto>','strings plus recursive conditional groups'], immune:['defenses.immunities','List<DefenseDto>','strings plus recursive conditional groups'], vulnerable:['defenses.vulnerabilities','Nullable<List<DefenseDto>>','explicit null differs from absent/empty'], conditionImmune:['defenses.conditionImmunities','List<DefenseDto>','strings and nested conditions'],
  trait:['features.traits','List<MonsterFeatureDto>','recursive rich entries and metadata'], action:['features.actions','List<MonsterFeatureDto>','recursive rich entries and tags'], bonus:['features.bonusActions','List<MonsterFeatureDto>','recursive rich entries'], reaction:['features.reactions','List<MonsterFeatureDto>','recursive rich entries and headers'], legendary:['features.legendaryActions','List<MonsterFeatureDto>','preserve action costs in descriptive text and structured extraction'], mythic:['features.mythicActions','List<MonsterFeatureDto>','preserve mythic phase text'], variant:['features.variants','List<MonsterVariantDto>','may contain stat changes, spellcasting, tables and version references'], actionNote:['features.actionNote','String','section explanation'], reactionNote:['features.reactionNote','String','section explanation'], legendaryHeader:['features.legendaryHeader','List<String>','section explanation'], mythicHeader:['features.mythicHeader','List<String>','section explanation'], reactionHeader:['features.reactionHeader','List<String>','section explanation'], legendaryActions:['combat.legendaryActionLimit','Integer','definition limit, not current encounter count'], legendaryActionsLair:['combat.lairLegendaryActionLimit','Integer','definition limit, not current encounter count'], spellcasting:['spellcasting.blocks','List<MonsterSpellcastingDto>','dedicated typed spellcasting model'],
  actionTags:['classification.actionTags','Set<String>','source tags retained as source-derived facets'], damageTags:['classification.damageTags','Set<String>','source tags retained as source-derived facets'], damageTagsLegendary:['classification.legendaryDamageTags','Set<String>','source tags retained'], damageTagsSpell:['classification.spellDamageTags','Set<String>','source tags retained'], conditionInflict:['classification.conditionInflictTags','Set<String>','source tags retained'], conditionInflictLegendary:['classification.legendaryConditionTags','Set<String>','source tags retained'], conditionInflictSpell:['classification.spellConditionTags','Set<String>','source tags retained'], languageTags:['classification.languageTags','Set<String>','source tags retained'], miscTags:['classification.miscTags','Set<String>','source tags retained'], senseTags:['classification.senseTags','Set<String>','source tags retained'], spellcastingTags:['classification.spellcastingTags','Set<String>','source tags retained'], traitTags:['classification.traitTags','Set<String>','source tags retained'],
  environment:['catalog.habitats','Set<String>','search facets'], gear:['catalog.gear','List<GearReferenceDto>','string or structured item/quantity'], attachedItems:['catalog.attachedItems','List<String>','item references'], treasure:['catalog.treasure','List<String>','treasure facets'], familiar:['catalog.familiar','Boolean','summon capability'], basicRules:['catalog.rulesAvailability.basicRules2014','Boolean','edition availability'], basicRules2024:['catalog.rulesAvailability.basicRules2024','Boolean','edition availability'], srd:['catalog.rulesAvailability.srd2014','Boolean','licensing/rules availability'], srd52:['catalog.rulesAvailability.srd2024','Boolean','licensing/rules availability'], isReprinted:['catalog.reprinted','Boolean','catalog metadata'], hasFluff:['catalog.hasFluff','Boolean','external content availability'], hasFluffImages:['catalog.hasFluffImages','Boolean','external content availability'], hasToken:['catalog.hasToken','Boolean','asset availability'], tokenCredit:['catalog.tokenCredit','String','asset attribution'], tokenCustom:['catalog.customToken','Boolean','asset metadata'], foundryTokenScale:['catalog.foundryTokenScale','Double','VTT presentation metadata'], altArt:['catalog.alternateArt','List<AlternateArtDto>','asset source/page/name'], pbNote:['statistics.proficiencyBonusNote','String','retain explanatory source notation'], sizeNote:['identity.sizeNote','String','retain source qualification'], soundClip:['catalog.soundClip','SoundReferenceDto','external audio identity/reference'],
};
const existing=new Map(JSON.parse(fs.readFileSync('analysis/monsters/schema-gameplay.json','utf8')).properties.map(p=>[p.path,p.classification]));
const allRules=JSON.parse(fs.readFileSync('analysis/monsters/schema-gameplay.json','utf8')).classificationRules;
const roles={identity:'IDENTITY_OR_PRESENTATION',statistics:'GAMEPLAY_STATISTICS',defenses:'GAMEPLAY_MECHANICS',features:'RULE_DESCRIPTION',combat:'COMBAT_RULES',spellcasting:'SPELLCASTING_RULES',classification:'SEARCH_AND_RULE_TAGS',catalog:'CATALOG_OR_SOURCE_METADATA'};
const metadataRoots=new Set(['source','page','otherSources','referenceSources','reprintedAs','basicRules','basicRules2024','srd','srd52','hasFluff','hasFluffImages','hasToken','tokenCredit','tokenCustom','foundryTokenScale','altArt','soundClip','isReprinted']);
const presentationRoots=new Set(['name','alias','group','size','type','alignment','alignmentPrefix','shortName','isNamedCreature','isNpc','dragonAge','dragonCastingColor','senses','languages','passive','environment','legendaryGroup']);
const searchRoots=new Set(['actionTags','damageTags','damageTagsLegendary','damageTagsSpell','conditionInflict','conditionInflictLegendary','conditionInflictSpell','languageTags','miscTags','senseTags','spellcastingTags','traitTags']);
const statMechanics=new Set(['str','dex','con','int','wis','cha','initiative','hp','cr','speed','ac','save','skill','savingThrowForced','savingThrowForcedLegendary','savingThrowForcedSpell']);
const ruleTags=new Set(['damageTags','damageTagsLegendary','damageTagsSpell','conditionInflict','conditionInflictLegendary','conditionInflictSpell']);
const editions=new Set(['basicRules','basicRules2024','srd','srd52','dragonAge','dragonCastingColor']);
const classificationDestinations={
  actionTags:'actionTags', damageTags:'damageTags', damageTagsLegendary:'legendaryDamageTags',
  damageTagsSpell:'spellDamageTags', conditionInflict:'conditionInflict',
  conditionInflictLegendary:'legendaryConditionInflict', conditionInflictSpell:'spellConditionInflict',
  languageTags:'languageTags', miscTags:'miscTags', senseTags:'senseTags',
  spellcastingTags:'spellcastingTags', traitTags:'traitTags'
};
const excludedPresentationRoots=new Set([
  'hasFluff','hasFluffImages','hasToken','tokenCustom','tokenCredit',
  'foundryTokenScale','altArt','soundClip'
]);
const nonDedicated=new Set(['name','source','page','alignmentPrefix','dragonAge','dragonCastingColor','isNamedCreature','isNpc','level','summonedByClass','summonedBySpell','summonedBySpellLevel','str','dex','con','int','wis','cha','actionNote','reactionNote','legendaryActions','legendaryActionsLair','legendaryHeader','mythicHeader','reactionHeader','alignmentPrefix','pbNote','sizeNote','basicRules','basicRules2024','familiar','hasFluff','hasFluffImages','hasToken','tokenCredit','tokenCustom','foundryTokenScale','isReprinted','srd','srd52']);
const classify=(path)=>{
  const candidates=[`monster[].${path}`,`monster.${path}`,path];
  for(const candidate of candidates)if(existing.has(candidate))return existing.get(candidate);
  return null;
};
for(const p of data.properties){
  const root=p.rootProperty; const m=roots[root];
  p.normalizedDestination=m?m[0]+p.path.slice(root.length):null;
  p.rootPropertyJavaType=m?m[1]:null;
  p.mappingRationale=m?m[2]:null;
  p.scannerClassification=classify(p.path);
  p.normalizedGroup=m?m[0].split('.')[0]:null;
  p.semanticRole=!m?'UNRESOLVED':metadataRoots.has(root)?'SOURCE_OR_ASSET_METADATA':searchRoots.has(root)?'SOURCE_DERIVED_CLASSIFICATION_TAG':presentationRoots.has(root)?'IDENTITY_OR_PRESENTATION':roles[m[0].split('.')[0]];
  p.editionSpecific=editions.has(root);
  p.editionContext=editions.has(root)?'EXPLICIT_SOURCE_EDITION_OR_AVAILABILITY_FIELD':(['statistics','defenses','features','combat','spellcasting'].includes(m?.[0].split('.')[0])?'RULE_CONTENT_DEPENDS_ON_SOURCE_RULESET':'NO_DIRECT_EDITION_MARKER');
  p.dedicatedNestedDto=m?!nonDedicated.has(root):null;
  p.safeNormalization=m?'YES_WITH_SOURCE_SHAPE_PRESERVED':'NO_MAPPING';
  p.derivationPolicy=({name:'Generate stable ID from normalized source and name; retain source values.',passive:'Retain imported value; do not recompute from Wisdom.',actionTags:'Retain source-derived tags; do not regenerate from descriptions.',damageTags:'Retain source-derived tags; do not regenerate from descriptions.',conditionInflict:'Retain source-derived tags; do not regenerate from descriptions.',spellcastingTags:'Retain source-derived tags; do not regenerate from spellcasting.',traitTags:'Retain source-derived tags; do not regenerate from traits.',pbNote:'Keep note; only derive proficiency with an explicit edition ruleset.',source:'Stable identity component; do not infer edition from acronym alone.'})[root]??'No independent derivation; preserve source value.';
  p.coverageStatus=m?'MAPPED':'UNRESOLVED';
  if(classificationDestinations[root]){
    p.normalizedDestination=`NormalizedMonsterRecord.classification.${classificationDestinations[root]}`;
    p.mappingRationale='Retained as immutable internal source classification for backend filtering; excluded from public MonsterDto.';
    p.normalizedGroup='INTERNAL_SEARCH_METADATA';
    p.semanticRole='SOURCE_DERIVED_CLASSIFICATION_TAG';
    p.coverageStatus='INTERNAL_METADATA';
    p.safeNormalization='YES; retained internally and excluded from the public DTO';
  }else if(excludedPresentationRoots.has(root)){
    p.normalizedDestination='Explicitly excluded presentation-only source metadata';
    p.rootPropertyJavaType=null;
    p.mappingRationale='Retained in resolved source content but intentionally excluded from the public monster API.';
    p.normalizedGroup='EXCLUDED_PRESENTATION_METADATA';
    p.semanticRole='SOURCE_OR_ASSET_METADATA';
    p.dedicatedNestedDto=false;
    p.derivationPolicy='Retain the resolved source property without exposing it through the public DTO.';
    p.coverageStatus='EXCLUDED_PUBLIC_METADATA';
    p.safeNormalization='EXCLUDED_FROM_PUBLIC_DTO';
    p.gameplayRelevant=false;
  }
  p.gameplayRelevant=m?(statMechanics.has(root)||['resist','immune','vulnerable','conditionImmune','trait','action','bonus','reaction','legendary','mythic','variant','legendaryActions','legendaryActionsLair','spellcasting','summonedByClass','summonedBySpell','summonedBySpellLevel','familiar','gear','attachedItems'].includes(root)||ruleTags.has(root)):null;
  if(p.explicitNullCount>0)p.normalizationRisk='Preserve explicit null separately from absent and empty';
  else if(Object.keys(p.observedTypes).length>1)p.normalizationRisk='Polymorphic source shape; use a typed variant or lossless common representation';
  else p.normalizationRisk='No observed scalar shape conflict; preserve nested structure and source text';
}
const unmapped=data.properties.filter(p=>p.coverageStatus==='UNRESOLVED').map(p=>p.path);
data.inventoryMethod='Traversed resolved ResolvedMonster.content() after DefaultMonsterCopyResolver and MonsterVersionExpander; presence counts are distinct canonical identities; node/type counts include repeated array elements.';
data.rootFieldCount=[...new Set(data.properties.map(p=>p.rootProperty))].length;
data.rootFields=[...new Set(data.properties.map(p=>p.rootProperty))].sort().map(root=>{
  const fields=data.properties.filter(p=>p.rootProperty===root);
  return {name:root,monsterCount:fields.find(p=>p.path===root)?.monsterCount??null,observedTypes:fields.find(p=>p.path===root)?.observedTypes??{},destination:fields[0].normalizedDestination?.replace(/\..*$/,'')??null,normalizedGroup:fields[0].normalizedGroup,scannerClassification:fields[0].scannerClassification};
});
data.unmappedResolvedPaths=unmapped;
data.allObservedResolvedPathsAccountedFor=unmapped.length===0;
data.exactScannerClassificationMissingPaths=data.properties.filter(p=>p.scannerClassification===null).map(p=>p.path);
data.properties.sort((a,b)=>a.path.localeCompare(b.path));
fs.writeFileSync(file,JSON.stringify(data,null,2)+'\n');
console.log(JSON.stringify({monsters:data.expandedCount,paths:data.resolvedProperties,roots:data.rootFieldCount,unmapped:unmapped.length}));
