const {test}=require('node:test');
const assert=require('node:assert/strict');
const {buildMigration}=require('../functions/migration-plan.cjs');
test('legacy migration retains plans, converts indexes and snapshots, removes stale indexes',()=>{
 const data={trips:{a:{t:{id:'t',createdBy:'a',members:['a','b'],plan:{day0:{x:{description:'Keep'}}}}}},users:{a:{email:'A@Example.com'}},userTrips:{a:{t:true},b:{t:true},c:{t:true}},favorites:{b:{t:{createdBy:'a'}},c:{t:{createdBy:'a'}}}};
 const original=JSON.stringify(data);const {updates,issues}=buildMigration(data,100);
 assert.deepEqual(issues,[]);assert.equal(updates['userTrips/b/t'],'a');assert.equal(updates['userTrips/c/t'],null);assert.equal(updates['favorites/b/t'],'a');assert.equal(updates['favorites/c/t'],null);assert.equal(updates['users/a/emailNormalized'],'a@example.com');assert.equal(updates['tripAccess/t'].revision,0);
 assert.equal(JSON.stringify(data),original);assert.ok(!Object.keys(updates).some(p=>p.startsWith('trips/')));
});
test('canonical membership wins over stale legacy list when rerunning migration',()=>{
 const {updates}=buildMigration({trips:{a:{t:{id:'t',createdBy:'a',members:['a','b']}}},tripAccess:{t:{creatorUid:'a',members:{a:true},revision:4}},userTrips:{b:{t:'a'}}},200);
 assert.equal(updates['userTrips/b/t'],null);assert.equal(updates['tripAccess/t'].revision,5);
});
test('conflicting creator/trip IDs stop migration',()=>{
 const {issues}=buildMigration({trips:{a:{t:{id:'t',createdBy:'wrong'}}}});
 assert.equal(issues.length,1);
});
