const {before, after, beforeEach, test} = require('node:test');
const fs = require('node:fs');
const {initializeTestEnvironment, assertSucceeds, assertFails} = require('@firebase/rules-unit-testing');
const {ref, set, get, update, query, orderByChild, equalTo, limitToFirst, serverTimestamp} = require('firebase/database');
let env;
const db = uid => env.authenticatedContext(uid, {email: `${uid}@example.com`}).database();
const trip = (id = 't1') => ({id, createdBy: 'owner', name: 'Trip', country: 'Vietnam', startDate: '14/10/2026', endDate: '15/10/2026', members: ['owner', 'member']});
before(async () => { env = await initializeTestEnvironment({projectId: 'demo-triptogether', database: {host: '127.0.0.1', port: 9000, rules: fs.readFileSync('database.rules.json', 'utf8')}}); });
after(async () => { await env.cleanup(); });
beforeEach(async () => {
 await env.clearDatabase();
 await env.withSecurityRulesDisabled(async c => set(ref(c.database()), {
  users: {owner: {email: 'owner@example.com', emailNormalized: 'owner@example.com', name: 'Owner'}},
  trips: {owner: {t1: trip()}},
  tripAccess: {t1: {creatorUid: 'owner', members: {owner: true, member: true}, updatedAt: 1, revision:0}},
  userTrips: {owner: {t1: 'owner'}, member: {t1: 'owner'}}
 }));
});
test('anonymous and global trip reads are denied; member can read exact trip', async () => {
 await assertFails(get(ref(env.unauthenticatedContext().database(), 'trips/owner/t1')));
 await assertFails(get(ref(db('member'), 'trips')));
 await assertFails(get(ref(db('outsider'), 'trips/owner/t1')));
 await assertSucceeds(get(ref(db('member'), 'trips/owner/t1')));
});
test('owner creates trip, access and all member indexes atomically', async () => {
 await assertSucceeds(update(ref(db('owner')), {
  'trips/owner/t2': trip('t2'),
  'tripAccess/t2': {creatorUid: 'owner', members: {owner: true, member: true}, updatedAt: serverTimestamp(),revision:0},
  'userTrips/owner/t2': 'owner', 'userTrips/member/t2': 'owner'
 }));
});
test('membership owner update is atomic; member cannot self-invite or remove owner', async () => {
 await assertFails(set(ref(db('member'), 'tripAccess/t1/members/outsider'), true));
 await assertFails(update(ref(db('owner')), {'tripAccess/t1/members/owner': null, 'tripAccess/t1/updatedAt': serverTimestamp(), 'tripAccess/t1/revision':1}));
 await assertSucceeds(update(ref(db('owner')), {'tripAccess/t1/members/member': null, 'tripAccess/t1/updatedAt': serverTimestamp(), 'tripAccess/t1/revision':1, 'userTrips/member/t1': null, 'favorites/member/t1': null, 'lastSeenChat/member/t1': null}));
 await assertFails(get(ref(db('member'), 'trips/owner/t1')));
});
test('indexes cannot grant access or be forged', async () => {
 await assertFails(set(ref(db('outsider'), 'userTrips/outsider/t1'), 'owner'));
 await assertFails(set(ref(db('owner'), 'userTrips/outsider/t1'), 'owner'));
});
test('profile own-write, bounded email lookup, forged normalized email denied', async () => {
 await assertFails(get(ref(db('owner'), 'users')));
 await assertSucceeds(get(query(ref(db('member'), 'users'), orderByChild('emailNormalized'), equalTo('owner@example.com'), limitToFirst(1))));
 await assertFails(set(ref(db('member'), 'users/owner'), {name:'Fake'}));
 await assertFails(set(ref(db('member'), 'users/member'), {name:'Fake', email:'member@example.com', emailNormalized:'owner@example.com'}));
});
test('chat accepts real sender and server time, rejects spoof and edits', async () => {
 const message = {sender:'member@example.com', senderUid:'member', message:'Hello', timestamp:serverTimestamp()};
 await assertSucceeds(set(ref(db('member'), 'chats/t1/m1'), message));
 await assertFails(set(ref(db('member'), 'chats/t1/m1'), message));
 await assertFails(set(ref(db('outsider'), 'chats/t1/m2'), {...message,senderUid:'outsider'}));
 await assertFails(set(ref(db('member'), 'chats/t1/m3'), {...message,senderUid:'owner'}));
});
test('members edit plans, outsiders cannot; invalid time denied', async () => {
 await assertSucceeds(set(ref(db('member'), 'trips/owner/t1/plan/day0/a1'), {time:'09:30',emoji:'',description:'Visit'}));
 await assertFails(set(ref(db('member'), 'trips/owner/t1/plan/day0/a2'), {time:'99:30',emoji:'',description:'Visit'}));
 await assertFails(set(ref(db('outsider'), 'trips/owner/t1/plan/day0/a3'), {time:'09:30',emoji:'',description:'Visit'}));
});
test('favorite reference is private and requires membership', async () => {
 await assertSucceeds(set(ref(db('member'), 'favorites/member/t1'), 'owner'));
 await assertFails(get(ref(db('outsider'), 'favorites/member')));
 await assertFails(set(ref(db('outsider'), 'favorites/outsider/t1'), 'owner'));
});
test('Drive metadata binds uploader, ID, kind and server time', async () => {
 const item={name:'a.pdf',url:'https://www.googleapis.com/drive/v3/files/file1?alt=media',provider:'drive',driveFileId:'file1',uploadedBy:'member@example.com',uploadedByUid:'member',contentType:'application/pdf',sizeBytes:100,timestamp:serverTimestamp()};
 await assertSucceeds(set(ref(db('member'),'tripDocuments/t1/f1'),item));
 await assertFails(set(ref(db('member'),'tripDocuments/t1/f2'),{...item,uploadedByUid:'owner'}));
 await assertFails(set(ref(db('member'),'tripDocuments/t1/f3'),{...item,url:'https://attacker.example/a.pdf'}));
 await assertFails(set(ref(db('member'),'tripImages/t1/f4'),item));
});
test('parallel invites preserve both members and revision increments', async () => {
 const change=uid=>update(ref(db('owner')), {[`tripAccess/t1/members/${uid}`]:true,'tripAccess/t1/updatedAt':serverTimestamp(),'tripAccess/t1/revision':{'.sv':{increment:1}},[`userTrips/${uid}/t1`]:'owner'});
 await Promise.all([assertSucceeds(change('guest1')),assertSucceeds(change('guest2'))]);
 await assertSucceeds(get(ref(db('guest1'),'trips/owner/t1')));
 await assertSucceeds(get(ref(db('guest2'),'trips/owner/t1')));
});
