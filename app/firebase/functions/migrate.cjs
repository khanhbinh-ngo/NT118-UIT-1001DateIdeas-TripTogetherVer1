// Dry-run by default. Requires Application Default Credentials; never put keys in the app.
const fs = require('node:fs');
const {initializeApp} = require('firebase-admin/app');
const {getDatabase} = require('firebase-admin/database');
initializeApp({projectId:'triptogetherv1', databaseURL:'https://triptogetherv1-default-rtdb.asia-southeast1.firebasedatabase.app'});
(async () => {
 const db = getDatabase();
 const snapshot = await db.ref().get();
 const data = snapshot.val() || {};
 const {updates,issues} = require('./migration-plan.cjs').buildMigration(data);
 console.log(JSON.stringify({paths:Object.keys(updates).length,issues},null,2));
 if (issues.length) throw new Error('Resolve migration conflicts before applying');
 if (!process.argv.includes('--apply')) {console.log('Dry run only. Use --apply after reviewing/exporting data.');return;}
 const backup=`backup-${Date.now()}.json`;
 fs.writeFileSync(backup,JSON.stringify(data,null,2),{mode:0o600});
 console.log(`Backup saved: ${backup}`);
 await db.ref().update(updates);
 console.log('Applied atomically. Existing trip plans/chat/files were preserved.');
})().then(()=>process.exit(0)).catch(e=>{console.error(e.message);process.exit(1)});
