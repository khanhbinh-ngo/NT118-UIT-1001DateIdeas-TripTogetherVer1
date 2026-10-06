const {before,after,test} = require('node:test');
const fs=require('node:fs');
const {initializeTestEnvironment,assertSucceeds,assertFails}=require('@firebase/rules-unit-testing');
const {doc,setDoc,updateDoc}=require('firebase/firestore');
const {ref,uploadBytes,getBytes,deleteObject}=require('firebase/storage');
let env;
const storage=uid=>env.authenticatedContext(uid).storage();
before(async()=>{
 env=await initializeTestEnvironment({projectId:'demo-triptogether',firestore:{host:'127.0.0.1',port:8080,rules:fs.readFileSync('firestore.rules','utf8')},storage:{host:'127.0.0.1',port:9199,rules:fs.readFileSync('storage.rules','utf8')}});
 await env.withSecurityRulesDisabled(async c=>setDoc(doc(c.firestore(),'tripAccess/t1'),{creatorUid:'owner',members:{owner:true,member:true},revision:0}));
});
after(async()=>env.cleanup());
test('ACL is server-only; clients cannot forge Storage membership',async()=>{
 await assertFails(setDoc(doc(env.authenticatedContext('outsider').firestore(),'tripAccess/t1'),{members:{outsider:true}}));
});
test('member uploads PDF, another member reads; outsiders and wrong MIME fail',async()=>{
 const path='tripFiles/owner/t1/documents/test.pdf';
 await assertSucceeds(uploadBytes(ref(storage('member'),path),Buffer.from('%PDF-1.4'),{contentType:'application/pdf',customMetadata:{uploadedByUid:'member'}}));
 await assertSucceeds(getBytes(ref(storage('owner'),path)));
 await assertFails(getBytes(ref(storage('outsider'),path)));
 await assertFails(uploadBytes(ref(storage('outsider'),'tripFiles/owner/t1/documents/no.pdf'),Buffer.from('x'),{contentType:'application/pdf',customMetadata:{uploadedByUid:'outsider'}}));
 await assertFails(uploadBytes(ref(storage('member'),'tripFiles/owner/t1/documents/no.exe'),Buffer.from('x'),{contentType:'application/octet-stream',customMetadata:{uploadedByUid:'member'}}));
 await assertFails(uploadBytes(ref(storage('member'),'tripFiles/owner/t1/documents/fake.pdf'),Buffer.from('x'),{contentType:'application/pdf',customMetadata:{uploadedByUid:'owner'}}));
 await assertSucceeds(deleteObject(ref(storage('member'),path)));
});
test('cover may be uploaded by owner before trip creation; another user cannot overwrite',async()=>{
 const path='tripDraftCovers/owner/new.jpg';
 await assertSucceeds(uploadBytes(ref(storage('owner'),path),Buffer.from('image'),{contentType:'image/jpeg'}));
 await assertFails(uploadBytes(ref(storage('member'),path),Buffer.from('image'),{contentType:'image/jpeg'}));
});
test('revoked member loses SDK access after ACL sync',async()=>{
 const path='tripFiles/owner/t1/images/revoke.jpg';
 await assertSucceeds(uploadBytes(ref(storage('owner'),path),Buffer.from('image'),{contentType:'image/jpeg',customMetadata:{uploadedByUid:'owner'}}));
 await env.withSecurityRulesDisabled(async c=>updateDoc(doc(c.firestore(),'tripAccess/t1'),{members:{owner:true},revision:1}));
 await assertFails(getBytes(ref(storage('member'),path)));
});
