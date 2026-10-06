const {initializeApp} = require('firebase-admin/app');
const {getDatabase} = require('firebase-admin/database');
const {getFirestore} = require('firebase-admin/firestore');
const {onValueWritten} = require('firebase-functions/v2/database');
initializeApp();

// RTDB is the authority; Storage can consult only Firestore, never RTDB.
exports.syncTripAccess = onValueWritten({
  ref: '/tripAccess/{tripId}', region: 'asia-southeast1',
  instance: 'triptogetherv1-default-rtdb', retry: true,
}, async (event) => {
  const id = event.params.tripId;
  const latest = (await getDatabase().ref(`tripAccess/${id}`).get()).val();
  if (!latest) return; // Trip deletion is not exposed in this version.
  const document = getFirestore().doc(`tripAccess/${id}`);
  await getFirestore().runTransaction(async (transaction) => {
    const previous = await transaction.get(document);
    if (previous.exists && previous.data().revision >= latest.revision) return;
    transaction.set(document, latest);
  });
});
